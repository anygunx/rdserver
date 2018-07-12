package com.rd.activitynew.info;

import com.rd.game.GameRole;
import com.rd.net.message.Message;

/**
 * 活动信息
 *
 * @author ---
 * @version 1.0
 * @date 2018年3月3日下午1:34:34
 */
public interface IActivityInfo {

    void getMessage(Message message, byte groupId, GameRole role);
}
