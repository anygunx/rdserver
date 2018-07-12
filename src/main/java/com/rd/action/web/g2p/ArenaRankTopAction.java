package com.rd.action.web.g2p;

import com.rd.game.pvp.ArenaPvPManager;
import com.rd.game.pvp.PvPWorld;
import com.rd.net.web.WebAction;
import com.rd.net.web.WebFilter;
import com.rd.util.HttpUtil;
import com.rd.util.StringUtil;
import org.jboss.netty.channel.Channel;

import java.util.Map;

/**
 * 竞技场排行榜
 *
 * @author Created by U-Demon on 2016年12月8日 上午11:46:56
 * @version 1.0.0
 */
@WebFilter(filter = "ArenaRankTop")
public class ArenaRankTopAction extends WebAction {

    @Override
    public void doAction(Map<String, String> params, Channel channel) {
        int serverId = Integer.valueOf(params.get("serverId"));
        ArenaPvPManager arenaMgr = PvPWorld.gi().getManager(serverId, ArenaPvPManager.class);
        //战斗
        String result = StringUtil.obj2Gson(arenaMgr.getArenaChallengeList(-5));
        HttpUtil.sendResponse(channel, result);
    }

}
