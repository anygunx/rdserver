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

/**
 * 限制礼包
 *
 * @author U-Demon Created on 2017年3月14日 下午6:06:03
 * @version 1.0.0
 */
public class LimitGiftVipEvent implements IActivityEvent {

    @Override
    public boolean onStart() {
        return true;
    }

    @Override
    public boolean onEnd() {
        return true;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void getMessage(Message msg, GameRole role) {
        long curr = System.currentTimeMillis();
        BaseActivityConfig configData = ActivityService.getActivityConfig(EActivityType.LIMIT_GIFT_VIP);
        ActivityRoundConfig currRound = configData.getCurrRound(0, curr);
        if (currRound == null)
            return;
        ActivityGroupData group = ActivityService.getGroupData(EActivityType.LIMIT_GIFT_VIP);
        //活动数据轮次
        msg.setByte(group.getDataRound(currRound.getRound()));
        PlayerActivity activityData = role.getActivityManager().getActivityData();
        Map<Integer, Integer> buys = activityData.getGiftBuy(EActivityType.LIMIT_GIFT_VIP);
        msg.setByte(buys.size());
        for (Entry<Integer, Integer> entry : buys.entrySet()) {
            msg.setByte(entry.getKey());
            msg.setShort(entry.getValue());
        }
    }

    @Override
    public boolean isOpen(GameRole role) {
        return true;
    }

}
