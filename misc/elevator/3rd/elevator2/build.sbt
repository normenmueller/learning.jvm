name := "elevator2"

version := "1.0"

scalaVersion := "2.11.7"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8", // yes, this is 2 args
  "-feature",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code", // N.B. doesn't work well with the ??? hole
  "-Ywarn-numeric-widen",
  "-Xfuture",
  "-target:jvm-1.8"
  //"-Ylog-classpath"
)

scalacOptions in Test ++= Seq("-Yrangepos")

lazy val AkkaVersion = "2.3.12"

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-actor_2.11" % AkkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  // Testing
  "com.typesafe.akka" %% "akka-testkit" % AkkaVersion,
  "org.specs2" %% "specs2-core" % "3.6.4"

)
