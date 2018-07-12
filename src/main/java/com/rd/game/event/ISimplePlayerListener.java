package com.rd.game.event;

import com.rd.bean.player.SimplePlayer;

/**
 * <p>Title: 简单玩家监听</p>
 * <p>Description: 简单玩家监听</p>
 * <p>Company: 北京万游畅想科技有限公司</p>
 *
 * @author ---
 * @version 1.0
 * @data 2016年12月29日 下午3:45:19
 */
public interface ISimplePlayerListener {

    /**
     * 单个simpleplayer更新处理
     *
     * @param simplePlayer
     */
    void updateSingleHandler(SimplePlayer simplePlayer);
}
