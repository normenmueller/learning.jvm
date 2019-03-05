package domain

import domain.Elevator.{ElevatorId, ElevatorStatus, ElevatorTrip}


/**
 * Baisc interface of an elevator control system.
 *
 * A basic implementation of an elevator control system must provide at lease
 *
 * A method to return the current status of all controlled elevators.
 * A method to pick up and move passengers (in form of an [[ElevatorTrip]])
 * A method to force all elevators to do a step.
 * A method which gets the nearest/best/... elevator to handle a given trip.
 */
trait ElevatorControlSystem {

  /**
   * Returns the current state of all elevators.
   * @return elevator states
   */
  def status(): Seq[ElevatorStatus]

  /**
   * Routes a given [[ElevatorTrip]] request to an elevator.
   * @param trip
   */
  def pickup(trip: ElevatorTrip): Unit

  /**
   * Force a step of all elevators.
   */
  def step(): Unit

  /**
   * Get the id of the nearest elevator to handle a given [[ElevatorTrip]].
   * @return
   */
  def getIdOfNearestElevator(trip: ElevatorTrip): ElevatorId

}


object ElevatorControlSystem {

  sealed trait Command

  case object Status extends Command

  case object Step extends Command

  case class PickUp(trip: ElevatorTrip) extends Command

  case class ElevatorControlSystemStatus(elevators: Seq[ElevatorStatus])

}