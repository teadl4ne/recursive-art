package art.server

import art.core.generators.RecursiveArt
import art.renderer.SvgRenderer
import java.nio.file.{Files, Paths}
import java.nio.charset.StandardCharsets

object MainApp {

  def main(args: Array[String]): Unit = {
    println("🎨 Generating recursive art...")

    val scene = RecursiveArt.generateTree(depth = 8) // you can change depth
    val svg = SvgRenderer.render(scene)

    val path = Paths.get("art.svg")
    Files.write(path, svg.getBytes(StandardCharsets.UTF_8))

    println(s"✅ Art generated and saved as ${path.toAbsolutePath}")
  }
}