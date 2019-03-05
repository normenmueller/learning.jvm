name := "wc"

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
  // distribution
  "org.apache.spark" %% "spark-core" % "2.4.0"
  // concurrency
  , "com.typesafe.akka" %% "akka-actor" % akkaVersion withSources() withJavadoc()
  // web
  , "com.typesafe.akka" %% "akka-http"   % akkaHttpVersion withSources() withJavadoc()
  , "com.typesafe.akka" %% "akka-stream" % akkaVersion withSources() withJavadoc()
  // date & time
  , "joda-time"   %  "joda-time"  % "2.10.1" withSources()
  // type classes
  , "org.scalaz" %% "scalaz-core" % "7.2.27" withSources() withJavadoc()
  // logging
  , "com.typesafe.scala-logging" %% "scala-logging"   % "3.9.2" withSources() withJavadoc()
  , "ch.qos.logback"              % "logback-classic" % "1.2.3" withSources() withJavadoc()
  // testing
  , "org.scalatest" %% "scalatest" % "3.0.5" % Test
)
libraryDependencies ~= { _.map(_.exclude("org.slf4j", "slf4j-log4j12")) }

