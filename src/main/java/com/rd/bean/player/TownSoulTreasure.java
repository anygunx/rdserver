package com.rd.bean.player;

import com.alibaba.fastjson.annotation.JSONField;
import com.rd.bean.drop.DropData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 镇魂宝库
 *
 * @author ---
 * @version 1.0
 * @date 2018年3月9日下午5:05:53
 */
public class TownSoulTreasure {

    @JSONField(name = "l")
    private short lottery;

    @JSONField(name = "o")
    private List<Short> lotteryReward = new ArrayList<>();

    @JSONField(name = "t")
    private long lotteryTimeStamp;

    @JSONField(name = "u")
    private short lucky;

    @JSONField(name = "r")
    private List<Byte> probailityRecord = new ArrayList<>();

    @JSONField(name = "p")
    private byte probaility = 1;

    @JSONField(name = "a")
    private Map<Short, DropData> pack = new HashMap<>();

    public TownSoulTreasure() {

    }

    public short getLottery() {
        return lottery;
    }

    public void setLottery(short lottery) {
        this.lottery = lottery;
    }

    public List<Short> getLotteryReward() {
        return lotteryReward;
    }

    public void setLotteryReward(List<Short> lotteryReward) {
        this.lotteryReward = lotteryReward;
    }

    public long getLotteryTimeStamp() {
        return lotteryTimeStamp;
    }

    public void setLotteryTimeStamp(long lotteryTimeStamp) {
        this.lotteryTimeStamp = lotteryTimeStamp;
    }

    public short getLucky() {
        return lucky;
    }

    public void setLucky(short lucky) {
        this.lucky = lucky;
    }

    public List<Byte> getProbailityRecord() {
        return probailityRecord;
    }

    public void setProbailityRecord(List<Byte> probailityRecord) {
        this.probailityRecord = probailityRecord;
    }

    public byte getProbaility() {
        return probaility;
    }

    public void setProbaility(byte probaility) {
        this.probaility = probaility;
    }

    public Map<Short, DropData> getPack() {
        return pack;
    }

    public void setPack(Map<Short, DropData> pack) {
        this.pack = pack;
    }
}
