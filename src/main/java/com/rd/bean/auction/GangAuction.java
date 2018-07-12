package com.rd.bean.auction;

import com.rd.define.EAuction;
import com.rd.game.AuctionService;
import com.rd.model.AuctionModel;

/**
 * 帮派拍卖行
 * Created by XingYun on 2017/10/25.
 */
public class GangAuction extends BaseAuction {
    public GangAuction() {
        super(EAuction.GangAuction.id);
    }


    @Override
    protected float getTax() {
        return AuctionModel.getTaxGang();
    }

    @Override
    protected void onLoss(AuctionItemData item) {
        AuctionItemData newOne = AuctionService.createAuctionItem(item.getType(), item.getModelId(), item.getOwners());
        // 流拍到世界拍卖行
        AuctionService.addItem(EAuction.WorldAuction, newOne);
    }

}
