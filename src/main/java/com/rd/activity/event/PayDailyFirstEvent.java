package com.rd.activity.event;

import com.rd.game.GameRole;
import com.rd.net.message.Message;
import org.apache.log4j.Logger;

/**
 * 限制礼包
 *
 * @author U-Demon Created on 2017年3月14日 下午6:06:03
 * @version 1.0.0
 */
public class PayDailyFirstEvent implements IActivityEvent {

    private static Logger logger = Logger.getLogger(KamPoEvent.class);

    @Override
    public boolean onStart() {
        return true;
    }

    @Override
    public boolean onEnd() {
        logger.info("节日期间每日首冲活动结束！");
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
