package com.rd.bean.dungeon;

import com.rd.net.message.Message;

public class DungeonDaily {

    private byte attackTimes = 0;

    private byte buyTimes = 0;

    public byte getAttackTimes() {
        return attackTimes;
    }

    public void setAttackTimes(byte attackTimes) {
        this.attackTimes = attackTimes;
    }

    public byte getBuyTimes() {
        return buyTimes;
    }

    public void setBuyTimes(byte buyTimes) {
        this.buyTimes = buyTimes;
    }

    public void getMessage(Message message) {
        message.setByte(attackTimes);
        message.setByte(buyTimes);
    }

    public void addAttackTimes() {
        ++this.attackTimes;
    }

    public void addBuyTimes() {
        ++this.buyTimes;
    }
}
