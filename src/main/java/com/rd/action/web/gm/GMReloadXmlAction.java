package com.rd.action.web.gm;

import com.rd.game.GameGangManager;
import com.rd.model.resource.ResourceManager;
import com.rd.net.web.WebAction;
import com.rd.net.web.WebFilter;
import com.rd.task.global.DailyTask;
import com.rd.task.global.LadderRewardTask;
import com.rd.util.HttpUtil;
import org.jboss.netty.channel.Channel;

import java.util.Map;

/**
 * ReloadXml
 *
 * @author Created by U-Demon on 2016年12月16日 下午7:35:46
 * @version 1.0.0
 */
@WebFilter(filter = "GMReloadXml")
public class GMReloadXmlAction extends WebAction {

    @Override
    public void doAction(Map<String, String> params, Channel channel) {
        String listen = params.get("listen");
        String xml = params.get("xml");
        if (listen.equals("dailyTask") && xml.equals("dailyTask")) {
            new DailyTask().dailyTask();
            HttpUtil.sendResponse(channel, "succ");
        } else if (listen.equals("gangdatabase") && xml.equals("gangdatabase")) {
            GameGangManager.getInstance().loadGang();
            HttpUtil.sendResponse(channel, "succ");
        } else if (listen.equals("ladder") && xml.equals("ladder")) {
            LadderRewardTask.ladderRewardTask();
            HttpUtil.sendResponse(channel, "succ");
        } else {
            boolean result = ResourceManager.getInstance().reload(listen, xml);
            if (result) {
                HttpUtil.sendResponse(channel, "succ");
            } else {
                HttpUtil.sendResponse(channel, "fail");
            }
        }
    }

}
