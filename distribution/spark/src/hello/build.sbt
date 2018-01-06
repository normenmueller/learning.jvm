name := "hello"

version := "0.1"

scalaVersion := "2.11.8"

//fork := true

val sparkVersion = "2.2.0"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-sql"  % sparkVersion,
  "org.apache.spark" %% "spark-core" % sparkVersion,

  "org.scalatest"    %% "scalatest"          % "3.0.1"       % Test,
  "com.holdenkarau"  %% "spark-testing-base" % "2.2.0_0.8.0" % Test
)

dependencyOverrides ++= Seq(
  "io.netty"         % "netty"       % "3.9.9.Final",
  "commons-net"      % "commons-net" % "2.2",
  "com.google.guava" % "guava"       % "11.0.2"
)

// for spark tests
//fork in Test := true
//parallelExecution in Test := false
//javaOptions ++= Seq("-Xms512M", "-Xmx4096M", "-XX:+CMSClassUnloadingEnabled")
