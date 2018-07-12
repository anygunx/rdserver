package com.rd.model.data.copy.zhongkui;

import com.rd.bean.drop.DropData;
import com.rd.model.data.copy.CopyData;

import java.util.List;

public class NZhongKuiData extends CopyData {

    private int id;

    public int getId() {
        return id;
    }

    public int getBossId() {
        return bossId;
    }

    public int[] getMonsterIds() {
        return monsterIds;
    }

    private int exp;
    private int bossId;
    private int[] monsterIds;
    List<DropData> reward;

    public NZhongKuiData(int id, int bossId, int[] monsterids, int exp, List<DropData> reward) {
        super(bossId, monsterids, reward);
        this.id = id;
        this.bossId = bossId;
        this.monsterIds = monsterids;
        this.exp = exp;
        this.reward = reward;

    }

    public int getExp() {
        return exp;
    }

}
