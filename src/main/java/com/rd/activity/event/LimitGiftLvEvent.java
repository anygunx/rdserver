package com.rd.activity.event;

import com.rd.bean.player.PlayerActivity;
import com.rd.game.GameRole;
import com.rd.net.message.Message;

import java.util.List;

/**
 * 限制礼包
 *
 * @author U-Demon Created on 2017年3月14日 下午6:06:03
 * @version 1.0.0
 */
public class LimitGiftLvEvent implements IActivityEvent {

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
        PlayerActivity activityData = role.getActivityManager().getActivityData();
        List<Byte> buys = activityData.getLvGift();
        msg.setByte(buys.size());
        for (byte id : buys) {
            msg.setByte(id);
            msg.setShort(1);
        }
    }

    @Override
    public boolean isOpen(GameRole role) {
        return true;
    }

}
