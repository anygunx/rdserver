package com.rd.game.manager;

import com.rd.bean.support.SupportData;
import com.rd.game.GameRole;
import org.apache.log4j.Logger;

public class SupportManager {
    private static Logger logger = Logger.getLogger(SupportManager.class);
    private GameRole gameRole;
    /**
     * 获取辅助功能数据
     */
    private SupportData supportData = null;

    public SupportManager(GameRole gameRole) {
        this.gameRole = gameRole;
    }

    public void init() {

    }

    public SupportData getSupport() {
        if (supportData != null) {
            return supportData;
        }
        // 不修改暂时屏蔽
//        // 很少用到 延迟处理 重要数据放在init处理
//        supportData = gameRole.getDbManager().supportDao.getData(gameRole.getPlayerId());
//        if (supportData != null){
//            return supportData;
//        }
//        supportData = new SupportData(gameRole.getPlayerId());
//        gameRole.getDbManager().supportDao.insertData(supportData);
        return supportData;
    }

}
