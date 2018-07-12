package com.rd.bean.mission;

import com.google.gson.annotations.SerializedName;
import com.rd.define.GameDefine;
import com.rd.model.MissionModel;
import com.rd.model.data.mission.CardMissionReward;
import com.rd.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class CardMission {
    /**
     * 卡组id
     **/
    @SerializedName("d")
    private byte id;

    private List<CardMissionData> missions;

    public CardMission() {

    }

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public List<CardMissionData> getMissions() {
        return missions;
    }

    public void updateMission() {
        int day = DateUtil.getDistanceDay(GameDefine.SERVER_CREATE_TIME, System.currentTimeMillis());
        if (day > 6) {
            this.id = 0;
            if (missions == null || missions.size() > 0) {
                missions = new ArrayList<>();
            }
            return;
        }
        int groupId = day % 7 + 1;
        if (this.id != groupId) {
            this.id = (byte) groupId;
            missions = new ArrayList<>();
            int endId = groupId * 5;
            int startId = endId - 4 > 0 ? endId - 4 : 1;
            for (int i = startId; i <= endId; ++i) {
                CardMissionReward reward = MissionModel.getCardReward((byte) i);
                missions.add(new CardMissionData(reward));
            }
        }
    }
}
