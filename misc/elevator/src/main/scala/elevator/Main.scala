package elevator

import scala.concurrent.{ Future }
import scala.collection.immutable.Seq

trait ElevatorControlSystem {

  type Elevator <: AbstractElevator

  trait AbstractElevator {

    def id: Id

    def current: Floor

    def destinations: Set[Floor]

  }

  protected def elevators: Set[Elevator]

  def status: Set[(Id, Floor, Set[Floor])]

  def pickup(request: Request): Elevator

  def update(elevator: Elevator, destination: Floor): Unit

  def step: Unit

  override def toString =
    status.toString

}

class SimpleElevatorControlSystem(es: Set[Int]) extends ElevatorControlSystem {

  case class Elevator(id: Id, current: Floor, destinations: Set[Floor]) extends AbstractElevator

  protected var elevators: Set[Elevator] =
    es map (id => Elevator(id, Floor(0), Set.empty[Floor]))

  def status: Set[(Id, Floor, Set[Floor])] =
    elevators map { case Elevator(id, current, destinations) => (id, current, destinations) }

  // this primitive system does not track pickup requests
  def pickup(request: Request): Elevator =
    elevators minBy { case Elevator(_, current, _) => current.n - request.from.n }

  def update(elevator: Elevator, destination: Floor): Unit =
    elevators = elevators map {
      case Elevator(elevator.id, _, _) => Elevator(elevator.id, elevator.current, elevator.destinations + destination)
      case e => e
    }

  def step: Unit =
    elevators = elevators map {
      case e if e.destinations.isEmpty => e
      case e => Elevator(e.id, e.destinations.head, e.destinations - e.destinations.head)
    }

}

object Building extends App {
  import scala.language.postfixOps

  val system = new SimpleElevatorControlSystem(1 to 2 toSet)
  println(system)

  // passenger at floor 0 request to go up
  val e1 = system pickup Request(Floor(0), Up)
  // after elevator arrives passenger chooses his concrete destination
  system update (e1, Floor(3))
  println(system)
  // system step
  system.step
  println(system)

}