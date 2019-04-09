package org.seefly.mynetty.netty.channel;

import java.nio.channels.Channel;

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

    public void lifecycle(){
        Channel channel = null;
    }
}
