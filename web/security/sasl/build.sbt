import sbt._
import Keys._

lazy val standardSettings = Seq(
  organization := "mdpm"
, scalaVersion := "2.12.6"
, scalacOptions ++= Seq("-deprecation", "-encoding", "UTF-8", "-feature", "-unchecked")
, resolvers ++= Seq(
    "Sonatype OSS Releases"  at "https://oss.sonatype.org/content/repositories/releases/"
  , "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
  )
)

lazy val sasl = (project in file(".")).settings(
  name := "SASL"
, standardSettings
, libraryDependencies ++= Seq(
    "com.typesafe.scala-logging" %% "scala-logging"   % "3.7.2"      withSources() withJavadoc()
  , "ch.qos.logback"              % "logback-classic" % "1.2.3"      withSources() withJavadoc()
  )
)
