package com.rd.action.web.g2p;

import com.rd.game.local.GameHttpManager;
import com.rd.game.pvp.ArenaPvPManager;
import com.rd.game.pvp.PvPWorld;
import com.rd.net.web.WebAction;
import com.rd.net.web.WebFilter;
import com.rd.util.HttpUtil;
import com.rd.util.StringUtil;
import org.jboss.netty.channel.Channel;

import java.util.Map;

/**
 * 竞技场排名信息
 *
 * @author Created by U-Demon on 2016年12月8日 上午11:46:56
 * @version 1.0.0
 */
@WebFilter(filter = "ArenaRankInfo")
public class ArenaRankInfoAction extends WebAction {

    @Override
    public void doAction(Map<String, String> params, Channel channel) {
        int serverId = Integer.valueOf(params.get("serverId"));
        int id = Integer.valueOf(params.get("id"));
        boolean refresh = Boolean.valueOf(params.get("refresh"));
        ArenaPvPManager arenaMgr = PvPWorld.gi().getManager(serverId, ArenaPvPManager.class);
        //个人排名
        int rank = arenaMgr.getArenaRankInfo(id);
        //服务器排名
        String serverRank = arenaMgr.getServerRankInfo();
        String json = null;
        if (refresh) {
            json = StringUtil.obj2Gson(arenaMgr.getArenaChallengeList(rank));
            HttpUtil.sendResponse(channel, rank + GameHttpManager.SPLIT + serverRank + GameHttpManager.SPLIT + json);
        } else {
            HttpUtil.sendResponse(channel, rank + GameHttpManager.SPLIT + serverRank);
        }
    }

}
