package com.rd.common.goods;

import com.rd.bean.drop.DropData;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRole;
import com.lg.bean.game.MoneyChange;
import com.rd.util.LogUtil;

import java.util.EnumSet;

/**
 * 积分
 *
 * @author Created by U-Demon on 2016年12月27日 下午6:29:22
 * @version 1.0.0
 */
public class PointsCmd implements IGoodsCmd {

    private static final PointsCmd _instance = new PointsCmd();

    public static PointsCmd gi() {
        return _instance;
    }

    private PointsCmd() {
    }

    @Override
    public long getValue(GameRole role, DropData data) {
        return role.getPlayer().getPoints();
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
        if (data.getN() <= 0)
            return false;
        if (!validate(role, data))
            return false;
        int value = -data.getN();

        role.getPlayer().addPoints(value);
        role.sendUpdateCurrencyMsg(EGoodsType.POINTS, changeType);
        //记录玩家积分变化日志
        LogUtil.log(role.getPlayer(), new MoneyChange(EGoodsType.POINTS.getId(), value, changeType.getId()));
        return saveData(enumSet);
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        if (data.getN() <= 0)
            return false;
        int value = data.getN();
        role.getPlayer().addPoints(value);
        role.sendUpdateCurrencyMsg(EGoodsType.POINTS, changeType);
        //记录玩家积分变化日志
        LogUtil.log(role.getPlayer(), new MoneyChange(EGoodsType.POINTS.getId(), value, changeType.getId()));
        return saveData(enumSet);
    }

    @Override
    public boolean saveData(EnumSet<EPlayerSaveType> enumSet) {
        enumSet.add(EPlayerSaveType.POINTS);
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
