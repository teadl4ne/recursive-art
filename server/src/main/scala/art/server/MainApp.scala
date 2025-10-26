package art.server

import art.core.generators.RecursiveArt
import art.renderer.SvgRenderer
import java.nio.file.{Files, Paths}
import java.nio.charset.StandardCharsets

object MainApp {

  def main(args: Array[String]): Unit = {
    println("🎨 Generating recursive art...")

    /**
     * This is where you may alter the values of parameters to influence
     * the result of Recursive Art Generator's output result!
     * Have fun!
    **/

    val seed = 123456L
    val scene = RecursiveArt.generateTree(
      depth = 8,
      seed = seed, // can be used System.currentTimeMillis() instead of 'seed' for variety
      length = 180,
      baseThickness = 9.0,
      thicknessFactor = 0.66,
      branchFactor = 4,
      baseAngle = math.Pi/6,
      jitter = 0.25,
      startColor = (33, 22, 13),
      endColor = (66, 142, 15)
    )

    val svg = SvgRenderer.render(scene)

    val path = Paths.get("art.svg")
    Files.write(path, svg.getBytes(StandardCharsets.UTF_8))

    println(s"✅ Art generated and saved as ${path.toAbsolutePath}")
  }
}