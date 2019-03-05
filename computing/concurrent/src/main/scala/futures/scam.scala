package learning
package concurrency
package futures
package scam

// Prokopec - 2014 - Learning Concurrent Programming in Scala, page 106

import scala.concurrent._
import ExecutionContext.Implicits.global

object Callbacks extends App {
  val netiquetteUrl = "http://www.ietf.org/rfc/rfc1855.txt"
  val urlSpecUrl = "http://www.w3.org/Addressing/URL/url-spec.txt"

  def getUrlSpec(): Future[List[String]] =
    Future {
      val f = io.Source.fromURL(urlSpecUrl)
      try f.getLines.toList finally f.close()
    }

  def find(lines: List[String], keyword: String): String =
    lines.zipWithIndex collect {
      case (line, n) if line.contains(keyword) => (n, line)
    } mkString("\n")

  val us: Future[List[String]] = getUrlSpec()

  us foreach {
    case lines => print(find(lines, "telnet"))
  }

  Thread.sleep(2000)
}

// Prokopec - 2014 - Learning Concurrent Programming in Scala, page 116

object FunctionalComposition extends App {

  val netiquetteUrl = "http://www.ietf.org/rfc/rfc1855.txt"
  val urlSpecUrl = "http://www.w3.org/Addressing/URL/url-spec.txt"

  val netiquette = Future { io.Source.fromURL(netiquetteUrl).mkString }
  val urlSpec = Future { io.Source.fromURL(urlSpecUrl).mkString }
  //val answer = netiquette.flatMap { nettext => urlSpec } // type correct but semantically incomplete
  val answer = netiquette.flatMap { nettext =>
    urlSpec.map { urltext => "Check this out: " + nettext + ". And check out: " + urltext }
  }

  answer foreach { case contents => println(contents) }

}
