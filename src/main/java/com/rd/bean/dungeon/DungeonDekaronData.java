package com.rd.bean.dungeon;

import com.rd.bean.drop.DropData;

import java.util.List;

public class DungeonDekaronData {

    private short id;

    private short fightId;

    private List<DropData> dropData;

    private int[] addAttr;

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public short getFightId() {
        return fightId;
    }

    public void setFightId(short fightId) {
        this.fightId = fightId;
    }

    public List<DropData> getDropData() {
        return dropData;
    }

    public void setDropData(List<DropData> dropData) {
        this.dropData = dropData;
    }

    public int[] getAddAttr() {
        return addAttr;
    }

    public void setAddAttr(int[] addAttr) {
        this.addAttr = addAttr;
    }
}
