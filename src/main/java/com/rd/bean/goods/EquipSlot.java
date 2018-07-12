package com.rd.bean.goods;

import com.rd.net.message.Message;

/**
 * 装备槽
 *
 * @author Created by U-Demon on 2016年10月27日 下午3:08:55
 * @version 1.0.0
 */
public class EquipSlot {

    //注灵等级
    private int zl = 0;

    //强化等级
    private int str = 0;

    //宝石等级
    private int j = 0;

    //铸魂
    private byte zh = 0;

    //淬炼
    private byte cl = 0;

    //经验
    private int cle = 0;

    public void getMessage(Message message) {
        message.setInt(zl);
        message.setInt(str);
        message.setInt(j);
        message.setByte(zh);
        message.setByte(cl);
        message.setInt(cle);
    }

    public int getZl() {
        return zl;
    }

    public void setZl(int zl) {
        this.zl = zl;
    }

    public void addZl(int lv) {
        this.zl += lv;
    }

    public int getStr() {
        return str;
    }

    public void setStr(int str) {
        this.str = str;
    }

    public void addStr(int lv) {
        this.str += lv;
    }

    public int getJ() {
        return j;
    }

    public void setJ(int j) {
        this.j = j;
    }

    public void addJ() {
        this.j++;
    }

    public byte getZh() {
        return zh;
    }

    public void setZh(byte zh) {
        this.zh = zh;
    }

    public byte getCl() {
        return cl;
    }

    public void setCl(byte cl) {
        this.cl = cl;
    }

    public int getCle() {
        return cle;
    }

    public void setCle(int cle) {
        this.cle = cle;
    }
}
