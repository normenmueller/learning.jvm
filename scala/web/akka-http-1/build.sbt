import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.12.4",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "Hello",
    libraryDependencies += scalaTest % Test,
    // For Akka 2.4.x or 2.5.x
    libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.0.10"
    // Only when running against Akka 2.5 explicitly depend on akka-streams in same version as akka-actor
    //libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.4", // or whatever the latest version is
    //libraryDependencies += "com.typesafe.akka" %% "akka-actor"  % "2.5.4" // or whatever the latest version is
  )
