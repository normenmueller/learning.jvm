package learning
package object spark {

  import scala.io.Source
  import scala.language.reflectiveCalls

  def using[A <: { def close(): Unit }, B](resource: A, wait: Boolean = false)(f: A => B): B = try {
    f(resource)
  } finally {
    if (wait) {
     println("""
       |========================================================================
       |
       |    Before we close the SparkContext, open the Spark Web Console
       |    http://localhost:4040 and browse the information about the tasks
       |    run for this example.
       |
       |    When finished, hit the <return> key to exit.
       |
       |========================================================================
       """.stripMargin)
     Console.in.read()
    }
    resource.close()
  }

  def slurp(filename: String): Option[List[String]] = try {
    Some(using(Source.fromFile(filename)) { source => 
      (for (line <- source.getLines) yield line).toList
    })
  } catch {
    case e: Exception => None
  }

  import scala.reflect.ClassTag
  import org.apache.spark.rdd.RDD

  implicit class RDDex[T: ClassTag](rdd: RDD[T]) {
    def flatMapValues[U: ClassTag](f: T => TraversableOnce[U]): RDD[U] =
      rdd flatMap f
  }
}
