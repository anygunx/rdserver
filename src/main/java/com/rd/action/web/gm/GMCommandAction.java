package com.rd.action.web.gm;

import com.rd.common.ChatService;
import com.rd.game.GameWorld;
import com.rd.net.web.WebAction;
import com.rd.net.web.WebFilter;
import com.rd.util.HttpUtil;
import org.jboss.netty.channel.Channel;

import java.util.Map;

@WebFilter(filter = "GMCommand")
public class GMCommandAction extends WebAction {

    public void doAction(Map<String, String> params, Channel channel) {
        String command = params.get("command");

        if (command.equals("shutdown")) {
            GameWorld.getPtr().setClose(true);
            GameWorld.getPtr().saveOnlinePlayerData();
            ChatService.saveAllChat();
            HttpUtil.sendResponse(channel, "succ");
            System.exit(0);
        } else if (command.startsWith("ban:")) {
            short cmdId = Short.valueOf(command.substring(4));
            GameWorld.getPtr().addBanCmd(cmdId);
            HttpUtil.sendResponse(channel, "succ");
        } else if (command.startsWith("unban:")) {
            short cmdId = Short.valueOf(command.substring(6));
            GameWorld.getPtr().removeBanCmd(cmdId);
            HttpUtil.sendResponse(channel, "succ");
        } else {
            HttpUtil.sendResponse(channel, "fail");
        }
    }
}
