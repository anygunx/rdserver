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

@WebFilter(filter = "GMGetPlayerInfoName")
public class GMGetPlayerInfoNameAction extends WebAction {
    static Logger log = Logger.getLogger(GMGetPlayerInfoNameAction.class.getName());

    @Override
    public void doAction(Map<String, String> params, Channel channel) {
        String name = params.get("name");
        try {
            if (name != null)
                name = URLDecoder.decode(name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int serverId = Integer.valueOf(params.get("serverId"));
        Player player = GMDao.getInstance().gmGetPlayerInfoByNameAccurate(serverId, name);
        if (player == null) {
            HttpUtil.sendResponse(channel, "fail");
            return;
        }
        String json = StringUtil.toJson(player);
        HttpUtil.sendResponse(channel, json);
    }

}
