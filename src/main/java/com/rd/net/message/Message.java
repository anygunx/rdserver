package com.rd.net.message;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import java.io.UnsupportedEncodingException;
import java.nio.ByteOrder;

public class Message {

    /**
     * 链接通道
     */
    private Channel channel;

    private ChannelBuffer buf = null;
    /**
     * 消息id
     */
    private short cmdId;
    /**
     * 消息长度
     */
    private int size;

    private Message() {
    }

    /**
     * 构建一个发送的消息，这个得手动设置channel
     *
     * @param cmdId
     */
    public Message(short cmdId) {
        buf = ChannelBuffers.dynamicBuffer(ByteOrder.BIG_ENDIAN, 1024);
        buf.writeInt(0);
        buf.writeShort(0);
        this.cmdId = cmdId;
    }

    /**
     * 构建一个发送的消息
     *
     * @param cmdId
     */
    public Message(short cmdId, Channel channel) {
        buf = ChannelBuffers.dynamicBuffer(ByteOrder.BIG_ENDIAN, 1024);
        buf.writeInt(0);
        buf.writeShort(0);
        this.cmdId = cmdId;
        this.channel = channel;
    }

    /**
     * 构建一个接收到的消息
     *
     * @param cmdId
     * @param buf
     * @return
     */
    public static Message buildReceiveMessage(Channel channel, short cmdId, ChannelBuffer buf) {
        Message msg = new Message();
        msg.setCmdId(cmdId);
        msg.setChannelBuf(buf);
        msg.setChannel(channel);
        return msg;
    }

    public void setCmdId(short id) {
        cmdId = id;
    }

    public short getCmdId() {
        return cmdId;
    }

    public ChannelBuffer getChannelBuffer() {
        return buf;
    }

    public void setChannelBuf(ChannelBuffer buf) {
        this.buf = buf;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void setBool(boolean value) {
        if (value) {
            buf.writeByte(1);
        } else {
            buf.writeByte(0);
        }
    }

    public boolean readBoolean() {
        byte value = buf.readByte();
        return value > 0 ? true : false;
    }

    public void setByte(int value) {
        buf.writeByte(value);
    }

    public void setByte(boolean value) {
        setByte(value ? 1 : 0);
    }

    public byte readByte() {
        return buf.readByte();
    }

    public void setShort(int value) {
        buf.writeShort(value);
    }

    public short readShort() {
        return buf.readShort();
    }

    public void setInt(int value) {
        buf.writeInt(value);
    }

    public int readInt() {
        return buf.readInt();
    }

    public void setLong(long value) {
        buf.writeDouble(value);
    }

    public long readLong() {
        return (long) buf.readDouble();
    }

    public void setString(String str) {
        if (str == null) {
            str = "";
        }
        try {
            byte[] bytes = str.getBytes("utf-8");
            if (bytes == null || bytes.length == 0) {
                buf.writeShort(0);
            } else {
                buf.writeShort(bytes.length);
                buf.writeBytes(bytes);
            }
        } catch (UnsupportedEncodingException e) {
            //Logger.error(LoggerSystem.NET, e, "The Character Encoding is not supported");
        }
    }

    public String readString() {
        int utflen = buf.readShort();
        if (utflen == -1) {
            return null;
        }
        byte[] chararr = new byte[utflen];
        buf.readBytes(chararr);
        String str = new String(chararr, 0, chararr.length);
        try {
            str = java.net.URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return str;
    }

//	float型数据用字符串代替
//	public void setFloat(float val) {
//		buf.writeFloat(val);
//	}

    public void pack() {
        buf.setInt(0, buf.writerIndex());
        buf.setShort(4, cmdId);
        size = buf.writerIndex();
    }

    public int getSize() {
        return size;
    }

    public byte[] getData() {
        return toArray();
    }

    public byte[] toArray() {
        byte[] array = buf.readBytes(size).array();
        buf.resetReaderIndex();
        return array;
    }

    public void writeBytes(ChannelBuffer buffer) {
        buf.writeBytes(buffer, 6, buffer.writerIndex() - 6);
    }

    public static void main(String[] args) {
        ChannelBuffer buf = ChannelBuffers.dynamicBuffer(
                ByteOrder.LITTLE_ENDIAN, 1024);
        for (int i = 0; i < 2048; i++) {
            buf.writeInt(i + 1);
        }
        for (int i = 0; i < 2048; i++) {
            int n = buf.readInt();
            System.out.println("第" + i + "次：" + n);
        }
    }

}
