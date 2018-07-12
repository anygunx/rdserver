package com.rd.bean.map;

import com.rd.bean.drop.DropData;

import java.util.List;

public class MapNodeData {

    private short id;

    private byte batch;

    private short monsterId;

    private short bossId;

    private short monsterDrop;

    private short bossDrop;

    private List<DropData> rewardDropList;

    private List<DropData> sweepDropList;

    private int power;

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public byte getBatch() {
        return batch;
    }

    public void setBatch(byte batch) {
        this.batch = batch;
    }

    public short getMonsterId() {
        return monsterId;
    }

    public void setMonsterId(short monsterId) {
        this.monsterId = monsterId;
    }

    public short getBossId() {
        return bossId;
    }

    public void setBossId(short bossId) {
        this.bossId = bossId;
    }

    public List<DropData> getRewardDropList() {
        return rewardDropList;
    }

    public void setRewardDropList(List<DropData> rewardDropList) {
        this.rewardDropList = rewardDropList;
    }

    public List<DropData> getSweepDropList() {
        return sweepDropList;
    }

    public void setSweepDropList(List<DropData> sweepDropList) {
        this.sweepDropList = sweepDropList;
    }

    public short getMonsterDrop() {
        return monsterDrop;
    }

    public void setMonsterDrop(short monsterDrop) {
        this.monsterDrop = monsterDrop;
    }

    public short getBossDrop() {
        return bossDrop;
    }

    public void setBossDrop(short bossDrop) {
        this.bossDrop = bossDrop;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public MapNodeData() {

    }
}
