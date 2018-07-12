package com.rd.bean.card;

import com.rd.net.message.Message;

/**
 * 玩家图鉴页
 */
public class PlayerCardSuit {
    private short id;
    private byte lv;

    public PlayerCardSuit(short id) {
        this(id, (byte) 0);
    }

    public PlayerCardSuit(short id, byte lv) {
        this.id = id;
        this.lv = lv;
    }

    public short getId() {
        return id;
    }

    public byte getLv() {
        return lv;
    }

    public void setLv(Byte lv) {
        this.lv = lv;
    }

    public void getMessage(Message message) {
        message.setShort(id);
        message.setByte(lv);
    }

}
