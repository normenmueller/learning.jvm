package learning
package concurrency
package futures
package flatmap

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

  // fut3
  val f: String => Future[Int] = x => Future {
    val start = ">>> I'm `f` and I'm running asynchronously"
    println(start)
    Thread.sleep(5000) // a long running operation
    val stop = ">>> I'm `f` and I'm done"
    println(stop)
    x.toInt
  }

  /* The `flatMap` method takes a function that maps the value of `fut1` to a
   * new future `fut3`. Future `fut2`, immidiately returned by `flatMap`, is
   * completed once `fut3` is completed.
   *
   * When `fut1` is completed, then run `f` with the result of `fut1`. Then,
   * when `fut3` is completed, complete `fut2` with the result of `fut3`. 
   */

  val fut2: Future[Int] = fut1 flatMap f

  /* polling
   */

  for (n <- 1 to 100) {
    println(f"$n%02d: Fut1 is completed: ${fut1.isCompleted} | Fut2 is completed: ${fut2.isCompleted}")
    Thread.sleep(100)
  }

}

// vim: set tw=80:
