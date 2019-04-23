organization in ThisBuild := "de.mdpm.oauth2"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.11.8"

// SCALA SUPPORT: Remove the line below
EclipseKeys.projectFlavor in Global := EclipseProjectFlavor.Java

lazy val oauth2Api = project("oauth2-api")
  .settings(
    version := "1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomJavadslApi,
      "com.nulab-inc" %% "scala-oauth2-core" % "1.3.0"
    )
  )

lazy val oauth2Impl = project("oauth2-impl")
  .enablePlugins(LagomJava)
  .settings(
    version := "1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomJavadslPersistenceCassandra,
      lagomJavadslTestKit
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(oauth2Api)

def project(id: String) = Project(id, base = file(id))
  .settings(javacOptions in compile ++= Seq("-encoding", "UTF-8", "-source", "1.8", "-target", "1.8", "-Xlint:unchecked", "-Xlint:deprecation"))
  .settings(jacksonParameterNamesJavacSettings: _*) // applying it to every project even if not strictly needed.

// See https://github.com/FasterXML/jackson-module-parameter-names
lazy val jacksonParameterNamesJavacSettings = Seq(
  javacOptions in compile += "-parameters"
)
