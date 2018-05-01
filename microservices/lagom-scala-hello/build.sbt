organization in ThisBuild := "de.safeplace"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.12.4"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % Test

lazy val `lagom-scala-hello` = (project in file("."))
  .aggregate(`lagom-scala-hello-api`, `lagom-scala-hello-impl`, `lagom-scala-hello-stream-api`, `lagom-scala-hello-stream-impl`)

lazy val `lagom-scala-hello-api` = (project in file("lagom-scala-hello-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `lagom-scala-hello-impl` = (project in file("lagom-scala-hello-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`lagom-scala-hello-api`)

lazy val `lagom-scala-hello-stream-api` = (project in file("lagom-scala-hello-stream-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `lagom-scala-hello-stream-impl` = (project in file("lagom-scala-hello-stream-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .dependsOn(`lagom-scala-hello-stream-api`, `lagom-scala-hello-api`)
