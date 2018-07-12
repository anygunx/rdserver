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
@WebFilter(filter = "CDKeyReward")
public class CDKeyRewardAction extends WebAction {
    private static final Logger logger = Logger.getLogger(CDKeyRewardAction.class.getName());

    @Override
    public void doAction(Map<String, String> params, Channel ch) {
        String ip = ch.getRemoteAddress().toString();
        if (!SecurityUtil.ipSDKValidate(ip)) {
            HttpUtil.sendResponse(ch, CODE_ERROR);
            logger.error("CDKeyRewardAction reject ip from " + ip);
            return;
        }
        try {
            // 元宝奖励
            int boxId = Integer.valueOf(params.get("boxId"));
            int modelId = Integer.valueOf(params.get("modelId"));
            int playerId = Integer.valueOf(params.get("playerId"));
            GameSupportUtil.callbackCDKeyReward(playerId, modelId, boxId);
            logger.info("CDKeyRewardAction.doAction() success. playerId= " + playerId + ", modelId=" + modelId + ", boxId=" + boxId);
            HttpUtil.sendResponse(ch, CODE_SUCCESS);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            HttpUtil.sendResponse(ch, CODE_ERROR);
        }
    }
}
