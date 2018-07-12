package com.rd.model.data.copy.geren;

import com.rd.bean.drop.DropData;
import com.rd.model.data.copy.CopyData;

import java.util.ArrayList;
import java.util.List;

public class NGeRenBossData extends CopyData {

    private int id;
    private short need_lv;
    private int bossId;
    private int[] monsterIds;
    private byte order;

    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public short getNeed_lv() {
        return need_lv;
    }


    public int getBossId() {
        return bossId;
    }


    public int[] getMonsterIds() {
        return monsterIds;
    }


    public List<DropData> getReward() {
        return reward;
    }


    private List<DropData> reward = new ArrayList<>();


    public NGeRenBossData(int id, short needLv, int bossId, int[] monsterIds,
                          List<DropData> reward) {
        super(bossId, monsterIds, reward);
        this.id = id;
        this.need_lv = needLv;
        this.bossId = bossId;
        this.monsterIds = monsterIds;
        this.reward = reward;
        //this.order=order;
    }

}
