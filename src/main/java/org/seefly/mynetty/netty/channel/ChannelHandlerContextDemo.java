package org.seefly.mynetty.netty.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;

/**
 * ChannelHandlerContext代表了ChannelHandler和ChannelPipeline之间的绑定关系
 * 每当有一个ChannelHandler绑定到ChannelPipeline上之后都会创建一个ChannelHandler
 * 它主要功能是管理他所关联的ChannelHandler和在同一个ChannelPipeline中其他ChannelHandler之间的关系
 *
 * ChannelHandlerContext和Channel\ChannelPipeline有很多共同的方法
 * 区别在于，调用ChannelHandlerContext中的方法，他们将沿着当前该ChannelHandler所处的位置向下传播
 * 而另外俩则从头开始。
 * @author liujianxin
 * @date 2019-04-09 10:31
 */
public class ChannelHandlerContextDemo {

    public void usage(){
        ChannelHandlerContext ctx = null;
        // get Channel
        Channel channel = ctx.channel();
        // get ChannelPipeline
        ChannelPipeline pipeline = ctx.pipeline();
        // write
        ctx.write(null);

        //可以缓存一个ChannelHandlerContext以便可以不基于事件触发，而主动向外写出数据
    }


}
