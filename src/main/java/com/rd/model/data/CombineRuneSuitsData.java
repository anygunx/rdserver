package com.rd.model.data;

public class CombineRuneSuitsData {

    private short level;
    //合击技能对玩家伤害提高
    private float pvpindamage;
    //合击技能对怪物伤害提高
    private float pveindamage;
    //怒气回复速度提高
    private float reangery;

    public short getLevel() {
        return level;
    }

    public void setLevel(short level) {
        this.level = level;
    }

    public float getPvpindamage() {
        return pvpindamage;
    }

    public void setPvpindamage(float pvpindamage) {
        this.pvpindamage = pvpindamage;
    }

    public float getPveindamage() {
        return pveindamage;
    }

    public void setPveindamage(float pveindamage) {
        this.pveindamage = pveindamage;
    }

    public float getReangery() {
        return reangery;
    }

    public void setReangery(float reangery) {
        this.reangery = reangery;
    }
}
