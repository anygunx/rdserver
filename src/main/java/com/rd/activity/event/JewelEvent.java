package com.rd.activity.event;

import com.rd.bean.player.PlayerActivity;
import com.rd.game.GameRole;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;

/**
 * 宝石抽奖
 *
 * @author Created by U-Demon on 2017年2月13日 下午5:56:29
 * @version 1.0.0
 */
public class JewelEvent implements IActivityEvent {

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
        long curr = System.currentTimeMillis();
        PlayerActivity pa = role.getActivityManager().getActivityData();
        if (DateUtil.dayEqual(curr, pa.getLastJewelSingle()))
            msg.setInt(99999);
        else
            msg.setInt(0);
    }

    @Override
    public boolean isOpen(GameRole role) {
        return true;
    }

}
