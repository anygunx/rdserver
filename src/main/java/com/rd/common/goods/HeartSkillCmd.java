package com.rd.common.goods;

import com.rd.bean.drop.DropData;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRole;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;

import java.util.EnumSet;

public class HeartSkillCmd implements IGoodsCmd {

    private static final HeartSkillCmd _instance = new HeartSkillCmd();

    public static HeartSkillCmd gi() {
        return _instance;
    }

    private HeartSkillCmd() {
    }

    @Override
    public long getValue(GameRole role, DropData data) {
        return role.getPlayer().getHeartSkillMap().get((byte) data.getG());
    }

    @Override
    public boolean validate(GameRole role, DropData data) {
        long value = getValue(role, data);
        if (value < data.getN())
            return false;
        return true;
    }

    @Override
    public boolean consume(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        if (data.getN() <= 0)
            return false;
        if (!validate(role, data))
            return false;
        role.subHeartSkill((byte) data.getG(), (short) data.getN());

        Message msg = new Message(MessageCommand.GOODS_USE_MESSAGE);
        msg.setByte(EGoodsType.HEART_SKILL.getId());
        msg.setShort(data.getG());
        msg.setInt(role.getPlayer().getHeartSkillMap().get((byte) data.getG()));
        role.putMessageQueue(msg);

        return saveData(enumSet);
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        return reward(role, data, changeType, enumSet, true);
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet,
                          boolean isNotifyClient) {
        if (data.getN() <= 0)
            return false;
        short id = data.getG();
        role.addHeartSkill((byte) id, data.getN());
        if (isNotifyClient) {
            Message msg = new Message(MessageCommand.GOODS_NEW_MESSAGE);
            msg.setByte(EGoodsType.HEART_SKILL.getId());
            msg.setByte(data.getG());
            msg.setShort(role.getPlayer().getHeartSkillMap().get((byte) data.getG()));
            msg.setShort(changeType.getId());
            role.putMessageQueue(msg);
        }
        return saveData(enumSet);
    }

    @Override
    public boolean saveData(EnumSet<EPlayerSaveType> enumSet) {
        enumSet.add(EPlayerSaveType.HEART_SKILL);
        return true;
    }

    @Override
    public boolean consume(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet,
                           boolean isNotifyClient) {
        // TODO Auto-generated method stub
        return false;
    }
}