package org.seefly.mynetty.netty.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 编码器
 * @author liujianxin
 * @date 2019-04-17 19:29
 */
public class MessageToByteEncoderDemo {

    private static class ShortToByteEncoder extends MessageToByteEncoder<Short>{

        @Override
        protected void encode(ChannelHandlerContext ctx, Short msg, ByteBuf out) throws Exception {
            out.writeShort(msg);
        }
    }
}
