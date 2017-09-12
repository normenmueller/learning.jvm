package object anno {

  // XXX Annotation required for a /sealed/ class?
  @scala.pickling.directSubclasses(
    Array(classOf[FromFun[_]])
  )
  private[anno] sealed abstract class Creator

  private[anno] case class FromFun[T](data: () => Wrapper[T]) extends Creator

}
