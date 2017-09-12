package learning.pickling
package getstarted

import scala.pickling.Defaults._ // Introduce all picklers and ops
import scala.pickling.json._     // Introduce JSON pickle format

// https://github.com/scala/pickling#defaults-mode

object Main extends AnyRef with App {

  case class Person(first: String, second: String, age: Int)

  val pickle = Person("Paul", "Mueller", 3).pickle
  println(pickle)

  val person = pickle.unpickle[Person]
  println(person)
  
}
