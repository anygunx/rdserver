package com.rd.activity.event;

import com.rd.activity.ActivityService;
import com.rd.activity.EActivityType;
import com.rd.activity.data.SetWordsLogicData;
import com.rd.bean.player.PlayerActivity;
import com.rd.dao.ActivityDao;
import com.rd.game.GameRole;
import com.rd.game.manager.ActivityManager;
import com.rd.net.message.Message;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.Map.Entry;

public class SetWordsEvent implements IActivityEvent {

    private static Logger logger = Logger.getLogger(SetWordsEvent.class);

    @Override
    public boolean onStart() {
        return true;
    }

    @Override
    public boolean onEnd() {
        logger.info("集字活动结束！");
        ActivityDao dao = new ActivityDao();
        for (PlayerActivity activity : dao.getPlayerActivitys2SetWords()) {
            dao.updateSetWords(activity);
        }
        return true;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void getMessage(Message msg, GameRole role) {
        Map<String, SetWordsLogicData> logics = ActivityService.getRoundData(EActivityType.SET_WORDS, 0);
        ActivityManager activityManager = role.getActivityManager();
        PlayerActivity activityData = activityManager.getActivityData();
        ActivityDao dao = activityManager.getActivityDao();
        Map<Integer, Integer> map = activityData.getSetWordsNums();
        for (String key : logics.keySet()) {
            int k = Integer.valueOf(key);
            if (!map.containsKey(k)) {
                map.put(k, 0);
            }
        }
        msg.setByte(map.size());
        for (Entry<Integer, Integer> entry : map.entrySet()) {
            msg.setInt(entry.getKey());
            msg.setInt(entry.getValue());
        }
        activityData.setSetWordsNums(map);
        dao.updateSetWordsNum(activityData);
    }

    @Override
    public boolean isOpen(GameRole role) {
        return true;
    }

}
