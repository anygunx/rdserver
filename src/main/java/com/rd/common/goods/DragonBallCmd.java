package com.rd.common.goods;

import com.rd.bean.drop.DropData;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRole;
import com.lg.bean.game.MoneyChange;
import com.rd.util.LogUtil;
import org.apache.log4j.Logger;

import java.util.EnumSet;

/**
 * Created by XingYun on 2017/10/30.
 */
public class DragonBallCmd implements IGoodsCmd {

    private static final Logger logger = Logger.getLogger(DragonBallCmd.class);

    private static final DragonBallCmd _instance = new DragonBallCmd();

    public static DragonBallCmd gi() {
        return _instance;
    }

    private DragonBallCmd() {
    }

    @Override
    public long getValue(GameRole role, DropData data) {
        return role.getPlayer().getDragonBall().getPieces();
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
        int value = -data.getN();
        role.getPlayer().getDragonBall().addPieces(value);
        role.sendUpdateCurrencyMsg(EGoodsType.DRAGONBALL_PIECE, changeType);
        //记录玩家龙珠碎片变化日志
        LogUtil.log(role.getPlayer(), new MoneyChange(EGoodsType.DRAGONBALL_PIECE.getId(), value, changeType.getId()));
        return saveData(enumSet);
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        if (data.getN() <= 0)
            return false;
        int value = data.getN();
        role.getPlayer().getDragonBall().addPieces(value);
        role.sendUpdateCurrencyMsg(EGoodsType.DRAGONBALL_PIECE, changeType);
        //记录玩家龙珠碎片变化日志
        LogUtil.log(role.getPlayer(), new MoneyChange(EGoodsType.DRAGONBALL_PIECE.getId(), value, changeType.getId()));
        return saveData(enumSet);
    }

    @Override
    public boolean saveData(EnumSet<EPlayerSaveType> enumSet) {
        enumSet.add(EPlayerSaveType.DRAGON_BALL);
        return true;
    }

    @Override
    public boolean consume(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet,
                           boolean isNotifyClient) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet,
                          boolean isNotifyClient) {
        throw new UnsupportedOperationException();
    }

}
