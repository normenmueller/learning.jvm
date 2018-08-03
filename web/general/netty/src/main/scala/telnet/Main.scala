package learning.netty
package telnet

import java.util.Date
import java.net.InetAddress

import io.netty.bootstrap.ServerBootstrap

import io.netty.channel.{ChannelFutureListener, ChannelInitializer, ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel

import io.netty.handler.logging.{LogLevel, LoggingHandler}

import io.netty.handler.codec.{Delimiters, DelimiterBasedFrameDecoder}
import io.netty.handler.codec.string.{StringEncoder, StringDecoder}

/**
 * Before Netty 4.x the start process of a server was similar as follows:
 *
 * {{{
 * ChannelFactory factory = new NioServerSocketChannelFactory1(
 *   Executors.newCachedThreadPool(),
 *   Executors.newCachedThreadPool());
 *
 * ServerBootstrap bootstrap = new ServerBootstrap2(factory);
 *
 * bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
 *   public ChannelPipeline getPipeline() {
 *     return Channels.pipeline(new DiscardServerHandler());
 *   }
 * });
 *
 * bootstrap.setOption("child.tcpNoDelay", true);
 * bootstrap.setOption("child.keepAlive", true);
 *
 * bootstrap.bind(new InetSocketAddress(8080));
 * }}}
 *
 * Cf. [[http://docs.jboss.org/netty/3.2/guide/html/start.html http://docs.jboss.org/netty/3.2/guide/html/start.html]]
 */
object Main extends AnyRef with App {

  val port = 8023

  /* Boss threads create and connect/bind sockets and then pass them off to the
   * worker threads. In the server, there is one boss thread allocated per
   * listening socket.
   */
  val boss = new NioEventLoopGroup(1)

  /* Worker threads perform all the asynchronous I/O. They are not general
   * purpose threads and developers should take precautions not to assign
   * unrelated tasks to threads in this pool which may cause the threads to
   * block, be rendered unable to perform their real work which in turn may
   * cause deadlocks and an untold number of performance issues.
   */
  val wrkr = new NioEventLoopGroup()

  try {
    val b = new ServerBootstrap()
    b.group(boss, wrkr)

     /* The class which is used to create `Channel` instances from when calling
      * `bind`. Is used to instantiate a new channel to accept incoming
      * connections.
      *
      * For now, think of a channel as a vehicle for incoming ("inbound") and
      * outgoing ("outbound") data. As such, it can be "open" or "closed",
      * "connected" or "disconnected".
      */
     .channel(classOf[NioServerSocketChannel])

     /* Netty uses callbacks internally when handling events; when a callback
      * is triggered the event can be handled by an implementation of
      * `ChannelHandler`. For example, when a new connection has been
      * established the `ChannelHandler` callback `channelActive()` will be
      * called.
      *
      * In Netty the `ChannelHandler` provides the basic abstraction for
      * handlers. For now you can think of each handler instance as a kind of
      * callback to be executed in response to a specific event.
      *
      * `childHandler` sets the `ChannelHandler` which is evaluated by a newly
      * accepted `Channel`. This handler serves the request for the newly
      * accepted `Channel`.
      *
      * The `ChannelInitializer` is a special handler that is purposed to help
      * a user configure a new `Channel`. It is most likely that you want to
      * configure the `ChannelPipeline` of the new Channel by adding some
      * handlers such as `TelnetServerHandler` to implement your network
      * application.
      */
     .childHandler(new ChannelInitializer[SocketChannel]() {

       override def initChannel(ch: SocketChannel): Unit =
         ch.pipeline()
           .addLast(new LoggingHandler(LogLevel.INFO))
           .addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter(): _*))
           .addLast(new StringDecoder())
           .addLast(new StringEncoder())
           .addLast(new TelnetServerHandler())

    })
    //---
    import scalaz.syntax.show._
    b.println
    //---
    b.bind(port).sync().channel().closeFuture().sync();
  } finally {
    boss.shutdownGracefully()
    wrkr.shutdownGracefully()
  }

  /* ChannelInboundHandlerAdapter which allows to explicit only handle a
   * specific type of messages. For example here is an implementation which
   * only handle `String` messages.
   */
  class TelnetServerHandler extends SimpleChannelInboundHandler[String] {

    override def channelActive(ctx: ChannelHandlerContext): Unit = {
      ctx.write("Welcome to " + InetAddress.getLocalHost.getHostName + "!\r\n")
      ctx.write("It is " + new Date() + " now.\r\n")
      ctx.flush()
    }

    override def channelRead0(ctx: ChannelHandlerContext, request: String): Unit = {
      val (response, close, shutdown) =
        if(request.isEmpty) ("Please type something.\r\n", false, false)
        else if("bye".equals(request.toLowerCase)) ("Have a good day!\r\n", true, false)
        else if("shutdown".equals(request.toLowerCase)) ("Let's call it a day!\r\n", false, true)
        else ("Did you say '" + request + "'?\r\n", false, false)

      val fut = ctx.write(response)

      if(close)
        fut.addListener(ChannelFutureListener.CLOSE)

      // cf. http://stackoverflow.com/questions/28032092/shutdown-netty-programmatically
      if(shutdown) {
        // Close the current channel
        fut.channel().close()
        // Then close the parent channel (the one attached to the bind)
        fut.channel().parent().close()
      }
    }

    override def channelReadComplete(ctx: ChannelHandlerContext): Unit =
      ctx.flush()

    override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
      cause.printStackTrace()
      ctx.close()
    }

  }

}

// Resources:
// [1] http://seeallhearall.blogspot.de/2012/05/netty-tutorial-part-1-introduction-to.html
// [2] https://manning-content.s3.amazonaws.com/download/f/20d72b1-2b7b-46fe-9258-ac57e4ca800d/netty_meap_ch1.pdf
