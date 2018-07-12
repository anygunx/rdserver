package com.rd.model.data.skin;

import java.util.List;

/***
 * 套装数据
 * @author MyPC
 *
 */
public class NTaoZhuangData {
    private short level;

    private int[] attr;
    private List<Integer> activationList;

    public int getLevel() {
        return level;
    }

    public int[] getAttr() {
        return attr;
    }

    public List<Integer> getActivationList() {
        return activationList;
    }

    public NTaoZhuangData(short level, List<Integer> activationList, int[] attr) {
        this.level = level;
        this.activationList = activationList;
        this.attr = attr;
    }

}
