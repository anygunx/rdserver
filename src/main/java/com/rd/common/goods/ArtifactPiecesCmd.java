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
 * 关卡神器碎片
 * Created by XingYun on 2017/11/28.
 */
public class ArtifactPiecesCmd implements IGoodsCmd {
    private static final ArtifactPiecesCmd _instance = new ArtifactPiecesCmd();

    public static ArtifactPiecesCmd gi() {
        return _instance;
    }

    private ArtifactPiecesCmd() {
    }

    @Override
    public long getValue(GameRole role, DropData data) {
        com.rd.bean.goods.Goods goods = role.getPackManager().getArtifactPieces(data.getG());
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
        return consume(role, data, changeType, enumSet, true);
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        return reward(role, data, changeType, enumSet, true);
    }

    public void sendAddArtifactPiecesMsg(GameRole role, com.rd.bean.goods.Goods goods, EGoodsChangeType changeType) {
        Message msg = new Message(MessageCommand.GOODS_NEW_MESSAGE);
        msg.setByte(EGoodsType.ARTIFACT_PIECES.getId());
        goods.getMessage(msg);
        msg.setShort(changeType.getId());
        role.putMessageQueue(msg);
    }

    public void sendUseArtifactPiecesMsg(GameRole role, com.rd.bean.goods.Goods goods) {
        Message msg = new Message(MessageCommand.GOODS_USE_MESSAGE);
        msg.setByte(EGoodsType.ARTIFACT_PIECES.getId());
        goods.getMessage(msg);
        role.putMessageQueue(msg);
    }

    public boolean saveData(EnumSet<EPlayerSaveType> enumSet) {
        enumSet.add(EPlayerSaveType.ARTIFACT_PIECES);
        return true;
    }

    @Override
    public boolean consume(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet,
                           boolean isNotifyClient) {
        if (data.getN() < 1)
            return false;
        com.rd.bean.goods.Goods goods = role.getPackManager().getArtifactPieces(data.getG());
        if (goods != null && goods.getN() >= data.getN()) {
            goods.subNum(data.getN());
            if (goods.getN() <= 0) {
                role.getPackManager().removeArtifactPiecesById(data.getG());
            }
            if (isNotifyClient) {
                sendUseArtifactPiecesMsg(role, goods);
            }
            //记录玩家道具变化日志
            LogUtil.log(role.getPlayer(), new Goods(EGoodsType.ARTIFACT_PIECES.getId(), data.getG(), -data.getN(), changeType.getId()));
            return saveData(enumSet);
        }
        return false;
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet,
                          boolean isNotifyClient) {
        if (data.getN() < 1)
            return false;
        com.rd.bean.goods.Goods goods = role.getPackManager().getArtifactPieces(data.getG());
        if (goods == null) {
            goods = new com.rd.bean.goods.Goods();
            goods.setD(data.getG());
            goods.setN(0);
            role.getPlayer().addArtifactPieces(goods);
        }
        goods.addNum(data.getN());
        if (isNotifyClient) {
            sendAddArtifactPiecesMsg(role, goods, changeType);
        }
        //记录玩家道具变化日志
        LogUtil.log(role.getPlayer(), new Goods(EGoodsType.ARTIFACT_PIECES.getId(), data.getG(), data.getN(), changeType.getId()));
        return saveData(enumSet);
    }
}
