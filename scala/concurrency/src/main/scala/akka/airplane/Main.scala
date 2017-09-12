package learning
package concurrency
package akka
package airplane
package avionics

// Wyatt - 2013 - Akka Concurrency, page 104

import _root_.akka.actor.{ Props, Actor, ActorRef, ActorSystem }
import _root_.akka.pattern.ask
import _root_.akka.util.Timeout
import scala.concurrent.Await
import scala.concurrent.duration._
// The futures created by the ask syntax need an execution context on which to run, and we will use the default global
// instance for that context
import scala.concurrent.ExecutionContext.Implicits.global

object Avionics {
  // needed for '?' below
  implicit val timeout = Timeout(5.seconds)

  val system = ActorSystem("PlaneSimulation")
  val plane = system.actorOf(Props[Plane], "Plane")

  def main(args: Array[String]) {

    // Grab the controls
    // We first ask the plane for the controls, which returns a Future. Unfortunately, the Future only knows about the
    // type that is returned as an `Any`, due to the fact that messages between actors are of type `Any`.
    // As a result of that loss of typing, we must use the Future's `mapTo` facility to coerce it down to the type we're
    // expecting.
    val control = Await.result((plane ? Plane.GiveMeControl).mapTo[ActorRef], 5.seconds)
    // Takeoff!
    system.scheduler.scheduleOnce(200.millis) {
      control ! ControlSurfaces.StickBack(1f)
    }
    // Level out
    system.scheduler.scheduleOnce(1.seconds) {
      control ! ControlSurfaces.StickBack(0f)
    }
    // Climb
    system.scheduler.scheduleOnce(3.seconds) {
      control ! ControlSurfaces.StickBack(0.5f)
    }
    // Level out
    system.scheduler.scheduleOnce(4.seconds) {
      control ! ControlSurfaces.StickBack(0f)
    }
    // Shut down
    system.scheduler.scheduleOnce(5.seconds) {
      system.terminate()
    }

  }

}

// vim: set tw=120 ft=scala:
