package com.rd.bean.gang;

public class GangDungeonFirst {

    private int playerId;

    private String name;

    private byte head;

    private short pass;

    private short cheer = 0;

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getHead() {
        return head;
    }

    public void setHead(byte head) {
        this.head = head;
    }

    public short getPass() {
        return pass;
    }

    public void setPass(short pass) {
        this.pass = pass;
    }

    public short getCheer() {
        return cheer;
    }

    public void setCheer(short cheer) {
        this.cheer = cheer;
    }

    public void addCheer() {
        ++this.cheer;
    }
}
