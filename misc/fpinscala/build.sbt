name := "fpinscala"

version := "1.0"

scalaVersion := "2.12.8"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked"
)

libraryDependencies ++= Seq(
  // type classses
    "org.scalaz" %% "scalaz-core" % "7.2.27" withSources() withJavadoc()
  // logging
  , "com.typesafe.scala-logging" %% "scala-logging"   % "3.9.2" withSources() withJavadoc()
  , "ch.qos.logback"              % "logback-classic" % "1.2.3" withSources() withJavadoc()
  // testing
  , "org.scalatest" %% "scalatest" % "3.0.5" % Test
)
