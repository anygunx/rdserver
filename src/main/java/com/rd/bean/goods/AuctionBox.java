package com.rd.bean.goods;

import com.google.gson.annotations.SerializedName;
import com.rd.bean.comm.BanConstructor;
import com.rd.bean.comm.BanMethod;
import com.rd.net.message.Message;

/**
 * 拍卖宝箱
 * Created by XingYun on 2017/11/6.
 */
public class AuctionBox extends Goods {
    private long uid;
    @SerializedName("dl")
    private long deadline;
    /**
     * 预开启的奖励记录
     **/
    @SerializedName("it")
    private short itemId = 0;

    @BanConstructor
    public AuctionBox() {
    }

    public AuctionBox(short id, long uid, long deadline) {
        super(id, 1);
        this.uid = uid;
        this.deadline = deadline;
    }

    public short getItemId() {
        return itemId;
    }

    public void setItemId(short itemId) {
        this.itemId = itemId;
    }

    public long getUid() {
        return uid;
    }

    @BanMethod
    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getDeadline() {
        return deadline;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    public void getMessage(Message message) {
        message.setString(String.valueOf(uid)); //精度问题
        message.setShort(getD());
        message.setLong(deadline);
        message.setShort(itemId);
    }

    public boolean isDead() {
        return deadline < System.currentTimeMillis();
    }
}
