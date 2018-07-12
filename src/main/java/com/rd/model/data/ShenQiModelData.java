package com.rd.model.data;

import com.rd.bean.drop.DropData;

/**
 * 神器数据
 *
 * @author Created by U-Demon on 2016年11月2日 下午1:41:54
 * @version 1.0.0
 */
public class ShenQiModelData {

    private int id;

    private int lv;

    private String name;

    private DropData cost;

    private int pinzhi;

    private int addType;

    private int addPercent;

    private int[] attr;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLv() {
        return lv;
    }

    public void setLv(int lv) {
        this.lv = lv;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DropData getCost() {
        return cost;
    }

    public void setCost(DropData cost) {
        this.cost = cost;
    }

    public int getPinzhi() {
        return pinzhi;
    }

    public void setPinzhi(int pinzhi) {
        this.pinzhi = pinzhi;
    }

    public int getAddType() {
        return addType;
    }

    public void setAddType(int addType) {
        this.addType = addType;
    }

    public int getAddPercent() {
        return addPercent;
    }

    public void setAddPercent(int addPercent) {
        this.addPercent = addPercent;
    }

    public int[] getAttr() {
        return attr;
    }

    public void setAttr(int[] attr) {
        this.attr = attr;
    }

}
