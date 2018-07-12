package com.lg.bean.game;

import com.lg.bean.BaseLog;
import com.lg.util.ReflectionUtil;

/**
 * Created by XingYun on 2016/6/17.
 */
public class Rank extends BaseLog {
    protected int playerId;
    private byte rankType;
    protected int rank;
    protected String name;
    protected short level;
    protected int vip;
    protected int value1;
    protected int value2;

    public Rank() {
    }

    public Rank(int playerId, int rankType, int rank, String name, short level, int vip,
                int value1, int value2) {
        this.playerId = playerId;
        this.rankType = (byte) rankType;
        this.rank = rank;
        this.name = name;
        this.level = level;
        this.vip = vip;
        this.value1 = value1;
        this.value2 = value2;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public byte getRankType() {
        return rankType;
    }

    public void setRankType(byte rankType) {
        this.rankType = rankType;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short getLevel() {
        return level;
    }

    public void setLevel(short level) {
        this.level = level;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public int getValue1() {
        return value1;
    }

    public void setValue1(int value1) {
        this.value1 = value1;
    }

    public int getValue2() {
        return value2;
    }

    public void setValue2(int value2) {
        this.value2 = value2;
    }

    @Override
    public String getFormatLog() throws Exception {
        StringBuilder builder = new StringBuilder(super.getPrefix())
                .append(ReflectionUtil.getFieldsString(this));
        return builder.toString();
    }
}
