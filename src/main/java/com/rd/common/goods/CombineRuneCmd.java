package com.rd.common.goods;

import com.rd.bean.drop.DropData;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRole;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.GameEvent;
import com.lg.bean.game.Goods;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import com.rd.util.LogUtil;

import java.util.EnumSet;

/**
 * 合击符文
 */
public class CombineRuneCmd implements IGoodsCmd {
    private static final CombineRuneCmd _instance = new CombineRuneCmd();

    public static CombineRuneCmd gi() {
        return _instance;
    }

    private CombineRuneCmd() {
    }

    @Override
    public long getValue(GameRole role, DropData data) {
        Integer num = role.getPlayer().getCombineRuneBag().get((byte) data.getG());
        if (num == null)
            return 0;
        return num;
    }

    @Override
    public boolean validate(GameRole role, DropData data) {
        return getValue(role, data) >= data.getN();
    }

    @Override
    public boolean consume(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        if (data.getN() < 1)
            return false;
        Integer num = role.getPlayer().getCombineRuneBag().get((byte) data.getG());
        if (num != null && num >= data.getN()) {
            num = num - data.getN();
            if (num <= 0) {
                role.getPlayer().getCombineRuneBag().remove((byte) data.getG());
            } else {
                role.getPlayer().getCombineRuneBag().put((byte) data.getG(), num);
            }
            sendUpdateMsg(role, (byte) data.getG(), num);
            //记录玩家道具变化日志
            LogUtil.log(role.getPlayer(), new Goods(EGoodsType.COMBINE_RUNE.getId(), data.getG(), -data.getN(), changeType.getId()));
            return saveData(enumSet);
        }
        return false;
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        if (data.getN() < 1)
            return false;
        Integer num = role.getPlayer().getCombineRuneBag().get((byte) data.getG());
        if (num == null) {
            num = 0;
        }
        num = num + data.getN();
        role.getPlayer().getCombineRuneBag().put((byte) data.getG(), num);

        sendUpdateMsg(role, (byte) data.getG(), num);

        role.getEventManager().notifyEvent(new GameEvent(EGameEventType.COMBINE_RUNE_NUM, role.getPlayer().getCombineRuneTotalNum(), enumSet));
        //记录玩家道具变化日志
        LogUtil.log(role.getPlayer(), new Goods(EGoodsType.COMBINE_RUNE.getId(), data.getG(), data.getN(), changeType.getId()));
        return saveData(enumSet);
    }

    public void sendUpdateMsg(GameRole role, byte id, int num) {
        Message msg = new Message(MessageCommand.COMBINE_RUNE_BAG_UPDATE_MESSAGE);
        msg.setByte(id);
        msg.setInt(num);
        role.putMessageQueue(msg);
    }

    public boolean saveData(EnumSet<EPlayerSaveType> enumSet) {
        enumSet.add(EPlayerSaveType.COMBINE_RUNE_BAG);
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
