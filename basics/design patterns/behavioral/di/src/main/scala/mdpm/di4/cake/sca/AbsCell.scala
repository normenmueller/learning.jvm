// Scalable Component Abstraction | Abstract Type Members
// User: Normen Müller (normen.mueller@gmail.com)
// Date: 8/7/12 | 11:12 AM
package mdpm.di4.cake.sca
package abs

/** The `AbsCell` class defines neither type nor value parameters.
 *
 * Instead it has an abstract type member `T` and an abstract value member `v`.
 *
 * N.B. Wrt. setters/getters refer to ''Programming in Scala, 18.2 Reassignable variables and properties''.
 */
abstract class AbsCell {
  type T

  def init: T

  private[this] var v = init

  def value: T = v

  // def value_=(x: T): Unit = v = x
  def value_=(x: T): this.type = {
    v = x
    this
  }

  def setMax(other: T)(implicit ord: Ordering[T]): this.type = {
    value = ord.max(value, other)
    this
  }
}

object Main extends App {

  /** The type of the value cell is `AbsCell { type T = Int }`.
    * Here the class type `AbsCell` is augmented by the ''refinement''
    * `{ type T = Int }`.
    */
  // Interesting: Check the type of `cell` identified by ScalaDoc!
  //              It's `val cell: AbsCell { type T = Int }`.
  val cell = new AbsCell {
    type T = Int

    // TODO post this example at scala-user; why do we need `lazy` here?
    lazy val init = 3
  }

  println("initial cell value                : %s" format cell.value)

  cell.value = cell.value * 2
  println("initial cell value * 2            : %s" format cell.value)

  // === Path-dependent types

  /** It's also possible to access objects of type `AbsCell` without knowing the concrete
    * binding of its type member. For instance, the following method resets a given cell
    * to its initial value, independently of its value type.
    *
    * Why does this work? Well, `cell.init` has type `cell.T` and `cell.value_=` has type `cell.T => ()`.
    * Since the formal parameter type and the concrete parameter type coincide, the method call is type-
    * correct.
    *
    * `cell.T` is an instance of an ''path-dependent type''.
    */

  // === Type selection and singleton type

  def reset(cell: AbsCell): Unit = cell.value = cell.init

  def reset2(cell: AbsCell): Unit = cell.value = (cell.init: cell.T)

  def reset3(cell: AbsCell): Unit = cell.value = (cell.init: cell.type#T)

  reset(cell)
  println("reset cell value to initial value : %s" format cell.value)
}
