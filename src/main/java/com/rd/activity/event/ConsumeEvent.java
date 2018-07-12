package com.rd.activity.event;

import com.rd.activity.ActivityService;
import com.rd.activity.EActivityType;
import com.rd.activity.config.BaseActivityConfig;
import com.rd.activity.group.ActivityGroupData;
import com.rd.activity.group.ActivityRoundConfig;
import com.rd.bean.player.PlayerActivity;
import com.rd.dao.ActivityDao;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.game.manager.ActivityManager;
import com.rd.net.message.Message;
import org.apache.log4j.Logger;

/**
 * 累计消费活动
 *
 * @author Created by U-Demon on 2017年1月23日 下午1:27:42
 * @version 1.0.0
 */
public class ConsumeEvent implements IActivityEvent {

    private static Logger logger = Logger.getLogger(ConsumeEvent.class);

    private ActivityDao dao = new ActivityDao();

    @Override
    public boolean onStart() {
        clearConsumeData();
        logger.info("累计消费活动开始了.清除累计消费总额.");
        return true;
    }

    @Override
    public boolean onEnd() {
        clearConsumeData();
        logger.info("累计消费活动结束了.清除累计消费总额.");
        return true;
    }

    private void clearConsumeData() {
        try {
            dao.clearConsumeData();
            for (GameRole role : GameWorld.getPtr().getOnlineRoles().values()) {
                ActivityManager manager = role.getActivityManager();
                manager.getActivityData().setConsume(0);
                role.putMessageQueue(manager.getActivityMsg(EActivityType.CONSUME));
            }
        } catch (Exception e) {
            logger.info("清除累计消费总额.", e);
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void getMessage(Message msg, GameRole role) {
        long curr = System.currentTimeMillis();
        BaseActivityConfig configData = ActivityService.getActivityConfig(EActivityType.CONSUME);
        PlayerActivity pa = role.getActivityManager().getActivityData();
        if (pa == null)
            msg.setInt(0);
        else
            msg.setInt(pa.getConsume());
        ActivityGroupData group = ActivityService.getGroupData(EActivityType.CONSUME);
        ActivityRoundConfig currRound = configData.getCurrRound(0, curr);
        if (currRound == null) {
            msg.setByte(0);
        } else {
            //活动数据轮次
            msg.setByte(group.getDataRound(currRound.getRound()));
        }
    }

    @Override
    public boolean isOpen(GameRole role) {
        return true;
    }

}
