package com.rd.bean.map;

public class MapData {

    private short id;

    private short nextId;

    private short stageCount;

    private short startStage;

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public short getNextId() {
        return nextId;
    }

    public void setNextId(short nextId) {
        this.nextId = nextId;
    }

    public short getStageCount() {
        return stageCount;
    }

    public void setStageCount(short stageCount) {
        this.stageCount = stageCount;
    }

    public short getStartStage() {
        return startStage;
    }

    public void setStartStage(short startStage) {
        this.startStage = startStage;
    }

    public boolean isMapEndStage(short stageId) {
        if (stageId >= this.stageCount) {
            return true;
        }
        return false;
    }
}
