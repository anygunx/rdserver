package com.rd.activity.event;

import com.rd.activity.ActivityService;
import com.rd.activity.EActivityType;
import com.rd.activity.config.BaseActivityConfig;
import com.rd.activity.data.TargetConsumeDaillyCumulateLogicData;
import com.rd.activity.group.ActivityGroupData;
import com.rd.activity.group.ActivityRoundConfig;
import com.rd.bean.drop.DropData;
import com.rd.bean.mail.Mail;
import com.rd.bean.player.Player;
import com.rd.bean.rank.ActivityRank;
import com.rd.common.MailService;
import com.rd.common.goods.DiamondCmd;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRankManager;
import com.rd.game.GameRole;
import com.rd.model.TargetModel;
import com.rd.model.data.DaBiaoData;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class TargetDailyConsumeCumulateEvent implements IActivityEvent {
    @Override
    public boolean onStart() {
        return true;
    }

    @Override
    public boolean onEnd() {
        this.dailyExecute();
        return true;
    }

    @Override
    public void getMessage(Message msg, GameRole role) {
        long curr = System.currentTimeMillis();
        BaseActivityConfig configData = ActivityService.getActivityConfig(EActivityType.TARGET_DAILY_CONSUME_CUMULATE);
        ActivityRoundConfig currRound = configData.getCurrRound(0, curr);
        if (currRound == null)
            return;
        ActivityGroupData<TargetConsumeDaillyCumulateLogicData> group = ActivityService.getGroupData(EActivityType.TARGET_DAILY_CONSUME_CUMULATE);
        //活动数据轮次
        int round = group.getDataRound(currRound.getRound());
        msg.setByte(round);
    }

    @Override
    public boolean isOpen(GameRole role) {
        return true;
    }


    public static ActivityRank getActivityRank(Player player) {
        long curr = System.currentTimeMillis();
        curr = DateUtil.getDayStartTime(curr);
        ActivityRank rank = new ActivityRank();
        rank.setId(player.getId());
        rank.setN(player.getName());
        rank.setVn(player.getVipLevel());
        rank.setM(System.currentTimeMillis());
        int consume = DiamondCmd.gi().dealConsumeDailyStr(curr, player.getConsumeDaily());
        rank.setV1(consume);
        return rank;
    }

    public void dailyExecute() {
        BaseActivityConfig configData = ActivityService.getActivityConfig(EActivityType.TARGET_DAILY_CONSUME_CUMULATE);
        ActivityRoundConfig currRound = configData.getCurrRound(0, System.currentTimeMillis() - 3600000);
        if (currRound == null)
            return;
        GameRankManager.getInstance().copyHistoryTargetConsumeRank();
        List<ActivityRank> ranks = GameRankManager.getInstance().getHistoryTargetConsumeRanks();
        if (ranks == null)
            return;
        ActivityGroupData<TargetConsumeDaillyCumulateLogicData> group = ActivityService.getGroupData(EActivityType.TARGET_DAILY_CONSUME_CUMULATE);
        //活动数据轮次
        int round = group.getDataRound(currRound.getRound());
        TargetConsumeDaillyCumulateLogicData logic = group.getRound(round).get(round + "");
        DaBiaoData data = TargetModel.getDaBiaoReward(logic.getId());
        for (int i = 0; i < ranks.size(); i++) {
            String title = "", content = "";
            List<DropData> rewards = new ArrayList<>();
            ActivityRank rank = ranks.get(i);
            if (i == 0) {
                title = data.getFirstTitle();
                content = data.getFirstContent();
                rewards.addAll(data.getFirstReward());
            } else if (i == 1) {
                title = data.getSecondTitle();
                content = data.getSecondContent();
                rewards.addAll(data.getSecondReward());
            } else if (i == 2) {
                title = data.getThirdTitle();
                content = data.getThirdContent();
                rewards.addAll(data.getThirdReward());
            } else if (i < 20) {
                title = data.getForthTitle();
                content = data.getForthContent();
                rewards.addAll(data.getForthReward());
            }
            if (i < 20) {
                Mail mail = MailService.createMail(title, content, EGoodsChangeType.TARGET_DAILY_CONSUME_ADD, rewards);
                MailService.sendSystemMail(rank.getId(), mail);
            }
        }
    }


}
