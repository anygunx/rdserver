package com.rd.task.global;

import com.rd.common.GangService;
import com.rd.model.LadderModel;
import com.rd.task.Task;
import org.apache.log4j.Logger;

/**
 * 每小时执行的任务
 *
 * @author Created by U-Demon on 2016年11月23日 下午1:30:03
 * @version 1.0.0
 */
public class HourTask implements Task {

    private static Logger logger = Logger.getLogger(HourTask.class);

    @Override
    public void run() {
        logger.info("每小时任务执行开始");
        //天梯排行榜信息
        LadderModel.refreshRankPlayerInfo();
        LadderModel.refreshRankLadderInfo();
        GangService.getPtr().updateState();
        try {
            logger.info("调用gc");
            System.gc();
        } catch (Exception e) {
            logger.error("调用gc失败");
        }
    }

    @Override
    public String name() {
        return "HourTask";
    }

}
