package com.rd.bean.auction;

/**
 * 拍卖行构建器标识
 * Created by XingYun on 2017/10/25.
 */
public interface IAuctionBuilder<A extends BaseAuction> {
    A build();
}
