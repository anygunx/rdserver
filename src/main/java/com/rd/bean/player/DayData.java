package com.rd.bean.player;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;
import com.rd.bean.gang.GangMission;
import com.rd.common.GameCommon;
import com.rd.define.GameDefine;
import com.rd.define.TeamDef;
import com.rd.util.DateUtil;

import java.util.*;

/**
 * <p>Title: 每日数据</p>
 * <p>Description: 为了方便记录每日需要清除的数据</p>
 * <p>Company: 北京万游畅想科技有限公司</p>
 *
 * @author ---
 * @version 1.0
 * @data 2016年12月13日 下午3:53:25
 */
public class DayData {


    /**
     * 帮会任务列表
     **/
    private List<GangMission> gangMissionList = new ArrayList<>();

    public List<GangMission> getGangMissionList() {
        return gangMissionList;
    }

    public void setGangMissionList(List<GangMission> gangMissionList) {
        this.gangMissionList = gangMissionList;
    }

    /***帮派每日获取奖励**/
    public Set<Integer> factionMeiRiTaskReward = new HashSet<>();
    /**
     * 普通上香次数
     **/
    private byte incense = 0;
    private int xiangHuo = 0;
    private byte jJCCnt = 5;
    private byte jJbuyCnt = 0;

    public byte getjJbuyCnt() {
        return jJbuyCnt;
    }

    public void setjJbuyCnt(byte jJbuyCnt) {
        this.jJbuyCnt = jJbuyCnt;
    }

    public void addjJbuyCnt() {
        ++jJbuyCnt;
    }

    private long jJTime = 0;

    public long getjJTime() {
        return jJTime;
    }

    public void setjJTime(long jJTime) {
        this.jJTime = jJTime;
    }

    public byte getjJCCnt() {
        return jJCCnt;
    }

    public void setjJCCnt(byte jJCCnt) {
        this.jJCCnt = jJCCnt;
    }

    public void addjJCCnt(byte jJCCnt) {
        this.jJCCnt += jJCCnt;
    }

    public void costJJCCnt() {
        --jJCCnt;
    }

    private byte[] sxRewards = null;

    public byte[] getSxRewards() {
        return sxRewards;
    }

    public void setSxRewards(byte[] sxRewards) {
        this.sxRewards = sxRewards;
    }

    public Set<Integer> getFactionMeiRiTaskReward() {
        return factionMeiRiTaskReward;
    }

    public void setFactionMeiRiTaskReward(Set<Integer> factionMeiRiTaskReward) {
        this.factionMeiRiTaskReward = factionMeiRiTaskReward;
    }


    public byte getIncense() {
        return incense;
    }

    public void setIncense(byte incense) {
        this.incense = incense;
    }

    public int getXiangHuo() {
        return xiangHuo;
    }

    public void setXiangHuo(int xiangHuo) {
        this.xiangHuo = xiangHuo;
    }

    /**************************************以下是旧版本的  以上新版本************************************************************/


    /**
     * 使用boss挑战令次数
     */
    private byte bossDekaronOrder = 0;


    /**
     * vip上香次数
     **/
    private byte incenseVip = 0;

    /**
     * 法宝转盘次数
     **/
    private byte magicTurntable = 0;

    /**
     * 转生等级兑换次数
     **/
    private byte reinEx = 0;

    /**
     * 宝物购买次数
     **/
    private Map<Short, Integer> boxUsed = new HashMap<>();

    /**
     * 帮会转盘已转次数
     **/
    private byte gangTurnableNum = 0;


    /**
     * 帮会副本每日通关奖励
     **/
    private boolean isGangDungeonRecv = false;

    /**
     * 是否助威
     **/
    private boolean isGangDungeonCheer = false;

    /**
     * 宠物免费抽
     **/
    private byte petFree = 1;

    /**
     * 五行每日挑战次数
     **/
    private byte fiveCh = 1;//100测试数据

    /**
     * 五行活动每日花元宝挑战次数
     **/
    private byte fiveCostGoldCh = 5;

    /**
     * 灵髓扫荡次数
     **/
    private short lingsuiSweep = 0;

    /**
     * 灵髓每日闯关次数
     **/
    private short lingsuiBattleTimes = (short) (DateUtil.getDistanceDay((GameDefine.SERVER_CREATE_TIME + 4 * 24 * 60 * 60 * 1000), System.currentTimeMillis()) * 5);

    /**
     * 龙珠日常任务
     **/
    @SerializedName("dbp")
    private List<Short> dragonballProcess = new ArrayList<>();
    /**
     * 龙珠宝箱掩码
     */
    @SerializedName("dbbm")
    private Byte dragonballBoxMask = 0;

    /**
     * 排行榜膜拜
     **/
    @SerializedName("trw")
    private Map<Byte, Boolean> topRankWorship = new HashMap<>();

    /**
     * 野外pvp奖励次数
     **/
    @SerializedName("fprn")
    private byte fieldPvpRewardNum = 0;

    private byte gangReward = 0;


    //跨服副本每天默认30次
    @JSONField(name = "cdn")
    private byte crossDunNum = 30;

    private byte laddAssistNum = TeamDef.LADD_ASSIST_NUM;

    private Map<Byte, List<Byte>> laddTreasureBox = new HashMap<>();

    private Map<Byte, List<Byte>> laddSweep = new HashMap<>();

    public Map<Byte, List<Byte>> getLaddTreasureBox() {
        return laddTreasureBox;
    }

