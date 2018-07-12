package com.rd.model.data;

public class CardSuitLvModelData {
    private final short id;
    private final byte lv;
    private final short cardLv;
    private final double cardAddition;
    private final int[] attr;

    public CardSuitLvModelData(short id, byte lv, short cardLv, double cardAddition, int[] attr) {
        this.id = id;
        this.lv = lv;
        this.cardLv = cardLv;
        this.cardAddition = cardAddition;
        this.attr = attr;
    }

    public short getId() {
        return id;
    }

    public byte getLv() {
        return lv;
    }

    public short getCardLv() {
        return cardLv;
    }

    public int[] getAttr() {
        return attr;
    }

    public double getCardAddition() {
        return cardAddition;
    }
}
