package com.rd.bean.faction;

import com.rd.bean.player.SimplePlayer;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.net.message.Message;

public class NFactionMember {


    /**
     * 简单玩家
     **/
    private SimplePlayer simplePlayer;

    /**
     * 公会id
     **/
    private int gangId;

    /**
     * 职位
     **/
    private byte position;

    /**
     * 捐献
     **/
    private int totalDonate;

    /**
     * 副本通关
     **/
    private short dungeonPass;

    public SimplePlayer getSimplePlayer() {
        return simplePlayer;
    }

    public void setSimplePlayer(SimplePlayer simplePlayer) {
        this.simplePlayer = simplePlayer;
    }

    public int getGangId() {
        return gangId;
    }

    public void setGangId(int gangId) {
        this.gangId = gangId;
    }

    public byte getPosition() {
        return position;
    }

    public void setPosition(byte position) {
        this.position = position;
    }

    public int getTotalDonate() {
        return totalDonate;
    }

    public void setTotalDonate(int totalDonate) {
        this.totalDonate = totalDonate;
    }

    public int getPlayerId() {
        return simplePlayer.getId();
    }

    public short getDungeonPass() {
        return dungeonPass;
    }

    public void setDungeonPass(short dungeonPass) {
        this.dungeonPass = dungeonPass;
    }

    public NFactionMember() {
        this.totalDonate = 0;
    }

    public NFactionMember(SimplePlayer simplePlayer, int gangId, byte position, short dungeonPass) {
        this();
        this.simplePlayer = simplePlayer;
        this.gangId = gangId;
        this.position = position;
        this.dungeonPass = dungeonPass;
    }

    public void getMessage(Message message) {
        this.simplePlayer.getSimpleMessage(message);
        message.setByte(this.position);
        message.setInt(this.totalDonate);
        long downLineTime = 0;
        GameRole role = GameWorld.getPtr().getOnlineRole(simplePlayer.getId());
        if (role == null) {

        }

        message.setLong(downLineTime);
    }

    public void addTotalDonate(int totalDonate) {
        this.totalDonate += totalDonate;
    }


}
