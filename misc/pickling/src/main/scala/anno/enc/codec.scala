package anno
package enc

import scala.pickling._
import scala.pickling.Defaults._

private[enc] class Encoder {

  def encode(msg: Creator): Unit =
    pickle(msg)

  def pickle[T <: Creator : Pickler](msg: T): Array[Byte] = {
    val pickler = implicitly[Pickler[T]]
    val tag     = pickler.tag
    val builder = binary.pickleFormat.createBuilder()
   
    builder.hintTag(tag)
    pickler.pickle(msg, builder)
    builder.result().value
  }

}
