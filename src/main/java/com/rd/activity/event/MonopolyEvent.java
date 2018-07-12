package com.rd.activity.event;

import com.rd.activity.ActivityService;
import com.rd.activity.EActivityType;
import com.rd.activity.config.BaseActivityConfig;
import com.rd.activity.data.MonopolyLogicData;
import com.rd.activity.data.MonopolyLogicData.NumInfo;
import com.rd.activity.group.ActivityGroupData;
import com.rd.activity.group.ActivityRoundConfig;
import com.rd.bean.drop.DropData;
import com.rd.bean.mail.Mail;
import com.rd.bean.player.PlayerActivity;
import com.rd.common.MailService;
import com.rd.dao.ActivityDao;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRole;
import com.rd.net.message.Message;
import com.rd.util.DiceUtil;
import org.apache.log4j.Logger;

import java.util.*;

public class MonopolyEvent implements IActivityEvent {

    private static Logger logger = Logger.getLogger(MonopolyEvent.class);

    @Override
    public boolean onStart() {
        return false;
    }

    @Override
    public boolean onEnd() {
        logger.info("大富翁活动结束！");
        ActivityDao dao = new ActivityDao();
        dao.clearMonopolyData();
        Map<String, MonopolyLogicData> logics = ActivityService.getRoundData(EActivityType.MONOPOLY, 0);
        MonopolyLogicData mld = logics.get("1");
        List<Integer> allReceives = new ArrayList<>(mld.getLevelInfos().keySet());

        for (PlayerActivity activity : dao.getPlayerActivitys2Monopoly()) {
            List<Integer> received = activity.getMonopolyLevelReceived();
            int highestLevelReceived = 0;
            for (int i : received) {
                if (i > highestLevelReceived) {
                    highestLevelReceived = i;
                }
            }
            int playLevel = activity.getMonopolyTodayPlayLevel();
            List<DropData> dds = new ArrayList<>();
            boolean flag = true;
            for (int i : allReceives) {
                if (playLevel < i || highestLevelReceived > i) continue;
                for (DropData dd : mld.getLevelInfos().get(i)) {
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
            Mail mail = MailService.createMail("大富翁层数未领取奖励", "大富翁层数未领取奖励", EGoodsChangeType.MONOPOLY_RECEIVE, dds);
            MailService.sendSystemMail(activity.getPlayerId(), mail);
            activity.resetMonopolyLevelReceived();
            dao.updateMonopolyLevelReceived(activity);
        }
        dao.clearMonoply();
        return false;
    }

    @Override
    public void getMessage(Message msg, GameRole role) {
        long curr = System.currentTimeMillis();

        BaseActivityConfig configData = ActivityService.getActivityConfig(EActivityType.MONOPOLY);
        ActivityRoundConfig currRound = configData.getCurrRound(0, curr);
        if (currRound == null)
            return;
        ActivityGroupData group = ActivityService.getGroupData(EActivityType.MONOPOLY);
        int round = group.getDataRound(currRound.getRound());
        Map<String, MonopolyLogicData> mlds = ActivityService.getRoundData(EActivityType.MONOPOLY, role.getPlayerId(), curr);
        PlayerActivity activityData = role.getActivityManager().getActivityData();
        ActivityDao dao = role.getActivityManager().getActivityDao();
        int currLevel = activityData.getMonopolyCurrLevel();
        Set<Integer> set = new HashSet<>();
        for (String str : mlds.keySet()) {
            set.add(Integer.valueOf(str));
        }
        if (currLevel <= 0) {
            currLevel = DiceUtil.getIntByOrder(currLevel, set);
            activityData.setMonopolyCurrLevel(currLevel);
            dao.updateMonopolyCurrLevel(activityData);
        }
        MonopolyLogicData mld = mlds.get(currLevel + "");
        //过滤使用过的订单
        Map<String, Integer> orders = role.getPayManager().getAllOrders(configData.getStartTime(0), configData.getEndTime());
        Set<String> usedOrders = activityData.getMonopolyUsedOrder();
        orders.keySet().removeAll(usedOrders);
        int playerTime = activityData.getMonopolyPlayerTime();
        List<Integer> rechargeList = new ArrayList<>(mld.getRechargeMap().keySet());
        Collections.sort(rechargeList, new Comparator<Integer>() {

            @Override
            public int compare(Integer o1, Integer o2) {
                if (o1 < o2) return 1;
                if (o1 > o2) return -1;
                return 0;
            }
        });
        for (Integer i : orders.values()) {
            for (Integer j : rechargeList) {
                if (i >= j * 100) {
                    playerTime += mld.getRechargeMap().get(j);
                    activityData.setMonopolyPlayerTime(playerTime);
                    break;
                }
            }
        }
        //未用订单添加到已用订单
        for (String uid : orders.keySet()) {
            activityData.getMonopolyUsedOrder().add(uid);
        }

        dao.updateMonopolyPlayerTime(activityData);
        dao.updateMonopolyRechargeOrder(activityData);
        List<Integer> todayNumReceives = activityData.getMonopolyTodayNumReceive();
        List<Integer> levelReceived = activityData.getMonopolyLevelReceived();
        int todayPlayLevel = activityData.getMonopolyTodayPlayLevel();
        int todayNum = activityData.getMonopolyTodayNum();
        int nextLevel = activityData.getMonopolyNextLevel();
        if (nextLevel <= 0) {
            nextLevel = DiceUtil.getIntByOrder(currLevel, set);
            activityData.setMonopolyNextLevel(nextLevel);
            dao.updateMonopolyNextLevel(activityData);
        }
        int currSteps = activityData.getMonopolyCurrSteps();
        Map<Integer, NumInfo> numInfos = mld.getNumInfos();

        int numReceive = 1;

        int highestNum = 0;
        for (Integer n : todayNumReceives) {
            if (highestNum < n) {
                highestNum = n;
            }
        }
        for (NumInfo ni : numInfos.values()) {
            if (ni.getNum() == highestNum) {
                numReceive = ni.getId() + 1;
                break;
            }
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
        msg.setInt(todayNum);
        //今日未领取次数
        msg.setInt(numReceive);
        //层数已领奖数组大小
        msg.setByte(levelReceived.size());
        //层数已领取id
        for (int i : levelReceived) {
            msg.setInt(i);
        }
        msg.setInt(playerTime);

        msg.setByte(activityData.getDiceOne());
        msg.setByte(activityData.getDiceTwo());
        msg.setByte(activityData.getStepIds().size());
        for (Integer id : activityData.getStepIds()) {
            msg.setInt(id);
        }
        msg.setByte(round);
        activityData.setDiceOne(0);
        activityData.setDiceTwo(0);
        activityData.resetStepIds();
    }

    @Override
    public boolean isOpen(GameRole role) {
        return true;
    }
}
