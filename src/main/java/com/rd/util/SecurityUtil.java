package com.rd.util;

import com.rd.define.GameDefine;

/**
 * 安全验证的工具类
 *
 * @author Created by U-Demon on 2016年10月26日 下午9:03:54
 * @version 1.0.0
 */
public class SecurityUtil {

    /**
     * 校验HTTP请求的IP是否合法
     *
     * @param ip
     * @return
     */
    public static boolean ipValidate(String ip) {
        for (String url : GameDefine.URL_SAFE_IP) {
            if (isMatch(ip, url)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isMatch(String source, String target) {
        if (target.endsWith("*")) {
            if (source.substring(0, source.lastIndexOf(".") + 1).startsWith(
                    target.substring(0, target.length() - 1)))
                return true;
        } else {
            if (source.startsWith(target))
                return true;
        }
        return false;
    }

    /**
     * 是否SDKServerIP
     *
     * @param ip
     * @return
     */
    public static boolean ipSDKValidate(String ip) {
        for (String url : GameDefine.PAY_SAFE_IP) {
            if (isMatch(ip, url)) {
                return true;
            }
        }
        return false;
    }

}
