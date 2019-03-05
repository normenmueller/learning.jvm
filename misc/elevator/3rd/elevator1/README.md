Elevator Control System
=======================

An app for elevator simulation written in Scala

## Description

Control system can be accessed by next interface:


```scala
trait ElevatorControlSystem {
  def status: Seq[ElevatorState]
  def update(id: Int, elevator: Elevator)
  def pickup(request: PickupRequest)
  def step()
}
```
Elevator might be in two states:
* Going for a pickup
* Delivering passengers

```scala
sealed trait ElevatorState 
case class LookingForPickup(id: Int, currentFloor: Floor) extends ElevatorState
case class DeliveringPassengers(id: Int,
                                currentFloor: Floor,
                                goalFloors: Set[Floor],
                                direction: MovingDirection) extends ElevatorState 
```

The algorithm of elevator depends on it's state:

If elevator is delivering passengers:
 * If on goal floor, then open the door
 * If there are any pickup requests in the same direction, take them, update goals and continue moving

If elevator is looking for a pickup:
 * Use a command provider by the scheduler
 

Two algorithms of scheduling are available:
 * Fist-Come-First-Served: assigns requests to elevators by the time of coming
 * Direction-Based: calculates the farthest request in both directions, and assigns elevators to them. 
   This decreases waiting time for the top and bottom passengers
   
## How to run

Install a sbt and type next command: 

```
sbt run
```