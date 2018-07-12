package com.rd.define;

import com.rd.util.DateUtil;

public class PvpDefine {

    public final static byte PVP_CHALLENGE_MAP_STAGE = 0;

    public final static byte CHALLENGER_COUNT = 4;

    public final static int CHALLENGER_UPDATE_INTERVAL_TIME = (int) (10 * DateUtil.MINUTE);

    public final static short RANK_COUNT = 3000;

    public final static byte RECORD_COUNT = 20;

    public final static byte SEARCH_CHALLENGE_DIAMOND = 100;

    public final static byte STREAK_WIN_MAX = 50;

    public final static byte DAY_REWARD_NUM = 60;
}
