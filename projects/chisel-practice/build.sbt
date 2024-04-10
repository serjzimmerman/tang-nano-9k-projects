val chiselVersion = "6.2.0"

lazy val root = (project in file("."))
  .settings(
    name := "chisel-practice",
    version := "0.1.0",
    scalaVersion := "2.13.12",
    libraryDependencies ++= Seq(
      "org.chipsalliance" %% "chisel" % chiselVersion
    ),
    addCompilerPlugin(
      "org.chipsalliance" % "chisel-plugin" % chiselVersion cross CrossVersion.full
    )
  )
