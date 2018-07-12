package com.rd.game.manager;

import com.rd.bean.player.Player;
import com.rd.game.GameRole;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 称号管理器
 *
 * @author U-Demon Created on 2017年3月31日 下午4:02:34
 * @version 1.0.0
 */
public class TitleManager {

    private GameRole role;

    private Player player;

    public TitleManager(GameRole role) {
        this.role = role;
        this.player = role.getPlayer();
    }

    public void init() {

    }

    /**
     * 称号信息
     *
     * @return
     */
    public Message getTitleInfoMsg(int id) {
        short titleId = (short) id;
        long curr = System.currentTimeMillis();
        Map<Short, Integer> left = new HashMap<>();
        //所有称号信息
        if (titleId == -1) {
            for (Entry<Short, Long> entry : player.getTitle().entrySet()) {
                //永久
                if (entry.getValue() == -1) {
                    left.put(entry.getKey(), -1);
                }
                //还在生效的
                else if (entry.getValue() > curr) {
                    left.put(entry.getKey(), (int) ((entry.getValue() - curr) / 1000));
                }
            }
        }
        //某一称号
        else {
            Long endTime = player.getTitle().get(titleId);
            if (endTime == null) {
                left.put(titleId, 0);
            } else if (endTime == -1) {
                left.put(titleId, -1);
            } else if (endTime <= curr) {
                left.put(titleId, 0);
            } else {
                left.put(titleId, (int) ((endTime - curr) / 1000));
            }
            //role.getEventManager().notifyEvent(new GameEvent(EGameEventType.NEW_TITLE, 1,null));
        }
        Message msg = new Message(MessageCommand.TITLE_INFO_MESSAGE);
        msg.setByte(left.size());
        for (Entry<Short, Integer> entry : left.entrySet()) {
            msg.setShort(entry.getKey());
            msg.setInt(entry.getValue());
        }
        return msg;
    }

    /**
     * 佩戴称号
     *
     * @param request
     */
    public void processTitleAdorn(Message request) {
//		//角色
//		byte idx = request.readByte();
//		//称号ID
//		short id = request.readShort();
//		Character cha = player.getCharacter(idx);
//		if (cha == null) {
//			role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		//卸下称号
//		if (id == 0) {
//			cha.setTitle(id);
//		}
//		//佩戴称号
//		else {			
//			//称号已失效
//			Long endTime = player.getTitle().get(id);
//			if (endTime == null) {
//				role.sendErrorTipMessage(request, ErrorDefine.ERROR_TITLE_NO_ACTIVE);
//				return;
//			}
//			long curr = System.currentTimeMillis();
//			if (endTime != -1 && endTime <= curr) {
//				role.sendErrorTipMessage(request, ErrorDefine.ERROR_TITLE_NO_ACTIVE);
//				return;
//			}
//			//其他角色已佩戴
//			for (int i = 0; i < GameDefine.OCCUPATION_NUM; i++) {
//				Character c = player.getCharacter(i);
//				if (c != null && c.getTitle() == id) {
//					c.setTitle((short)0);
//					break;
//				}
//			}
//			cha.setTitle(id);
//		}
//		//消息
//		Message msg = new Message(MessageCommand.TITLE_ADORN_MESSAGE, request.getChannel());
//		msg.setByte(idx);
//		msg.setShort(id);
//		role.sendMessage(msg);
//		//保存数据
//		role.saveData(idx, EnumSet.of(EPlayerSaveType.CHA_TITLE));
    }

}
