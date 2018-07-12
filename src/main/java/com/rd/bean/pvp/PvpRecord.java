package com.rd.bean.pvp;

import com.alibaba.fastjson.annotation.JSONField;
import com.rd.net.message.Message;

public class PvpRecord {

    private String name;

    private byte result;

    private String time;

    private byte meltSoulStone;

    private short exp;

    private short gold;

    private byte prestige;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getResult() {
        return result;
    }

    public void setResult(byte result) {
        this.result = result;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public byte getMeltSoulStone() {
        return meltSoulStone;
    }

    public void setMeltSoulStone(byte meltSoulStone) {
        this.meltSoulStone = meltSoulStone;
    }

    public short getExp() {
        return exp;
    }

    public void setExp(short exp) {
        this.exp = exp;
    }

    public short getGold() {
        return gold;
    }

    public void setGold(short gold) {
        this.gold = gold;
    }

    public byte getPrestige() {
        return prestige;
    }

    public void setPrestige(byte prestige) {
        this.prestige = prestige;
    }

    @JSONField(serialize = false)
    public void getMessage(Message message) {
        message.setString(name);
        message.setByte(result);
        message.setString(time);
        message.setByte(meltSoulStone);
        message.setShort(exp);
        message.setShort(gold);
        message.setByte(prestige);
    }
}
