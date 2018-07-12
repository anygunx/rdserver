package com.rd.common.goods;

import com.rd.bean.drop.DropData;
import com.rd.bean.goods.AuctionBox;
import com.rd.bean.goods.data.AuctionBoxData;
import com.rd.common.GameCommon;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRole;
import com.rd.model.GoodsModel;

import java.util.EnumSet;

/**
 * Created by XingYun on 2017/11/3.
 */
public class AuctionBoxCmd implements IGoodsCmd {

    private static final AuctionBoxCmd _instance = new AuctionBoxCmd();

    public static AuctionBoxCmd gi() {
        return _instance;
    }

    private AuctionBoxCmd() {
    }

    @Override
    public boolean saveData(EnumSet<EPlayerSaveType> enumSet) {
        enumSet.add(EPlayerSaveType.AUCTION_BOX);
        return true;
    }

    @Override
    public long getValue(GameRole role, DropData data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean validate(GameRole role, DropData data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean consume(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean consume(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet, boolean isNotifyClient) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        return reward(role, data, changeType, enumSet, true);
    }

    @Override
    public boolean reward(GameRole role, DropData data, EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet, boolean isNotifyClient) {
        long id = GameCommon.generateId();
        AuctionBoxData modelData = GoodsModel.getAuctionBox(data.getG());
        AuctionBox box = new AuctionBox(data.getG(), id, System.currentTimeMillis() + modelData.getLastTime());
        role.getPackManager().addAuctionBox(box, changeType);
        return saveData(enumSet);
    }
}
