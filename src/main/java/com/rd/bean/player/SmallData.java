package com.rd.bean.player;

import com.alibaba.fastjson.annotation.JSONField;
import com.rd.bean.goods.TimeGoods;
import com.rd.define.SectionDefine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 小数据存储
 * 仅用于存储不是很重要的零散数据，禁止存储重要数据
 *
 * @author ---
 */
public class SmallData {

    /**
     * 新手引导进度
     **/
    private List<Byte> noviceGuide = new ArrayList<>();

    /**
     * 限时物品列表
     **/
    private List<TimeGoods> timeGoodsList = new ArrayList<>();

    /**
     * 使用祝福瓶集合
     **/
    private Set<String> useBlessBottle = new HashSet<>();

    /**
     * 法宝升级次数
     **/
    private byte magicLevelUpNum = SectionDefine.MAGIC_LEVELUP_MAX_NUM;
    /**
     * 法宝升级时间
     **/
    private long magicLevelUpTime = 0;

    /**
     * 坐骑升星时间
     **/
    private long horseTime = 0;

    /**
     * 是否发送桌面
     **/
    private byte sendDesk = 0;

    /**
     * 限时任务提示小广告是否已显示
     **/
    private byte ltTipShow = 0;

    /**
     * 状态记录
     **/
    private Set<Byte> stateR = new HashSet<Byte>();

    /**
     * 五行升级次数
     **/
    private byte fiveElementUpNum = SectionDefine.FIVE_ELEMENTS_UP_MAX_TIMES;

    /**
     * 五行升级时间
     **/
    private long fiveElementUpTime = 0;

    /**
     * 五行重置次数时间
     **/
    private long fiveRestTime = 0;

    /**
     * 五行副本的状态
     **/
    private byte state = 2;

    /**
     * 五行副本领奖状态
     **/
    private byte fiveState = 0;

    private long fiveFuseTime = 0;

    /**
     * 灵髓开启奖励是否发放
     **/
    private boolean sr = false;

    /**
     * 等级直升丹购买
     */
    @JSONField(name = "l")
    private boolean limitLevelBuy = false;

    /**
     * 等级直升丹购买倒计时
     */
    @JSONField(name = "i")
    private long limitLevelStamp = 0;

    private List<Byte> crossTeamStageState = new ArrayList<>();

    public Set<Byte> getStateR() {
        return stateR;
    }

    public void setStateR(Set<Byte> stateR) {
        this.stateR = stateR;
    }

    public List<Byte> getNoviceGuide() {
        return noviceGuide;
    }

    public void setNoviceGuide(List<Byte> noviceGuide) {
        this.noviceGuide = noviceGuide;
    }

    public List<TimeGoods> getTimeGoodsList() {
        return timeGoodsList;
    }

    public void setTimeGoodsList(List<TimeGoods> timeGoodsList) {
        this.timeGoodsList = timeGoodsList;
    }

    public Set<String> getUseBlessBottle() {
        return useBlessBottle;
    }

    public void setUseBlessBottle(Set<String> useBlessBottle) {
        this.useBlessBottle = useBlessBottle;
    }

    public byte getMagicLevelUpNum() {
        return magicLevelUpNum;
    }

    public void setMagicLevelUpNum(byte magicLevelUpNum) {
        this.magicLevelUpNum = magicLevelUpNum;
    }

    public long getMagicLevelUpTime() {
        return magicLevelUpTime;
    }

    public void setMagicLevelUpTime(long magicLevelUpTime) {
        this.magicLevelUpTime = magicLevelUpTime;
    }

    public long getHorseTime() {
        return horseTime;
    }

    public void setHorseTime(long horseTime) {
        this.horseTime = horseTime;
    }

    public byte getSendDesk() {
        return sendDesk;
    }

    public void setSendDesk(byte sendDesk) {
        this.sendDesk = sendDesk;
    }

    public byte getLtTipShow() {
        return ltTipShow;
    }

    public void setLtTipShow(byte ltTipShow) {
        this.ltTipShow = ltTipShow;
    }

    public byte getFiveElementUpNum() {
        return fiveElementUpNum;
    }

    public void setFiveElementUpNum(byte fiveElementUpNum) {
        this.fiveElementUpNum = fiveElementUpNum;
    }

    public long getFiveElementUpTime() {
        return fiveElementUpTime;
    }

    public void setFiveElementUpTime(long fiveElementUpTime) {
        this.fiveElementUpTime = fiveElementUpTime;
    }

    public long getFiveRestTime() {
        return fiveRestTime;
    }

    public void setFiveRestTime(long fiveRestTime) {
        this.fiveRestTime = fiveRestTime;
    }

    public byte getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }

    public byte getFiveState() {
        return fiveState;
    }

    public void setFiveState(byte fiveState) {
        this.fiveState = fiveState;
    }

    public long getFiveFuseTime() {
        return fiveFuseTime;
    }

    public void setFiveFuseTime(long fiveFuseTime) {
        this.fiveFuseTime = fiveFuseTime;
    }

    public boolean isSr() {
        return sr;
    }

    public void setSr(boolean sr) {
        this.sr = sr;
    }

    public boolean isLimitLevelBuy() {
        return limitLevelBuy;
    }

    public void setLimitLevelBuy(boolean limitLevelBuy) {
        this.limitLevelBuy = limitLevelBuy;
    }

    public long getLimitLevelStamp() {
        return limitLevelStamp;
    }

    public void setLimitLevelStamp(long limitLevelStamp) {
        this.limitLevelStamp = limitLevelStamp;
    }

    public List<Byte> getCrossTeamStageState() {
        return crossTeamStageState;
    }

    public void setCrossTeamStageState(List<Byte> crossTeamStageState) {
        this.crossTeamStageState = crossTeamStageState;
    }

}
