package org.seefly.mynetty.netty.transport;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.SocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * 演示几种传输机制之间的差别
 *
 *
 * @author liujianxin
 * @date 2019-04-08 14:14
 */
public class DifferenceDemo {

    /**
     * 在几种传输方式之间的切换
     */
    public void server() throws InterruptedException {
        final ByteBuf buf = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hi\r\n", Charset.forName("utf-8")));
        // 阻塞IO使用的EventLoopGroup
        //EventLoopGroup group = new OioEventLoopGroup();

        // 非阻塞
        //EventLoopGroup group = new NioEventLoopGroup();

        // 基于Linux平台的 epoll API 高性能异步IO
        EventLoopGroup group = new EpollEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(group)
                // 阻塞IO使用的管道
                //.channel(OioServerSocketChannel.class)

                //非阻塞
                //.channel(NioServerSocketChannel.class)

                // 基于Linux平台的高性能异步IO
                .channel(EpollServerSocketChannel.class)
                .localAddress(new InetSocketAddress(8888))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch)  {
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelActive(ChannelHandlerContext ctx)  {
                                ctx.writeAndFlush(buf.duplicate()).addListener(ChannelFutureListener.CLOSE);
                            }
                        });
                    }
                });
        b.bind().sync().channel().closeFuture().sync();

    }
}
