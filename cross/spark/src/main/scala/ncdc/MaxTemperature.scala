package learning.spark
package ncdc

import java.io._
import java.nio.file.Paths
import java.util.zip.GZIPInputStream
import scala.io.Source

// cf. White - 2012 - Hadoop The Definitive Guide (3rd Edition), Chapter 2, Section Java MapReduce

trait Input {

  def ls(d: File): List[File] =
    if (d.exists && d.isDirectory)
      d.listFiles.filter(_.isFile).toList
    else Nil

  val source = new File(getClass().getResource("/ncdc/data").getFile())

  val input: List[String] = ls(source) flatMap { f =>
    Source.fromInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream(f))))
          .getLines
          .toList
  }

}

object NCDC extends App with Input {

  /** The map-phase
   *
   *  Cf. org.apache.hadoop.mapreduce.Mapper
   */
  val mapped: List[(String, Int)] = input flatMap { line =>
    val year = line.substring(15, 19)
    val temp = 
      if (line.charAt(87) == '+') // toInt doesn't like leading plus signs
        line.substring(88, 92).toInt
      else line.substring(87, 92).toInt
    if (temp != 9999 && line.substring(92, 93).matches("[01459]"))
      List((year, temp))
    else Nil
  }

  /** The shuffle-phase
   *
   *  Shuffling rsp. sorting is done by the MapReduce framework!
   *
   *  The output from the map function is processed by the MapReduce framework before being sent to
   *  the reduce function. This processing sorts and groups the key-value pairs by key.
   *
   *  Cf. `etc/MapReduce%20logical%20data%20flow.png`
   */
  val grouped = mapped groupBy (_._1) mapValues { _ map { case (_,v) => v } }

  /** The reduce-phase
   *
   *  Cf. org.apache.hadoop.mapreduce.Reducer
   *  Cf. `etc/MapReduce%20data%20flow%20with%20a%20single%20reduce%20task.png`
   */
  import scala.language.postfixOps
  val reduced1 = grouped mapValues { _ max }

  // Emphasize reduce
  val reduced2 = grouped mapValues { _ reduce { (t1, t2) => 
    scala.math.max(t1,t2)
  }}

  assert(reduced1 == reduced2)
  reduced1 foreach println

}
