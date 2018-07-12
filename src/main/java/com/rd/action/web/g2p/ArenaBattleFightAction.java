package com.rd.action.web.g2p;

import com.rd.game.pvp.ArenaPvPManager;
import com.rd.game.pvp.PvPWorld;
import com.rd.net.web.WebAction;
import com.rd.net.web.WebFilter;
import com.rd.util.HttpUtil;
import org.jboss.netty.channel.Channel;

import java.util.Map;

/**
 * 竞技场战斗
 *
 * @author Created by U-Demon on 2016年12月8日 上午11:46:56
 * @version 1.0.0
 */
@WebFilter(filter = "ArenaBattleFight")
public class ArenaBattleFightAction extends WebAction {

    @Override
    public void doAction(Map<String, String> params, Channel channel) {
        int serverId = Integer.valueOf(params.get("serverId"));
        int selfId = Integer.valueOf(params.get("selfId"));
        int rank = Integer.valueOf(params.get("rank"));
        int selfRank = Integer.valueOf(params.get("selfRank"));
        ArenaPvPManager arenaMgr = PvPWorld.gi().getManager(serverId, ArenaPvPManager.class);
        //战斗
        String result = arenaMgr.arenaBattleFight(serverId, selfId, rank, selfRank);
        HttpUtil.sendResponse(channel, result);
    }

}
