package com.rd.bean.data;

import com.alibaba.fastjson.annotation.JSONField;
import com.rd.net.message.Message;

public class LevelExpData extends LevelData {

    private int exp;

    public LevelExpData() {

    }

    public LevelExpData(byte id, short level, int exp) {
        super(id, level);
        this.exp = exp;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    @JSONField(serialize = false)
    public void getMessage(Message message) {
        super.getMessage(message);
        message.setInt(exp);
    }
}
