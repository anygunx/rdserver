package com.lg.bean.game;

import com.lg.bean.PlayerLog;

/**
 * Created by XingYun on 2016/6/24.
 */
public class LevelUp extends PlayerLog {
    private short level;

    public LevelUp() {
    }

    public LevelUp(short level) {
        this.level = level;
    }

    public short getLevel() {
        return level;
    }

    public void setLevel(short level) {
        this.level = level;
    }
}
