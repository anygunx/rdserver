package com.rd.common.goods;

import com.rd.bean.drop.DropData;
import com.rd.bean.player.Player;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRole;
import com.lg.bean.game.Goods;
import com.rd.util.LogUtil;

import java.util.EnumSet;

public class YuanQiCmd implements IGoodsCmd {
    private static final YuanQiCmd _instance = new YuanQiCmd();

    public static YuanQiCmd gi() {
        return _instance;
    }

    private YuanQiCmd() {
    }

    @Override
    public long getValue(GameRole role, DropData data) {
        return role.getPlayer().getYuanqi();
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
        player.addYuanqi(value);
        role.sendUpdateCurrencyMsg(EGoodsType.YUANQI, changeType);
        return saveData(enumSet);
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        if (data.getN() <= 0)
            return false;
        int value = data.getN();
        Player player = role.getPlayer();
        player.addYuanqi(value);
        role.sendUpdateCurrencyMsg(EGoodsType.YUANQI, changeType);
        //记录玩家道具变化日志
        LogUtil.log(role.getPlayer(), new Goods(EGoodsType.YUANQI.getId(), 0, data.getN(), changeType.getId()));
        return saveData(enumSet);
    }


    @Override
    public boolean saveData(EnumSet<EPlayerSaveType> enumSet) {
        enumSet.add(EPlayerSaveType.YUANQI);
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
