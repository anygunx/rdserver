package com.rd.define;

public enum NfactionLogType {
    JOIN(1),        //进入公会
    EXIT(2),        //退出公会
    DISMISS(3),        //踢出公会
    INCENSE(4);    //公会上香


    private final byte value;

    public byte getValue() {
        return value;
    }

    NfactionLogType(int id) {
        this.value = (byte) id;
    }

}
