package org.seefly.mynetty.netty.coder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * 将两种消息格式进行转换
 * @author liujianxin
 * @date 2019-04-17 18:54
 */
public class MessageToMessageDecoderDemo {

    /**
     * Integer to String
     */
    private static class IntgerToStringDecoder extends MessageToMessageDecoder<Integer> {

        @Override
        protected void decode(ChannelHandlerContext ctx, Integer msg, List<Object> out) throws Exception {
            out.add(String.valueOf(msg));
            System.out.println("String");
        }




    }

    /**
     * 思路
     *  先从原始的byte数组转到int，再从int转到String
     *
     *  需要注意的是MessageToMessage是有编码解码两种对应的类的
     *  不要用错了，一个用于出站，一个用于入站
     */
    public static void main(String[] args) {
        ByteTomessageDemo.ToIntegerDecoder ind = new ByteTomessageDemo.ToIntegerDecoder();
        IntgerToStringDecoder isd = new IntgerToStringDecoder();
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(ind,isd);
        ByteBuf source = Unpooled.buffer();
        for(int i = 0 ; i < 10 ; i++){
            source.writeInt(i);
        }
        embeddedChannel.writeInbound(source);
        assert embeddedChannel.finish();
        for(int i = 0 ; i < 10 ; i++){
            String k = embeddedChannel.readInbound();
            System.out.println(k);
        }
        assert embeddedChannel.readInbound() == null;
        embeddedChannel.close();

    }

}
