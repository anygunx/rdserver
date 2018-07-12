package com.rd.game.manager;

import com.rd.bean.drop.DropData;
import com.rd.bean.player.Player;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.define.ErrorDefine;
import com.rd.define.GameDefine;
import com.rd.define.SkillDefine;
import com.rd.game.GameRole;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.GameEvent;
import com.rd.game.event.IEventListener;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SkillManager implements IEventListener {

    private GameRole gameRole;
    private Player player;

    public SkillManager(GameRole role) {
        this.gameRole = role;
        this.player = role.getPlayer();
    }

    /**
     * 升级主动技
     *
     * @param request
     */
    public void processUpSkill(Message request) {
        byte idx = request.readByte();
        Character character = player.getCharacter(idx);
        if (character == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        //技能索引
        byte skillIdx = request.readByte();
        if (skillIdx >= SkillDefine.SKILL_NUM) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        short result = this.skillUp(character, skillIdx, enumSet, true);
        if (ErrorDefine.ERROR_NONE == result) {
            short skillLevel = 1;
            //通知技能升级消息
            gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.SKILL_LEVEL_UP, skillLevel, enumSet));

            Message message = this.getSkillUpMessage(idx, skillIdx, skillLevel);
            message.setChannel(request.getChannel());
            gameRole.sendMessage(message);

            enumSet.add(EPlayerSaveType.CHA_SKILL);
            gameRole.saveData(idx, enumSet);
        } else {
            gameRole.sendErrorTipMessage(request, result);
        }
    }

    private Message getSkillUpMessage(byte idx, byte skillIdx, short skillLevel) {
        Message message = new Message(MessageCommand.SKILL_UP_MESSAGE);
        message.setByte(idx);
        message.setByte(skillIdx);
        message.setShort(skillLevel);
        return message;
    }

    private Short skillUp(Character character, byte skillIdx, EnumSet<EPlayerSaveType> enumSet, boolean isNotifyClient) {
//		short skillLevel=character.getSkillSystem().getSkillList().get(skillIdx).getLevel();
//		if (skillLevel >= LevelDefine.MAX_SKILL_LEVEL)
//		{
//			return ErrorDefine.ERROR_MAX_LEVEL;
//		}else if(skillLevel < 1 || skillLevel>=this.getCanUpSkillLevel()){
//			return ErrorDefine.ERROR_LEVEL_LESS;
//		}
//		//消耗金币
//		int needGold = 5000 + 500*(skillLevel-1);
//		DropData cost = new DropData(EGoodsType.GOLD, 0, needGold);
//		if (!gameRole.getPackManager().useGoods(cost, EGoodsChangeType.SKILL_UP_CONSUME,enumSet,isNotifyClient))
//		{
//			return ErrorDefine.ERROR_GOODS_LESS;
//		}
//		//升级技能
//		++skillLevel;
//		character.getSkillSystem().getSkillList().get(skillIdx).setLevel(skillLevel);
        return ErrorDefine.ERROR_NONE;
    }

    /**
     * 一键升级技能
     *
     * @param request
     */
    public void processUpSkillAuto(Message request) {
        byte idx = request.readByte();
        Character character = player.getCharacter(idx);
        if (character == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        short errorCode = ErrorDefine.ERROR_NONE;
        short count = 0;
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        int result = 0;
        while (result < SkillDefine.SKILL_NUM) {
            result = 0;
            for (byte i = 0; i < SkillDefine.SKILL_NUM; ++i) {
                errorCode = this.skillUp(character, i, enumSet, false);
                if (ErrorDefine.ERROR_NONE != errorCode) {
                    ++result;
                }
            }
            ++count;
        }
        if (count < 2) {
            gameRole.sendErrorTipMessage(request, errorCode);
            return;
        }

//		short skillLevel=character.getSkillSystem().getSkillList().get(0).getLevel();
//		//通知技能升级消息
//		gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.SKILL_LEVEL_UP,skillLevel,enumSet));
//		
//		Message message=new Message(MessageCommand.SKILL_UP_AUTO_MESSAGE,request.getChannel());
//		message.setByte(idx);
//		for(Skill skill:character.getSkillSystem().getSkillList()){
//			message.setShort(skill.getLevel());
//		}
//		gameRole.sendUpdateCurrencyMsg(EGoodsType.GOLD, EGoodsChangeType.SKILL_UP_CONSUME);
//		gameRole.sendMessage(message);
//		
//		enumSet.add(EPlayerSaveType.CHA_SKILL);
//		gameRole.saveData(idx, enumSet);
    }

    public void handleEvent(GameEvent event) {
//		if(event.getType()==EGameEventType.PLAYER_REACH_LEVEL){
//			for(int idx=0;idx<player.getCharacterList().size();++idx){
//				Character character=player.getCharacterList().get(idx);
//				boolean isSkillOpen=false;
//				for(int i=0;i<SkillDefine.SKILL_NUM;++i){
//					if(character.getSkillSystem().getSkillList().get(i).getLevel()==0 && SkillDefine.SKILL_OPEN_LEVEL[i]<=player.getLevel()){
//						character.getSkillSystem().getSkillList().get(i).setLevel((short)1);
//						Message message=this.getSkillUpMessage(character.getIdx(), (byte)i, (short)1);
//						gameRole.putMessageQueue(message);
//						isSkillOpen=true;
//					}
//				}
//				if(isSkillOpen){
//					gameRole.savePlayer(idx, EnumSet.of(EPlayerSaveType.CHA_SKILL));
//				}
//			}
//		}
    }

    private int getCanUpSkillLevel() {
        if (player.getLevel() < GameDefine.REIN_LV) {
            return player.getLevel();
        }
        return GameDefine.REIN_LV + player.getRein() * 10;
    }


    /**
     * 860 得到心法数据
     *
     * @param message
     */
    public Message getHeartSkillMessage() {
        Message message = new Message(MessageCommand.HEART_SKILL_DATA_MESSAGE);
//		message.setByte(this.player.getCharacterList().size());
//		for(Character c:this.player.getCharacterList()){
//			message.setByte(c.getHeartSkillSlotList().size());
//			//心法装备
//			for(HeartSkillSlot slot:c.getHeartSkillSlotList()){
//				slot.getMessage(message);
//			}
//		}
        return message;
    }

    /**
     * 861 心法学习
     *
     * @param message
     */
    public void processHeartSkillLearn(Message request) {
//		byte idx=request.readByte();
//		byte pos=request.readByte();
//		byte id=request.readByte();
//		Short num=player.getHeartSkillMap().get(id);
//		if(num==null || num<1){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//			return;
//		}
//		if(player.getRein()<SkillDefine.HEART_SKILL_SLOT_OPEN_REIN[pos] && player.getVipLevel()<SkillDefine.HEART_SKILL_SLOT_OPEN_VIP[pos]){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		Character character=player.getCharacterList().get(idx);
//		if(character==null){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		if(character.getHeartSkillSlotList().size()<=pos){
//			for(;pos>=character.getHeartSkillSlotList().size();){
//				character.getHeartSkillSlotList().add(new HeartSkillSlot());
//			}
//		}
//		HeartSkillSlot slot=character.getHeartSkillSlotList().get(pos);
//		if(slot.getId()>0 || slot.getLevel()>0){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		
//		slot.setId(id);
//		slot.setLevel((byte)1);
//		
//		EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
//		EGoodsType.HEART_SKILL.getCmd().consume(gameRole, new DropData(EGoodsType.HEART_SKILL,id,1), EGoodsChangeType.HEART_SKILL_LEARN, saves);
//
//		Message message=new Message(MessageCommand.HEART_SKILL_LEARN_MESSAGE,request.getChannel());
//		message.setByte(idx);
//		message.setByte(pos);
//		message.setByte(id);
//		gameRole.sendMessage(message);
//		
//		saves.add(EPlayerSaveType.CHA_HEART_SKILL_SLOT);
//		saves.add(EPlayerSaveType.HEART_SKILL);
//		gameRole.saveData(idx,saves);
    }

    /**
     * 863 心法升级
     *
     * @param message
     */
    public void processHeartSkillUp(Message request) {
//		byte idx=request.readByte();
//		byte pos=request.readByte();
//		Character character=player.getCharacterList().get(idx);
//		if(character==null){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		if(character.getHeartSkillSlotList().size()<pos){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		HeartSkillSlot slot=character.getHeartSkillSlotList().get(pos);
//		if(slot.getId()<1 || slot.getLevel()<1){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		HeartSkillData data=null;//SkillModel.getHeardSkillData(slot.getId(), slot.getLevel());
//		if(data==null){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		if(data.getUpCost()==null){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_MAX_LEVEL);
//			return;
//		}
//		
//		EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
//		if(!gameRole.getPackManager().useGoods(data.getUpCost(), EGoodsChangeType.HEART_SKILL_UP_CONSUME, saves)){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//			return;
//		}
//		
//		slot.addLevel();
//		
//		Message message=new Message(MessageCommand.HEART_SKILL_UP_MESSAGE,request.getChannel());
//		message.setByte(idx);
//		message.setByte(pos);
//		message.setByte(slot.getLevel());
//		gameRole.sendMessage(message);
//		
//		saves.add(EPlayerSaveType.CHA_HEART_SKILL_SLOT);
//		gameRole.savePlayer(idx,saves);
    }

    /**
     * 864 心法拆卸
     *
     * @param message
     */
    public void processHeartSkillRm(Message request) {
//		byte idx=request.readByte();
//		byte pos=request.readByte();
//		Character character=player.getCharacterList().get(idx);
//		if(character==null){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		if(character.getHeartSkillSlotList().size()<pos){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		HeartSkillSlot slot=character.getHeartSkillSlotList().get(pos);
//		if(slot.getId()<1 || slot.getLevel()<1){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		HeartSkillData data=null;//SkillModel.getHeardSkillData(slot.getId(), slot.getLevel());
//		if(data==null){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		
//		EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
//		if(!gameRole.getPackManager().useGoods(new DropData(EGoodsType.DIAMOND,0,500), EGoodsChangeType.HEART_SKILL_RM_CONSUME, saves)){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//			return;
//		}
//		
//		//Short num=player.getHeartSkillMap().get(slot.getId());
//		//if(num==null){
//		//	player.getHeartSkillMap().put(slot.getId(), (short)1);
//		//}else{
//		//	player.getHeartSkillMap().put(slot.getId(), ++num);
//		//}
//		
//		//for(byte i=1;i<slot.getLevel();++i){
//		//	data=SkillModel.getHeardSkillData(slot.getId(), i);
//		//	if(data!=null){
//				gameRole.getPackManager().addGoods(data.getRmCost(), EGoodsChangeType.HEART_SKILL_RM_ADD, saves);
//		//	}
//		//}
//		slot.reset();
//		
//		Message message=new Message(MessageCommand.HEART_SKILL_RM_MESSAGE,request.getChannel());
//		message.setByte(idx);
//		message.setByte(pos);
//		gameRole.sendMessage(message);
//		
//		saves.add(EPlayerSaveType.CHA_HEART_SKILL_SLOT);
//		saves.add(EPlayerSaveType.HEART_SKILL);
//		gameRole.saveData(idx,saves);
    }

    /**
     * 865 心法合成
     *
     * @param message
     */
    public void processHeartSkillCombine(Message request) {
        byte id1 = request.readByte();
        byte id2 = request.readByte();
        byte id3 = request.readByte();

        Map<Byte, Byte> idMap = new HashMap<Byte, Byte>();
        Byte num = idMap.get(id1);
        if (num == null) {
            idMap.put(id1, (byte) 1);
        } else {
            ++num;
            idMap.put(id1, num);
        }
        num = idMap.get(id2);
        if (num == null) {
            idMap.put(id2, (byte) 1);
        } else {
            ++num;
            idMap.put(id2, num);
        }
        num = idMap.get(id3);
        if (num == null) {
            idMap.put(id3, (byte) 1);
        } else {
            ++num;
            idMap.put(id3, num);
        }

        for (Entry<Byte, Byte> entry : idMap.entrySet()) {
            Short bagNum = player.getHeartSkillMap().get(entry.getKey());
            if (bagNum == null || bagNum < entry.getValue()) {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
                return;
            }
        }

        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        for (Entry<Byte, Byte> entry : idMap.entrySet()) {
            EGoodsType.HEART_SKILL.getCmd().consume(gameRole, new DropData(EGoodsType.HEART_SKILL, entry.getKey(), entry.getValue()), EGoodsChangeType.HEART_SKILL_COMBINE_CONSUME, saves);
        }

        byte combineId = 1;
        EGoodsType.HEART_SKILL.getCmd().reward(gameRole, new DropData(EGoodsType.HEART_SKILL, combineId, 1), EGoodsChangeType.HEART_SKILL_COMBINE_ADD, saves);

        Message message = new Message(MessageCommand.HEART_SKILL_COMBINE_MESSAGE, request.getChannel());
        message.setByte(combineId);
        gameRole.sendMessage(message);

        saves.add(EPlayerSaveType.HEART_SKILL);
        gameRole.savePlayer(saves);
    }
}
