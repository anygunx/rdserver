package com.rd.task.global;

import com.rd.dao.ChatDao;
import com.rd.dao.MailDao;
import com.rd.task.Task;
import org.apache.log4j.Logger;

/**
 * 每日清理任务
 */
public class CleanTask implements Task {
    private static Logger logger = Logger.getLogger(CleanTask.class);

    @Override
    public void run() {
        cleanTask();
    }

    public void cleanTask() {
        logger.info("DailyTask start...");
        //清理长期过期邮件
        mailClear();
        //清理长期过期聊天记录
        chatClear();
        logger.info("DailyTask end...");
    }

    private void mailClear() {
        try {
            new MailDao().clearMails();
        } catch (Exception e) {
            logger.error("清理全服长期过期邮件时发生异常.", e);
        }
    }

    private void chatClear() {
        try {
            new ChatDao().clear();
        } catch (Exception e) {
            logger.error("清理全服长期过期私聊记录时发生异常.", e);
        }
    }

    @Override
    public String name() {
        return "clean";
    }

}
