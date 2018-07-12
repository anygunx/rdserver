package com.rd.bean.card;

import com.rd.bean.comm.BanConstructor;
import com.rd.net.message.Message;

public class PlayerCardData {
    private short id;
    private short lv = -1;

    @BanConstructor
    public PlayerCardData() {
    }

    public PlayerCardData(short id, short lv) {
        this.id = id;
        this.lv = lv;
    }

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public short getLv() {
        return lv;
    }

    public void setLv(short lv) {
        this.lv = lv;
    }

    public void getMessage(Message message) {
        message.setShort(getId());
        message.setShort(getLv());
    }
}
