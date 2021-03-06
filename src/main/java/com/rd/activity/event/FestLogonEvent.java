package com.rd.activity.event;

import com.rd.activity.ActivityService;
import com.rd.activity.EActivityType;
import com.rd.activity.config.BaseActivityConfig;
import com.rd.activity.data.LogonLogicData;
import com.rd.bean.player.PlayerActivity;
import com.rd.dao.ActivityDao;
import com.rd.game.GameRole;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;
import org.apache.log4j.Logger;

import java.util.Map;

public class FestLogonEvent implements IActivityEvent {

    private static final Logger logger = Logger.getLogger(FestLogonEvent.class);

    @Override
    public boolean onStart() {
        return true;
    }

    @Override
    public boolean onEnd() {
        logger.info("节日登录活动结束!");
        new ActivityDao().clearFestLogon();
        return true;
    }

    @Override
    public void getMessage(Message msg, GameRole role) {
        //活动开始时间
        BaseActivityConfig config = ActivityService.getActivityConfig(EActivityType.FEST_LOGON);
        long startTime = config.getStartTime(role.getPlayerId());
        //距离活动开始第几天
        int today = DateUtil.getDistanceDay(startTime, System.currentTimeMillis()) + 1;
        //节日登录活动记录
        PlayerActivity data = role.getActivityManager().getActivityData();
        if (data.getFestLogon().size() < today) {
            for (int i = data.getFestLogon().size(); i < today - 1; i++) {
                data.getFestLogon().add((byte) 0);
            }
            data.getFestLogon().add((byte) 1);
            new ActivityDao().updateFestLogon(data);
        }
        int size = data.getFestLogon().size();
        if (size > 7)
            size = 7;
        msg.setByte(size);
        for (int i = 0; i < size; i++) {
            msg.setByte(data.getFestLogon().get(i));
        }
    }

    @Override
    public boolean isOpen(GameRole role) {
        Map<String, LogonLogicData> model = ActivityService.getRoundData(EActivityType.FEST_LOGON, 0);
        PlayerActivity data = role.getActivityManager().getActivityData();
        int rewards = 0;
        for (byte reward : data.getFestLogon()) {
            if (reward == 2)
                rewards++;
        }
        if (rewards >= model.size())
            return false;
        return true;
    }

}
