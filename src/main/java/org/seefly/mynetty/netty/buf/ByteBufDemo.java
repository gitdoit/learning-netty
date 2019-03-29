package org.seefly.mynetty.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.ByteBuffer;

/**
 *    网络数据的基本单位永远是 byte(字节)。Java NIO 提供 ByteBuffer 作为字节的容器，但这个类是过于复杂，有点 难以使用。
 * Netty 中 ByteBuffer 替代是 ByteBuf，一个强大的实现，解决 JDK 的 API 的限制，以及为网络应用程序开发者一个更好的工具。
 * 但 ByteBuf 并不仅仅暴露操作一个字节序列的方法;这也是专门的Netty 的 ChannelPipeline 的语义设计。
 *
 *
 *
 *
 *     写入数据到 ByteBuf 后，writerIndex（写入索引）增加。开始读字节后，readerIndex（读取索引）增加。
 * 你可以读取字节，直到写入索引和读取索引处在相同的位置，ByteBuf 变为不可读。当访问数据超过数组的最后位，则会抛出 IndexOutOfBoundsException。
 * 调用 ByteBuf 的 "read" 或 "write" 开头的任何方法都会提升 相应的索引。另一方面，"set" 、 "get"操作字节将不会移动索引位置；
 * 他们只会操作相关的通过参数传入方法的相对索引。
 *
 * 可以给ByteBuf指定一个最大容量值，这个值限制着ByteBuf的容量。任何尝试将写入索引超过这个值的行为都将导致抛出异常。ByteBuf 的默认最大容量限制是 Integer.MAX_VALUE。
 * @author liujianxin
 * @date 2019-03-28 22:08
 */
public class ByteBufDemo {

    /**
     * 最常用的模式是 ByteBuf 将数据存储在 JVM 的堆空间，这是通过将数据存储在数组的实现。
     * 堆缓冲区可以快速分配，当不使用时也可以快速释放。它还提供了直接访问数组的方法，通过 ByteBuf.array() 来获取 byte[]数据
     */
    public void heapBuf(){
        ByteBuf byteBuf = Unpooled.copiedBuffer("nihao".getBytes());
        if(byteBuf.hasArray()){
            // 获取被包装的字节数组
            byte[] array = byteBuf.array();
            // 第一个可读的字节偏移量
            int offset = byteBuf.arrayOffset()+byteBuf.readerIndex();
            // 可读的字节数量
            int length = byteBuf.readableBytes();
            // handle(array,offset,length)
        }
    }

    /**
     * “直接缓冲区”是另一个 ByteBuf 模式。对象的所有内存分配发生在 堆，对不对？好吧，并非总是如此。
     *   在 JDK1.4 中被引入 NIO 的ByteBuffer 类允许 JVM 通过本地方法调用分配内存，其目的是
     *       通过免去中间交换的内存拷贝, 提升IO处理速度; 直接缓冲区的内容可以驻留在垃圾回收扫描的堆区以外。
     *       DirectBuffer 在 -XX:MaxDirectMemorySize=xxM大小限制下, 使用 Heap 之外的内存, GC对此”无能为力”,也就意味着规避了在高负载下频繁的GC过程对应用线程的中断影响.
     *       (详见http://docs.oracle.com/javase/7/docs/api/java/nio/ByteBuffer.html.)
     *
     * 这就解释了为什么“直接缓冲区”对于那些通过 socket 实现数据传输的应用来说，是一种非常理想的方式。
     * 如果你的数据是存放在堆中分配的缓冲区，那么实际上，在通过 socket 发送数据之前，JVM 需要将先数据复制到直接缓冲区。
     * 但是直接缓冲区的缺点是在内存空间的分配和释放上比堆缓冲区更复杂，另外一个缺点是如果要将数据传递给遗留代码处理，因为数据不是在堆上
     */
    public void directBuf(){
        ByteBuf directBuf = Unpooled.directBuffer();
        // 检查是否由数组支持，若不是，则为直接缓存
        if (!directBuf.hasArray()){
            // 获取可读字节数
            int length = directBuf.readableBytes();
            // 创建字节数组
            byte[] bytes = new byte[length];
            // 直接缓存复制到 字节数组
            directBuf.getBytes(directBuf.readableBytes(),bytes);
            // handle(array,0,length)
        }
    }

    /**
     * 复合缓冲区
     * 我们可以创建多个不同的 ByteBuf，然后提供一个这些 ByteBuf 组合的视图。
     * 复合缓冲区就像一个列表，我们可以动态的添加和删除其中的 ByteBuf，JDK 的 ByteBuffer 没有这样的功能。
     *
     * CompositeByteBuf 不允许访问其内部可能存在的依赖数组(backing byte array)，也不允许直接访问数据，这一点类似于直接缓冲区模式
     */
    public void compositeBuf(){

        /**ByteBuffer的做法*/
        ByteBuffer header = ByteBuffer.allocate(1);
        ByteBuffer body = ByteBuffer.allocate(1);
        // 使用数组保存消息的各个部分
        ByteBuffer[] message = { header, body };
        // 使用副本来合并这两个部分
        ByteBuffer message2 = ByteBuffer.allocate(header.remaining() + body.remaining());
        message2.put(header);
        message2.put(body);
        message2.flip();

        /**ByteBuf的做法*/
        CompositeByteBuf messageBuf = Unpooled.compositeBuffer();
        ByteBuf headerBuf = null;
        ByteBuf bodyBuf = null;
        messageBuf.addComponents(headerBuf, bodyBuf);
        // ....
        messageBuf.removeComponent(0);
        for (int i = 0; i < messageBuf.numComponents(); i++) {
            System.out.println(messageBuf.component(i).toString());
        }
        /**CompositeByteBuf如何访问其内数据*/
        int length = messageBuf.readableBytes();
        byte[] array = new byte[length];
        messageBuf.getBytes(messageBuf.readerIndex(), array);
        //handleArray(array, 0, length);

    }


}
