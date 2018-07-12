package com.rd.activity.config;

import com.rd.activity.group.ActivityRoundConfig;
import com.rd.define.ActivityDefine;
import com.rd.game.GameWorld;
import com.rd.game.IGameRole;
import com.rd.util.DateUtil;

/**
 * 个人类型时间的活动
 *
 * @author Created by U-Demon on 2016年11月2日 下午7:06:54
 * @version 1.0.0
 */
public class PlayerActivityConfig extends BaseActivityConfig {

    public PlayerActivityConfig(int id, String name, long startTime, long endTime,
                                boolean loop, float keepDay, float restDay, int roundTotal) {
        super(ActivityDefine.ACTIVITY_CONFIG_TIME_PLAYER, id, name, startTime, endTime, loop, keepDay, restDay, roundTotal);
    }

    @Override
    public long getStartTime(int playerId) {
        IGameRole role = GameWorld.getPtr().getGameRole(playerId);
        if (role == null)
            return 0;
        long dayTime = DateUtil.getDayStartTime(role.getPlayer().getCreateTime());
        return dayTime;
    }

    /**
     * 玩家相关的活动，每次都新建RoundConfig
     */
    @Override
    public ActivityRoundConfig getCurrRound(int playerId, long currTime) {
        //固定时间
        if (!loop) {
            long startTime = getStartTime(playerId);
            long endTime = startTime + keepTime;
            return new ActivityRoundConfig(id, 0, startTime, endTime);
        } else {
            long firstTime = getStartTime(playerId);
            if (currTime < firstTime)
                return null;
            long passTime = currTime - firstTime;
            int round = (int) (passTime / (keepTime + restTime));
            //endTime 表示一共多少轮
            if (round >= endTime)
                return null;
            long startTime = firstTime + round * (keepTime + restTime);
            long endTime = startTime + keepTime;
            return new ActivityRoundConfig(id, round, startTime, endTime);
        }
    }

}
