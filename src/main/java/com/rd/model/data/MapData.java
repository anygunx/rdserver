package com.rd.model.data;

import com.rd.bean.drop.DropData;

import java.util.Map;

/**
 * @author ---
 * @version 1.0
 * @date 2018年4月17日下午1:48:02
 */
public class MapData {

    private short id;

    private DropData exp;

    private DropData gold;

    private Map<Short, MapStageData> stageMap;

    public MapData(short id, DropData exp, DropData gold, Map<Short, MapStageData> stageMap) {
        this.id = id;
        this.stageMap = stageMap;
    }

    public short getId() {
        return id;
    }

    public DropData getExp() {
        return exp;
    }

    public DropData getGold() {
        return gold;
    }

    public Map<Short, MapStageData> getStageMap() {
        return stageMap;
    }

}
