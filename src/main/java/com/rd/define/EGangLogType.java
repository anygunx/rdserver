package com.rd.define;

public enum EGangLogType {

    JOIN(1),        //进入公会
    EXIT(2),        //退出公会
    DISMISS(3),        //踢出公会
    INCENSE(4),    //公会上香
    APPOINT(5),        //公会任命
    LEVELUP(6),    //公会升级
    BOSS(7),        //公会BOSS
    ASSIGN(8),        //分配奖励
    IMPEACHMENT(9),    //弹劾帮主
    ;

    private final byte value;

    public byte getValue() {
        return value;
    }

    EGangLogType(int id) {
        this.value = (byte) id;
    }
}
