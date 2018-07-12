package com.rd.define;

import java.util.HashMap;
import java.util.Map;

/**
 * 主宰位置
 *
 * @author U-Demon
 */
public enum EDomType {

    HELMET(0, "面夹", 10),
    SHOULDER(1, "护肩", 10),
    KNEE(2, "护膝", 10),
    PENDANT(3, "吊坠", 10),;

    public static final int DOM_SIZE;

    public static final Map<Byte, EDomType> valueMap;

    static {
        valueMap = new HashMap<>();
        for (EDomType type : EDomType.values()) {
            valueMap.put(type.pos, type);
        }
        DOM_SIZE = EDomType.values().length;
    }

    private final byte pos;
    private final String name;
    //分解获得碎片
    private final int piece;

    EDomType(int pos, String name, int piece) {
        this.pos = (byte) pos;
        this.name = name;
        this.piece = piece;
    }

    public static EDomType getType(int id) {
        return valueMap.get((byte) id);
    }

    public byte getPos() {
        return pos;
    }

    public String getName() {
        return name;
    }

    public int getPiece() {
        return piece;
    }

}
