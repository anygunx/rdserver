package com.rd.model.data.copy.quanmin;

import com.rd.bean.drop.DropData;
import com.rd.model.data.copy.CopyData;

import java.util.List;

public class QianMinBossData extends CopyData {


    public QianMinBossData(byte id, short needLv, int bossID, int[] monsterID, List<DropData> reward,
                           DropData rebirthitem, DropData rebirthcost, int rebirthtime) {
        super(bossID, monsterID, reward);
        this.id = id;
        this.need_lv = needLv;
        this.bossId = bossID;
        this.monsterId = monsterID;
        this.reward = reward;
        this.rebirthitem = rebirthitem;
        this.rebirthcost = rebirthcost;
        this.rebirthtime = rebirthtime;
    }

    public byte getId() {
        return id;
    }

    public short getNeed_lv() {
        return need_lv;
    }

    public int getBossId() {
        return bossId;
    }

    public int[] getMonsterId() {
        return monsterId;
    }

    public int getRebirthtime() {
        return rebirthtime;
    }

    public DropData getRebirthitem() {
        return rebirthitem;
    }

    public DropData getRebirthcost() {
        return rebirthcost;
    }

    public List<DropData> getReward() {
        return reward;
    }

    private byte id;
    private short need_lv;
    private int bossId;
    private int[] monsterId;
    private int rebirthtime;
    private DropData rebirthitem;
    private DropData rebirthcost;
    private List<DropData> reward;


}
