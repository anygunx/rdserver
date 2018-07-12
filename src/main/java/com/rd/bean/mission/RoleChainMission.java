package com.rd.bean.mission;

import com.rd.define.MissionDefine;
import com.rd.net.message.Message;

/**
 * Created by XingYun on 2016/9/5.
 */
public class RoleChainMission {
    /**
     * 任务id
     */
    private short id;
    /**
     * 进度
     */
    private short c = 0;
    /**
     * 任务状态
     */
    private byte s = MissionDefine.MISSION_STATE_UNCOMPLETED;

    public RoleChainMission() {

    }

    public void setId(short id) {
        this.id = id;
    }

    public short getId() {
        return id;
    }

    public short getC() {
        return c;
    }

    public void setC(short c) {
        this.c = c;
    }

    public byte getS() {
        return s;
    }

    public void setS(byte s) {
        this.s = s;
    }

    public void setNewMission(short id) {
        this.id = id;
        this.c = 0;
        this.s = MissionDefine.MISSION_STATE_UNCOMPLETED;
    }

    public boolean isUncompleted() {
        return s == MissionDefine.MISSION_STATE_UNCOMPLETED;
    }

    public boolean isReceived() {
        return s == MissionDefine.MISSION_STATE_RECEIVED;
    }

    public boolean isCompleted() {
        return s == MissionDefine.MISSION_STATE_COMPLETED;
    }

    public void getMessage(Message message) {
        message.setShort(getId());
        message.setShort(getC());
        message.setByte(getS());
    }
}
