package com.rd.bean.lianti;

import com.google.gson.annotations.SerializedName;
import com.rd.bean.comm.BanConstructor;
import com.rd.net.message.Message;

/**
 * 角色神兵数据
 * Created by XingYun on 2017/11/30.
 */
public class ShenBingData {
    private short id;
    @SerializedName("e")
    private int exp;

    @BanConstructor
    public ShenBingData() {
        id = 0;
        exp = 0;
    }

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getExp() {
        return exp;
    }

    public void getMessage(Message message) {
        message.setShort(id);
        message.setInt(exp);
    }
}
