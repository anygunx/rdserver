package com.rd.model.data;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * 关卡神器模板数据
 * Created by XingYun on 2017/11/28.
 */
public class ArtifactBossModelData {
    private final byte id;
    private final int[] attr;
    private final Map<Short, Byte> pieces;
    private final int fighting;

    public ArtifactBossModelData(byte id, int[] attr, Map<Short, Byte> pieces, int fighting) {
        this.id = id;
        this.attr = attr;
        this.pieces = ImmutableMap.copyOf(pieces);
        this.fighting = fighting;
    }

    public byte getId() {
        return id;
    }

    public int[] getAttr() {
        return attr;
    }

    public Map<Short, Byte> getPieces() {
        return pieces;
    }

    public int getFighting() {
        return fighting;
    }
}
