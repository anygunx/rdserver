package com.rd.bean.relationship;

import com.rd.bean.player.ISimplePlayer;

/**
 * 相关信息标识
 * Created by XingYun on 2017/5/2.
 */
public interface IRelatedPlayer extends ISimplePlayer {
    long getLastLoginTime();

    void setLastLoginTime(long lastLoginTime);

    long getLastLogoutTime();

    void setLastLogoutTime(long lastLogoutTime);
}
