package com.rd.common.goods;

import com.rd.bean.drop.DropData;
import com.rd.bean.goods.Box;
import com.rd.bean.goods.TimeGoods;
import com.rd.bean.goods.data.BoxData;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRole;
import com.lg.bean.game.Goods;
import com.rd.model.GoodsModel;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import com.rd.util.LogUtil;

import java.util.EnumSet;

/**
 * 宝物
 *
 * @author Created by U-Demon on 2016年11月1日 下午1:33:17
 * @version 1.0.0
 */
public class BoxCmd implements IGoodsCmd {

    private static final BoxCmd _instance = new BoxCmd();

    public static BoxCmd gi() {
        return _instance;
    }

    private BoxCmd() {
    }

    @Override
    public long getValue(GameRole role, DropData data) {
        com.rd.bean.goods.Goods goods = role.getPackManager().getBoxById(data.getG());
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
        Box goods = role.getPackManager().getBoxById(data.getG());
        if (goods != null && goods.getN() >= data.getN()) {
            goods.subNum(data.getN());
            if (goods.getN() <= 0) {
                role.getPackManager().removeBoxById(goods.getD());
            }
            sendUseBoxMsg(role, goods);
            //记录玩家宝箱变化日志
            LogUtil.log(role.getPlayer(), new Goods(EGoodsType.BOX.getId(), data.getG(), -data.getN(), changeType.getId()));
            return saveData(enumSet);
        }
        return false;
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        if (data.getN() < 1)
            return false;
        Box goods = role.getPackManager().getBoxById(data.getG());
        if (goods == null) {
            goods = new Box();
            goods.setD(data.getG());
            goods.setN(0);
            role.getPlayer().getBoxList().add(goods);
        }
        goods.addNum(data.getN());
        sendAddBoxMsg(role, goods, changeType);
        //加入限时物品逻辑
        BoxData boxData = GoodsModel.getBoxDataById(data.getG());
        if (boxData != null && boxData.getLastTime() > 0) {
            long current = System.currentTimeMillis();
            role.getPackManager().updateTimeGoods(current);
            TimeGoods timeGoods = new TimeGoods();
            timeGoods.setType(EGoodsType.BOX.getId());
            timeGoods.setId(data.getG());
            timeGoods.setTime(current + boxData.getLastTime() * 1000);
            role.getPlayer().addTimeGoods(timeGoods);
            role.sendLimitGoods(timeGoods);
            enumSet.add(EPlayerSaveType.SMALLDATA);
        }
        //记录玩家宝箱变化日志
        LogUtil.log(role.getPlayer(), new Goods(EGoodsType.BOX.getId(), data.getG(), data.getN(), changeType.getId()));
        return saveData(enumSet);
    }

    public void sendAddBoxMsg(GameRole role, com.rd.bean.goods.Goods goods, EGoodsChangeType changeType) {
        Message msg = new Message(MessageCommand.GOODS_NEW_MESSAGE);
        msg.setByte(EGoodsType.BOX.getId());
        goods.getMessage(msg);
        msg.setShort(changeType.getId());
        role.putMessageQueue(msg);
    }

    public void sendUseBoxMsg(GameRole role, com.rd.bean.goods.Goods goods) {
        Message msg = new Message(MessageCommand.GOODS_USE_MESSAGE);
        msg.setByte(EGoodsType.BOX.getId());
        goods.getMessage(msg);
        role.putMessageQueue(msg);
    }

    public boolean saveData(EnumSet<EPlayerSaveType> enumSet) {
        enumSet.add(EPlayerSaveType.BOX);
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
