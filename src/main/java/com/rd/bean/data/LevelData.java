package com.rd.bean.data;

import com.alibaba.fastjson.annotation.JSONField;
import com.rd.net.message.Message;

public class LevelData {

    private byte id;

    private short level;

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public short getLevel() {
        return level;
    }

    public void setLevel(short level) {
        this.level = level;
    }

    public LevelData() {

    }

    public LevelData(byte id, short level) {
        this.id = id;
        this.level = level;
    }

    @JSONField(serialize = false)
    public void getMessage(Message message) {
        message.setByte(id);
        message.setShort(level);
    }
}
