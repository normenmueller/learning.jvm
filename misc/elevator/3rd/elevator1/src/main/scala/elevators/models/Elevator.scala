package elevators.models

import elevators.control.{Stay, OpenDoors, Move, ElevatorActionPlanner}

class Elevator(id: Int, actionPlanner: ElevatorActionPlanner) {

  private var currentFloor: Floor = Floor(1)
  private var passengers: Set[Passenger] = Set.empty
  private var deliveryDirection: Option[MovingDirection] = None

  def state = if (passengers.isEmpty) {
    LookingForPickup(id, currentFloor)
  } else {
    DeliveringPassengers(id, currentFloor, goalFloors, passengers.head.direction)
  }

  def step() = actionPlanner.nextAction(id) match {
    case Move(Up) => currentFloor = currentFloor.upperFloor
    case Move(Down) => currentFloor = currentFloor.lowerFloor
    case OpenDoors(direction) => deliveryDirection = Some(direction)
    case Stay => deliveryDirection = None
  }

  private def goalFloors = passengers.map(_.goalFloor)

  def takePassengers(candidates: Set[Passenger]): Set[Passenger] = {
    val (taken, remaining) = candidates.partition(p =>
      p.currentFloor == currentFloor &&
        p.direction == deliveryDirection.get
    )
    passengers ++= taken
    remaining
  }

  def releasePassengers(): Set[Passenger] = {
    val (released, remaining) = passengers.partition(_.goalFloor == currentFloor)
    passengers = remaining
    released
  }

  def isDeliveringPassengers = deliveryDirection.isDefined

}


sealed trait ElevatorState {
  def currentFloor: Floor

  def id: Int
}

case class LookingForPickup(id: Int, currentFloor: Floor) extends ElevatorState

case class DeliveringPassengers(id: Int,
                                currentFloor: Floor,
                                goalFloors: Set[Floor],
                                direction: MovingDirection) extends ElevatorState {
  def isOnGoalFloor = goalFloors.contains(currentFloor)

  def isPickupCompleted(request: PickupRequest) = {
    request.pickupFloor == currentFloor && request.direction == direction
  }
}