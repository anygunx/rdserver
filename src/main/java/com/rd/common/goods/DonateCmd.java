package com.rd.common.goods;

import com.rd.bean.drop.DropData;
import com.rd.bean.player.Player;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRole;

import java.util.EnumSet;

/**
 * Created by XingYun on 2017/1/6.
 */
public class DonateCmd implements IGoodsCmd {
    private static final DonateCmd _instance = new DonateCmd();

    public static DonateCmd gi() {
        return _instance;
    }

    private DonateCmd() {
    }

    @Override
    public long getValue(GameRole role, DropData data) {
        return role.getPlayer().getDonate();
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
        Player player = role.getPlayer();
        player.changeDonate(value);
        role.sendUpdateCurrencyMsg(EGoodsType.DONATE, changeType);
        return saveData(enumSet);
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        if (data.getN() <= 0)
            return false;
        int value = data.getN();
        Player player = role.getPlayer();
        player.changeDonate(value);
        role.getGangManager().updateTotalDonate(value);
        role.sendUpdateCurrencyMsg(EGoodsType.DONATE, changeType);
        return saveData(enumSet);
    }


    @Override
    public boolean saveData(EnumSet<EPlayerSaveType> enumSet) {
        enumSet.add(EPlayerSaveType.DONATE);
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
