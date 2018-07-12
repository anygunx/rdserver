package com.rd.define;

/**
 * 功能日志类型
 *
 * @author Created by U-Demon on 2017年2月9日 下午5:37:45
 * @version 1.0.0
 */
public enum LogFunType {

    LADDER(1, "天梯"),
    EXPLORE(2, "探索"),
    ESCORT_COMP(3, "押镖完成"),
    ESCORT_ROB(4, "劫镖"),
    TZZP(5, "投资转盘"),
    JEWEL_MERGE(6, "宝石合成"),;

    private final byte id;

    private final String desc;

    public byte getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }

    LogFunType(int id, String desc) {
        this.id = (byte) id;
        this.desc = desc;
    }

}
