package learning.netty
package echo

object Main extends App {

  val srv = new EchoServer(8080)
  srv.run()

}
