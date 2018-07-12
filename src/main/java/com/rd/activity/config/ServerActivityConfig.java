package com.rd.activity.config;

import com.rd.activity.group.ActivityRoundConfig;
import com.rd.define.ActivityDefine;

/**
 * 服务器类型的活动
 *
 * @author Created by U-Demon on 2016年11月2日 下午7:02:50
 * @version 1.0.0
 */
public class ServerActivityConfig extends BaseActivityConfig {

    //固定时间活动
    private ActivityRoundConfig roundConfig;

    public ServerActivityConfig(int id, String name, long startTime, long endTime,
                                boolean loop, float keepDay, float restDay, int roundTotal) {
        super(ActivityDefine.ACTIVITY_CONFIG_TIME_SERVER, id, name, startTime, endTime, loop, keepDay, restDay, roundTotal);
    }

    @Override
    public long getStartTime(int playerId) {
        return startTime;
    }

    @Override
    public ActivityRoundConfig getCurrRound(int playerId, long currTime) {
        //固定时间
        if (!loop) {
            if (roundConfig == null) {
                long endTime = getStartTime(playerId) + keepTime;
                roundConfig = new ActivityRoundConfig(id, 0, getStartTime(playerId), endTime);
            }
            return roundConfig;
        } else {
            if (currTime < getStartTime(playerId) || currTime > endTime)
                return null;
            if (roundConfig != null && !roundConfig.isEnd(currTime)) {
                return roundConfig;
            }
            long passTime = currTime - getStartTime(playerId);
            int round = (int) (passTime / (keepTime + restTime));
            if (round >= roundTotal)
                return null;
            long startTime = getStartTime(playerId) + round * (keepTime + restTime);
            long endTime = startTime + keepTime;
            roundConfig = new ActivityRoundConfig(id, round, startTime, endTime);
            return roundConfig;
        }
    }

}
