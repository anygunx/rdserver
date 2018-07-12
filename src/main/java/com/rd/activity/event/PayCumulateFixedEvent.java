package com.rd.activity.event;

import com.rd.activity.ActivityService;
import com.rd.activity.EActivityType;
import com.rd.activity.config.BaseActivityConfig;
import com.rd.activity.group.ActivityGroupData;
import com.rd.activity.group.ActivityRoundConfig;
import com.rd.bean.player.PlayerActivity;
import com.rd.dao.ActivityDao;
import com.rd.game.GameRole;
import com.rd.net.message.Message;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * 固定时间累计充值
 *
 * @author ---
 * @version 1.0
 * @date 2018年1月19日下午3:48:33
 */
public class PayCumulateFixedEvent implements IActivityEvent {

    private static Logger logger = Logger.getLogger(PayCumulateFixedEvent.class);

    private int activityId;

    public PayCumulateFixedEvent(int id) {
        this.activityId = id;
    }

    @Override
    public boolean onStart() {
        return false;
    }

    @Override
    public boolean onEnd() {
        logger.info("累计充值活动结束!");
        new ActivityDao().clearPayCumulateFixed();
        return false;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void getMessage(Message msg, GameRole role) {
        long curr = System.currentTimeMillis();
        EActivityType type = EActivityType.getType(activityId);
        BaseActivityConfig configData = ActivityService.getActivityConfig(type);
        ActivityRoundConfig currRound = configData.getCurrRound(0, curr);
        if (currRound == null)
            return;
        ActivityGroupData group = ActivityService.getGroupData(type);
        int sum = role.getPayManager().getDiamondInPay(currRound.getStartTimeStr(), currRound.getEndTimeStr());
        msg.setInt(sum);
        //活动数据轮次
        msg.setShort(group.getDataRound(currRound.getRound()));

        PlayerActivity activityData = role.getActivityManager().getActivityData();
        List<Integer> list = new ArrayList<>();
        for (String key : activityData.getPayCumulateFixedData()) {
            String[] str = key.split("_");
            if (str[0].equals(String.valueOf(activityId))) {
                list.add(Integer.parseInt(str[1]));
            }
        }
        msg.setByte(list.size());
        for (int key : list) {
            msg.setInt(key);
        }
    }

    @Override
    public boolean isOpen(GameRole role) {
        return true;
    }

}
