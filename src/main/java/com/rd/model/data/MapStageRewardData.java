package com.rd.model.data;

import com.google.common.collect.ImmutableList;
import com.rd.bean.drop.DropData;

import java.util.List;

public class MapStageRewardData {

    private short id;

    private short monsterExp;

    private short monsterGold;

    private int bossExp;

    private int bossGold;

    private List<DropData> rewardList;

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public short getMonsterExp() {
        return monsterExp;
    }

    public void setMonsterExp(short monsterExp) {
        this.monsterExp = monsterExp;
    }

    public short getMonsterGold() {
        return monsterGold;
    }

    public void setMonsterGold(short monsterGold) {
        this.monsterGold = monsterGold;
    }

    public int getBossExp() {
        return bossExp;
    }

    public void setBossExp(int bossExp) {
        this.bossExp = bossExp;
    }

    public int getBossGold() {
        return bossGold;
    }

    public void setBossGold(int bossGold) {
        this.bossGold = bossGold;
    }

    public List<DropData> getRewardList() {
        return rewardList;
    }

    public void setRewardList(List<DropData> rewardList) {
        this.rewardList = ImmutableList.copyOf(rewardList);
    }
}
