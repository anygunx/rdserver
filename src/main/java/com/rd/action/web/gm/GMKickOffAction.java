package com.rd.action.web.gm;

import com.rd.bean.player.Player;
import com.rd.dao.GMDao;
import com.rd.game.GameWorld;
import com.rd.net.web.WebAction;
import com.rd.net.web.WebFilter;
import com.rd.util.HttpUtil;
import org.jboss.netty.channel.Channel;

import java.util.Map;

/**
 * 踢玩家下线
 *
 * @author Created by U-Demon on 2016年12月8日 上午11:46:56
 * @version 1.0.0
 */
@WebFilter(filter = "GMKickOffRole")
public class GMKickOffAction extends WebAction {

    @Override
    public void doAction(Map<String, String> params, Channel channel) {
        String account = params.get("account");
        int serverId = Integer.valueOf(params.get("serverId"));

        Player player = GMDao.getInstance().gmGetPlayerInfo(serverId, account);
        if (player == null) {
            HttpUtil.sendResponse(channel, "fail");
            return;
        }
        GameWorld.getPtr().kickOff(player.getId());
        HttpUtil.sendResponse(channel, "succ");
    }

}
