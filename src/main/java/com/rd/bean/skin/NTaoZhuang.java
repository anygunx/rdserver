package com.rd.bean.skin;

import java.util.ArrayList;
import java.util.List;

public class NTaoZhuang {
    private int type;
    private int level;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * 只是为了作为累加计数来用  还有就是给前端发消息
     */
    public List<Integer> getSkinList() {
        if (skinList == null) {
            skinList = new ArrayList<>();
        }
        return skinList;
    }

    public void setSkinList(List<Integer> skinList) {
        this.skinList = skinList;
    }

    /**
     * 只是为了作为累加计数来用  还有就是给前端发消息
     */
    private List<Integer> skinList;


}
