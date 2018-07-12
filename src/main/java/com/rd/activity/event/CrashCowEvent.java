package com.rd.activity.event;

import com.rd.activity.ActivityService;
import com.rd.bean.player.PlayerActivity;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.PlayerDao;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.net.message.Message;
import org.apache.log4j.Logger;

/**
 * 摇钱树
 * Created by XingYun on 2017/1/18.
 */
public class CrashCowEvent implements IActivityEvent {
    private static final Logger logger = Logger.getLogger(CrashCowEvent.class);

    @Override
    public boolean onStart() {
        logger.info("摇钱树活动开启!");
        return false;
    }

    @Override
    public boolean onEnd() {
        logger.info("摇钱树活动结束!");
        try {
            // 积分清理
            PlayerDao dao = new PlayerDao();
            dao.clearTLPoints();
            for (GameRole role : GameWorld.getPtr().getOnlineRoles().values()) {
                role.getPlayer().setTlPoints(0);
                role.sendUpdateCurrencyMsg(EGoodsType.TLPOINTS, EGoodsChangeType.GOLDTREE_GET_ADD);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public void getMessage(Message msg, GameRole role) {
        PlayerActivity activityData = role.getActivityManager().getActivityData();
        int freeTimes = ActivityService.CRASH_COW_DAILY_FREE_TIMES - activityData.getCrashcowTimes();
        msg.setByte(freeTimes < 0 ? 0 : freeTimes);
    }

    @Override
    public boolean isOpen(GameRole role) {
        return true;
    }
}
