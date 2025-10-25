package art.core.generators
import art.core.model._
import scala.math._
import cats.data.State

object RecursiveArt {
  // ---------- Small pure RNG based on a simple xor-shift-like transform ----------
  // State is Long seed; we expose nextDouble (0..1)

  private type RNGState[A] = State[Long, A]

  private def nextLong: RNGState[Long] = State { seed =>
    // xor-shift update (deterministic, simple)
    var x = seed ^ (seed << 13)
    x = x ^ (x >>> 7) ^ (x << 17)
    val next = x
    (next, next)
  }

  private def nextDouble: RNGState[Double] =
    nextLong.map { n =>
      // convert to [0,1)
      val positive = if (n < 0) -n else n
      (positive.toDouble / Long.MaxValue.toDouble) % 1.0
    }

  private def sequenceState[A](xs: Vector[RNGState[A]]): RNGState[Vector[A]] =
    xs.foldLeft(State.pure[Long, Vector[A]](Vector.empty[A])) { (acc, s) =>
      for {
        v <- acc
        x <- s
      } yield v :+ x
    }

  // ---------- Generator with jitter and thickness ----------
  /**
   * Generating a fractal tree with:
   * - jitter: maximum random variation added to each branch angle (radians). Actual jitter per branch is uniform in [-jitter, +jitter].
   * - baseThickness: stroke width at the root branch
   * - thicknessFactor: multiply thickness by this each level (e.g. 0.7)
   *
   * Returns a State[Long, Scene] so you can pass a seed and get reproducible output.
   */

  def generateTreeState(
                         depth: Int,
                         start: Point = Point(400, 700),
                         angle: Double = -Pi / 2,
                         length: Double = 120,
                         baseThickness: Double = 6.0,
                         thicknessFactor: Double = 0.65,
                         branchFactor: Int = 2,
                         baseAngle: Double = Pi / 6,   // base split angle from parent
                         jitter: Double = 0.18         // max random jitter (radians)
                       ): RNGState[Scene] = {

    // recursive helper returning State that produces Vector[Shape]
    def branch(point: Point, angle: Double, length: Double, depth: Int, thickness: Double): RNGState[Vector[Shape]] = {
      if (depth == 0) State.pure(Vector.empty)
      else {
        // compute endpoint purely
        val end = Point(point.x + cos(angle) * length, point.y + sin(angle) * length)
        // create line with current thickness
        val line = Line(point, end, strokeWidth = thickness)

        // For each child, sample a jitter and generate its branch state
        val childStates: Vector[RNGState[Vector[Shape]]] = (0 until branchFactor).toVector.map { i =>
          // compute nominal child angle offset (spread around parent)
          val offset = (i - (branchFactor - 1) / 2.0) * baseAngle
          // sample a jitter in [-jitter, +jitter]
          for {
            r <- nextDouble
          } yield {
            val sampledJitter = (r * 2.0 - 1.0) * jitter // map [0,1) -> [-1,1) times jitter
            // final child angle
            val childAngle = angle + offset + sampledJitter
            // but we only computed sampledJitter here; we still need to call recursive branch generation
            // We'll return a function-like placeholder: but easier is to return a State that continues recursion.
            // We'll convert this into an actual recursive State below by mapping.
            // We will not reach here, because we actually need to call branch again inside for-comprehension.
            Vector.empty[Shape] // placeholder (not used)
          }
        }

        // Instead of building childStates like above, we build states directly by sampling jitter per child:
        val childStateBuilders: Vector[RNGState[Vector[Shape]]] = (0 until branchFactor).toVector.map { i =>
          for {
            r <- nextDouble
            // use r to compute jitter
            sampledJitter = (r * 2.0 - 1.0) * jitter
            offset = (i - (branchFactor - 1) / 2.0) * baseAngle
            childAngle = angle + offset + sampledJitter
            child <- branch(end, childAngle, length * 0.72, depth - 1, thickness * thicknessFactor)
          } yield child
        }

        // sequence children and prepend the current line
        for {
          childrenVec <- sequenceState(childStateBuilders)
        } yield Vector(line) ++ childrenVec.flatten
      }
    }

    // build scene from root branch
    for {
      shapes <- branch(start, angle, length, depth, baseThickness)
    } yield Scene(800, 800, shapes)
  }

  /** Convenience wrapper: run generator with a seed and return Scene */
  def generateTree(
                    depth: Int,
                    seed: Long,
                    start: Point = Point(400, 700),
                    angle: Double = -Pi / 2,
                    length: Double = 120,
                    baseThickness: Double = 6.0,
                    thicknessFactor: Double = 0.65,
                    branchFactor: Int = 2,
                    baseAngle: Double = Pi / 6,
                    jitter: Double = 0.18
                  ): Scene = {
    val state = generateTreeState(depth, start, angle, length, baseThickness, thicknessFactor, branchFactor, baseAngle, jitter)
    // run the State with the given seed; .run returns Eval[(S, A)] so .runA or .run used earlier pattern:
    val (seedOut, scene) = state.run(seed).value
    scene
  }
}