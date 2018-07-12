package com.rd.model.data;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import com.rd.bean.drop.DropData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 怪物攻城模板数据
 */
public class MonsterSiegeModelData {
    private final short id;
    private final byte day;
    private final short fighterId;
    /**
     * 逃跑时间
     **/
    private final int escapeTime;
    /**
     * 复活时间
     **/
    private final int deadTime;
    /**
     * 攻击次数
     **/
    private final int attackTimes;
    /**
     * 排行积分奖励
     **/
    private final Map<Integer, Integer> scores;
    /**
     * 排行资源奖励
     **/
    private final ListMultimap<Integer, DropData> rewards;
    private String title;
    private String content;

    public MonsterSiegeModelData(short id, byte day, short fighter, int escapeTime, int deadTime,
                                 int attackTimes, HashMap<Integer, Integer> scores,
                                 String title, String content, ArrayListMultimap<Integer, DropData> rewards) {
        this.id = id;
        this.day = day;
        this.fighterId = fighter;
        this.escapeTime = escapeTime;
        this.deadTime = deadTime;
        this.attackTimes = attackTimes;
        this.scores = ImmutableMap.copyOf(scores);
        this.title = title;
        this.content = content;
        this.rewards = ImmutableListMultimap.copyOf(rewards);
    }

    public short getId() {
        return id;
    }

    public byte getDay() {
        return day;
    }

    public short getFighterId() {
        return fighterId;
    }

    public int getEscapeTime() {
        return escapeTime;
    }

    public int getDeadTime() {
        return deadTime;
    }

    public int getAttackTimes() {
        return attackTimes;
    }

    public int getScore(int rank) {
        return scores.get(rank);
    }

    public List<DropData> getRewardList(int rank) {
        return rewards.get(rank);
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
