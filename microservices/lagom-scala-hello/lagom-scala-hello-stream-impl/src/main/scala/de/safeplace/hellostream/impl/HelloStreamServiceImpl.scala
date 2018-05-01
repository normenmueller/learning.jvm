package de.safeplace.hellostream.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import de.safeplace.hellostream.api.HelloStreamService
import de.safeplace.hello.api.{HelloService}
import scala.concurrent.Future

/**
  * Implementation of the HelloStreamService.
  */
// TODO How get's 'HelloService' injected?
class HelloStreamServiceImpl(helloService: HelloService) extends HelloStreamService {
  def stream = ServiceCall { hellos =>
    Future.successful(hellos.mapAsync(8)(helloService.hello(_).invoke()))
  }
}
