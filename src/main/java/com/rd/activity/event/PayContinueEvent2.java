package com.rd.activity.event;

import com.rd.activity.ActivityService;
import com.rd.activity.EActivityType;
import com.rd.activity.config.BaseActivityConfig;
import com.rd.bean.player.PlayerActivity;
import com.rd.game.GameRole;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;

public class PayContinueEvent2 implements IActivityEvent {

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
        BaseActivityConfig config = ActivityService.getActivityConfig(EActivityType.DUANWU_CONTINUE);
        long startTime = config.getStartTime(0);
        int day = DateUtil.getDistanceDay(startTime, curr) + 1;
        //第几天
        msg.setByte(day);
//		int sum = new PayDao().getTodayRMBPay(role.getPlayer());
        int sum = role.getPayManager().getTodayRmbInPay();
        //今日充值金额
        msg.setInt(sum);
        PlayerActivity activityData = role.getActivityManager().getActivityData();
        //连续充值领取
        msg.setByte(activityData.getPayConReward2().size());
        for (byte reward : activityData.getPayConReward2()) {
            msg.setByte(reward);
        }
        //今天剩余多少秒
        msg.setInt((int) ((DateUtil.getDayStartTime(curr) + DateUtil.DAY - curr) / 1000));
    }

    @Override
    public boolean isOpen(GameRole role) {
//		PlayerActivity activityData = role.getActivityManager().getActivityData();
//		int maxDay = ActivityService.getRoundData(EActivityType.DUANWU_CONTINUE, 0).size() - 1;
//		if (activityData.getPayConReward().contains((byte)maxDay))
//			return false;
        return true;
    }

}
