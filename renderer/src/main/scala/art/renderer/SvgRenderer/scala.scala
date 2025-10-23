package art.renderer

import art.core.model._

object SvgRenderer {

  /** Converts a Scene into a full SVG XML string */
  def render(scene: Scene): String = {

    val header =
      s"""<svg xmlns="http://www.w3.org/2000/svg" width="${scene.width}" height="${scene.height}" viewBox="0 0 ${scene.width} ${scene.height}" style="background-color:black">"""

    val shapesSvg = scene.shapes.map {
      case Line(from, to, strokeWidth) =>
        s"""<line x1="${from.x}" y1="${from.y}" x2="${to.x}" y2="${to.y}" stroke="lime" stroke-width="$strokeWidth" stroke-linecap="round" />"""

      case Circle(center, radius) =>
        s"""<circle cx="${center.x}" cy="${center.y}" r="$radius" fill="none" stroke="cyan" stroke-width="1.2" />"""

      case Group(children) =>
        children.map(_ => "").mkString // placeholder (optional feature later)

      case _ => ""
    }.mkString("\n")

    val footer = "</svg>"
    s"$header\n$shapesSvg\n$footer"
  }
}