import java.nio.file.Paths
import scala.util.{Failure, Success, Try}

object Main extends AnyRef with App with FileManager with WC {
  import scalaz.std.anyVal._
  import scalaz.std.string._
  import scalaz.syntax.show._

  slurp(Paths.get("./input.txt")) match {
    case Success(value) => wc(value).println
    case Failure(ex) => ex.getMessage.println
  }
}

trait WC {
  import org.apache.spark.SparkConf
  import org.apache.spark.SparkContext

  def wc(cnt: Seq[String]): Int =
    new SparkContext(new SparkConf().setMaster("local").setAppName("WC"))
      .parallelize(cnt)
      .flatMap(line => line.split(" "))
      .map(word => (word, 1))
      .reduceByKey(_ + _)
      .values.sum().toInt
}

trait FileManager extends ResourceManager {
  import java.nio.file.Path
  import scala.io.Source

  def slurp(p: Path): Try[Seq[String]] = Try {
    withResource(Source.fromFile(p.toFile)) { src =>
      src.getLines().toSeq
    }
  }

}

trait ResourceManager {
  import java.io.Closeable
  import scala.language.reflectiveCalls

  def withResource[R <: Closeable, T](r: => R, wait: Boolean = false)(block: R => T): T =
    try {
      block (r)
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
      r.close()
    }

}
