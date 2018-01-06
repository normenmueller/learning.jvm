# Future#map

```scala
override def map[S](f: T => S)(implicit executor: ExecutionContext): Future[S] = {
  val p = Promise[S]()
  onComplete { v => p complete (v map f) }
  p.future
}
```

# Future#flatmap

```scala
def flatMap[S](f: T => Future[S])(implicit executor: ExecutionContext): Future[S] = {
  import scala.concurrent.impl.Promise.DefaultPromise
  val p = new DefaultPromise[S]()
  onComplete {
    case f: Failure[_] => p complete f.asInstanceOf[Failure[S]]
    case Success(v) => try f(v) match {
      // If possible, link DefaultPromises to avoid space leaks
      case dp: DefaultPromise[_] => dp.asInstanceOf[DefaultPromise[S]].linkRootOf(p)
      case fut => fut.onComplete(p.complete)(internalExecutor)
    } catch { case NonFatal(t) => p failure t }
  }
  p.future
}
```
