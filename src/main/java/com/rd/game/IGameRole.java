package com.rd.game;

import com.rd.bean.player.Player;

/**
 * <p>Title: 游戏角色接口</p>
 * <p>Description: 用作区分在线离线角色</p>
 * <p>Company: 北京万游畅想科技有限公司</p>
 *
 * @author ---
 * @version 1.0
 * @data 2016年11月24日 下午7:29:41
 */
public interface IGameRole {

    /**
     * 是否在线
     *
     * @return
     */
    public boolean isOnline();

    /**
     * 得到玩家数据
     *
     * @return
     */
    public Player getPlayer();

    /**
     * 得到在线玩家数据
     *
     * @return
     */
    public GameRole getGameRole();
}
