package com.rd.bean.auction;

import com.google.gson.annotations.SerializedName;
import com.rd.net.message.Message;

/**
 * 拍卖记录
 * Created by XingYun on 2017/10/24.
 */
public class AuctionLog {
    /**
     * 拍品id
     **/
    @SerializedName("i")
    private AuctionItemData item;
    @SerializedName("t")
    private long time;

    /**
     * 仅用于DB映射
     */
    @Deprecated
    public AuctionLog() {
    }

    public AuctionLog(AuctionItemData item, long time) {
        this.item = item;
        this.time = time;
    }

    public AuctionItemData getItem() {
        return item;
    }

    /**
     * 仅用于DB映射
     */
    @Deprecated
    public void setItem(AuctionItemData item) {
        this.item = item;
    }

    public long getTime() {
        return time;
    }

    /**
     * 仅用于DB映射
     */
    @Deprecated
    public void setTime(long time) {
        this.time = time;
    }

    public void getMessage(int playerId, Message message) {
        message.setString(String.valueOf(item.getId()));
        message.setShort(item.getModelId());
        message.setLong(time);
        Bidder bidder = item.getBidder();
        message.setInt(bidder.getPlayerId());
        message.setString(bidder.getName());
        message.setInt(bidder.getBid());
        message.setBool(item.isOwner(playerId));
    }
}
