package com.lg.bean.game;

import com.lg.bean.PlayerLog;

public class Function extends PlayerLog {

    private short type;

    private String record;

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public Function() {

    }

    public Function(short type, String record) {
        this.type = type;
        this.record = record;
    }
}
