package org.seefly.mynetty.netty.coder.http;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.*;

/**
 * @author liujianxin
 * @date 2019-04-18 14:34
 */
public class HttpPipelineInitializer extends ChannelInitializer<Channel> {
    private final boolean client;

    public HttpPipelineInitializer(boolean client) {
        this.client = client;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if(client){
            // 如果是客户端，则请求需要进行编码
            pipeline.addLast("encode", new HttpRequestEncoder());
            // 响应需要解码
            pipeline.addLast("decode",new HttpResponseDecoder());
        }else {
            // 如果是服务器，响应需要编码
            pipeline.addLast("encode",new HttpResponseEncoder());
            // 请求需要解码
            pipeline.addLast("decode",new HttpRequestDecoder());
        }
    }

    /**
     * 由于Http的请求和响应可能有许多部分组成，因此如果希望聚合他们看到完整的消息，可以如下
     * 由于消息分段需要被缓冲，知道可以转发一个完整的消息给下一个ChannelInboundHandler，所以这个操作有轻微
     * 开销
     */
    protected void initChannel1(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if(client){
            pipeline.addLast("codec", new HttpClientCodec());
        }else {
            pipeline.addLast("codec",new HttpServerCodec());
        }
    }
}
