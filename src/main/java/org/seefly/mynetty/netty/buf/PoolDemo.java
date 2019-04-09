package org.seefly.mynetty.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.junit.Test;

/**
 * 为了降低分配和释放内存的开销，Netty实现了缓存的池化
 * 它可以用来分配任意类型的ByteBuf实例。
 * 可以通过Channel或者绑定到ChannelHandler的ChannelHandlerContext获取到
 * 一个ByteBufAllocator的引用
 *
 * Netty提供了两种ByteBufAllocator的实现
 * PooledByteBufAllocator
 *      池化了ByteBuf的实例以提高性能并最大限度减少内存碎片
 * UnPooledByteBufAllocator
 *      不池化ByteBuf的实例，每次调用都返回一个新的ByteBuf
 * @author liujianxin
 * @date 2019-04-08 16:49
 */
public class PoolDemo {

    public void testGet(){
        Channel channel = null;
        ByteBufAllocator alloc = channel.alloc();
        alloc.heapBuffer();
        alloc.directBuffer();
        alloc.compositeBuffer();

        ChannelHandlerContext channelHandlerContext = null;
        channelHandlerContext.alloc();
        // ...
    }

    @Test
    public void unPooled(){
        Unpooled.buffer();
        Unpooled.directBuffer();
        Unpooled.copiedBuffer(new byte[]{});

        ByteBufUtil.getBytes(Unpooled.buffer());
    }

    @Test
    public void reference(){
        ByteBuf buffer = Unpooled.buffer();
        assert buffer.refCnt() == 1;
        // 将引用计数器 -1
        buffer.release();
        System.out.println(buffer.refCnt());
    }



}
