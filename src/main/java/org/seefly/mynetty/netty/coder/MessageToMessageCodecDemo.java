package org.seefly.mynetty.netty.coder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

/**
 * 也是可以同时处理出站和入站数据的编解码工作
 * 但是不能用在执行连首位，因为不是针对byte的
 *
 * 但是这个类优缺点就是没有复用性，编解码工作耦合到一个类中
 * @author liujianxin
 * @date 2019-04-18 11:42
 */
public class MessageToMessageCodecDemo {
    public static void main(String[] args) {
        MessageToMessageCodec codec = new MessageToMessageCodec<String,Integer>() {
            @Override
            protected void encode(ChannelHandlerContext ctx, Integer msg, List out) throws Exception {

            }

            @Override
            protected void decode(ChannelHandlerContext ctx, String msg, List out) throws Exception {

            }
        };
    }
}
