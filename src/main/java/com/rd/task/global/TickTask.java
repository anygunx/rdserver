package com.rd.task.global;

import com.rd.game.AuctionService;
import com.rd.game.GameMonsterSiegeService;
import com.rd.game.GameWorld;
import com.rd.task.Task;
import org.apache.log4j.Logger;

/**
 * 心跳任务
 *
 * @author Created by U-Demon on 2016年11月8日 下午8:35:50
 * @version 1.0.0
 */
public class TickTask implements Task {
    private static Logger logger = Logger.getLogger(TickTask.class);

    @Override
    public void run() {
        try {
            GameWorld.getPtr().onTick();
            long ts = System.currentTimeMillis();
            AuctionService.onTick(ts);
            GameMonsterSiegeService.onTick(ts);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    @Override
    public String name() {
        return "tick";
    }

}
