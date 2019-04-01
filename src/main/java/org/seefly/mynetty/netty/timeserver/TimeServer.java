package org.seefly.mynetty.netty.timeserver;

import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * @author liujianxin
 * @date 2019-04-01 13:18
 */
public class TimeServer {
    public static void main(String[] args) {
        new Thread(new MultiplexerTimeServer(8899),"NIO_MultiplexerTimeServer").start();
    }

    private static class MultiplexerTimeServer implements Runnable{
        private Selector selector;
        private ServerSocketChannel serverSocketChannel;
        private volatile boolean stop;

        public MultiplexerTimeServer(int port){
            try {
                // 创建Reactor线程
                selector = Selector.open();
                // 打开ServerSocketChannel用于监听客户端的连接，他是所有客户端连接的父管道
                serverSocketChannel = ServerSocketChannel.open();
                // 连接设置为非阻塞模式，绑定监听端口
                serverSocketChannel.configureBlocking(false);
                serverSocketChannel.socket().bind(new InetSocketAddress(port),1024);
                // 将ServerSocketChannel注册到Reactor线程的多路复用器Selector上，监听ACCEPT事件
                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
                System.out.println("The time server is start in port:"+port);

            }catch (IOException ex){
                ex.printStackTrace();
            }
        }
        public void stop(){
            this.stop = true;
        }

        @Override
        public void run() {
            while (!stop){
                try {
                    // 1s/次轮询
                    selector.select(1000);
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> it = selectionKeys.iterator();
                    SelectionKey key = null;
                    while (it.hasNext()){
                        key = it.next();
                        it.remove();
                        try {
                            handleInput(key);
                        }catch (Exception e){
                            if(key != null){
                                key.cancel();
                                if(key.channel() != null){
                                    key.channel().close();
                                }
                            }
                        }
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            // 多虑复用器关闭后，所有注册在上面的Channel和Pipe等资源都会被自动去注册并关闭，所以不需要重复释放资源
            if(selector != null){
                try{
                    selector.close();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }

        private void handleInput(SelectionKey key) throws IOException {
            if(key.isValid()){
                // 处理接入请求
                if(key.isAcceptable()){
                    ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    sc.register(selector,SelectionKey.OP_READ);
                }
                // 读取数据
                if(key.isReadable()){
                    SocketChannel sc = (SocketChannel)key.channel();
                    ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                    int readBytes = sc.read(readBuffer);
                    if(readBytes > 0){
                        readBuffer.flip();
                        byte[] bytes = new byte[readBuffer.remaining()];
                        readBuffer.get(bytes);
                        String body = new String(bytes, StandardCharsets.UTF_8);
                        System.out.println("The time server receive order:"+body);
                        String currentTime = "QUERY TIME ORDER".equals(body)?new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
                        doWrite(sc,currentTime);
                    }else if(readBytes < 0){
                        key.cancel();
                        sc.close();
                    }
                }
            }
        }

        private void doWrite(SocketChannel channel,String response) throws IOException {
            if(!StringUtils.isEmpty(response)){
                byte[] bytes = response.getBytes();
                ByteBuffer allocate = ByteBuffer.allocate(bytes.length);
                allocate.put(bytes);
                allocate.flip();
                channel.write(allocate);
            }

        }
    }
}


