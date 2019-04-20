package org.seefly.mynetty.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * @author liujianxin
 * @date 2019-04-19 14:45
 */
public class UsefulHandlerDemo {

    private static  class  IdleStateHandlerInitializer extends ChannelInitializer<Channel>{
        @Override
        protected void initChannel(Channel ch)  {
            ChannelPipeline pipeline = ch.pipeline();
            // 一个检测连接闲置的handler，在指定的时间内没有活动的话将会产生一个 闲置事件
            // 我们可以在后面的handler中重写userEventTriggered方法来接收这玩意
            pipeline.addLast(new IdleStateHandler(0,0,60, TimeUnit.SECONDS));
            // 超时触发一个事件，然后发一个心跳，如果没响应就断开
            pipeline.addLast(new HearbeatHandler());
        }
    }
    private static class HearbeatHandler extends ChannelInboundHandlerAdapter{
        private static ByteBuf BEAT = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("HEARBEAT".getBytes(StandardCharsets.UTF_8)));
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if(evt instanceof IdleStateEvent){
                ctx.writeAndFlush(BEAT.duplicate()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }

        }

    }
}
