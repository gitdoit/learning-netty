package org.seefly.mynetty.netty.channel;

/**
 * ChannelHandler定义的生命周期操作，在ChannelHandler被添加或从ChannelPipeline中移除时会调用
 * 其实也对，ChannelHandler脱离了ChannelPipeline也就没有了存在的价值，所以它的生命周期也就围绕着ChannelPipeline来展开
 *
 * handlerAdded     将ChannelHandler添加到ChannelPipeline时触发
 * handlerRemove    将ChannelHandler从ChannelPipeline移除时触发
 * exceptionCaught  当处理过程在ChannelPipeline中有错误时被调用
 *
 * @author liujianxin
 * @date 2019-04-09 09:41
 */
public class ChannelHandlerDemo {

}
