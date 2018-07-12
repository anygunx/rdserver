package com.rd.model.data.copy;

import com.rd.bean.drop.DropData;

import java.util.ArrayList;
import java.util.List;

public class CopyData {
    private int bossid;
    private int[] monsterids;
    private List<DropData> dropDataList = new ArrayList<>();

    public int getBossid() {
        return bossid;
    }

    public int[] getMonsterids() {
        return monsterids;
    }

    public List<DropData> getDropDataList() {
        return dropDataList;
    }

    public CopyData(int bossid, int[] monsterids, List<DropData> dropDataList) {
        this.bossid = bossid;
        this.monsterids = monsterids;
        this.dropDataList = dropDataList;
    }

}
