package learning.spark
package hello

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

object Hello extends App {

  /* 'setMaster': A cluster URL, namely 'local' in these examples, which tells Spark how to connect to a cluster.
   * 'local' is a special value that runs Spark on one thread on the local machine, without connecting to a cluster.
   *
   * 'setAppName': An application name, namely "My App" in these examples. This will identify your application on the
   * cluster manager's UI if you connect to a cluster.
   */
  val conf = new SparkConf().setMaster("local").setAppName("My App")

  using(new SparkContext(conf) { def close(): Unit = stop }, wait = true) { sc =>
    import sc._
    //sc.setLogLevel("DEBUG")

    val input: RDD[String] = slurp("build.sbt")
      .map(makeRDD(_)).getOrElse(makeRDD(Nil))
      .cache

    val words: RDD[(String,Int)] = input
      .map(line => line.toLowerCase)
      .flatMap(line => line.split("""\W+"""))
      //.groupBy(identity).mapValues(_.size).cache
      .map(word => (word, 1))
      .reduceByKey(_ + _)

    /* job #1 */ words take 10 foreach println
    /* job #2 */ words take 10 foreach println
  }
}
