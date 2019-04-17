package org.seefly.mynetty.netty.coder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 *
 *
 * @author liujianxin
 * @date 2019-04-10 11:11
 */
public class ByteTomessageDemo {


    /**
     * int数据解码
     */
    public  static class ToIntegerDecoder extends ByteToMessageDecoder{
        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            while (in.readableBytes() >= 4){
                out.add(in.readInt());
                System.out.println("int");
            }
        }

        /**
         * 这个方法将在最后一次被调用，默认实现就是直接调一下decode
         * 一般用来清场用的，我觉得还可以用来给应用发一个指令啥的，告诉应用层连接关闭了
         * 不过这好像是脱了裤子放屁，多此一举啊
         */
        @Override
        protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            super.decodeLast(ctx, in, out);
            System.out.println("最后一次调用");
        }
    }

    /**
     * 这个在调用的时候没有判断是否有足够的数据，因为ReplayingDecoder调用
     * 这个方法的时候包装了一个try，所以这个性能没有上面的那个好。不过这个用起来简单
     */
    private static class ToIntegerDecoder2 extends ReplayingDecoder<ByteBuf>{
        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            out.add(in.readInt());
        }


    }

    public static void main(String[] args) {
        EmbeddedChannel channel = new EmbeddedChannel(new LineBasedFrameDecoder(30));
        ByteBuf source = Unpooled.buffer();
        source.writeBytes("nihao\r\nwoshinidaye\r\n".getBytes());
        channel.writeInbound(source);
        assert channel.finish();
        ByteBuf buf = channel.readInbound();
        System.out.println(new String(buf.array()));
        System.out.println(channel.readInbound().toString());
    }

}
