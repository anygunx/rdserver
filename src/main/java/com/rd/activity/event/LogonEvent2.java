package com.rd.activity.event;

import com.rd.bean.player.PlayerActivity;
import com.rd.dao.ActivityDao;
import com.rd.game.GameRole;
import com.rd.net.message.Message;
import org.apache.log4j.Logger;

public class LogonEvent2 implements IActivityEvent {

    private static final Logger logger = Logger.getLogger(PuzzleEvent.class);

    @Override
    public boolean onStart() {
        return true;
    }

    @Override
    public boolean onEnd() {
        logger.info("新年登录活动结束！");
        ActivityDao dao = new ActivityDao();
        dao.updateNewYearLogon();
        return true;
    }

    @Override
    public void getMessage(Message msg, GameRole role) {
        PlayerActivity data = role.getActivityManager().getActivityData();
        role.getPlayer().getCreateTime();
        role.getActivityManager().handleLogin2NewYear();
        int today = data.getNewYearLoginDay();
        if (today > 9)
            today = 9;
        msg.setByte(today);
        msg.setByte(data.getNewYearLogonRewards().size());
        for (byte reward : data.getNewYearLogonRewards()) {
            msg.setByte(reward);
        }
    }

    @Override
    public boolean isOpen(GameRole role) {
        PlayerActivity data = role.getActivityManager().getActivityData();
        if (data.getNewYearLogonRewards().size() >= 9)
            return false;
        return true;
    }

}
