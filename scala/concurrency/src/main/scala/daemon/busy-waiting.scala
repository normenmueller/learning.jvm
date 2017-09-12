package learning
package concurrency
package daemon
package worker

import scala.collection._

// Prokopec - 2014 - Learning Concurrent Programming in Scala, page 47

object Main extends AnyRef with App {

  private val tasks = mutable.Queue[() => Unit]()

  val worker = new Thread {

    def poll(): Option[() => Unit] = tasks.synchronized {
      if (tasks.nonEmpty) Some(tasks.dequeue()) else None
    }

    override def run() = while (true) poll() match {
      case Some(task) => task()
      case None =>
    }

  }

  worker.setName("Worker")

  /* When a Java Virtual Machine starts up, there is usually a single '''non-daemon''' thread which typically calls the
   * method named `main` of some designated class.
   *
   * When code running in some thread creates a new `Thread` object, the new thread has its priority initially set equal
   * to the priority of the creating thread (the parent), and is a '''daemon''' thread if and only if the creating
   * thread is a daemon (cf. `this.daemon = parent.isDaemon();` at `java.lang.Thread#init`). 
   *
   * Thus if the subsequent line is commented out, the `worker` thread is, due to its parent, a '''non-daemon''' thread
   * and prevents JVM from termination.
   */
  worker.setDaemon(true)

  worker.start()

  def asynchronous(body: =>Unit) = tasks.synchronized {
    tasks.enqueue(() => body)
  }

  asynchronous { log("Hello") }
  asynchronous { log(" world!") }

  Thread.sleep(5000)

}

// vim: set tw=120 ft=scala:
