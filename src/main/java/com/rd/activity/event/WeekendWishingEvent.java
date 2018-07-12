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

public class WeekendWishingEvent implements IActivityEvent {

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
        BaseActivityConfig configData = ActivityService.getActivityConfig(EActivityType.WEEKEND_WISHING_WELL);
        ActivityRoundConfig currRound = configData.getCurrRound(0, curr);
        if (currRound == null)
            return;
        ActivityGroupData<WishingWellLogicData> group = ActivityService.getGroupData(
                EActivityType.WEEKEND_WISHING_WELL);
        //活动数据轮次
        int round = group.getDataRound(currRound.getRound());
        msg.setByte(round);
//		int sum = new PayDao().getTodayRMBPay(role.getPlayer())*10;
        int sum = role.getPayManager().getTodayDiamondInPay();
        msg.setInt(sum);
        WishingWellLogicData logic = group.getRound(round).get("" + round);
        int next = 0;
        for (; next < logic.getPrices().size(); next++) {
            if (sum < logic.getPrices().get(next))
                break;
        }
        msg.setInt(next < logic.getPrices().size() ? logic.getPrices().get(next) : logic.getPrices().get(logic.getPrices().size() - 1));
        PlayerActivity activityData = role.getActivityManager().getActivityData();
        //剩余许愿次数
        int used = 0;
        if (activityData.getFestWishing() != null && activityData.getFestWishing().size() > 1)
            used = activityData.getFestWishing().size() - 1;
        msg.setByte(next - used < 0 ? 0 : next - used);
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
