name := "learning.spark"

version := "1.0"

scalaVersion := "2.11.8"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked"
)

unmanagedSourceDirectories in Compile <<= (scalaSource in Compile)(Seq(_))

unmanagedSourceDirectories in Test <<= (scalaSource in Test)(Seq(_))

unmanagedResourceDirectories in Compile += baseDirectory.value / "conf"

libraryDependencies ++= Seq(
  "org.apache.spark"  %% "spark-core"      % "1.6.2"  withSources(),
  "org.apache.spark"  %% "spark-streaming" % "1.6.2"  withSources(),
  "org.apache.spark"  %% "spark-sql"       % "1.6.2"  withSources(),
  "org.apache.spark"  %% "spark-repl"      % "1.6.2"  withSources(),
  "org.apache.spark"  %% "spark-mllib"     % "1.6.2"  withSources(),
  "org.apache.spark"  %% "spark-hive"      % "1.6.2"  withSources(),
  "org.scalaz"        %% "scalaz-core"     % "7.2.3"  withSources()
)

initialCommands += """
  import org.apache.spark.{SparkContext, SparkConf}
  import org.apache.spark.SparkContext._
  import org.apache.spark.sql.SQLContext

  val sparkConf = new SparkConf()
  sparkConf.setMaster("local[*]")
  sparkConf.setAppName("Spark Console")

  // Silence annoying warning from the Metrics system:
  sparkConf.set("spark.app.id", "Spark Console")

  val sc = new SparkContext(sparkConf)
  val sqlContext = new SQLContext(sc)

  import sqlContext.implicits._
  sqlContext.setConf("spark.sql.shuffle.partitions", "4")
"""

cleanupCommands += """
  println("Closing the SparkContext:")
  sc.stop()
"""
