package elevators

import elevators.control.{FCFSElevatorControlSystem, DirectionBasedControlSystem}
import elevators.models.{Floor, Passenger, Elevator}


object Simulation extends App {

  val controlSystem = new DirectionBasedControlSystem

  val elevators = (0 to 2).map(id => new Elevator(id, controlSystem.actionPlanner))

  var waitingPassengers = Seq(
    Passenger(1, Floor(1), Floor(6)),
    Passenger(2, Floor(4), Floor(8)),
    Passenger(3, Floor(9), Floor(2)),
    Passenger(4, Floor(8), Floor(3)),
    Passenger(5, Floor(1), Floor(4)),
    Passenger(6, Floor(1), Floor(6)),
    Passenger(7, Floor(4), Floor(8)),
    Passenger(8, Floor(9), Floor(2)),
    Passenger(9, Floor(8), Floor(3)),
    Passenger(10, Floor(1), Floor(4)),
    Passenger(11, Floor(5), Floor(1)),
    Passenger(12, Floor(5), Floor(1))
  )

  val totalPassengers = waitingPassengers.length

  var deliveredPassengers = Set.empty[Passenger]

  waitingPassengers.foreach(p => controlSystem.pickup(p.pickupRequest))

  elevators.zipWithIndex.foreach {
    case (e, id) => controlSystem.update(id, e)
  }


  while (deliveredPassengers.size != totalPassengers) {
    println(controlSystem.status)

    updateSimulation
    controlSystem.step()

    if (deliveredPassengers.size == totalPassengers) {
      println("all passengers are delivered")
    }
  }

  def updateSimulation = elevators
    .filter(_.isDeliveringPassengers)
    .foreach { elevator =>
    releasePassengersFromElevator(elevator)
    takePassengersToElevator(elevator)
  }


  def takePassengersToElevator(elevator: Elevator) = {
    val remaining = elevator.takePassengers(waitingPassengers.toSet)
    waitingPassengers = remaining.toSeq
  }

  def releasePassengersFromElevator(elevator: Elevator) = {
    val releasedPassengers = elevator.releasePassengers()

    releasedPassengers.foreach { p =>
      deliveredPassengers += p
    }
    if (releasedPassengers.nonEmpty) {
      println(s"passengers ${releasedPassengers.map(_.id).mkString(", ")} is done")
    }

  }

}
