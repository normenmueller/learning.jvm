package elevators.control

import elevators.models.Floor.FloorOrdering._
import elevators.models._

import scala.collection.mutable


trait ElevatorScheduler {


  def handlePickupRequest(request: PickupRequest): Unit

  def nextIdleElevatorAction(elevator: LookingForPickup): ElevatorAction

  def isReadyToPickup(elevator: DeliveringPassengers): Boolean

  protected def nextActionToPickup(elevatorState: LookingForPickup, pickupFloor: Floor, direction: MovingDirection) = {
    if (elevatorState.currentFloor < pickupFloor) {
      Move(Up)
    } else if (elevatorState.currentFloor > pickupFloor) {
      Move(Down)
    } else {
      OpenDoors(direction)
    }
  }

  def removeCompletedPickups(elevatorState: DeliveringPassengers): Unit

}
