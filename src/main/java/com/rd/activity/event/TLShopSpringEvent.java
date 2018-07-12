package com.rd.activity.event;

import com.rd.activity.ActivityService;
import com.rd.activity.EActivityType;
import com.rd.activity.config.BaseActivityConfig;
import com.rd.activity.group.ActivityGroupData;
import com.rd.activity.group.ActivityRoundConfig;
import com.rd.bean.player.PlayerActivity;
import com.rd.game.GameRole;
import com.rd.net.message.Message;

import java.util.Map;

/**
 * Created by XingYun on 2017/1/20.
 */
public class TLShopSpringEvent implements IActivityEvent {
    @Override
    public boolean onStart() {
        return false;
    }

    @Override
    public boolean onEnd() {
        return false;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void getMessage(Message msg, GameRole role) {
        long curr = System.currentTimeMillis();
        EActivityType activityType = EActivityType.TLSHOP_SPRING;
        BaseActivityConfig configData = ActivityService.getActivityConfig(activityType);
        ActivityRoundConfig currRound = configData.getCurrRound(0, curr);
        if (currRound == null)
            return;
        ActivityGroupData group = ActivityService.getGroupData(activityType);
        //活动数据轮次
        msg.setByte(group.getDataRound(currRound.getRound()));
        PlayerActivity activityData = role.getActivityManager().getActivityData();
        Map<Integer, Integer> buys = activityData.getShopSpringBuy(activityType);
        msg.setShort(buys.size());
        for (Map.Entry<Integer, Integer> entry : buys.entrySet()) {
            msg.setInt(entry.getKey());
            msg.setInt(entry.getValue());
        }
    }

    @Override
    public boolean isOpen(GameRole role) {
        return true;
    }
}
