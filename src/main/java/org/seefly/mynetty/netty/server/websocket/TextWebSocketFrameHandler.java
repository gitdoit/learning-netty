package org.seefly.mynetty.netty.server.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * 这个类只是单纯的把用户发的消息分发到其他所有用户哪里
 * 并且监听websocket连接成功事件，告诉其他人有新人加入会话了
 * @author liujianxin
 * @date 2019-04-19 16:06
 */
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private final ChannelGroup group;

    public TextWebSocketFrameHandler(ChannelGroup group) {
        this.group = group;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 该成员发来的消息，直接发送到群组中
        group.writeAndFlush(msg.retain());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE){
            // 如果握手完成，则移除HttpRequestHandler
            ctx.pipeline().remove(HttpRequestHandler.class);
            // 将该channel添加到群组
            group.add(ctx.channel());
            // 向组内其他成员发送 新成员加入消息
            group.writeAndFlush(new TextWebSocketFrame("新管道加入:"+ctx.channel()));
        }else {
            super.userEventTriggered(ctx,evt);
        }
        System.out.println(evt.getClass());
    }
}
