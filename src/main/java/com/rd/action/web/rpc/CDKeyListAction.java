package com.rd.action.web.rpc;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.rd.game.GameWorld;
import com.rd.game.IGameRole;
import com.rd.net.web.WebAction;
import com.rd.net.web.WebFilter;
import com.rd.util.HttpUtil;
import com.rd.util.SecurityUtil;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

import java.util.Map;

import static com.rd.action.web.rpc.RPCDefine.*;

/**
 * Created by XingYun on 2017/2/16.
 */
@WebFilter(filter = "CDKeyList")
public class CDKeyListAction extends WebAction {
    private static final Logger logger = Logger.getLogger(CDKeyListAction.class.getName());

    @Override
    public void doAction(Map<String, String> params, Channel ch) {
        String ip = ch.getRemoteAddress().toString();
        if (!SecurityUtil.ipSDKValidate(ip)) {
            HttpUtil.sendResponse(ch, CODE_ERROR);
            logger.error("CDKeyListAction reject ip from " + ip);
            return;
        }
        JSONObject resultObj = new JSONObject();
        try {
            int playerId = Integer.valueOf(params.get("playerid"));
            IGameRole role = GameWorld.getPtr().getGameRole(playerId);
            Preconditions.checkNotNull(role, "CDKeyListAction.doAction() failed. Unexpected playerId=" + playerId);
            resultObj.put(PARAM_RET, CODE_SUCCESS);
            resultObj.put("playerId", playerId);
            resultObj.put("cdkeyList", role.getPlayer().getCDKeyListJson());
            HttpUtil.sendResponse(ch, resultObj.toJSONString());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            resultObj.put(PARAM_RET, CODE_ERROR);
            HttpUtil.sendResponse(ch, resultObj.toJSONString());
        }
    }
}
