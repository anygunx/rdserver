package com.rd.common.goods;

import com.lg.bean.game.MoneyChange;
import com.rd.bean.drop.DropData;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRole;
import com.rd.util.LogUtil;

import java.util.EnumSet;

/**
 * 荣誉
 *
 * @author Created by U-Demon on 2016年11月9日 下午2:05:56
 * @version 1.0.0
 */
public class HonorCmd implements IGoodsCmd {

    private static final HonorCmd _instance = new HonorCmd();

    private HonorCmd() {
    }

    public static HonorCmd gi() {
        return _instance;
    }

    @Override
    public long getValue(GameRole role, DropData data) {
        return role.getPlayer().getHonor();
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
        role.getPlayer().addHonor(value);
        role.sendUpdateCurrencyMsg(EGoodsType.HONOR, changeType);
        //记录玩家荣誉变化日志
        LogUtil.log(role.getPlayer(), new MoneyChange(EGoodsType.HONOR.getId(), value, changeType.getId()));
        return saveData(enumSet);
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        if (data.getN() <= 0)
            return false;
        int value = data.getN();
        role.getPlayer().addHonor(value);
        role.sendUpdateCurrencyMsg(EGoodsType.HONOR, changeType);
        //记录玩家荣誉变化日志
        LogUtil.log(role.getPlayer(), new MoneyChange(EGoodsType.HONOR.getId(), value, changeType.getId()));
        return saveData(enumSet);
    }

    @Override
    public boolean saveData(EnumSet<EPlayerSaveType> enumSet) {
        enumSet.add(EPlayerSaveType.HONOR);
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
