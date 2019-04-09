package org.seefly.mynetty.netty.inaction.echo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 *
 * Sharable 标识这类的实例之间可以在 channel 里面共享
 * @author liujianxin
 * @date 2019-03-28 13:55
 */
@ChannelHandler.Sharable
public class EchoServerHandle extends ChannelInboundHandlerAdapter {

    /**
     * 每一个入站信息都会激活
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        System.out.println("Server received: " + in.toString(CharsetUtil.UTF_8));
        // 将所接收的消息返回给发送者。注意，这还没有冲刷数据
        ctx.write(in);
    }

    /**
     * 通知处理器最后的 channelread() 是当前批处理中的最后一条消息时调用
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        //冲刷所有待审消息到远程节点。关闭通道后，操作完成
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 覆盖 exceptionCaught 使我们能够应对任何 Throwable 的子类型。在这种情况下我们记录，并关闭所有可能处于未知状态的连接。
     * 它通常是难以 从连接错误中恢复，所以干脆关闭远程连接。
     * 当然，也有可能的情况是可以从错误中恢复的，所以可以用一个更复杂的措施来尝试识别和处理 这样的情况
     *
     * 如果异常没有被捕获，会发生什么？
     * 每个 Channel 都有一个关联的 ChannelPipeline，它代表了 ChannelHandlerDemo 实例的链。
     * 适配器处理的实现只是将一个处理方法调用转发到链中的下一个处理器。
     * 因此，如果一个 Netty 应用程序不覆盖exceptionCaught ，那么这些错误将最终到达 ChannelPipeline，并且结束警告将被记录。
     * 出于这个原因，你应该提供至少一个 实现 exceptionCaught 的 ChannelHandlerDemo。
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) {
        cause.printStackTrace();
        // 关闭通道
        ctx.close();
    }
}
