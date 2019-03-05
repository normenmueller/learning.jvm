package learning
package concurrency
package akka
package airplane
package avionics

// Wyatt - 2013 - Akka Concurrency, page 104

import _root_.akka.actor.{ Props, Actor, ActorLogging }

object Plane {
  // Returns the control surface to the Actor that asks for them
  case object GiveMeControl
}

// We want the Plane to own the Altimeter and we're going to do that by passing in a specific factory we can use to
// build the Altimeter
class Plane extends Actor with ActorLogging {
  import Altimeter._
  import Plane._

  val altimeter = context.actorOf(Props[Altimeter], "Altimeter")
  val controls = context.actorOf(Props(new ControlSurfaces(altimeter)), "ControlSurfaces")

  import EventSource._
  override def preStart() {
    altimeter ! RegisterListener(self)
  }

  def receive = {
    case GiveMeControl =>
      log.info("Plane giving control.")
      sender ! controls
    case AltitudeUpdate(altitude) =>
      log.info(s"Altitude is now: $altitude")
  }

}

// vim: set tw=120 ft=scala:
