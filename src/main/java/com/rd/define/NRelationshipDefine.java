package com.rd.define;

public class NRelationshipDefine {


    /**
     * 没关系
     **/
    public static final byte RELATIONSHIP_NONE = 0;
    /**
     * 关注
     **/
    public static final byte RELATIONSHIP_GUANZHU = 1;
    /**
     * 粉丝
     **/
    public static final byte RELATIONSHIP_FENSI = 2;
    /**
     * 黑名单
     **/
    public static final byte RELATIONSHIP_BLACK = 3;
    /**
     * 关注其他玩家上限人数
     */
    public static final int GUANZHU_OTHER_ROLE_LIMITE_NUM = 3;

    /**
     * 被关注的玩家上限人数
     */
    public static final int GUANZHU_MY_ROLE_LIMITE_NUM = 3;
    /***
     * 推荐关注人的上限人数
     */
    public static final byte RANDOM_ROLE_MAX = 10;

    /***
     * 黑名单上限人数
     */
    public static final byte BLACK_MAX = 3;

    /**
     * 添加友情币当天上限人数
     */
    public static final byte ADD_YOUQINGBI_PLAYER_MAX = 3;
    /**
     * 关注成功
     */
    public static final byte ZHUANZHU_SUCESS = 1;

    public static final byte RECEVIE_FRIEND_COIN_LIMITE = 3;

}
