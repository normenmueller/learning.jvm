package elevators.models

import Constants._

case class Floor(number: Int) {
  require(number <= HighestFloorNumber)
  require(number >= LowestFloorNumber)

  def upperFloor = copy(number + 1)

  def lowerFloor = copy(number - 1)
}

object Floor {
  val FloorOrdering = Ordering.by[Floor, Int](_.number)
}