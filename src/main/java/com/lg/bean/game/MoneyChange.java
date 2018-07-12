package com.lg.bean.game;

import com.lg.bean.PlayerLog;

/**
 * Created by XingYun on 2016/6/15.
 */
public class MoneyChange extends PlayerLog {
    private byte moneyType;
    private int addValue;
    private int changeType;

    public MoneyChange() {
    }

    public MoneyChange(byte moneyType, int addValue, int changeType) {
        this.moneyType = moneyType;
        this.addValue = addValue;
        this.changeType = changeType;
    }

    public byte getMoneyType() {
        return moneyType;
    }

    public void setMoneyType(byte moneyType) {
        this.moneyType = moneyType;
    }

    public int getAddValue() {
        return addValue;
    }

    public void setAddValue(int addValue) {
        this.addValue = addValue;
    }

    public int getChangeType() {
        return changeType;
    }

    public void setChangeType(int changeType) {
        this.changeType = changeType;
    }
}
