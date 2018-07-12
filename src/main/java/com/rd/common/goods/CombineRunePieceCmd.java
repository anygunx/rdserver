package com.rd.common.goods;

import com.rd.bean.drop.DropData;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRole;
import com.lg.bean.game.MoneyChange;
import com.rd.util.LogUtil;

import java.util.EnumSet;

/**
 * 合击符文碎片
 */
public class CombineRunePieceCmd implements IGoodsCmd {
    private static final CombineRunePieceCmd _instance = new CombineRunePieceCmd();

    public static CombineRunePieceCmd gi() {
        return _instance;
    }

    private CombineRunePieceCmd() {
    }

    @Override
    public long getValue(GameRole role, DropData data) {
        return role.getPlayer().getCombineRunePiece();
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
        if (data.getN() <= 0)
            return false;
        if (!validate(role, data))
            return false;
        int value = -data.getN();

        role.getPlayer().addCombineRunePiece(value);
        role.sendUpdateCurrencyMsg(EGoodsType.COMBINE_RUNE_PIECE, changeType);
        //记录玩家合击符文碎片变化日志
        LogUtil.log(role.getPlayer(), new MoneyChange(EGoodsType.COMBINE_RUNE_PIECE.getId(), value, changeType.getId()));
        return saveData(enumSet);
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        if (data.getN() <= 0)
            return false;
        int value = data.getN();
        role.getPlayer().addCombineRunePiece(value);
        role.sendUpdateCurrencyMsg(EGoodsType.COMBINE_RUNE_PIECE, changeType);
        //记录玩家积分变化日志
        LogUtil.log(role.getPlayer(), new MoneyChange(EGoodsType.COMBINE_RUNE_PIECE.getId(), value, changeType.getId()));
        return saveData(enumSet);
    }

    @Override
    public boolean saveData(EnumSet<EPlayerSaveType> enumSet) {
        enumSet.add(EPlayerSaveType.COMBINE_RUNE_PIECE);
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
