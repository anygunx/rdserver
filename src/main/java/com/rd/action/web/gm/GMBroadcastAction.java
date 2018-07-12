package com.rd.action.web.gm;

import com.rd.bean.chat.PeriodBroadcast;
import com.rd.common.ChatService;
import com.rd.net.web.WebAction;
import com.rd.net.web.WebFilter;
import com.rd.util.DateUtil;
import com.rd.util.HttpUtil;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

/**
 * 广播
 *
 * @author Created by U-Demon on 2016年12月8日 上午11:46:56
 * @version 1.0.0
 */
@WebFilter(filter = "GMBroadcast")
public class GMBroadcastAction extends WebAction {
    private static final Logger logger = Logger.getLogger(GMBroadcastAction.class);

    @Override
    public void doAction(Map<String, String> params, Channel channel) {
        String content = "";
        try {
            content = URLDecoder.decode(params.get("content"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }
        int delay = Integer.valueOf(params.get("delay"));
        int count = Integer.valueOf(params.get("count"));
        int space = Integer.valueOf(params.get("space"));
        long curr = System.currentTimeMillis();
        long startTime = curr + delay * DateUtil.SECOND;
        long endTime = startTime + count * space * DateUtil.SECOND;
        PeriodBroadcast broadcast = PeriodBroadcast.build(content,
                startTime, endTime, space * DateUtil.SECOND);
        ChatService.broadcastSystemMsg(broadcast);
        HttpUtil.sendResponse(channel, "succ");
    }

}
