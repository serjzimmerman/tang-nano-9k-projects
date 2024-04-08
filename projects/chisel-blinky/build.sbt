val scala2Version_ = "2.13.12"
val chiselVersion_ = "6.2.0"

assemblyMergeStrategy in assembly := {
 case PathList("META-INF", _*) => MergeStrategy.discard
 case _                        => MergeStrategy.first
}

lazy val root = (project in file("."))
  .settings(
    name := "chisel-blinky",
    version := "0.1.0",
    scalaVersion := scala2Version_,
    assembly / mainClass := Some("blinky.Main"),
    libraryDependencies ++= Seq(
      "org.chipsalliance" %% "chisel" % chiselVersion_
    ),
    addCompilerPlugin(
      "org.chipsalliance" % "chisel-plugin" % chiselVersion_ cross CrossVersion.full
    )
  )
