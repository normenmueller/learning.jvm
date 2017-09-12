package learning.spark
package stages 

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd._

object Stages extends App {

  val conf = new SparkConf().setMaster("local").setAppName("My App")

  using(new SparkContext(conf) {
    def close(): Unit = stop
  }) { sc =>
    sc.setLogLevel("INFO")
    import sc._

    val input: RDD[String] =
      slurp("src/main/scala/stages/input.txt")
        .map(ls => sc.makeRDD(ls))
        .getOrElse(sc.makeRDD(Nil))

    // how many log messages appear at each level of severity.
    val extractor = """(INFO|WARN|ERROR)\s(.*)""".r

    val cleaned = input.flatMapValues { 
      case "" => None
      case line => 
        val extractor(severity, text) = line
        Some((severity, text))
    }.cache

    println(cleaned.countByKey())
    Thread.sleep(10000)

  }
}

