package com.rd.common.goods;

import com.rd.bean.drop.DropData;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.enumeration.EEquip;
import com.rd.game.GameRole;
import com.lg.bean.game.Goods;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import com.rd.util.LogUtil;

import java.util.EnumSet;

/**
 * @author ---
 * @version 1.0
 * @date 2018年5月17日下午5:28:01
 */
public class GrowEquipPetPsychicCmd implements IGoodsCmd {

    private static final GrowEquipPetPsychicCmd _instance = new GrowEquipPetPsychicCmd();

    public static GrowEquipPetPsychicCmd gi() {
        return _instance;
    }

    private GrowEquipPetPsychicCmd() {
    }

    @Override
    public long getValue(GameRole role, DropData data) {
        return role.getPlayer().getGrowEquipList().get(EEquip.PET_PSYCHIC.ordinal()).get(data.getG());
    }

    @Override
    public boolean validate(GameRole role, DropData data) {
        return getValue(role, data) >= data.getN();
    }

    @Override
    public boolean consume(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        if (data.getN() < 1)
            return false;
        Integer goods = role.getPlayer().getGrowEquipList().get(EEquip.PET_PSYCHIC.ordinal()).get(data.getG());
        if (goods != null && goods >= data.getN()) {
            goods -= data.getN();
            if (goods <= 0) {
                role.getPlayer().getGrowEquipList().get(EEquip.PET_PSYCHIC.ordinal()).remove(data.getG());
            }
            sendUseItemMsg(role, data.getG(), goods);
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
        Integer goods = role.getPlayer().getGrowEquipList().get(EEquip.PET_PSYCHIC.ordinal()).get(data.getG());
        if (goods == null) {
            goods = 0;
        }
        goods += data.getN();
        role.getPlayer().getGrowEquipList().get(EEquip.PET_PSYCHIC.ordinal()).put(data.getG(), goods);

        sendAddItemMsg(role, data.getG(), goods, changeType);
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

    public void sendAddItemMsg(GameRole role, short d, int n, EGoodsChangeType changeType) {
        Message msg = new Message(MessageCommand.GOODS_NEW_MESSAGE);
        //msg.setByte(EGoodsType.GROW_EQUIP_PET_PSYCHIC.getId());
        msg.setShort(d);
        msg.setInt(n);
        msg.setShort(changeType.getId());
        role.putMessageQueue(msg);
    }

    public void sendUseItemMsg(GameRole role, short d, int n) {
        Message msg = new Message(MessageCommand.GOODS_USE_MESSAGE);
        //msg.setByte(EGoodsType.GROW_EQUIP_PET_PSYCHIC.getId());
        msg.setShort(d);
        msg.setInt(n);
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
