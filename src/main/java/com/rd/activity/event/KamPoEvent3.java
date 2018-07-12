package com.rd.activity.event;

import com.rd.bean.player.PlayerActivity;
import com.rd.dao.ActivityDao;
import com.rd.game.GameRole;
import com.rd.net.message.Message;
import org.apache.log4j.Logger;

public class KamPoEvent3 implements IActivityEvent {

    private static Logger logger = Logger.getLogger(KamPoEvent3.class);

    @Override
    public boolean onStart() {
        return false;
    }

    @Override
    public boolean onEnd() {
        logger.info("幸运鉴宝3结束!");
        ActivityDao dao = new ActivityDao();
        for (PlayerActivity activity : dao.getPlayerActivitys2KamPo()) {
            activity.clearKamPo2Costs();
            activity.setKamPo2Count(0);
            dao.updateKamPo2(activity);
        }
        return false;
    }

    @Override
    public void getMessage(Message msg, GameRole role) {

    }

    @Override
    public boolean isOpen(GameRole role) {
        return true;
    }

}
