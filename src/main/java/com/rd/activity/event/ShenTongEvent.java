package com.rd.activity.event;

import com.rd.bean.player.PlayerActivity;
import com.rd.game.GameRole;
import com.rd.net.message.Message;

/**
 * 一折神通
 *
 * @author Created by U-Demon on 2017年2月17日 上午11:46:26
 * @version 1.0.0
 */
public class ShenTongEvent implements IActivityEvent {

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
        PlayerActivity pa = role.getActivityManager().getActivityData();
        if (pa.getShenTong() == null || pa.getShenTong().size() == 0) {
            msg.setInt(0);
            msg.setByte(0);
        } else {
            msg.setInt(pa.getShenTong().get(0));
            msg.setByte(pa.getShenTong().size() - 1);
            for (int i = 1; i < pa.getShenTong().size(); i++) {
                msg.setInt(pa.getShenTong().get(i));
            }
        }
    }

    @Override
    public boolean isOpen(GameRole role) {
        return true;
    }

}
