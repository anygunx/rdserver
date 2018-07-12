package com.rd.bean.skin;

import java.util.ArrayList;
import java.util.List;

public class NSkin {
    private int type;

    private List<Integer> huanHuaList;

    private int currHZId;

    public int getType() {

        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<Integer> getHuanHuaList() {
        if (huanHuaList == null) {
            huanHuaList = new ArrayList<>();
        }
        return huanHuaList;
    }

    public void setHuanHuaListt(List<Integer> huanzhuangList) {
        this.huanHuaList = huanzhuangList;
    }

    public int getCurrHZId() {
        return currHZId;
    }

    public void setCurrHZId(int currHZId) {
        this.currHZId = currHZId;
    }

    public boolean isHaveHuanHuaId(int modelId) {
        if (huanHuaList == null || huanHuaList.isEmpty()) {
            return false;
        }
        for (Integer hzid : huanHuaList) {
            if (hzid == modelId) {
                return true;
            }
        }
        return false;
    }

}
