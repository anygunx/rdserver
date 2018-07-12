package com.rd.activity.event;

import com.rd.activity.ActivityService;
import com.rd.activity.EActivityType;
import com.rd.activity.data.LogonLogicData;
import com.rd.bean.player.PlayerActivity;
import com.rd.game.GameRole;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;

import java.util.Map;

public class LogonEvent implements IActivityEvent {

    @Override
    public boolean onStart() {
        return true;
    }

    @Override
    public boolean onEnd() {
        return true;
    }

    @Override
    public void getMessage(Message msg, GameRole role) {
        PlayerActivity data = role.getActivityManager().getActivityData();
        if (!DateUtil.dayEqual(System.currentTimeMillis(), role.getPlayer().getLastLoginTime()))
            role.getActivityManager().handleLogin();
        int today = data.getLoginDay();
        if (today > 100)
            today = 100;
        msg.setByte(today);
        msg.setByte(data.getLogonRewards().size());
        for (byte reward : data.getLogonRewards()) {
            msg.setByte(reward);
        }
    }

    @Override
    public boolean isOpen(GameRole role) {
        Map<String, LogonLogicData> model = ActivityService.getRoundData(EActivityType.LOGON, role.getPlayerId(), System.currentTimeMillis());
        PlayerActivity data = role.getActivityManager().getActivityData();
        if (model == null || data.getLogonRewards().size() >= model.size())
            return false;
        return true;
    }

}
