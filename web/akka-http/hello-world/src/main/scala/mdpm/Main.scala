package mdpm

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import scala.concurrent.Future
import scala.io.StdIn

object Main extends App {
  implicit val system = ActorSystem("mdpmsys")
  implicit val materializer = ActorMaterializer()

  implicit val executionContext = system.dispatcher

  val route: Route =
    path("hello") {
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
      }
    }

  val bound: Future[Http.ServerBinding] = Http()(system).bindAndHandle(route, "localhost", 8080)(materializer)

  // scalastyle:off
  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  // scalastyle:on

  StdIn.readLine() // let it run until user presses return

  // bound
  //   .flatMap(_.unbind())                      // trigger unbinding from the port
  //   .onComplete(_ => system.terminate())      // and shutdown when done
  val unbound = bound.flatMap(_.unbind())     // trigger unbinding from the port
  unbound.onComplete(_ => system.terminate()) // and shutdown when done
}
