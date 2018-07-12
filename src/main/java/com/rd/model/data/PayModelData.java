package com.rd.model.data;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public class PayModelData {

    //人民币
    private final int rmb;

    //充值获得元宝数
    private final int diamond;

    //类型：0--普通充值，>0--对应的月卡ID
    private final byte type;

    //附送列表
    private final Set<Integer> additions;

    public PayModelData(int rmb, int diamond, byte type, Set<Integer> additions) {
        this.rmb = rmb;
        this.diamond = diamond;
        this.type = type;
        this.additions = ImmutableSet.copyOf(additions);
    }

    public int getRmb() {
        return rmb;
    }

    public int getDiamond() {
        return diamond;
    }

    public byte getType() {
        return type;
    }

    public Set<Integer> getAdditions() {
        return additions;
    }
}
