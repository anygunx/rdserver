package com.rd.bean.chat;

import com.alibaba.fastjson.annotation.JSONField;
import com.rd.game.GameWorld;
import com.rd.net.message.Message;

/**
 * 简单聊天记录
 * Created by XingYun on 2017/5/5.
 */
public class ChatPrivate {
    /**
     * 发送方id
     **/
    private int from;
    /**
     * 接受方id
     **/
    private int to;
    /**
     * 创建时间
     **/
    private long ts;
    /**
     * 内容
     **/
    private String c;

    public ChatPrivate() {
    }

    public void init(int from, int to, long ts, String c) {
        this.from = from;
        this.to = to;
        this.ts = ts;
        this.c = c;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public void getMessage(Message message, int playerId) {
        //player.getChatMessage(message);
        //message.setString(content);

        message.setInt(from);
        message.setInt(to);
        // 按这个格式发送。。
        ChatPlayer chatPlayer = getChatPlayer(from != playerId ? from : to);
        chatPlayer.getChatMessage(message);
        message.setString(c);
    }

    @JSONField(serialize = false)
    private ChatPlayer getChatPlayer(int playerId) {
        return new ChatPlayer(GameWorld.getPtr().getGameRole(playerId).getPlayer());
    }
}
