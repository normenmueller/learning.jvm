package elevators.control

import elevators.models._

import scala.collection.mutable

class FCFSElevatorScheduler extends ElevatorScheduler {

  protected val requestQueue = mutable.Queue.empty[PickupRequest]

  private val assignedRequests = mutable.Map.empty[Int, PickupRequest]

  override final def handlePickupRequest(request: PickupRequest): Unit = {
    requestQueue.enqueue(request)
  }

  override def nextIdleElevatorAction(elevator: LookingForPickup): ElevatorAction = {
    if (requestQueue.isEmpty) {
      Stay
    } else {
      val req = assignedRequests.getOrElseUpdate(elevator.id,{
        requestQueue.dequeue()
      })
      nextActionToPickup(elevator, req.pickupFloor, req.direction)
    }
  }

  override def isReadyToPickup(elevator: DeliveringPassengers): Boolean = {
    requestQueue.contains(PickupRequest(elevator.currentFloor, elevator.direction))
  }

  override def removeCompletedPickups(elevatorState: DeliveringPassengers): Unit = {
    requestQueue.dequeueAll(request => elevatorState.isPickupCompleted(request))
    assignedRequests.remove(elevatorState.id)
  }
}
