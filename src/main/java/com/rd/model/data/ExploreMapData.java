package com.rd.model.data;

import com.rd.bean.drop.DropData;

import java.util.List;

public class ExploreMapData {

    private byte id;

    private byte type;

    private int mapId;

    private int levelLimit;

    private List<DropData> rewards;

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }

    public int getLevelLimit() {
        return levelLimit;
    }

    public void setLevelLimit(int levelLimit) {
        this.levelLimit = levelLimit;
    }

    public List<DropData> getRewards() {
        return rewards;
    }

    public void setRewards(List<DropData> rewards) {
        this.rewards = rewards;
    }

}
