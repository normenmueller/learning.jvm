import akka.actor.{ActorRef, ActorSystem}
import akka.util.Timeout
import core.{ElevatorControlSystemActor, ElevatorActor}
import domain.Elevator.{ElevatorStatus, ElevatorTrip}
import domain.ElevatorControlSystem
import domain.ElevatorControlSystem.{PickUp, Status}
import akka.pattern.ask

import scala.util.{Failure, Success}
import scala.concurrent.duration._

/**
 * Elevator Entry Point
 */
object Main extends App {
  val actorSystem = ActorSystem("ElevatorControl")
  implicit val timeout = Timeout(5.seconds)

  import actorSystem.dispatcher

  sys.addShutdownHook(actorSystem.shutdown())

  //val elevatorControl = actorSystem.actorOf(ElevatorControlSystemActor.props)
  val elevatorControl: ActorRef = actorSystem.actorOf(ElevatorControlSystemActor.props(10), "ElevatorControlSystem")

  Thread.sleep(2000)

  elevatorControl ! PickUp(ElevatorTrip(1, 7))
  elevatorControl ! PickUp(ElevatorTrip(3, 5))
  elevatorControl ! PickUp(ElevatorTrip(3, 7))
  elevatorControl ! PickUp(ElevatorTrip(3, 7))
  elevatorControl ! PickUp(ElevatorTrip(9, 5))
  Thread.sleep(2000)
  elevatorControl ! PickUp(ElevatorTrip(3, 5))
  elevatorControl ! PickUp(ElevatorTrip(1, 10))
  Thread.sleep(2000)
  elevatorControl ! PickUp(ElevatorTrip(1, 10))
  elevatorControl ! PickUp(ElevatorTrip(3, 7))

  val statusFuture = elevatorControl ? Status
  for (status <- statusFuture) println(status)


}
