Elevator
----

###Task
Design and implement an elevator control system. What data structures, interfaces and algorithms will you need? Your elevator control system should be able to handle a few elevators — up to 16.

You can use the language of your choice to implement an elevator control system. In the end, your control system should provide an interface for:

Querying the state of the elevators (what floor are they on and where they are going),
receiving an update about the status of an elevator,
receiving a pickup request,
time-stepping the simulation.

###Implementation

###Project Structure

Main code in src/main/scala
Test code in src/test/scala

Interfaces (traits), domain specific case classes and objects are located in the package domain. The implementations can be found in package core.

####Build and Run
The solution is written in scala with one Main object in the root of the project. It can be execute by running

```
sbt run
```

Tests can be executed by

```
sbt test
```

The main method handles one semi complex use case where multiple pick up commands happen over time. It shows the following use cases:

* Goal of first elevator trip has highest prio
* New elevator trips (on-the-fly pick ups) that are located on the way to the first goal will be picked up too
* When goals of the 'on-the-fly pick ups are reached during the trip to the first goal, these goals are also marked as reached and deleted from the destinations queue
* new trips that are not in the movement direction of the elevator (UP/DOWN) are queued to the pick up queue and will be picked up when they are the first pickup after the current goal was reached or when they are located on the path to the next goal
* new trips that are not on the path of the first elevator will be send to another free elevator
* multiple requests from the same floor are handled as well as mulitple get offs of passengers at the same floor

####Architecture

The main method starts an elevator control system wich starts 16 elevators as childs of the system. The elevator control system as well as the elevators are implemented as actors. 
Each elevator notifies the control system when a movement (floor or state change) has happened. The movement of the elevators
is simulated by an embedded scheduler in each elevator and is triggered every second. Therefore the control system is able to determine
the best fitting elevator for incoming elevator trips.
The function to find the best elevator is part of the interface of the elevator control system and can be implemented in different was, e.g. round robin, nearest floor distance, machine learning algo, etc
The control system handles:

* new trip requests (PickUp command)
* the demand for the current status (Status command) - the control system returns the state of all elevators
* a new step (Step command) - the control system forces all elevators to move

#### Not Considered

* More Tests especially regarding the control system
* Command line interface / other interface to trigger manually elevators or new trips
* max floor value in elevator
* better strategy to determine the best fitting elevator
* inactive elevators
* open/close door states of elevators as well as the time that it takes to move an elevator from one floor to the other



