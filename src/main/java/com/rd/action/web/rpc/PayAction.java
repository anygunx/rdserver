package com.rd.action.web.rpc;

import com.alibaba.fastjson.JSON;
import com.rd.action.GameAction;
import com.rd.bean.pay.OrderData;
import com.rd.game.GamePayUtil;
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
@WebFilter(filter = "Pay")
public class PayAction extends WebAction {
    private static final Logger logger = Logger.getLogger(PayAction.class.getName());

    @Override
    public void doAction(Map<String, String> params, Channel ch) {
        String ip = ch.getRemoteAddress().toString();
        if (!SecurityUtil.ipSDKValidate(ip)) {
            HttpUtil.sendResponse(ch, CODE_ERROR);
            logger.error("PayAction reject ip from " + ip);
            return;
        }
        try {
            String account = params.get("account");
            String orderid = params.get("orderid");
            short channel = Short.valueOf(params.get("channel"));
            short serverId = Short.valueOf(params.get("server"));
            byte platform = Byte.valueOf(params.get("platform"));
            int playerId = Integer.valueOf(params.get("playerid"));
            int amount = Integer.valueOf(params.get("amount"));//单位是元
            long currentTime = System.currentTimeMillis();
            logger.info("channel=" + channel + ",account=" + account
                    + ",server=" + serverId + ",playerid=" + playerId + ",orderid=" + orderid + ",amount=" + amount);

            GameAction.submit(playerId, () -> {
                // 排队执行
                OrderData orderData = new OrderData();
                orderData.init(channel, serverId, account, platform, playerId, orderid, amount);
                orderData.setCreateTime(currentTime);

                orderData = GamePayUtil.callbackOnPay(orderData);
                if (orderData == null) {
                    HttpUtil.sendResponse(ch, CODE_ERROR);
                    return;
                }
                String orderStr = JSON.toJSONString(orderData);
                logger.info("PayAction.doAction() success. " + orderStr);
                HttpUtil.sendResponse(ch, CODE_SUCCESS + orderStr);
            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            HttpUtil.sendResponse(ch, CODE_ERROR);
        }

    }
}
