package learning.netty
package echo

import java.util.concurrent.{ Executors, TimeUnit }

import scala.concurrent.{ ExecutionContext }

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.{ EventLoopGroup, ChannelFuture, ChannelInitializer, ChannelOption }
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel

import com.typesafe.scalalogging.{ StrictLogging => Logging }

class EchoServer(val port: Int) extends Logging {

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
            ch.pipeline().addLast(new EchoServerHandler)
          }
       })
       .option(ChannelOption.SO_BACKLOG.asInstanceOf[ChannelOption[Any]], 128)
       .childOption(ChannelOption.SO_KEEPALIVE.asInstanceOf[ChannelOption[Any]], true)

      val f = b.bind(port).sync()
      logger.info(s"""SERVER: up and running (port = $port).""")

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

class EchoServerHandler extends ChannelInboundHandlerAdapter with Logging {

  override def channelRead(ctx: ChannelHandlerContext, msg: Object): Unit = {
    logger.info("SERVER: entered status `channelRead`")

    val in = msg.asInstanceOf[ByteBuf]
    val txt = in.toString(io.netty.util.CharsetUtil.US_ASCII).trim()
    logger.info(s"SERVER: received message: $txt")

    // echo
    // Please note that we did not release the received message unlike we did in the DISCARD example.
    // It is because Netty releases it for you when it is written out to the wire.
    ctx.write(msg)
    ctx.flush()

    txt match {
      case "END" =>
        // Close the current channel
        ctx.channel().close();
        // Then close the parent channel (the one attached to the bind)
        ctx.channel().parent().close();
      case _ =>
    }

  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    ctx.close()
  }

}
