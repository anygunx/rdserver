package com.rd.activitynew.data;

import java.util.Map;

/**
 * 新活动组数据
 *
 * @author ---
 * @version 1.0
 * @date 2018年3月2日下午2:46:43
 */
public class ActivityNewGroupData {

    private byte id;

    private Map<Byte, ActivityNewData> activityMap;

    public ActivityNewGroupData(byte id, Map<Byte, ActivityNewData> activityMap) {
        this.id = id;
        this.activityMap = activityMap;
    }

    public byte getId() {
        return id;
    }

    public Map<Byte, ActivityNewData> getActivityMap() {
        return activityMap;
    }
}
