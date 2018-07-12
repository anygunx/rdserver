package com.rd.common.goods;

import com.lg.bean.game.MoneyChange;
import com.rd.bean.drop.DropData;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRole;
import com.rd.util.LogUtil;

import java.util.EnumSet;

/**
 * 竞技场点数
 *
 * @author Created by U-Demon on 2016年12月27日 下午6:29:22
 * @version 1.0.0
 */
public class ArenaCmd implements IGoodsCmd {

    private static final ArenaCmd _instance = new ArenaCmd();

    public static ArenaCmd gi() {
        return _instance;
    }

    private ArenaCmd() {
    }

    @Override
    public long getValue(GameRole role, DropData data) {
        return role.getPlayer().getArena();
    }

    @Override
    public boolean validate(GameRole role, DropData data) {
        long value = getValue(role, data);
        if (value < data.getN())
            return false;
        return true;
    }

    @Override
    public boolean consume(GameRole role, DropData data, EGoodsChangeType changeType,
                           EnumSet<EPlayerSaveType> enumSet) {
        return consume(role, data, changeType, enumSet, true);
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        return reward(role, data, changeType, enumSet, true);
    }

    @Override
    public boolean saveData(EnumSet<EPlayerSaveType> enumSet) {
        enumSet.add(EPlayerSaveType.ARENA);
        return true;
    }

    @Override
    public boolean consume(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet,
                           boolean isNotifyClient) {
        if (data.getN() <= 0)
            return false;
        if (!validate(role, data))
            return false;
        int value = -data.getN();

        role.getPlayer().addArena(value);
        if (isNotifyClient) {
            role.sendUpdateCurrencyMsg(EGoodsType.ARENA, changeType);
        }
        //记录玩家积分变化日志
        LogUtil.log(role.getPlayer(), new MoneyChange(EGoodsType.ARENA.getId(), value, changeType.getId()));
        return saveData(enumSet);
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet,
                          boolean isNotifyClient) {
        if (data.getN() <= 0)
            return false;
        int value = data.getN();
        role.getPlayer().addArena(value);
        if (isNotifyClient) {
            role.sendUpdateCurrencyMsg(EGoodsType.ARENA, changeType);
        }
        //记录玩家积分变化日志
        LogUtil.log(role.getPlayer(), new MoneyChange(EGoodsType.ARENA.getId(), value, changeType.getId()));
        return saveData(enumSet);
    }

}
