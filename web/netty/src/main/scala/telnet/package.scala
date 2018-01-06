package learning

package object netty {

  import scalaz.Show
  import io.netty.bootstrap.ServerBootstrap

  implicit object serverBootstrapInstance extends AnyRef with Show[ServerBootstrap] {
    override def show(f: ServerBootstrap) = f.toString()
  }

}