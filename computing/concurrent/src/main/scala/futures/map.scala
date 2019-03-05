package learning.concurrency
package futures
package map

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main extends AnyRef with App {

  import scala.concurrent.Future
  import scala.concurrent.ExecutionContext.Implicits.global

  val fut1: Future[String] = Future {
    val start = ">>> I'm `fut1` and I'm running asynchronously"
    println(start)
    Thread.sleep(1000) // a long running operation
    val stop = ">>> I'm `fut1` and I'm done"
    println(stop)
    "result"
  }

  val f: String => Int = x => {
    val start = ">>> I'm `f` and I'm running"
    println(start)
    Thread.sleep(5000) // a long running operation
    val stop = ">>> I'm `f` and I'm done"
    println(stop)
    x.length
  }

  val g: Int => Int = x => {
    val start = ">>> I'm `g` and I'm running"
    println(start)
    Thread.sleep(5000) // a long running operation
    val stop = ">>> I'm `g` and I'm done"
    println(stop)
    x + 1
  }

  /* the returned future `fut2` is completed with a mapped value from the
   * original future 
   */

  val fut2: Future[Int] = fut1 map f map g

  while(!fut2.isCompleted) {
    print(".")
    Thread.sleep(500)
  }
  //println(Await.result(fut2, Duration.Inf))

  /* polling to demo non-blocking execution of `f`, i.e., the subsequent loop is
   * running w/o interruption but the console output is interleaved with outputs
   * from `fut1` and `f`.
   */

//  for (n <- 1 to 200) {
//    println(f"$n%02d: Fut1 is completed: ${fut1.isCompleted} | Fut2 is completed: ${fut2.isCompleted}")
//    Thread.sleep(100)
//  }

}
