package com.rd.activity.event;

import com.rd.dao.ActivityDao;
import com.rd.game.GameRole;
import com.rd.net.message.Message;
import org.apache.log4j.Logger;

public class FiveElementEvent implements IActivityEvent {

    private static Logger logger = Logger.getLogger(KamPoEvent.class);

    @Override
    public boolean onStart() {
        return true;
    }

    @Override
    public boolean onEnd() {
        logger.info("节日期间五行活动结束！");
        ActivityDao dao = new ActivityDao();
        dao.updateActivityFirstDaily();
        return true;
    }

    @Override
    public void getMessage(Message msg, GameRole role) {


    }

    @Override
    public boolean isOpen(GameRole role) {
        return true;
    }


}
