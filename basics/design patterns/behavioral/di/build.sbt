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

lazy val di = (project in file(".")).settings(
  name := "di"
, standardSettings
, libraryDependencies ++= Seq(
    "com.google.inject"           % "guice"           % "3.0"
  , "com.softwaremill.macwire"   %% "macros"          % "2.3.0"
  , "com.typesafe.scala-logging" %% "scala-logging"   % "3.7.2"
  , "ch.qos.logback"              % "logback-classic" % "1.2.3"
  )
)
