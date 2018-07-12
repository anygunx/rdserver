package com.rd.common.goods;

import com.rd.bean.drop.DropData;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRole;
import com.lg.bean.game.MoneyChange;
import com.rd.util.LogUtil;

import java.util.EnumSet;

/**
 * 代金券
 *
 * @author wh
 */
public class MysteryIntegralCmd implements IGoodsCmd {

    private static final MysteryIntegralCmd _instance = new MysteryIntegralCmd();

    public static MysteryIntegralCmd gi() {
        return _instance;
    }

    private MysteryIntegralCmd() {
    }

    @Override
    public long getValue(GameRole role, DropData data) {
        return role.getPlayer().getMysteryIntegral();
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

        role.getPlayer().addMysteryIntegral(value);
        role.sendUpdateCurrencyMsg(EGoodsType.MYSTERY_INTEGRAL, changeType);
        LogUtil.log(role.getPlayer(), new MoneyChange(EGoodsType.MYSTERY_INTEGRAL.getId(), value, changeType.getId()));
        return saveData(enumSet);
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        if (data.getN() <= 0)
            return false;
        int value = data.getN();
        role.getPlayer().addMysteryIntegral(value);
        role.sendUpdateCurrencyMsg(EGoodsType.MYSTERY_INTEGRAL, changeType);
        LogUtil.log(role.getPlayer(), new MoneyChange(EGoodsType.MYSTERY_INTEGRAL.getId(), value, changeType.getId()));
        return saveData(enumSet);
    }

    public boolean saveData(EnumSet<EPlayerSaveType> enumSet) {
        enumSet.add(EPlayerSaveType.MYSTERY_INTEGRAL);
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
