package learning.netty
package discard

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.{EventLoopGroup, ChannelInitializer, ChannelOption}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel

import com.typesafe.scalalogging.{ StrictLogging => Logging }

class DiscardServer(val port: Int) extends Logging {

  def run(): Unit = {
    logger.info(s"SERVER: starting...")

    val boss: EventLoopGroup = new NioEventLoopGroup
    val wrkr: EventLoopGroup = new NioEventLoopGroup

    try {
      val b = new ServerBootstrap
      b.group(boss, wrkr)
       .channel(classOf[NioServerSocketChannel])
       .childHandler(new ChannelInitializer[SocketChannel]() {
          override def initChannel(ch: SocketChannel): Unit = {
            ch.pipeline().addLast(new DiscardServerHandler)
          }
       })
       .option(ChannelOption.SO_BACKLOG.asInstanceOf[ChannelOption[Any]], 128)
       .childOption(ChannelOption.SO_KEEPALIVE.asInstanceOf[ChannelOption[Any]], true)

      val f = b.bind(port).sync()
      logger.info(s"""SERVER: up and running (port = $port).""")

      /* Wait until the server socket is closed. In this example, this does not
       * happen, but you can do that to gracefully shut down your server. Note,
       * this call blocks JVM termination.
       */
      f.channel().closeFuture.sync()

      logger.info(s"SERVER: shutdown done.")
    } finally {
      wrkr.shutdownGracefully()
      boss.shutdownGracefully()
    }
  }

}

import io.netty.buffer.ByteBuf
import io.netty.util.ReferenceCountUtil
import io.netty.channel.{ ChannelHandlerContext, ChannelInboundHandlerAdapter }

class DiscardServerHandler extends ChannelInboundHandlerAdapter with Logging {

  override def channelRead(ctx: ChannelHandlerContext, msg: Object): Unit = {
    try {

    } finally {
      ReferenceCountUtil.release(msg)
    }
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    ctx.close()
  }

}
