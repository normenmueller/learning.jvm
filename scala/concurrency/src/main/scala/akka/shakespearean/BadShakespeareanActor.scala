package learning
package concurrency
package akka
package shakespearean

// Wyatt - 2013 - Akka Concurrency, page 91

import _root_.akka.actor.{ Actor, ActorRef, Props, ActorSystem }

class BadShakespeareanActor extends Actor {

  // The 'Business Logic'
  def receive = {
    case "Good Morning"    => println("Him: Forsooth 'tis the 'morn, but mourneth for thou doest I do!")
    case "You're terrible" => println("Him: Yup")
  }

}

object BadShakespeareanMain {

  val system: ActorSystem = ActorSystem("BadShakespearean")
  val actor: ActorRef  = system.actorOf(Props[BadShakespeareanActor], "Shake")

  /* The ''tell'' syntax, denoted by `!`.
   *
   * The `!` method is an asynchronous message pass that puts the message in the actor’s Mailbox and returns
   * immediately, safe in the knowledge that the message is in the queue.
   */
  def send(msg: String): Unit = {
    println(s"Me:  $msg")
    (actor ! msg): Unit
    Thread.sleep(100)
  }

  // driver
  def main(args: Array[String]) {
    send("Good Morning")
    send("You're terrible")
    system.terminate()
  }

}

// vim: set tw=120 ft=scala:
