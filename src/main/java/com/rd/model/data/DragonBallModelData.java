package com.rd.model.data;

import com.rd.bean.drop.DropData;

/**
 * 龙珠模板数据
 * Created by XingYun on 2017/10/30.
 */
public class DragonBallModelData {
    private final short level;
    private final DropData consume;
    private final short[] attr;

    public DragonBallModelData(short level, DropData consume, short[] attr) {
        this.level = level;
        this.consume = consume;
        this.attr = attr;
    }

    public short getLevel() {
        return level;
    }

    public short[] getAttr() {
        return attr;
    }

    public DropData getConsume() {
        return consume;
    }
}
