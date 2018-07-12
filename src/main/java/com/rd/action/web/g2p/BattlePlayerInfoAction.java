package com.rd.action.web.g2p;

import com.rd.bean.player.BattlePlayer;
import com.rd.game.pvp.PvPWorld;
import com.rd.net.web.WebAction;
import com.rd.net.web.WebFilter;
import com.rd.util.HttpUtil;
import com.rd.util.StringUtil;
import org.jboss.netty.channel.Channel;

import java.util.Map;

/**
 * PVP玩家信息
 *
 * @author Created by U-Demon on 2016年12月8日 上午11:46:56
 * @version 1.0.0
 */
@WebFilter(filter = "BattlePlayerInfo")
public class BattlePlayerInfoAction extends WebAction {

    @Override
    public void doAction(Map<String, String> params, Channel channel) {
        String bpInfo = params.get("BattlePlayer");
        BattlePlayer bp = StringUtil.gson2Obj(bpInfo, BattlePlayer.class);
        if (bp == null) {
            HttpUtil.sendResponse(channel, "fail");
            return;
        }
        bp.decodeName();
        PvPWorld.gi().addBattlePlayer(bp);
        HttpUtil.sendResponse(channel, "succ");
    }

}
