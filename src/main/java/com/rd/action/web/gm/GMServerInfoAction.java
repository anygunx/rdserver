package com.rd.action.web.gm;

import com.rd.dao.GMDao;
import com.rd.net.web.WebAction;
import com.rd.net.web.WebFilter;
import com.rd.util.HttpUtil;
import org.jboss.netty.channel.Channel;

import java.util.Map;

/**
 * 服务器信息
 *
 * @author Created by U-Demon on 2016年12月8日 上午11:46:56
 * @version 1.0.0
 */
@WebFilter(filter = "GMServerInfo")
public class GMServerInfoAction extends WebAction {

    @Override
    public void doAction(Map<String, String> params, Channel channel) {
        String result = GMDao.getInstance().gmGetServerInfo();
        if (result.length() == 0) {
            HttpUtil.sendResponse(channel, "fail");
            return;
        }
        HttpUtil.sendResponse(channel, result);
    }

}
