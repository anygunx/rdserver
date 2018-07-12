package com.rd.bean.fight.monstersiege;

import com.rd.net.message.Message;

/**
 * 玩家守城记录
 */
public class PlayerMonsterRecord {
    private short id;
    private int dmg;
    private long ts;

    public PlayerMonsterRecord() {
    }

    public PlayerMonsterRecord(short id, int dmg, long ts) {
        this.id = id;
        this.dmg = dmg;
        this.ts = ts;
    }

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public int getDmg() {
        return dmg;
    }

    public void setDmg(int dmg) {
        this.dmg = dmg;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public void getMessage(Message message) {
        message.setShort(id);
        message.setInt(dmg);
        message.setLong(ts);
    }
}
