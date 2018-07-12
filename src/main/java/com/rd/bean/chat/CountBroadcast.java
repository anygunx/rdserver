package com.rd.bean.chat;

import com.rd.common.ChatService;
import com.rd.define.EBroadcast;
import com.rd.net.message.Message;

/**
 * 按次数广播
 *
 * @author Created by U-Demon on 2016年11月8日 下午7:41:31
 * @version 1.0.0
 */
public class CountBroadcast extends BaseBroadcast {

    //广播多少次
    private int count;

    protected CountBroadcast(Message msg, int count) {
        super(msg);
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    @Override
    public boolean isNeedBroadcast(long currTime) {
        return count > 0;
    }

    @Override
    public void countDown(long currTime) {
        count--;
    }

    @Override
    public boolean isDone(long currTime) {
        return count <= 0;
    }

    public static final CountBroadcast build(String content, int count) {
        Message message = ChatService.createBroadcastMsg(null, EBroadcast.SYSTEM, content);
        return new CountBroadcast(message, count);
    }

}
