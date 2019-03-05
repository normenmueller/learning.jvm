package elevators.control

import elevators.models._

class ElevatorActionPlanner(scheduler: ElevatorScheduler) {

  private var elevators: Map[Int, Elevator] = Map.empty

  def nextAction(elevatorId: Int): ElevatorAction = {
    val elevatorState = elevators.getOrElse(
      elevatorId,
      throw new IllegalArgumentException(s"Elevator $elevatorId does not exists")
    ).state
    elevatorState match {
      case e: LookingForPickup =>
        scheduler.nextIdleElevatorAction(e)
      case e: DeliveringPassengers =>
        if (e.isOnGoalFloor || scheduler.isReadyToPickup(e)) {
          scheduler.removeCompletedPickups(e)
          OpenDoors(e.direction)
        } else {
          Move(e.direction)
        }
    }
  }

  def updateElevator(id: Int, elevator: Elevator) = {
    elevators += id -> elevator
  }
  
  def state() = elevators.values.map(_.state).toSeq
  
  def step() = elevators.values.foreach(_.step())

}

trait ElevatorAction
case class Move(direction: MovingDirection) extends ElevatorAction
case class OpenDoors(passengerDirection: MovingDirection) extends ElevatorAction
case object Stay extends ElevatorAction