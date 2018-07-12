package com.rd.model.data;

import com.google.common.collect.ImmutableList;
import com.rd.bean.drop.DropData;

import java.util.List;

public class CardModelData {
    private final short id;
    private final byte pos;

    private final List<DropData> cost;

    public CardModelData(short id, byte pos, List<DropData> cost) {
        this.id = id;
        this.pos = pos;
        this.cost = ImmutableList.copyOf(cost);
    }

    public short getId() {
        return id;
    }

    public byte getPos() {
        return pos;
    }

    public List<DropData> getCost() {
        return cost;
    }
}
