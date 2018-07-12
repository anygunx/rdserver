package com.rd.bean.role;

/**
 * @author ---
 * @version 1.0
 * @date 2018年6月23日下午2:41:17
 */
public class RoleData {

    private short level;

    private long exp;

    private int[] attr;

    public RoleData(short level, long exp, int[] attr) {
        this.level = level;
        this.exp = exp;
        this.attr = attr;
    }

    public short getLevel() {
        return level;
    }

    public long getExp() {
        return exp;
    }

    public int[] getAttr() {
        return attr;
    }
}
