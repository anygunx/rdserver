package com.rd.model.data.mission;

import com.google.common.collect.ImmutableList;
import com.rd.bean.drop.DropData;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.EMissionUpdateType;
import com.rd.model.data.condition.IConditionModelData;

import java.util.Collections;
import java.util.List;

public class MissionModelData {

    private final short id;
    /**
     * 任务参数 类型相关
     */
    private final short param;
    /**
     * 奖励
     */
    private final List<DropData> rewardList;
    private final short next;
    private EGameEventType eventType;
    private EMissionUpdateType updateType;
    private List<IConditionModelData> conditionList;

    public MissionModelData(short id, short param, List<DropData> rewardList, short next,
                            EGameEventType eventType, EMissionUpdateType updateType) {
        this(id, param, rewardList, next,
                eventType, updateType, Collections.emptyList());
    }

    public MissionModelData(short id, short param, List<DropData> rewardList, short next,
                            EGameEventType eventType, EMissionUpdateType updateType, List<IConditionModelData> conditionList) {
        this.id = id;
        this.param = param;
        this.rewardList = ImmutableList.copyOf(rewardList);
        this.next = next;
        this.eventType = eventType;
        this.updateType = updateType;
        this.conditionList = ImmutableList.copyOf(conditionList);
    }

    public short getId() {
        return id;
    }

    public List<DropData> getRewardList() {
        return rewardList;
    }

    public short getParam() {
        return param;
    }

    public short getNext() {
        return next;
    }

    public EGameEventType getEventType() {
        return eventType;
    }

    public EMissionUpdateType getUpdateType() {
        return updateType;
    }

    public List<IConditionModelData> getConditionList() {
        return conditionList;
    }
}
