package com.rd.task.global;

import com.rd.activity.ActivityService;
import com.rd.common.BossService;
import com.rd.common.MonthlyCardService;
import com.rd.dao.PlayerDao;
import com.rd.dao.PlayerMonsterSiegeDao;
import com.rd.game.*;
import com.rd.game.manager.GuanJieManager;
import com.rd.model.LadderModel;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import com.rd.task.ETaskType;
import com.rd.task.Task;
import com.rd.task.TaskManager;
import com.rd.util.DateUtil;
import org.apache.log4j.Logger;

import java.time.DayOfWeek;

/**
 * 每日零点任务
 *
 * @author Created by U-Demon on 2016年11月4日 下午2:40:36
 * @version 1.0.0
 */
public class DailyTask implements Task {

    private static Logger logger = Logger.getLogger(DailyTask.class);

    @Override
    public void run() {
        dailyTask();
        int weekDay = DateUtil.getWeekDay();
        if (weekDay == DayOfWeek.MONDAY.getValue()) {
            weeklyTask();
        }
        sendDayRefreshMsg();
    }

    /**
     * 每周更新 为了与每日同步 放在此类中顺序执行
     */
    private void weeklyTask() {
        logger.info("weeklyTask start...");
        clearGameMonsterSiege();
        clearPlayerMonsterSiege();
        logger.info("weeklyTask end...");
    }

    private void clearGameMonsterSiege() {
        try {
            //怪物攻城每周重置
            GameMonsterSiegeService.onWeekly();
        } catch (Exception e) {
            logger.error("怪物攻城每周重置时发生异常.", e);
        }
    }

    private void clearPlayerMonsterSiege() {
        try {
            //玩家怪物攻城数据清理
            new PlayerMonsterSiegeDao().clear();
        } catch (Exception e) {
            logger.error("玩家怪物攻城数据清理时发生异常.", e);
        }
    }

    public void dailyTask() {
        logger.info("DailyTask start...");
        //更新活动全服数据
        ActivityService.updateServerData();
        //怪物攻城每日
        GameMonsterSiegeService.onDaily();
        //重置在线玩家状态		在前更新
        onlineReset();
        //重置天梯赛季时间数据
        ladderReset();
        //探索BOSS重置
        bossReset();
        //月卡奖励
        monthlyCardReward();
        //更新活动信息
        doUpdateActivity();
        //遭遇战排行榜每日更新
        dailyPVPReset();
        //帮会副本通关记录重置
        dailyGangDungeonPassReset();
        //回收威望
        recoveryWeiWang();

        //重置秘境BOSS的挑战次数
        restMysteryBossLeft();

        logger.info("DailyTask end...");
    }

    /**
     * 重置秘境BOSS的挑战次数到数据库
     */
    private void restMysteryBossLeft() {
        try {
            logger.info("每日重置玩家秘境BOSS挑战次数");
            new PlayerDao().restMysteryBossLeft();
        } catch (Exception e) {
            logger.error("每日重置玩家秘境BOSS挑战次数时发生异常.", e);
        }
    }

    /**
     * 0点回收服务器所有用户指定官阶威望，更新到数据库
     */
    private void recoveryWeiWang() {
        try {
            logger.info("每日回收所有玩家指定官阶威望");
            GuanJieManager.updateWeiWang();
        } catch (Exception e) {
            logger.error("每日回收所有玩家指定官阶威望时发生异常.", e);
        }
    }

    /**
     * 推送每日刷新消息
     */
    private void sendDayRefreshMsg() {
        for (GameRole role : GameWorld.getPtr().getOnlineRoles().values()) {
            role.putMessageQueue(role.getPlayer().getDayRefreshMsg());
            role.putMessageQueue(role.getMissionManager().getDragonBallMessage());
            Message msg = new Message(MessageCommand.BROADCAST_PET_MESSAGE);
            msg.setByte(role.getPlayer().getDayData().getPetFree());
            msg.setBool(true);
            msg.setByte(0);
            role.putMessageQueue(msg);
        }
    }

    /**
     * 重置在线玩家状态
     */
    private void onlineReset() {
        try {
            GameWorld.getPtr().resetDailyState();
        } catch (Exception e) {
            logger.error("重置在线玩家状态时发生异常.", e);
        }
    }

    /**
     * 更新活动状态
     */
    private void doUpdateActivity() {
        try {
            TaskManager.getInstance().scheduleDelayTask(ETaskType.ACTIVITY,
                    new Task() {
                        @Override
                        public void run() {
                            ActivityService.dailyTask();
                        }

                        @Override
                        public String name() {
                            return "ActivityDailyTask";
                        }
                    }, 100);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void monthlyCardReward() {
        try {
            logger.info("月卡奖励发放开始");
            MonthlyCardService.dailyTask();
            logger.info("月卡奖励发放完毕");
        } catch (Exception e) {
            logger.error("月卡奖励发放时发生异常.", e);
        }
    }

    private void bossReset() {
        try {
            logger.info("探索BOSS重置");
            BossService.dailyReset();
        } catch (Exception e) {
            logger.error("探索BOSS重置时发生异常.", e);
        }
    }

    /**
     * 重置天梯数据
     */
    private void ladderReset() {
        try {
            logger.info("天梯竞技场每日数据重置");
            LadderModel.refreshSeasonTime();
        } catch (Exception e) {
            logger.error("天梯竞技场每日数据重置失败.", e);
        }
    }

    private void dailyPVPReset() {
        try {
            logger.info("每日遭遇战排行榜重置");
            GamePvpManager.getInstance().resetRank();
        } catch (Exception e) {
            logger.error("每日遭遇战排行榜重置时发生异常.", e);
        }
    }

    private void dailyGangDungeonPassReset() {
        try {
            logger.info("每日帮会副本通关记录重置");
            GameGangManager.getInstance().resetDungeon();
        } catch (Exception e) {
            logger.error("每日帮会副本通关记录重置时发生异常.", e);
        }
    }

    @Override
    public String name() {
        return "daily";
    }

}
