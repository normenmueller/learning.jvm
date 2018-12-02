import scala.language.higherKinds

trait Functor[F[_]] { self =>
  /** Lift `f` into `F` and apply to `F[A]`. */
  def map[A, B](fa: F[A])(f: A => B): F[B]
}

object Main extends App {

  //
  // w/o Scalaz
  //
  
  implicit def optionFunctor: Functor[Option] = new Functor[Option] {
    override def map[A,B](fa: Option[A])(f: A => B): Option[B] = fa match {
      case None => None
      case Some(x) => Some(f(x))
    }
  }

  // right-biased
  implicit def eitherFunctor[L]: Functor[({type λ[R] = Either[L,R]})#λ] =
    new Functor[({type λ[R] = Either[L,R]})#λ] {
      override def map[A,B](fa: Either[L,A])(f: A => B): Either[L,B] =
        fa match {
          case Left(x) => Left(x)
          case Right(x) => Right(f(x))
        }
    }

  println (implicitly[Functor[Option]].map(Some(4))(_ + 1))
  println (implicitly[Functor[({type λ[R] = Either[String,R]})#λ]].map(Right(4))(_ + 1))

  //
  // w/ Scalaz
  //

  import scalaz.Functor       // type class

  import scalaz.std.option._  // instances
  import scalaz.std.either._

  println (Functor[Option].map(Some(4))(_ + 1))
  println (Functor[({type λ[R] = Either[String,R]})#λ].map(Right(4))(_ + 1))
}
