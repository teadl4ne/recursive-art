package art.server

import art.core.generators.RecursiveArt
import art.renderer.SvgRenderer
import java.nio.file.{Files, Paths}
import java.nio.charset.StandardCharsets

object MainApp {

  def main(args: Array[String]): Unit = {
    println("🎨 Generating recursive art...")

    val seed = 123456L
    val scene = RecursiveArt.generateTree(
      depth = 8,
      seed = seed,
      length = 130,
      baseThickness = 6.0,
      thicknessFactor = 0.66,
      branchFactor = 2,
      baseAngle = math.Pi/5,
      jitter = 0.25
    )

    val svg = SvgRenderer.render(scene)

    val path = Paths.get("art.svg")
    Files.write(path, svg.getBytes(StandardCharsets.UTF_8))

    println(s"✅ Art generated and saved as ${path.toAbsolutePath}")
  }
}