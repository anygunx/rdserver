package com.rd.common.goods;

import com.rd.bean.drop.DropData;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRole;

import java.util.EnumSet;

public class DomCmd implements IGoodsCmd {

    @Override
    public long getValue(GameRole role, DropData data) {
//		Byte dom = role.getPlayer().getDoms().get((int)data.getG());
//		if (dom == null)
//			return 0;
        return 1;
    }

    @Override
    public boolean validate(GameRole role, DropData data) {
        return getValue(role, data) >= data.getN();
    }

    @Override
    public boolean consume(GameRole role, DropData data, EGoodsChangeType changeType,
                           EnumSet<EPlayerSaveType> enumSet) {
//		Map<Integer, Byte> doms = role.getPlayer().getDoms();
//		if (!doms.containsKey((int)data.getG()))
//			return false;
//		Byte dom=doms.remove((int)data.getG());
//		//记录玩家主宰变化日志
//		LogUtil.log(role.getPlayer(), new MoneyChange(EGoodsType.DOM.getId(),dom,changeType.getId()));
//		return saveData(enumSet);
        return true;
    }

    @Override
    public boolean consume(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet,
                           boolean isNotifyClient) {
        return consume(role, data, changeType, enumSet);
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
//		if (role.getPlayer().getDoms().size() >= role.getPlayer().getDomBagMax())
//			return false;
//		int id = role.getPackManager().getDomNewId();
//		role.getPlayer().getDoms().put(id, (byte)data.getG());
//		//记录玩家主宰变化日志
//		LogUtil.log(role.getPlayer(), new MoneyChange(EGoodsType.DOM.getId(),data.getG(),changeType.getId()));
//		return saveData(enumSet);
        return true;
    }

    @Override
    public boolean saveData(EnumSet<EPlayerSaveType> enumSet) {
//		enumSet.add(EPlayerSaveType.DOM);
        return true;
    }

    public static DomCmd gi() {
        return _instance;
    }

    private DomCmd() {
    }

    private static final DomCmd _instance = new DomCmd();

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet,
                          boolean isNotifyClient) {
        // TODO Auto-generated method stub
        return false;
    }

}
