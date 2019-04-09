package org.seefly.mynetty.netty.channel;

import io.netty.channel.ChannelHandlerAdapter;

/**
 * ChannelHandlerAdapter提供了{@link ChannelHandlerAdapter#isSharable()}方法
 * 如果它返回true，表示这个ChannelHandler可以被多个ChannelPipeline共享使用
 * 所以使用@Sharable注解的ChannelHandler应该是线程安全的
 * @author liujianxin
 * @date 2019-04-09 10:06
 */
public class ChannelhandelrAdapterDemo {
}
