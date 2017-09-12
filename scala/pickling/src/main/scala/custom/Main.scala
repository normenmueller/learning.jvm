package learning.pickling
package custom

import scala.pickling._
//import scala.pickling.Defaults._
import scala.pickling.Defaults.{ stringPickler => stringPU }

//https://github.com/scala/pickling/wiki/Writing-custom-Picklers-Unpicklers

class Foo(val x: String)

object Foo {

  implicit object pickler extends AnyRef with Pickler[Foo] with Unpickler[Foo] {

    /* Grabbing picklers for our nested members
     *
     * First thing we have to do is grab the picklers we will use when
     * pickling/unpickling the memebers of our object. Since the Foo class
     * consists of only a single string, this means grabing the
     * pickler/unpickler for string:
     */
    private val stringPickler   = implicitly[Pickler[String]]
    private val stringUnpickler = implicitly[Unpickler[String]]

    /* Creating a stringy type tag
     *
     * Pickling requires a type-string to handle subclassing at runtime.
     * Because of this, every pickler is required to provide a tag before
     * sending any data down. The Pickling framework has a convenience method
     * for autogenerating these type strings from a literal type.
     */
    override val tag = FastTypeTag[Foo]

    /* Writing the pickling logic.
     *
     * Now we can write the pickling logc. Pickling logic basically follows the
     * following form:
     *
     * 1. Hint your type tag to the reader
     * 2. Begin the pickling entry
     * 3. Serialize any fields (Note: This is different for collections)
     * 4. Inform the builder we are done with the current entry.
     */
    override def pickle(picklee: Foo, builder: PBuilder): Unit = {
      builder.hintTag(tag) // This is always required
      builder.beginEntry(picklee)
      builder.putField("x", { fieldBuilder =>
        stringPickler.pickle(picklee.x, fieldBuilder)
      })
      builder.endEntry()
    }

    /* Writing the unpickling logic
     *
     * The unpickling logic is a bit different than the pickling logic. This is
     * because the pickling framework requires the use of type tags to ensure
     * proper runtime decoding of class hierarchies. As such, the unpickling
     * logic that needs to be written does not include any beginEntry/endEntry
     * calls, as these will occur elsewhere in the framework. Instead, we focus
     * on Grabbing our fields and returning a value as the only thing we have
     * to do.
     *
     * You can see here that we immediately begin reading fields out of the
     * pickle reader, and use these fields to instantiate the value. Note that
     * during unpickling, we are always returning Any and therefor casting will
     * be needed on values.
     */
    override def unpickle(tag: String, reader: PReader): Any = {
      val x = stringUnpickler.unpickleEntry(reader.readField("x"))
      new Foo(x.asInstanceOf[String])
    }

  }
  
}

object Main extends AnyRef with App {
  import scala.pickling.Defaults.pickleOps
  import scala.pickling.json._

  (new Foo("hello")).pickle
  
}
