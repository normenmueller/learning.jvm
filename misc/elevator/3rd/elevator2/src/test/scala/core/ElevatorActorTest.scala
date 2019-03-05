package core

import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, TestKit}
import domain.Elevator.ElevatorTrip
import domain.ElevatorControlSystem.{Step, PickUp}
import org.specs2.mutable.SpecificationLike

class ElevatorActorTest extends TestKit(ActorSystem("ElevatorActorTest")) with SpecificationLike {
  "An Elevator" >> {
    "pick up a new trip" >> {
      val elevator = TestActorRef(new ElevatorActor(1, enableScheduler = false))
      val trip = ElevatorTrip(1, 2)
      elevator ! PickUp(trip)
      elevator.underlyingActor.pickUpQueue.must_===(Vector(trip))
    }
    "pick up a new trip at floor 1 and reach goal at 2nd floor after two steps" >> {
      val elevator = TestActorRef(new ElevatorActor(1, enableScheduler = false))
      val trip = ElevatorTrip(1, 2)
      elevator ! PickUp(trip)
      elevator ! Step
      elevator ! Step
      elevator.underlyingActor.currentFloor.must_===(2)
      elevator.underlyingActor.goalFloor.must_===(2)
    }

    "pick up a new trip, people and release then while moving towards goals" >> {
      val elevator = TestActorRef(new ElevatorActor(1, enableScheduler = false))
      elevator ! PickUp(ElevatorTrip(1, 6))
      elevator ! PickUp(ElevatorTrip(2, 5))
      elevator.underlyingActor.pickUpQueue.size.must_==(2)

      elevator ! Step // people in and out then move to 1
      elevator.underlyingActor.goalFloor.must_===(1)
      elevator ! Step // people in and out then move to 2
      elevator.underlyingActor.currentFloor.must_===(2)
      elevator.underlyingActor.goalFloor.must_===(6)
      elevator.underlyingActor.pickUpQueue.size.must_==(1)
      elevator.underlyingActor.destinationQueue.must_===(Vector(6))

      elevator ! Step // people in and out then move to 3
      elevator.underlyingActor.destinationQueue.must_===(Vector(6, 5))
      elevator.underlyingActor.pickUpQueue.size.must_==(0)
      elevator ! Step // now move to 4
      elevator ! Step // now move to 5

      elevator ! Step // people in and out then move to 6
      elevator.underlyingActor.currentFloor.must_===(6)
      elevator.underlyingActor.destinationQueue.must_===(Vector(6))
      elevator.underlyingActor.goalFloor.must_===(6)

      elevator ! Step // final step to let people in and out and look for new goals
      elevator.underlyingActor.destinationQueue.must_===(Vector())

    }
  }

  trait ElevatorProbe {
    val elevator = TestActorRef(new ElevatorActor(1, enableScheduler = false))
  }

}

