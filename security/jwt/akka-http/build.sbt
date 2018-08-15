name := "jwt-akka-http"

version := "1.0"

scalaVersion := "2.12.6"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked"
)

unmanagedSourceDirectories.in(Compile) := Seq(scalaSource.in(Compile).value)

unmanagedSourceDirectories.in(Test) := Seq(scalaSource.in(Test).value)

libraryDependencies ++= Seq(
  // Web
  "com.typesafe.akka" %% "akka-http"   % "10.1.3",
  "com.typesafe.akka" %% "akka-actor"  % "2.5.14",
  "com.typesafe.akka" %% "akka-stream" % "2.5.14",
  // JWT
  "com.pauldijou" %% "jwt-core"  % "0.17.0",
  "com.pauldijou" %% "jwt-circe" % "0.17.0",
  // JSON
  "io.circe" %% "circe-core" % "0.9.3",
  "io.circe" %% "circe-generic" % "0.9.3",
  "io.circe" %% "circe-parser" % "0.9.3",
  // Logging
  "ch.qos.logback"              % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging"   % "3.9.0"

)
