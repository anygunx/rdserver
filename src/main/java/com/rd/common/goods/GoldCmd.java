package com.rd.common.goods;

import com.lg.bean.game.MoneyChange;
import com.rd.bean.drop.DropData;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRole;
import com.rd.util.LogUtil;

import java.util.EnumSet;

public class GoldCmd implements IGoodsCmd {

    private static final GoldCmd _instance = new GoldCmd();

    public static GoldCmd gi() {
        return _instance;
    }

    private GoldCmd() {
    }

    @Override
    public long getValue(GameRole role, DropData data) {
        return role.getPlayer().getGold();
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
        return consume(role, data, changeType, enumSet, true);
    }

    @Override
    public boolean consume(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet,
                           boolean isNotifyClient) {
        if (data.getN() <= 0)
            return false;
        if (!validate(role, data))
            return false;

        int value = -data.getN();

        role.getPlayer().changeGold(value);
        if (isNotifyClient) {
            role.sendUpdateCurrencyMsg(EGoodsType.GOLD, changeType);
        }
        //记录玩家绑元变化日志
        LogUtil.log(role.getPlayer(), new MoneyChange(EGoodsType.GOLD.getId(), value, changeType.getId()));
        return saveData(enumSet);
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        return reward(role, data, changeType, enumSet, true);
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet,
                          boolean isNotifyClient) {
        if (data.getN() <= 0)
            return false;
        int value = data.getN();
        role.getPlayer().changeGold(value);
        if (isNotifyClient) {
            role.sendUpdateCurrencyMsg(EGoodsType.GOLD, changeType);
        }
        //记录玩家绑元变化日志
        LogUtil.log(role.getPlayer(), new MoneyChange(EGoodsType.GOLD.getId(), value, changeType.getId()));
        return saveData(enumSet);
    }

    @Override
    public boolean saveData(EnumSet<EPlayerSaveType> enumSet) {
        enumSet.add(EPlayerSaveType.GOLD);
        return true;
    }
}
