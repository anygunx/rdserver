package com.rd.bean.goods.data;

import com.google.common.collect.ImmutableList;
import com.rd.bean.comm.BaseRandomData;

import java.util.List;

/**
 * 拍卖宝箱模板数据
 * Created by XingYun on 2017/11/6.
 */
public class AuctionBoxData {
    private final short id;
    private final long lastTime;
    private final List<BaseRandomData<Short>> dataList;

    public AuctionBoxData(short id, long lastTime, List<BaseRandomData<Short>> dataList) {
        this.id = id;
        this.lastTime = lastTime;
        this.dataList = ImmutableList.copyOf(dataList);
    }

    public short getId() {
        return id;
    }

    public long getLastTime() {
        return lastTime;
    }

    public List<BaseRandomData<Short>> getDataList() {
        return dataList;
    }
}
