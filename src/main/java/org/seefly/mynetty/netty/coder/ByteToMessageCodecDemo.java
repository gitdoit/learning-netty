package org.seefly.mynetty.netty.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

/**
 * 既可以处理出站数据又可以处理入站数据
 * 这个我觉得是放在执行链的首位的
 * 这样入站数据可以从byte->pojo
 * 出站数据pojo->byte
 * @author liujianxin
 * @date 2019-04-18 11:28
 */
public class ByteToMessageCodecDemo {
    public static void main(String[] args) {
        ByteToMessageCodec byteToMessageCodec = new ByteToMessageCodec<String>() {
            @Override
            protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {

            }

            @Override
            protected void decode(ChannelHandlerContext ctx, ByteBuf in, List out) throws Exception {

            }
        };
    }
}
