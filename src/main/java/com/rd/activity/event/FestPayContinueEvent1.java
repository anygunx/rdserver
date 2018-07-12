package com.rd.activity.event;

import com.rd.activity.ActivityService;
import com.rd.activity.EActivityType;
import com.rd.activity.config.BaseActivityConfig;
import com.rd.activity.data.FestPayContinueLogicData;
import com.rd.bean.player.PlayerActivity;
import com.rd.dao.ActivityDao;
import com.rd.game.GameRole;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;
import org.apache.log4j.Logger;

import java.util.Map;

public class FestPayContinueEvent1 implements IActivityEvent {

    private static Logger logger = Logger.getLogger(FestPayContinueEvent1.class);

    @Override
    public boolean onStart() {
        return true;
    }

    @Override
    public boolean onEnd() {
        logger.info("节日期间每日充值结束!");
        ActivityDao dao = new ActivityDao();
        dao.updateActivityFestPayCon();
        return true;
    }

    @Override
    public void getMessage(Message msg, GameRole role) {
        long curr = System.currentTimeMillis();
        BaseActivityConfig config = ActivityService.getActivityConfig(EActivityType.FEST_PAY_CONTINUE1);
        Map<String, FestPayContinueLogicData> logics = ActivityService.getRoundData(EActivityType.FEST_PAY_CONTINUE1, 0);
        // 注：此算法中只支持统一价格 否则深度增加
        int rmb = logics.values().iterator().next().getRmb();
        long startTime = config.getStartTime(0);
        int day = DateUtil.getDistanceDay(startTime, curr) + 1;
        //第几天
        msg.setByte(day);
        byte dayCount = role.getPayManager().getPayConGreaterDays(rmb, startTime, curr);
        msg.setByte(dayCount);
//		int sum = new PayDao().getTodayRMBPay(role.getPlayer());
        int sum = role.getPayManager().getTodayRmbInPay();
        //今日充值金额
        msg.setInt(sum);
        PlayerActivity activityData = role.getActivityManager().getActivityData();
        activityData.setFestPayConDayCount(dayCount);
        //连续充值领取
        msg.setByte(activityData.getFestPayConReward().size());
        for (byte reward : activityData.getFestPayConReward()) {
            msg.setByte(reward);
        }
        //今天剩余多少秒
        msg.setInt((int) ((DateUtil.getDayStartTime(curr) + DateUtil.DAY - curr) / DateUtil.SECOND));
    }

    @Override
    public boolean isOpen(GameRole role) {
        return true;
    }

}
