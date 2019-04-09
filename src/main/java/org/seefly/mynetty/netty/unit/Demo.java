package org.seefly.mynetty.netty.unit;

import io.netty.channel.embedded.EmbeddedChannel;

/**
 * {@link EmbeddedChannel}嵌入式Channel测试类
 * 它用于对ChannelHandler进行方便的单元测试
 * 它的几种方法可以方便的模拟出站、入站的数据事件，而不用真正的去开个服务器和客户端模拟
 *
 * {@link EmbeddedChannel#writeInbound(java.lang.Object...)} 模拟入站数据
 * {@link EmbeddedChannel#readInbound()} 从EmbeddedChannel 中读取一个入站消息，任何返回的东西都穿越了整个ChannelPipeline，若无则返回null
 *
 * {@link EmbeddedChannel#writeOutbound(java.lang.Object...)} 模拟出站数据
 * {@link EmbeddedChannel#readOutbound()} 从EmbeddedChannel 中读取一个出站消息，任何返回的东西都穿越了整个ChannelPipeline，若无则返回null
 *
 * {@link EmbeddedChannel#finish()} 标记EmbeddedChannel为已完成，并且如果有可被读取的入站/出站数据则返回true。他还会调用close方法
 *
 *
 * @author liujianxin
 * @date 2019-04-09 17:06
 */
public class Demo {


}
