package com.rd.activity.event;

import com.rd.bean.player.PlayerActivity;
import com.rd.dao.ActivityDao;
import com.rd.game.GameRole;
import com.rd.net.message.Message;
import org.apache.log4j.Logger;

import java.util.List;

public class PuzzleEvent implements IActivityEvent {

    private static final Logger logger = Logger.getLogger(PuzzleEvent.class);

    @Override
    public boolean onStart() {
        return true;
    }

    @Override
    public boolean onEnd() {
        logger.info("拼图活动结束!");
        ActivityDao dao = new ActivityDao();
        dao.updatePuzzData();
        return true;
    }

    @Override
    public void getMessage(Message msg, GameRole role) {
        PlayerActivity activityData = role.getActivityManager().getActivityData();
        List<Integer> puzzleReceiveds = activityData.getPuzzleReceived();
        int restTime = activityData.getPuzzleRestTime();
        msg.setByte(restTime);
        msg.setByte(puzzleReceiveds.size());
        for (Integer pr : puzzleReceiveds) {
            msg.setInt(pr);
        }
    }

    @Override
    public boolean isOpen(GameRole role) {
        return true;
    }

}
