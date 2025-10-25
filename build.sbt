ThisBuild / scalaVersion := "2.13.12"

lazy val commonSettings = Seq(
  version := "0.1.0",
  organization := "org.recursiveart",
  scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")
)

lazy val root = (project in file("."))
  .aggregate(core, renderer, server, ui)
  .settings(commonSettings)
  .settings(
    name := "recursive-art"
  )

lazy val core = (project in file("core"))
  .settings(commonSettings)
  .settings(
    name := "core",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.13.0"
    )
  )

lazy val renderer = (project in file("renderer"))
  .dependsOn(core)
  .settings(commonSettings)
  .settings(name := "renderer")

lazy val server = (project in file("server"))
  .dependsOn(core, renderer)
  .settings(commonSettings)
  .settings(name := "server")

lazy val ui = (project in file("ui"))
  .dependsOn(core, renderer)
  .settings(commonSettings)
  .settings(name := "ui")
