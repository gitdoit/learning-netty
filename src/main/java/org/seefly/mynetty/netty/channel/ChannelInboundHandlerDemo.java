package org.seefly.mynetty.netty.channel;

/**
 * channelUnregistered  Channel从它的EventLoop注销，无法处理任何I/O时被调用
 * channelRegistered    Channel注册到EventLoop并且能够处理I/O时被调用
 * channelActive        Channel处于活动状态(已经连接到它的远程节点)。
 * channelInactive      Channel离开活动状态并且不再连接它的远程节点时被调用
 * channelReadComplete  当Channel上的一个读操作完成时被调用(所有可读的字节都已经从Channel中读取之后)
 * channelRead          当从Channel读取数据时被调用
 * ChannelWritabilityChanged 当Channel的可写状态发生改变时被调用
 * userEventTriggered 当ChannelInboundHandler.fireUserEventTriggered方法被调用时被调用
 *
 *
 * 当某一个ChannelInboundHandler重写了channelRead方法时，他将负责显式的释放与池化的ByteBuf实例相关的内存
 * @author liujianxin
 * @date 2019-04-09 09:51
 */
public class ChannelInboundHandlerDemo {

}
