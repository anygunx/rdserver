package com.rd.task.global;

import com.rd.game.NGameRankManager;
import com.rd.task.Task;
import org.apache.log4j.Logger;

public class NJingJiSendMailTask implements Task {

    private static Logger logger = Logger.getLogger(DailyTask.class);

    @Override
    public void run() {

        logger.info("NJingJiSendMailTask start...");
        NGameRankManager.getInstance().sendMail();
        logger.info("NJingJiSendMailTask end...");
    }

    @Override
    public String name() {
        // TODO Auto-generated method stub
        return "jingjiSendMail";
    }
}
