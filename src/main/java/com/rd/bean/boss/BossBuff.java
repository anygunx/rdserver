package com.rd.bean.boss;

/**
 * BUFF
 *
 * @author Created by U-Demon on 2016年11月25日 下午2:32:11
 * @version 1.0.0
 */
public class BossBuff {

    //玩家ID
    private int playerId;

    //BUFF层数
    private int num;

    //第一个BUFF的时间
    private long firstTime;

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public long getFirstTime() {
        return firstTime;
    }

    public void setFirstTime(long firstTime) {
        this.firstTime = firstTime;
    }

}
