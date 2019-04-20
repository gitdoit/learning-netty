package org.seefly.mynetty.netty.websocket;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * 这个类处理http部分
 * 访问任何非/wsUrl路径都会相应一个html页面
 * 访问/wsUrl不处理，给后面的Handler处理
 * @author liujianxin
 * @date 2019-04-19 15:34
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final String wsUrl;
    private static final File INDEX;
    static {
        URL location = HttpRequestHandler.class.getProtectionDomain().getCodeSource().getLocation();
        try {
            String path = location.toURI()+ "index.html";
            path = !path.contains("file:") ? path : path.substring(5);
            INDEX = new File(path);
        }catch (URISyntaxException ex){
            throw new IllegalArgumentException("无法定位 index.html！");
        }
    }

    public HttpRequestHandler(String wsUrl) {
        this.wsUrl = wsUrl;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        // 如果请求的是Websocket端口，则不处理
        if(wsUrl.equalsIgnoreCase(request.uri())){
            ctx.fireChannelRead(request.retain());
        }else {
            if (HttpUtil.is100ContinueExpected(request)){
                send100Continue(ctx);
            }
            RandomAccessFile randomAccessFile = new RandomAccessFile(INDEX, "r");
            // 这里注意用的是HttpResponse 不是 FullHttpResponse
            // 如果使用FullHttpResponse的话，你一旦向外写出这个响应，那么本次响应就算完结了，不能再写东西了
            HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK);
            // 设置响应头
            response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/html;charset=UTF-8");
            boolean keepAlive = HttpUtil.isKeepAlive(request);
            if(keepAlive){
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
                                .set(HttpHeaderNames.CONTENT_LENGTH,randomAccessFile.length());
            }
            ctx.write(response);
            if(ctx.pipeline().get(SslHandler.class) == null){
                ctx.write(new DefaultFileRegion(randomAccessFile.getChannel(),0,randomAccessFile.length()));
            }else {
                ctx.write(new ChunkedNioFile(randomAccessFile.getChannel()));
            }
            ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT).addListener((ChannelFuture future) -> {
                if(!keepAlive){
                    future.channel().close();
                }
            });
        }
    }

    private static void send100Continue(ChannelHandlerContext channelHandlerContext){
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.CONTINUE);
        channelHandlerContext.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
