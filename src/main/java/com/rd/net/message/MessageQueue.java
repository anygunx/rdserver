package com.rd.net.message;

import com.rd.game.GameWorld;
import org.jboss.netty.channel.Channel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageQueue {

    private int capbility = 100;

    /**
     * 消息缓冲队列
     */
    private BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();

    public MessageQueue() {

    }

    public void put(Message msg) {
        //缓冲池有一个固定容量，如果超过这个容量则舍弃前面的消息
        if (queue.size() >= capbility) {
            queue.poll();
        }

        queue.add(msg);
    }

    public void clear() {
        queue.clear();
    }

    /**
     * 发送缓存中的消息
     *
     * @param channel
     */
    public void sendBufferMessages(Channel channel) {
        int size = queue.size();
        MessageArray msgs = new MessageArray(channel);
        msgs.setLog(true);
        for (int i = 0; i < size; i++) {
            Message msg = queue.poll();
            msgs.addMessage(msg);
        }

        GameWorld.getPtr().sendMessage(msgs.getChannel(), msgs.getBuf());
    }

    public void addBufferdMessages(MessageArray msgs) {
        int size = queue.size();
        for (int i = 0; i < size; i++) {
            Message msg = queue.poll();
            msgs.addMessage(msg);
        }
    }
}
