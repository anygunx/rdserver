package com.rd.game.manager;

import com.rd.bean.drop.DropData;
import com.rd.bean.function.FunctionData;
import com.rd.bean.goods.Equip;
import com.rd.bean.goods.data.EquipData;
import com.rd.bean.player.Player;
import com.rd.bean.player.TownSoulTreasure;
import com.rd.common.BroadcastService;
import com.rd.common.ChatService;
import com.rd.common.GameCommon;
import com.rd.common.goods.CombineRuneCmd;
import com.rd.common.goods.CombineRunePieceCmd;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.*;
import com.rd.game.GameRankManager;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.game.data.TownSoulTurntableRecord;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.GameEvent;
import com.rd.model.EquipModel;
import com.rd.model.GoodsModel;
import com.rd.model.OrangeModel;
import com.rd.model.ShenQiModel;
import com.rd.model.data.*;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;
import com.rd.util.GameUtil;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.Map.Entry;

/**
 * 装备管理器
 *
 * @author Created by U-Demon on 2016年10月31日 下午5:30:16
 * @version 1.0.0
 */
public class EquipManager {

    private static final Logger logger = Logger.getLogger(EquipManager.class);

    private GameRole gameRole;
    private Player player;

    public EquipManager(GameRole role) {
        this.gameRole = role;
        this.player = role.getPlayer();
    }

    public void init() {
    }

    public void processWearEquip(Message request) {
        byte idx = request.readByte();
        Character character = player.getCharacter(idx);
        if (character == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        byte size = request.readByte();
        if (size < 1) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        for (int i = 0; i < size; ++i) {
            byte pos = request.readByte();
            short equipId = request.readShort();
            equipWear(character, pos, equipId, enumSet);
        }
        //通知穿装备消息
        gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.WEAR_EQUIPMENT, player.getMasterEquipedNumber(), enumSet));
        gameRole.sendTick(request);

        gameRole.saveData(idx, enumSet);
    }

    /**
     * 穿装备
     *
     * @param equipId
     */
    private void equipWear(Character character, byte pos, short equipId, EnumSet<EPlayerSaveType> enumSet) {
//		Equip equip = gameRole.getPackManager().getEquipById(equipId);
//		if (equip != null)
//		{
//			short lvTakeOn = 0;
//			byte occTakeOn = -1;
//			if (equip.getQ() == EGoodsQuality.RED.getValue()) {
//				RedModelData model = OrangeModel.getRed(equip.getG());
//				if(model.getType()!=EquipDefine.getEquipType(pos)){
//					return;
//				}
//				lvTakeOn = model.getLv();		
//			} else {
//				EquipData equipData = GoodsModel.getEquipDataById(equip.getG());
//				if(equipData.getPosition()!=EquipDefine.getEquipType(pos)){
//					return;
//				}
//				lvTakeOn = equipData.getLevel();
//				occTakeOn = equipData.getOccupation();
//			}
//			if((occTakeOn == -1 || occTakeOn==character.getOccupation()) && player.lvValidate(lvTakeOn)){
//				Equip roleEquip=character.getEquipList().get(pos);
//				if (roleEquip != null)
//				{
//					//脱装备
//					gameRole.getPackManager().addEquip(roleEquip, EGoodsChangeType.TAKEOFF_ADD);
//				}
//				
//				//穿装备
//				DropData data = new DropData(EGoodsType.EQUIP, equip.getD(), 1);
//				gameRole.getPackManager().useGoods(data, EGoodsChangeType.TAKEOFF_CONSUME,enumSet);
//				character.getEquipList().set(pos, equip);
//				
//				Message message=new Message(MessageCommand.GOODS_WEAR_EQUIP_MESSAGE);
//				message.setByte(character.getIdx());
//				message.setByte(pos);
//				equip.getMessage(message);
//				gameRole.putMessageQueue(message);
//				
//				enumSet.add(EPlayerSaveType.CHA_EQUIP);
//			}
//		}
    }

    /**
     * 305 装备灵器
     *
     * @param request
     */
    public void processArtifactEquip(Message request) {
//		short id=request.readShort();
//		Artifact artifact=gameRole.getPackManager().getArtifactById(id);
//		if(artifact!=null){
//			ArtifactData data=GoodsModel.getArtifactDataById(artifact.getG());
//			short blessLevel=0;
//			if(EBlessType.HORSE.ordinal()==data.getType()){
//				blessLevel=player.getHorseLevel();
//			}else if(EBlessType.WEAPON.ordinal()==data.getType()){
//				blessLevel=player.getWeaponLevel();
//			}else if(EBlessType.CLOTHES.ordinal()==data.getType()){
//				blessLevel=player.getClothesLevel();
//			}else if(EBlessType.WING.ordinal()==data.getType()){
//				blessLevel=player.getWingLevel();
//			}else if(EBlessType.MAGIC.ordinal()==data.getType()){
//				blessLevel=player.getMagicLevel();
//			}
//			if(data.getLevel()<=blessLevel){
//				EnumSet<EPlayerSaveType> enumSet=EnumSet.noneOf(EPlayerSaveType.class);
//				
//				int index=data.getType()*3+data.getPosition();
//				
//				Artifact artifactEquip=player.getBlessArtifactList().get(index);
//				if(artifactEquip!=null){
//					//脱装备
//					gameRole.getPackManager().addArtifact(artifactEquip, EGoodsChangeType.TAKEOFF_ADD);
//				}
//				
//				//穿装备
//				DropData dropData = new DropData(EGoodsType.ARTIFACT, artifact.getD(), 1);
//				gameRole.getPackManager().useGoods(dropData, EGoodsChangeType.TAKEOFF_CONSUME,enumSet);
//				player.getBlessArtifactList().set(index, artifact);
//				
//				//通知装备灵器消息
//				gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.WEAR_ARTIFACT,1,enumSet));
//				
//				Message message=new Message(MessageCommand.GOODS_ARTIFACT_EQUIP_MESSAGE,request.getChannel());
//				artifact.getMessage(message);
//				gameRole.sendMessage(message);
//				
//				enumSet.add(EPlayerSaveType.BLESSARTIFACT);
//				gameRole.savePlayer(enumSet);
//			}else{
//				gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_BLESS_LEVEL_LESS);
//			}
//		}else{
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//		}
    }

    /**
     * 获取注灵等级最小的索引
     *
     * @return
     */
    private int getZhuLingMinIndex(Character cha) {
//		List<EquipSlot> slots = cha.getEquipSlotList();
//		for (int i = 0; i < slots.size() - 1; i++)
//		{
//			if (slots.get(i) == null || slots.get(i).getZl() == 0)
//				return i;
//			if (slots.get(i+1) == null)
//				return i+1;
//			if (slots.get(i).getZl() > slots.get(i+1).getZl())
//				return i+1;
//		}
        return 0;
    }

    private byte getStrMinIndex(Character cha) {
//		List<EquipSlot> slots = cha.getEquipSlotList();
//		for (int i = 0; i < slots.size() - 1; i++)
//		{
//			if (slots.get(i) == null || slots.get(i).getStr() == 0)
//				return (byte)i;
//			if (slots.get(i+1) == null)
//				return (byte)(i+1);
//			if (slots.get(i).getStr() > slots.get(i+1).getStr())
//				return (byte)(i+1);
//		}
        return 0;
    }

    private byte getJewelMinIndex(Character cha) {
//		List<EquipSlot> slots = cha.getEquipSlotList();
//		for (int i = 0; i < slots.size() - 1; i++)
//		{
//			if (slots.get(i) == null || slots.get(i).getJ() == 0)
//				return (byte)i;
//			if (slots.get(i+1) == null)
//				return (byte)(i+1);
//			if (slots.get(i).getJ() > slots.get(i+1).getJ())
//				return (byte)(i+1);
//		}
        return 0;
    }

    private int getGongMinIndex(Character cha) {
//		List<Short> gongs = cha.getGong();
//		if (gongs.size() < 5)
//			return gongs.size();
//		for (int i = 0; i < gongs.size() - 1; i++) {
//			if (gongs.get(i) > gongs.get(i+1))
//				return i+1;
//		}
        return 0;
    }

    /**
     * 装备注灵
     */
    public void processZhuLing(Message request) {
//		byte idx = request.readByte();
//		Character cha = player.getCharacter(idx);
//		//计算本次注灵的索引
//		int pos = getZhuLingMinIndex(cha);
//		//装备类型
//		if(pos < 0 || pos >= EquipDefine.EQUIP_POS_NUM)
//		{
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_EQUIP_EMPTY);
//			return;
//		}
//		//装备槽
//		EquipSlot slot = cha.getEquipSlotList().get(pos);
//		if (slot == null)
//		{
//			slot = new EquipSlot();
//			slot.setZl(0);
//			cha.getEquipSlotList().set(pos, slot);
//		}
//		//下一级
//		ZhuLingModelData next = EquipModel.getZhuLingData(slot.getZl()+1, EquipDefine.getEquipType(pos));
//		if (next == null)
//		{
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_EQUIP_RUNHUN_MAX);
//			return;
//		}
//		EnumSet<EPlayerSaveType> enumSet=EnumSet.of(EPlayerSaveType.CHA_EQUIPSLOT);
//		//物品不足
//		if (!gameRole.getPackManager().useGoods(next.getCost(), EGoodsChangeType.ZHULING_CONSUME,enumSet))
//		{
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//			return;
//		}
//		slot.addZl(1);
//		//通知注灵消息
//		//gameRole.getEventManager().notifyEvent(EGameEventType.EQUIP_FILL_SOUL.create(gameRole, 1,enumSet));
//		//发送消息
//		Message message = new Message(MessageCommand.EQUIP_ZHULING_MESSAGE, request.getChannel());
//		message.setByte(idx);
//		message.setByte(pos);
//		message.setInt(slot.getZl());
//		gameRole.sendMessage(message);
//		//保存数据
//		gameRole.getEventManager().notifyEvent(EGameEventType.EQUIP_FILL_SOUL.create(gameRole, 1, enumSet));
//		gameRole.saveData(idx, enumSet);
//		int minLv = -1;
//		for (EquipSlot es : cha.getEquipSlotList())
//		{
//			if (es == null)
//			{
//				minLv = 0;
//				break;
//			}
//			if (minLv == -1)
//				minLv = es.getZl();
//			else if (es.getZl() < minLv)
//				minLv = es.getZl();
//		}
    }

    /**
     * 装备强化
     */
    public void processStrength(Message request) {
//		byte idx = request.readByte();
//		//装备类型
//		byte pos = request.readByte();
//		Character cha = player.getCharacter(idx);
//		pos = getStrMinIndex(cha);
//		//装备槽
//		EquipSlot slot = cha.getEquipSlotList().get(pos);
//		if (slot == null)
//		{
//			slot = new EquipSlot();
//			slot.setStr(0);
//			cha.getEquipSlotList().set(pos, slot);
//		}
//		//下一级
//		EquipStrModelData next = EquipModel.getStrData(slot.getStr(),EquipDefine.getEquipType(pos));
//		if (next.getCost() == null)
//		{
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_EQUIP_STRENGTH_MAX);
//			return;
//		}
//		EnumSet<EPlayerSaveType> enumSet=EnumSet.of(EPlayerSaveType.CHA_EQUIPSLOT);
//		//物品不足
//		if (!gameRole.getPackManager().useGoods(next.getCost(), EGoodsChangeType.STRENGTHEN_CONSUME,enumSet))
//		{
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//			return;
//		}
//		slot.addStr(1);
//		
//		//记录玩家强化升级
//		LogUtil.log(player, new Strength(pos,slot.getStr()));
//		
//		//通知强化消息
//		gameRole.getEventManager().notifyEvent(EGameEventType.EQUIP_STRENGTHEN.create(gameRole, 1,enumSet));
//		//发送消息
//		Message message = new Message(MessageCommand.EQUIP_STRENGTH_MESSAGE, request.getChannel());
//		message.setByte(idx);
//		message.setByte(pos);
//		message.setInt(slot.getStr());
//		gameRole.sendMessage(message);
//		
//		gameRole.saveData(idx, enumSet);
//		if (slot.getStr() >= 10 && slot.getStr() % 5 == 0){
//			ChatService.broadcastPlayerMsg(player, EBroadcast.EquipStrengthen, 
//					EquipDefine.getEquipPosName(pos), String.valueOf(slot.getStr()));
//		}
//		int minLv = -1;
//		for (EquipSlot es : cha.getEquipSlotList())
//		{
//			if (es == null)
//			{
//				minLv = 0;
//				break;
//			}
//			if (minLv == -1)
//				minLv = es.getStr();
//			else if (es.getStr() < minLv)
//				minLv = es.getStr();
//		}
//		gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.EQUIP_SUIT_UPGRADE,minLv));
    }

