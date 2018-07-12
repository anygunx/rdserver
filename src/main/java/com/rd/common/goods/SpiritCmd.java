package com.rd.common.goods;

import com.rd.bean.drop.DropData;
import com.rd.bean.goods.Spirit;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRole;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;

import java.util.EnumSet;
import java.util.Map;

/**
 * 元魂
 *
 * @author Created by U-Demon on 2016年11月9日 下午2:05:56
 * @version 1.0.0
 */
public class SpiritCmd implements IGoodsCmd {

    private static final SpiritCmd _instance = new SpiritCmd();

    private SpiritCmd() {
    }

    public static SpiritCmd gi() {
        return _instance;
    }

    @Override
    public long getValue(GameRole role, DropData data) {
        Spirit sp = role.getPlayer().getSpirits().get((int) data.getG());
        if (sp == null)
            return 0;
        return 1;
    }

    @Override
    public boolean validate(GameRole role, DropData data) {
        return getValue(role, data) >= data.getN();
    }

    @Override
    public boolean consume(GameRole role, DropData data, EGoodsChangeType changeType,
                           EnumSet<EPlayerSaveType> enumSet) {
        Map<Integer, Spirit> spirits = role.getPlayer().getSpirits();
        if (!spirits.containsKey((int) data.getG()))
            return false;
        Spirit sp = spirits.remove((int) data.getG());
        Message msg = new Message(MessageCommand.SPIRIT_REWARD_MESSAGE);
        //消耗
        msg.setInt(changeType.getId());
        msg.setByte(0);
        msg.setByte(1);
        sp.getMessage(msg);
        role.putMessageQueue(msg);
        //记录玩家元魂变化日志
        //LogUtil.log(role.getPlayer(), new MoneyChange(EGoodsType.SPIRIT.getId(),sp.getG(),changeType.getId()));
        return saveData(enumSet);
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
//		if (role.getSpiritManager().getSpiritBagNum() >= role.getPlayer().getSpiritBagMax())
//			return false;
        Map<Integer, Spirit> spirits = role.getPlayer().getSpirits();
        Spirit sp = new Spirit();
        sp.setD(role.getPackManager().getSpiritNewId());
        sp.setG(data.getG());
        spirits.put(sp.getD(), sp);
        Message msg = new Message(MessageCommand.SPIRIT_REWARD_MESSAGE);
        //获取
        msg.setInt(changeType.getId());
        msg.setByte(1);
        msg.setByte(1);
        sp.getMessage(msg);
        role.putMessageQueue(msg);
        //记录玩家元魂变化日志
//		LogUtil.log(role.getPlayer(), new MoneyChange(EGoodsType.SPIRIT.getId(),data.getG(),changeType.getId()));
        return saveData(enumSet);
    }

    @Override
    public boolean saveData(EnumSet<EPlayerSaveType> enumSet) {
        enumSet.add(EPlayerSaveType.SPIRIT);
        return true;
    }

    @Override
    public boolean consume(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet,
                           boolean isNotifyClient) {
        return false;
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet,
                          boolean isNotifyClient) {
        // TODO Auto-generated method stub
        return false;
    }

}
