package com.rd.model.data;

import com.google.common.collect.ImmutableList;
import com.rd.bean.drop.DropData;

import java.util.List;

/**
 * 神羽装备模型数据
 */
public class WingGodModelData {
    private final short id;
    private final byte pos;
    private final byte level;
    private final short wingStageLimit;
    /**
     * 合成消耗
     **/
    private final List<DropData> craftCost;
    /**
     * 转换消耗
     **/
    private final List<DropData> converseCost;
    private final int[] attr;
    private final String name;

    public WingGodModelData(short id, byte pos, byte level, short wingStageLimit, List<DropData> craftCost, List<DropData> converseCost, int[] attr, String name) {
        this.id = id;
        this.pos = pos;
        this.level = level;
        this.wingStageLimit = wingStageLimit;
        this.craftCost = ImmutableList.copyOf(craftCost);
        this.converseCost = ImmutableList.copyOf(converseCost);
        this.attr = attr;
        this.name = name;
    }

    public short getId() {
        return id;
    }

    public byte getPos() {
        return pos;
    }

    public byte getLevel() {
        return level;
    }

    public short getWingStageLimit() {
        return wingStageLimit;
    }

    public List<DropData> getCraftCost() {
        return craftCost;
    }

    public List<DropData> getConverseCost() {
        return converseCost;
    }

    public int[] getAttr() {
        return attr;
    }

    public String getName() {
        return name;
    }


}
