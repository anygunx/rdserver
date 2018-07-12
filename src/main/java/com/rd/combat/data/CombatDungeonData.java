package com.rd.combat.data;

/**
 * @author ---
 * @version 1.0
 * @date 2018年6月21日下午3:40:15
 */
public class CombatDungeonData {

    private int boss;

    private int[] monster;

    public CombatDungeonData(int boss, int[] monster) {
        this.boss = boss;
        this.monster = monster;
    }

    public int getBoss() {
        return boss;
    }

    public void setBoss(int boss) {
        this.boss = boss;
    }

    public int[] getMonster() {
        return monster;
    }

    public void setMonster(int[] monster) {
        this.monster = monster;
    }

}
