package learning.spark
package pair 

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd._

object Pair extends App {

  val conf = new SparkConf().setMaster("local").setAppName("My App")

  using(new SparkContext(conf) { def close(): Unit = stop }, wait = true) { sc =>
    //sc.setLogLevel("DEBUG")
    import sc._

    val input: RDD[String] = slurp("build.sbt")
      .map(ls => makeRDD(ls))
      .getOrElse(makeRDD(Nil))

    val words: RDD[Option[(String,String)]] = input
      .map( _ match {
        case line if line.isEmpty => None
        case line                 => Some((line.split(" ")(0), line))
      })
      .cache

    println(words.toDebugString)

    import scalaz.std.list._
    import scalaz.std.string._
    import scalaz.std.tuple._
    import scalaz.syntax.monoid._
    import scalaz.syntax.show._

    (words.collect(): Seq[Option[(String,String)]])
      .foldLeft(Nil: List[(String,String)])((acc: List[(String,String)], tuple: Option[(String,String)]) => acc |+| (tuple.toList))
      //.foldLeft(Nil: List[(String,String)])(_ |+| _.toList)
      .println
  }
}
