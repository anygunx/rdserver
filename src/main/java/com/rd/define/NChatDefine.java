package com.rd.define;

import com.rd.util.DateUtil;

public class NChatDefine {
    //聊天缓存容量
    public static final int CHAT_CACHE_CAPACITY = 20;
    //聊天长度限制
    public static final int CHAT_CONTENT_LENGTH_LIMIT = 50;

    //系统广播容量
    public static final int BROADCAST_SYSTEM_CAPACITY = 10;
    //玩家广播容量
    public static final int BROADCAST_PLAYER_CAPACITY = 50;
    //每次向玩家推送广播的最大数量
    public static final int PUSH_BROADCAST_MAX = 6;


    //聊天类型
    public static final byte CHAT_TYPE_SYSTEM = 0;    //系统
    public static final byte CHAT_TYPE_PLAYER = 1;    //玩家
    public static final byte CHAT_TYPE_FACTION = 2;    //公会
    public static final byte CHAT_TYPE_NORMAL = 3;    //所有 client用
    public static final byte CHAT_TYPE_PRIVATE = 4;    //私聊

    public static final byte CHAT_TYPE_PLAYER_PET_SHOW = 1;    //宠物展示
    public static final byte CHAT_TYPE_PLAYER_EQUIT_SHOW = 2;    //装备展示

    public static final byte CHAT_TYPE_NORMER = 3;    //正常聊天
    //广播类型
    public static final byte BROADCAST_TYPE_SYSTEM = 0;    //系统
    public static final byte BROADCAST_TYPE_PLAYER = 1;    //玩家


    //私聊历史记录容量
    public static final int PRIVATE_HISTORY_CAPACITY = 100;
    //私聊记录保留时间
    public static final long PRIVATE_HISTORY_REMAIN_TIME = 7 * DateUtil.DAY;
    //私聊记录存储间隔
    public static final long CHAT_CACHE_SAVE_INTERVAL = 1 * DateUtil.HOUR;//DateUtil.MINUTE;//

    public static final long CHAT_TIME = 5000;
}
