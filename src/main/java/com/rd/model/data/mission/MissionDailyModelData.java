package com.rd.model.data.mission;

import com.rd.game.event.EGameEventType;

public class MissionDailyModelData {

    private short id;
    /**
     * 任務次數
     **/
    private short count;
    /**
     * 任務獎勵
     **/
    private short score;

    private EGameEventType eventType;

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public short getCount() {
        return count;
    }

    public void setCount(short count) {
        this.count = count;
    }

    public short getScore() {
        return score;
    }

    public void setScore(short score) {
        this.score = score;
    }

    public EGameEventType getEventType() {
        return eventType;
    }

    public void setEventType(EGameEventType eventType) {
        this.eventType = eventType;
    }
}
