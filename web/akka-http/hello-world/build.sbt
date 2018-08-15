import sbt._
import Keys._

lazy val standardSettings = Seq(
  organization := "mdpm"
, scalaVersion := "2.12.4"
, scalacOptions ++= Seq("-deprecation", "-encoding", "UTF-8", "-feature", "-unchecked")
, resolvers ++= Seq(
    "Sonatype OSS Releases"  at "https://oss.sonatype.org/content/repositories/releases/"
  , "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
  )
)

lazy val hello = (project in file(".")).settings(
  name := "Hello Akka-Http"
, standardSettings
, libraryDependencies ++= Seq(
  // web
    "com.typesafe.akka"          %% "akka-http"            % "10.1.0-RC1" withSources() withJavadoc()
  , "com.typesafe.akka"          %% "akka-http-spray-json" % "10.1.0-RC1" withSources() withJavadoc()
  // utils
  , "joda-time"                   %  "joda-time"           % "2.8.1"      withSources() withJavadoc()
  , "com.typesafe.akka"          %% "akka-actor"           % "2.5.8"      withSources() withJavadoc()
  , "com.typesafe.akka"          %% "akka-stream"          % "2.5.8"      withSources() withJavadoc()
  , "org.scalaz"                 %% "scalaz-core"          % "7.2.15"     withSources() withJavadoc()
  // logging
  , "com.typesafe.scala-logging" %% "scala-logging"        % "3.7.2"      withSources() withJavadoc()
  , "ch.qos.logback"              % "logback-classic"      % "1.2.3"      withSources() withJavadoc()
  // testing
  , "org.scalatest"              %% "scalatest"            % "3.0.3" % Test
  )
)
