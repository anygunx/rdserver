package com.rd.activity.event;

import com.rd.bean.player.PlayerActivity;
import com.rd.game.GameRole;
import com.rd.net.message.Message;

/**
 * 摇钱树
 *
 * @author U-Demon Created on 2017年3月30日 下午5:06:16
 * @version 1.0.0
 */
public class GoldTreeEvent implements IActivityEvent {

    @Override
    public boolean onStart() {
        return true;
    }

    @Override
    public boolean onEnd() {
        return true;
    }

    @Override
    public void getMessage(Message msg, GameRole role) {
        PlayerActivity data = role.getActivityManager().getActivityData();
        msg.setShort(data.getGoldTreeNum());
        msg.setByte(data.getGoldTreeReward().size());
        for (byte id : data.getGoldTreeReward()) {
            msg.setByte(id);
        }
    }

    @Override
    public boolean isOpen(GameRole role) {
        return true;
    }

}
