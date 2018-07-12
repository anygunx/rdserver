package com.rd.bean.pvp;

import com.rd.define.GameDefine;

/**
 * 跨服战的一些周边数据
 *
 * @author U-Demon Created on 2017年5月19日 下午1:48:53
 * @version 1.0.0
 */
public class CrossData {

    //玩家ID
    private int playerId;

    //竞技场剩余次数
    private int arenaCount = GameDefine.ARENA_COUNT;

    //竞技场购买了多少次
    private int arenaBuy = 0;

    public CrossData() {

    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getArenaCount() {
        return arenaCount;
    }

    public void setArenaCount(int arenaCount) {
        this.arenaCount = arenaCount;
    }

    public void addArenaCount(int add) {
        this.arenaCount += add;
    }

    public int getArenaBuy() {
        return arenaBuy;
    }

    public void setArenaBuy(int arenaBuy) {
        this.arenaBuy = arenaBuy;
    }

    public void addArenaBuy(int add) {
        this.arenaBuy += add;
    }

}
