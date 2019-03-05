package elevators.models

import elevators.models.Floor.FloorOrdering._

case class Passenger(id: Int, currentFloor: Floor, goalFloor: Floor) {
  require(currentFloor != goalFloor)

  def direction = if (currentFloor < goalFloor) Up else Down

  def pickupRequest = PickupRequest(currentFloor, direction)
}
