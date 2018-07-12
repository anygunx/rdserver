package com.lg.bean.game;

import com.lg.bean.PlayerLog;

/**
 * �ǳ�log
 * Created by XingYun on 2016/6/15.
 */
public class Logout extends PlayerLog {
    /**
     * ����ʱ��
     **/
    private long timeOnline;

    public Logout() {
    }

    public Logout(long timeOnline) {
        this.timeOnline = timeOnline;
    }

    public long getTimeOnline() {
        return timeOnline;
    }

    public void setTimeOnline(long timeOnline) {
        this.timeOnline = timeOnline;
    }
}
