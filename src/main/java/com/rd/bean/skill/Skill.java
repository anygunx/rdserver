package com.rd.bean.skill;

import com.rd.model.data.SkillModelData;

public class Skill {

    private SkillModelData data;

    private short level = 0;

    private int cd = 0;

    private short delay3 = 0;

    public SkillModelData getData() {
        return data;
    }

    public void setData(SkillModelData data) {
        this.data = data;
    }

    public short getLevel() {
        return level;
    }

    public void setLevel(short level) {
        this.level = level;
    }

    public int getCd() {
        return cd;
    }

    public void setCd(int cd) {
        this.cd = cd;
    }

    public short getDelay3() {
        return delay3;
    }

    public void setDelay3(short delay3) {
        this.delay3 = delay3;
    }

    public Skill() {

    }

    public void coolDown(short time) {
        if (cd > 0) {
            this.cd -= time;
        }
    }

    public boolean isCoolDown() {
        if (cd < 1) {
            return true;
        }
        return false;
    }

    public short Launch() {
        this.cd = this.data.cd;
        return (short) this.data.delay;
    }

    public void addDelay(short time) {
        this.delay3 += time;
    }

    public void setDelay(short time) {
        this.delay3 = time;
    }

    public boolean isDelay() {
        if (this.delay3 >= this.data.delay3) {
            return true;
        }
        return false;
    }

    public void reset() {
        this.cd = 0;
        this.delay3 = 0;
    }
}
