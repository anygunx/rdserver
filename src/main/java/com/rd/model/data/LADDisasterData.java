package com.rd.model.data;

import com.rd.bean.drop.DropData;
import com.rd.combat.data.CombatDungeonData;

import java.util.List;

/**
 * @author ---
 * @version 1.0
 * @date 2018年6月19日上午11:27:33
 */
public class LADDisasterData {

    private int id;

    private CombatDungeonData[] monster;

    private List<DropData> reward1;

    private List<DropData> reward2;

    private DropData rewardAssist;

    private DropData[] rewardBox;

    public LADDisasterData(int id, CombatDungeonData[] monster, List<DropData> reward1, List<DropData> reward2, DropData rewardAssist, DropData[] rewardBox) {
        this.id = id;
        this.monster = monster;
        this.reward1 = reward1;
        this.reward2 = reward2;
        this.rewardAssist = rewardAssist;
        this.rewardBox = rewardBox;
    }

    public int getId() {
        return id;
    }

    public CombatDungeonData[] getMonster() {
        return monster;
    }

    public List<DropData> getReward1() {
        return reward1;
    }

    public List<DropData> getReward2() {
        return reward2;
    }

    public DropData getRewardAssist() {
        return rewardAssist;
    }

    public DropData[] getRewardBox() {
        return rewardBox;
    }

}
