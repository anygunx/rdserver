package com.rd.bean.gang;

import com.alibaba.fastjson.annotation.JSONField;
import com.rd.bean.player.SimplePlayer;
import com.rd.common.GangBossService;
import com.rd.util.DateUtil;

/**
 * 公会BOSS
 *
 * @author U-Demon Created on 2017年4月13日 下午1:38:38
 * @version 1.0.0
 */
public class GangBoss {

    //ID
    private byte id;

    //死亡时间
    private long deadTime = 0;

    //战斗开始时间
    private long fightStartTime = 0;

    //当前战斗玩家
    private SimplePlayer battle = null;

    /**
     * 获取BOSS状态
     *
     * @return
     */
    @JSONField(serialize = false)
    public byte getState(int playerId) {
        refreshState();
        if (deadTime > 0)
            return GangBossService.BOSS_STATE_DEAD;
        if (fightStartTime > 0 && battle != null && battle.getId() != playerId) {
            return GangBossService.BOSS_STATE_FIGHTING;
        }
        return GangBossService.BOSS_STATE_READY;
    }

    private void refreshState() {
        if (deadTime == 0 && fightStartTime == 0)
            return;
        long curr = System.currentTimeMillis();
        //BOSS死亡
        if (deadTime > 0) {
            long dayStart = DateUtil.getDayStartTime(curr);
            int passRound = (int) ((curr - dayStart) / GangBossService.REFRESH_TIME);
            long startTime = dayStart + passRound * GangBossService.REFRESH_TIME;
            //BOSS在前面轮次中死亡  复活
            if (deadTime < startTime) {
                deadTime = 0;
                fightStartTime = 0;
                battle = null;
            }
        }
        //挑战中
        if (fightStartTime > 0) {
            //超时
            if (curr - fightStartTime > GangBossService.FIGHT_TIME_OUT) {
                fightStartTime = 0;
                battle = null;
            }
        }
    }

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public long getDeadTime() {
        return deadTime;
    }

    public void setDeadTime(long deadTime) {
        this.deadTime = deadTime;
    }

    public long getFightStartTime() {
        return fightStartTime;
    }

    public void setFightStartTime(long fightStartTime) {
        this.fightStartTime = fightStartTime;
    }

    public SimplePlayer getBattle() {
        return battle;
    }

    public void setBattle(SimplePlayer battle) {
        this.battle = battle;
    }

}
