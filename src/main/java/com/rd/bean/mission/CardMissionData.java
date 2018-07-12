package com.rd.bean.mission;

import com.google.gson.annotations.SerializedName;
import com.rd.define.MissionDefine;
import com.rd.model.data.mission.CardMissionReward;

import java.util.ArrayList;
import java.util.List;

public class CardMissionData {
    /**
     * 卡牌任务id
     **/
    @SerializedName("d")
    private byte id;
    /**
     * 奖励领取状态
     **/
    @SerializedName("s")
    private byte state;
    /**
     * 任务
     **/
    @SerializedName("m")
    private List<RoleChainMission> missions;

    public CardMissionData(CardMissionReward reward) {
        this.id = reward.getId();
        this.state = MissionDefine.MISSION_STATE_UNCOMPLETED;
        this.missions = new ArrayList<>();
        for (Byte mid : reward.getCards()) {
            RoleChainMission m = new RoleChainMission();
            m.setId(mid);
            this.missions.add(m);
        }
    }

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public byte getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }

    public List<RoleChainMission> getMissions() {
        return missions;
    }

    public void setMissions(List<RoleChainMission> missions) {
        this.missions = missions;
    }

}
