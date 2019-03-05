package core

import akka.actor.{Actor, ActorRef, Cancellable, Props}
import domain.Elevator._
import domain.ElevatorControlSystem._
import domain.{Elevator, ElevatorControlSystem}
import org.slf4j.LoggerFactory

import scala.concurrent.duration._


/**
 * Basic actor implementation of an elevator.
 *
 * Handles commands to:
 *
 * Move the elevator.
 * To return the current state.
 * To pick up passengers in form of an [[ElevatorTrip]].
 *
 *
 * The current implementation simulates the steps (or the movement) every second automatically.
 * When the elevator is moving to its next goal, it is checked at each floor whether there are new pickups
 * or whether there are passengers who wants to get off.
 *
 * When the elevator is empty and when there is no new goal it moves back to the first floor.
 */
class ElevatorActor(val id: ElevatorId, enableScheduler: Boolean) extends Actor with Elevator {

  import ElevatorActor._

  val Logger = LoggerFactory.getLogger(s"${Elevator.getClass}-$id")

  var currentFloor: Floor = 0
  var goalFloor: Floor = 0
  var direction: Direction = NoDirection
  var pickUpQueue: Vector[ElevatorTrip] = Vector.empty[ElevatorTrip]
  var destinationQueue: Vector[Floor] = Vector.empty[Floor]

  import context.dispatcher

  val scheduler: Option[Cancellable] = if (enableScheduler) Some(context.system.scheduler.schedule(0.seconds, 1.second, self, Step)) else None

  override def postStop() = {
    super.postStop()
    for (cancellable <- scheduler) cancellable.cancel()
  }

  override def getNewGoal: Floor = {
    if (destinationQueue.nonEmpty) destinationQueue.head
    else if (pickUpQueue.nonEmpty) pickUpQueue.head.from
    // since someone left the elevator he needs to get in at sometime, so we stay here. Better
    // would be to have an overview of the current elevator traffic which could be used to go to a
    // more likely floor...
    else currentFloor
  }

  override def getDirection: Direction = {
    val diff = goalFloor - currentFloor
    diff match {
      case _ if diff > 0 => Up
      case _ if diff < 0 => Down
      case _ => NoDirection
    }
  }

  def removeOnTheFlyGoals(): Unit = {
    val onTheFlyPickUpDestinations = pickUpQueue.filter(p => p.from == currentFloor).map(_.from)
    if (onTheFlyPickUpDestinations.nonEmpty) {
      Logger.debug(s"Picking up ${onTheFlyPickUpDestinations.length} person(s) at floor $currentFloor on the way to $goalFloor")
      destinationQueue = destinationQueue ++ pickUpQueue.filter(p => p.from == currentFloor).map(_.to)
      pickUpQueue = pickUpQueue.filter(p => p.from != currentFloor)
    }
    val onTheFlyDestinations = destinationQueue.filter(p => p == currentFloor)
    if (onTheFlyDestinations.nonEmpty) {
      Logger.debug(s"${onTheFlyDestinations.length} person(s) are leaving at floor $currentFloor")
      destinationQueue = destinationQueue.filter(p => p != currentFloor)
    }
  }

  def sendStatus(dest: ActorRef): Unit = dest ! ElevatorStatus(id, currentFloor, goalFloor, direction, pickUpQueue, destinationQueue)

  override def move() = {

    removeOnTheFlyGoals()
    if (isGoalReached || direction == NoDirection) {
      if (isGoalReached && direction != NoDirection) Logger.info(s"Reached goal at floor $currentFloor.")
      val lastGoal = goalFloor
      goalFloor = getNewGoal
      direction = getDirection
      if (direction != NoDirection)
        Logger.info(s"Setting new goal $goalFloor from $lastGoal. Direction is $direction")
    }
    currentFloor = moveElevator(direction, currentFloor)
    sendStatus(context.parent)
  }

  override def receive: Receive = {
    case command: Command =>
      command match {
        case Status => sendStatus(sender())
        case PickUp(trip) if trip.from == trip.to =>
          Logger.info("Nice to see you and good bye ;)")
        case PickUp(trip) if trip.from != trip.to =>
          Logger.info(s"Picking up $trip")
          pickUpQueue = pickUpQueue :+ trip
        case Step => move()
        case msg => Logger.error(s"Cannot handle command $msg => Rejected.")
      }
    case msg => Logger.error(s"Cannot handle $msg. Only commands are allowed here.")
  }

  def isGoalReached: Boolean = currentFloor == goalFloor

  override def getCurrentFloor: Floor = currentFloor
}

object ElevatorActor {
  def moveElevator(direction: Direction, currentFloor: Floor): Floor = {
    direction match {
      case Up => currentFloor + 1 // no top limit here currently
      case Down if currentFloor > 0 => currentFloor - 1
      case _ => currentFloor
    }
  }

  def props(id: ElevatorId, enableScheduler: Boolean = true): Props = Props(classOf[ElevatorActor], id, enableScheduler)
}