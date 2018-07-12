package com.rd.activity.event;

import com.rd.activity.ActivityService;
import com.rd.activity.EActivityType;
import com.rd.activity.config.BaseActivityConfig;
import com.rd.activity.data.PayFeastLogicData;
import com.rd.activity.group.ActivityRoundConfig;
import com.rd.dao.ActivityDao;
import com.rd.game.GameRole;
import com.rd.net.message.Message;

import java.util.Map;

public class PayFeastEvent implements IActivityEvent {

    @Override
    public boolean onStart() {
        new ActivityDao().clearPayFeast();
        return true;
    }

    @Override
    public boolean onEnd() {
        return true;
    }

    @Override
    public void getMessage(Message msg, GameRole role) {
        long curr = System.currentTimeMillis();
        BaseActivityConfig configData = ActivityService.getActivityConfig(EActivityType.PAY_FEAST);
        ActivityRoundConfig currRound = configData.getCurrRound(0, curr);
        if (currRound == null)
            return;
        //充值金额
        int sum = role.getPayManager().getDiamondInPay(currRound.getStartTimeStr(), currRound.getEndTimeStr());
        msg.setInt(sum);
        Map<String, PayFeastLogicData> logicData = ActivityService.getRoundData(
                EActivityType.PAY_FEAST, currRound.getRound());
        if (sum < logicData.get("0").getCost())
            msg.setByte(0);
        else if (role.getActivityManager().getActivityData().getPayFeast() == 1)
            msg.setByte(2);
        else
            msg.setByte(1);
    }

    @Override
    public boolean isOpen(GameRole role) {
        if (role.getActivityManager().getActivityData().getPayFeast() == 1)
            return false;
        return true;
    }

}
