package learning.fpis
package monoids

trait Monoid[A] {

  def op(a1: A, a2: A): A

  def zero: A

}

object MonoidInstances {

  implicit val stringMonoid = new Monoid[String] {
    def op(a1: String, a2: String): String = a1 + a2
    val zero: String = ""
  }

  implicit val plusIntMonoid = new Monoid[Int] {
    def op(a1: Int, a2: Int): Int = a1 + a2
    def zero: Int = 0
  }

  implicit val multIntMonoid = new Monoid[Int] {
    def op(a1: Int, a2: Int): Int = a1 * a2
    def zero: Int = 1
  }

  implicit def listMonoid[A] = new Monoid[List[A]] {
    def op(a1: List[A], a2: List[A]): List[A] = a1 ++ a2
    val zero: List[A] = Nil: List[A]
  }

  implicit val orMonoid = new Monoid[Boolean] {
    def op(a1: Boolean, a2: Boolean): Boolean = a1 || a2
    def zero: Boolean = false
  }

  implicit val andMonoid = new Monoid[Boolean] {
    def op(a1: Boolean, a2: Boolean): Boolean = a1 && a2
    def zero: Boolean = true
  }

  implicit def optionMonoid[A : Monoid] = new Monoid[Option[A]] {
    def op(a1: Option[A], a2: Option[A]): Option[A] = (a1, a2) match {
      case (Some(a1), Some(a2)) => Some(implicitly[Monoid[A]].op(a1,a2))
      case _                    => None
    }
    def zero: Option[A] = None
  }

  implicit def endoMonoid[A] = new Monoid[A => A] {
    def op(a1: A => A, a2: A => A): A => A = a1 compose a2
    def zero: A => A = identity[A]
  }

}

object MonoidFunctions {
  import scalaz.Traverse
  import scala.language.higherKinds

  def foldMap[A, B : Monoid, F[_] : Traverse](ma: F[A])(f: A => B): B = {
    val op2:  (B, => B) => B = (b1, b2) => implicitly[Monoid[B]].op(b1,b2)
    Traverse[F].foldRight(Traverse[F].map(ma)(f), implicitly[Monoid[B]].zero)(op2)
  }

  def foldMap[A,B](as: List[A], m: Monoid[B])(f: A => B): B =
    //(as map f).foldRight(m.zero)(m.op)
    // no `map` required!
    as.foldLeft(m.zero)((b, a) => m.op(b, f(a)))

}

object Main extends AnyRef with App {
  import MonoidInstances._
  import MonoidFunctions._

  val words = List("Hic", "Est", "Index")
  var s: String = _
  var t: String = _

  s = words.foldRight(stringMonoid.zero)(stringMonoid.op)
  t = words.foldLeft(stringMonoid.zero)(stringMonoid.op)
  println(s"s = $s")
  println(s"t = $t")

  s = words.foldRight("")(_ + _)
  t = words.foldLeft("")(_ + _)
  println(s"s = $s")
  println(s"t = $t")

  case class W(s: String)

  s = foldMap(List(W("Hic"), W("Est"), W("Index")), stringMonoid)(_.s)
  println(s"s = $s")

  import scalaz.std.list._
  s = foldMap(List(W("Hic"), W("Est"), W("Index")))(_.s)
  println(s"s = $s")

}
