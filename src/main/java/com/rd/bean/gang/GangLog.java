package com.rd.bean.gang;

public class GangLog {

    private byte type;

    private String firstName;

    private String secondName;

    private byte byteData;

    private long time;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    private short boxId;

    private short boxNum;

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public byte getByteData() {
        return byteData;
    }

    public void setByteData(byte byteData) {
        this.byteData = byteData;
    }

    public short getBoxId() {
        return boxId;
    }

    public void setBoxId(short boxId) {
        this.boxId = boxId;
    }

    public short getBoxNum() {
        return boxNum;
    }

    public void setBoxNum(short boxNum) {
        this.boxNum = boxNum;
    }

    public GangLog() {

    }

    public GangLog(byte type) {
        this.type = type;
    }

    public GangLog(byte type, String firstName) {
        this.type = type;
        this.firstName = firstName;
    }

    public GangLog(byte type, String firstName, String secondName) {
        this.type = type;
        this.firstName = firstName;
        this.secondName = secondName;
        this.time = System.currentTimeMillis();
    }

    public GangLog(byte type, String firstName, byte data) {
        this.type = type;
        this.firstName = firstName;
        this.byteData = data;
        this.time = System.currentTimeMillis();
    }

    public GangLog(byte type, String firstName, String secondName, byte data) {
        this.type = type;
        this.firstName = firstName;
        this.secondName = secondName;
        this.byteData = data;
    }

    public GangLog(byte type, byte data) {
        this.type = type;
        this.byteData = data;
    }

    public GangLog(byte type, String firstName, String secondName, short boxId, short boxNum) {
        this.type = type;
        this.firstName = firstName;
        this.secondName = secondName;
        this.boxId = boxId;
        this.boxNum = boxNum;
    }
}
