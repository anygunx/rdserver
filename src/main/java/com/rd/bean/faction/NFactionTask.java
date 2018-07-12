package com.rd.bean.faction;

import com.alibaba.fastjson.annotation.JSONField;
import com.rd.net.message.Message;

public class NFactionTask {

    private byte id;

    private byte progress;

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public byte getProgress() {
        return progress;
    }

    public void setProgress(byte progress) {
        this.progress = progress;
    }

    @JSONField(serialize = false)
    public void getMessage(Message message) {
        message.setByte(id);
        message.setByte(progress);
    }
}
