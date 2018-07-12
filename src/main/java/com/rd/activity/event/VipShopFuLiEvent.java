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
import java.util.Map.Entry;

public class VipShopFuLiEvent implements IActivityEvent {

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
        BaseActivityConfig configData = ActivityService.getActivityConfig(EActivityType.VIPSHOPFULI);
        ActivityRoundConfig currRound = configData.getCurrRound(0, curr);
        if (currRound == null)
            return;
        ActivityGroupData group = ActivityService.getGroupData(EActivityType.VIPSHOPFULI);
        //活动数据轮次
        msg.setByte(group.getDataRound(currRound.getRound()));
        PlayerActivity activityData = role.getActivityManager().getActivityData();
        Map<Integer, Integer> buys = activityData.getShopBuy(EActivityType.VIPSHOPFULI);
        msg.setShort(buys.size());
        for (Entry<Integer, Integer> entry : buys.entrySet()) {
            msg.setInt(entry.getKey());
            msg.setInt(entry.getValue());
        }
    }

    @Override
    public boolean isOpen(GameRole role) {
        return true;
    }

}
