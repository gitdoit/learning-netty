package org.seefly.mynetty.netty.server.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;


/**
 * @author liujianxin
 * @date 2019-04-23 10:14
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        boolean keepAlive = HttpUtil.isKeepAlive(msg);
        if (!msg.decoderResult().isSuccess()) {
            sendError(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }
        // 只支持get方法
        if (msg.method() != HttpMethod.GET) {
            sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED);
        }
        // 拿到访问的文件或文件夹
        String uri = uriDecode(ctx, msg.uri());
        File file = new File(uri);
        // 检查合法性
        if(file.isHidden() || !file.exists()){
            sendError(ctx,HttpResponseStatus.NOT_FOUND);
            return;
        }
        // 如果是个文件夹就展示文件夹里面的内容
        if(file.isDirectory()){
            returnHtml(ctx,file);
            return;
        }
        // 不是文件夹就是文件啊
        if(!file.isFile()){
            sendError(ctx,HttpResponseStatus.FORBIDDEN);
            return;
        }
        // 把文件返回过去
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH,file.length());
        if(keepAlive){
            response.headers().set(HttpHeaderNames.CONNECTION,HttpHeaderValues.KEEP_ALIVE);
        }
        ctx.write(response);
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        ctx.write(new DefaultFileRegion(randomAccessFile.getChannel(),0,randomAccessFile.length()));
        if(!keepAlive){
            ctx.close();
        }
    }

    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus code) {
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, code);
        ctx.writeAndFlush(fullHttpResponse);
        ctx.close();
    }

    private String uriDecode(ChannelHandlerContext ctx, String uri) {
        try {
            uri = URLDecoder.decode(uri, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            try {
                uri = URLDecoder.decode(uri, StandardCharsets.ISO_8859_1.name());
            } catch (UnsupportedEncodingException e1) {
                System.out.println("URI编码错误！" + uri);
                sendError(ctx, HttpResponseStatus.BAD_REQUEST);
            }
        }
        uri = uri.replace('/', File.separatorChar);
        if (uri.contains('.' + File.separator) || uri.contains(File.separator + '.') || uri.startsWith(".") || uri.endsWith(".")) {
            sendError(ctx, HttpResponseStatus.BAD_REQUEST);
        }
        return System.getProperty("user.dir") + File.separator + uri;
    }

    private void returnHtml(ChannelHandlerContext ctx,File file){
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/html;charset=utf-8");
        File[] files = file.listFiles();
        StringBuilder sb = new StringBuilder("<html><head></head><body>");
        sb.append("<a href='..'>..</a>").append("<br/>");
        for(File f : files){
            sb.append("<a href='"+f.getAbsolutePath()+"'>"+f.getName()+"</a>").append("<br/>");
        }
        sb.append("</body></html>");
        ByteBuf byteBuf = Unpooled.copiedBuffer(sb.toString().getBytes(StandardCharsets.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH,byteBuf.readableBytes());
        response.content().writeBytes(byteBuf);
        ctx.writeAndFlush(response).addListeners(ChannelFutureListener.CLOSE_ON_FAILURE);
    }
}
