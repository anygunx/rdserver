package com.rd.model.data;

import com.rd.bean.drop.DropData;

/**
 * 铸魂模板数据
 * Created by XingYun on 2017/2/21.
 */
public class EquipZhuHunModelData {
    private final byte type;
    private final byte level;
    private final int[] attr;
    private final DropData consume;

    public EquipZhuHunModelData(byte type, byte level, int[] attr, DropData consume) {
        this.type = type;
        this.level = level;
        this.attr = attr;
        this.consume = consume;
    }

    public byte getType() {
        return type;
    }

    public byte getLevel() {
        return level;
    }

    public int[] getAttr() {
        return attr;
    }

    public DropData getConsume() {
        return consume;
    }
}
