package mdpm

// OutputChannel[AnyRef] <: OutputChannel[String]
// Überall dort wo ein OutputChannel[String] verlangt wird, kann auch
// ein OutputChannel[AnyRef] eingesetzt werden. 
// Although it may seem non-intuitive, it actually makes sense. To see
// why, consider what you can do with an OutputChannel[String]. The
// only supported operation is writing a String to it. The same
// operation can also be done on an OutputChannel[AnyRef]. So it is
// safe to substitute an OutputChannel[AnyRef] for an
// OutputChannel[String]. By contrast, it would not be safe to
// substitute an OutputChannel[String] where an OutputChannel[AnyRef]
// is required. After all, you can send any object to an
// OutputChannel[AnyRef], whereas an OutputChannel[String] requires
// that the written values are all strings.

trait OutputChannel[-T] { def write(x: T) = sys.error("nyi") }
trait Queue[+T]         { def head: T = sys.error("nyi") }

object VarianceEx extends App {
  def f(oc: OutputChannel[String]) = oc write new java.lang.Integer(1)
  //f(new OutputChannel[String] {}) // <<<
  f(new OutputChannel[AnyRef] {})
  
  def h(q: Queue[Number]) = q head
  
}
