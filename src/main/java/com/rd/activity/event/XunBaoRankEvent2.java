package com.rd.activity.event;

import com.rd.activity.ActivityService;
import com.rd.activity.EActivityType;
import com.rd.activity.config.BaseActivityConfig;
import com.rd.activity.data.TargetLogicData;
import com.rd.activity.data.XunBaoRankLogicData;
import com.rd.activity.group.ActivityGroupData;
import com.rd.activity.group.ActivityRoundConfig;
import com.rd.bean.mail.Mail;
import com.rd.bean.rank.ActivityRank;
import com.rd.common.MailService;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRankManager;
import com.rd.game.GameRole;
import com.rd.net.message.Message;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

public class XunBaoRankEvent2 implements IActivityEvent {

    private static final Logger logger = Logger.getLogger(XunBaoRankEvent2.class);

    @Override
    public boolean onStart() {
        return true;
    }

    @Override
    public boolean onEnd() {
        GameRankManager.getInstance().copyHistoryXunbaoRank();
        GameRankManager.getInstance().clearXunbaoRanks();
        List<ActivityRank> ranks = GameRankManager.getInstance().getHistoryXunbaoRanks();
        if (ranks == null)
            return false;
        Map<String, XunBaoRankLogicData> logicData = ActivityService.getRoundData(
                EActivityType.XUNBAO_RANK2, 0, System.currentTimeMillis() - 3600000);
        if (logicData == null)
            return false;
        for (int i = 0; i < ranks.size(); i++) {
            ActivityRank rank = ranks.get(i);
            XunBaoRankLogicData data = logicData.get(String.valueOf(i + 1));
            if (data == null)
                continue;
            if (rank.getV1() < data.getMin())
                continue;
            logger.error("寻宝排行榜结算信息：第" + i + "名，玩家：" + rank.getId());
            Mail mail = MailService.createMail(data.getTitle(), data.getContent(),
                    EGoodsChangeType.XUNBAO_RANK_ADD, data.getRewards());
            MailService.sendSystemMail(rank.getId(), mail);
        }
        return true;
    }

    @Override
    public void getMessage(Message msg, GameRole role) {
        long curr = System.currentTimeMillis();
        BaseActivityConfig configData = ActivityService.getActivityConfig(EActivityType.XUNBAO_RANK2);
        ActivityRoundConfig currRound = configData.getCurrRound(0, curr);
        if (currRound == null)
            return;
        ActivityGroupData<TargetLogicData> group = ActivityService.getGroupData(EActivityType.XUNBAO_RANK2);
        //活动数据轮次
        int round = group.getDataRound(currRound.getRound());
        msg.setByte(round);
        msg.setInt(role.getActivityManager().getActivityData().getXunbaoCount());
    }

    @Override
    public boolean isOpen(GameRole role) {
        return true;
    }

}
