package com.lg.bean.game;

import com.lg.bean.PlayerLog;

/**
 * Created by XingYun on 2016/8/26.
 */
public class CreatePlayer extends PlayerLog {
    private String createTime;

    public CreatePlayer() {
    }

    public CreatePlayer(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
