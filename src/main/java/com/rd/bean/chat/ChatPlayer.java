package com.rd.bean.chat;

import com.rd.bean.gang.Gang;
import com.rd.bean.gang.GangMember;
import com.rd.bean.player.Player;
import com.rd.bean.player.SimplePlayer;
import com.rd.net.message.Message;

/**
 * 聊天对象
 *
 * @author Created by U-Demon on 2016年11月8日 下午1:48:02
 * @version 1.0.0
 */
public class ChatPlayer extends SimplePlayer {

    private int gangId = 0;
    private byte gangPos = 0;

    public ChatPlayer(Player player) {
        this.init(player);
        // 先不修改其他地方的simplePlayer
        setHead(player.getHead());

        Gang gang = player.getGang();
        if (gang != null) {
            this.gangId = gang.getId();
            GangMember member = gang.getGangMember(this.id);
            if (member != null)
                this.gangPos = member.getPosition();
        }
    }

    public void getChatMessage(Message message) {
        getSimpleMessageNew(message);
    }


    public void getBroadcastMessage(Message message) {
        message.setInt(id);
        message.setString(name);
        message.setInt(vip);
        message.setByte(head);
    }

    public int getPlayerId() {
        return id;
    }

    public int getGangId() {
        return gangId;
    }

    public byte getGangPos() {
        return gangPos;
    }

}
