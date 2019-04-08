package org.seefly.mynetty.netty.inaction.echo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;

/**
 * @author liujianxin
 * @date 2019-03-28 14:24
 */
@ChannelHandler.Sharable
public class EchoClientHandle extends SimpleChannelInboundHandler<ByteBuf> {

    /**
     * 服务器的连接被建立后调用
     *
     * 建立连接后该 channelActive() 方法被调用一次。逻辑很简单：一旦建立了连接，字节序列被发送到服务器。
     * 该消息的内容并不重要;在这里，我们使用了 Netty 编码字符串 “Netty rocks!” 通过覆盖这种方法，我们确保东西被尽快写入到服务器。
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ChannelFuture channelFuture = ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));
        // ChannelFutureListener 用于监听操作完成后的回调
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess()){
                    System.out.println("success send msg!");
                }else {
                    future.cause().printStackTrace();
                }
            }
        });
    }

    /**
     * 该方法会在接收到数据时被调用。注意，由服务器所发送的消息可以以块的形式被接收。
     * 即，当服务器发送 5 个字节是不保证所有的 5 个字节会立刻收到的
     * 即使是只有 5 个字节，channelRead0() 方法可被调用两次，第一次用一个ByteBuf（Netty的字节容器）装载3个字节和第二次一个 ByteBuf 装载 2 个字节。
     * 唯一要保证的是，该字节将按照它们发送的顺序分别被接收。 （注意，这是真实的，只有面向流的协议如TCP）
     *
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        System.out.println("Client received: " + msg.toString(CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
