package com.rd.game.event;

/**
 * 对任务参数的影响类型
 * Created by XingYun on 2016/5/25.
 */
public enum EMissionUpdateType {
    NONE,            //(0),
    /**
     * 作为增量 任务中计数
     **/
    INCREMENT,        //(1),
    // FIXME 以下这些换成条件检查 不用事件触发
    /**
     * 当前进度与其他系统中计数中的较大值
     **/
    BIGGER,            //(2),
    /**
     * 当前进度与其他系统中计数和的较大值
     **/
    BIGGER_TOTAL,   //(3),
    /**
     * 接收到该事件算一次
     **/
    ONECE,            //(4),
    ;

    EMissionUpdateType() {

    }

    public static EMissionUpdateType getEventType(int id) {
        return values()[id];
    }
}
