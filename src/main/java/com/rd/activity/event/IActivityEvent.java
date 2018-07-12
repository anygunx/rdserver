package com.rd.activity.event;

import com.rd.game.GameRole;
import com.rd.net.message.Message;

/**
 * 活动开始和结束的事件
 *
 * @author Created by U-Demon on 2016年11月3日 下午8:13:25
 * @version 1.0.0
 */
public interface IActivityEvent {

    boolean onStart();

    boolean onEnd();

    void getMessage(Message msg, GameRole role);

    boolean isOpen(GameRole role);

}
