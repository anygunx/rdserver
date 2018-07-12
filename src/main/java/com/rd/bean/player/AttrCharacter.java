package com.rd.bean.player;

import com.rd.define.EAttrType;
import com.rd.net.message.Message;

public class AttrCharacter {

    /**
     * 职业
     */
    private byte occ = 0;

    /**
     * 战斗力
     */
    private long fighting = 0;

    /**
     * 角色属性
     */
    private int[] attribute = new int[EAttrType.ATTR_SIZE];

    public void getMessage(Message msg) {
        msg.setLong(this.fighting);
        for (int value : this.attribute) {
            msg.setInt(value);
        }
    }

    public byte getOcc() {
        return occ;
    }

    public void setOcc(byte occ) {
        this.occ = occ;
    }

    public long getFighting() {
        return fighting;
    }

    public void setFighting(long fighting) {
        this.fighting = fighting;
    }

    public int[] getAttribute() {
        return attribute;
    }

    public void setAttribute(int[] attribute) {
        this.attribute = attribute;
    }

}
