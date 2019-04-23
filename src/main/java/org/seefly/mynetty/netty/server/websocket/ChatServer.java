package org.seefly.mynetty.netty.server.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;

/**
 * @author liujianxin
 * @date 2019-04-20 13:20
 */
public class ChatServer  {
    /**channel组，跟群组一个意思*/
    private final ChannelGroup channels = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    private final EventLoopGroup group = new NioEventLoopGroup();
    private Channel channel;

    public ChannelFuture start(){
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(group)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChatServerInitializer(channels));
        ChannelFuture bind = bootstrap.bind(8866);
        bind.syncUninterruptibly();
        channel = bind.channel();
        return bind;
    }

    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer();
        ChannelFuture start = chatServer.start();
        Runtime.getRuntime().addShutdownHook(new Thread(chatServer::destroy));
        start.channel().closeFuture().syncUninterruptibly();
    }

    public void  destroy(){
        if(channel != null){
            channel.close();
        }
        channels.close();
        group.shutdownGracefully();
    }

}
