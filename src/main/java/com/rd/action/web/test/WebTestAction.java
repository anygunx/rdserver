package com.rd.action.web.test;

import com.rd.net.web.WebAction;
import com.rd.net.web.WebFilter;
import com.rd.util.HttpUtil;
import org.jboss.netty.channel.Channel;

import java.util.Map;

/**
 * 测试普通数据格式请求
 * http://127.0.0.1:3001/test?test=this is a common test.do
 *
 * @author Created by U-Demon on 2016年10月26日 下午2:07:13
 * @version 1.0.0
 */
@WebFilter(filter = "test")
public class WebTestAction extends WebAction {

    @Override
    public void doAction(Map<String, String> params, Channel channel) {
        System.out.println(params.get("test"));
        HttpUtil.sendResponse(channel, "common test!");
    }

}
