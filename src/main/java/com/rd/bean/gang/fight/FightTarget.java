package com.rd.bean.gang.fight;

import com.rd.bean.player.Player;

public class FightTarget {

    private Player player;

    private byte beStar;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public byte getBeStar() {
        return beStar;
    }

    public void setBeStar(byte beStar) {
        this.beStar = beStar;
    }

    public FightTarget(Player player) {
        this.player = player;
    }

    public void reset() {
        this.beStar = 0;
    }
}
