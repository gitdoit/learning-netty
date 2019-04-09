package org.seefly.mynetty.netty.bootstrap;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

/**
 * @author liujianxin
 * @date 2019-04-09 14:54
 */
public class Demo {
    /**
     * 应用于客户端的引导程序
     */
    public void client(){
        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                // 不同的EventLoopGroup要使用不同的Channel实现，netty包结构可见
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new SimpleChannelInboundHandler<String>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                        System.out.println("客户端接收到消息"+msg);
                    }
                });
        bootstrap.connect(new InetSocketAddress("www.baidu.com",80))
                .addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()){
                        System.out.println("连接成功！");
                    }else {
                        System.out.println("连接失败!");
                        future.cause().printStackTrace();
                    }
        });
        //指定新创建的Channel的属性值，这些属性通过bind 或 connect方法设置到Channel
        //bootstrap.attr(null,null);
        //bootstrap.option(null,null);
    }


    /**
     * 应用于服务器的引导程序
     * 服务器的引导程序和客户端的引导程序不一样
     * 因为他需要使用一个父Channel来接收来自客户端的连接，并创建子Channel用于他们之间的通讯
     */
    public void server(){
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(new NioEventLoopGroup())
                // 父Channel使用何种通道类型，它负责创建子Channel，这些子Channel代表了被接收的连接
                .channel(ServerSocketChannel.class)
                // 父Channel也是一个Channel，也需要一个ChannelPipeline以及ChannelHandle来处理事件
                //.handler(new SimpleChannelInboundHandler<String>() {@Override protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {});

                // 应用于子Channel上的ChannelHandler
                .childHandler(new SimpleChannelInboundHandler<String>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

                    }
                });
        // 绑定之后上面的通道将被创建
        ChannelFuture bind = serverBootstrap.bind(80);
        bind.addListener((future -> {
            if(future.isSuccess()){
                System.out.println("Server bind");
            }else {
                System.out.println("Fail");
                future.cause().printStackTrace();
            }
        }));
    }

    /**
     * 一个Channel上添加多个ChannelHandler
     */
    public void moreChannelHandler() throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(new NioEventLoopGroup(),new NioEventLoopGroup())
                .channel(ServerSocketChannel.class)
                // 一个特殊的ChannelHandler实现了ChannelInboundHandlerAdapter它的initChannel方法可以注册多个ChannelHandler
                // 并在注册完成后，将自身从EventLoop上移除
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("A",null);
                        pipeline.addLast("B",null);
                    }
                }).bind(80).sync();
    }

    /**
     * 可以在引导的同时配置每个Channel的连接信息
     * 或者附加一些其他数据
     */
    public void channelOption(){
        // 这种方式用在初始化静态变量的时候，在多线程环境下使用可能会抛出异常
        AttributeKey<Integer> id = AttributeKey.newInstance("ID");
        // 这个线程安全
        // AttributeKey<Integer> id = AttributeKey.valueOf("ID");

        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                .channel(NioSocketChannel.class)
                .group(new NioEventLoopGroup())
                // 我不知道这个东西的应用场景是啥
                .attr(id,123456)
                // 给每个Channel设置长时连接
                .option(ChannelOption.SO_KEEPALIVE,true)
                // 给每个连接设置连接超时时间
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                .handler(new SimpleChannelInboundHandler<String>() {
                    @Override
                    public void channelRegistered(ChannelHandlerContext ctx){
                        // 创建Channel时分配的属性，可以在整个生命周期内通过Channel拿到
                        Integer integer = ctx.channel().attr(id).get();
                        System.out.println("用户"+integer+"连接来了");
                    }

                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                        System.out.println("收到消息！");
                    }
                });
    }


}
