package org.seefly.mynetty.netty.unit;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.junit.Test;

import java.util.List;

/**
 * 该类的作用是将所有的出站整形数据转换为它的绝对值
 * @author liujianxin
 * @date 2019-04-09 18:27
 */
public class AbsIntegerEncoder extends MessageToMessageEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        while (msg.readableBytes() >= 4){
            int i = msg.readInt();
            out.add(Math.abs(i));
          }
    }

    @Test
    public void test(){
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(new AbsIntegerEncoder());
        ByteBuf source = Unpooled.buffer();
        for(int i = 0 ; i < 10 ; i++){
            source.writeInt(i* -1);
        }
        embeddedChannel.writeOutbound(source);
         assert embeddedChannel.finish();
        for(int i = 0 ; i < 10 ; i++){
            int k = embeddedChannel.readOutbound();
            assert i == k;
        }
        assert embeddedChannel.readOutbound() == null;
    }
}
