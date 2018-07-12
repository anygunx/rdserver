package com.rd.action.web.gm;

import com.rd.bean.pay.OrderData;
import com.rd.bean.player.Player;
import com.rd.dao.GMDao;
import com.rd.game.GamePayUtil;
import com.rd.net.web.WebAction;
import com.rd.net.web.WebFilter;
import com.rd.util.HttpUtil;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

import java.util.Map;

/**
 * 简易测试充值
 *
 * @author Created by U-Demon on 2016年12月16日 下午7:35:46
 * @version 1.0.0
 */
@WebFilter(filter = "GMTestPay")
public class GMTestPayAction extends WebAction {
    static Logger log = Logger.getLogger(GMTestPayAction.class.getName());

    @Override
    public void doAction(Map<String, String> params, Channel channel) {
        String account = params.get("account");
        int serverId = Integer.valueOf(params.get("serverId"));
        Player player = GMDao.getInstance().gmGetPlayerInfo(serverId, account);
        if (player == null) {
            HttpUtil.sendResponse(channel, "fail");
            return;
        }
        int pay = Integer.valueOf(params.get("pay"));

        boolean result = false;
        //月卡
        if (pay <= 0) {
            //result = MonthlyCardService.buyMonthlyCard(player.getId(), (byte)1);
        }
        //普通充值
        else {
            OrderData order = new OrderData();
            order.setChannelId(player.getChannel());
            order.setSubChannel(player.getSubChannel());
            order.setOrderId(System.currentTimeMillis() + "");
            order.setServerId(player.getServerId());
            order.setAccount(account);
            order.setPlayerId(player.getId());
            order.setAmount(pay);
            result = (GamePayUtil.callbackOnPay(order) != null);
        }

        if (result) {
            HttpUtil.sendResponse(channel, "succ");
        } else {
            HttpUtil.sendResponse(channel, "fail");
        }

        log.info("GMTestPayAction account=" + account + " pay=" + pay);
    }

}
