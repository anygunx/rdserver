package com.rd.bean.goods.data;

import com.rd.bean.drop.DropData;

import java.util.List;

public class BoxData extends GoodsData {

    private String name;

    private short levelLimit;

    private byte vipLimit;

    private List<DropData> rewards;

    private short gainId;

    private String startTime;

    private String endTime;

    private byte type;

    /**
     * 限时时间 (秒)
     **/
    private int lastTime;

    //每日使用次数限制	-1：表示无限制
    private int dayCount = -1;

    private int haveNum;

    public int getHaveNum() {
        return haveNum;
    }

    public void setHaveNum(int haveNum) {
        this.haveNum = haveNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short getLevelLimit() {
        return levelLimit;
    }

    public void setLevelLimit(short levelLimit) {
        this.levelLimit = levelLimit;
    }

    public byte getVipLimit() {
        return vipLimit;
    }

    public void setVipLimit(byte vipLimit) {
        this.vipLimit = vipLimit;
    }

    public List<DropData> getRewards() {
        return rewards;
    }

    public void setRewards(List<DropData> rewards) {
        this.rewards = rewards;
    }

    public short getGainId() {
        return gainId;
    }

    public void setGainId(short gainId) {
        this.gainId = gainId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public int getLastTime() {
        return lastTime;
    }

    public void setLastTime(int lastTime) {
        this.lastTime = lastTime;
    }

    public int getDayCount() {
        return dayCount;
    }

    public void setDayCount(int dayCount) {
        this.dayCount = dayCount;
    }

}
