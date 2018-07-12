package com.rd.common.goods;

import com.lg.bean.game.MoneyChange;
import com.rd.bean.drop.DropData;
import com.rd.common.ChatService;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EBroadcast;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRole;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.GameEvent;
import com.rd.model.VipModel;
import com.rd.model.data.VipModelData;
import com.rd.util.LogUtil;

import java.util.EnumSet;

/**
 * VIP经验
 *
 * @author Created by U-Demon on 2016年12月21日 下午2:55:52
 * @version 1.0.0
 */
public class VipCmd implements IGoodsCmd {

    private static final VipCmd _instance = new VipCmd();

    public static VipCmd gi() {
        return _instance;
    }

    private VipCmd() {
    }

    @Override
    public long getValue(GameRole role, DropData data) {
        return role.getPlayer().getVip();
    }

    @Override
    public boolean validate(GameRole role, DropData data) {
        long value = getValue(role, data);
        if (value < data.getN())
            return false;
        return true;
    }

    @Override
    public boolean consume(GameRole role, DropData data, EGoodsChangeType changeType,
                           EnumSet<EPlayerSaveType> enumSet) {
        return false;
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        if (data.getN() <= 0)
            return false;
        byte vipLevelOld = VipModel.getVipLv(role.getPlayer().getVip());
        int value = data.getN();
        role.getPlayer().addVip(value);
        role.sendUpdateCurrencyMsg(EGoodsType.VIP, changeType);
        byte vipLevelNew = VipModel.getVipLv(role.getPlayer().getVip());
        if (vipLevelNew > vipLevelOld) {
            for (int level = vipLevelOld + 1; level <= vipLevelNew; ++level) {
                VipModelData vipData = VipModel.getModelByLv(level);
                role.sendVipGiftMsg(vipData);
            }
            if (vipLevelNew > 1) {
                ChatService.broadcastPlayerMsg(role.getPlayer(), EBroadcast.VipLevelUp, String.valueOf(vipLevelNew));
            }
            role.getEventManager().notifyEvent(new GameEvent(EGameEventType.VIP_REACH_LEVEL, vipLevelNew, enumSet));
        }
        //记录玩家vip变化日志
        LogUtil.log(role.getPlayer(), new MoneyChange(EGoodsType.VIP.getId(), value, changeType.getId()));
        return saveData(enumSet);
    }

    @Override
    public boolean saveData(EnumSet<EPlayerSaveType> enumSet) {
        enumSet.add(EPlayerSaveType.VIP);
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
