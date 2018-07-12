package com.rd.bean.auction;

import com.google.gson.annotations.SerializedName;
import com.rd.net.message.Message;
import com.rd.util.StringUtil;

/**
 * 竞买人
 * Created by XingYun on 2017/10/26.
 */
public class Bidder {
    @SerializedName("i")
    private int playerId;
    @SerializedName("n")
    private String name;
    /**
     * 出价
     **/
    @SerializedName("b")
    private int bid;

    public Bidder() {
    }

    public Bidder(int playerId, String name, int bid) {
        this.playerId = playerId;
        this.name = name;
        this.bid = bid;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }

    public void getMessage(Message message) {
        message.setInt(playerId);
        message.setString(name);
        message.setInt(bid);
    }

    @Override
    public String toString() {
        return StringUtil.obj2Gson(this);
    }
}
