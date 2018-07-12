package com.rd.common.goods;

import com.lg.bean.game.MoneyChange;
import com.rd.bean.drop.DropData;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRole;
import com.rd.util.LogUtil;

import java.util.EnumSet;

/**
 * 限时积分
 * Created by XingYun on 2017/1/23.
 */
public class TLPointsCmd implements IGoodsCmd {

    private static final TLPointsCmd _instance = new TLPointsCmd();

    public static TLPointsCmd gi() {
        return _instance;
    }

    private TLPointsCmd() {
    }

    @Override
    public long getValue(GameRole role, DropData data) {
        return role.getPlayer().getTlPoints();
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

        role.getPlayer().addTLPoints(value);
        role.sendUpdateCurrencyMsg(EGoodsType.TLPOINTS, changeType);
        LogUtil.log(role.getPlayer(), new MoneyChange(EGoodsType.TLPOINTS.getId(), value, changeType.getId()));
        return saveData(enumSet);
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        if (data.getN() <= 0)
            return false;
        int value = data.getN();
        role.getPlayer().addTLPoints(value);
        role.sendUpdateCurrencyMsg(EGoodsType.TLPOINTS, changeType);
        LogUtil.log(role.getPlayer(), new MoneyChange(EGoodsType.TLPOINTS.getId(), value, changeType.getId()));
        return saveData(enumSet);
    }

    @Override
    public boolean saveData(EnumSet<EPlayerSaveType> enumSet) {
        enumSet.add(EPlayerSaveType.TLPOINTS);
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
