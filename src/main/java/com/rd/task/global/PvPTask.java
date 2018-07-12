package com.rd.task.global;

import com.rd.define.GameDefine;
import com.rd.game.pvp.ArenaPvPManager;
import com.rd.game.pvp.PvPWorld;
import com.rd.task.Task;
import org.apache.log4j.Logger;

/**
 * PVP服每分钟执行的任务
 *
 * @author Created by U-Demon on 2016年11月23日 下午1:30:03
 * @version 1.0.0
 */
public class PvPTask implements Task {

    private static Logger logger = Logger.getLogger(PvPTask.class);

    @Override
    public void run() {
        logger.info("PVP服每分钟执行的任务");
        //更新竞技场
        doUpdateArena();
        //PVP战斗数据失效
        doValidPvP();
    }

    private void doUpdateArena() {
        if (!GameDefine.ISPVP)
            return;
        try {
            for (ArenaPvPManager mgr : PvPWorld.gi().getArenas().values()) {
                mgr.refreshRankList();
            }
        } catch (Exception e) {
            logger.error("PVP竞技场更新出错", e);
        }
    }

    private void doValidPvP() {
        if (!GameDefine.ISPVP)
            return;
        try {
            logger.info("PVP战斗数据时效性开始");
            PvPWorld.gi().validBattlePlayer();
            logger.info("PVP战斗数据时效性完毕");
        } catch (Exception e) {
            logger.error("PVP战斗数据时效性", e);
        }
    }

    @Override
    public String name() {
        return "PvPTask";
    }

}
