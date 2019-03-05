package elevators.control

import elevators.models.{Elevator, PickupRequest, ElevatorState}


trait ElevatorControlSystem {
  def status: Seq[ElevatorState]
  def update(id: Int, elevator: Elevator)
  def pickup(request: PickupRequest)
  def step()
}




