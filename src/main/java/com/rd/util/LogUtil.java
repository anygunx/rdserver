package com.rd.util;

import com.rd.bean.drop.DropData;
import com.rd.define.EGoodsChangeType;
import com.lg.bean.ILog;
import com.lg.bean.IPlayer;
import com.lg.bean.PlayerLog;
import com.rd.task.ETaskType;
import com.rd.task.Task;
import com.rd.task.TaskManager;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by XingYun on 2016/6/15.
 */
public class LogUtil {
    private static final Logger logger = Logger.getLogger(LogUtil.class);

    public static final void log(final ILog log) {
        TaskManager.getInstance().scheduleTask(ETaskType.LOG, new Task() {
            @Override
            public void run() {
                try {
                    logger.info(log.getFormatLog());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public String name() {
                return "log";
            }
        });
    }

    public static final void log(final IPlayer player, final PlayerLog log) {
        TaskManager.getInstance().scheduleTask(ETaskType.LOG, new Task() {
            @Override
            public void run() {
                try {
                    log.init(player.getId(), player.getAccount(), player.getChannel(), player.getSubChannel(), player.getServerId(), player.getName(), player.getPlatform());
                    logger.info(log.getFormatLog());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public String name() {
                return "log_player";
            }
        });
    }

//    public static final void log(final IPlayer player, final List<PlayerLog> logList){
//        TaskManager.getInstance().scheduleTask(ETaskType.LOG, new Task() {
//            @Override
//            public void run() {
//                try {
//                    for (PlayerLog log: logList) {
//                        log.init(player.getId(), player.getAccount(), player.getChannel(), player.getServerId());
//                        logger.info(log.getFormatLog());
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }


    public static final void logGoodsConsume(final IPlayer player, final List<DropData> dropList, final EGoodsChangeType changeType) {
        TaskManager.getInstance().scheduleTask(ETaskType.LOG, new Task() {
            @Override
            public void run() {
                try {
//                	for (DropData dropData: dropList) {
//                		PlayerLog log = new LogGoodsChange(dropData.getT(), dropData.getG(), -dropData.getN(), changeType.getId());
//                		log.init(player.getId(), player.getAccount(), player.getChannel(), player.getChannelInner(), player.getServerId(), player.getName());
//                		logger.info(log.getFormatLog());
//                	}
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public String name() {
                // TODO Auto-generated method stub
                return null;
            }
        });
    }

    public static final void logGoodsAdd(final IPlayer player, final List<DropData> dropList, final EGoodsChangeType changeType) {
        TaskManager.getInstance().scheduleTask(ETaskType.LOG, new Task() {
            @Override
            public void run() {
                try {
//                    for (DropData dropData: dropList) {
//                        PlayerLog log = new LogGoodsChange(dropData.getT(), dropData.getG(), dropData.getN(), changeType.getId());
//                        log.init(player.getId(), player.getAccount(), player.getChannel(), player.getChannelInner(), player.getServerId(), player.getName());
//                        logger.info(log.getFormatLog());
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public String name() {
                // TODO Auto-generated method stub
                return null;
            }
        });
    }

    public static final void info(final String log) {
        TaskManager.getInstance().scheduleTask(ETaskType.LOG, new Task() {
            @Override
            public void run() {
                try {
                    logger.info(log);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public String name() {
                // TODO Auto-generated method stub
                return null;
            }
        });
    }
}
