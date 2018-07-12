package com.rd.activity.event;

import com.rd.activity.ActivityService;
import com.rd.activity.EActivityType;
import com.rd.activity.config.BaseActivityConfig;
import com.rd.activity.group.ActivityGroupData;
import com.rd.activity.group.ActivityRoundConfig;
import com.rd.bean.player.Player;
import com.rd.bean.player.PlayerActivity;
import com.rd.game.GameRole;
import com.rd.net.message.Message;

import java.util.Map.Entry;

public class BuyOneEvent implements IActivityEvent {

    @Override
    public boolean onStart() {
        return true;
    }

    @Override
    public boolean onEnd() {
        return true;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void getMessage(Message msg, GameRole role) {
        long curr = System.currentTimeMillis();
        BaseActivityConfig configData = ActivityService.getActivityConfig(EActivityType.BUY_ONE);
        ActivityRoundConfig currRound = configData.getCurrRound(0, curr);
        if (currRound == null)
            return;
        ActivityGroupData group = ActivityService.getGroupData(EActivityType.BUY_ONE);
        //活动数据轮次
        msg.setByte(group.getDataRound(currRound.getRound()));
        //今天充值元宝数
        Player player = role.getPlayer();
        int sum = role.getPayManager().getRmbInPay(currRound.getStartTimeStr(), currRound.getEndTimeStr());
        msg.setInt(sum);
        //购买记录
        PlayerActivity pa = role.getActivityManager().getActivityData();
        if (pa.getBuyOne() == null)
            msg.setByte(0);
        else {
            msg.setByte(pa.getBuyOne().size());
            for (Entry<Integer, Integer> entry : pa.getBuyOne().entrySet()) {
                msg.setInt(entry.getKey());
                msg.setInt(entry.getValue());
            }
        }
    }

    @Override
    public boolean isOpen(GameRole role) {
        return true;
    }

}
