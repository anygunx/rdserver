package com.rd.task;

import com.rd.util.DateUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 任务管理器
 *
 * @author Created by U-Demon on 2016年10月31日 下午3:32:34
 * @version 1.0.0
 */
public class TaskManager {

    /**
     * 不同类型的任务调度
     */
    private Map<ETaskType, TaskScheduler> taskSchedulerMap;

    private static final TaskManager instance = new TaskManager();

    private TaskManager() {

    }

    public static TaskManager getInstance() {
        return instance;
    }

    /**
     * 初始化任务管理器
     */
    public void init() {
        taskSchedulerMap = new HashMap<>();

        TaskScheduler globalSche = new TaskSchedulerImpl(5);
        taskSchedulerMap.put(ETaskType.GLOBAL, globalSche);

        TaskScheduler activitySche = new TaskSchedulerImpl(3);
        taskSchedulerMap.put(ETaskType.ACTIVITY, activitySche);

        TaskScheduler commonSche = new TaskSchedulerImpl(5);
        taskSchedulerMap.put(ETaskType.COMMON, commonSche);

        TaskScheduler logScheduler = new TaskSchedulerImpl(8);
        taskSchedulerMap.put(ETaskType.LOG, logScheduler);

        TaskScheduler logicScheduler = new TaskSchedulerImpl(3);
        taskSchedulerMap.put(ETaskType.LOGIC, logicScheduler);
    }

    public void release() {
        taskSchedulerMap.clear();
        taskSchedulerMap = null;
    }

    public boolean startService() {
        for (TaskScheduler taskScheduler : taskSchedulerMap.values()) {
            taskScheduler.startup();
        }
        return true;
    }

    public boolean stopService() {
        for (TaskScheduler taskScheduler : taskSchedulerMap.values()) {
            taskScheduler.shutdown();
        }
        return true;
    }

    public TaskScheduler geTaskScheduler(ETaskType taskType) {
        return taskSchedulerMap.get(taskType);
    }

    /**
     * 调度周期任务
     *
     * @param taskType
     * @param task
     * @param delay
     * @param period
     * @return
     */
    public PeriodicTaskHandle schedulePeriodicTask(ETaskType taskType, Task task, long delay, long period) {
        TaskScheduler taskScheduler = geTaskScheduler(taskType);
        return taskScheduler.schedulePeriodTask(task, delay, period);
    }

    /**
     * 中止任务
     *
     * @param taskType
     * @param task
     */
    public void cancleTask(ETaskType taskType, Task task) {
        TaskScheduler taskScheduler = geTaskScheduler(taskType);
        taskScheduler.cancelTask(task);
    }

    /**
     * 中止任务
     *
     * @param taskType
     * @param name
     */
    public void cancleTask(ETaskType taskType, String name) {
        TaskScheduler taskScheduler = geTaskScheduler(taskType);
        taskScheduler.cancelTask(name);
    }

    /**
     * 计划每日任务
     *
     * @param taskType
     * @param task
     * @param formatTime
     * @return
     * @throws ParseException
     */
    public PeriodicTaskHandle scheduleDailyTask(ETaskType taskType, final Task task, String formatTime)
            throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        DateFormat dayFormat = new SimpleDateFormat("yy-MM-dd");
        Date targetDate = dateFormat.parse(dayFormat.format(new Date()) + " " + formatTime);
        long targetTime = targetDate.getTime();
        long initDelay = targetTime - System.currentTimeMillis();
        initDelay = initDelay > 0 ? initDelay : DateUtil.DAY + initDelay;

        return schedulePeriodicTask(taskType, task, initDelay, DateUtil.DAY);
    }

    /**
     * 立即执行一个任务
     *
     * @param taskType
     * @param task
     * @return
     */
    public PeriodicTaskHandle scheduleTask(ETaskType taskType, Task task) {
        if (task == null)
            throw new NullPointerException("Task must not be null");
        TaskScheduler taskScheduler = geTaskScheduler(taskType);
        return taskScheduler.scheduleTask(task);
    }

    /**
     * 调度延时任务
     *
     * @param taskType
     * @param task
     * @param delay
     * @return
     */
    public PeriodicTaskHandle scheduleDelayTask(ETaskType taskType, Task task, long delay) {
        if (task == null)
            throw new NullPointerException("Task must not be null");
        if (delay < 0)
            throw new IllegalArgumentException("Delay must not be negative");
        TaskScheduler taskScheduler = geTaskScheduler(taskType);
        return taskScheduler.scheduleTask(task, delay);
    }

}
