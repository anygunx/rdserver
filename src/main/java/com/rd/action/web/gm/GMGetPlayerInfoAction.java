package com.rd.action.web.gm;

import com.rd.bean.player.Player;
import com.rd.dao.GMDao;
import com.rd.net.web.WebAction;
import com.rd.net.web.WebFilter;
import com.rd.util.HttpUtil;
import com.rd.util.StringUtil;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

@WebFilter(filter = "GMGetPlayerInfo")
public class GMGetPlayerInfoAction extends WebAction {
    static Logger log = Logger.getLogger(GMGetPlayerInfoAction.class.getName());

    @Override
    public void doAction(Map<String, String> params, Channel channel) {
        String account = params.get("account");
        String name = params.get("name");
        try {
            if (name != null)
                name = URLDecoder.decode(name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int serverId = Integer.valueOf(params.get("serverId"));
        Player player = null;
        if (account != null && account.length() > 0) {
            player = GMDao.getInstance().gmGetPlayerInfo(serverId, account);
        } else if (name != null && name.length() > 0) {
            player = GMDao.getInstance().gmGetPlayerInfoByName(serverId, name);
        }
        if (player == null) {
            HttpUtil.sendResponse(channel, "fail");
            return;
        }
        String json = StringUtil.toJson(player);
        System.out.println(json);
        HttpUtil.sendResponse(channel, json);

        log.info("GMGetPlayerInfoAction account=" + account);
    }

}
