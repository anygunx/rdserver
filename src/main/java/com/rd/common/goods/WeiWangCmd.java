package com.rd.common.goods;

import com.rd.bean.drop.DropData;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRole;

import java.util.EnumSet;

public class WeiWangCmd implements IGoodsCmd {

    private static final WeiWangCmd _instance = new WeiWangCmd();

    public static WeiWangCmd gi() {
        return _instance;
    }

    private WeiWangCmd() {
    }

    @Override
    public long getValue(GameRole role, DropData data) {
        return role.getPlayer().getWeiWang();
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
    public boolean consume(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet,
                           boolean isNotifyClient) {

        if (data.getN() <= 0)
            return false;

        if (!validate(role, data))
            return false;

        int value = -data.getN();

        role.getGuanJieManager().calculateWeiWang(value, enumSet);

        if (isNotifyClient) {

            role.sendUpdateCurrencyMsg(EGoodsType.WEIWANG, changeType);
        }

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

        role.getGuanJieManager().calculateWeiWang(value, enumSet);

        if (isNotifyClient) {
            role.sendUpdateCurrencyMsg(EGoodsType.WEIWANG, changeType);
        }

        return saveData(enumSet);
    }

    @Override
    public boolean saveData(EnumSet<EPlayerSaveType> enumSet) {
        enumSet.add(EPlayerSaveType.WEIWANG);
        return true;
    }

}
