package com.rd.bean.chat;

import com.rd.net.message.Message;

/**
 * 聊天信息
 *
 * @author Created by U-Demon on 2016年11月8日 下午1:55:02
 * @version 1.0.0
 */
public class Chat {

    //聊天对象
    private ChatPlayer player;

    //聊天内容
    private String content;

    private final long createTime;

    public Chat(ChatPlayer player, String content, long createTime) {
        this.player = player;
        this.content = content;
        this.createTime = createTime;
    }

    public void getMessage(Message message) {
        player.getChatMessage(message);
        message.setString(content);
        message.setLong(createTime);


    }

    public ChatPlayer getPlayer() {
        return player;
    }

    public String getContent() {
        return content;
    }

    public long getCreateTime() {
        return createTime;
    }

}
