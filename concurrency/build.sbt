name := "learning.concurrency"

version := "1.0"

scalaVersion := "2.12.2"

scalacOptions += "-deprecation"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.2"
)
