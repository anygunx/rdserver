package com.rd.bean.player;

import com.rd.common.GameCommon;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;

public class Activity7Mission {

    //7日活动ID
    private short id;

    //进度
    private int p = 0;

    //领取进度
    private int l = 0;

    public Activity7Mission() {
    }

    public Activity7Mission(short id) {
        this.id = id;
    }

    public void getMessage(Message message, Player player) {
        message.setShort(id);
        int progress = getRealP();
        message.setInt(progress);
        message.setInt(l);
//      message.setInt(getRestTime(startTime, timeLimit, currentTime));
    }

    /**
     * 奖励是否已领取
     **/
    public boolean isReceived(int level) {
        return GameCommon.getBit2BooleanValue(l, level);
    }

    public void setReceive(int level) {
        l = GameCommon.setSubValue(l, GameCommon.True, level, level);
    }

    public int getRealP() {
        int prog = getP();
        return prog < 0 ? -prog : prog;
    }

    @SuppressWarnings("unused")
    private static int getRestTime(long startTime, int timeLimit, long currentTime) {
        long restTime;
        if (timeLimit == -1) {
            restTime = 0;
        } else {
            //剩余时间
            long passTime = currentTime - startTime;
            long missionTime = timeLimit * DateUtil.HOUR;
            restTime = missionTime > passTime ? missionTime - passTime : 0;
        }
        return (int) (restTime / DateUtil.SECOND);
    }

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public int getP() {
        return p;
    }

    public void setP(int p) {
        this.p = p;
    }

    public void addP(int add) {
        this.p += add;
    }

    public int getL() {
        return l;
    }

    public void setL(int l) {
        this.l = l;
    }

}
