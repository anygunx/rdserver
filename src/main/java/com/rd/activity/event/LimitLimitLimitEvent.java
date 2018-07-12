package com.rd.activity.event;

import com.rd.activity.ActivityService;
import com.rd.activity.EActivityType;
import com.rd.activity.config.BaseActivityConfig;
import com.rd.activity.group.ActivityRoundConfig;
import com.rd.bean.player.PlayerActivity;
import com.rd.game.GameRole;
import com.rd.net.message.Message;

/**
 * 限时限级限购
 *
 * @author ---
 * @version 1.0
 * @date 2018年3月22日下午7:13:46
 */
public class LimitLimitLimitEvent implements IActivityEvent {

    @Override
    public boolean onStart() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onEnd() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void getMessage(Message msg, GameRole role) {
        BaseActivityConfig configData = ActivityService.getActivityConfig(EActivityType.LIMIT_LIMIT_LIMIT);
        ActivityRoundConfig currRound = configData.getCurrRound(role.getPlayerId(), System.currentTimeMillis());

        PlayerActivity activityData = role.getActivityManager().getActivityData();
        int round = activityData.getLimitLimitLimit() / 1000;
        msg.setByte(currRound.getRound());
        if (currRound.getRound() == round) {
            msg.setByte(activityData.getLimitLimitLimit() % 1000);
        } else {
            msg.setByte(0);
        }
    }

    @Override
    public boolean isOpen(GameRole role) {
        // TODO Auto-generated method stub
        return true;
    }

}
