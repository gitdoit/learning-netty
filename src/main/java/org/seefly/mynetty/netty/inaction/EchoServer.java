package org.seefly.mynetty.netty.inaction;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author liujianxin
 * @date 2019-03-28 14:13
 */
public class EchoServer {
    public static void main(String[] args) throws InterruptedException {
        // 创建 EventLoopGroup
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            //创建 ServerBootstrap
            b.group(group)
                    // 指定使用 NIO 的传输 Channel
                    .channel(NioServerSocketChannel.class)
                    // 设置 socket 地址使用所选的端口
                    .localAddress(new InetSocketAddress(8888))
                    // 添加 EchoServerHandler 到 Channel 的 ChannelPipeline
                    // 当一个新的连接被接受，一个新的子 Channel 将被创建， ChannelInitializer 会添加我们EchoServerHandler 的实例到 Channel 的 ChannelPipeline。
                    // 正如我们如前所述，这个处理器将被通知如果有入站信息。
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoServerHandle());
                        }
                    });
            // 绑定的服务器;sync 等待服务器关闭
            ChannelFuture f = b.bind().sync();
            System.out.println(EchoServer.class.getName() + " started and listen on " + f.channel().localAddress());
            // 关闭 channel 和 块，直到它被关闭
            f.channel().closeFuture().sync();
        } finally {
            // 关机的 EventLoopGroup，释放所有资源。
            group.shutdownGracefully().sync();
        }
    }
}
