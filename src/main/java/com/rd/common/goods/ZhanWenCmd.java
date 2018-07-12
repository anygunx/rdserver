package com.rd.common.goods;

import com.lg.bean.game.MoneyChange;
import com.rd.bean.drop.DropData;
import com.rd.bean.goods.ZhanWen;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRole;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import com.rd.util.LogUtil;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class ZhanWenCmd implements IGoodsCmd {

    private static final ZhanWenCmd _instance = new ZhanWenCmd();

    public static ZhanWenCmd gi() {
        return _instance;
    }

    @Override
    public long getValue(GameRole role, DropData data) {
        ZhanWen zhanWen = role.getPlayer().getZhanWens().get(data.getG());
        if (zhanWen == null) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public boolean validate(GameRole role, DropData data) {

        return getValue(role, data) >= data.getN();
    }

    @Override
    public boolean consume(GameRole role, DropData data, EGoodsChangeType changeType,
                           EnumSet<EPlayerSaveType> enumSet) {
        return consume(role, data, changeType, enumSet, true);
    }

    @Override
    public boolean consume(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet,
                           boolean isNotifyClient) {

        Map<Integer, ZhanWen> zhanWens = role.getPlayer().getZhanWens();

        Integer idx = new Integer(data.getG());

        if (!zhanWens.containsKey(idx)) {
            return false;
        }

        ZhanWen zhanWen = zhanWens.remove(idx);

        Message msg = new Message(MessageCommand.ZHANWEN_REWARD_MESSAGE);
        //消耗
        msg.setInt(changeType.getId());
        msg.setByte(0);
        msg.setByte(1);
        zhanWen.getMessage(msg);
        role.putMessageQueue(msg);

        //记录玩家战纹变化日志
        LogUtil.log(role.getPlayer(), new MoneyChange(EGoodsType.ZHANWEN.getId(), data.getG(), changeType.getId()));

        return saveData(enumSet);
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        return reward(role, data, changeType, enumSet, true);
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet,
                          boolean isNotifyClient) {

        if (data.getG() <= 0) {
            return false;
        }

        Map<Integer, ZhanWen> zhanWens = role.getPlayer().getZhanWens();

        List<ZhanWen> list = new ArrayList<ZhanWen>();
        for (int i = 0; i < data.getN(); ++i) {
            ZhanWen zhanWen = new ZhanWen();
            zhanWen.setD((short) role.getPackManager().getZhanWenNewId());
            zhanWen.setG(data.getG());
            zhanWens.put(zhanWen.getD(), zhanWen);
            list.add(zhanWen);
        }

        Message msg = new Message(MessageCommand.ZHANWEN_REWARD_MESSAGE);
        //获取
        msg.setInt(changeType.getId());
        msg.setByte(1);
        msg.setByte(list.size());
        for (ZhanWen zhanWen : list) {
            zhanWen.getMessage(msg);
        }
        role.putMessageQueue(msg);

        //记录玩家战纹变化日志
        LogUtil.log(role.getPlayer(), new MoneyChange(EGoodsType.ZHANWEN.getId(), data.getG(), changeType.getId()));

        return saveData(enumSet);
    }

    @Override
    public boolean saveData(EnumSet<EPlayerSaveType> enumSet) {
        enumSet.add(EPlayerSaveType.ZHANWEN);
        return true;
    }

}
