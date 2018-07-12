package com.rd.action.web.g2p;

import com.rd.game.pvp.BossPvPManager;
import com.rd.game.pvp.PvPWorld;
import com.rd.net.web.WebAction;
import com.rd.net.web.WebFilter;
import com.rd.util.HttpUtil;
import org.jboss.netty.channel.Channel;

import java.util.Map;

/**
 * 重置转生BOSS
 *
 * @author U-Demon Created on 2017年5月31日 下午5:26:58
 * @version 1.0.0
 */
@WebFilter(filter = "BossReinInit")
public class BossReinInitAction extends WebAction {

    @Override
    public void doAction(Map<String, String> params, Channel channel) {
        int serverId = Integer.valueOf(params.get("serverId"));
        BossPvPManager mgr = PvPWorld.gi().getManager(serverId, BossPvPManager.class);
        if (mgr == null) {
            HttpUtil.sendResponse(channel, "fail");
            return;
        }
        mgr.initReinBoss();
        HttpUtil.sendResponse(channel, "succ");
    }

}
