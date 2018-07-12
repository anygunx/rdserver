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
public class VouchersCmd implements IGoodsCmd {

    private static final VouchersCmd _instance = new VouchersCmd();

    public static VouchersCmd gi() {
        return _instance;
    }

    private VouchersCmd() {
    }

    @Override
    public long getValue(GameRole role, DropData data) {
        return role.getPlayer().getVouchers();
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

        role.getPlayer().addVouchers(value);
        role.sendUpdateCurrencyMsg(EGoodsType.VOUCHERS, changeType);
        LogUtil.log(role.getPlayer(), new MoneyChange(EGoodsType.VOUCHERS.getId(), value, changeType.getId()));
        return saveData(enumSet);
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        if (data.getN() <= 0)
            return false;
        int value = data.getN();
        role.getPlayer().addVouchers(value);
        role.sendUpdateCurrencyMsg(EGoodsType.VOUCHERS, changeType);
        LogUtil.log(role.getPlayer(), new MoneyChange(EGoodsType.VOUCHERS.getId(), value, changeType.getId()));
        return saveData(enumSet);
    }

    public boolean saveData(EnumSet<EPlayerSaveType> enumSet) {
        enumSet.add(EPlayerSaveType.VOUCHERS);
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
