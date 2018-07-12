package com.rd.activity.event;

import com.rd.activity.ActivityService;
import com.rd.activity.EActivityType;
import com.rd.activity.data.InvestFundData;
import com.rd.bean.player.PlayerActivity;
import com.rd.game.GameRole;
import com.rd.net.message.Message;

import java.util.Map;

/**
 * 投资基金
 *
 * @author ---
 * @version 1.0
 * @date 2018年3月19日下午8:14:14
 */
public class InvestFundEvent implements IActivityEvent {

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
        msg.setByte(role.getPlayer().getForever());
        msg.setByte(activityData.getInvestFund());
    }

    @Override
    public boolean isOpen(GameRole role) {
        PlayerActivity activityData = role.getActivityManager().getActivityData();
        Map<String, InvestFundData> model = ActivityService.getRoundData(EActivityType.INVEST_FUND, 0);

        if (activityData.getInvestFund() >= model.size())
            return false;
        return true;
    }

}
