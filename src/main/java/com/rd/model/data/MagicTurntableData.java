package com.rd.model.data;

import com.rd.bean.drop.DropData;

public class MagicTurntableData {

    private byte id;

    private DropData itemCost;

    private byte[] multiplyValue;

    private short[] multiplyChance;

    private byte[] numValue;

    private short[] numChance;

    private DropData rewardData;

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public DropData getItemCost() {
        return itemCost;
    }

    public void setItemCost(DropData itemCost) {
        this.itemCost = itemCost;
    }

    public byte[] getMultiplyValue() {
        return multiplyValue;
    }

    public void setMultiplyValue(byte[] multiplyValue) {
        this.multiplyValue = multiplyValue;
    }

    public short[] getMultiplyChance() {
        return multiplyChance;
    }

    public void setMultiplyChance(short[] multiplyChance) {
        this.multiplyChance = multiplyChance;
    }

    public byte[] getNumValue() {
        return numValue;
    }

    public void setNumValue(byte[] numValue) {
        this.numValue = numValue;
    }

    public short[] getNumChance() {
        return numChance;
    }

    public void setNumChance(short[] numChance) {
        this.numChance = numChance;
    }

    public DropData getRewardData() {
        return rewardData;
    }

    public void setRewardData(DropData rewardData) {
        this.rewardData = rewardData;
    }
}
