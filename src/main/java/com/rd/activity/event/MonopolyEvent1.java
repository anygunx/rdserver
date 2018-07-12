package com.rd.activity.event;

import com.rd.activity.ActivityService;
import com.rd.activity.EActivityType;
import com.rd.activity.config.BaseActivityConfig;
import com.rd.activity.data.MonopolyLogicData1;
import com.rd.activity.data.MonopolyLogicData1.StepInfo;
import com.rd.activity.group.ActivityGroupData;
import com.rd.activity.group.ActivityRoundConfig;
import com.rd.bean.drop.DropData;
import com.rd.bean.mail.Mail;
import com.rd.bean.player.PlayerActivity;
import com.rd.common.MailService;
import com.rd.dao.ActivityDao;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRole;
import com.rd.game.manager.ActivityManager;
import com.rd.net.message.Message;
import org.apache.log4j.Logger;

import java.util.*;

public class MonopolyEvent1 implements IActivityEvent {

    private static Logger logger = Logger.getLogger(MonopolyEvent1.class);

    @Override
    public boolean onStart() {
        return false;
    }

    @Override
    public boolean onEnd() {
        logger.info("大富翁1活动结束！");
        ActivityDao dao = new ActivityDao();
        Map<String, MonopolyLogicData1> logics = ActivityService.getRoundData(EActivityType.MONOPOLY1, 0);
        MonopolyLogicData1 mld = logics.get("1");
        List<Integer> allReceives = new ArrayList<>(mld.getLevelInfos().keySet());
        //FIX
        for (PlayerActivity activity : dao.getPlayerActivitys2Monopoly1()) {
            List<Integer> received = activity.getMonopoly1LevelReceived();
            int highestLevelReceived = 0;
            for (int i : received) {
                if (i > highestLevelReceived) {
                    highestLevelReceived = i;
                }
            }
            int playLevel = activity.getMonopoly1TodayPlayLevel();
            List<DropData> dds = new ArrayList<>();
            boolean flag = true;
            for (int i : allReceives) {
                if (playLevel < i || highestLevelReceived > i) continue;
                for (DropData dd : mld.getLevelInfos().get(i).getReward()) {
                    if (dds.isEmpty()) {
                        dds.add(dd);
                    } else {
                        int size = dds.size();
                        for (int j = 0; j < size; j++) {
                            DropData dropData = dds.get(j);
                            if (dropData.getT() == dd.getT() && dropData.getG() == dd.getG()) {
                                int sum = dd.getN() + dropData.getN();
                                dd.setN(sum);
                                dds.set(j, dd);
                                flag = false;
                                break;
                            }
                        }
                        if (flag) {
                            dds.add(dd);
                        }
                    }
                }
            }
            Mail mail = MailService.createMail("大富翁层数未领取奖励", "大富翁层数未领取奖励", EGoodsChangeType.MONOPOLY1_ADD, dds);
            MailService.sendSystemMail(activity.getPlayerId(), mail);
        }
        dao.clearMonopoly1Data();
        return false;
    }

    @Override
    public void getMessage(Message msg, GameRole role) {
        long curr = System.currentTimeMillis();

        BaseActivityConfig configData = ActivityService.getActivityConfig(EActivityType.MONOPOLY1);
        ActivityRoundConfig currRound = configData.getCurrRound(0, curr);
        if (currRound == null)
            return;
        ActivityGroupData group = ActivityService.getGroupData(EActivityType.MONOPOLY1);
        Map<String, MonopolyLogicData1> mlds = ActivityService.getRoundData(EActivityType.MONOPOLY1, role.getPlayerId(), curr);
        PlayerActivity activityData = role.getActivityManager().getActivityData();
        ActivityManager activityManager = role.getActivityManager();
        ActivityDao dao = role.getActivityManager().getActivityDao();
        int currLevel = activityData.getMonopoly1CurrLevel();
        int currSteps = activityData.getMonopoly1CurrSteps();
        int playedAllLevel = activityData.getMonopoly1TodayPlayLevel();
        Set<Integer> set = new HashSet<>();
        for (String str : mlds.keySet()) {
            set.add(Integer.valueOf(str));
        }
        if (currLevel <= 0) {
            currLevel = role.getActivityManager().getNextLevel(playedAllLevel, mlds);
            activityData.setMonopoly1CurrLevel(currLevel);
            dao.updateMonopoly4CurrLevel(activityData);
        }
        MonopolyLogicData1 mld = role.getActivityManager().getMonopolyDataIdByCurrLevel(currLevel, currSteps, mlds);

        List<Integer> todayStepReceives = activityData.getMonopoly1TodayStepReceive();
        List<Integer> levelReceived = activityData.getMonopoly1LevelReceived();
        int todayPlayLevel = activityData.getMonopoly1TodayPlayLevel();
        int nextLevel = activityData.getMonopoly1NextLevel();
        if (nextLevel <= 0) {
            nextLevel = role.getActivityManager().getNextLevel(playedAllLevel + 1, mlds);
            activityData.setMonopoly1NextLevel(nextLevel);
            dao.updateMonopoly4NextLevel(activityData);
        }
        Map<Integer, StepInfo> stepInfos = mld.getStepInfos();

        int stepReceive = 1;

        int highestStep = 0;
        for (Integer n : todayStepReceives) {
            if (highestStep < n) {
                highestStep = n;
            }
        }
        for (StepInfo si : stepInfos.values()) {
            if (si.getId() == highestStep) {
                stepReceive = si.getId() + 1;
                break;
            }
        }
        if (stepReceive > highestStep && highestStep == activityManager.getMaxRewardStep()) {
            stepReceive = 0;
        }
        //当前层数
        msg.setByte(currLevel);
        //下次层数
        msg.setByte(nextLevel);
        //今日已玩层数
        msg.setInt(todayPlayLevel);
        //当前层数的步数
        msg.setByte(currSteps);
        //今日次数
//		msg.setInt(todayNum);
        //今日未领取步数奖励
        msg.setInt(stepReceive);
        //层数已领奖数组大小
        msg.setByte(levelReceived.size());
        //层数已领取id
        for (int i : levelReceived) {
            msg.setInt(i);
        }

        msg.setByte(activityData.getMonopoly1DiceOne());
        msg.setInt(activityData.getMonopoly1ResetNum());
        msg.setInt(activityData.getMonopoly1PlayedStep());
        msg.setInt(activityData.getMonopoly1FreeNum());
        msg.setByte(activityData.getMonopoly1Status());
        activityData.setMonopoly1DiceOne(0);
        activityData.resetMonopoly1StepIds();
        activityData.setMonopoly1Status((byte) 0);
    }

    @Override
    public boolean isOpen(GameRole role) {
        return true;
    }
}
