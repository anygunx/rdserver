package com.rd.model.data;

import com.rd.bean.drop.DropData;

import java.util.ArrayList;
import java.util.List;

public class ExploreTaskData {

    private short taskId;

    private int npc;

    private byte type;

    private List<DropData> aReward;

    private List<DropData> bReward;

    private List<Short> bossId = new ArrayList<>();

    private List<Integer> bossRate = new ArrayList<>();

    public short getTaskId() {
        return taskId;
    }

    public void setTaskId(short taskId) {
        this.taskId = taskId;
    }

    public int getNpc() {
        return npc;
    }

    public void setNpc(int npc) {
        this.npc = npc;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public List<DropData> getaReward() {
        return aReward;
    }

    public void setaReward(List<DropData> aReward) {
        this.aReward = aReward;
    }

    public List<DropData> getbReward() {
        return bReward;
    }

    public void setbReward(List<DropData> bReward) {
        this.bReward = bReward;
    }

    public List<Short> getBossId() {
        return bossId;
    }

    public void setBossId(List<Short> bossId) {
        this.bossId = bossId;
    }

    public List<Integer> getBossRate() {
        return bossRate;
    }

    public void setBossRate(List<Integer> bossRate) {
        this.bossRate = bossRate;
    }

}