//	/**
//	 * 坐骑装备熔炼
//	 * @param request
//	 */
//	public void processMeltingHorse(Message request)
//	{
//		short size = request.readShort();
//		if (size <= 0)
//			return;
//		//读取熔炼灵器的ID列表
//		List<Short> ids = new ArrayList<>();
//		for (int i = 0; i < size; i++)
//		{
//			ids.add(request.readShort());
//		}
//		//增加的经验值
//		int exp = 0;
//		//消耗灵器
//		List<Short> results = new ArrayList<>();
//		for (short id : ids)
//		{
//			Artifact artifact = gameRole.getPackManager().costArtifact(id);
//			if(artifact != null)
//			{
//				results.add(id);
//				ArtifactData artifactData = GoodsModel.getArtifactDataById(artifact.getG());
//				if(artifactData != null)
//				{
//					//计算经验
//					exp += artifactData.getPrice();
//				}
//			}
//		}
//		EnumSet<EPlayerSaveType> enumSet = EnumSet.of(EPlayerSaveType.ARTIFACT);
//		//增加经验值
//		addMeltExp(exp);
//		//通知熔炼消息
//		//gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.MOUNT_MELTING,1,enumSet));
//		//发送消息
//		Message message = new Message(MessageCommand.EQUIP_MELTING_HORSE_MESSAGE, request.getChannel());
//		message.setShort(player.getMeltLv());
//		message.setInt(player.getMeltExp());
//		message.setShort(0);
//		message.setShort(results.size());
//		for (int id : results)
//		{			
//			message.setShort(id);
//		}
//		gameRole.sendMessage(message);
//		//保存数据
//		enumSet.add(EPlayerSaveType.MELTLV);
//		enumSet.add(EPlayerSaveType.MELTEXP);
//		gameRole.savePlayer(enumSet);
//	}
//	
//	/**
//	 * 升级熔炼
//	 * @param addExp
//	 */
//	private void addMeltExp(int addExp)
//	{
//		player.addMeltExp(addExp);
//		int exp = player.getMeltExp();
//		EquipRlModelData next = EquipModel.getRlData(player.getMeltLv() + 1);
//		while (next != null && exp >= next.getExp())
//		{
//			//扣经验
//			exp -= next.getExp();
//			player.setMeltExp(exp);
//			//升级
//			player.addMeltLv(1);
//			next = EquipModel.getRlData(player.getMeltLv() + 1);
//		}
//		if (next == null)
//			player.setMeltExp(0);
//	}

    /**
     * 人物装备熔炼
     *
     * @param request
     */
    public void processMeltingRole(Message request) {
        short size = request.readShort();
        if (size <= 0)
            return;
        //读取熔炼装备的ID列表
        List<Short> ids = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ids.add(request.readShort());
        }
        this.meltingEquip(request, ids);
    }

    /**
     * 557 人物装备一键熔炼
     *
     * @param request
     */
    public void processMeltingOneKeyRole(Message request) {
        if (player.getVipLevel() < 1) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_VIP_LEVEL_LESS);
            return;
        }
        this.meltingEquip(request, getCanMeltingEquip());
    }


    private void meltingEquip(Message request, List<Short> ids) {
//		//增加的金币
//		int gold = 0,goodNum = 0;
//		//消耗装备
//		List<Short> results = new ArrayList<>();
//		for (short id : ids)
//		{
//			Equip me = gameRole.getPackManager().getEquipById(id);
//			if(me != null)
//			{
//				//橙装不能熔炼
//				if (me.getQ() >= EGoodsQuality.ORANGE.getValue())
//					continue;
//				gameRole.getPackManager().costEquip(id);
//				results.add(id);
//				EquipData med = GoodsModel.getEquipDataById(me.getG());
//				if (med != null)
//				{					
//					gold += this.getMeltingEquipGold(med)*EquipDefine.MELT_QUALITY_FACTOR[me.getQ()];
//					goodNum += this.getMeltingEquipStrengthenStone(med)*EquipDefine.MELT_QUALITY_FACTOR[me.getQ()];
//				}
//			}
//		}
//		EnumSet<EPlayerSaveType> enumSet = EnumSet.of(EPlayerSaveType.EQUIP);
//		//增加金币
//		gameRole.getPackManager().addGoods(new DropData(EGoodsType.GOLD, 0, gold), 
//				EGoodsChangeType.MELT_ADD, enumSet);
//		//增加强化石
//		gameRole.getPackManager().addGoods(new DropData(EGoodsType.ITEM, GoodsDefine.ITEM_ID_STRENGTHEN_STONE, goodNum), 
//				EGoodsChangeType.MELT_ADD, enumSet);
//		//通知熔炼消息
//		gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.EQUIP_MELTING,1,enumSet));
//		//发送消息
//		Message message = new Message(MessageCommand.EQUIP_MELTING_ROLE_MESSAGE, request.getChannel());
//		message.setShort(results.size());
//		for (int id : results)
//		{			
//			message.setShort(id);
//		}
//		message.setShort(0);
//		gameRole.sendMessage(message);
//		//保存数据
//		gameRole.savePlayer(enumSet);
    }

    private List<Short> getCanMeltingEquip() {
//		List<Short> list = new ArrayList<Short>();
//        List<Equip> equips = player.getEquipList();
//        
//        int[][] best = new int[GameDefine.OCCUPATION_NUM][EquipDefine.EQUIP_TYPE_NUM];
//        int[][][] keep = new int[GameDefine.OCCUPATION_NUM][EquipDefine.EQUIP_TYPE_NUM][2];
//        for(Character ch:player.getCharacterList()){
//        	for(Equip equip:ch.getEquipList()){
//        		if(equip!=null){
//	        		double qualityAddRate = EquipDefine.EQUIP_QUALITY_RATIO[equip.getQ()];
//					int[] baseAttrs = GoodsModel.getEquipBaseAttrs(equip.getQ(), equip.getG());
//					int[] addAttrs = GoodsModel.getEquipAddAttrs(equip,qualityAddRate);
//					int[] tempAttribute = new int[EAttrType.ATTR_SIZE];
//					for(int i=0;i<EAttrType.ATTR_SIZE;++i){
//						int baseValue = (int) (baseAttrs[i]*qualityAddRate);
//						int addValue = addAttrs[i];
//						tempAttribute[i]+= baseValue + addValue;
//					}
//					int fighting = GameCommon.calculationFighting(tempAttribute);
//					byte type = GoodsModel.getEquipType(equip.getG(),equip.getQ());
//					if(best[ch.getOccupation()][type]<fighting){
//						best[ch.getOccupation()][type] = fighting;
//					}
//        		}
//        	}
//        }
//        EquipData equipData;
//        for (Equip equip:equips) {
//            if (equip.getQ() > EGoodsQuality.PURPLE.getValue()){
//                continue;
//            }
//            equipData = GoodsModel.getEquipDataById(equip.getG());
//    		double qualityAddRate = EquipDefine.EQUIP_QUALITY_RATIO[equip.getQ()];
//			int[] baseAttrs = GoodsModel.getEquipBaseAttrs(equip.getQ(), equip.getG());
//			int[] addAttrs = GoodsModel.getEquipAddAttrs(equip,qualityAddRate);
//			int[] tempAttribute = new int[EAttrType.ATTR_SIZE];
//			for(int i=0;i<EAttrType.ATTR_SIZE;++i){
//				int baseValue = (int) (baseAttrs[i]*qualityAddRate);
//				int addValue = addAttrs[i];
//				tempAttribute[i]+= baseValue + addValue;
//			}
//			int fighting = GameCommon.calculationFighting(tempAttribute);
//			if(best[equipData.getOccupation()][equipData.getPosition()]>fighting){
//				list.add(equip.getD());
//			}else{
//				if(keep[equipData.getOccupation()][equipData.getPosition()][0]<fighting){
//					keep[equipData.getOccupation()][equipData.getPosition()][0] = fighting;
//					keep[equipData.getOccupation()][equipData.getPosition()][1] = equip.getD();
//				}
//				list.add(equip.getD());
//			}
//        }
//        find:for(Iterator<Short> it=list.iterator();it.hasNext();){
//        	short d = it.next();
//        	for(int i=0;i<keep.length;++i){
//        		for(int j=0;j<keep[0].length;++j){
//        			if(keep[i][j][1]==d){
//        				it.remove();
//        				continue find;
//        			}
//        		}
//        	}
//        }
//        return list;
        return null;
    }

    /**
     * 558 装备一键强化
     */
    public void processEquipStrengthOneKey(Message request) {
//		if(player.getVipLevel()<2){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_VIP_LEVEL_LESS);
//			return;
//		}
//		
//		byte idx = request.readByte();
//		Character cha = player.getCharacter(idx);
//		
//		EnumSet<EPlayerSaveType> enumSet=EnumSet.of(EPlayerSaveType.CHA_EQUIPSLOT);
//		Map<Byte,Integer> map = new HashMap<Byte,Integer>();
//		short state = ErrorDefine.ERROR_NONE;
//		byte pos;
//		int count = 0;
//		int strCount = 0;
//		boolean isStrength = true;
//		while(isStrength){
//			pos = getStrMinIndex(cha);
//			//装备槽
//			EquipSlot slot = cha.getEquipSlotList().get(pos);
//			if (slot == null)
//			{
//				slot = new EquipSlot();
//				slot.setStr(0);
//				cha.getEquipSlotList().set(pos, slot);
//			}
//			//下一级
//			EquipStrModelData next = EquipModel.getStrData(slot.getStr(),EquipDefine.getEquipType(pos));
//			if (next.getCost() == null)
//			{
//				++count;
//				if(count==EquipDefine.EQUIP_POS_NUM){
//					isStrength = false;
//					state = ErrorDefine.ERROR_EQUIP_STRENGTH_MAX;
//				}
//				continue;
//			}
//			
//			//物品不足
//			if (!gameRole.getPackManager().useGoods(next.getCost(), EGoodsChangeType.STRENGTHEN_CONSUME,enumSet))
//			{
//				if(strCount==0){
//					state = ErrorDefine.ERROR_GOODS_LESS;
//				}
//				isStrength = false;
//				continue;
//			}
//			slot.addStr(1);
//			map.put(pos, slot.getStr());
//			++strCount;
//		}
//		if(state!=ErrorDefine.ERROR_NONE){
//			gameRole.sendErrorTipMessage(request, state);
//			return;
//		}
//		
//		//通知强化消息
//		gameRole.getEventManager().notifyEvent(EGameEventType.EQUIP_STRENGTHEN.create(gameRole, 1,enumSet));
//		//发送消息
//		Message message = new Message(MessageCommand.EQUIP_STRENGTH_ONEKEY_MESSAGE, request.getChannel());
//		message.setByte(idx);
//		message.setByte(map.size());
//		for(Entry<Byte,Integer> entry:map.entrySet()){
//			message.setByte(entry.getKey());
//			message.setInt(entry.getValue());
//			
//			//记录玩家强化升级
//			LogUtil.log(player, new Strength(entry.getKey(),entry.getValue()));
//		}
//		gameRole.sendMessage(message);
//		
//		gameRole.saveData(idx, enumSet);
    }

    /**
     * 功法升级
     *
     * @param request
     */
    public void processGongFa(Message request) {
//		byte idx = request.readByte();
//		Character cha = player.getCharacter(idx);
//		if (cha == null) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
//			return;
//		}
//		//本次要升级的索引
//		int index = getGongMinIndex(cha);
//		//当前等级
//		short lv = 0;
//		if (index < cha.getGong().size()) {
//			lv = cha.getGong().get(index);
//		}
//		lv++;
//		//消耗
//		GongFaModelData model = GongFaModel.getGongFa(index, lv);
//		if (model == null) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_MAX_LEVEL);
//			return;
//		}
//		EnumSet<EPlayerSaveType> saves = EnumSet.of(EPlayerSaveType.CHA_GONG);
//		if (!gameRole.getPackManager().useGoods(model.getCost(), EGoodsChangeType.GONGFA_UPGRADE_CONSUME, saves)) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//			return;
//		}
//		//升级
//		if (index >= cha.getGong().size()) {
//			cha.getGong().add(lv);
//		} else {
//			cha.getGong().set(index, lv);
//		}
//		//gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.GONG_FA_UP, 1,saves));
//		//消息
//		Message msg = new Message(MessageCommand.GONGFA_UPGRADE_MESSAGE, request.getChannel());
//		msg.setByte(idx);
//		msg.setByte(index);
//		msg.setShort(lv);
//		gameRole.sendMessage(msg);
//		//保存
//		gameRole.saveData(idx, saves);
    }

    private int getMeltingEquipGold(EquipData data) {
        return getMeltingEquipGold(data.getLevel());
    }

    public int getMeltingEquipGold(short level) {
        if (level >= 90) {
            return (level / 10 + 1) * 200;
        }
        return level / 10 * 200 + 200;
    }

    private int getMeltingEquipStrengthenStone(EquipData data) {
        if (data.getLevel() >= 90) {
            return EquipDefine.MELT_STRENGTHEN_STONE[data.getLevel() / 10];
        }
        return EquipDefine.MELT_STRENGTHEN_STONE[data.getLevel() / 10];
    }

    /**
     * 神器列表
     *
     * @param request
     */
    public void processGodArtifactInfo(Message request) {
        Message message = new Message(MessageCommand.GOD_ARTIFACT_INFO_MESSAGE, request.getChannel());
        player.getGodArtifactMsg(message);
        gameRole.sendMessage(message);
    }

    /**
     * 神器激活
     *
     * @param request
     */
    public void processGodArtifactActive(Message request) {
        //激活的ID
        byte id = request.readByte();

        FunctionData godArtifact = player.getGodArtifactById(id);
        if (godArtifact != null && godArtifact.getLevel() > 0) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOD_ARTIFACT_ACTIVATED);
            return;
        }

		/*
        1. 所有角色攻击+5%
		2.所有角色防御+5% 
		3所有角色生命+5% 
		4 挂机金币产出+5% 
		5 挂机经验产出+5%
		6 我要干掉他
		7
		8
	*/

        //判断各个神器激活的条件
        boolean succ = false;
        switch (id) {
            case 1:
                //次日登录
                int day1 = DateUtil.getDistanceDay(player.getCreateTime(), System.currentTimeMillis());
                if (day1 >= 1)
                    succ = true;
                break;
            case 2:
                //未知
                break;
            case 3:
                //首冲
                if (gameRole.getPayManager().hasFirstPay())
                    succ = true;
                break;
            case 4:
                //V3		效果：在线离线金币增加百分之五
                if (3 <= player.getVipLevel())
                    succ = true;
                break;
            case 5:
                //V6		效果：在线离线经验增加百分之五
                if (6 <= player.getVipLevel())
                    succ = true;
                break;
            case 6:
                //七日登录		效果：在线离线金币增加百分之五 (待删除)
                int day7 = DateUtil.getDistanceDay(player.getCreateTime(), System.currentTimeMillis());
                if (day7 >= 6)
                    succ = true;
                break;
            case 7:
                //V7		效果：所有角色生命增加百分之五
                if (7 <= player.getVipLevel())
                    succ = true;
                break;
            case 8:
                //V9		效果：所有角色攻击增加百分之五
                if (9 <= player.getVipLevel())
                    succ = true;
                break;
            case 9:
                //V10		效果：所有角色防御增加百分之五
                if (10 <= player.getVipLevel())
                    succ = true;
                break;
            default:
                break;
        }
        //未达到激活条件
        if (!succ) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOD_ARTIFACT_NOT_ACTIVATE);
            return;
        }
        //激活神器
        if (godArtifact == null) {
            godArtifact = new FunctionData();
            godArtifact.setId(id);
            player.getGodArtifactList().add(godArtifact);
        }
        godArtifact.setLevel((short) 1);

        EnumSet<EPlayerSaveType> enumSet = EnumSet.of(EPlayerSaveType.GODARTIFACT);

        //gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.GOD_ARTIFACT_ACTIVE, 1,enumSet));

        Message msg = new Message(MessageCommand.GOD_ARTIFACT_ACTIVE_MESSAGE, request.getChannel());
        msg.setByte(id);
        gameRole.sendMessage(msg);

        gameRole.savePlayer(enumSet);
        //跑马灯
        ChatService.broadcastPlayerMsg(player, EBroadcast.SHENQI_JIHUO, ShenQiModel.getData(id, 1).getName());
    }

    /**
     * 神器升级
     *
     * @param request
     */
    public void processGodArtifactUpgrade(Message request) {
        //激活的ID
        byte id = request.readByte();

        //激活神器
        FunctionData godArtifact = player.getGodArtifactById(id);
        if (godArtifact == null) {
            godArtifact = new FunctionData();
            godArtifact.setId(id);
            player.getGodArtifactList().add(godArtifact);
        }
        ShenQiModelData next = ShenQiModel.getData(id, godArtifact.getLevel() + 1);
        if (next == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOD_ARTIFACT_MAX);
            return;
        }
        next = ShenQiModel.getData(id, godArtifact.getLevel());
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        //消耗物品
        if (!gameRole.getPackManager().useGoods(next.getCost(), EGoodsChangeType.SHENQI_UP_CONSUME, enumSet)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }
        godArtifact.addLevel(1);

        //gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.GOD_ARTIFACT_ACTIVE, 1,enumSet));

        //发送消息
        Message message = new Message(MessageCommand.GOD_ARTIFACT_UPGRADE_MESSAGE, request.getChannel());
        message.setByte(id);
        message.setShort(godArtifact.getLevel());
        gameRole.sendMessage(message);

        //保存数据
        enumSet.add(EPlayerSaveType.GODARTIFACT);
        gameRole.savePlayer(enumSet);

        if (godArtifact.getLevel() >= 3) {
            ChatService.broadcastPlayerMsg(player, EBroadcast.GodArtifactUp, next.getName(), String.valueOf(godArtifact.getLevel()));
        }
    }

    /**
     * 红装合成
     *
     * @param request
     */
    public void processRedMix(Message request) {
//		byte idx = request.readByte();
//		Character character=player.getCharacter(idx);
//		if(character==null){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		if (player.isEquipBagFull()) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_BAG_FULL);
//			return;
//		}
//		//装备位置
//		byte pos = request.readByte();
//		byte type = EquipDefine.getEquipType(pos);
//		//橙装模型
//		RedModelData model = null;
//		
//		//装备等级
//		int[] redLv = {110,130,150,170,180};
//		int lv = redLv[0];
//		Equip equip=character.getEquipList().get(pos);
//		if(equip!=null){
//			int fightC=0;
//			if(equip.getQ()==EGoodsQuality.RED.getValue()){
//				RedModelData redModel = OrangeModel.getRed(equip.getD());
//				fightC=GameCommon.calculationFighting(redModel.getAttr());
//				fightC+=GameCommon.calculationFighting(redModel.getAddAttr());
//			}else{
//				EquipData currEquip=GoodsModel.getEquipDataById(equip.getG());
//				fightC=GameCommon.calculationEquipFighting(currEquip.getAttr(),EquipDefine.EQUIP_QUALITY_RATIO[equip.getQ()]);
//			}
//			for(int i=0;i<redLv.length;++i){
//				lv=redLv[i];
//				RedModelData redModel = OrangeModel.getFitRed((short)lv, type);
//				int fightN=GameCommon.calculationFighting(redModel.getAttr());
//				if(fightN>fightC){
//					lv=(short)i;
//					model = redModel;
//					break;
//				}
//			}
//		}else{
//			model=OrangeModel.getFitRed((short)lv, type);
//		}
//		if(player.getLvConvert()<lv){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_LEVEL_LESS);
//			return;
//		}
//		
//		EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
//		//消耗
//		DropData cost = new DropData(EGoodsType.ITEM, GoodsDefine.ITEM_ID_RED_PIECES, model.getCost());
//		if (!gameRole.getPackManager().useGoods(cost, EGoodsChangeType.ORANGE_MIX_CONSUME, saves))
//		{
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//			return;
//		}
//		//装备
//		DropData data = new DropData(EGoodsType.EQUIP.getId(), model.getId(), 
//				EGoodsQuality.RED.getValue(), 1);
//		gameRole.getPackManager().addGoods(data, EGoodsChangeType.RED_MIX_ADD, saves);
//		Equip equipNew = getLastEquip(EGoodsQuality.RED);
//		if (equipNew == null)
//			return;
//		//换装
//		equipWear(character,pos,equipNew.getD(), saves);
//		Message msg = new Message(MessageCommand.RED_MIX_MESSAGE, request.getChannel());
//		gameRole.sendMessage(msg);
//		//通知穿装备消息
//		gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.WEAR_EQUIPMENT,1,saves));
//		gameRole.saveData(idx, saves);
    }

    /**
     * 橙装合成
     *
     * @param request
     */
    public void processOrangeMix(Message request) {
//		byte idx = request.readByte();
//		Character character=player.getCharacter(idx);
//		if(character==null){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		if (player.isEquipBagFull()) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_BAG_FULL);
//			return;
//		}
//		//装备位置
//		byte pos = request.readByte();
//		//装备等级
//		short lv = 1;
//		Equip equip=character.getEquipList().get(pos);
//		
//		if(equip!=null){
//			int fightC=0;
//			if(equip.getQ()==EGoodsQuality.RED.getValue()){
//				RedModelData model = OrangeModel.getRed(equip.getD());
//				fightC=GameCommon.calculationFighting(model.getAttr());
//				fightC+=GameCommon.calculationFighting(model.getAddAttr());
//			}else{
//				EquipData currEquip=GoodsModel.getEquipDataById(equip.getG());
//				fightC=GameCommon.calculationEquipFighting(currEquip.getAttr(),EquipDefine.EQUIP_QUALITY_RATIO[equip.getQ()]);
//			}
//			for(int i=1;i<=200;){
//				EquipData nextEquip = GoodsModel.getEquipData(i, EquipDefine.getEquipType(pos), character.getOccupation());
//				int fightN=GameCommon.calculationEquipFighting(nextEquip.getAttr(),EquipDefine.EQUIP_QUALITY_RATIO[4]);
//				if(fightN>fightC){
//					lv=(short)i;
//					break;
//				}
//				if(i==1){
//					i+=9;
//				}else{
//					i+=10;
//				}
//			}
//		}
//		if(player.getLvConvert()<lv){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_LEVEL_LESS);
//			return;
//		}
//		
//		//橙装模型
//		EquipData equipData = GoodsModel.getEquipData(lv, EquipDefine.getEquipType(pos), character.getOccupation());
//		if (equipData == null)
//		{
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_LEVEL_LESS);
//			return;
//		}
//		OrangeModelData model = OrangeModel.getOrange(lv);
//		if (model == null)
//		{
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_LEVEL_LESS);
//			return;
//		}
//		EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
//		//消耗
//		DropData cost = new DropData(EGoodsType.ITEM, GoodsDefine.ITEM_ID_ORANGE_PIECES, model.getHecheng());
//		if (!gameRole.getPackManager().useGoods(cost, EGoodsChangeType.ORANGE_MIX_CONSUME, saves))
//		{
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//			return;
//		}
//		//装备
//		DropData data = new DropData(EGoodsType.EQUIP.getId(), equipData.getGoodsId(), 
//				EGoodsQuality.ORANGE.getValue(), 1);
//		gameRole.getPackManager().addGoods(data, EGoodsChangeType.ORANGE_MIX_ADD, saves);
//		Equip equipNew = getLastEquip(EGoodsQuality.ORANGE);
//		if (equipNew == null)
//			return;
//		//换装
//		equipWear(character,pos,equipNew.getD(), saves);
//		gameRole.sendTick(request);
//		//通知穿装备消息
//		gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.WEAR_EQUIPMENT,1,saves));
//		gameRole.saveData(idx, saves);
//		//跑马灯
//		if (equipData.getLevel() >= 30) {
//			ChatService.broadcastPlayerMsg(player, EBroadcast.ORANGE_MIX, GameUtil.getLvConvertStr(equipData.getLevel()), 
//					equipData.getName());			
//		}
    }

    private Equip getLastEquip(EGoodsQuality quality) {
//		for (int i = player.getEquipList().size()-1; i >= 0; i--) {
//			Equip equip = player.getEquipList().get(i);
//			if (equip.getQ() == quality.getValue())
//				return equip;
//		}
        return null;
    }

    /**
     * 红装升级
     *
     * @param request
     */
    public void processRedUpgrade(Message request) {
        //角色
//		byte life = request.readByte();
//		//装备位置
//		byte pos = request.readByte();
//		byte type = EquipDefine.getEquipType(pos);
//		//当前装备
//		Equip equip = player.getCharacter(life).getEquipList().get(pos);
//		RedModelData curr = OrangeModel.getRed(equip.getG());
//		if (equip == null || curr == null)
//		{
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
//			return;
//		}
//		//下一级橙装模型
//		RedModelData next = OrangeModel.getFitRed(player.getLvConvert(), type);
//		if (next == null || next.getLv() == curr.getLv())
//		{
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_REIN_LESS);
//			return;
//		}
//		EnumSet<EPlayerSaveType> saves = EnumSet.of(EPlayerSaveType.CHA_EQUIP);
//		int num = next.getCost() - curr.getCost();
//		if (num < 0)
//			return;
//		//消耗
//		DropData cost = new DropData(EGoodsType.ITEM, GoodsDefine.ITEM_ID_RED_PIECES, num);
//		if (!gameRole.getPackManager().useGoods(cost, EGoodsChangeType.RED_UP_CONSUME, saves))
//		{
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//			return;
//		}
//		//装备
//		short id = equip.getD();
//		equip = EquipCommon.createRedEquip(next);
//		equip.setD(id);
//		player.getCharacter(life).getEquipList().set(pos, equip);
//		Message message = new Message(MessageCommand.GOODS_WEAR_EQUIP_MESSAGE, request.getChannel());
//		message.setByte(life);
//		message.setByte(pos);
//		equip.getMessage(message);
//		gameRole.sendMessage(message);
//		//通知穿装备消息
//		gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.WEAR_EQUIPMENT,1,saves));
//		gameRole.saveData(life, saves);
    }

    /**
     * 橙装升级
     *
     * @param request
     */
    public void processOrangeUpgrade(Message request) {
//		//角色
//		byte life = request.readByte();
//		//装备位置
//		byte pos = request.readByte();
//		//当前装备
//		Character cha = player.getCharacter(life);
//		Equip equip = cha.getEquipList().get(pos);
//		if (equip == null || equip.getQ() != EGoodsQuality.ORANGE.getValue())
//		{
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
//			return;
//		}
//		EquipData equipData = GoodsModel.getEquipDataById(equip.getG());
//		OrangeModelData curr = OrangeModel.getOrange(equipData.getLevel());
//		
//		OrangeModelData next = OrangeModel.getOrange(curr.getNext());
//		if (next == null)
//		{
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
//			return;
//		}
//		int plv = player.getLvConvert();
//		if (plv < next.getLevel()) 
//		{
//			if (plv >= GameDefine.REIN_LV)
//				gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_REIN_LESS);
//			else
//				gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_LEVEL_LESS);
//			return;
//		}
//		EquipData nextEquip = GoodsModel.getEquipData(next.getLevel(), EquipDefine.getEquipType(pos), cha.getOccupation());
//		if (nextEquip == null)
//		{
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
//			return;
//		}
//		
//		EnumSet<EPlayerSaveType> saves = EnumSet.of(EPlayerSaveType.CHA_EQUIP);
//		int num = next.getHecheng() - curr.getHecheng();
//		if (num < 0)
//			return;
//		//消耗
//		DropData cost = new DropData(EGoodsType.ITEM, GoodsDefine.ITEM_ID_ORANGE_PIECES, num);
//		if (!gameRole.getPackManager().useGoods(cost, EGoodsChangeType.ORANGE_UP_CONSUME, saves))
//		{
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//			return;
//		}
//		//装备
//		short id = equip.getD();
//		equip = EquipCommon.generateEquip(new DropData(EGoodsType.EQUIP.getId(), 
//				nextEquip.getGoodsId(), EGoodsQuality.ORANGE.getValue(), 1));
//		equip.setD(id);
//		cha.getEquipList().set(pos, equip);
//		Message message = new Message(MessageCommand.GOODS_WEAR_EQUIP_MESSAGE, request.getChannel());
//		message.setByte(life);
//		message.setByte(pos);
//		equip.getMessage(message);
//		gameRole.sendMessage(message);
//		//通知穿装备消息
//		gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.WEAR_EQUIPMENT,1,saves));
//		gameRole.saveData(life, saves);
//		//跑马灯
//		if (nextEquip.getLevel() >= 30) {
//			ChatService.broadcastPlayerMsg(player, EBroadcast.ORANGE_UP, nextEquip.getName(), 
//					GameUtil.getLvConvertStr(nextEquip.getLevel()));			
//		}
    }

    /**
     * 红装分解
     *
     * @param request
     */
    public void processRedRes(Message request) {
//		short size = request.readShort();
//		if (size <= 0)
//			return;
//		//增加的红装碎片数
//		int num = 0;
//		List<Integer> results = new ArrayList<>();
//		//消耗装备
//		for (int i = 0; i < size; i++)
//		{
//			int id = request.readInt();
//			Equip me = gameRole.getPackManager().costEquip(id);
//			if(me != null)
//			{
//				//红装才能分解
//				if (me.getQ() != EGoodsQuality.RED.getValue())
//					continue;
//				results.add(id);
//				RedModelData model = OrangeModel.getRed(me.getG());
//				if (model != null)
//				{					
//					//计算碎片数
//					num += model.getCost();
//				}
//			}
//		}
//		EnumSet<EPlayerSaveType> enumSet = EnumSet.of(EPlayerSaveType.EQUIP);
//		//增加红装碎片
//		gameRole.getPackManager().addGoods(new DropData(EGoodsType.ITEM, GoodsDefine.ITEM_ID_RED_PIECES, num), 
//				EGoodsChangeType.RED_RES_ADD, enumSet);
//		//发送消息
//		Message message = new Message(MessageCommand.RED_RES_MESSAGE, request.getChannel());
//		message.setShort(results.size());
//		for (int id : results)
//		{			
//			message.setInt(id);
//		}
//		gameRole.sendMessage(message);
//		//保存数据
//		gameRole.savePlayer(enumSet);
    }

    /**
     * 橙装分解
     *
     * @param request
     */
    public void processOrangeRes(Message request) {
//		short size = request.readShort();
//		if (size <= 0)
//			return;
//		//增加的碎片数
//		int num = 0;
//		short sid = 0;
//		List<Integer> results = new ArrayList<>();
//		//消耗装备
//		for (int i = 0; i < size; i++)
//		{
//			int equipId = request.readInt();
//			Equip me = gameRole.getPackManager().costEquip(equipId);
//			if(me != null)
//			{
//				results.add(equipId);
//				//橙装分解
//				if (me.getQ() == EGoodsQuality.ORANGE.getValue())
//				{
//					EquipData ed = GoodsModel.getEquipDataById(me.getG());
//					OrangeModelData model = OrangeModel.getOrange(ed.getLevel());
//					if (model != null)
//					{
//						//计算碎片数
//						num += model.getHecheng();
//						sid = GoodsDefine.ITEM_ID_ORANGE_PIECES;
//					}
//				}
//				else if (me.getQ() == EGoodsQuality.RED.getValue())
//				{
//					RedModelData rd = OrangeModel.getRed(me.getG());
//					if (rd != null)
//					{
//						//计算碎片数
//						num += rd.getCost();
//						sid = GoodsDefine.ITEM_ID_RED_PIECES;
//					}
//				}
//			}
//		}
//		EnumSet<EPlayerSaveType> enumSet = EnumSet.of(EPlayerSaveType.EQUIP);
//		//增加橙装碎片
//		gameRole.getPackManager().addGoods(new DropData(EGoodsType.ITEM, sid, num), 
//				EGoodsChangeType.ORANGE_RES_ADD, enumSet);
//		//发送消息
//		Message message = new Message(MessageCommand.ORANGE_RES_MESSAGE, request.getChannel());
//		message.setShort(results.size());
//		for (int id : results)
//		{			
//			message.setInt(id);
//		}
//		gameRole.sendMessage(message);
//		//保存数据
//		gameRole.savePlayer(enumSet);
    }

    /**
     * 宝石升级
     *
     * @param request
     */
    public void processJewelUpgrade(Message request) {
//		byte idx = request.readByte();
//		//装备类型
//		byte pos = request.readByte();
//		Character cha = player.getCharacter(idx);
//		pos = getJewelMinIndex(cha);
//		//装备槽
//		EquipSlot slot = cha.getEquipSlotList().get(pos);
//		if (slot == null)
//		{
//			slot = new EquipSlot();
//			slot.setJ(0);
//			cha.getEquipSlotList().set(pos, slot);
//		}
//		//下一级
//		JewelModelData next = EquipModel.getJewelData(slot.getJ()+1,EquipDefine.getEquipType(pos));
//		if (next == null)
//		{
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_EQUIP_JEWEL_MAX);
//			return;
//		}
//		EnumSet<EPlayerSaveType> enumSet=EnumSet.of(EPlayerSaveType.CHA_EQUIPSLOT);
//		//物品不足
//		if (!gameRole.getPackManager().useGoods(next.getCost(), EGoodsChangeType.JEWEL_UPGRADE_CONSUME,enumSet))
//		{
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//			return;
//		}
//		slot.addJ();
//		//发送宝石升级消息
//		int gemLevel = 0;
//		for(Character ch:player.getCharacterList()){
//			for(EquipSlot equipSlot : ch.getEquipSlotList()){
//				if(equipSlot.getJ()>gemLevel){
//					gemLevel=equipSlot.getJ();
//				}
//			}
//		}
//		gameRole.getEventManager().notifyEvent(EGameEventType.GEM_REACH_LEVEL.create(gameRole, gemLevel,enumSet));
//		//发送消息
//		Message message = new Message(MessageCommand.JEWEL_UPGRADE_MESSAGE, request.getChannel());
//		message.setByte(idx);
//		message.setByte(pos);
//		message.setInt(slot.getJ());
//		gameRole.sendMessage(message);
//		
//		gameRole.saveData(idx, enumSet);
    }

    /**
     * 装备铸魂
     *
     * @param request
     */
    public void processZhuHun(Message request) {
//		byte idx = request.readByte();
//		byte equipType = request.readByte();
//		Character cha = player.getCharacter(idx);
//		EquipSlot slot = cha.getEquipSlotList().get(equipType);
//		byte zhCurr = slot.getZh();
//		//已铸魂
//		if (zhCurr >= LevelDefine.MAX_LEVEL_LIANHUA)
//		{
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_NONE);
//			return;
//		}
//		++zhCurr;
//		EquipZhuHunModelData modelData = EquipModel.getZhuHunData(EquipDefine.getEquipType(equipType),zhCurr);
//		if (modelData == null)
//		{
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
//			return;
//		}
//		EnumSet<EPlayerSaveType> saves = EnumSet.of(EPlayerSaveType.CHA_EQUIPSLOT);
//		if (!gameRole.getPackManager().useGoods(modelData.getConsume(), EGoodsChangeType.ZHUHUN_CONSUME, saves))
//		{
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//			return;
//		}
//		slot.setZh(zhCurr);
//		
//		gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.CASTING_SOUL_UP,1,saves));
//		gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.CASTING_SOUL_TOTAL_UP,player.getCastingSoulTotalNum(),saves));
//		
//		Message msg = new Message(MessageCommand.EQUIP_ZHUHUN_MESSASGE, request.getChannel());
//		msg.setByte(idx);
//		msg.setByte(equipType);
//		msg.setByte(zhCurr);
//		gameRole.sendMessage(msg);
//		//保存数据
//		gameRole.saveData(idx, saves);
//		//跑马灯
//		ChatService.broadcastPlayerMsg(player, EBroadcast.LIANHUA, EquipDefine.getEquipPosName(equipType));
    }

    /**
     * 铜镜玉笛
     *
     * @param request
     */
    public void processTongjingYudi(Message request) {
//		byte idx=request.readByte();
//		byte type=request.readByte();
//		Character character=player.getCharacterList().get(idx);
//		if(character==null){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		short level=0;
//		if(EquipDefine.EQUIP_TONGJING==type){
//			level=character.getTongjing();
//		}else if(EquipDefine.EQUIP_YUDI==type){
//			level=character.getYudi();
//		}else{
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		if(level>=EquipModel.MAX_TONGJINGYUDI_LEVEL){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_MAX_LEVEL);
//			return;
//		}
//		++level;
//		EquipAttrData data=EquipModel.getTongjingYudiData(level, type);
//		if(data!=null){
//			EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
//			if (!gameRole.getPackManager().useGoods(data.getCostData(), EGoodsChangeType.TONGJINGYUDI_CONSUME,enumSet))
//			{
//				gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//				return;
//			}
//			if(EquipDefine.EQUIP_TONGJING==type){
//				character.setTongjing(level);
//				enumSet.add(EPlayerSaveType.CHA_TONGJING);
//				
//				gameRole.getEventManager().notifyEvent(EGameEventType.MIRROR_UP.create(gameRole, character.getTongjing(),enumSet));
//			}else if(EquipDefine.EQUIP_YUDI==type){
//				character.setYudi(level);
//				enumSet.add(EPlayerSaveType.CHA_YUDI);
//				
//				gameRole.getEventManager().notifyEvent(EGameEventType.FLUTE_UP.create(gameRole,character.getYudi(),enumSet));
//			}
//			Message msg = new Message(MessageCommand.EQUIP_TONGJINGYUDI_MESSAGE, request.getChannel());
//			msg.setByte(idx);
//			msg.setByte(type);
//			msg.setShort(level);
//			gameRole.sendMessage(msg);
//			//保存数据			
//			gameRole.saveData(idx, enumSet);
//		}else{
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
    }

    /**
     * 左右眼
     *
     * @param request
     */
    public void processZuoYouYan(Message request) {
//		byte idx=request.readByte();
//		byte type=request.readByte();
//		Character character=player.getCharacterList().get(idx);
//		if(character==null){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		short level=0;
//		if(EquipDefine.EQUIP_ZUOYAN==type){
//			level=character.getZuoyan();
//		}else if(EquipDefine.EQUIP_YOUYAN==type){
//			level=character.getYouyan();
//		}else{
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		if(level>=LevelDefine.MAX_ZUOYOUYAN_LEVEL){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_MAX_LEVEL);
//			return;
//		}
//		++level;
//		EquipAttrData data=EquipModel.getZuoYouYanData(level, type);
//		if(data!=null){
//			EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
//			if (!gameRole.getPackManager().useGoods(data.getCostData(), EGoodsChangeType.ZUOYOUYAN_CONSUME,enumSet))
//			{
//				gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//				return;
//			}
//			if(EquipDefine.EQUIP_ZUOYAN==type){
//				character.setZuoyan(level);
//				enumSet.add(EPlayerSaveType.CHA_ZUOYAN);
//				//gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.EYE_LEFT_UP,1,enumSet));
//			}else if(EquipDefine.EQUIP_YOUYAN==type){
//				character.setYouyan(level);
//				enumSet.add(EPlayerSaveType.CHA_YOUYAN);
//				//gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.EYE_RIGHT_UP,1,enumSet));
//			}
//			Message msg = new Message(MessageCommand.EQUIP_ZUOYOUYAN_MESSAGE, request.getChannel());
//			msg.setByte(idx);
//			msg.setByte(type);
//			msg.setShort(level);
//			gameRole.sendMessage(msg);
//			//保存数据			
//			gameRole.saveData(idx, enumSet);
//		}else{
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
    }

    /**
     * 寻宝 726
     *
     * @param request
     */
    public void processRedLottery(Message request) {
        byte type = request.readByte();
        int count = 1;
        XunBaoModelData model = OrangeModel.getXunbaoData();
        DropData cost = model.getCost();
        //DropData base = new DropData(model.getReward().getT(), model.getReward().getG(), model.getReward().getN());
        //装备几率
        Map<Integer, Integer> equip_rates = EquipDefine.XUNBAO_EQUIP_SINGLE;
        EnumSet<EPlayerSaveType> saves = EnumSet.of(EPlayerSaveType.EQUIPBAG);
        boolean isCostDiamond = true;
        //十连抽
        if (type == 1) {
            count = model.getOneKeyCount();
            cost = model.getCostOneKey();
            //base.setN(base.getN()*count);
            equip_rates = EquipDefine.XUNBAO_EQUIP_TEN;
        } else {
            if (gameRole.getPackManager().useGoods(new DropData(EGoodsType.ITEM.getId(), EquipDefine.EQUIP_TREASURE_MAP, 1), EGoodsChangeType.XUNBAO_CONSUME, saves)) {
                isCostDiamond = false;
            }
        }
        //消耗
        if (isCostDiamond) {
            if (!gameRole.getPackManager().useGoods(cost, EGoodsChangeType.XUNBAO_CONSUME, saves)) {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
                return;
            }
        }
        //获得
        List<DropData> rewards = new ArrayList<>();
        //基础奖励
        //rewards.add(base);
        for (int i = 0; i < count; i++) {
            //道具
            if (GameUtil.getRangedRandom(1, 100) <= 50) {
                int random = GameUtil.getRangedRandom(1, 100);
                DropData dropItem = GameUtil.getRatesGoodsValue(EquipDefine.XUNBAO_ITEM, random);
                rewards.add(new DropData(dropItem.getT(), dropItem.getG(), dropItem.getN()));
            }
            //装备
            else {
                int random = GameUtil.getRangedRandom(1, 10000);
                int key = GameUtil.getRatesValue(equip_rates, random);
                int quality = key / 1000;
                int level = key % 1000;
                short equipId = 0;
                //同级或上下10级橙装
                if (quality == EGoodsQuality.PURPLE.getValue()) {
                    level = player.getLvConvert();
                    int plRandom = GameUtil.getRangedRandom(1, 3);
                    if (plRandom == 1)
                        level -= 10;
                    else if (plRandom == 2)
                        level += 10;
                }
                if (level < 10) level = 10;
                if (level > 200) level = 200;
                //红装
                if (quality == EGoodsQuality.RED.getValue()) {
                    List<Short> ids = new ArrayList<>();
                    for (RedModelData data : OrangeModel.getRedMap((short) level).values()) {
                        ids.add(data.getId());
                    }
                    if (ids.size() < 1)
                        continue;
                    equipId = ids.get(GameUtil.getRangedRandom(0, ids.size() - 1));
                }
                //紫、橙装
                else {
                    equipId = GoodsModel.getRandomDataByLv((short) level).getGoodsId();
                }
                DropData dropEquip = new DropData(EGoodsType.EQUIP.getId(), equipId, (byte) quality, 1);
                rewards.add(dropEquip);
            }
        }
        //十连抽特殊规则
        if (type == 1) {
//			//保底50级橙装
//			boolean orange = false;
//			for (DropData data : rewards) {
//				if (data.getT() == EGoodsType.EQUIP.getId() && data.getQ() >= EGoodsQuality.ORANGE.getValue()) {
//					orange = true;
//					break;
//				}
//			}
//			if (!orange) {
//				rewards.remove(rewards.size() - 1);
//				short equipId = GoodsModel.getRandomDataByLv((short)50).getGoodsId();
//				DropData dropEquip = new DropData(EGoodsType.EQUIP.getId(), equipId, EGoodsQuality.ORANGE.getValue(), 1);
//				rewards.add(dropEquip);
//			}
            //最多抽1件一转以上橙装
            final int MAX = 1, LEVEL = 90;
            int num = 0;
            for (DropData data : rewards) {
                if (data.getT() == EGoodsType.EQUIP.getId()) {
                    //橙装
                    if (data.getQ() == EGoodsQuality.ORANGE.getValue()) {
                        EquipData em = GoodsModel.getEquipDataById(data.getG());
                        if (em != null && em.getLevel() >= LEVEL) {
                            num++;
                        }
                    }
                    //红装
                    else if (data.getQ() == EGoodsQuality.RED.getValue()) {
                        num++;
                    }
                    if (num > MAX) {
                        //变紫装
                        if (data.getQ() == EGoodsQuality.RED.getValue())
                            data.setG(GoodsModel.getRandomDataByLv(player.getLvConvert()).getGoodsId());
                        data.setQ(EGoodsQuality.PURPLE.getValue());
                        num--;
                    }
                }
            }
        }
        player.getEquipBag().addAll(rewards);
        sortEquipBag();
        //首抽送
        if (type == 1 && player.getRedLottery() == 0) {
//			saves.add(EPlayerSaveType.REDLOTTERY);
//			player.setRedLottery((byte)1);
//			DropData firstEquip = new DropData(EGoodsType.EQUIP.getId(), 
//					EquipDefine.XUNBAO_FIRST[player.getCharacter(0).getOccupation()], EGoodsQuality.ORANGE.getValue(), 1);
//			rewards.add(firstEquip);
//			player.getEquipBag().add(firstEquip);
        }
        //消息
        gameRole.putMessageQueue(getRedEquipBagMsg());
        Message msg = new Message(MessageCommand.RED_LOTTERY_MESSAGE, request.getChannel());
        msg.setByte(player.getRedLottery());
        msg.setByte(rewards.size());
        for (DropData data : rewards) {
            data.getMessage(msg);
        }
        gameRole.sendMessage(msg);
        gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.SEEK_TREASURE, type == 1 ? 10 : 1, saves));

        //保存数据
        gameRole.savePlayer(saves);
        GameRankManager.getInstance().addXunbaoTop(gameRole, count);
        //广播
        List<DropData> broadcasts = new ArrayList<>();
        for (DropData data : rewards) {
            if (data.getQ() == EGoodsQuality.RED.getValue()) {
                broadcasts.add(data);
                continue;
            }
            if (data.getT() == EGoodsType.EQUIP.getId() && data.getQ() == EGoodsQuality.ORANGE.getValue()) {
                EquipData ed = GoodsModel.getEquipDataById(data.getG());
                if (ed != null && ed.getLevel() > GameDefine.REIN_LV) {
                    broadcasts.add(data);
                    //跑马灯
                    ChatService.broadcastPlayerMsg(player, EBroadcast.XUNBAO, ed.getName(), (ed.getLevel() / 10 - 8) + "");
                }
            }
        }
        if (broadcasts.size() > 0)
            BroadcastService.addXunbaoMsg(player.getName(), broadcasts);
    }

    private void sortEquipBag() {
        try {
            List<DropData> goods = new ArrayList<>();
            List<DropData> equips = new ArrayList<>();
            for (DropData data : player.getEquipBag()) {
                //装备、灵器
                if (data.getT() == EGoodsType.EQUIP.getId()) {
                    equips.add(data);
                } else {
                    //同物品叠加
                    boolean add = false;
                    for (DropData g : goods) {
                        if (g.getT() == data.getT() && g.getG() == data.getG()) {
                            g.setN(g.getN() + data.getN());
                            add = true;
                            break;
                        }
                    }
                    if (!add) {
                        goods.add(new DropData(data.getT(), data.getG(), data.getN()));
                    }
                }
            }
            goods.addAll(equips);
            player.setEquipBag(goods);
        } catch (Exception e) {
            logger.error("排序仓库发生异常", e);
        }
    }

    /**
     * 提取装备
     *
     * @param request
     */
    public void processRedPickUp(Message request) {
        if (player.getEquipBag().size() < 1) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        //所有物品
        List<Short> itemIndex = new ArrayList<>();
        List<Short> equipIndex = new ArrayList<>();
        List<Short> boxIndex = new ArrayList<>();
        EnumSet<EPlayerSaveType> saves = EnumSet.of(EPlayerSaveType.EQUIPBAG);
        Iterator<DropData> it = player.getEquipBag().iterator();
        short index = -1;
        while (it.hasNext()) {
            DropData goods = it.next();
            index++;
            //物品  都放在前面
            if (goods.getT() == EGoodsType.ITEM.getId()) {
                gameRole.getPackManager().addGoods(goods, EGoodsChangeType.EQUIPBAG_PICKUP_ADD, saves);
                itemIndex.add(index);
                it.remove();
            } else if (goods.getT() == EGoodsType.BOX.getId()) {
                gameRole.getPackManager().addGoods(goods, EGoodsChangeType.EQUIPBAG_PICKUP_ADD, saves);
                boxIndex.add(index);
                it.remove();
            } else if (goods.getT() == EGoodsType.EQUIP.getId()) {
                if (!gameRole.getPackManager().addGoods(goods, EGoodsChangeType.EQUIPBAG_PICKUP_ADD, saves))
                    break;
                equipIndex.add(index);
                it.remove();
            }
            //一次性最多提取90件
            if (index >= 90)
                break;
        }
        if (itemIndex.size() + equipIndex.size() + boxIndex.size() <= 0) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_BAG_FULL);
            return;
        }
        Message msg = new Message(MessageCommand.RED_PICKUP_MESSAGE, request.getChannel());
        msg.setShort(itemIndex.size() + equipIndex.size() + boxIndex.size());
        gameRole.sendMessage(msg);
        //保存数据
        gameRole.savePlayer(saves);
    }

    public Message getRedEquipBagMsg() {
        Message msg = new Message(MessageCommand.RED_BAG_MESSAGE);
        msg.setShort(player.getEquipBag().size());
        for (DropData data : player.getEquipBag()) {
            data.getMessage(msg);
        }
        return msg;
    }

    /**
     * 翅膀激活
     *
     * @param request
     */
    public void processWingActive(Message request) {
//		byte idx = request.readByte();
//		byte id = request.readByte();
//		Character cha = player.getCharacter(idx);
//		if (cha == null) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		long curr = System.currentTimeMillis();
//		//模型数据
//		FashionModelData model = EquipModel.getWingFashionData(id);
//		if (model == null) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
//			return;
//		}
//		//已经激活该永久翅膀
//		Long endTime = cha.getWings().get(id);
//		if (endTime != null && endTime == -1) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_WING_REPEAT);
//			return;
//		}
//		EnumSet<EPlayerSaveType> saves = EnumSet.of(EPlayerSaveType.CHA_WINGS);
//		//消耗
//		if (!gameRole.getPackManager().useGoods(model.getCost(), EGoodsChangeType.WING_ACTIVE_CONSUME, saves)) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//			return;
//		}
//		//永久翅膀
//		if (model.getTime() == -1) {
//			cha.getWings().put(id, -1L);
//		} 
//		//时间限制
//		else {
//			if (endTime == null || endTime < curr)
//				endTime = curr;
//			endTime += model.getTime()*DateUtil.SECOND;
//			cha.getWings().put(id, endTime);
//		}
//		//gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.WING_ACTIVE, 1,saves));
//		//消息
//		Message msg = new Message(MessageCommand.WING_ACTIVE_MESSAGE, request.getChannel());
//		msg.setByte(idx);
//		msg.setByte(id);
//		if(endTime==null){
//			msg.setInt(-1);
//		}else{
//			msg.setInt((int) ((endTime - curr)/1000));
//		}
//		gameRole.sendMessage(msg);
//		//保存数据
//		gameRole.saveData(idx, saves);
    }

    /**
     * 翅膀展示
     *
     * @param request
     */
    public void processWingShow(Message request) {
//		byte idx = request.readByte();
//		byte id = request.readByte();
//		Character cha = player.getCharacter(idx);
//		if (cha == null) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		//如果卸下 id = 0
//		if(id != 0){
//			//未激活该翅膀
//			Long endTime = cha.getWings().get(id);
//			if (endTime == null) {
//				gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_WING_NO_ACTIVE);
//				return;
//			}
//			long curr = System.currentTimeMillis();
//			if (endTime != -1 && endTime <= curr) {
//				gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_WING_NO_ACTIVE);
//				return;
//			}
//		}
//		EnumSet<EPlayerSaveType> saves = EnumSet.of(EPlayerSaveType.CHA_WINGSHOW);
//		cha.setWingShow(id);
//		//消息
//		Message msg = new Message(MessageCommand.WING_SHOW_MESSAGE, request.getChannel());
//		msg.setByte(idx);
//		msg.setByte(id);
//		gameRole.sendMessage(msg);
//		//保存数据
//		gameRole.saveData(idx, saves);
    }


    /**
     * 武器激活 363
     *
     * @param request
     */
    public void processWeaponActive(Message request) {
//		byte idx = request.readByte();
//		byte id = request.readByte();
//		Character cha = player.getCharacter(idx);
//		if (cha == null) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		long curr = System.currentTimeMillis();
//		//模型数据
//		FashionOccupModelData model = EquipModel.getWeaponFashionData(id);
//		if (model == null || model.getOccupation()!=cha.getOccupation()) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
//			return;
//		}
//		//已经激活该永久武器
//		Long endTime = cha.getWeapons().get(id);
//		if (endTime != null && endTime == -1) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_WEAPON_REPEAT);
//			return;
//		}
//		EnumSet<EPlayerSaveType> saves = EnumSet.of(EPlayerSaveType.CHA_WEAPONS);
//		//消耗
//		if (!gameRole.getPackManager().useGoods(model.getCost(), EGoodsChangeType.WEAPON_ACTIVE_CONSUME, saves)) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//			return;
//		}
//		//永久武器
//		if (model.getTime() == -1) {
//			cha.getWeapons().put(id, -1L);
//		} 
//		//时间限制
//		else {
//			if (endTime == null || endTime < curr)
//				endTime = curr;
//			endTime += model.getTime()*DateUtil.SECOND;
//			cha.getWeapons().put(id, endTime);
//		}
//		//gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.WEAPON_ACTIVE, 1,saves));
//		//消息
//		Message msg = new Message(MessageCommand.WEAPON_ACTIVE_MESSAGE, request.getChannel());
//		msg.setByte(idx);
//		msg.setByte(id);
//		if(endTime==null){
//			msg.setInt(-1);
//		}else{
//			msg.setInt((int) ((endTime - curr)/1000));
//		}
//		gameRole.sendMessage(msg);
//		//保存数据
//		gameRole.saveData(idx, saves);
    }

    /**
     * 武器展示 364
     *
     * @param request
     */
    public void processWeaponShow(Message request) {
//		byte idx = request.readByte();
//		byte id = request.readByte();
//		Character cha = player.getCharacter(idx);
//		if (cha == null) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		//如果卸下 id = 0
//		if(id != 0){
//			//未激活该武器
//			Long endTime = cha.getWeapons().get(id);
//			if (endTime == null) {
//				gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_WEAPON_NO_ACTIVE);
//				return;
//			}
//			long curr = System.currentTimeMillis();
//			if (endTime != -1 && endTime <= curr) {
//				gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_WEAPON_NO_ACTIVE);
//				return;
//			}
//		}
//		EnumSet<EPlayerSaveType> saves = EnumSet.of(EPlayerSaveType.CHA_WEAPONSHOW);
//		cha.setWeaponShow(id);
//		//消息
//		Message msg = new Message(MessageCommand.WEAPON_SHOW_MESSAGE, request.getChannel());
//		msg.setByte(idx);
//		msg.setByte(id);
//		gameRole.sendMessage(msg);
//		//保存数据
//		gameRole.saveData(idx, saves);
    }

    /**
     * 装备激活 367
     *
     * @param request
     */
    public void processArmorActive(Message request) {
//		byte idx = request.readByte();
//		byte id = request.readByte();
//		Character cha = player.getCharacter(idx);
//		if (cha == null) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		long curr = System.currentTimeMillis();
//		//模型数据
//		FashionOccupModelData model = EquipModel.getArmorFashionData(id);
//		if (model == null || model.getOccupation()!=cha.getOccupation()) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
//			return;
//		}
//		//已经激活该永久装备
//		Long endTime = cha.getArmors().get(id);
//		if (endTime != null && endTime == -1) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_ARMOR_REPEAT);
//			return;
//		}
//		EnumSet<EPlayerSaveType> saves = EnumSet.of(EPlayerSaveType.CHA_ARMORS);
//		//消耗
//		if (!gameRole.getPackManager().useGoods(model.getCost(), EGoodsChangeType.ARMOR_ACTIVE_CONSUME, saves)) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//			return;
//		}
//		//永久武器
//		if (model.getTime() == -1) {
//			cha.getArmors().put(id, -1L);
//		} 
//		//时间限制
//		else {
//			if (endTime == null || endTime < curr)
//				endTime = curr;
//			endTime += model.getTime()*DateUtil.SECOND;
//			cha.getArmors().put(id, endTime);
//		}
//		//gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.ARMOR_ACTIVE, 1,saves));
//		//消息
//		Message msg = new Message(MessageCommand.ARMOR_ACTIVE_MESSAGE, request.getChannel());
//		msg.setByte(idx);
//		msg.setByte(id);
//		if(endTime==null){
//			msg.setInt(-1);
//		}else{
//			msg.setInt((int) ((endTime - curr)/1000));
//		}
//		gameRole.sendMessage(msg);
//		//保存数据
//		gameRole.saveData(idx, saves);
    }

    /**
     * 装备展示 368
     *
     * @param request
     */
    public void processArmorShow(Message request) {
//		byte idx = request.readByte();
//		byte id = request.readByte();
//		Character cha = player.getCharacter(idx);
//		if (cha == null) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		//如果卸下 id = 0
//		if(id != 0){
//			//未激活该装备
//			Long endTime = cha.getArmors().get(id);
//			if (endTime == null) {
//				gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_ARMOR_NO_ACTIVE);
//				return;
//			}
//			long curr = System.currentTimeMillis();
//			if (endTime != -1 && endTime <= curr) {
//				gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_ARMOR_NO_ACTIVE);
//				return;
//			}
//		}
//		EnumSet<EPlayerSaveType> saves = EnumSet.of(EPlayerSaveType.CHA_ARMORSHOW);
//		cha.setArmorShow(id);
//		//消息
//		Message msg = new Message(MessageCommand.ARMOR_SHOW_MESSAGE, request.getChannel());
//		msg.setByte(idx);
//		msg.setByte(id);
//		gameRole.sendMessage(msg);
//		//保存数据
//		gameRole.saveData(idx, saves);
    }

    /**
     * 坐骑激活 369
     *
     * @param request
     */
    public void processMountActive(Message request) {
//		byte idx = request.readByte();
//		byte id = request.readByte();
//		Character cha = player.getCharacter(idx);
//		if (cha == null) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		long curr = System.currentTimeMillis();
//		//模型数据
//		FashionModelData model = EquipModel.getMountFashionData(id);
//		if (model == null) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
//			return;
//		}
//		//已经激活该永久坐骑
//		Long endTime = cha.getMounts().get(id);
//		if (endTime != null && endTime == -1) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_MOUNT_REPEAT);
//			return;
//		}
//		EnumSet<EPlayerSaveType> saves = EnumSet.of(EPlayerSaveType.CHA_MOUNTS);
//		//消耗
//		if (!gameRole.getPackManager().useGoods(model.getCost(), EGoodsChangeType.MOUNT_ACTIVE_CONSUME, saves)) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//			return;
//		}
//		//永久坐骑
//		if (model.getTime() == -1) {
//			cha.getMounts().put(id, -1L);
//		} 
//		//时间限制
//		else {
//			if (endTime == null || endTime < curr)
//				endTime = curr;
//			endTime += model.getTime()*DateUtil.SECOND;
//			cha.getMounts().put(id, endTime);
//		}
//		//gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.MOUNT_ACTIVE, 1,saves));
//		//消息
//		Message msg = new Message(MessageCommand.MOUNT_ACTIVE_MESSAGE, request.getChannel());
//		msg.setByte(idx);
//		msg.setByte(id);
//		if(endTime==null){
//			msg.setInt(-1);
//		}else{
//			msg.setInt((int) ((endTime - curr)/1000));
//		}
//		gameRole.sendMessage(msg);
//		//保存数据
//		gameRole.saveData(idx, saves);
    }

    /**
     * 坐骑展示 370
     *
     * @param request
     */
    public void processMountShow(Message request) {
//		byte idx = request.readByte();
//		byte id = request.readByte();
//		Character cha = player.getCharacter(idx);
//		if (cha == null) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		//如果卸下 id = 0
//		if(id != 0){
//			//未激活该坐骑
//			Long endTime = cha.getMounts().get(id);
//			if (endTime == null) {
//				gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_MOUNT_NO_ACTIVE);
//				return;
//			}
//			long curr = System.currentTimeMillis();
//			if (endTime != -1 && endTime <= curr) {
//				gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_MOUNT_NO_ACTIVE);
//				return;
//			}
//		}
//		EnumSet<EPlayerSaveType> saves = EnumSet.of(EPlayerSaveType.CHA_MOUNTSHOW);
//		cha.setMountShow(id);
//		//消息
//		Message msg = new Message(MessageCommand.MOUNT_SHOW_MESSAGE, request.getChannel());
//		msg.setByte(idx);
//		msg.setByte(id);
//		gameRole.sendMessage(msg);
//		//保存数据
//		gameRole.saveData(idx, saves);
    }

    /**
     * 装备铸魂
     *
     * @param request
     */
    public void processCuiLian(Message request) {
//		byte idx = request.readByte();
//		byte pos = request.readByte();
//		Character cha = player.getCharacter(idx);
//		EquipSlot slot = cha.getEquipSlotList().get(pos);
//		byte level = slot.getCl();
//		int exp = slot.getCle();
//
//		byte nextLevel = (byte) (level + 1);
//		EquipCuiLianModelData modelData = EquipModel.getCuiLianData(nextLevel);
//		if (modelData == null)
//		{
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
//			return;
//		}
//		EnumSet<EPlayerSaveType> saves = EnumSet.of(EPlayerSaveType.CHA_EQUIPSLOT);
//		if (!gameRole.getPackManager().useGoods(modelData.getCost(), EGoodsChangeType.CUILIAN_CONSUME, saves))
//		{
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//			return;
//		}
//		exp++;
//		int totalExp = modelData.getExp();
//		if (exp >= totalExp){
//			level = nextLevel;
//			exp -= totalExp;
//		}
//		slot.setCl(level);
//		slot.setCle(exp);
//
//		//gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.CUILIAN_UP,1,saves));
//
//		Message msg = new Message(MessageCommand.EQUIP_CUILIAN_MESSASGE, request.getChannel());
//		msg.setByte(idx);
//		msg.setByte(pos);
//		msg.setByte(level);
//		msg.setInt(exp);
//		gameRole.sendMessage(msg);
//		//保存数据
//		gameRole.saveData(idx, saves);
    }

    /**
     * 合击符文背包
     *
     * @param request
     */
    public void processCombineRuneBag(Message request) {
        Message message = new Message(MessageCommand.COMBINE_RUNE_BAG_MESSAGE, request.getChannel());
        message.setInt(player.getCombineRunePiece());
        message.setByte(player.getCombineRuneBag().size());
//		System.out.println(player.getCombineRuneBag().size());
        for (Entry<Byte, Integer> entry : player.getCombineRuneBag().entrySet()) {
            message.setByte(entry.getKey());
            message.setInt(entry.getValue());
//			System.out.println(entry.getKey()+":"+entry.getValue());
        }
        gameRole.sendMessage(message);
    }

    /**
     * 合击符文装备
     *
     * @param request
     */
    public void processCombineRuneEquip(Message request) {
        byte id = request.readByte();
        Integer num = player.getCombineRuneBag().get(id);
        if (num == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }
        CombineRuneData data = EquipModel.getCombineRuneData(id);
        if (data == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
//		if (data.getLevel() <= 80 && player.getLevel() < data.getLevel()) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_LEVEL_LESS);
//			return;
//		} else if (data.getLevel() > 80 && player.getRein() < data.getLevel()/10-8) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_REIN_LESS);
//			return;
//		}

        int pos = id % 10 - 1;
        byte equiped = player.getCombineRune()[pos];
        if (equiped != 0) {
            CombineRuneData equipedData = EquipModel.getCombineRuneData(equiped);
            if (equipedData.getInto() != id) {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
                return;
            }
        }

        player.getCombineRune()[pos] = id;

        EnumSet<EPlayerSaveType> enumSet = EnumSet.of(EPlayerSaveType.COMBINE_RUNE);
        CombineRuneCmd.gi().consume(gameRole, new DropData(EGoodsType.COMBINE_RUNE, id, 1), EGoodsChangeType.COMBINE_RUNE_EQUIP_CONSUME, enumSet);

        Message message = new Message(MessageCommand.COMBINE_RUNE_EQUIP_MESSAGE, request.getChannel());
        message.setByte(pos);
        message.setByte(id);
        gameRole.sendMessage(message);
        gameRole.savePlayer(enumSet);
    }

    /**
     * 获取当前激活的合击符文套装
     * @return
     */
//	public short getCombineSuitInvoked(){
//		byte[] combines = player.getCombineRune();
//		for (byte pos = 0; pos < combines.length; pos++){
//		}
//	}

    /**
     * 合击符文分解
     *
     * @param request
     */
    public void processCombineRuneDecompose(Message request) {
        List<Byte> list = new ArrayList<>();
        byte size = request.readByte();
        for (int i = 0; i < size; ++i) {
            byte id = request.readByte();
            list.add(id);
        }

        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        int addNum = 0;
        for (byte id : list) {
            Integer num = player.getCombineRuneBag().get(id);
            if (num == null) {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
                return;
            }
            CombineRuneData data = EquipModel.getCombineRuneData(id);
            addNum += data.getDecompose();

            CombineRuneCmd.gi().consume(gameRole, new DropData(EGoodsType.COMBINE_RUNE, id, 1), EGoodsChangeType.COMBINE_RUNE_EQUIP_CONSUME, enumSet);
        }
        CombineRunePieceCmd.gi().reward(gameRole, new DropData(EGoodsType.COMBINE_RUNE_PIECE, 0, addNum), EGoodsChangeType.COMBINE_RUNE_PIECE_DECOMPOSE_ADD, enumSet);

        gameRole.sendTick(request);

        gameRole.savePlayer(enumSet);
    }

    /**
     * 合击符文合成
     *
     * @param request
     */
    public void processCombineRuneCompose(Message request) {
        byte id = request.readByte();
        CombineRuneData data = EquipModel.getCombineRuneData(id);
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        if (!CombineRunePieceCmd.gi().consume(gameRole, new DropData(EGoodsType.COMBINE_RUNE_PIECE, 0, data.getCompose()), EGoodsChangeType.COMBINE_RUNE_PIECE_CONSUME, enumSet)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }
        CombineRuneCmd.gi().reward(gameRole, new DropData(EGoodsType.COMBINE_RUNE, id, 1), EGoodsChangeType.COMBINE_RUNE_COMPOSE_ADD, enumSet);
        gameRole.sendTick(request);
        gameRole.savePlayer(enumSet);
    }

    /**
     * 镇魂装备升级 2401
     *
     * @param request
     */
    public void processTownSoulUpgrade(Message request) {
//		byte idx = request.readByte();
//		byte pos = request.readByte();
//		if(pos<0 || pos>=EquipDefine.EQUIP_POS_NUM){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		Character character=player.getCharacter(idx);
//		if(character==null){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		byte lv=character.getTownSoulEquip()[pos];
//		TownSoulData data=EquipModel.getTownSoulData(pos, ++lv);
//		EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
//		if(!gameRole.getPackManager().useGoods(data.getCost(), EGoodsChangeType.TOWN_SOUL_UPGRADE_CONSUME, enumSet)){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//			return;
//		}
//		character.getTownSoulEquip()[pos]=lv;
//		
//		Message message=new Message(MessageCommand.TOWN_SOUL_UPGRADE,request.getChannel());
//		message.setByte(idx);
//		message.setByte(pos);
//		message.setByte(lv);
//		gameRole.sendMessage(message);
//		
//		enumSet.add(EPlayerSaveType.CHA_TOWN_SOUL);
//		gameRole.saveData(idx, enumSet);
    }

    /**
     * 镇魂装备合成 2402
     *
     * @param request
     */
    public void processTownSoulCompose(Message request) {
        byte pos = request.readByte();
        if (pos < 0 || pos >= EquipDefine.EQUIP_POS_NUM) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        TownSoulData data = EquipModel.getTownSoulData(pos, (byte) 1);
        if (data.getCompose().getN() < 1) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        if (!gameRole.getPackManager().useGoods(data.getCompose(), EGoodsChangeType.TOWN_SOUL_COMPOSE_CONSUME, enumSet)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }
        gameRole.getPackManager().addGoods(data.getCost(), EGoodsChangeType.TOWN_SOUL_COMPOSE_ADD, enumSet);

        Message message = new Message(MessageCommand.TOWN_SOUL_COMPOSE, request.getChannel());
        gameRole.sendMessage(message);

        gameRole.savePlayer(enumSet);
    }

    /**
     * 镇魂装备分解2403
     *
     * @param request
     */
    public void processTownSoulDecompose(Message request) {
        short itemId = request.readShort();

        TownSoulData data = EquipModel.getTownSoulData(itemId);
        if (data == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        if (!gameRole.getPackManager().useGoods(data.getCost(), EGoodsChangeType.TOWN_SOUL_DECOMPOSE_CONSUME, enumSet)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }
        gameRole.getPackManager().addGoods(data.getDecompose(), EGoodsChangeType.TOWN_SOUL_DECOMPOSE_ADD, enumSet);

        Message message = new Message(MessageCommand.TOWN_SOUL_COMPOSE, request.getChannel());
        gameRole.sendMessage(message);

        gameRole.savePlayer(enumSet);
    }

    /**
     * 镇魂宝库2404
     *
     * @param request
     */
    public void processTownSoulTreasure(Message request) {
        TownSoulTreasure treasure = player.getTownSoulTreasure();

        int downTime = 0;
        if (treasure.getLotteryTimeStamp() != 0) {
            long currTime = System.currentTimeMillis();
            if (treasure.getLotteryTimeStamp() + EquipDefine.TOWN_SOUL_TURNTABLE_RESET_TIME < currTime) {
                treasure.setLottery((short) 0);
                treasure.getLotteryReward().clear();
            } else {
                downTime = (int) (treasure.getLotteryTimeStamp() + EquipDefine.TOWN_SOUL_TURNTABLE_RESET_TIME - currTime) / 1000;
            }
        }

        Message message = new Message(MessageCommand.TOWN_SOUL_TREASURE, request.getChannel());
        message.setShort(treasure.getLottery());
        message.setByte(treasure.getLotteryReward().size());
        for (short id : treasure.getLotteryReward()) {
            message.setShort(id);
        }
        message.setInt(downTime);
        message.setShort(treasure.getLucky());
        message.setByte(treasure.getPack().size());
        for (DropData data : treasure.getPack().values()) {
            data.getMessage(message);
        }
        message.setByte(GameWorld.getPtr().getWorldDataManager().getTownSoulTurntableRecordList().size());
        for (TownSoulTurntableRecord record : GameWorld.getPtr().getWorldDataManager().getTownSoulTurntableRecordList()) {
            message.setString(record.getName());
            record.getReward().getMessage(message);
        }
        gameRole.sendMessage(message);
    }

    /**
     * 镇魂宝库转盘2405
     *
     * @param request
     */
    public void processTownSoulTreasureTurntable(Message request) {
        byte type = request.readByte();
        int num = 1;

        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        if (type == 2) {
            if (!gameRole.getPackManager().useGoods(new DropData(EGoodsType.DIAMOND.getId(), 0, EquipDefine.TOWN_SOUL_TURNTABLE_TEN), EGoodsChangeType.TOWN_SOUL_TURNTABLE_CONSUME, enumSet)) {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
                return;
            }
            num = 10;
        } else {
            if (!gameRole.getPackManager().useGoods(new DropData(EGoodsType.ITEM.getId(), EquipDefine.TOWN_SOUL_TURNTABLE_TICKET, 1), EGoodsChangeType.TOWN_SOUL_TURNTABLE_CONSUME, enumSet)) {
                if (!gameRole.getPackManager().useGoods(new DropData(EGoodsType.DIAMOND.getId(), 0, EquipDefine.TOWN_SOUL_TURNTABLE_ONE), EGoodsChangeType.TOWN_SOUL_TURNTABLE_CONSUME, enumSet)) {
                    gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
                    return;
                }
            }
        }

        TownSoulTreasure townSoulTreasure = player.getTownSoulTreasure();
        byte probaility = townSoulTreasure.getProbaility();

        TownSoulTurntableProbailityData probailityData = EquipModel.getTownSoulTurntableProbailityData(probaility);
        int lucky = townSoulTreasure.getLucky() + num;
        if (lucky > probailityData.getTimeMax()) {
            townSoulTreasure.setProbaility(++probaility);
            probailityData = EquipModel.getTownSoulTurntableProbailityData(probaility);
        }
        townSoulTreasure.setLucky((short) lucky);
        byte[] useProbaility = probailityData.getProbability();
        if (townSoulTreasure.getProbailityRecord().contains(probaility)) {
            useProbaility = EquipModel.getTownSoulTurntableProbailityData(EquipDefine.TOWN_SOUL_INITIAL_PROBAILITY).getProbability();
        }

        boolean isMax = false;
        List<DropData> reward = new ArrayList<>();
        for (int i = 0; i < num; ++i) {
            short index = GameCommon.getRandomIndex(useProbaility);
            if (index == -1) {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
                return;
            }
            ++index;
            for (byte t : probailityData.getTarget()) {
                if (t == index) {
                    townSoulTreasure.getProbailityRecord().add(townSoulTreasure.getProbaility());
                    useProbaility = EquipModel.getTownSoulTurntableProbailityData(EquipDefine.TOWN_SOUL_INITIAL_PROBAILITY).getProbability();
                }
            }
            DropData dropData = EquipModel.getTownSoulTreasureData().getReward().get(index);
            reward.add(dropData);
            if (dropData.getT() == EGoodsType.BOX.getId() && dropData.getG() == GoodsDefine.BOX_ID_TOWN_SOUL_TREASURE) {
                isMax = true;
            }
        }
        if (isMax) {
            townSoulTreasure.setLucky((short) 0);
            townSoulTreasure.setProbaility((byte) 1);
            townSoulTreasure.getProbailityRecord().clear();
        }
        townSoulTreasure.setLottery((short) (townSoulTreasure.getLottery() + num));

        List<DropData> record = new ArrayList<>();
        for (DropData data : reward) {
            DropData haveGoods = townSoulTreasure.getPack().get(data.getG());
            if (haveGoods != null) {
                haveGoods.setN(haveGoods.getN() + data.getN());
            } else {
                townSoulTreasure.getPack().put(data.getG(), data.createCopy());
            }
            if (data.getG() != GoodsDefine.ITEM_ID_TOWN_SOUL_STONE) {
                record.add(data.createCopy());
            }
        }

        long currTime = System.currentTimeMillis();
        if (townSoulTreasure.getLotteryTimeStamp() + EquipDefine.TOWN_SOUL_TURNTABLE_RESET_TIME < currTime) {
            townSoulTreasure.setLotteryTimeStamp(currTime);
        }
        int downTime = (int) (townSoulTreasure.getLotteryTimeStamp() + EquipDefine.TOWN_SOUL_TURNTABLE_RESET_TIME - currTime) / 1000;

        Message message = new Message(MessageCommand.TOWN_SOUL_TREASURE_TURNTABLE, request.getChannel());
        message.setShort(townSoulTreasure.getLottery());
        message.setInt(downTime);
        message.setShort(townSoulTreasure.getLucky());
        message.setByte(reward.size());
        for (DropData data : reward) {
            data.getMessage(message);
        }

        Message recordMessage = null;

        if (record.size() > 0) {
            recordMessage = new Message(MessageCommand.TOWN_SOUL_TREASURE_RECORD);
            recordMessage.setByte(record.size());
            for (DropData dd : record) {
                GameWorld.getPtr().getWorldDataManager().addRecord(player.getName(), dd);

                recordMessage.setString(player.getName());
                dd.getMessage(recordMessage);
            }
        }
        if (recordMessage != null) {
            gameRole.putMessageQueue(recordMessage);
        }

        gameRole.sendMessage(message);

        if (recordMessage != null) {
            for (GameRole gRole : GameWorld.getPtr().getOnlineRoles().values()) {
                if (gRole.getPlayerId() != player.getId()) {
                    gRole.putMessageQueue(recordMessage);
                }
            }
        }

        enumSet.add(EPlayerSaveType.TOWN_SOUL);
        gameRole.savePlayer(enumSet);

        if (record.size() > 0) {
            GameWorld.getPtr().getWorldDataManager().saveTownSoulTurntableRecord();
        }
    }

    /**
     * 镇魂宝库取出2406
     *
     * @param request
     */
    public void processTownSoulTreasureOut(Message request) {

        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        gameRole.getPackManager().addGoods(new ArrayList<DropData>(player.getTownSoulTreasure().getPack().values()), EGoodsChangeType.TOWN_SOUL_TREASURE_OUT_ADD, enumSet);
        player.getTownSoulTreasure().getPack().clear();

        Message message = new Message(MessageCommand.TOWN_SOUL_TREASURE_OUT, request.getChannel());
        gameRole.sendMessage(message);

        enumSet.add(EPlayerSaveType.TOWN_SOUL);
        gameRole.savePlayer(enumSet);
    }

    /**
     * 镇魂宝库领取抽取宝箱2407
     *
     * @param request
     */
    public void processTownSoulTreasureBox(Message request) {
        short time = request.readShort();

        TownSoulTreasureRewardData rewardData = EquipModel.getTownSoulTreasureData().getTimeRewardMap().get(time);
        if (rewardData == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        TownSoulTreasure treasure = player.getTownSoulTreasure();
        if (treasure.getLotteryReward().contains(time)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        if (treasure.getLottery() < time) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);

        gameRole.getPackManager().addGoods(rewardData.getReward(), EGoodsChangeType.TOWN_SOUL_TREASURE_BOX_ADD, enumSet);
        treasure.getLotteryReward().add(time);

        Message message = new Message(MessageCommand.TOWN_SOUL_TREASURE_BOX, request.getChannel());
        message.setShort(time);
        gameRole.sendMessage(message);

        enumSet.add(EPlayerSaveType.TOWN_SOUL);
        gameRole.savePlayer(enumSet);
    }
}
