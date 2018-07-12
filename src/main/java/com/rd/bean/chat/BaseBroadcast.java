package com.rd.bean.chat;

import com.rd.net.message.Message;

/**
 * 系统广播基类
 *
 * @author Created by U-Demon on 2016年11月8日 下午7:36:38
 * @version 1.0.0
 */
public abstract class BaseBroadcast {

    //广播的消息
    protected final Message msg;

    //脏数据标识，需要countDown
    protected boolean dirty;

    protected BaseBroadcast(Message msg) {
        this.msg = msg;
    }

    /**
     * 是否可以广播
     *
     * @param currTime
     * @return
     */
    public abstract boolean isNeedBroadcast(long currTime);

    /**
     * 广播计数减少
     *
     * @param currTime
     */
    public abstract void countDown(long currTime);

    /**
     * 广播是否结束
     *
     * @param currTime
     * @return
     */
    public abstract boolean isDone(long currTime);

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public Message getMsg() {
        return msg;
    }

}
