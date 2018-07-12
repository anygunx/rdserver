package com.rd.action.web.gm;

import com.rd.game.GameRelationshipManager;
import com.rd.net.web.WebAction;
import com.rd.net.web.WebFilter;
import com.rd.util.HttpUtil;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

import java.util.Map;

/**
 * Created by XingYun on 2017/5/9.
 */
@WebFilter(filter = "GMClearRelationCache")
public class GMClearRelationCacheAction extends WebAction {
    private static final Logger logger = Logger.getLogger(GMClearRelationCacheAction.class);

    @Override
    public void doAction(Map<String, String> params, Channel channel) {
        try {
            int id = Integer.valueOf(params.get("id"));
            GameRelationshipManager.getInstance().removeCache(id);
            HttpUtil.sendResponse(channel, "succ");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            HttpUtil.sendResponse(channel, "failed.");
        }
    }

}
