package com.rd.activity.event;

import com.rd.activity.ActivityService;
import com.rd.activity.EActivityType;
import com.rd.activity.data.TurntableLogicData;
import com.rd.bean.player.PlayerActivity;
import com.rd.dao.ActivityDao;
import com.rd.game.GameRole;
import com.rd.game.PlayerTurntableInfosService;
import com.rd.game.PlayerTurntableInfosService.PlayerTurntableInfo;
import com.rd.net.message.Message;
import org.apache.log4j.Logger;

import java.util.*;

public class TurntableEvent implements IActivityEvent {

    private static Logger logger = Logger.getLogger(TurntableEvent.class);

    @Override
    public boolean onStart() {
        return false;
    }

    @Override
    public boolean onEnd() {
        logger.info("元宝王者活动结束");
        return false;
    }

    @Override
    public void getMessage(Message msg, GameRole role) {
        PlayerActivity activityData = role.getActivityManager().getActivityData();
        ActivityDao activityDao = role.getActivityManager().getActivityDao();
        //是否首次参加
        int turntableRound = activityData.getTurntableRound();
        int round = -1;
        if (turntableRound <= 0) {
            round = (int) (Math.random() * 7);
            activityData.setTurntableRound(round);
            activityDao.updateTurntableRound(activityData);
        } else {
            round = turntableRound;
        }
        Map<String, TurntableLogicData> logics = ActivityService.getRoundData(EActivityType.TURN_TABLE, round);
        if (logics == null) {
            return;
        }
        Map<String, Integer> orders = role.getPayManager().getDailyOrderByAmount();

        List<TurntableLogicData> tlds = new ArrayList<>(logics.values());
        Collections.sort(tlds, new Comparator<TurntableLogicData>() {

            @Override
            public int compare(TurntableLogicData o1, TurntableLogicData o2) {
                if (o1.getId() > o2.getId()) return 1;
                if (o1.getId() < o2.getId()) return -1;
                return 0;
            }
        });
        //int paySum = 10000;//role.getPayManager().getTodayDiamondInPay();
        Map<String, Integer> usedOrder = activityData.setTodayUsedOrderJson(activityDao.getTodayUsedOrder(activityData));
        for (String uid : orders.keySet()) {
            for (String usedUID : usedOrder.keySet()) {
                if (uid.equals(usedUID)) {
                    orders.put(uid, 0);
                }
            }
        }

        int turntableData = activityData.getTurntableData();
        int highest = 0;
        for (TurntableLogicData data : tlds) {
            if (data.getId() > highest) {
                highest = data.getId();
            }
        }
        if (turntableData < highest) {
            for (String uid : orders.keySet()) {
                int o = orders.get(uid);
                for (TurntableLogicData data : tlds) {
                    if (o >= data.getId() && activityData.getTurntableData() < data.getId() &&
                            !activityData.getTurntableReceiveNum().contains(data.getId())) {

                        activityData.addTurntableReceivceNum(data.getId());
                        break;
                    }
                }
                if (o != 0) {
                    activityData.getTodayUsedOrder().put(uid, o);
                }
            }
        }
        //档位下坐标
        int coordi = 0;
        for (int i = 0; i < tlds.size(); i++) {
            if (turntableData == 0) {
                break;
            }
            if (tlds.get(i).getId() == turntableData) {
                coordi = i + 1;
                break;
            }
        }
        int len = activityData.getTurntableReceiveNum().size();
        msg.setByte(len);
        ArrayList<Integer> list = new ArrayList<>(activityData.getTurntableReceiveNum());
        Collections.sort(list);
        for (int i = 0; i < len; i++) {
            msg.setInt(list.get(i));
        }
        List<PlayerTurntableInfo> ptis = PlayerTurntableInfosService.getPlayerTurntableInfos();
        int size = ptis.size() > 5 ? 5 : ptis.size();
        msg.setByte(size);
        for (int i = 0; i < size; i++) {
            msg.setString(ptis.get(i).getPlayerName());
            msg.setInt(ptis.get(i).getReward());
        }
        msg.setInt(round);
        msg.setByte(coordi);
        activityDao.updateTurntableReceiveNums(activityData);
        activityDao.updateTodayUsedOrder(activityData);

    }

    @Override
    public boolean isOpen(GameRole role) {
        return true;
    }

}
