package com.rd.bean.heartSkill;

import com.rd.net.message.Message;

public class HeartSkillSlot {

    private byte id;

    private byte level;

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public void getMessage(Message message) {
        message.setByte(id);
        message.setByte(level);
    }

    public void addLevel() {
        this.level += 1;
    }

    public void reset() {
        this.id = 0;
        this.level = 0;
    }
}
