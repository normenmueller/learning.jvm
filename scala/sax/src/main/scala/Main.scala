import scala.io.Source
import scala.xml.pull._

// https://tuxdna.wordpress.com/2014/02/03/a-simple-scala-parser-to-parse-44gb-wikipedia-xml-dump/
// http://www.mkyong.com/java/how-to-read-xml-file-in-java-sax-parser/
object Main extends App {

  val xml = new XMLEventReader(Source.fromFile("test.xml"))

  def printText(text: String, currNode: List[String]) {
    currNode match {
      case List("firstname", "staff", "company") => println("First Name: " + text)
      case List("lastname", "staff", "company") => println("Last Name: " + text)
      case List("nickname", "staff", "company") => println("Nick Name: " + text)
      case List("salary", "staff", "company") => println("Salary: " + text)
      case _ => ()
    }
  }

  def parse(xml: XMLEventReader) {
    def loop(currNode: List[String]) {
      if (xml.hasNext) {
        xml.next match {
          case EvElemStart(_, label, _, _) =>
            println("Start element: " + label)
            loop(label :: currNode)
          case EvElemEnd(_, label) =>
            println("End element: " + label)
            loop(currNode.tail)
          case EvText(text) =>
            printText(text, currNode)
            loop(currNode)
          case _ => loop(currNode)
        }
      }
    }
    loop(List.empty)
  }

  parse(xml)

}

trait XMLReader { }
