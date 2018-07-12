package com.rd.activitynew.data;

/**
 * 活动数据
 *
 * @author ---
 * @version 1.0
 * @date 2018年3月2日下午2:51:57
 */
public class ActivityNewData {

    private byte id;

    private byte configId;

    private long endTime;

    public ActivityNewData(byte id, byte configId, long endTime) {
        this.id = id;
        this.configId = configId;
        this.endTime = endTime;
    }

    public byte getId() {
        return id;
    }

    public byte getConfigId() {
        return configId;
    }

    public long getEndTime() {
        return endTime;
    }
}
