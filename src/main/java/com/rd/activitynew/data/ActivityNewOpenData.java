package com.rd.activitynew.data;

public class ActivityNewOpenData {

    private byte id;

    private long startTime;

    private long endTime;

    public ActivityNewOpenData(byte id, long startTime, long endTime) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public byte getId() {
        return id;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }
}