    public void setLaddTreasureBox(Map<Byte, List<Byte>> laddTreasureBox) {
        this.laddTreasureBox = laddTreasureBox;
    }

    public Map<Byte, List<Byte>> getLaddSweep() {
        return laddSweep;
    }

    public void setLaddSweep(Map<Byte, List<Byte>> laddSweep) {
        this.laddSweep = laddSweep;
    }

    public void subLaddAssistNum() {
        if (this.laddAssistNum > 0)
            --this.laddAssistNum;
    }

    public byte getLaddAssistNum() {
        return laddAssistNum;
    }

    public void setLaddAssistNum(byte laddAssistNum) {
        this.laddAssistNum = laddAssistNum;
    }

    public byte getCrossDunNum() {
        return crossDunNum;
    }

    public void setCrossDunNum(byte crossDunNum) {
        this.crossDunNum = crossDunNum;
    }

    public void subCrossDunNum() {
        if (this.crossDunNum > 0)
            --this.crossDunNum;
    }


    public byte getFieldPvpRewardNum() {
        return fieldPvpRewardNum;
    }

    public void setFieldPvpRewardNum(byte fieldPvpRewardNum) {
        this.fieldPvpRewardNum = fieldPvpRewardNum;
    }

    public byte getBossDekaronOrder() {
        return bossDekaronOrder;
    }

    public void setBossDekaronOrder(byte bossDekaronOrder) {
        this.bossDekaronOrder = bossDekaronOrder;
    }

    public void addBossDekaronOrder(int num) {
        this.bossDekaronOrder += num;
    }


    public byte getIncenseVip() {
        return incenseVip;
    }

    public void setIncenseVip(byte incenseVip) {
        this.incenseVip = incenseVip;
    }

    public byte getMagicTurntable() {
        return magicTurntable;
    }

    public void setMagicTurntable(byte magicTurntable) {
        this.magicTurntable = magicTurntable;
    }

    public byte getReinEx() {
        return reinEx;
    }

    public void setReinEx(byte reinEx) {
        this.reinEx = reinEx;
    }

    public void addReinEx() {
        this.reinEx++;
    }

    public Map<Short, Integer> getBoxUsed() {
        return boxUsed;
    }

    public void setBoxUsed(Map<Short, Integer> boxUsed) {
        this.boxUsed = boxUsed;
    }

    public byte getGangTurnableNum() {
        return gangTurnableNum;
    }

    public void setGangTurnableNum(byte gangTurnableNum) {
        this.gangTurnableNum = gangTurnableNum;
    }


    public boolean isGangDungeonRecv() {
        return isGangDungeonRecv;
    }

    public void setGangDungeonRecv(boolean isGangDungeonRecv) {
        this.isGangDungeonRecv = isGangDungeonRecv;
    }

    public boolean isGangDungeonCheer() {
        return isGangDungeonCheer;
    }

    public void setGangDungeonCheer(boolean isGangDungeonCheer) {
        this.isGangDungeonCheer = isGangDungeonCheer;
    }

    public byte getPetFree() {
        return petFree;
    }

    public void setPetFree(int petFree) {
        this.petFree = (byte) petFree;
    }

    public byte getFiveCh() {
        return fiveCh;
    }

    public void setFiveCh(byte fiveCh) {
        this.fiveCh = fiveCh;
    }

    public List<Short> getDragonballProcess() {
        return dragonballProcess;
    }

    public void setDragonballProcess(List<Short> dragonballProcess) {
        this.dragonballProcess = dragonballProcess;
    }

    public void setDragonballProcess(short id, short value) {
        int idx = id - 1;
        this.dragonballProcess.set(idx, value);
    }

    public short getDragonballProcess(short id) {
        int idx = id - 1;
        return this.dragonballProcess.get(idx);
    }

    public Byte getDragonballBoxMask() {
        return dragonballBoxMask;
    }

    public void setDragonballBoxMask(Byte dragonballBoxMask) {
        this.dragonballBoxMask = dragonballBoxMask;
    }

    public void addDragonBallBox(byte boxId) {
        dragonballBoxMask = (byte) GameCommon.setSubValue(dragonballBoxMask, GameCommon.True, boxId, boxId);
    }

    /**
     * 龙珠宝箱是否领取
     *
     * @return
     */
    public boolean isBoxReceived(byte boxId) {
        return GameCommon.getBit2BooleanValue(dragonballBoxMask, boxId);
    }

    public Map<Byte, Boolean> getTopRankWorship() {
        return topRankWorship;
    }

    public void setTopRankWorship(Map<Byte, Boolean> topRankWorship) {
        this.topRankWorship = topRankWorship;
    }

    public byte getGangReward() {
        return gangReward;
    }

    public void setGangReward(byte gangReward) {
        this.gangReward = gangReward;
    }

    public byte getFiveCostGoldCh() {
        return fiveCostGoldCh;
    }

    public void setFiveCostGoldCh(byte fiveCostGoldCh) {
        this.fiveCostGoldCh = fiveCostGoldCh;
    }

    public short getLingsuiSweep() {
        return lingsuiSweep;
    }

    public void setLingsuiSweep(short lingsuiSweep) {
        this.lingsuiSweep = lingsuiSweep;
    }

    public short getLingsuiBattleTimes() {
        return lingsuiBattleTimes;
    }

    public void setLingsuiBattleTimes(short lingsuiBattleTimes) {
        this.lingsuiBattleTimes = lingsuiBattleTimes;
    }
}
