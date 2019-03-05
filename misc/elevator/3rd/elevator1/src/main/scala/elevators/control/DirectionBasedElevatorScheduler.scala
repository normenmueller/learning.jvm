package elevators.control

import elevators.models._

import scala.collection.mutable


class DirectionBasedElevatorScheduler extends ElevatorScheduler {

  protected val requestQueue = mutable.Queue.empty[PickupRequest]

  override def nextIdleElevatorAction(elevator: LookingForPickup): ElevatorAction = {
    if (requestQueue.isEmpty) {
      Stay
    } else {
      val req = requestQueue.head
      req.direction match {
        case Up =>
          nextActionToPickup(elevator, minUpRequest.pickupFloor, Up)

        case Down =>
          nextActionToPickup(elevator, maxDownRequest.pickupFloor, Down)
      }
    }
  }

  override def handlePickupRequest(request: PickupRequest): Unit = requestQueue.enqueue(request)


  override def isReadyToPickup(elevator: DeliveringPassengers): Boolean = {
    requestQueue.contains(PickupRequest(elevator.currentFloor, elevator.direction))
  }

  override def removeCompletedPickups(elevatorState: DeliveringPassengers): Unit = {
    requestQueue.dequeueAll(request => elevatorState.isPickupCompleted(request))
  }

  private def minUpRequest =
    requestQueue.filter(_.direction == Up)
      .sortBy(_.pickupFloor.number)
      .head

  private def maxDownRequest =
    requestQueue.filter(_.direction == Down)
      .sortBy(-_.pickupFloor.number)
      .head
}
