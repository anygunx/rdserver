package com.rd.activity.event;

import com.rd.activity.ActivityService;
import com.rd.activity.EActivityType;
import com.rd.activity.data.TLGiftLogicData;
import com.rd.bean.player.PlayerActivity;
import com.rd.define.ERankType;
import com.rd.game.GameRole;
import com.rd.net.message.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TLGiftEvent implements IActivityEvent {

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
        long curr = System.currentTimeMillis();
        Map<String, TLGiftLogicData> round = ActivityService.getRoundData(EActivityType.TLGIFT,
                role.getPlayerId(), curr);
        if (round == null)
            return;
        ERankType rankType = ERankType.WING;
        for (TLGiftLogicData data : round.values()) {
            rankType = ERankType.getType(data.getType() + 2);
            msg.setByte(data.getType());
            break;
        }
        msg.setInt(0);
        PlayerActivity activityData = role.getActivityManager().getActivityData();
        List<Byte> rewards = new ArrayList<>();
        for (TLGiftLogicData data : round.values()) {
            if (activityData.getTlGift().contains(data.getId()))
                rewards.add(data.getId());
        }
        msg.setByte(rewards.size());
        for (byte id : rewards) {
            msg.setByte(id);
        }
    }

    @Override
    public boolean isOpen(GameRole role) {
        return true;
    }

}
