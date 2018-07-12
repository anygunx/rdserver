package com.rd.model.data.copy;

import com.rd.bean.drop.DropData;

import java.util.ArrayList;
import java.util.List;

public class NSJGCopyData extends CopyData {
    private int id;
    private int bossid;
    private int[] monsterids;
    private List<DropData> dropDataList = new ArrayList<>();

    public NSJGCopyData(int id, int bossid, int[] monsterids, List<DropData> dropDataList) {
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
