package com.rd.combat;

import com.rd.enumeration.EAttr;

/**
 * @author ---
 * @version 1.0
 * @date 2018年6月6日下午4:27:07
 */
public class Effect {

    private int originalDamage;

    private int[] originalAttr;

    private int damage;

    private int[] attr;

    public Effect() {

    }

    public Effect(int[] originalAttr) {
        this.originalAttr = originalAttr;
        this.attr = new int[EAttr.SIZE];
    }

    public int getOriginalDamage() {
        return originalDamage;
    }

    public void setOriginalDamage(int originalDamage) {
        this.originalDamage = originalDamage;
    }

    public int[] getOriginalAttr() {
        return originalAttr;
    }

    public void setOriginalAttr(int[] originalAttr) {
        this.originalAttr = originalAttr;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int[] getAttr() {
        return attr;
    }

    public void setAttr(int[] attr) {
        this.attr = attr;
    }

    public void addDamage(int damage) {
        this.damage += damage;
    }

    public int at(int index) {
        return originalAttr[index] + attr[index];
    }
}
