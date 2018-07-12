package com.rd.common.goods;

import com.lg.bean.game.MoneyChange;
import com.rd.bean.drop.DropData;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRole;
import com.rd.util.LogUtil;

import java.util.EnumSet;

/**
 * 代金券
 *
 * @author wh
 */
public class QizhenIntegralCmd implements IGoodsCmd {

    private static final QizhenIntegralCmd _instance = new QizhenIntegralCmd();

    public static QizhenIntegralCmd gi() {
        return _instance;
    }

    private QizhenIntegralCmd() {
    }

    @Override
    public long getValue(GameRole role, DropData data) {
        return role.getPlayer().getQizhenIntegral();
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

        role.getPlayer().addQizhenIntegral(value);
        role.sendUpdateCurrencyMsg(EGoodsType.QIZHEN_INTEGRAL, changeType);
        LogUtil.log(role.getPlayer(), new MoneyChange(EGoodsType.QIZHEN_INTEGRAL.getId(), value, changeType.getId()));
        return saveData(enumSet);
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        if (data.getN() <= 0)
            return false;
        int value = data.getN();
        role.getPlayer().addQizhenIntegral(value);
        role.sendUpdateCurrencyMsg(EGoodsType.QIZHEN_INTEGRAL, changeType);
        LogUtil.log(role.getPlayer(), new MoneyChange(EGoodsType.QIZHEN_INTEGRAL.getId(), value, changeType.getId()));
        return saveData(enumSet);
    }

    public boolean saveData(EnumSet<EPlayerSaveType> enumSet) {
        enumSet.add(EPlayerSaveType.QIZHEN_INTEGRAL);
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
