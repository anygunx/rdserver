package com.rd.task.global;

import com.rd.game.GameMonsterSiegeService;
import com.rd.game.GameRankManager;
import com.rd.task.Task;
import org.apache.log4j.Logger;

/**
 * 半点执行的任务
 *
 * @author Created by U-Demon on 2016年11月23日 下午1:30:03
 * @version 1.0.0
 */
public class HalfTask implements Task {

    private static Logger logger = Logger.getLogger(HalfTask.class);

    @Override
    public void run() {
        logger.info("半点任务执行开始");
        //排行榜
        doUpdateRank();
    }

    private void doUpdateRank() {
        try {
            logger.info("排行数据搜集开始");
            //有些数据格式不好比较，在这里搜集一下数据。
            //不做内存表是保证server重启后依然可以正确排序
//            PlayerDao playerDao = new PlayerDao();
//            for (GameRole gameRole : GameWorld.getPtr().getOnlineRoles().values())
//            {
//                playerDao.updatePlayerRanksData(gameRole.getPlayer());
//            }
            logger.info("排行数据搜集完毕");
            //排行
            GameRankManager.getInstance().startRank();
            //怪物攻城积分排行
            GameMonsterSiegeService.loadRanks();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public String name() {
        return "HalfTask";
    }

}
