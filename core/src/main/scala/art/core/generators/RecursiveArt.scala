package art.core.generators

import art.core.model._
import scala.math._
import cats.data.State

object RecursiveArt {
  // ---------- Color interpolation ----------
  private def interpolateColor(start: (Int, Int, Int), end: (Int, Int, Int), t: Double): String = {
    val (r1, g1, b1) = start
    val (r2, g2, b2) = end
    val r = (r1 + ((r2 - r1) * t)).toInt.max(0).min(255)
    val g = (g1 + ((g2 - g1) * t)).toInt.max(0).min(255)
    val b = (b1 + ((b2 - b1) * t)).toInt.max(0).min(255)
    f"#$r%02x$g%02x$b%02x"
  }

  // ---------- Small pure RNG based on a simple xor-shift-ish transform ----------
  // State is Long seed; we expose nextDouble (0..1)
  private type RNGState[A] = State[Long, A]

  private def nextLong: RNGState[Long] = State { seed =>
    var x = seed ^ (seed << 13)
    x = x ^ (x >>> 7) ^ (x << 17)
    val next = x
    (next, next)
  }

  private def nextDouble: RNGState[Double] =
    nextLong.map { n =>
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

  // ---------- Generator with jitter, thickness and color ----------
  /**
   * Returns a State[Long, Scene]. Call generateTree(...) convenience wrapper to run with seed.
   *
   * Note: the last parameter maxDepth should be the original depth value so we can compute
   * per-branch interpolation from depth -> color.
   */
  private def generateTreeState(
                                 depth: Int,
                                 start: Point = Point(400, 700),
                                 angle: Double = -Pi / 2,
                                 length: Double = 120,
                                 baseThickness: Double = 6.0,
                                 thicknessFactor: Double = 0.65,
                                 branchFactor: Int = 2,
                                 baseAngle: Double = Pi / 6,   // base split angle from parent
                                 jitter: Double = 0.18,       // max random jitter (radians)
                                 startColor: (Int, Int, Int),
                                 endColor: (Int, Int, Int),
                                 maxDepth: Int
                               ): RNGState[Scene] = {

    // recursive helper returning State that produces Vector[Shape]
    def branch(point: Point, angle: Double, length: Double, depth: Int, thickness: Double): RNGState[Vector[Shape]] = {
      if (depth == 0) State.pure(Vector.empty)
      else {
        // compute endpoint purely
        val end = Point(point.x + cos(angle) * length, point.y + sin(angle) * length)

        // compute interpolation t based on how deep we are relative to maxDepth:
        // root (depth == maxDepth) -> t = 0.0 (startColor)
        // leaves (depth -> 0) -> t -> 1.0 (endColor)
        val currentLevel = (maxDepth - depth).toDouble
        val t = if (maxDepth <= 0) 0.0 else (currentLevel / maxDepth.toDouble).max(0.0).min(1.0)
        val branchColor = interpolateColor(startColor, endColor, t)

        // create line with current thickness and color
        val line = Line(point, end, strokeWidth = thickness, strokeColor = branchColor)

        // build states directly by sampling jitter per child:
        val childStateBuilders: Vector[RNGState[Vector[Shape]]] = (0 until branchFactor).toVector.map { i =>
          for {
            r <- nextDouble
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

    // build scene from root branch; startColor & endColor are captured via closure in interpolate
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
                    jitter: Double = 0.18,
                    startColor: (Int, Int, Int) = (91, 58, 41),
                    endColor: (Int, Int, Int) = (60, 179, 113)
                  ): Scene = {
    val state = generateTreeState(depth, start, angle, length, baseThickness, thicknessFactor, branchFactor, baseAngle, jitter, startColor, endColor, depth)
    val (_, scene) = state.run(seed).value
    scene
  }
}
