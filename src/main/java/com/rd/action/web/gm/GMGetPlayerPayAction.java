package com.rd.action.web.gm;

import com.rd.bean.player.Player;
import com.rd.dao.GMDao;
import com.rd.dao.PayDao;
import com.rd.net.web.WebAction;
import com.rd.net.web.WebFilter;
import com.rd.util.HttpUtil;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

import java.util.Map;

@WebFilter(filter = "GMGetPlayerPay")
public class GMGetPlayerPayAction extends WebAction {
    static Logger log = Logger.getLogger(GMGetPlayerPayAction.class.getName());

    @Override
    public void doAction(Map<String, String> params, Channel channel) {
        String account = params.get("account");
        int serverId = Integer.valueOf(params.get("serverId"));
        String startTime = params.get("startTime");
        String endTime = params.get("endTime");

        Player player = GMDao.getInstance().gmGetPlayerInfo(serverId, account);
        if (player == null) {
            HttpUtil.sendResponse(channel, "fail");
            return;
        }

        int num = new PayDao().getDayPay(player, startTime, endTime);
        HttpUtil.sendResponse(channel, num + "");
    }

}
