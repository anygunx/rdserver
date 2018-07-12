package com.rd.activity.event;

import com.rd.activity.ActivityService;
import com.rd.activity.EActivityType;
import com.rd.activity.data.RebateLogicData;
import com.rd.bean.player.PlayerActivity;
import com.rd.dao.ActivityDao;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.net.message.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 周末特惠包
 *
 * @author U-Demon Created on 2017年3月13日 下午3:31:16
 * @version 1.0.0
 */
public class RebateEvent3 implements IActivityEvent {

    @Override
    public boolean onStart() {
        return true;
    }

    @Override
    public boolean onEnd() {
        new ActivityDao().clearRebate(EActivityType.WEEKEND_REBATE);
        for (GameRole role : GameWorld.getPtr().getOnlineRoles().values()) {
            PlayerActivity activityData = role.getActivityManager().getActivityData();
            if (activityData.getRebates() == null)
                continue;
            for (int i = activityData.getRebates().size() - 1; i >= 0; i--) {
                if (activityData.getRebates().get(i) / 10000 == EActivityType.WEEKEND_REBATE.getId())
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
            if (key / 10000 == EActivityType.WEEKEND_REBATE.getId()) {
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
                EActivityType.WEEKEND_REBATE, role.getPlayerId(), System.currentTimeMillis());
        List<Integer> list = new ArrayList<>();
        for (int key : activityData.getRebates()) {
            if (key / 10000 == EActivityType.WEEKEND_REBATE.getId()) {
                list.add(key % 10000);
            }
        }
        if (list.size() == logicData.size())
            return false;
        return true;
    }

}
