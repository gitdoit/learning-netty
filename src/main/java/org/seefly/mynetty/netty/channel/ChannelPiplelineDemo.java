package org.seefly.mynetty.netty.channel;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;

import java.util.List;

/**
 * ChannelPipeline是一个拦截流经Channel的入站和出战事件的ChannelHandler链
 * 每创建一个新的Channel都会被分配一个新的ChannelPipeline，且Channel即不能附加另外一个ChannelPipeline，也不能从当前的
 * ChannelPipeline中分离；
 * 一个ChannelPipeline可以由多个inbound或outbound组成
 * 但是一个入站事件只会从左边(头部)开始流经所有的inbound，而跳过所有的outbound
 * 出站事件则只会从右边(尾部)开始流经所有的outbound
 *
 * **************ChannelPipeline******************
 * --> inbound ---------------------> inbound---->
 * <-------- outbound <-- outbound <--------------
 * ********************************************
 *
 * @author liujianxin
 * @date 2019-04-09 10:09
 */
public class ChannelPiplelineDemo {

    public void channelPipeline(){
        ChannelPipeline pipeline = null;
        // C A B
        pipeline.addFirst("A",null);
        pipeline.addLast("B",null);
        pipeline.addFirst("C",null);

        // 移除
        pipeline.remove("A");

        // 获取
        ChannelHandler a = pipeline.get("A");

        // 获取和A ChannelHandler绑定的ChannelHandlerContext
        ChannelHandlerContext a1 = pipeline.context("A");
        // 你懂的
        List<String> names = pipeline.names();
    }

    public void event(){
        ChannelPipeline pipeline = null;
        pipeline.fireChannelActive();
        pipeline.fireChannelRegistered();
        //。。。。
    }

}
