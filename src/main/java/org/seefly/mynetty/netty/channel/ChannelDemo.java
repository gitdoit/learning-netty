package org.seefly.mynetty.netty.channel;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.CharsetUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * Channel定义了一组和ChannelInboundHandler API密切相关的状态模型
 *
 * 生命周期
 * ChannelUnregistered  Channel已经被创建，但未注册到EventLoop
 * ChannelRegistered    Channel已经被注册到了EventLoop
 * ChannelActive        Channel处于活动状态(已经连接到它的远程节点)。他现在可以接收和发送数据了
 * ChannelInactive      Channel没有连接到远程节点
 *
 * ChannelRegistered -> ChannelActive -> ChannelUnregistered -> ChannelInactive
 * @author liujianxin
 * @date 2019-04-09 09:34
 */
public class ChannelDemo {

    /**
     * Channel是线程安全的，这意味着在多线程环境下
     * 使用Channel不会出现线程安全问题，唯一需要注意的是数据的发送顺序
     */
    public void threadSafe(){
        final Channel channel = null;
        ExecutorService executorService = Executors.newCachedThreadPool();
        // 线程安全的
        executorService.execute(() -> channel.writeAndFlush(Unpooled.copiedBuffer("ThreadSafe", CharsetUtil.UTF_8)));
        executorService.execute(() -> channel.writeAndFlush(Unpooled.copiedBuffer("ThreadSafe", CharsetUtil.UTF_8)));
    }
}
