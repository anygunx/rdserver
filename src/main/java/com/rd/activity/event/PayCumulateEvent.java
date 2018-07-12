package com.rd.activity.event;

import com.rd.activity.ActivityService;
import com.rd.activity.EActivityType;
import com.rd.activity.config.BaseActivityConfig;
import com.rd.activity.group.ActivityGroupData;
import com.rd.activity.group.ActivityRoundConfig;
import com.rd.bean.player.PlayerActivity;
import com.rd.game.GameRole;
import com.rd.net.message.Message;
import org.apache.log4j.Logger;

/**
 * 累计充值
 *
 * @author Created by U-Demon on 2016年12月26日 下午5:27:26
 * @version 1.0.0
 */
public class PayCumulateEvent implements IActivityEvent {

    private static Logger logger = Logger.getLogger(PayCumulateEvent.class);

    @Override
    public boolean onStart() {
        return false;
    }

    @Override
    public boolean onEnd() {
        logger.info("累计充值活动结束!");
        return false;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void getMessage(Message msg, GameRole role) {
        long curr = System.currentTimeMillis();
        BaseActivityConfig configData = ActivityService.getActivityConfig(EActivityType.PAY_CUMULATE);
        ActivityRoundConfig currRound = configData.getCurrRound(0, curr);
        if (currRound == null)
            return;
        ActivityGroupData group = ActivityService.getGroupData(EActivityType.PAY_CUMULATE);
        int sum = role.getPayManager().getDiamondInPay(currRound.getStartTimeStr(), currRound.getEndTimeStr());
        msg.setInt(sum);
        //活动数据轮次
        msg.setShort(group.getDataRound(currRound.getRound()));

        PlayerActivity activityData = role.getActivityManager().getActivityData();
        msg.setByte(activityData.getPayCumulateData().size());
        for (Integer id : activityData.getPayCumulateData()) {
            msg.setInt(id);
        }
    }

    @Override
    public boolean isOpen(GameRole role) {
        return true;
    }

}
