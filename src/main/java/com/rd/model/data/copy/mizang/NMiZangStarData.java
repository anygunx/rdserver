package com.rd.model.data.copy.mizang;

import com.rd.bean.drop.DropData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NMiZangStarData {
    private int id;

    private Map<Byte, List<DropData>> mapReward = new HashMap<>();

    public int getId() {
        return id;
    }

    public Map<Byte, List<DropData>> getMapReward() {
        return mapReward;
    }

    public NMiZangStarData(int id, Map<Byte, List<DropData>> mapReward) {
        this.id = id;
        this.mapReward = mapReward;
    }

}
