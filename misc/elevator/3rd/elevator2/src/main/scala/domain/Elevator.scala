package domain

import domain.Elevator.{Direction, ElevatorId, Floor}

/**
 * Basic functionality of an elevator.
 */
trait Elevator {

  /**
   * Id of the elevator.
   */
  val id: ElevatorId

  /**
   * Trigger movement.
   */
  def move(): Unit

  /**
   * Get the current direction of the movement
   * @return [[domain.Elevator.Direction]]
   */
  def getDirection: Direction

  /**
   * Get goal floor where the elevator is heading.
   * @return [[Floor]]
   */
  def getNewGoal: Floor

  /**
   * Get current floor.
   * @return [[Floor]]
   */
  def getCurrentFloor: Floor

}

object Elevator {

  type Floor = Int
  type ElevatorId = Int

  case class ElevatorTrip(from: Floor, to: Floor)

  sealed trait Direction

  case object Up extends Direction

  case object Down extends Direction

  case object NoDirection extends Direction


  case class ElevatorStatus(id: ElevatorId, currentFloor: Floor, goalFloor: Floor, direction: Direction, pickUps: Seq[ElevatorTrip], destinations: Seq[Floor]) {

    override def toString: String = {
      s"ID:$id currentFloor:$currentFloor goalFloor:$goalFloor direction:$direction pickUps: $pickUps destinations:$destinations"
    }
  }

}
