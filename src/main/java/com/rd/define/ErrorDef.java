package com.rd.define;

/**
 * @author ---
 * @version 1.0
 * @date 2018年6月11日下午3:26:49
 */
public class ErrorDef {
    /**
     * 无错误
     */
    public static final short NONE = -1;
    /**
     * 参数错误
     */
    public static final short PARAMETER = 0;

    /**
     * 已在队伍中
     */
    public static final short TEAM_EXIST = 177;
    /**
     * 队伍不存在
     */
    public static final short TEAM_NON_EXIST = 178;
    /**
     * 队伍人数已满
     */
    public static final short TEAM_MEM_FULL = 179;
}
