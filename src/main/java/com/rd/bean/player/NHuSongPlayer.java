package com.rd.bean.player;

import com.rd.game.manager.EscortManager;
import com.rd.model.NHuSongModel;
import com.rd.net.message.Message;

/*******
 * 玩家护送信息
 *
 * */
public class NHuSongPlayer extends SimplePlayer {

    //镖车开始时间
    private long startTime;

    //镖车星级
    private byte quality;

    private byte hurted;

    public byte getHurted() {
        return hurted;
    }

    public void setHurted(byte hurted) {
        this.hurted = hurted;
    }

    public void addHurted() {
        this.hurted += 1;
    }

    private NBiaoche nBiaoche;

    public NBiaoche getnBiaoche() {
        return nBiaoche;
    }

    public void setnBiaoche(NBiaoche nBiaoche) {
        this.nBiaoche = nBiaoche;
    }

    //外形
    private AppearPlayer appear = new AppearPlayer();

    public NHuSongPlayer(Player player) {
        init(player);
    }

    public NHuSongPlayer() {

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


    public long getShengYuTime() {
        long shengyu = 0;
        long curr = System.currentTimeMillis();
        if (this.startTime > 0) {
            shengyu = this.startTime + NHuSongModel.getNHuSongDataById(quality).getTime() * 1000 - curr;
        }
        return shengyu;
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
