package com.lg.bean.game;

import com.lg.bean.PlayerLog;

/**
 * ��¼log
 * Created by XingYun on 2016/6/15.
 */
public class Login extends PlayerLog {

    private int fighting;

    public Login() {

    }

    public Login(int fighting) {
        this.fighting = fighting;
    }

    public int getFighting() {
        return fighting;
    }

    public void setFighting(int fighting) {
        this.fighting = fighting;
    }
}
