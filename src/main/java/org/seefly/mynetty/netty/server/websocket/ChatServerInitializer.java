package org.seefly.mynetty.netty.server.websocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author liujianxin
 * @date 2019-04-19 16:26
 */
public class ChatServerInitializer extends ChannelInitializer<Channel> {
    private final ChannelGroup group;

    public ChatServerInitializer(ChannelGroup group) {
        this.group = group;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 将字节解码为HttpRequest HttpContent LastHttpContent以及编码为字节
        pipeline.addLast(new HttpServerCodec());
        // 写一个文件的内容
        pipeline.addLast(new ChunkedWriteHandler());
        // 将一个HttpMessage 和跟随它的多个HttpContent聚合为单个FullHttpRequest或FullHttpResponse
        // 安装了这个Handler后，它的下一个Handler将会收到一个完整的Http请求或响应
        pipeline.addLast(new HttpObjectAggregator(1024 * 1024));
        // 处理FullHttpRequest
        pipeline.addLast(new HttpRequestHandler("/ws"));
        // 处理websocket协议升级,当协议升级完成之后，他会将HttpRequestDecoder替换成WebsocketFrameDecoder
        // HttpResponseEncoder替换成WebsocketFrameEncoder,并移除所有不被websocket所使用的handler
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        // 处理websocket帧
        pipeline.addLast(new TextWebSocketFrameHandler(group));
    }
}
