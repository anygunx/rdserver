package com.rd.common;

import com.rd.bean.drop.DropData;
import com.rd.bean.player.Player;
import com.rd.game.GameWorld;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 广播服务类
 *
 * @author U-Demon Created on 2017年3月13日 下午3:02:25
 * @version 1.0.0
 */
public class BroadcastService {

    private static final Logger logger = Logger.getLogger(BroadcastService.class);

    //缓存的宠物广播
    private static final int PETMAX = 10;
    private static Queue<String> petQueue = new ConcurrentLinkedQueue<>();

    //寻宝广播
    private static final int XUNBAOMAX = 10;
    private static Queue<String> xunbaoQueue = new ConcurrentLinkedQueue<>();

    //元宝王者广播
    private static final int TURNTABLE = 10;
    private static Queue<String> turnTableQueue = new ConcurrentLinkedQueue<>();

    /**
     * 缓存宠物广播
     */
    public static void addPetMsg(String name, List<DropData> rewards) {
        while (petQueue.size() + rewards.size() > PETMAX) {
            petQueue.poll();
        }
        for (DropData data : rewards) {
            StringBuilder sb = new StringBuilder();
            sb.append(name).append(",").append(data.getT()).append(",").append(data.getG())
                    .append(",").append(data.getQ()).append(",").append(data.getN());
            petQueue.offer(sb.toString());
        }
    }

    public static Message getAllPetMsg(Player player) {
        Message msg = new Message(MessageCommand.BROADCAST_PET_MESSAGE);
        msg.setByte(player.getDayData().getPetFree());
        msg.setBool(true);
        try {
            msg.setByte(petQueue.size());
            for (String str : petQueue) {
                String[] ss = str.split(",");
                if (ss.length != 5)
                    continue;
                msg.setString(ss[0]);
                msg.setByte(Byte.valueOf(ss[1]));
                msg.setShort(Short.valueOf(ss[2]));
                msg.setByte(Byte.valueOf(ss[3]));
                msg.setInt(Integer.valueOf(ss[4]));
            }
        } catch (Exception e) {
            logger.error("获取宠物抽奖广播时发生异常", e);
            msg = new Message(MessageCommand.BROADCAST_PET_MESSAGE);
            msg.setByte(0);
            msg.setByte(0);
        }
        return msg;
    }

    //元宝王者
    public static void addTurntableMsg(String... msg) {
        if (msg == null || msg.length == 0) {
            return;
        }
        while (turnTableQueue.size() + msg.length > TURNTABLE) {
            turnTableQueue.poll();
        }
        Message message = new Message(MessageCommand.TURN_TABLE_LOG);
        message.setByte(msg.length);
        for (String s : msg) {
            message.setString(s);
            turnTableQueue.offer(s);
        }
        GameWorld.getPtr().broadcast(message);
    }

    public static void addXunbaoMsg(String name, List<DropData> rewards) {
        while (xunbaoQueue.size() + rewards.size() > XUNBAOMAX) {
            xunbaoQueue.poll();
        }
        Message msg = new Message(MessageCommand.RED_LOG_MESSAGE);
        msg.setByte(rewards.size());
        for (DropData data : rewards) {
            msg.setString(name);
            data.getMessage(msg);
            StringBuilder sb = new StringBuilder();
            sb.append(name).append(",").append(data.getT()).append(",").append(data.getG())
                    .append(",").append(data.getQ()).append(",").append(data.getN());
            xunbaoQueue.offer(sb.toString());
        }
        GameWorld.getPtr().broadcast(msg);
    }

    public static Message getAllXunbaoMsg() {
        Message msg = new Message(MessageCommand.RED_LOG_MESSAGE);
        try {
            msg.setByte(xunbaoQueue.size());
            for (String str : xunbaoQueue) {
                String[] ss = str.split(",");
                if (ss.length != 5)
                    continue;
                msg.setString(ss[0]);
                msg.setByte(Byte.valueOf(ss[1]));
                msg.setShort(Short.valueOf(ss[2]));
                msg.setByte(Byte.valueOf(ss[3]));
                msg.setInt(Integer.valueOf(ss[4]));
            }
        } catch (Exception e) {
            logger.error("获取寻宝广播时发生异常", e);
            msg = new Message(MessageCommand.RED_LOG_MESSAGE);
            msg.setByte(0);
        }
        return msg;
    }

}
