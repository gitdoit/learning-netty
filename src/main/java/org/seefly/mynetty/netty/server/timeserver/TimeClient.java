package org.seefly.mynetty.netty.server.timeserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * @author liujianxin
 * @date 2019-04-01 13:55
 */
public class TimeClient {


    public static void main(String[] args) {
        new Thread(new TimeClientHandle("127.0.0.1",8899),"Time_server_client").start();
    }

    private static class TimeClientHandle implements Runnable {
        private String host;
        private int port;
        private Selector selector;
        private SocketChannel socketChannel;
        private volatile boolean stop;

        public TimeClientHandle(String host,int port){
            this.host = host;
            this.port = port;
            try {
                selector = Selector.open();
                socketChannel = SocketChannel.open();
                socketChannel.configureBlocking(false);
            }catch (Exception ex){
                ex.printStackTrace();
            }

        }

        @Override
        public void run() {
            try {
                // 建立连接
                if(socketChannel.connect(new InetSocketAddress(host,port))){
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    byte[] bytes = "QUERY TIME ORDER".getBytes();
                    ByteBuffer request = ByteBuffer.allocate(bytes.length);
                    request.put(bytes);
                    request.flip();
                    socketChannel.write(request);
                    if(!request.hasRemaining()){
                        System.out.println("send message to server success");
                    }
                }else {
                    socketChannel.register(selector,SelectionKey.OP_CONNECT);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            while (!stop){
                try{
                    selector.select(1000);
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    SelectionKey  key = null;
                    while (iterator.hasNext()){
                        key = iterator.next();
                        iterator.remove();
                        try{
                            if(key.isValid()){
                                SocketChannel sc = (SocketChannel) key.channel();
                                if(key.isConnectable()){
                                    if(sc.finishConnect()){
                                        sc.register(selector,SelectionKey.OP_READ);
                                    }else {
                                        System.exit(1);
                                    }
                                }
                                if(key.isReadable()){
                                    ByteBuffer allocate = ByteBuffer.allocate(1024);
                                    int read = sc.read(allocate);
                                    if(read > 0){
                                        allocate.flip();
                                        byte[] response = new byte[allocate.remaining()];
                                        allocate.get(response);
                                        String body = new String(response, StandardCharsets.UTF_8);
                                        System.out.println("Now is :"+body);
                                        this.stop = true;
                                    }else if(read < 0){
                                        key.channel();
                                        sc.close();
                                    }
                                }
                            }
                        }catch (Exception ex){
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
            if(selector != null){
                try {
                    selector.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
