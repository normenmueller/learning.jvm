package elevators.control

import elevators.models.{Elevator, PickupRequest, ElevatorState}


class FCFSElevatorControlSystem extends ElevatorControlSystem {

  private val scheduler = new FCFSElevatorScheduler

  val actionPlanner = new ElevatorActionPlanner(scheduler)

  override def status: Seq[ElevatorState] = actionPlanner.state()

  override def update(id: Int, elevator: Elevator): Unit = {
    actionPlanner.updateElevator(id, elevator)
  }

  override def pickup(request: PickupRequest): Unit = {
    scheduler.handlePickupRequest(request)
  }

  override def step(): Unit = actionPlanner.step()


}
