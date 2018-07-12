package com.rd.bean.goods.data;

import com.rd.bean.drop.DropData;

import java.util.ArrayList;
import java.util.List;

public class NEquipData extends GoodsData {

    public List<DropData> getBreakItem() {
        return breakItem;
    }

    private short level;

    private byte quality;

    private byte position;

    private String name;

    private int attr[];

    public short getLevel() {
        return level;
    }

    public byte getQuality() {
        return quality;
    }

    public byte getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    public int[] getAttr() {
        return attr;
    }

    List<DropData> breakItem = new ArrayList<>();


    public NEquipData(short level, byte quality, byte pos, int[] attr, List<DropData> breakItem) {
        this.level = level;
        this.quality = quality;
        this.attr = attr;
        this.position = pos;
        this.breakItem = breakItem;
    }

}
