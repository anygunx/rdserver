package com.rd.define;

/**
 * 关系定义
 * Created by XingYun on 2017/5/2.
 */
public class RelationshipDefine {
    /**
     * 没关系
     **/
    public static final byte RELATIONSHIP_NONE = 0;
    /**
     * 好友
     **/
    public static final byte RELATIONSHIP_FRIEND = 1;
    /**
     * 黑名单
     **/
    public static final byte RELATIONSHIP_BLACK = 2;
    /**
     * 好友申请
     **/
    public static final byte RELATIONSHIP_APPLICATION = 3;

    /**
     * 好友列表容量
     **/
    public static final byte FRIEND_MAX = 100;
    /**
     * 黑名单容量
     **/
    public static final byte BLACK_MAX = 100;
    /**
     * 好友申请列表容量
     **/
    public static final byte APPLICATION_MAX = 100;
}
