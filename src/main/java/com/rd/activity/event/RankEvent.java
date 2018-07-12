package com.rd.activity.event;

import com.rd.define.GameDefine;
import com.rd.game.GameRankManager;
import com.rd.game.GameRole;
import com.rd.net.message.Message;
import com.rd.task.ETaskType;
import com.rd.task.Task;
import com.rd.task.TaskManager;
import com.rd.util.DateUtil;
import org.apache.log4j.Logger;

public class RankEvent implements IActivityEvent {

    private static Logger logger = Logger.getLogger(RankEvent.class);

    @Override
    public boolean onStart() {
        return false;
    }

    @Override
    public boolean onEnd() {
        TaskManager.getInstance().scheduleDelayTask(ETaskType.ACTIVITY,
                new Task() {
                    @Override
                    public void run() {
                        try {
                            logger.info("排行榜奖励发放");
                            long curr = System.currentTimeMillis();
                            int day = DateUtil.getDistanceDay(GameDefine.SERVER_CREATE_TIME, curr);
                            GameRankManager.getInstance().dailyReward(day - 1);
                        } catch (Exception e) {
                            logger.error("排行榜奖励发放时发生异常.", e);
                        }
                    }

                    @Override
                    public String name() {
                        return "GameRankRewardTask";
                    }
                }, 100);
        return true;
    }

    @Override
    public void getMessage(Message msg, GameRole role) {

    }

    @Override
    public boolean isOpen(GameRole role) {
        return false;
    }

}
