package com.rd.common;

import com.sun.istack.internal.logging.Logger;
import com.rd.bean.gang.GangBoss;
import com.rd.model.GangModel;
import com.rd.model.data.GangBossModelData;
import com.rd.util.DateUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 公会BOSS
 *
 * @author U-Demon Created on 2017年4月13日 下午9:59:10
 * @version 1.0.0
 */
public class GangBossService {

    private static final Logger logger = Logger.getLogger(GangBossService.class);

    //每天挑战BOSS的次数
    public static final int FIGHT_MAX = 5;
    //BOSS每半小时刷新一次
    public static final long REFRESH_TIME = 1800 * DateUtil.SECOND;
    //BOSS每天的开始和结束时间
    private static final long START_TIME = 9 * DateUtil.HOUR + 30 * DateUtil.MINUTE;
    private static final long END_TIME = 23 * DateUtil.HOUR + 30 * DateUtil.MINUTE;
    //BOSS挑战超时时间
    public static final long FIGHT_TIME_OUT = 3 * DateUtil.MINUTE;

    public static final byte BOSS_STATE_READY = 1;
    public static final byte BOSS_STATE_FIGHTING = 2;
    public static final byte BOSS_STATE_DEAD = 3;
    public static final byte BOSS_STATE_CLOSED = 4;

    //所有公会的BOSS
    private static Map<Integer, Map<Byte, GangBoss>> gangBossMap = new ConcurrentHashMap<>();

    /**
     * 获取BOSS数据
     *
     * @param gangId
     * @param bossId
     * @return
     */
    public static GangBoss getBoss(int gangId, byte bossId) {
        return getAllBoss(gangId).get(bossId);
    }

    public static Map<Byte, GangBoss> getAllBoss(int gangId) {
        if (!gangBossMap.containsKey(gangId)) {
            Map<Byte, GangBoss> bossMap = new ConcurrentHashMap<>();
            for (GangBossModelData model : GangModel.getGangBossMap().values()) {
                GangBoss boss = new GangBoss();
                boss.setId(model.getId());
                bossMap.put(boss.getId(), boss);
            }
            gangBossMap.put(gangId, bossMap);
        }
        return gangBossMap.get(gangId);
    }

    /**
     * 公会BOSS是否在开放时间
     *
     * @return
     */
    public static boolean isOpen() {
        long curr = System.currentTimeMillis();
        long dayStart = DateUtil.getDayStartTime(curr);
        long startTime = dayStart + START_TIME;
        long endTime = dayStart + END_TIME;
        return curr >= startTime && curr <= endTime;
    }

    /**
     * 获取下次刷新时间
     *
     * @return
     */
    public static int getNextRefreshTime() {
        long curr = System.currentTimeMillis();
        long dayStart = DateUtil.getDayStartTime(curr);
        long startTime = dayStart + START_TIME;
        long endTime = dayStart + END_TIME;
        //还未开始
        if (curr < startTime)
            return (int) ((startTime - curr) / 1000) + 1;
        //已经结束
        if (curr >= endTime)
            return (int) ((startTime + DateUtil.DAY - curr) / 1000) + 1;
        //在刷新周期内
        int passRound = (int) ((curr - startTime) / REFRESH_TIME);
        return (int) ((startTime + (passRound + 1) * REFRESH_TIME - curr) / 1000) + 1;
    }

}
