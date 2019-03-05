package core

import akka.actor.{Actor, ActorRef, Props}
import domain.Elevator._
import domain.ElevatorControlSystem
import domain.ElevatorControlSystem._
import org.slf4j.LoggerFactory

/**
 * Elevator Control Systems manages given number of elevators.
 *
 * New trips will be queued to the nearest elevator. This is done by finding out the elvator which
 * moved towards the given pickup floor but it currently not at the position of the given pick up floor.
 *
 * This is a compromise for not handling open/close door states of an elvator, the time that an elevator
 * needs to move from one floor to the other, etc.
 *
 * While the elevators are doing their steps, every triggered movement causes a notification of the
 * control system. Therefore it is possible for the elevator control system to determine the 'best'elevator
 * to be picked for the given ElevatorTrip which is obviously based on the function to get the
 * nearest elevator.
 */
class ElevatorControlSystemActor(numberOfElevators: Int) extends Actor with ElevatorControlSystem {

  require(numberOfElevators >= 1, "Can only mananage a number of elevators >= 1")

  val Logger = LoggerFactory.getLogger(ElevatorControlSystemStatus.getClass)

  var elevatorStatus: Map[ElevatorId, ElevatorStatus] = Map.empty[ElevatorId, ElevatorStatus]
  val elevators: Map[ElevatorId, ActorRef] =
    (1 to numberOfElevators map (elevatorId => elevatorId -> context.actorOf(ElevatorActor.props(elevatorId), s"Elevator-$elevatorId"))).toMap

  // get status from all elevators first to fill initial state of our elevatorStatus seq
  elevators.values.foreach(_ ! Status)

  override def receive: Receive = {
    case status: ElevatorStatus => elevatorStatus = elevatorStatus.updated(status.id, status)
    case Status => sender() ! ElevatorControlSystemStatus(status())
    case PickUp(trip) => pickup(trip)
    case Step => step()
  }

  override def status(): Seq[ElevatorStatus] = elevatorStatus.values.toSeq


  override def pickup(trip: ElevatorTrip): Unit = {
    elevators.get(getIdOfNearestElevator(trip)) match {
      case Some(elevator) =>
        elevator ! PickUp(trip)
      case None => Logger.error(s"Cannot find an elevator for the given trip $trip.")
    }
  }

  override def step(): Unit = elevators.values.foreach(_ ! Step)

  override def getIdOfNearestElevator(trip: ElevatorTrip): ElevatorId = {

    elevatorStatus.values.filter { status =>
      status.direction match {
        case Up => trip.from > status.currentFloor
        case Down => trip.from < status.currentFloor
        case NoDirection => true
      }
    }.toList match {
      // no elevator going in that direction, none of them is not moving...
      case Nil => elevatorStatus.values.minBy(status => Math.abs(trip.from - status.currentFloor)).id
      case potentialElevators => potentialElevators.minBy(s => Math.abs(trip.from - s.currentFloor)).id
    }
  }
}

object ElevatorControlSystemActor {
  def props(numberOfElevators: Int = 1): Props = Props(classOf[ElevatorControlSystemActor], numberOfElevators)
}