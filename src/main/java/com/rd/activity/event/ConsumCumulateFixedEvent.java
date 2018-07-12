package com.rd.activity.event;

import com.rd.activity.ActivityService;
import com.rd.activity.EActivityType;
import com.rd.activity.config.BaseActivityConfig;
import com.rd.activity.group.ActivityGroupData;
import com.rd.activity.group.ActivityRoundConfig;
import com.rd.bean.player.Player;
import com.rd.bean.player.PlayerActivity;
import com.rd.dao.ActivityDao;
import com.rd.game.GameRole;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;
import org.apache.log4j.Logger;

/**
 * 固定时间累计充值
 *
 * @author ---
 * @version 1.0
 * @date 2018年1月19日下午3:48:33
 */
public class ConsumCumulateFixedEvent implements IActivityEvent {

    private static Logger logger = Logger.getLogger(ConsumCumulateFixedEvent.class);

    public ConsumCumulateFixedEvent() {
    }

    @Override
    public boolean onStart() {
        return false;
    }

    @Override
    public boolean onEnd() {
        logger.info("累计消费活动结束!");
        return false;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void getMessage(Message msg, GameRole role) {
        long curr = System.currentTimeMillis();
        BaseActivityConfig configData = ActivityService.getActivityConfig(EActivityType.CONSUM_CUMULATE);
        ActivityRoundConfig currRound = configData.getCurrRound(0, curr);
        if (currRound == null)
            return;

        ActivityDao dao = role.getActivityManager().getActivityDao();
        Player player = role.getPlayer();
        ActivityGroupData group = ActivityService.getGroupData(EActivityType.CONSUM_CUMULATE);

        int consume = this.dealConsumeDailyStr(DateUtil.getDayStartTime(curr), player.getConsumeDaily());
        PlayerActivity activityData = role.getActivityManager().getActivityData();
        msg.setByte(activityData.getConsumeCumulateFixedData().size());
        for (Integer key : activityData.getConsumeCumulateFixedData()) {
            msg.setInt(key);
        }
        msg.setInt(consume);
        msg.setShort(group.getDataRound(currRound.getRound()));
//		role.sendMessage(msg);
        dao.updateConsumCumulateReceived(activityData);
    }

    /**
     * 处理每日消耗
     *
     * @param currTime 当天凌晨时间戳
     * @param str      数据库
     * @return
     */
    private int dealConsumeDailyStr(long currTime, String str) {
        try {
            if (str == null || "".equals(str) || "null".equals(str) || "{}".equals(str)) return 0;
            if (str.indexOf(":") > -1) {
                String s = str.substring(str.lastIndexOf(":") + 1, str.length() - 1);
                return Integer.parseInt(s);
            } else {
                String[] strs = str.split(",");
                String str1 = strs[0];
                long oldTime = Long.parseLong(str1);
                if (oldTime != currTime) return 0;
                return Integer.parseInt(strs[1]);
            }
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public boolean isOpen(GameRole role) {
        return true;
    }

}
