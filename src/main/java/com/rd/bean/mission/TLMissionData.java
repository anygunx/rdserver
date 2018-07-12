package com.rd.bean.mission;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 限时任务
 * Created by XingYun on 2017/12/5.
 */
public class TLMissionData {
    /**
     * 当前可领取组
     **/
    @SerializedName("id")
    private byte groupId;
    /**
     * 当前组限时
     **/
    @SerializedName("dl")
    private long deadline;
    /**
     * 任务-状态
     **/
    @SerializedName("m")
    private Map<Short, RoleChainMission> missions;

    public TLMissionData() {
        this.groupId = 1;
        this.deadline = 0;
        this.missions = new HashMap<>();
    }

    public void setGroupId(byte groupId) {
        this.groupId = groupId;
    }

    public byte getGroupId() {
        return groupId;
    }

    public TLMissionData(long deadline) {
        this.deadline = deadline;
    }

    public long getDeadline() {
        return deadline;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    public Map<Short, RoleChainMission> getMissions() {
        return missions;
    }

    public void setMissions(Map<Short, RoleChainMission> missions) {
        this.missions = missions;
    }

    /**
     * 是否开启
     * 未开启状态下可完成不可领取
     *
     * @return
     */
    public boolean isOpen() {
        return deadline > 0;
    }

    /**
     * 是否过期
     * 过期状态下不可完成不可领取
     *
     * @return
     */
    public boolean isDead() {
        return deadline > 0 && System.currentTimeMillis() > deadline;
    }

    /**
     * 是否可用
     * 开启&&未过期
     *
     * @return
     */
    public boolean isValid() {
        return isOpen() && !isDead();
    }

    public void updateMission(short id, RoleChainMission mission) {
        missions.put(id, mission);
    }

    public boolean isCompleted(Set<Short> missions) {
        if (this.missions.size() < missions.size()) {
            return false;
        }
        for (Short id : missions) {
            RoleChainMission mission = this.missions.get(id);
            if (mission == null) {
                return false;
            }
            if (!mission.isReceived()) {
                return false;
            }
        }
        return true;
    }
}
