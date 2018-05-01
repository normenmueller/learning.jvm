package de.safeplace.hello.impl

import java.time.LocalDateTime
import scala.collection.immutable.Seq
import akka.Done
import play.api.libs.json.{Format, Json}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}

/**
  * This is an event sourced entity.
  *
  * It has a state, [[HelloState]], which stores what the greeting should be (eg, "Hello").
  *
  * Event sourced entities are interacted with by sending them commands.
  *
  * This entity supports two commands, a [[UseGreetingMessage]] command, which is
  * used to change the greeting, and a [[Hello]] command, which is a read
  * only command which returns a greeting to the name specified by the command.
  *
  * Commands get translated to events, and it's the events that get persisted by
  * the entity. Each event will have an event handler registered for it, and an
  * event handler simply applies an event to the current state. This will be done
  * when the event is first created, and it will also be done when the entity is
  * loaded from the database - each event will be replayed to recreate the state
  * of the entity.
  *
  * This entity defines one event, the [[GreetingMessageChanged]] event,
  * which is emitted when a [[UseGreetingMessage]] command is received.
  */
class HelloEntity extends PersistentEntity {

  override type Command = HelloCommand[_]
  override type Event = HelloEvent
  override type State = HelloState

  /** The initial state. This is used if there is no snapshotted state to be found. */
  override def initialState: HelloState = HelloState("Hello", LocalDateTime.now.toString)

  /** An entity can define different behaviours for different states, so the behaviour
    * is a function of the current state to a set of actions.
    */
  // @formatter:off
  override def behavior: Behavior = {
    case HelloState(message, _) => Actions()

      .onCommand[UseGreetingMessage, Done] {
        case (UseGreetingMessage(newMessage), ctx, state) =>
          // In response to this command, we want to first persist it as a [[GreetingMessageChanged]] event
          // READ: "persist event 'GreetingMessageChanged', then execute the passed function
          ctx.thenPersist(GreetingMessageChanged(newMessage)) { _ =>                                             // [1]
            // Then once the event is successfully persisted, we respond with done.
            ctx.reply(Done)                                                                                      // [4]
          }
      }

      .onReadOnlyCommand[Hello, String] {
        case (Hello(name), ctx, state) =>
          // Reply with a message built from the current message, and the name of
          // the person we're meant to say hello to.
          ctx.reply(s"$message, $name!")
      }

      .onEvent {
        // Event handler for the GreetingMessageChanged event
        case (GreetingMessageChanged(newMessage), state) =>                                                      // [2]
          // We simply update the current state to use the greeting message from
          // the event.
          HelloState(newMessage, LocalDateTime.now().toString)                                                   // [3]
      }

  }
  // @formatter:on
}

/** The current state held by the persistent entity. */
case class HelloState(message: String, timestamp: String)

object HelloState {
  /**
    * Format for the hello state.
    *
    * Persisted entities get snapshotted every configured number of events. This
    * means the state gets stored to the database, so that when the entity gets
    * loaded, you don't need to replay all the events, just the ones since the
    * snapshot. Hence, a JSON format needs to be declared so that it can be
    * serialized and deserialized when storing to and from the database.
    */
  implicit val format: Format[HelloState] = Json.format
}

/**
  * Akka serialization, used by both persistence and remoting, needs to have
  * serializers registered for every type serialized or deserialized. While it's
  * possible to use any serializer you want for Akka messages, out of the box
  * Lagom provides support for JSON, via this registry abstraction.
  *
  * The serializers are registered here, and then provided to Lagom in the
  * application loader.
  */
object HelloSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[UseGreetingMessage],
    JsonSerializer[Hello],
    JsonSerializer[GreetingMessageChanged],
    JsonSerializer[HelloState]
  )
}
