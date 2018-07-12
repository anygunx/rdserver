package com.rd.model.data;

import com.rd.bean.drop.DropData;

/**
 * @author ---
 * @version 1.0
 * @date 2018年4月17日下午2:02:41
 */
public class MapStageData {

    private short id;

    private int boss;

    private int[] monster;

    private DropData exp;

    private DropData gold;

    private boolean isEnd;

    public MapStageData(short id, int boss, int[] monster, DropData exp, DropData gold, boolean isEnd) {
        this.id = id;
        this.boss = boss;
        this.monster = monster;
        this.exp = exp;
        this.gold = gold;
        this.isEnd = isEnd;
    }

    public short getId() {
        return id;
    }

    public int getBoss() {
        return boss;
    }

    public int[] getMonster() {
        return monster;
    }

    public DropData getExp() {
        return exp;
    }

    public DropData getGold() {
        return gold;
    }

    public boolean isEnd() {
        return isEnd;
    }

}
