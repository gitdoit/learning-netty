package org.seefly.mynetty.netty.buf;

import io.netty.buffer.ByteBuf;

/**
 *
 * 写入数据到 ByteBuf 后，writerIndex（写入索引）增加。开始读字节后，readerIndex（读取索引）增加。
 * 你可以读取字节，直到写入索引和读取索引处在相同的位置，ByteBuf 变为不可读。当访问数据超过数组的最后位，则会抛出 IndexOutOfBoundsException。
 * 调用 ByteBuf 的 "read" 或 "write" 开头的任何方法都会提升 相应的索引。另一方面，"set" 、 "get"操作字节将不会移动索引位置；
 * 他们只会操作相关的通过参数传入方法的相对索引。
 *
 * 可以给ByteBuf指定一个最大容量值，这个值限制着ByteBuf的容量。任何尝试将写入索引超过这个值的行为都将导致抛出异常。ByteBuf 的默认最大容量限制是 Integer.MAX_VALUE。
 * @author liujianxin
 * @date 2019-03-28 22:08
 */
public class ByteBufDemo {

    public void demo1(){
    }
}
