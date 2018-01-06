name := "learning.netty"

version := "1.0"

scalaVersion := "2.11.7"

scalacOptions += "-deprecation"

libraryDependencies ++= Seq(
  "io.netty"                    % "netty-all"       % "4.0.33.Final" withSources 
, "org.scalaz"                 %% "scalaz-core"     % "7.1.5" withSources
, "org.slf4j"                   % "slf4j-api"       % "1.7.13"
, "ch.qos.logback"              % "logback-classic" % "1.1.3"
, "com.typesafe.scala-logging" %% "scala-logging"   % "3.1.0"
)
