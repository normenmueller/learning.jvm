package learning.spark
package combine 

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd._

object Combine extends App {

  val conf = new SparkConf().setMaster("local").setAppName("My App")

  using(new SparkContext(conf) {
    def close(): Unit = stop
  }) { sc =>
    //sc.setLogLevel("DEBUG")
    import sc._

    val input: RDD[String] =
      slurp("build.sbt")
        .map(ls => sc.makeRDD(ls))
        .getOrElse(sc.makeRDD(Nil))

    val result: RDD[(String,Float)] = input
      .map(line => line.toLowerCase)
      .flatMap(line => line.split("""\W+"""))
      .map((_,1))
      .combineByKey(
        // new element (Value, Count)
        createCombiner = (_: Int, 1),
        // value has been seen before
        mergeValue     = (acc: (Int, Int), v) => (acc._1 + v, acc._2 + 1),
        // combine accumulator 
        mergeCombiners = (acc1: (Int, Int), acc2: (Int, Int)) => (acc1._1 + acc2._1, acc1._2 + acc2._2) )
      .map { case (key, value) => (key, value._1 / value._2.toFloat) }

    // Note: As here the value is always `1`
    // (acc._1 + v, acc._2 +1) == (acc._1 + 1, acc._2 +1)
    // and thus the average is always `1` :)
    result.collectAsMap().foreach(println(_))
  }
}
