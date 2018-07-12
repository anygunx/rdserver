package com.rd.define;

public enum EFuntionType {

    AVATARTRAINACTIVE(1),        //神通培养激活
    AVATARTRAINLEVEL(2),        //神通培养升级
    AVATARTRAINSTAGE(3),        //神通培养升阶
    ;

    private final short value;

    public short getValue() {
        return value;
    }

    EFuntionType(int id) {
        this.value = (short) id;
    }
}
