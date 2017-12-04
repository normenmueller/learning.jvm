import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "mdpm",
      scalaVersion := "2.12.2",
      version      := "0.1.0-SNAPSHOT",
      scalacOptions ++= Seq(
        "-deprecation",
        "-encoding", "UTF-8",
        "-feature",
        "-unchecked")
    )),
    name := "saxp",
    libraryDependencies ++= Seq(
      `scala-xml`,
      scalaz,

      `scala-logging`,
      logback,

      scalaTest % Test)
  )
