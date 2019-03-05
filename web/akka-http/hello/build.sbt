name := "hello"

version := "1.0"

scalaVersion := "2.12.8"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked"
)

val akkaVersion = "2.5.19"
val akkaHttpVersion = "10.1.7"

libraryDependencies ++= Seq(
  // concurrent
  "com.typesafe.akka" %% "akka-actor" % akkaVersion withSources() withJavadoc()
  // web
  , "com.typesafe.akka" %% "akka-http"   % akkaHttpVersion withSources() withJavadoc()
  , "com.typesafe.akka" %% "akka-stream" % akkaVersion withSources() withJavadoc()
  // utils
  , "joda-time"   %  "joda-time"  % "2.10.1" withSources()
  , "org.scalaz" %% "scalaz-core" % "7.2.27" withSources() withJavadoc()
  // logging
  , "com.typesafe.scala-logging" %% "scala-logging"   % "3.9.2" withSources() withJavadoc()
  , "ch.qos.logback"              % "logback-classic" % "1.2.3" withSources() withJavadoc()
  // testing
  , "org.scalatest" %% "scalatest" % "3.0.5" % Test
)
