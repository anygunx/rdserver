package com.rd.activity.event;

import com.rd.activity.ActivityService;
import com.rd.activity.EActivityType;
import com.rd.activity.data.TreasuresLogicData;
import com.rd.bean.player.PlayerActivity;
import com.rd.dao.ActivityDao;
import com.rd.game.GameRole;
import com.rd.game.manager.ActivityManager;
import com.rd.net.message.Message;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

public class TreasuresEvent implements IActivityEvent {

    private static Logger logger = Logger.getLogger(TreasuresEvent.class);

    @Override
    public boolean onStart() {
        return true;
    }

    @Override
    public boolean onEnd() {
        logger.info("秘宝活动结束！");
        ActivityDao dao = new ActivityDao();
        //清除活动数据
        for (PlayerActivity activity : dao.getPlayerActivitys2Treasure()) {
            dao.updateTreasure(activity);
        }
        return true;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void getMessage(Message msg, GameRole role) {
        ActivityManager activityManager = role.getActivityManager();
        PlayerActivity activityData = activityManager.getActivityData();
        ActivityDao dao = role.getActivityManager().getActivityDao();
//		BaseActivityConfig activityConfig = ActivityService.getActivityConfig(EActivityType.TREASURES);
        Map<String, TreasuresLogicData> logics = ActivityService.getRoundData(EActivityType.TREASURES, 0);
        List<Integer> fourItems = activityData.getFourItems();
        if (fourItems.isEmpty()) {
            fourItems = activityManager.getRandomIds4Treasures(logics);
            activityData.setFourItems(fourItems);
        }
//		List<Integer> buiedItems = activityData.getBuiedItems();
//		msg.setByte(buiedItems.size());
//		for(Integer id : buiedItems) {
//			msg.setInt(id);
//		}
        List<Integer> reBuiedItems = activityData.getReBuiedItems();
        List<Integer> vouchersList = activityData.getVouchersList();
        int integral = activityData.getTreasureIntegral();
        int vouchers = role.getPlayer().getVouchers();
        byte free = activityData.getTreasureFree();
        msg.setInt(integral);
        msg.setInt(vouchers);
        msg.setByte(free);
        msg.setByte(fourItems.size());
        for (Integer id : fourItems) {
            msg.setInt(id);
        }
        msg.setByte(reBuiedItems.size());
        for (Integer id : reBuiedItems) {
            msg.setInt(id);
        }
        msg.setByte(vouchersList.size());
        for (Integer id : vouchersList) {
            msg.setInt(id);
        }
        dao.updateTreasureFourItems(activityData);
    }

    @Override
    public boolean isOpen(GameRole role) {
        return true;
    }

}
