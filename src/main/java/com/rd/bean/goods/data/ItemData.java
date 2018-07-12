package com.rd.bean.goods.data;

public class ItemData extends GoodsData {

    private String name;

    /**
     * 限时时间 (毫秒)
     **/
    private int lastTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLastTime() {
        return lastTime;
    }

    public void setLastTime(int lastTime) {
        this.lastTime = lastTime;
    }

    public ItemData() {

    }
}
