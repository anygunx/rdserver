package com.rd.model.data;

import com.rd.bean.drop.DropData;

import java.util.List;

/**
 * @author ---
 * @version 1.0
 * @date 2018年6月11日上午11:47:09
 */
public class CrossDunData {

    private byte id;

    private short needLevel;

    private int boss;

    private int[] monster;

    private List<DropData> reward1;

    private List<DropData> reward2;

    public CrossDunData(byte id, short needLevel, int boss, int[] monster, List<DropData> reward1, List<DropData> reward2) {
        this.id = id;
        this.needLevel = needLevel;
        this.boss = boss;
        this.monster = monster;
        this.reward1 = reward1;
        this.reward2 = reward2;
    }

    public byte getId() {
        return id;
    }

    public short getNeedLevel() {
        return needLevel;
    }

    public int getBoss() {
        return boss;
    }

    public int[] getMonster() {
        return monster;
    }

    public List<DropData> getReward1() {
        return reward1;
    }

    public List<DropData> getReward2() {
        return reward2;
    }

}
