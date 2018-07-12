package com.rd.bean.player;

import com.google.gson.annotations.SerializedName;

/**
 * 日常系统次数  领取 记录 封装
 */
public class NRCData {
    @SerializedName("zkct")
    private byte zhongdaoCopyCnt = 0;
    @SerializedName("tzkct")
    private byte totalZKCopyCnt = 0;
    @SerializedName("tzklq")
    private byte isLingqu = 0;


    //钟馗上次恢复时间
    @SerializedName("zkt")
    private long zhognkuitime = 0;

    /**
     * 野外战斗次数累计
     */
    @SerializedName("ywc")
    private short yewaiCount = 0;
    @SerializedName("ywlq")
    private short[] isYeWailingqu = null;


    /**
     * 组队副本次数
     */
    @SerializedName("tmcc")
    private short teamCopyCount = 0;

    @SerializedName("tmclq")
    private byte isTeamCopyLingqu = 0;


    public short getTeamCopyCount() {
        return teamCopyCount;
    }

    public void setTeamCopyCount(short teamCopyCount) {
        this.teamCopyCount = teamCopyCount;
    }

    public void addTeamCopyCount(short teamCopyCount) {
        this.teamCopyCount += teamCopyCount;
    }

    public byte getIsTeamCopyLingqu() {
        return isTeamCopyLingqu;
    }

    public void setIsTeamCopyLingqu(byte isTeamCopyLingqu) {
        this.isTeamCopyLingqu = isTeamCopyLingqu;
    }

    public short getYewaiCount() {
        return yewaiCount;
    }

    public void setYewaiCount(short yewaiCount) {
        this.yewaiCount = yewaiCount;
    }


    public short[] getIsYeWailingqu() {
        return isYeWailingqu;
    }

    public void setIsYeWailingqu(short[] isYeWailingqu) {
        this.isYeWailingqu = isYeWailingqu;
    }

    public void addYewaiCount(short yewaiCount) {
        this.yewaiCount += yewaiCount;
    }

    public byte getZhongdaoCopyCnt() {
        return zhongdaoCopyCnt;
    }

    public void setZhongdaoCopyCnt(byte zhongdaoCopyCnt) {
        this.zhongdaoCopyCnt = zhongdaoCopyCnt;
    }

    public void addZhongdaoCopyCnt() {
        --zhongdaoCopyCnt;
    }

    public void addZhongdaoCopyCnt(int count) {
        zhongdaoCopyCnt += count;
    }


    public long getZhognkuitime() {
        return zhognkuitime;
    }

    public void setZhognkuitime(long zhognkuitime) {
        this.zhognkuitime = zhognkuitime;
    }


    public byte getTotalZKCopyCnt() {
        return totalZKCopyCnt;
    }

    public void setTotalZKCopyCnt(byte totalZKCopyCnt) {
        this.totalZKCopyCnt = totalZKCopyCnt;
    }

    public void addTotalZKCopyCnt(byte totalZKCopyCnt) {
        this.totalZKCopyCnt += totalZKCopyCnt;
    }

    public byte getIsLingqu() {
        return isLingqu;
    }

    public void setIsLingqu(byte isLingqu) {
        this.isLingqu = isLingqu;
    }

}
