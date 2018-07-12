package com.rd.bean.chat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.rd.define.ChatDefine;
import com.rd.net.message.Message;
import com.rd.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 私聊记录
 * Created by XingYun on 2017/5/5.
 */
public class ChatHistory {
    private int playerId;
    private final ArrayBlockingQueue<ChatPrivate> queue;

    private boolean dirty;
    private long lastUpdateTime;

    public ChatHistory(int playerId) {
        this.playerId = playerId;
        this.queue = new ArrayBlockingQueue<>(ChatDefine.PRIVATE_HISTORY_CAPACITY);
        this.dirty = false;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public void init(String data, long lastUpdateTime) {
        long currentTime = System.currentTimeMillis();
        if (!StringUtil.isEmpty(data)) {
            List<ChatPrivate> dataList = JSON.parseObject(data, new TypeReference<ArrayList<ChatPrivate>>() {
            });
            for (ChatPrivate chat : dataList) {
                if (currentTime - chat.getTs() >= ChatDefine.PRIVATE_HISTORY_REMAIN_TIME) {
                    // 过滤过期数据
                    continue;
                }
                this.queue.add(chat);
                setDirty(true);
            }
        }
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getData() {
        return JSON.toJSONString(queue);
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public int getPlayerId() {
        return playerId;
    }

    public static void getMessage(ChatHistory history, Message message) {
        if (history == null) {
            message.setByte(0);
            return;
        } else {
            ArrayBlockingQueue<ChatPrivate> chatQueue = history.queue;
            message.setByte(chatQueue.size());
            for (ChatPrivate chat : chatQueue) {
                chat.getMessage(message, history.getPlayerId());
            }
        }
    }

    /**
     * 添加记录
     *
     * @param chat
     */
    public void add(ChatPrivate chat) {
        int times = 0;
        while (!queue.offer(chat) && times++ < 20) {
            queue.poll();
        }
        setDirty(true);
    }

    /**
     * 移除相关记录
     *
     * @param targetPlayerId
     */
    public void removeAll(int targetPlayerId) {
        for (ChatPrivate chat : queue) {
            if (chat.getFrom() == targetPlayerId || chat.getTo() == targetPlayerId) {
                queue.remove(chat);
            }
        }
        setDirty(true);
    }
}