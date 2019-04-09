package org.seefly.mynetty.netty.unit;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * @author liujianxin
 * @date 2019-04-09 17:15
 */
public class FixedLengthFrameDecoder extends ByteToMessageDecoder {
    /**
     * 一个定长帧解码器
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        while (in.readableBytes() >= 3){
            ByteBuf byteBuf = in.readBytes(3);
            out.add(byteBuf);
        }
    }

    @Test
    public void testDecoded(){
        ByteBuf source = Unpooled.buffer();
        for(int i = 0; i < 9 ; i++){
            source.writeByte(i);
        }
        // 写入站数据，一个长度为9的数据，被定长3解码器解码，可以得到3个数据段
        ByteBuf duplicate = source.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder());
        channel.writeInbound(duplicate.retain());
        assertTrue(channel.finish());

        // 读取入站数据
        ByteBuf read = channel.readInbound();
        // 判断是否和source的0~2 的数据相等
        assertEquals(source.readSlice(3),read);
        read.release();

        // 读取第二段
        read = channel.readInbound();
        assertEquals(source.readSlice(3),read);
        read.release();

        // 读取第三段
        read = channel.readInbound();
        assertEquals(source.readSlice(3),read);
        read.release();
    }
}
