package com.rd.activity.event;

import com.rd.activity.EActivityType;
import com.rd.bean.player.PlayerActivity;
import com.rd.game.GameRole;
import com.rd.net.message.Message;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * 特惠礼包
 *
 * @author Created by U-Demon on 2016年12月26日 下午4:43:05
 * @version 1.0.0
 */
public class RebateEvent implements IActivityEvent {

    private static Logger logger = Logger.getLogger(RebateEvent.class);

    @Override
    public boolean onStart() {
        logger.info("特惠礼包活动开启!");
        return true;
    }

    @Override
    public boolean onEnd() {
        logger.info("特惠礼包活动结束!");
        return true;
    }

    @Override
    public void getMessage(Message msg, GameRole role) {
        PlayerActivity activityData = role.getActivityManager().getActivityData();
        List<Integer> list = new ArrayList<>();
        for (int key : activityData.getRebates()) {
            if (key / 10000 == EActivityType.REBATE.getId()) {
                list.add(key % 10000);
            }
        }
        msg.setByte(list.size());
        for (int id : list) {
            msg.setByte(id);
        }
    }

    @Override
    public boolean isOpen(GameRole role) {
//		PlayerActivity activityData = role.getActivityManager().getActivityData();
//		Map<String, RebateLogicData> logicData = ActivityService.getRoundData(
//				EActivityType.REBATE, role.getPlayerId(), System.currentTimeMillis());
//		List<Integer> list = new ArrayList<>();
//		for (int key : activityData.getRebates())
//		{
//			if (key / 10000 == EActivityType.REBATE.getId())
//			{
//				list.add(key % 10000);
//			}
//		}
//		if (list.size() == logicData.size())
//			return false;
        return true;
    }

}
