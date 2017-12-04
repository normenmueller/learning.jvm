import sbt._

object Dependencies {
  lazy val `scala-xml` = "org.scala-lang.modules" %% "scala-xml" % "1.0.6"
  lazy val scalaz = "org.scalaz" %% "scalaz-core" % "7.2.14"

  lazy val `scala-logging` = "com.typesafe.scala-logging" %% "scala-logging" % "3.7.1"
  lazy val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1"
}
