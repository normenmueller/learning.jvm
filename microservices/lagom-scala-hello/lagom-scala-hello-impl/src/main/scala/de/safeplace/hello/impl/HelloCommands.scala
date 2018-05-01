package de.safeplace.hello.impl

import akka.Done
import play.api.libs.json.{Format, Json}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType

/** This interface defines all the commands that the HelloWorld entity supports. */
sealed trait HelloCommand[R] extends ReplyType[R]

/** A command to switch the greeting message. It has a reply type of [[Done]], which is sent back to the caller when all the events emitted by this command are successfully persisted. */
case class UseGreetingMessage(message: String) extends HelloCommand[Done]

object UseGreetingMessage {
  implicit val format: Format[UseGreetingMessage] = Json.format
}

/** A command to say hello to someone using the current greeting message. The reply type is String, and will contain the message to say to that person. */
case class Hello(name: String) extends HelloCommand[String]

object Hello {
  implicit val format: Format[Hello] = Json.format
}
