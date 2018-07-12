package com.rd.model.data.copy.tianmen;

import com.rd.bean.drop.DropData;
import com.rd.model.data.copy.CopyData;

import java.util.ArrayList;
import java.util.List;

public class NTianMenData extends CopyData {
    private int id;
    private int bossid;
    private int[] monsterids;
    private List<DropData> dropDataList = new ArrayList<>();

    public NTianMenData(int id, int bossid, int[] monsterids, List<DropData> dropDataList) {
        super(bossid, monsterids, dropDataList);
        this.id = id;
        this.bossid = bossid;
        this.monsterids = monsterids;
        this.dropDataList = dropDataList;
    }

    public List<DropData> getDropDataList() {
        return dropDataList;
    }

    public void setDropDataList(List<DropData> dropDataList) {
        this.dropDataList = dropDataList;
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

}
