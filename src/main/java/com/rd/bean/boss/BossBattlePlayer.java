package com.rd.bean.boss;

import com.rd.bean.player.Player;
import com.rd.bean.player.SimplePlayer;

/**
 * 参加BOSS站玩家信息
 *
 * @author Created by U-Demon on 2016年11月25日 下午1:39:34
 * @version 1.0.0
 */
public class BossBattlePlayer extends SimplePlayer implements Comparable<BossBattlePlayer> {

    //参战次数
    private short count;

    //复活次数
    private int relive;

    //上次战斗结束时间
    private long lastTime;

    //造成的伤害总量
    private long damage;

    //死亡时间
    private long deadTime = 0;

    private long deadBossTime = 0;

    public long getDeadBossTime() {
        return deadBossTime;
    }

    public void setDeadBossTime(long deadBossTime) {
        this.deadBossTime = deadBossTime;
    }

    public BossBattlePlayer(Player player) {
        super.init(player);
        this.count = 0;
        this.lastTime = 0;
        this.damage = 0;
        this.relive = 0;
    }

    public BossBattlePlayer(SimplePlayer player) {
        setId(player.getId());
        setName(player.getName());
        setRein(player.getRein());
        setLevel(player.getLevel());
        setVip(player.getVip());
        setFighting(player.getFighting());
        this.count = 0;
        this.lastTime = 0;
        this.damage = 0;
        this.relive = 0;
    }

    public short getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = (short) count;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public long getDamage() {
        return damage;
    }

    public void addDamage(long damage) {
        this.damage += damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getRelive() {
        return relive;
    }

    public void setRelive(int relive) {
        this.relive = relive;
    }

    public void addRelive() {
        this.relive++;
    }

    public long getDeadTime() {
        return deadTime;
    }

    public void setDeadTime(long deadTime) {
        this.deadTime = deadTime;
    }

    @Override
    public int compareTo(BossBattlePlayer o) {
        if (this.getDamage() > o.getDamage())
            return -1;
        else
            return 1;
    }

}
