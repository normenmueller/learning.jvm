package de.safeplace.hello.impl

import play.api.libs.json.{Format, Json}
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag}

/** This interface defines all the events that the HelloEntity supports. */
sealed trait HelloEvent extends AggregateEvent[HelloEvent] {
  def aggregateTag = HelloEvent.Tag
}

object HelloEvent {
  val Tag = AggregateEventTag[HelloEvent]
}

/** An event that represents a change in greeting message. */
case class GreetingMessageChanged(message: String) extends HelloEvent

object GreetingMessageChanged {
  implicit val format: Format[GreetingMessageChanged] = Json.format
}
