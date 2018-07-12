package com.rd.activity.config;

import com.rd.activity.EActivityType;
import com.rd.activity.group.ActivityRoundConfig;
import com.rd.define.GameDefine;
import com.rd.util.DateUtil;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 活动时间配置数据的基类
 *
 * @author Created by U-Demon on 2016年10月31日 下午4:51:18
 * @version 1.0.0
 */
public abstract class BaseActivityConfig {

    private static Logger logger = Logger.getLogger(BaseActivityConfig.class);

    //活动起始时间类型
    protected final int type;
    //活动配置ID
    protected final EActivityType id;
    //活动名称
    protected final String name;
    //开始时间
    protected final long startTime;
    //结束时间
    protected final long endTime;
    //循环活动loop的时候，使用keepTime
    //每轮持续多久
    protected final long keepTime;
    //每轮休息多久
    protected final long restTime;
    //活动总轮次数
    protected final int roundTotal;
    //是否轮次活动
    protected final boolean loop;
    //排序
    protected byte order;
    //开放的服务器
    protected Map<Short, Set<Short>> servers;

    public BaseActivityConfig(int type, int id, String name, long startTime, long endTime,
                              boolean loop, float keepDay, float restDay, int roundTotal) {
        this.type = type;
        this.id = EActivityType.getType(id);
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.loop = loop;
        this.keepTime = (long) (keepDay * DateUtil.DAY);
        this.restTime = (long) (restDay * DateUtil.DAY);
        this.roundTotal = roundTotal;
    }

    /**
     * 活动开始时间
     *
     * @param playerId
     * @return
     */
    public abstract long getStartTime(int playerId);

    /**
     * 获取当前时间的轮次数据
     * FIXME LOOP时  活动结束时间跟ENDTIME无关  通过ROUNDTOTAL控制
     *
     * @param playerId
     * @param currTime
     * @return
     */
    public abstract ActivityRoundConfig getCurrRound(int playerId, long currTime);

    /**
     * 创建活动配置数据
     *
     * @param id
     * @param name
     * @param startTimeStr
     * @param keepDay
     * @param loop
     * @param endTimeStr
     * @return
     */
    public static BaseActivityConfig createActivityConfig(int id, String name, String startTimeStr,
                                                          String endTimeStr, boolean loop, float keepDay, float restDay, int roundTotal) {
        BaseActivityConfig baseActivityData;
        long startTime, endTime;
        if (endTimeStr.contains("-"))
            endTime = DateUtil.parseDataTime(endTimeStr).getTime();
        else
            endTime = Long.valueOf(endTimeStr);
        //开服时间在此之后的以开服时间算
        if (startTimeStr.startsWith("after_")) {
            String timeStr = startTimeStr.substring(6);
            long time = DateUtil.parseDataTime(timeStr).getTime();
            //在此之前开服的
            if (GameDefine.SERVER_CREATE_TIME < time) {
                startTimeStr = timeStr;
                if (loop)
                    endTime = 1888888888888L;
                else
                    endTime = (long) (DateUtil.getDayStartTime(time) + keepDay * DateUtil.DAY);
            }
            //在此之后开服的
            else if (GameDefine.SERVER_CREATE_TIME >= time && GameDefine.SERVER_CREATE_TIME < endTime) {
                startTimeStr = "server";
                if (loop)
                    endTime = 1888888888888L;
                else
                    endTime = (long) (DateUtil.getDayStartTime(GameDefine.SERVER_CREATE_TIME) + keepDay * DateUtil.DAY);
            }
        }
        if (startTimeStr.startsWith("after;")) {
            String timeStr = startTimeStr.substring(6);
            String[] times = timeStr.split(";");
            if (times.length == 2) {
                String[] afterServer = times[0].split("_");
                if (!afterServer[0].equals("server"))
                    return null;
                int day = Integer.valueOf(afterServer[1]);
                long afterServerTime = DateUtil.getDayStartTime(GameDefine.SERVER_CREATE_TIME);
                afterServerTime = DateUtil.getDayStartTime(afterServerTime + day * DateUtil.DAY);
                long afterAbsoluteTime = DateUtil.parseDataTime(times[1]).getTime();
                //在此之前开服的
                if (afterServerTime < afterAbsoluteTime) {
                    startTimeStr = DateUtil.formatDateTime(afterAbsoluteTime);
                    //if (loop)
                    //	endTime = 1888888888888L;
                } else {
                    startTimeStr = DateUtil.formatDateTime(afterServerTime);
                    //if (loop)
                    //	endTime = 1888888888888L;
                }
            }

        }
        //服务器创建时间开始
        if (startTimeStr.startsWith("server")) {
            startTime = DateUtil.getDayStartTime(GameDefine.SERVER_CREATE_TIME);
            String[] days = startTimeStr.split("_");
            if (days.length > 1) {
                int day = Integer.valueOf(days[1]);
                startTime = DateUtil.getDayStartTime(startTime + day * DateUtil.DAY);
            }
            baseActivityData = new ServerActivityConfig(id, name, startTime, endTime, loop, keepDay, restDay, roundTotal);
        }
        //角色创建时间开始
        else if (startTimeStr.equals("player")) {
            baseActivityData = new PlayerActivityConfig(id, name, -1, endTime, loop, keepDay, restDay, roundTotal);
        }
        //固定时间活动
        else {
            startTime = DateUtil.parseDataTime(startTimeStr).getTime();
            baseActivityData = new AbsoluteActivityConfig(id, name, startTime, endTime, loop, keepDay, restDay, roundTotal);
        }
        logger.info("创建活动配置数据：" + name + ",startTime=" + startTimeStr + ", keepDay=" + keepDay + ", loop=" + loop);
        return baseActivityData;
    }

    /**
     * 活动是否在本服开放
     *
     * @return
     */
    public boolean containsServer(short channelId, short serverId) {
        //servers字段没有配置->对所有服开放
        if (servers == null || servers.size() == 0)
            return true;
        //没有配置本渠道信息
        if (!servers.containsKey(channelId))
            return false;
        //渠道下没有配置服务器ID->对本渠道所有服都开放
        Set<Short> set = servers.get(channelId);
        if (set.size() == 0)
            return true;
        //在本渠道下是否配置的本服
        return set.contains(serverId);
    }

    public int getType() {
        return type;
    }

    public EActivityType getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getKeepTime() {
        return keepTime;
    }

    public boolean isLoop() {
        return loop;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getRestTime() {
        return restTime;
    }

    public int getRoundTotal() {
        return roundTotal;
    }

    public byte getOrder() {
        return order;
    }

    public void setOrder(byte order) {
        this.order = order;
    }

    public void initServers(String str) {
        this.servers = new HashMap<>();
        String[] csArray = str.split(";");
        for (String csStr : csArray) {
            String[] sArray = csStr.split(",");
            if (sArray.length < 1)
                continue;
            short channelId = Short.valueOf(sArray[0]);
            if (!this.servers.containsKey(channelId))
                this.servers.put(channelId, new HashSet<>());
            Set<Short> set = this.servers.get(channelId);
            for (int i = 1; i < sArray.length; i++) {
                set.add(Short.valueOf(sArray[i]));
            }
        }
    }

}
