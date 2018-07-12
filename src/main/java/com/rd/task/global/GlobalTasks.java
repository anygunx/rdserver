package com.rd.task.global;

import com.rd.define.GameDefine;
import com.rd.model.LadderModel;
import com.rd.task.ETaskType;
import com.rd.task.Task;
import com.rd.task.TaskManager;
import com.rd.util.DateUtil;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * {@code ETaskType.GLOBAL}
 *
 * @author Created by U-Demon on 2016年11月4日 下午2:50:00
 * @version 1.0.0
 */
public class GlobalTasks {

    private static Logger logger = Logger.getLogger(GlobalTasks.class);

    //每日任务
    private static final Map<Task, String> DAILY_TASKS = new HashMap<Task, String>() {
        private static final long serialVersionUID = 1L;

        {
            put(new DailyTask(), "00:00:01");
            put(new CleanTask(), "03:15:00");
            put(new NJingJiSendMailTask(), "21:00:01");
        }
    };

    private static GlobalTasks _instance = new GlobalTasks();

    public static GlobalTasks gi() {
        return _instance;
    }

    private GlobalTasks() {
    }

    ;

    /**
     * 初始化GLOBAL task
     */
    public void init() {
        initHourTask();
        initHalfTask();
        initDailyTask();
        initTickTask();
        initMinuteTask();
        initLadderRewardTask();
    }

    public void initPvP() {
        initPvPTask();
    }

    /**
     * 初始化整点任务
     */
    public void initHourTask() {
        try {
            long currentTime = System.currentTimeMillis();
            long nextHourTime = DateUtil.getLastClockTime(currentTime) + DateUtil.HOUR;
            TaskManager.getInstance().schedulePeriodicTask(ETaskType.GLOBAL,
                    new HourTask(), nextHourTime - currentTime, DateUtil.HOUR);
        } catch (Exception e) {
            logger.error("初始化整点任务失败", e);
        }
    }

    /**
     * 初始化半点任务
     */
    public void initHalfTask() {
        try {
            long currTime = System.currentTimeMillis();
            long spaceTime = DateUtil.MINUTE * 30;
            long nextTime = DateUtil.getLastSpaceTime(currTime, spaceTime) + spaceTime - 1 * DateUtil.MINUTE;
            TaskManager.getInstance().schedulePeriodicTask(ETaskType.GLOBAL,
                    new HalfTask(), nextTime - currTime, spaceTime);
        } catch (Exception e) {
            logger.error("初始化半点任务失败", e);
        }
    }

    /**
     * 初始化每日任务
     */
    private void initDailyTask() {
        try {
            for (Entry<Task, String> entry : DAILY_TASKS.entrySet()) {
                Task task = entry.getKey();
                String time = entry.getValue();
                TaskManager.getInstance().scheduleDailyTask(ETaskType.GLOBAL, task, time);
            }
        } catch (Exception e) {
            logger.error("初始化每日任务发生异常。", e);
        }
    }

    /**
     * 初始化心跳任务
     */
    private void initTickTask() {
        try {
            TickTask task = new TickTask();
            TaskManager.getInstance().schedulePeriodicTask(ETaskType.GLOBAL, task, 100, DateUtil.SECOND);
        } catch (Exception e) {
            logger.error("初始化每日任务发生异常。", e);
        }
    }

    /**
     * 初始化每分钟任务
     */
    private void initMinuteTask() {
        try {
            TaskManager.getInstance().schedulePeriodicTask(ETaskType.GLOBAL, new MinuteTask(), 100, DateUtil.MINUTE);
        } catch (Exception e) {
            logger.error("初始化每分钟任务发生异常。", e);
        }
    }

    /**
     * 初始化每分钟任务
     */
    private void initPvPTask() {
        if (!GameDefine.ISPVP)
            return;
        try {
            TaskManager.getInstance().schedulePeriodicTask(ETaskType.GLOBAL,
                    new PvPTask(), 500, DateUtil.MINUTE);
        } catch (Exception e) {
            logger.error("初始化每分钟任务发生异常。", e);
        }
    }

    /**
     * 初始化天梯竞技场的结算奖励任务
     */
    private void initLadderRewardTask() {
        try {
            LadderModel.refreshSeasonTime();
            long delay = LadderModel.SEASON_REWARD_TIME - System.currentTimeMillis();
            TaskManager.getInstance().schedulePeriodicTask(ETaskType.COMMON, new LadderRewardTask(),
                    delay, LadderModel.LAST_TIME + LadderModel.START_TIME);
        } catch (Exception e) {
            logger.error("结算天梯竞技场的奖励发生异常。", e);
        }
    }

}
