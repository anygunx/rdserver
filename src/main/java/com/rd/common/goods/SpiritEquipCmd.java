package com.rd.common.goods;

import com.rd.bean.drop.DropData;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRole;
import com.lg.bean.game.Goods;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import com.rd.util.LogUtil;

import java.util.EnumSet;

public class SpiritEquipCmd implements IGoodsCmd {

    private static final SpiritEquipCmd _instance = new SpiritEquipCmd();

    public static SpiritEquipCmd gi() {
        return _instance;
    }

    private SpiritEquipCmd() {
    }

    @Override
    public long getValue(GameRole role, DropData data) {
        com.rd.bean.goods.Goods goods = role.getPackManager().getWingGod(data.getG());
        if (goods == null)
            return 0;
        return goods.getN();
    }

    @Override
    public boolean validate(GameRole role, DropData data) {
        return getValue(role, data) >= data.getN();
    }

    @Override
    public boolean consume(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        if (data.getN() < 1)
            return true;
        com.rd.bean.goods.Goods goods = role.getPackManager().getWingGod(data.getG());
        if (goods != null && goods.getN() >= data.getN()) {
            goods.subNum(data.getN());
            if (goods.getN() <= 0) {
                role.getPackManager().removeWingGod(goods.getD());
            }
            sendUseWingGodMsg(role, goods);
            //记录玩家宝箱变化日志
            LogUtil.log(role.getPlayer(), new Goods(EGoodsType.WING_GODS.getId(), data.getG(), -data.getN(), changeType.getId()));
            return saveData(enumSet);
        }
        return false;
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        if (data.getN() < 1)
            return false;
        com.rd.bean.goods.Goods goods = role.getPackManager().getWingGod(data.getG());
        if (goods == null) {
            goods = new com.rd.bean.goods.Goods();
            goods.setD(data.getG());
            goods.setN(0);
            role.getPlayer().getWingGods().put(data.getG(), goods);
        }
        goods.addNum(data.getN());
        sendAddWingGodMsg(role, goods, changeType);
        //记录玩家宝箱变化日志
        LogUtil.log(role.getPlayer(), new Goods(EGoodsType.WING_GODS.getId(), data.getG(), data.getN(), changeType.getId()));
        return saveData(enumSet);
    }

    public void sendAddWingGodMsg(GameRole role, com.rd.bean.goods.Goods goods, EGoodsChangeType changeType) {
        Message msg = new Message(MessageCommand.GOODS_NEW_MESSAGE);
        msg.setByte(EGoodsType.WING_GODS.getId());
        goods.getMessage(msg);
        msg.setShort(changeType.getId());
        role.putMessageQueue(msg);
    }

    public void sendUseWingGodMsg(GameRole role, com.rd.bean.goods.Goods goods) {
        Message msg = new Message(MessageCommand.GOODS_USE_MESSAGE);
        msg.setByte(EGoodsType.WING_GODS.getId());
        goods.getMessage(msg);
        role.putMessageQueue(msg);
    }

    public boolean saveData(EnumSet<EPlayerSaveType> enumSet) {
        enumSet.add(EPlayerSaveType.WINGGODS);
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