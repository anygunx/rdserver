package com.rd.common.goods;

import com.rd.bean.drop.DropData;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.define.EGoodsQuality;
import com.rd.game.GameRole;
import com.lg.bean.game.Goods;
import com.rd.util.LogUtil;

import java.util.EnumSet;

/**
 * 装备
 *
 * @author Created by U-Demon on 2016年11月1日 下午1:33:17
 * @version 1.0.0
 */
public class EquipCmd implements IGoodsCmd {

    private static final EquipCmd _instance = new EquipCmd();

    public static EquipCmd gi() {
        return _instance;
    }

    private EquipCmd() {
    }

    /**
     * data.G为装备唯一ID
     */
    @Override
    public long getValue(GameRole role, DropData data) {
        Integer num = role.getPlayer().getRoleEquipMap().get(data.getG());
        if (num == null)
            return 0;
        return 1;
    }

    @Override
    public boolean validate(GameRole role, DropData data) {
        return getValue(role, data) >= data.getN();
    }

    @Override
    public boolean consume(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        int num = role.getPackManager().costEquip(data.getG());

        role.getPackManager().sendUseEquipMsg(data.getG());
        //记录玩家装备变化日志
        if (data.getQ() > EGoodsQuality.PURPLE.getValue()) {
            LogUtil.log(role.getPlayer(), new Goods(EGoodsType.EQUIP.getId(), data.getG(), -1, changeType.getId()));
        }
        return saveData(enumSet);
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        return reward(role, data, changeType, enumSet, true);
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet,
                          boolean isNotifyClient) {
        if (role.getPlayer().isEquipBagFull() && data.getQ() < EGoodsQuality.ORANGE.getValue())
            return false;
//		Equip equip;
//		if(data.getQ()==EGoodsQuality.RED.getValue()){
//			RedModelData model = OrangeModel.getRed(data.getG());
//			equip=EquipCommon.createRedEquip(model);
//			//TODO 发送红装通知
//		}else if(data.getQ()==EGoodsQuality.ORANGE.getValue()){
//			equip=EquipCommon.generateEquip(data);
//		}else{
//			equip=EquipCommon.generateEquip(data);
//		}
//			
//		equip.setD(role.getPackManager().getEquipNewId());
        role.getPackManager().addEquip(data.getG(), changeType, isNotifyClient);
        //记录玩家装备变化日志
        if (data.getQ() > EGoodsQuality.PURPLE.getValue()) {
            LogUtil.log(role.getPlayer(), new Goods(EGoodsType.EQUIP.getId(), data.getG(), 1, changeType.getId()));
        }
        return saveData(enumSet);
    }

    @Override
    public boolean saveData(EnumSet<EPlayerSaveType> enumSet) {
        enumSet.add(EPlayerSaveType.EQUIP);
        return true;
    }

    @Override
    public boolean consume(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet,
                           boolean isNotifyClient) {
        // TODO Auto-generated method stub
        return false;
    }
}
