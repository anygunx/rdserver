package com.rd.action.web.test;

import com.rd.net.web.WebAction;
import com.rd.net.web.WebFilter;
import com.rd.util.HttpUtil;
import org.jboss.netty.channel.Channel;

import java.util.Map;

/**
 * 测试JSON数据格式请求
 * http://127.0.0.1:3001/testJson?{"id":1,"name":"zzq"}.do
 *
 * @author Created by U-Demon on 2016年10月26日 下午2:21:21
 * @version 1.0.0
 */
@WebFilter(filter = "testJson", isJson = true)
public class WebTestJsonAction extends WebAction {

    @Override
    public void doAction(Map<String, String> params, Channel channel) {

    }

    @SuppressWarnings("hiding")
    @Override
    public <WebTestJsonData> void doJsonAction(WebTestJsonData jsonObj, Channel channel) {
        System.out.println(jsonObj.toString());
        HttpUtil.sendResponse(channel, "json test!");
    }

    @Override
    public Class<?> getJsonClass() {
        return WebTestJsonData.class;
    }

}
