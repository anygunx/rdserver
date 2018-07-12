package com.rd.action.web.rpc;

import com.alibaba.fastjson.JSONObject;
import com.rd.bean.support.ServerInfo;
import com.rd.game.GameSupportUtil;
import com.rd.net.web.WebAction;
import com.rd.net.web.WebFilter;
import com.rd.util.HttpUtil;
import com.rd.util.SecurityUtil;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

import java.util.Collection;
import java.util.Map;

/**
 * Created by XingYun on 2017/6/5.
 */
@WebFilter(filter = "ServerInfo")
public class ServerInfoAction extends WebAction {
    private static final Logger logger = Logger.getLogger(ServerInfoAction.class.getName());

    @Override
    public void doAction(Map<String, String> params, Channel ch) {
        JSONObject info = new JSONObject();
        String ip = ch.getRemoteAddress().toString();
        if (!SecurityUtil.ipSDKValidate(ip)) {
            info.put(RPCDefine.PARAM_RET, RPCDefine.CODE_ERROR);
            HttpUtil.sendResponse(ch, info.toJSONString());
            logger.error("ServerInfoAction reject ip from " + ip);
            return;
        }

        try {
            Collection<ServerInfo> infoList = GameSupportUtil.getServerADInfoList();
            info.put(RPCDefine.PARAM_RET, RPCDefine.CODE_SUCCESS);
            info.put("serverinfo", JSONObject.toJSONString(infoList));
            HttpUtil.sendResponse(ch, info.toJSONString());
        } catch (Exception e) {
            info.put(RPCDefine.PARAM_RET, RPCDefine.CODE_ERROR);
            HttpUtil.sendResponse(ch, info.toJSONString());
            logger.error(e.getMessage(), e);
        }

    }
}