package com.rd.activity.event;

import com.rd.activity.ActivityService;
import com.rd.activity.EActivityType;
import com.rd.activity.config.BaseActivityConfig;
import com.rd.activity.data.NoRepeatTurntableLogicData;
import com.rd.bean.player.PlayerActivity;
import com.rd.dao.ActivityDao;
import com.rd.game.GameRole;
import com.rd.game.manager.ActivityManager;
import com.rd.net.message.Message;
import com.rd.util.DiceUtil.Ele;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

public class NoRepeatTurntableEvent implements IActivityEvent {

    private static Logger logger = Logger.getLogger(NoRepeatTurntableEvent.class);

    @Override
    public boolean onStart() {
        return true;
    }

    @Override
    public boolean onEnd() {
        logger.info("至尊转盘活动结束！");
        ActivityDao dao = new ActivityDao();
        for (PlayerActivity activity : dao.getPlayerActivitys2NoRepeatTurntable()) {
            activity.setFree((byte) 0);
            activity.setNoRepeatTurntableLuck(0);
            activity.setNoRepeatTurntableNum(0);
            activity.clearNoRepeatTurntableReceived();
            activity.clearNoRepeatTurntableAll();
            activity.setNoRepeatTurntablePayTime(0);
            activity.setNoRepeatTurntableRefreshTime(0);
            activity.clearNoRepeatTurntableTargeted();
            dao.updateNoRepeatTurntable(activity);
            dao.updateNoRepeatTurntableRefreshTime(activity);
        }
        return true;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void getMessage(Message msg, GameRole role) {
        ActivityManager activityManager = role.getActivityManager();
        ActivityDao dao = role.getActivityManager().getActivityDao();
        BaseActivityConfig activityConfig = ActivityService.getActivityConfig(EActivityType.NOREPEATTURNTABLE);
        Map<String, NoRepeatTurntableLogicData> model = ActivityService.getRoundData(EActivityType.NOREPEATTURNTABLE, 0);
        PlayerActivity activity = role.getActivityManager().getActivityData();
        long startTime = activity.getNoRepeatTurntablePayTime() == 0 ? activityConfig.getStartTime(0) : activity.getNoRepeatTurntablePayTime();
        long endTime = activityConfig.getEndTime();

        int diamond = role.getPayManager().getDiamondInPay(startTime, endTime);
        int num = activity.getNoRepeatTurntableNum();
        if (diamond >= 2000) {
            num += diamond / 2000;
            long curr = System.currentTimeMillis();
            activity.setNoRepeatTurntableNum(num);
            activity.setNoRepeatTurntablePayTime(curr);
        }
        //是否免费
        int free = activity.getFree();
        msg.setByte(free);
        //祝福值
        int blessings = activity.getNoRepeatTurntableLuck();
        msg.setInt(blessings);
        //可玩次数
        msg.setInt(num);
        //奖励
        List<Integer> all = activity.getNoRepeatTurntableAll();
        if (blessings == 0 && all == null || all.isEmpty()) {
//			boolean isFullLuck = false;
            List<Ele> eles = role.getActivityManager().getNoRepeatTurntableEle(model);
            activity.setNoRepeatTurntableAll(role.getActivityManager().parse2IntList(eles));
            all = activity.getNoRepeatTurntableAll();
        }
        msg.setByte(all.size());
        for (Integer i : all) {
            msg.setInt(i);
        }
        //已领取id
        List<Integer> receiveds = activity.getNoRepeatTurntableReceived();
        msg.setByte(receiveds.size());
        for (Integer i : receiveds) {
            msg.setInt(i);
        }
        //随机id
        msg.setInt(activity.getNoRepeatTurntableRandomId());
        //所在分段id
        int maxTargetedLuck = activityManager.getNoRepeatTurntableMaxTargeted();
        int segmentId = activityManager.getIdBySegment(model, maxTargetedLuck);
        msg.setInt(segmentId);
//		//免费剩余时间
//		long curr = System.currentTimeMillis();
//		long left = (activity.getNoRepeatTurntableRefreshTime()/NoRepeatTurntableLogicData.REFRESH_SPACE + 1) * 
//				NoRepeatTurntableLogicData.REFRESH_SPACE - curr;
//		msg.setLong(left);
        activity.setNoRepeatTurntableRandomId(-1);
        dao.updateNoRepeatTurntable(activity);

    }

    @Override
    public boolean isOpen(GameRole role) {
        return true;
    }

}
