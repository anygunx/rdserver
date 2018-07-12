package com.rd.action.web.rpc;

import com.rd.game.GameSupportUtil;
import com.rd.net.web.WebAction;
import com.rd.net.web.WebFilter;
import com.rd.util.HttpUtil;
import com.rd.util.SecurityUtil;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

import java.util.Map;

import static com.rd.action.web.rpc.RPCDefine.CODE_ERROR;
import static com.rd.action.web.rpc.RPCDefine.CODE_SUCCESS;

/**
 * Created by XingYun on 2016/11/23.
 */
@WebFilter(filter = "Focus")
public class FocusAction extends WebAction {
    private static final Logger logger = Logger.getLogger(FocusAction.class.getName());

    @Override
    public void doAction(Map<String, String> params, Channel ch) {
        String ip = ch.getRemoteAddress().toString();
        if (!SecurityUtil.ipSDKValidate(ip)) {
            HttpUtil.sendResponse(ch, CODE_ERROR);
            logger.error("FocusAction reject ip from " + ip);
            return;
        }
        try {
            // 元宝奖励
            int diamond = Integer.valueOf(params.get("diamond"));
            int playerId = Integer.valueOf(params.get("playerid"));
            boolean result = GameSupportUtil.callbackOnFocus(playerId, diamond);
            logger.info("FocusAction.doAction() success. playerId= " + playerId + ", diamond=" + diamond);
            HttpUtil.sendResponse(ch, result ? CODE_SUCCESS : CODE_ERROR);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            HttpUtil.sendResponse(ch, CODE_ERROR);
        }
    }
}
