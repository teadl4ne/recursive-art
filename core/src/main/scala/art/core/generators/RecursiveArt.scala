package art.core.generators

import art.core.model._
import scala.math._

object RecursiveArt {

  /**
   * Generates a recursive branching pattern (a fractal tree)
   *
   * @param depth the amount of recursive levels to draw
   * @param start starting point
   * @param angle direction in radians
   * @param length length of the first branch
   * @return a Scene containing all generated lines
   */

  def generateTree(
                    depth: Int,
                    start: Point = Point(400, 700),
                    angle: Double = -Pi / 2,
                    length: Double = 120
                  ): Scene = {

    // Recursive helper function — pure functional style
    def branch(point: Point, angle: Double, length: Double, depth: Int): Vector[Shape] = {
      if (depth == 0) Vector.empty
      else {
        val end = Point(
          point.x + cos(angle) * length,
          point.y + sin(angle) * length
        )

        val line = Line(point, end, strokeWidth = 1.5)

        // recursively generate left and right branches
        val left = branch(end, angle - Pi / 6, length * 0.7, depth - 1)
        val right = branch(end, angle + Pi / 6, length * 0.7, depth - 1)

        Vector(line) ++ left ++ right
      }
    }

    val shapes = branch(start, angle, length, depth)
    Scene(800, 800, shapes)
  }
}
