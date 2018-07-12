package com.rd.bean.player;

import com.rd.game.manager.EscortManager;
import com.rd.net.message.Message;

/**
 * 玩家镖车信息
 *
 * @author Created by U-Demon on 2016年11月30日 下午2:04:19
 * @version 1.0.0
 */
public class EscortPlayer extends SimplePlayer {

    //镖车开始时间
    private long startTime;

    //镖车星级
    private byte quality;

    //外形
    private AppearPlayer appear = new AppearPlayer();

    public EscortPlayer(Player player) {
        init(player);
    }

    public EscortPlayer() {

    }

    public void getEscortMessage(Message msg) {
        super.getSimpleMessage(msg);
        msg.setByte(quality);
        int sec = 0;
        long curr = System.currentTimeMillis();
        if (this.startTime > 0)
            sec = (int) ((this.startTime + EscortManager.getEscortKeepTime(quality) - curr) / 1000);
        msg.setInt(sec);
        appear.getMessage(msg);
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public byte getQuality() {
        return quality;
    }

    public void setQuality(byte quality) {
        this.quality = quality;
    }

    public AppearPlayer getAppear() {
        return appear;
    }

    public void setAppear(AppearPlayer appear) {
        this.appear = appear;
    }

}
