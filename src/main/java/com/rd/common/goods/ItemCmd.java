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

/**
 * 物品
 *
 * @author Created by U-Demon on 2016年11月1日 下午1:33:17
 * @version 1.0.0
 */
public class ItemCmd implements IGoodsCmd {

    private static final ItemCmd _instance = new ItemCmd();

    public static ItemCmd gi() {
        return _instance;
    }

    private ItemCmd() {
    }

    @Override
    public long getValue(GameRole role, DropData data) {
        com.rd.bean.goods.Goods goods = role.getPackManager().getItemById(data.getG());
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
            return false;
        com.rd.bean.goods.Goods goods = role.getPackManager().getItemById(data.getG());
        if (goods != null && goods.getN() >= data.getN()) {
            goods.subNum(data.getN());
            if (goods.getN() <= 0) {
                role.getPackManager().removeItemById(data.getG());
            }
            sendUseItemMsg(role, goods);
            //记录玩家道具变化日志
            LogUtil.log(role.getPlayer(), new Goods(EGoodsType.ITEM.getId(), data.getG(), -data.getN(), changeType.getId()));
            return saveData(enumSet);
        }
        return false;
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        if (data.getN() < 1)
            return false;
        com.rd.bean.goods.Goods goods = role.getPackManager().getItemById(data.getG());
        if (goods == null) {
            goods = new com.rd.bean.goods.Goods();
            goods.setD(data.getG());
            goods.setN(0);
            role.getPlayer().getItemList().add(goods);
        }
        goods.addNum(data.getN());
        sendAddItemMsg(role, goods, changeType);
        //加入限时物品逻辑
//		ItemData itemData=GoodsModel.getItemDataById(data.getG());
//		if(itemData!=null && itemData.getLastTime()>0){
//			TimeGoods timeGoods=new TimeGoods();
//			timeGoods.setType(EGoodsType.ITEM.getId());
//			timeGoods.setId(data.getG());
//			timeGoods.setTime(System.currentTimeMillis()+itemData.getLastTime());
//			role.getPlayer().addTimeGoods(timeGoods);
//			role.sendLimitGoods(timeGoods);
//			enumSet.add(EPlayerSaveType.SMALLDATA);
//		}
        //记录玩家道具变化日志
        LogUtil.log(role.getPlayer(), new Goods(EGoodsType.ITEM.getId(), data.getG(), data.getN(), changeType.getId()));
        return saveData(enumSet);
    }

    public void sendAddItemMsg(GameRole role, com.rd.bean.goods.Goods goods, EGoodsChangeType changeType) {
        Message msg = new Message(MessageCommand.GOODS_NEW_MESSAGE);
        msg.setByte(EGoodsType.ITEM.getId());
        goods.getMessage(msg);
        msg.setShort(changeType.getId());
        role.putMessageQueue(msg);
    }

    public void sendUseItemMsg(GameRole role, com.rd.bean.goods.Goods goods) {
        Message msg = new Message(MessageCommand.GOODS_USE_MESSAGE);
        msg.setByte(EGoodsType.ITEM.getId());
        goods.getMessage(msg);
        role.putMessageQueue(msg);
    }

    public boolean saveData(EnumSet<EPlayerSaveType> enumSet) {
        enumSet.add(EPlayerSaveType.ITEM);
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
