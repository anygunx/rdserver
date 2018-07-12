package com.rd.activity.group;

import com.rd.activity.EActivityType;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;

/**
 * 活动轮次数据
 *
 * @author Created by U-Demon on 2016年11月2日 下午7:13:48
 * @version 1.0.0
 */
public class ActivityRoundConfig {
    //活动Id
    private final EActivityType id;
    //活动轮次
    private final int round;
    //活动开始时间
    private final long startTime;
    //活动开始时间串
    private final String startTimeStr;
    //活动结束时间
    private final long endTime;
    //活动结束时间串
    private final String endTimeStr;

    public ActivityRoundConfig(EActivityType id, int round, long startTime, long endTime) {
        this.id = id;
        this.round = round;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startTimeStr = DateUtil.formatDateTime(startTime);
        this.endTimeStr = DateUtil.formatDateTime(endTime);
    }

    /**
     * 获取剩余时间
     *
     * @param currentTime
     * @return
     */
    public long getRestTime(long currentTime) {
        if (isEnd(currentTime)) {
            return 0;
        }
        return endTime - currentTime;
    }

    /**
     * 活动轮次是否结束
     *
     * @param currentTime
     * @return
     */
    public boolean isEnd(long currentTime) {
        if (currentTime < startTime)
            return true;
        if (currentTime > endTime)
            return true;
        return false;
    }

    public void getMessage(Message message, long currentTime) {
        message.setInt(getId().getId());
        message.setInt(getRound());
        message.setString(getStartTimeStr());
        message.setString(getEndTimeStr());
        message.setLong(getRestTime(currentTime));
    }

    public EActivityType getId() {
        return id;
    }

    public int getRound() {
        return round;
    }

    public long getStartTime() {
        return startTime;
    }

    public String getStartTimeStr() {
        return startTimeStr;
    }

    public long getEndTime() {
        return endTime;
    }

    public String getEndTimeStr() {
        return endTimeStr;
    }

}
