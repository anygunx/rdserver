package com.rd.common.goods;

import com.rd.bean.drop.DropData;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRankManager;
import com.rd.game.GameRole;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.GameEvent;
import com.lg.bean.game.MoneyChange;
import com.rd.util.DateUtil;
import com.rd.util.LogUtil;
import org.apache.log4j.Logger;

import java.util.EnumSet;

public class DiamondCmd implements IGoodsCmd {

    private static final Logger logger = Logger.getLogger(DiamondCmd.class);

    private static final DiamondCmd _instance = new DiamondCmd();

    public static DiamondCmd gi() {
        return _instance;
    }

    private DiamondCmd() {
    }

    @Override
    public long getValue(GameRole role, DropData data) {
        return role.getPlayer().getDiamond();
    }

    @Override
    public boolean validate(GameRole role, DropData data) {
        long value = getValue(role, data);
        if (value < data.getN())
            return false;
        return true;
    }

    @Override
    public boolean consume(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        if (data.getN() <= 0)
            return false;
        if (!validate(role, data))
            return false;
        int value = -data.getN();
        role.getPlayer().changeDiamond(value);
        //每日消耗元宝数
        long curr = System.currentTimeMillis();
        int old = this.dealConsumeDailyStr(DateUtil.getDayStartTime(curr), role.getPlayer().getConsumeDaily());
        int sum = old + data.getN();
        String str = DateUtil.getDayStartTime(curr) + "," + sum;
        role.getPlayer().setConsumeDailyStr(str);
        GameRankManager.getInstance().addTargetConsumeTop(role, sum);
        role.sendUpdateCurrencyMsg(EGoodsType.DIAMOND, changeType);
        try {
            if (EGoodsChangeType.SLOT_MACHINE_CONSUME != changeType) {
                role.getActivityManager().handlerConsumeDiamond(data.getN());
            }
        } catch (Exception e) {
            logger.error("元宝消费活动发生异常。", e);
        }
        //记录玩家元宝变化日志
        LogUtil.log(role.getPlayer(), new MoneyChange(EGoodsType.DIAMOND.getId(), value, changeType.getId()));
        logger.info(role.getPlayerId() + "_" + role.getPlayer().getDiamond());
        role.getEventManager().notifyEvent(new GameEvent(EGameEventType.COST_DIAMOND, data.getN(), enumSet));
        enumSet.add(EPlayerSaveType.DAILY_CONSUM);
        return saveData(enumSet);
    }

    /**
     * 处理每日消耗
     *
     * @param currTime 当天凌晨时间戳
     * @param str      数据库
     * @return
     */
    public int dealConsumeDailyStr(long currTime, String str) {
        try {
            if (str == null || "".equals(str) || "null".equals(str) || "{}".equals(str)) return 0;
            if (str.indexOf(":") > -1) {
                String s = str.substring(str.lastIndexOf(":") + 1, str.length() - 1);
                return Integer.parseInt(s);
            } else {
                String[] strs = str.split(",");
                String str1 = strs[0];
                long oldTime = Long.parseLong(str1);
                if (oldTime != currTime) return 0;
                return Integer.parseInt(strs[1]);
            }
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        if (data.getN() <= 0)
            return false;
        int value = data.getN();
        role.getPlayer().changeDiamond(value);
        role.sendUpdateCurrencyMsg(EGoodsType.DIAMOND, changeType);
        //记录玩家元宝变化日志
        LogUtil.log(role.getPlayer(), new MoneyChange(EGoodsType.DIAMOND.getId(), value, changeType.getId()));
        logger.info(role.getPlayerId() + "_" + role.getPlayer().getDiamond());
        if (changeType == EGoodsChangeType.PAY_ADD) {
            role.getEventManager().notifyEvent(new GameEvent(EGameEventType.PAY_DIAMOND, data.getN(), enumSet));
        }
        return saveData(enumSet);
    }

    @Override
    public boolean saveData(EnumSet<EPlayerSaveType> enumSet) {
        enumSet.add(EPlayerSaveType.DIAMOND);
        return true;
    }

    @Override
    public boolean consume(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet,
                           boolean isNotifyClient) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet,
                          boolean isNotifyClient) {
        // TODO Auto-generated method stub
        return false;
    }

}
