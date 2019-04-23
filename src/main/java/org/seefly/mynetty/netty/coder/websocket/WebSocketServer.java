package org.seefly.mynetty.netty.coder.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 *
 * js代码
 * var ws = new WebSocket("ws://127.0.0.1:9999/server");
 * 		ws.onopen = function() {
 * 		console.error('握手成功');
 * 		alert('OK');};
 * 	ws.send('hello');
 *
 *
 *
 * @author liujianxin
 * @date 2019-04-19 10:11
 */
public class WebSocketServer {

    public static void main(String[] args) throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(new NioEventLoopGroup(),new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(
                                // 请求、响应编解码
                                new HttpServerCodec(),
                                // 消息聚合，处理分片消息
                                new HttpObjectAggregator(65536),
                                // 处理握手协议升级
                                new WebSocketServerProtocolHandler("/server"),
                                new TextFrameHandler(),
                                new ContinuationFrameHandler()
                                );
                    }
                }).bind(9999).sync().addListener((future) ->{
                    if(future.isSuccess()){
                        System.out.println("OK");
                    }
        });
    }

    private static final class TextFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame>{

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
            System.out.println(msg.text());
        }
    }

    private static final class ContinuationFrameHandler extends SimpleChannelInboundHandler<ContinuationWebSocketFrame>{

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ContinuationWebSocketFrame msg) throws Exception {
            System.out.println(msg.text());
        }
    }
}
