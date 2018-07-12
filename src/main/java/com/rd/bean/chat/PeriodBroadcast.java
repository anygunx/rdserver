package com.rd.bean.chat;

import com.rd.common.ChatService;
import com.rd.define.EBroadcast;
import com.rd.net.message.Message;

/**
 * 周期广播
 *
 * @author Created by U-Demon on 2016年11月8日 下午8:08:30
 * @version 1.0.0
 */
public class PeriodBroadcast extends BaseBroadcast {

    private final long startTime;
    private final long endTime;
    private final long interval;
    private long lastUpdateTime = -1;

    protected PeriodBroadcast(Message msg, long startTime, long endTime, long interval) {
        super(msg);
        this.startTime = startTime;
        this.endTime = endTime;
        this.interval = interval;
    }

    @Override
    public boolean isNeedBroadcast(long currTime) {
        if (currTime < startTime || currTime > endTime)
            return false;
        if (lastUpdateTime != -1 && currTime - lastUpdateTime < interval)
            return false;
        return true;
    }

    @Override
    public void countDown(long currTime) {
        lastUpdateTime = currTime;
    }

    @Override
    public boolean isDone(long currTime) {
        if (currTime > endTime)
            return true;
        return false;
    }

    public static final PeriodBroadcast build(String content, long startTime, long endTime, long interval) {
        Message message = ChatService.createBroadcastMsg(null, EBroadcast.SYSTEM, content);
        return new PeriodBroadcast(message, startTime, endTime, interval);
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getInterval() {
        return interval;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

}
