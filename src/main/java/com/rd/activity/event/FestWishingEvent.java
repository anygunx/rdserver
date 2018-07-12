package com.rd.activity.event;

import com.rd.activity.ActivityService;
import com.rd.activity.EActivityType;
import com.rd.activity.config.BaseActivityConfig;
import com.rd.activity.data.WishingWellLogicData;
import com.rd.activity.group.ActivityGroupData;
import com.rd.activity.group.ActivityRoundConfig;
import com.rd.bean.player.PlayerActivity;
import com.rd.game.GameRole;
import com.rd.net.message.Message;

public class FestWishingEvent implements IActivityEvent {

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
        BaseActivityConfig configData = ActivityService.getActivityConfig(EActivityType.FEST_WISHING_WELL);
        ActivityRoundConfig currRound = configData.getCurrRound(0, curr);
        if (currRound == null)
            return;
        ActivityGroupData<WishingWellLogicData> group = ActivityService.getGroupData(EActivityType.FEST_WISHING_WELL);
        //活动数据轮次
        int round = group.getDataRound(currRound.getRound());
        msg.setByte(round);
        PlayerActivity activityData = role.getActivityManager().getActivityData();
        //许愿次数
        if (activityData.getFestWishing() == null || activityData.getFestWishing().size() == 0)
            msg.setByte(0);
        else
            msg.setByte(activityData.getFestWishing().get(0));
        //获得了哪些奖励
        msg.setByte(activityData.getFestWishing().size() - 1);
        for (int i = 1; i < activityData.getFestWishing().size(); i++) {
            msg.setByte(activityData.getFestWishing().get(i));
        }
    }

    @Override
    public boolean isOpen(GameRole role) {
        return true;
    }

}
