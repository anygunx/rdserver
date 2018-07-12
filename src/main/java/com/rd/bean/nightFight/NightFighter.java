package com.rd.bean.nightFight;

import com.rd.bean.drop.DropData;
import com.rd.bean.player.Player;
import com.rd.define.NightFightDefine;
import com.rd.model.NightFightModel;

import java.util.Map.Entry;

public class NightFighter {

    private Player player;

    private short point;

    private long dieTime;

    private byte camp;

    private long attackMonsterTime;

    private short exchangeMark;

    private short rank;

    private long enterTime;

    private volatile long attackTime;

    public long getAttackTime() {
        return attackTime;
    }

    public void setAttackTime(long attackTime) {
        this.attackTime = attackTime;
    }

    public long getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(long enterTime) {
        this.enterTime = enterTime;
    }

    public short getRank() {
        return rank;
    }

    public void setRank(short rank) {
        this.rank = rank;
    }

    public short getExchangeMark() {
        return exchangeMark;
    }

    public void setExchangeMark(short exchangeMark) {
        this.exchangeMark = exchangeMark;
    }

    public long getAttackMonsterTime() {
        return attackMonsterTime;
    }

    public void setAttackMonsterTime(long attackMonsterTime) {
        this.attackMonsterTime = attackMonsterTime;
    }

    public byte getCamp() {
        return camp;
    }

    public void setCamp(int size) {
        this.camp = (byte) (size % 2);
    }

    public short getPoint() {
        return point;
    }

    public long getDieTime() {
        return dieTime;
    }

    public void setDieTime(long dieTime) {
        this.dieTime = dieTime;
    }

    public Player getPlayer() {
        return player;
    }

    public NightFighter(Player player, int size) {
        this.player = player;
        this.setCamp(size);
        this.enterTime = System.currentTimeMillis();
    }

    public Boolean isLive() {
        if (this.dieTime == 0 || this.dieTime + NightFightDefine.RELIVE_TIME < System.currentTimeMillis()) {
            return true;
        }
        return false;
    }

    public void addPoint(int add) {
        this.point += add;
    }

    public byte getProtectedTime() {
        long time = System.currentTimeMillis() - this.enterTime;
        if (time > NightFightDefine.PROTECTED_TIME) {
            return 0;
        } else {
            return (byte) ((NightFightDefine.PROTECTED_TIME - time) / 1000);
        }
    }

    public short getExchangeMax() {
        short nextMax = -1;
        Entry<Short, DropData> entry = NightFightModel.getExchangeFeats(this.exchangeMark);
        if (entry != null) {
            nextMax = entry.getKey();
        }
        return nextMax;
    }
}
