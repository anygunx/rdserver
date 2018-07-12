package com.rd.activity.event;

import com.rd.activity.ActivityService;
import com.rd.activity.EActivityType;
import com.rd.activity.data.KamPoLogicData;
import com.rd.bean.drop.DropData;
import com.rd.bean.mail.Mail;
import com.rd.bean.player.PlayerActivity;
import com.rd.common.MailService;
import com.rd.dao.ActivityDao;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRole;
import com.rd.model.data.MailRewardModelData;
import com.rd.net.message.Message;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KamPoEvent implements IActivityEvent {

    private static Logger logger = Logger.getLogger(KamPoEvent.class);

    @Override
    public boolean onStart() {
        return false;
    }

    @Override
    public boolean onEnd() {
        logger.info("幸运鉴宝结束");
        ActivityDao dao = new ActivityDao();
        List<PlayerActivity> activities = dao.getPlayerActivitys2();
        Map<String, KamPoLogicData> logics = ActivityService.getRoundData(EActivityType.KAM_PO, 0);
        KamPoLogicData kpd = logics.get("1");
        Map<Integer, DropData> map = kpd.getLuck_reward();
        List<Integer> all = new ArrayList<>(map.keySet());
        for (PlayerActivity activity : activities) {
            List<Integer> received = activity.getReceivedLuckScore();
            all.removeAll(received);
            List<DropData> dropDatas = new ArrayList<>();

            int currLuckScore = activity.getKamPoLuckScore();
            for (int i = 0; i < all.size(); i++) {
                int score = all.get(i);
                if (score <= currLuckScore) {
                    DropData dd = kpd.getLuck_reward().get(score);
                    dropDatas.add(dd);
                }
            }
            int id = 0;
            if (!dropDatas.isEmpty()) {
                Mail mail = MailService.createMail(new MailRewardModelData((short) 1, "幸运轮盘未领取奖励", "亲爱的玩家，您在参加幸运轮盘时未即时领取专属奖励，特以邮件形式为您发放", dropDatas), EGoodsChangeType.LUCK_SCORE_ADD);
                id = MailService.sendSystemMail(activity.getPlayerId(), mail);
            }
            if (id != -1) {
                activity.resetReceivedLuckScore();
                activity.setKamPoLuckScore(0);
                dao.updateKamPoLuckScore(activity);
                dao.updateReceiveLuckScore(activity);
            }
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
