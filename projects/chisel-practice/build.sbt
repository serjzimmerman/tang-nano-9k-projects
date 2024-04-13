val chiselVersion = "6.2.0"
val chiselTestVersion = "6.0.0"

lazy val root = (project in file("."))
  .settings(
    name := "chisel-practice",
    version := "0.1.0",
    scalaVersion := "2.13.12",
    scalacOptions ++= Seq(
      "-language:reflectiveCalls",
      "-deprecation",
      "-feature",
      "-Xcheckinit",
      "-Ymacro-annotations"
    ),
    libraryDependencies ++= Seq(
      "edu.berkeley.cs" %% "chiseltest" % chiselTestVersion,
      "org.chipsalliance" %% "chisel" % chiselVersion
    ),
    addCompilerPlugin(
      "org.chipsalliance" % "chisel-plugin" % chiselVersion cross CrossVersion.full
    )
  )
