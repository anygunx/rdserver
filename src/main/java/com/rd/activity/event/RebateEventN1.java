package com.rd.activity.event;

import com.rd.activity.ActivityService;
import com.rd.activity.EActivityType;
import com.rd.activity.data.RebateLogicData;
import com.rd.bean.player.PlayerActivity;
import com.rd.dao.ActivityDao;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.net.message.Message;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 节日特惠包
 *
 * @author Created by U-Demon on 2016年12月26日 下午4:43:05
 * @version 1.0.0
 */
public class RebateEventN1 implements IActivityEvent {

    private static Logger logger = Logger.getLogger(RebateEventN1.class);

    @Override
    public boolean onStart() {
        logger.info("特惠礼包N活动开启!");
        return true;
    }

    @Override
    public boolean onEnd() {
        logger.info("特惠礼包N活动结束!");
        new ActivityDao().clearRebate(EActivityType.REBATE_N1);
        for (GameRole role : GameWorld.getPtr().getOnlineRoles().values()) {
            PlayerActivity activityData = role.getActivityManager().getActivityData();
            if (activityData.getRebates() == null)
                continue;
            for (int i = activityData.getRebates().size() - 1; i >= 0; i--) {
                if (activityData.getRebates().get(i) / 10000 == EActivityType.REBATE_N1.getId())
                    activityData.getRebates().remove(i);
            }
        }
        return true;
    }

    @Override
    public void getMessage(Message msg, GameRole role) {
        PlayerActivity activityData = role.getActivityManager().getActivityData();
        List<Integer> list = new ArrayList<>();
        for (int key : activityData.getRebates()) {
            if (key / 10000 == EActivityType.REBATE_N1.getId()) {
                list.add(key % 10000);
            }
        }
        msg.setByte(list.size());
        for (int id : list) {
            msg.setByte(id);
        }
    }

    @Override
    public boolean isOpen(GameRole role) {
        PlayerActivity activityData = role.getActivityManager().getActivityData();
        Map<String, RebateLogicData> logicData = ActivityService.getRoundData(
                EActivityType.REBATE_N1, role.getPlayerId(), System.currentTimeMillis());
        List<Integer> list = new ArrayList<>();
        for (int key : activityData.getRebates()) {
            if (key / 10000 == EActivityType.REBATE_N1.getId()) {
                list.add(key % 10000);
            }
        }
        if (list.size() == logicData.size())
            return false;
        return true;
    }

}
