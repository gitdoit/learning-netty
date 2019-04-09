package org.seefly.mynetty.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ByteProcessor;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

/**
 * @author liujianxin
 * @date 2019-04-08 15:01
 */
public class ByteBufAPI {
    public void get(){
        ByteBuf byteBuf = null;
        for(int i = 0 ; i < byteBuf.capacity() ; i++){
            // getByte方法并不会改变 读/写索引
            System.out.println((char) byteBuf.getByte(i));
            // 手动设置读索引
            //byteBuf.readerIndex(5)
        }
    }

    /**
     * discardReadBytes用于丢弃已经读取过的字节
     * 这常用于内存非常紧张的时候，但是可能会导致频繁的内存复制
     */
    @Test
    public void discard(){
        ByteBuf byteBuf = Unpooled.copiedBuffer("1234".getBytes(StandardCharsets.UTF_8));
        System.out.println("字符串 1234 所用字节数:"+byteBuf.capacity());
        System.out.println("当前读索引"+byteBuf.readerIndex());
        byteBuf.readByte();
        System.out.println("调用readByte之后,读索引"+byteBuf.readerIndex());
        byteBuf.discardReadBytes();
        System.out.println("调用discardReadBytes后，读索引"+byteBuf.readerIndex());


        // 可以调用clear方法重置读/写索引而不对内容做任何变更
        byteBuf.clear();
    }

    @Test
    public void readAll(){
        ByteBuf byteBuf = Unpooled.copiedBuffer("1234".getBytes(StandardCharsets.UTF_8));
        while (byteBuf.isReadable()){
            System.out.println((char) byteBuf.readByte());
        }
    }

    @Test
    public void write(){
        ByteBuf byteBuf = Unpooled.buffer(32);
        while (byteBuf.writableBytes() >= 4){
            byteBuf.writeInt(1);
        }
        System.out.println("读索引"+byteBuf.readerIndex());
        System.out.println("写索引"+byteBuf.writerIndex());
    }

    @Test
    public void find(){
        // 0 1 2 4 \r
        byte[] bytes = new byte[]{48,49,50,51,13,10};
        ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
        // 查找指定字节的索引,注意返回值 false 代表当前字节为目标字节
        int k = byteBuf.forEachByte(b -> b != '\r');

        int i = byteBuf.forEachByte(ByteProcessor.FIND_CRLF);

        System.out.println(k);
    }

    /**
     * 派生缓冲区的创建是廉价的，因为它会使用和源缓冲区一样的存储空间
     * 不同的是派生缓冲区的读/写索引是自己维护的
     * 所以使用的时候要小心
     */
    @Test
    public void 派生缓冲区(){
        // 原缓冲区
        ByteBuf byteBuf = Unpooled.copiedBuffer("1234".getBytes(StandardCharsets.UTF_8));
        /**派生缓冲区*/

        // 派生整个
        byteBuf.duplicate();
        // 派生可读的那一部分
        byteBuf.slice();
        // 自己指定
        byteBuf.slice(0,byteBuf.capacity());
        // 派生一个只读的缓冲区
        byteBuf.asReadOnly();
        // 返回一个从读索引开始，以参数作为读取长度的派生
        // 并同时增加源缓冲区的读索引
        byteBuf.readSlice(2);

        // 这个完全复制
        byteBuf.copy();
    }

    /**
     * getXX/setXX方法不会对缓存索引做改变
     * 需要注意的是例如 getInt(int index)方法中的index参数
     * 不是代表第几个int类型的索引，而是代表从第几个byte开始拿4个字节 当作int
     */
    @Test
    public void getAndSet(){
        ByteBuf buffer = Unpooled.buffer(16);
        buffer.writeInt(1);
        buffer.writeInt(2);
        buffer.writeInt(3);
        buffer.writeInt(4);
        // 从指定索引处，读取一个int类型数据
        // 直观点就是从byte数组的指定索引开始  读取4个字节，产生一个int类型数据返回
        // get* 并不会改变缓存的任何索引
        int anInt = buffer.getInt(4);
        System.out.println("获取第2个int："+anInt);
        System.out.println("获取之后索引"+buffer.readerIndex());

        // 就是把 3 -> 66
        buffer.setInt(8,66);
        System.out.println(buffer.getInt(8));
    }

    @Test
    public void readAndWirte(){
        ByteBuf buffer = Unpooled.buffer(16);
        buffer.writeInt(1);
        buffer.writeBoolean(false);
        buffer.writeChar(48);

        System.out.println(buffer.readInt());
        System.out.println("readInt之后，读索引"+buffer.readerIndex());

        System.out.println(buffer.readBoolean());
        System.out.println("readBoolean之后，读索引"+buffer.readerIndex());
    }


}
