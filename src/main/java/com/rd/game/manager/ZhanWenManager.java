package com.rd.game.manager;

import com.rd.bean.drop.DropData;
import com.rd.bean.goods.ZhanWen;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.define.ErrorDefine;
import com.rd.game.GameRole;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.GameEvent;
import com.rd.model.ZhanWenModel;
import com.rd.model.data.ZhanWenModelData;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;

import java.util.EnumSet;
import java.util.Map;

/**
 * 战纹管理器
 *
 * @author lwq
 */
public class ZhanWenManager {

    private GameRole role;

    public ZhanWenManager(GameRole role) {
        this.role = role;
    }

    /**
     * 装备战纹
     */
    public void processZhanWenActive(Message request) {
        /** 角色索引 **/
        byte idx = request.readByte();
        /** 战纹的位置 **/
        byte pos = request.readByte();
        /** 要装备战纹的id **/
        int id = request.readInt();

        if (role.getPlayer().getRein() == 0 || (role.getPlayer().getRein() == 1 && pos > 1)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_ZHANWEN_NO_ACTIVE);
            return;
        } else if (role.getPlayer().getRein() != 1 && role.getPlayer().getRein() + 1 < pos) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_ZHANWEN_NO_ACTIVE);
            return;
        }

        Character ch = role.getPlayer().getCharacter(idx);
        if (ch == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        //要装备的战纹
        ZhanWen zhanWen = role.getPlayer().getZhanWens().get(id);

        if (zhanWen == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }

        EnumSet<EPlayerSaveType> saves = EnumSet.of(EPlayerSaveType.CHA_ZHANWEN);
        //已装备的战纹
//		ZhanWen active = ch.getZhanWen().get(pos);
//		if (active != null) {
//			//卸下原来位置的战纹,放回到战纹背包
//			ch.getZhanWen().put(pos, null);
//			DropData dropData = new DropData(EGoodsType.ZHANWEN, active.getG(), 1);
//			role.getPackManager().addGoods(dropData, EGoodsChangeType.ZHANWEN_EQUIP_ADD, saves);
//		}
//		
//		//从战纹背包中扣除要装备的战纹
//		DropData dropData = new DropData(EGoodsType.ZHANWEN,id, 1);
//		if (!role.getPackManager().useGoods(dropData, EGoodsChangeType.ZHANWEN_EQUIP_CONSUME, saves)) {
//			role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//			return;
//		}
//		
//		//装上战纹
//		ch.getZhanWen().put(pos, zhanWen);

        //更新角色属性
        role.getEventManager().notifyEvent(new GameEvent(EGameEventType.ZHANWEN_ACTIVE, 1, saves));

        //发送消息
        Message msg = new Message(MessageCommand.ZHANWEN_ACTIVE_MESSAGE, request.getChannel());
        msg.setByte(idx);
        msg.setByte(pos);
        msg.setShort(zhanWen.getG());
        role.sendMessage(msg);

        //保存数据
        role.saveData(idx, saves);
    }

    /**
     * 战纹升级
     */
    public void processZhanWenUpGrade(Message request) {

//		/** 角色索引 **/
//		byte idx = request.readByte();
//		/** 战纹的位置 **/
//		byte pos = request.readByte();
//		
//		Character ch = role.getPlayer().getCharacter(idx);
//		
//		if (ch == null) {
//			role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
//			return;
//		} 
//		
//		ZhanWen zhanWen = ch.getZhanWen().get(pos);
//		
//		if (zhanWen == null) {
//			role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
//			return;
//		}
//		//当前战纹数据
//		ZhanWenModelData modelData = ZhanWenModel.getZhanWenModelData(zhanWen.getG());
//		//下一级战纹数据
//		ZhanWenModelData nextModelData = ZhanWenModel.getZhanWenByTQL(modelData.getType(),modelData.getPinzhi(),(byte) (modelData.getLv()+1));
//		
//		if (nextModelData == null) {
//			role.sendErrorTipMessage(request, ErrorDefine.ERROR_ZHANWEN_LV_MAX);
//			return;
//		}
//		
//		EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
//		//升级战纹消耗战纹精华
//		if (!role.getPackManager().useGoods(nextModelData.getCost(),EGoodsChangeType.ZHANWEN_UPGRADE_CONSUME, saves)) {
//			role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//			return;
//		}
//		
//		zhanWen.setG(nextModelData.getId());
//		
//		//更新角色属性
//		role.getEventManager().notifyEvent(new GameEvent(EGameEventType.ZHANWEN_UP,1, saves));
//		
//		Message msg = new Message(MessageCommand.ZHANWEN_UPGRADE_MESSAGE, request.getChannel());
//		msg.setByte(idx);
//		msg.setByte(pos);
//		msg.setShort(zhanWen.getG());
//		role.sendMessage(msg);
//		//保存数据
//		saves.add(EPlayerSaveType.CHA_ZHANWEN);
//		role.saveData(idx, saves);
    }

    /**
     * 战纹分解
     */
    public void processZhanWenRes(Message request) {

        short size = request.readShort();

        if (size <= 0) {
            return;
        }
        //战纹背包里的所有战纹
        Map<Integer, ZhanWen> zhanWenMap = role.getPlayer().getZhanWens();
        //战纹精华
        int num = 0;
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        ZhanWenModelData modelData = null;
        for (int i = 0; i < size; i++) {
            int id = request.readInt();
            ZhanWen zhanWen = zhanWenMap.get(id);
            if (zhanWen == null) {
                continue;
            }
            modelData = ZhanWenModel.getZhanWenModelData(zhanWen.getG());
            if (modelData == null) {
                continue;
            }

            DropData dropData = new DropData(EGoodsType.ZHANWEN, id, 1);

            if (role.getPackManager().useGoods(dropData, EGoodsChangeType.ZHANWEN_RES_CONSUME, saves)) {
                num += modelData.getFenjie().getN();
            }

        }
        //分解战纹获取到的战纹精华
        if (modelData != null) {
            DropData addData = new DropData(modelData.getFenjie().getT(), modelData.getFenjie().getG(), num);
            role.getPackManager().addGoods(addData, EGoodsChangeType.ZHANWEN_RES_ADD, saves);
            role.savePlayer(saves);
        }
        role.sendTick(request);
    }
}
