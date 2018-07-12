package com.rd.net.web;

import com.rd.util.FileUtil;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理Web请求
 *
 * @author Created by U-Demon on 2016年10月26日 下午1:54:45
 * @version 1.0.0
 */
public class WebActionManager {

    private static Logger logger = Logger.getLogger(WebActionManager.class);

    //WebAction存放的包名
    private static final String PACKAGE_NAME = "com.rd.action.web";

    /**
     * 存放所有WebAction的容器
     */
    private static Map<String, WebAction> actionMapper = new HashMap<>();

    /**
     * 外部调用添加新的Action的方法
     *
     * @param filter
     * @param action
     */
    public static void add(String filter, WebAction action) {
        logger.debug("增加的Action=" + filter + "->" + action.getClass().getName());
        actionMapper.put(filter, action);
    }

    /**
     * 通过发送的请求，获得该请求对应的Aciton实例
     *
     * @param filter
     * @return
     */
    public static WebAction getAction(String filter) {
        return actionMapper.get(filter);
    }

    /**
     * 初始化所有的WebAction
     */
    public static void init() {
        //获取包下所有的类
        List<String> webActions = FileUtil.getClasses(PACKAGE_NAME);
        for (String name : webActions) {
            try {
                Class<?> clazz = Class.forName(name);
                //获取所有WebAction注解的类
                WebFilter web = (WebFilter) clazz.getAnnotation(WebFilter.class);
                if (web != null) {
                    WebAction action = (WebAction) clazz.newInstance();
                    action.isJson = web.isJson();
                    add(web.filter(), action);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}
