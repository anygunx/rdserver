package com.rd.task.global;

import com.rd.bean.ladder.PlayerLadder;
import com.rd.bean.mail.Mail;
import com.rd.common.MailService;
import com.rd.dao.LadderDao;
import com.rd.define.EGoodsChangeType;
import com.rd.define.LadderDefine.LadderRGS;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.model.LadderModel;
import com.rd.model.data.LadderSeasonReward;
import com.rd.task.ETaskType;
import com.rd.task.Task;
import com.rd.task.TaskManager;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * 天梯奖励结算
 *
 * @author Created by U-Demon on 2016年11月1日 下午6:45:30
 * @version 1.0.0
 */
public class LadderRewardTask implements Task {

    private static Logger logger = Logger.getLogger(LadderRewardTask.class);

    @Override
    public void run() {
        ladderRewardTask();
    }

    public static void ladderRewardTask() {
        LadderDao dao = new LadderDao();
        //对天梯进行排名
        dao.seasonRank();
        logger.info("天梯赛季排名结束");
        //更新排行榜数据
        LadderModel.loadRankList();
        logger.info("从数据库读取天梯竞技场信息结束");
        LadderModel.refreshRankPlayerInfo();
        logger.info("天梯竞技场排行榜角色信息结束");
        //天梯发放奖励
        List<PlayerLadder> ladders = dao.getAllLadders();
        logger.info("满足条件玩家数量：" + ladders.size());
        handlerLadderReward(ladders);
        logger.info("向满足条件玩家发放奖励结束");
        //处理在线玩家
        handlerOnlinePlayers();
        logger.info("处理在线结束");
        LadderModel.topList.clear();
        logger.info("清空排行结束");
        //重置天梯赛季数据
        dao.seasonReset();
        logger.info("天梯重置数据结束");
    }

    @Override
    public String name() {
        return "LadderRewardTask";
    }

    /**
     * 天梯发放奖励
     */
    private static void handlerLadderReward(final List<PlayerLadder> ladders) {
        TaskManager.getInstance().scheduleTask(ETaskType.COMMON, new Task() {
            @Override
            public void run() {
                for (int i = 0; i < ladders.size(); i++) {
                    PlayerLadder ladder = ladders.get(i);
                    try {
                        //段位奖励
                        LadderRGS rgs = LadderModel.getRGS(ladder.getStar());
                        LadderSeasonReward rewardData = LadderModel.getRankReward(rgs.rank);
                        Mail rewardMail = MailService.createMail(rewardData.getTitle(), rewardData.getContent(),
                                EGoodsChangeType.LADDER_SEASON_ADD, rewardData.getReward());
                        MailService.sendSystemMail(ladder.getPlayerId(), rewardMail);
                        //排名奖励
                        if (ladder.getLastStar() >= LadderModel.MAX_STAR && ladder.getLastRank() >= 1
                                && ladder.getLastRank() <= 5) {
                            LadderSeasonReward rd = LadderModel.getOrderReward(ladder.getLastRank());
                            if (rd != null) {
                                Mail orderMail = MailService.createMail(rd.getTitle(), rd.getContent(),
                                        EGoodsChangeType.LADDER_SEASON_ADD, rd.getReward());
                                MailService.sendSystemMail(ladder.getPlayerId(), orderMail);

                            }
                        }
                    } catch (Exception e) {
                        logger.error("向玩家：" + ladder.getPlayerId() + "发放天梯奖励异常。", e);
                    }
                }
            }

            @Override
            public String name() {
                return "handlerLadderReward";
            }
        });
    }

    private static void handlerOnlinePlayers() {
        //在线玩家
        for (GameRole role : GameWorld.getPtr().getOnlineRoles().values()) {
            if (role == null)
                continue;
            try {
                role.getLadderManager().resetSeasonLadder();
                role.getLadderManager().sendLadderDetail();
            } catch (Exception e) {
                logger.error("重置在线玩家的天梯赛季数据异常。", e);
            }
        }
    }

}
