package org.seefly.mynetty.netty.transport;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.CharsetUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author liujianxin
 * @date 2019-04-08 11:35
 */
public class ChennelDemo {

    /**
     * Channel是线程安全的，这意味着在多线程环境下
     * 使用Channel不会出现线程安全问题，唯一需要注意的是数据的发送顺序
     */
    public void threadSafe(){
        final Channel channel = null;
        ExecutorService executorService = Executors.newCachedThreadPool();
        // 线程安全的
        executorService.execute(() -> channel.writeAndFlush(Unpooled.copiedBuffer("ThreadSafe", CharsetUtil.UTF_8)));
        executorService.execute(() -> channel.writeAndFlush(Unpooled.copiedBuffer("ThreadSafe", CharsetUtil.UTF_8)));
    }
}
