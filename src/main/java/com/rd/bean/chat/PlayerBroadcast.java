package com.rd.bean.chat;

import com.rd.bean.player.Player;
import com.rd.common.ChatService;
import com.rd.define.EBroadcast;
import com.rd.net.message.Message;

/**
 * 玩家广播
 *
 * @author Created by U-Demon on 2016年11月8日 下午8:56:18
 * @version 1.0.0
 */
public class PlayerBroadcast extends BaseBroadcast {

    //广播多少次
    private int count;

    protected PlayerBroadcast(Message msg, int count) {
        super(msg);
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    @Override
    public boolean isNeedBroadcast(long currTime) {
        return count > 0;
    }

    @Override
    public void countDown(long currTime) {
        count--;
    }

    @Override
    public boolean isDone(long currTime) {
        return count <= 0;
    }

    public static final PlayerBroadcast build(Player player, EBroadcast type, String... contents) {
        Message message = ChatService.createBroadcastMsg(player, type, contents);
        return new PlayerBroadcast(message, 1);
    }

}
