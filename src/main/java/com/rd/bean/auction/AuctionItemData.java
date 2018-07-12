package com.rd.bean.auction;

import com.google.common.base.Preconditions;
import com.google.gson.annotations.SerializedName;
import com.rd.bean.comm.BanConstructor;
import com.rd.bean.comm.BanMethod;
import com.rd.define.EAuctionItemType;
import com.rd.net.message.Message;
import com.rd.util.StringUtil;

import java.util.Set;

/**
 * 拍品数据
 * Created by XingYun on 2017/10/24.
 */
public class AuctionItemData {
    /**
     * 拍品UID
     **/
    @SerializedName("i")
    private long id;
    /**
     * 全服拍品ID
     **/
    @SerializedName("t")
    private EAuctionItemType type;
    /**
     * 拍品模板ID
     **/
    @SerializedName("m")
    private short modelId;
    /**
     * 归属人
     **/
    @SerializedName("o")
    private Set<Integer> owners;
    /**
     * 保护时间
     **/
    @SerializedName("pt")
    private long protectTime;
    /**
     * 过期时间
     **/
    @SerializedName("dt")
    private long deadline;
    /**
     * 竞价人信息
     **/
    @SerializedName("b")
    private volatile Bidder bidder;

    @BanConstructor
    public AuctionItemData() {
    }

    /**
     * 拍品
     *
     * @param type
     * @param id
     * @param modelId      拍品模板id
     * @param owners       拥有者
     * @param protectTime  保护时间
     * @param currentPrice 当前价格
     */
    public AuctionItemData(EAuctionItemType type, long id, short modelId, Set<Integer> owners, long protectTime, int currentPrice) {
        this.type = type;
        this.id = id;
        this.modelId = modelId;
        this.owners = owners;
        this.protectTime = protectTime;
        this.bidder = new Bidder(-1, "", currentPrice);
    }

    public EAuctionItemType getType() {
        return type;
    }

    @BanMethod
    public void setType(EAuctionItemType type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    @BanMethod
    public void setId(long id) {
        this.id = id;
    }

    public short getModelId() {
        return modelId;
    }

    @BanMethod
    public void setModelId(short modelId) {
        this.modelId = modelId;
    }

    public Set<Integer> getOwners() {
        return owners;
    }

    @BanMethod
    public void setOwners(Set<Integer> owners) {
        this.owners = owners;
    }

    public long getDeadline() {
        return deadline;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    public Bidder getBidder() {
        return bidder;
    }

    /**
     * 设置最后一次竞拍的数据
     * 须要外部同步
     *
     * @param bidder
     */
    public void setBidder(Bidder bidder) {
        this.bidder = bidder;
    }

    public boolean containOwner(int playerId) {
        return owners.contains(playerId);
    }

    /**
     * 获取税后收益
     *
     * @param tax
     * @return
     */
    public int getReward(float tax) {
        int totalReward = (int) (bidder.getBid() * (1 - tax));
        // 平均分
        int finalReward = totalReward / owners.size();
        Preconditions.checkArgument(finalReward >= 0, "拍卖获得奖励错误:" + finalReward);
        return finalReward;
    }

    public long getProtectTime() {
        return protectTime;
    }

    public void getMessage(int playerId, Message message) {
        message.setString(String.valueOf(id));
        message.setShort(modelId);
        long restProtectTime = protectTime - System.currentTimeMillis();
        message.setInt(restProtectTime < 0 ? 0 : (int) restProtectTime);
        message.setInt((int) (deadline - System.currentTimeMillis())); //restTime
        bidder.getMessage(message);
        message.setBool(isOwner(playerId));
    }

    public boolean isOwner(int playerId) {
        return owners.contains(playerId);
    }

    public boolean isDead(long currentTime) {
        return deadline < currentTime;
    }

    @Override
    public String toString() {
        return StringUtil.obj2Gson(this);
    }
}
