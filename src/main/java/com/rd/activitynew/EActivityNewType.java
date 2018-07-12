package com.rd.activitynew;

import com.rd.activitynew.info.ActivityCumulatePayInfo;
import com.rd.activitynew.info.IActivityInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * 新活动类型
 *
 * @author ---
 * @version 1.0
 * @date 2018年3月3日下午1:58:05
 */
public enum EActivityNewType {

    /**
     * 1：累计充值
     */
    ACTIVITY_CUMULATE_PAY((byte) 1, new ActivityCumulatePayInfo());

    private static final Map<Byte, EActivityNewType> valueMap;

    static {
        valueMap = new HashMap<>();
        for (EActivityNewType type : EActivityNewType.values()) {
            valueMap.put(type.id, type);
        }
    }

    public static EActivityNewType getType(byte type) {
        return valueMap.get(type);
    }

    private byte id;

    private IActivityInfo info;

    EActivityNewType(byte id, IActivityInfo info) {
        this.id = id;
        this.info = info;
    }

    public byte getId() {
        return this.id;
    }

    public IActivityInfo getActivityInfo() {
        return this.info;
    }
}
