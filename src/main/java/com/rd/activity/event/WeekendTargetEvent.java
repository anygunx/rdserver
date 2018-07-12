package com.rd.activity.event;

import com.rd.activity.ActivityService;
import com.rd.activity.EActivityType;
import com.rd.activity.config.BaseActivityConfig;
import com.rd.activity.data.TargetLogicData;
import com.rd.activity.group.ActivityGroupData;
import com.rd.activity.group.ActivityRoundConfig;
import com.rd.bean.player.PlayerActivity;
import com.rd.game.GameRole;
import com.rd.net.message.Message;
import org.apache.log4j.Logger;

public class WeekendTargetEvent implements IActivityEvent {

    private static final Logger logger = Logger.getLogger(WeekendTargetEvent.class);

    @Override
    public boolean onStart() {
        return true;
    }

    @Override
    public boolean onEnd() {
//		GameRankManager.getInstance().copyHistoryWeekendRank();
//		GameRankManager.getInstance().clearWeekendRanks();
//		List<ActivityRank> ranks = GameRankManager.getInstance().getHistoryWeekendRanks();
//		if (ranks == null)
//			return false;
//		
//		BaseActivityConfig configData = ActivityService.getActivityConfig(EActivityType.WEEKEND_TARGET);
//		ActivityRoundConfig currRound = configData.getCurrRound(0, System.currentTimeMillis()-3600000);
//		if (currRound == null)
//			return false;
//		ActivityGroupData<TargetLogicData> group = ActivityService.getGroupData(EActivityType.WEEKEND_TARGET);
//		//活动数据轮次
//		int round = group.getDataRound(currRound.getRound());
//		TargetLogicData logic = group.getRound(round).get(round+"");
//		DaBiaoData data = Activity7Model.getDaBiaoReward(logic.getPaihangreward());
//		//奖励
//		for (int i = 0; i < ranks.size(); i++) {
//			String title = "", content = "";
//			List<DropData> rewards = new ArrayList<>();
//			ActivityRank rank = ranks.get(i);
//			if (rank.getV1() < GameRankManager.WEEKEND_VALUE_MIN)
//				continue;
//			if (i == 0) {
//				title = data.getFirstTitle();
//				content = data.getFirstContent();
//				rewards.addAll(data.getFirstReward());
//			} else if (i == 1) {
//				title = data.getSecondTitle();
//				content = data.getSecondContent();
//				rewards.addAll(data.getSecondReward());
//			} else if (i == 2) {
//				title = data.getThirdTitle();
//				content = data.getThirdContent();
//				rewards.addAll(data.getThirdReward());
//			} else if (i < 20) {
//				title = data.getForthTitle();
//				content = data.getForthContent();
//				rewards.addAll(data.getForthReward());
//			}
//			logger.error("周末充值排行榜结算信息：第"+(i+1)+"名，玩家："+rank.getId());
//			Mail mail = MailService.createMail(title, content, EGoodsChangeType.WEEKEND_TARGET_ADD, rewards);
//			MailService.sendSystemMail(rank.getId(), mail);
//		}
        return true;
    }

    @Override
    public void getMessage(Message msg, GameRole role) {
        long curr = System.currentTimeMillis();
        BaseActivityConfig configData = ActivityService.getActivityConfig(EActivityType.WEEKEND_TARGET);
        ActivityRoundConfig currRound = configData.getCurrRound(0, curr);
        if (currRound == null)
            return;
        ActivityGroupData<TargetLogicData> group = ActivityService.getGroupData(EActivityType.WEEKEND_TARGET);
        //活动数据轮次
        int round = group.getDataRound(currRound.getRound());
        msg.setByte(round);
        //领取记录
        PlayerActivity pa = role.getActivityManager().getActivityData();
        //自己的信息
        msg.setByte(pa.getWeekendReward() + 1);
        msg.setInt(pa.getWeekendPay());
    }

    @Override
    public boolean isOpen(GameRole role) {
        return true;
    }

}
