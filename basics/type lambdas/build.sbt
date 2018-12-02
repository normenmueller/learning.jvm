import sbt._
import Keys._

lazy val standardSettings = Seq(
  organization := "mdpm"
, scalaVersion := "2.12.7"
, scalacOptions ++= Seq("-deprecation", "-encoding", "UTF-8", "-feature", "-unchecked")
, resolvers ++= Seq(
    "Sonatype OSS Releases"  at "https://oss.sonatype.org/content/repositories/releases/"
  , "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
  )
)

lazy val `typed-lambda` = (project in file(".")).settings(
  name := "Typed Lambda"
, standardSettings
, libraryDependencies ++= Seq(
    "org.scalaz" %% "scalaz-core" % "7.2.27" withSources() withJavadoc()
  )
)
