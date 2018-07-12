package com.lg.bean.logon;

import com.lg.bean.UserLog;

/**
 * Created by XingYun on 2016/8/26.
 */
public class LogCreateUser extends UserLog {
    private String createTime;

    public LogCreateUser() {
    }

    public LogCreateUser(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

}
