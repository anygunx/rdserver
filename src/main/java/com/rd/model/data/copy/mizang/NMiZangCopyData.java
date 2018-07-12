package com.rd.model.data.copy.mizang;

import com.rd.bean.drop.DropData;
import com.rd.model.data.copy.CopyData;

import java.util.ArrayList;
import java.util.List;

public class NMiZangCopyData extends CopyData implements Comparable<NMiZangCopyData> {
    private int id;
    private int type;
    private int bossid;
    private int[] monsterids;
    private List<DropData> reward = new ArrayList<>();
    private List<DropData> first = new ArrayList<>();
    private int level;

    public NMiZangCopyData(int type, int id, int bossid, List<DropData> reward, List<DropData> first, int[] monsterids, int level) {
        super(bossid, monsterids, reward);
        this.type = type;
        this.id = id;
        this.bossid = bossid;
        this.reward = reward;
        this.first = first;
        this.monsterids = monsterids;
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public int getBossid() {
        return bossid;
    }

    public int[] getMonsterids() {
        return monsterids;
    }

    public List<DropData> getReward() {
        return reward;
    }

    public List<DropData> getFirst() {
        return first;
    }

    @Override
    public int compareTo(NMiZangCopyData o) {
        if (this.id > o.getId()) {
            return 1;
        } else if (this.id < o.getId()) {
            return -1;
        }

        return 0;
    }


}
