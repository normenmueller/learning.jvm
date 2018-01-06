package learning.netty
package file

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.{ EventLoopGroup, ChannelFuture, ChannelInitializer, ChannelOption }
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.LineBasedFrameDecoder
import io.netty.handler.codec.string.{ StringDecoder, StringEncoder }
import io.netty.handler.logging.{ LogLevel, LoggingHandler }
import io.netty.handler.stream.ChunkedWriteHandler
import io.netty.util.CharsetUtil

import com.typesafe.scalalogging.{ StrictLogging => Logging }

class FileServer(val port: Int) extends Logging {

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
            ch.pipeline().addLast(
              new LoggingHandler(LogLevel.INFO),
              new StringEncoder(CharsetUtil.UTF_8),
              new LineBasedFrameDecoder(8192),
              new StringDecoder(CharsetUtil.UTF_8),
              new ChunkedWriteHandler(),
              new FileServerHandler()
            )
          }
       })
       .option(ChannelOption.SO_BACKLOG.asInstanceOf[ChannelOption[Any]], 100)

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

import java.io.RandomAccessFile;

import io.netty.buffer.ByteBuf
import io.netty.util.ReferenceCountUtil
import io.netty.channel.{ ChannelHandlerContext, ChannelInboundHandlerAdapter, DefaultFileRegion, SimpleChannelInboundHandler }

class FileServerHandler extends SimpleChannelInboundHandler[String] with Logging {

  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    ctx.writeAndFlush("HELO: Type the path of the file to retrieve.\n")
  }

  override def channelRead0(ctx: ChannelHandlerContext, msg: String): Unit = {
    logger.info("SERVER: entered status `channelRead`")

    var raf: RandomAccessFile = null 
    var length: Long = -1

    try {
      raf = new RandomAccessFile(msg, "r")
      length = raf.length()
    } catch { 
      case e: Exception => 
        ctx.writeAndFlush("ERR: " + e.getClass().getSimpleName() + ": " + e.getMessage() + '\n')
        return
    } finally if (length < 0 && raf != null) raf.close()

    ctx.write("OK: " + raf.length() + '\n')

    ctx.write(new DefaultFileRegion(raf.getChannel(), 0, length))
    //ctx.write(new ChunkedFile(raf))
    ctx.writeAndFlush("\n")
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    ctx.close()
  }

}
