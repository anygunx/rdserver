package com.rd.net.message;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class MessageArray {

    /**
     * 是否打印日志
     */
    private boolean log = true;

    private ChannelBuffer buf = null;

    /**
     * 消息集合
     */
    private List<Message> cmdList;

    /**
     * 链接通道
     */
    private Channel channel;

    public MessageArray(Channel channel) {
        buf = ChannelBuffers.dynamicBuffer(ByteOrder.BIG_ENDIAN, 1024);
        cmdList = new ArrayList<Message>();
        this.channel = channel;
    }

    public MessageArray(Message msg) {
        buf = ChannelBuffers.dynamicBuffer(ByteOrder.BIG_ENDIAN, 1024);
        cmdList = new ArrayList<Message>();
        this.channel = msg.getChannel();
        cmdList.add(msg);
    }

    public List<Message> getCmdList() {
        return cmdList;
    }

    public void setCmdList(List<Message> cmdList) {
        this.cmdList = cmdList;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public ChannelBuffer getBuf() {
        return buf;
    }

    public void setBuf(ChannelBuffer buf) {
        this.buf = buf;
    }

    public void addMessage(Message msg) {
        if (msg != null)
            cmdList.add(msg);
    }

    public boolean isLog() {
        return log;
    }

    public void setLog(boolean log) {
        this.log = log;
    }

    public void pack() {
        int size = cmdList.size();
        buf.clear();
        buf.writeByte(size);
        for (int i = 0; i < size; i++) {
            Message msg = cmdList.get(i);
            msg.pack();
            buf.writeBytes(msg.toArray());
        }
    }
}
