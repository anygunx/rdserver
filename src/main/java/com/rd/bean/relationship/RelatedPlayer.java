package com.rd.bean.relationship;

import com.rd.bean.player.Player;
import com.rd.bean.player.SimplePlayer;
import com.rd.game.GameWorld;
import com.rd.game.IGameRole;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;

/**
 * 相关角色信息
 * Created by XingYun on 2017/5/2.
 */
public class RelatedPlayer extends SimplePlayer implements IRelatedPlayer {
    private long lastLoginTime;
    private long lastLogoutTime;

    public void init(int id, String name, byte head, short rein, short level, int vip, long fighting, long lastLoginTime, long lastLogoutTime) {
        this.id = id;
        this.name = name;
        this.head = head;
        this.rein = rein;
        this.level = level;
        this.vip = vip;
        this.fighting = fighting;
        this.lastLoginTime = lastLoginTime;
        this.lastLogoutTime = lastLogoutTime;
    }

    public void init(Player other) {
        IRelatedPlayer relatedPlayer = other;
        this.init(relatedPlayer);
    }

    public void init(IRelatedPlayer other) {
        this.init(other.getId(), other.getName(), other.getHead(), other.getRein(), other.getLevel(), other.getVip(), other.getFighting(), other.getLastLoginTime(), other.getLastLogoutTime());
    }

    @Override
    public long getLastLogoutTime() {
        return lastLogoutTime;
    }

    @Override
    public void setLastLogoutTime(long lastLogoutTime) {
        this.lastLogoutTime = lastLogoutTime;
    }

    @Override
    public long getLastLoginTime() {
        return lastLoginTime;
    }

    @Override
    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public void getMessage(Message message, long currentTime) {
        super.getSimpleMessage(message);
        long lastTime = lastLoginTime < lastLogoutTime ? lastLogoutTime : lastLoginTime;
        int passTime = (int) ((currentTime - lastTime) / DateUtil.SECOND);
        IGameRole onlineRole = GameWorld.getPtr().getOnlineRole(id);
        message.setInt(onlineRole != null ? 0 : passTime);
    }
}
