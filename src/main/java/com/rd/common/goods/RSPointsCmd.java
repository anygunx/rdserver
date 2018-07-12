package com.rd.common.goods;

import com.rd.bean.drop.DropData;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRole;
import com.lg.bean.game.MoneyChange;
import com.rd.util.LogUtil;

import java.util.EnumSet;

/**
 * 商城积分
 * Created by XingYun on 2017/1/23.
 */
public class RSPointsCmd implements IGoodsCmd {

    private static final RSPointsCmd _instance = new RSPointsCmd();

    public static RSPointsCmd gi() {
        return _instance;
    }

    private RSPointsCmd() {
    }

    @Override
    public long getValue(GameRole role, DropData data) {
        return role.getPlayer().getRsPoints();
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

        role.getPlayer().addRsPoints(value);
        role.sendUpdateCurrencyMsg(EGoodsType.RSPOINTS, changeType);
        LogUtil.log(role.getPlayer(), new MoneyChange(EGoodsType.RSPOINTS.getId(), value, changeType.getId()));
        return saveData(enumSet);
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        if (data.getN() <= 0)
            return false;
        int value = data.getN();
        role.getPlayer().addRsPoints(value);
        role.sendUpdateCurrencyMsg(EGoodsType.RSPOINTS, changeType);
        LogUtil.log(role.getPlayer(), new MoneyChange(EGoodsType.RSPOINTS.getId(), value, changeType.getId()));
        return saveData(enumSet);
    }

    @Override
    public boolean saveData(EnumSet<EPlayerSaveType> enumSet) {
        enumSet.add(EPlayerSaveType.RSPOINTS);
        return true;
    }

    @Override
    public boolean consume(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet,
                           boolean isNotifyClient) {
        return false;
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet,
                          boolean isNotifyClient) {
        // TODO Auto-generated method stub
        return false;
    }

}
