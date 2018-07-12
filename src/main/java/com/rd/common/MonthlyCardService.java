package com.rd.common;

import com.rd.bean.mail.Mail;
import com.rd.bean.player.Player;
import com.rd.bean.player.PlayerActivity;
import com.rd.dao.ActivityDao;
import com.rd.dao.PlayerDao;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.model.MonthlyCardModel;
import com.rd.model.data.MonthlyCardModelData;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * 月卡服务类
 *
 * @author Created by U-Demon on 2016年12月20日 下午3:28:35
 * @version 1.0.0
 */
public class MonthlyCardService {

    private static Logger logger = Logger.getLogger(MonthlyCardService.class);

    /**
     * 购买月卡
     *
     * @param playerId
     * @param id
     * @return
     */
    public static boolean buyMonthlyCard(int playerId, byte id) {
        //在线玩家
        GameRole role = GameWorld.getPtr().getOnlineRole(playerId);
        Player player = null;
        //离线玩家
        if (role == null) {
            player = GameWorld.getPtr().getOfflinePlayer(playerId);
            //数据库
            if (player == null) {
                player = new PlayerDao().getPlayer(playerId);
            }
            if (player == null) {
                return false;
            }
            role = new GameRole(player);
            //role.getActivityManager().init();
        }
        boolean result = role.getActivityManager().buyMonthlyCard(id);
        //发送消息
        if (result) {
            role.putMessageQueue(role.getActivityManager().getMonthlyCardMsg());
        }
        return result;
    }

    /**
     * 每天月卡奖励任务
     */
    public static void dailyTask() {
        MonthlyCardModelData model = MonthlyCardModel.getModel(1);
        Mail mail = MailService.createMail(model.getTitle(), model.getContent(), EGoodsChangeType.MONTHLY_CARD_ADD,
                model.getReward());
        ActivityDao dao = new ActivityDao();
        logger.info("准备查询月卡用户...");
        List<PlayerActivity> cards = dao.getMonthlyCardList();
        logger.info("准备向月卡用户发放邮件...");
        for (PlayerActivity card : cards) {
            //发送邮件
            MailService.sendSystemMail(card.getPlayerId(), mail.clone());
        }
        dao.updateMonthlyCardRewardList();
        logger.info("月卡用户发放奖励邮件完毕...");

//		logger.info("准备查询终生卡用户...");
//		List<Player> forevers = dao.getForeverCardList();
//		logger.info("准备向终生卡用户发放邮件...");
//		MonthlyCardModelData modelForever = MonthlyCardModel.getModel(2);
//		Mail mailForever = MailService.createMail(modelForever.getTitle(), modelForever.getContent(), 
//				EGoodsChangeType.MONTHLY_CARD_ADD, modelForever.getReward());
//		for (Player card : forevers)
//		{
//			//发送邮件
//			MailService.sendSystemMail(card.getId(), mailForever.clone());
//		}
//		dao.updateForeverCardRewardList();
//		logger.info("终生卡用户发放奖励邮件完毕...");
    }

}
