package com.rd.net.web;

import com.alibaba.fastjson.JSON;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * 所有GET请求都通过这里处理，需要实现类的抽象方法
 * 这个类里会进行必要的公共部分的处理，例如IP的校验，请求合法性判断等。
 * 请求格式  http://127.0.0.1:3001/ActionName?参数.do
 *
 * @author Created by U-Demon on 2016年10月26日 下午1:27:11
 * @version 1.0.0
 */
public abstract class WebAction {

    private static Logger logger = Logger.getLogger(WebAction.class);

    //是否解析成Json,初始化的时候已经根据注解进行了设置
    public boolean isJson;

    /**
     * 需要子类实现的请求处理
     *
     * @param jsonData
     * @param params
     * @param channel
     */
    public abstract void doAction(Map<String, String> params, Channel channel);

    /**
     * 如果请求的数据为JSON格式，重写下面俩方法
     *
     * @param clazz
     * @param channel
     */
    public <T> void doJsonAction(T jsonObj, Channel channel) {

    }

    public Class<?> getJsonClass() {
        return Object.class;
    }

    /**
     * 对HTTP请求进行合法性校验
     *
     * @param request
     * @return
     */
    public boolean doValidate(HttpRequest request) {
        return true;
    }

    /**
     * JSON数据的请求
     *
     * @param request
     */
    public void doJsonRequest(HttpRequest request, Channel channel) {
        try {
            //校验合法性
            if (!doValidate(request)) {
                logger.debug("WebJson请求合法性校验失败");
                return;
            }
            //提取请求参数
            String uri = new String(request.getUri().getBytes(), "UTF-8");
            String json = uri.substring(uri.indexOf("?") + 1, uri.length() - 3);
            logger.debug("接收到JSON请求：" + json);
            Object data = JSON.parseObject(json, getJsonClass());
            doJsonAction(data, channel);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("处理WebJson请求是发生异常！", e);
        }
    }

    /**
     * 普通数据的请求
     *
     * @param request
     */
    public void doRequest(HttpRequest request, Channel channel) {
        try {
            //校验合法性
            if (!doValidate(request)) {
                logger.debug("Web请求合法性校验失败");
                return;
            }
            //提取请求参数
            String uri = request.getUri();
            Map<String, String> params = getParameters(uri);
            doAction(params, channel);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("处理WebJson请求是发生异常！", e);
        }
    }

    public Map<String, String> getParameters(String uri) {
        Map<String, String> params = new HashMap<>();
        String ps = uri.substring(uri.indexOf("?") + 1, uri.length() - 3);
        if (ps != null && ps.length() > 0) {
            for (String pss : ps.split("&")) {
                String[] psa = pss.split("=");
                if (psa.length == 2) {
                    params.put(psa[0], psa[1]);
                }
            }
        }
        return params;
    }

}
