package object elevator {
  import scala.math.Ordering

  type Id = Int

  case class Floor(n: Int)

  //private val o = Ordering.by[Position, Int](_.n)
  //import o._

  abstract class Direction
  case object Up extends Direction
  case object Down extends Direction

  case class Request(from: Floor, direction: Direction)
}