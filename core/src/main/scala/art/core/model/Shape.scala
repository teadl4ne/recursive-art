package art.core.model

sealed trait Shape
final case class Point(x: Double, y: Double)
final case class Line(from: Point, to: Point, strokeWidth: Double = 1.0) extends Shape
final case class Circle(center: Point, radius: Double) extends Shape
final case class Group(children: Vector[Shape]) extends Shape
final case class Scene(width: Int, height: Int, shapes: Vector[Shape])
