package com.rd.activity.event;

import com.rd.bean.player.PlayerActivity;
import com.rd.dao.ActivityDao;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;
import org.apache.log4j.Logger;

/**
 * 签到
 * Created by XingYun on 2017/1/19.
 */
public class SignEvent implements IActivityEvent {
    private static final Logger logger = Logger.getLogger(CrashCowEvent.class);

    @Override
    public boolean onStart() {
        logger.info("春节签到活动开始!");
        return false;
    }

    @Override
    public boolean onEnd() {
        logger.info("春节签到活动结束!");
        try {
            // 积分清理
            ActivityDao dao = new ActivityDao();
            dao.clearSignData();
            for (GameRole role : GameWorld.getPtr().getOnlineRoles().values()) {
                PlayerActivity playerActivity = role.getActivityManager().getActivityData();
                playerActivity.setSignNum((short) 0);
                playerActivity.setSignTime(0);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public void getMessage(Message msg, GameRole role) {
        PlayerActivity playerActivity = role.getActivityManager().getActivityData();
        boolean signToday = DateUtil.dayEqual(playerActivity.getSignTime(), System.currentTimeMillis());
        msg.setShort(playerActivity.getSignNum());
        msg.setBool(signToday);
    }

    @Override
    public boolean isOpen(GameRole role) {
        return true;
    }

}
