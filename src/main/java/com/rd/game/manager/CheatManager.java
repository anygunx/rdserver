package com.rd.game.manager;

import com.rd.define.ErrorDefine;
import com.rd.game.GameRole;
import com.rd.net.message.Message;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * 作弊管理
 *
 * @author U-Demon Created on 2017年5月12日 下午4:36:11
 * @version 1.0.0
 */
public class CheatManager {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(CheatManager.class);

    private GameRole role;

    //消息间隔
    private static final int REQUEST_SPAN = 3000;
    //上次请求时间
    private Map<Short, Long> requestTime = new HashMap<>();

    public CheatManager(GameRole role) {
        this.role = role;
    }

    public void init() {

    }

    /**
     * 消息是否太过频繁
     *
     * @param request
     * @return
     */
    public boolean requestFrequent(Message request) {
        long curr = System.currentTimeMillis();
        short cmdId = request.getCmdId();
        //第一次请求该消息
        if (!requestTime.containsKey(cmdId)) {
            requestTime.put(cmdId, curr);
            return false;
        }
        long lastTime = requestTime.get(cmdId);
        //消息频繁
        if (curr - lastTime <= REQUEST_SPAN) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_OVER_QUICK);
            return true;
        }
        requestTime.put(cmdId, curr);
        return false;
    }

}
