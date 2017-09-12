package learning
package concurrency
package latches
package system

class MySystem {
  import java.util.concurrent.CountDownLatch

  private val latch = new CountDownLatch(1)

  (new Thread {
    override def run(): Unit = {
      /* The `await` methods block until the current count reaches zero due to
       * invocations of the `countDown()` method.
       */
      latch.await()
    }
  }).start()

  def someMethod(): Unit = {
    println("someMethod")
  }

  def anotherMethod(): Unit = {
    println("anotherMethod")
  }

  def terminate(): Unit = {
    latch.countDown()
  }
}

object System extends App {
  val sys = new MySystem()
  sys.someMethod()
  sys.anotherMethod()
  // JVM does not terminate if the following call is commented out
  sys.terminate()
}
