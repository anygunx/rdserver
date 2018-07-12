package com.rd.activity.event;

import com.rd.activity.ActivityService;
import com.rd.activity.EActivityType;
import com.rd.activity.data.InvestLogicData;
import com.rd.bean.player.PlayerActivity;
import com.rd.game.GameRole;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;

import java.util.Map;

/**
 * 投资计划
 *
 * @author Created by U-Demon on 2016年12月26日 下午8:48:47
 * @version 1.0.0
 */
public class InvestEvent implements IActivityEvent {

    @Override
    public boolean onStart() {
        return false;
    }

    @Override
    public boolean onEnd() {
        return false;
    }

    @Override
    public void getMessage(Message msg, GameRole role) {
        PlayerActivity activityData = role.getActivityManager().getActivityData();
        //未购买
        if (activityData.getInvests().size() <= 0) {
            msg.setByte(0);
        } else {
            //已购买
            msg.setByte(1);
            int day = DateUtil.getDistanceDay(activityData.getInvests().get(0), System.currentTimeMillis()) + 1;
            msg.setByte(day > 100 ? 100 : day);
            msg.setByte(activityData.getInvests().size() - 1);
            for (int i = 1; i < activityData.getInvests().size(); i++) {
                long id = activityData.getInvests().get(i);
                msg.setByte((int) id);
            }
        }
    }

    @Override
    public boolean isOpen(GameRole role) {
        PlayerActivity activityData = role.getActivityManager().getActivityData();
        Map<String, InvestLogicData> model = ActivityService.getRoundData(EActivityType.INVEST, 0);
        //已经全部领取
        if (activityData.getInvests().size() == model.size() + 1)
            return false;
        return true;
    }

}
