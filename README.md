Project Name: Recursive Art Generator

Description:
This project generates recursive fractal tree art using Scala. The program creates an SVG image of a tree that branches recursively with adjustable parameters such as depth, branch factor, jitter, and thickness reduction. Recent additions include dynamic branch angles, variable thickness, and smooth color gradients from the base of the tree to its tips.

Features:
* Generates fractal tree art with randomized variations
* Adjustable depth, branch factor, angles, and jitter
* Dynamic branch thickness scaling per recursion level
* Smooth color gradient between start and end colors
* Outputs artwork as an SVG file for easy viewing

File Overview:
Shape.scala
Defines the basic geometric shapes used in the drawing, including Point, Line, Circle, Group, and Scene.

RecursiveArt.scala
Contains the recursive tree generation logic. It handles color interpolation, branch angle variation, and branch thickness scaling. The generator uses a simple pseudo-random number generator for reproducibility.

SvgRenderer.scala
Responsible for converting the generated Scene into an SVG file. Each line in the scene is drawn with its color and thickness values.

MainApp.scala
The entry point of the program. This file allows users to adjust parameters such as tree depth, branch factor, angle, jitter, base thickness, and color settings before generating the final SVG file.

Usage Instructions:
1. Open the project in IntelliJ IDEA or any Scala-compatible environment.
2. Build the project using sbt compile.
3. Run the application using sbt run or execute the MainApp.scala file.
4. The generated SVG file named art.svg will be created in the project directory.
5. Open art.svg in a web browser or any vector graphics viewer to view the generated fractal tree.

Adjustable Parameters in MainApp.scala:
* depth: number of recursive levels
* seed: random seed for reproducibility
* length: base length of the initial branch
* baseThickness: initial branch thickness
* thicknessFactor: multiplier for branch thickness per recursion
* branchFactor: number of child branches per node
* baseAngle: base angular spread between branches
* jitter: maximum random angle deviation per branch
* startColor: RGB tuple defining the color of the trunk
* endColor: RGB tuple defining the color at the tips of the tree

Example Output:
The resulting SVG displays a fractal-like tree with smooth color transitions and varying branch thickness. Each execution may produce a different pattern depending on the random seed and jitter settings.
