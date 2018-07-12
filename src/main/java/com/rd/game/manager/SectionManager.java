package com.rd.game.manager;

import com.rd.bean.drop.DropData;
import com.rd.bean.lianti.Wing;
import com.rd.bean.player.Player;
import com.rd.common.BroadcastService;
import com.rd.common.ChatService;
import com.rd.common.GameCommon;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.define.ErrorDefine;
import com.rd.define.LevelDefine;
import com.rd.define.SectionDefine;
import com.rd.game.GameRole;
import com.rd.model.RoleModel;
import com.rd.model.SectionModel;
import com.rd.model.data.*;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class SectionManager {
    private static final Logger logger = Logger.getLogger(SectionManager.class);
    private GameRole gameRole;
    private Player player;

    public SectionManager(GameRole gameRole) {
        this.gameRole = gameRole;
        this.player = gameRole.getPlayer();
    }

    public void init() {
//		for (Character character: player.getCharacterList()){
//			updateWingGodSkill(character.getWing());
//		}
    }

    /**
     * 601 坐骑升级
     *
     * @param request
     */
    public void processMountUp(Message request) {
//		byte idx=request.readByte();
//		Character character=player.getCharacter(idx);
//		if(character==null){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		boolean up = false;
//		byte type=request.readByte();
//		EnumSet<EPlayerSaveType> enumSet=EnumSet.noneOf(EPlayerSaveType.class);
//		if(character.getMountStar()==0 && character.getMountStage()==0){
//			character.addMountJieduan();
//			enumSet.add(EPlayerSaveType.CHA_MOUNTSTAR);
//			enumSet.add(EPlayerSaveType.CHA_MOUNTJIEDUAN);
//			gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.WING_REACH_STAGE,character.getMountStage(),enumSet));
//		}else if(character.getMountStar()==SectionDefine.STAR_FULL){
//			if(character.getMountStage()==LevelDefine.MAX_MOUNT_JIEDUAN){
//				gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_MAX_LEVEL);
//				return;
//			}else{
//				character.addMountJieduan();
//				enumSet.add(EPlayerSaveType.CHA_MOUNTSTAR);
//				enumSet.add(EPlayerSaveType.CHA_MOUNTJIEDUAN);
//				gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.WING_REACH_STAGE,character.getMountStage(),enumSet));
//			}
//		}else{
//			MountData data=SectionModel.getMountData(character.getMountStage(),character.getMountStar());
//			if(SectionDefine.UP_TYPE_GOLD==type){
//				DropData cost = new DropData(EGoodsType.GOLD.getId(), 0, data.getGoldCost());
//				if (!gameRole.getPackManager().useGoods(cost, EGoodsChangeType.MOUNTUP_CONSUME,enumSet)) {
//					gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//					return;
//				}
//				up = character.addMountExp(data.getGoldExp());
//				enumSet.add(EPlayerSaveType.CHA_MOUNTSTAR);
//				enumSet.add(EPlayerSaveType.CHA_MOUNTEXP);
//			}else if(SectionDefine.UP_TYPE_ITEM==type){
//				if (!gameRole.getPackManager().useGoods(data.getItemCost(), EGoodsChangeType.MOUNTUP_CONSUME,enumSet)) {
//					gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//					return;
//				}
//				up = character.addMountExp(data.getItemExp());
//				enumSet.add(EPlayerSaveType.CHA_MOUNTSTAR);
//				enumSet.add(EPlayerSaveType.CHA_MOUNTEXP);
//			}else{
//				gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//				return;
//			}
//		}
//		//通知坐骑培养消息
//		gameRole.getEventManager().notifyEvent(EGameEventType.WING_UP.create(gameRole, 1,enumSet));
//		
//		this.sendMountUpMessage(idx, character, request);
//		//升星成功
//		if (up == true) {
//			player.getSmallData().setHorseTime(System.currentTimeMillis());
//			enumSet.add(EPlayerSaveType.SMALLDATA);
//			enumSet.add(EPlayerSaveType.CHA_MOUNTJIEDUAN);
//		}
//		gameRole.saveData(idx, enumSet);
    }

    /**
     * 602 法宝详细信息
     *
     * @param request
     */
    public void processMagicDetail(Message request) {
        this.checkMagicLevelUpReplyTime();
        Message message = new Message(MessageCommand.MAGIC_DETAIL_MESSAGE, request.getChannel());
        message.setByte(player.getSmallData().getMagicLevelUpNum());
        message.setInt((int) (System.currentTimeMillis() - player.getSmallData().getMagicLevelUpTime()));
        message.setInt(player.getMagicStageExp());
        message.setByte(player.getDayData().getMagicTurntable());
        gameRole.sendMessage(message);
    }

    /**
     * 603 法宝升级
     *
     * @param request
     */
    public void processMagicLevelUp(Message request) {
        if (player.getMagicLevel() < LevelDefine.MAX_MAGIC_LEVEL) {
            this.checkMagicLevelUpReplyTime();
            byte levelUpNum = player.getSmallData().getMagicLevelUpNum();
            if (levelUpNum < 1) {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_NUM_MAX);
                return;
            }
            EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);

            short level = player.getMagicLevel();
            if (level == 0) {
                level = 1;
                player.setMagicLevel(level);
                enumSet.add(EPlayerSaveType.MAGICLEVEL);
            }
            byte star = player.getMagicLevelStar();
            ++star;
            if (star == SectionDefine.STAR_FULL) {
                ++level;
                star = 0;
            }
            MagicLevelData data = SectionModel.getMagicLevelData(level, star);

            if (!gameRole.getPackManager().useGoods(new DropData(EGoodsType.GOLD, 0, data.getCostGold()), EGoodsChangeType.MAGIC_LEVELUP_CONSUME, enumSet)) {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
                return;
            }
            if (player.getSmallData().getMagicLevelUpNum() == SectionDefine.MAGIC_LEVELUP_MAX_NUM) {
                player.getSmallData().setMagicLevelUpTime(System.currentTimeMillis());
            }
            --levelUpNum;
            player.getSmallData().setMagicLevelUpNum(levelUpNum);
            enumSet.add(EPlayerSaveType.SMALLDATA);
            int addStar = 1;
            if (GameCommon.getRandomIndex(7500, 2500) > 0) {
                addStar = 2;
            }
            enumSet.add(EPlayerSaveType.MAGICLEVELSTAR);
            if (player.addMagicLevelStar(addStar)) {
                enumSet.add(EPlayerSaveType.MAGICLEVEL);
            }

            //gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.MAGIC_LEVEL_UP,1,enumSet));

            Message message = new Message(MessageCommand.MAGIC_LEVEL_UP_MESSAGE, request.getChannel());
            message.setShort(player.getMagicLevel());
            message.setByte(player.getMagicLevelStar());
            message.setByte(player.getSmallData().getMagicLevelUpNum());
            message.setInt((int) (System.currentTimeMillis() - player.getSmallData().getMagicLevelUpTime()));
            gameRole.sendMessage(message);
            gameRole.savePlayer(enumSet);
        } else {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_MAX_LEVEL);
        }
    }

    public void checkMagicLevelUpReplyTime() {
        if (player.getSmallData().getMagicLevelUpNum() < SectionDefine.MAGIC_LEVELUP_MAX_NUM) {
            long time = System.currentTimeMillis() - player.getSmallData().getMagicLevelUpTime();
            if (time < 0) {
                return;
            }
            long num = time / SectionDefine.MAGIC_LEVELUP_NUM_REPLY_TIME;
            if (player.getSmallData().getMagicLevelUpNum() + num >= SectionDefine.MAGIC_LEVELUP_MAX_NUM) {
                player.getSmallData().setMagicLevelUpNum(SectionDefine.MAGIC_LEVELUP_MAX_NUM);
                player.getSmallData().setMagicLevelUpTime(0);
            } else {
                player.getSmallData().setMagicLevelUpNum((byte) (player.getSmallData().getMagicLevelUpNum() + num));
                player.getSmallData().setMagicLevelUpTime(player.getSmallData().getMagicLevelUpTime() + num * SectionDefine.MAGIC_LEVELUP_NUM_REPLY_TIME);
            }
        }
    }

    /**
     * 604 法宝升阶
     *
     * @param request
     */
    public void processMagicStageUp(Message request) {
        if (player.getMagicStage() < LevelDefine.MAX_MAGIC_STAGE || player.getMagicStageStar() < 10) {
            EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
            if (player.getMagicStage() == 0 && player.getMagicStageStar() == 0) {
                player.addMagicStage();
                enumSet.add(EPlayerSaveType.MAGICSTAGE);
            } else if (player.getMagicStageStar() == SectionDefine.STAR_FULL) {
                player.addMagicStage();
                enumSet.add(EPlayerSaveType.MAGICSTAGE);
                enumSet.add(EPlayerSaveType.MAGICSTAGESTAR);
            } else {
                short stage = player.getMagicStage();
                byte star = player.getMagicStageStar();
                ++star;
                if (star > SectionDefine.STAR_FULL) {
                    ++stage;
                    star = 0;
                }
                MagicStageData data = SectionModel.getMagicStageData(stage, star);
                if (data.getItemCost().getN() > 0) {
                    if (!gameRole.getPackManager().useGoods(data.getItemCost(), EGoodsChangeType.MAGIC_STAGEUP_CONSUME, enumSet)) {
                        gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
                        return;
                    }
                }
                int addExp = data.getItemExp();
                int random = GameCommon.getRandomIndex(2500, 500);
                if (random == 0) {
                    addExp *= 2;
                } else if (random == 1) {
                    addExp *= 5;
                }

                player.addMagicStageExp(addExp, data.getExp());
                enumSet.add(EPlayerSaveType.MAGICSTAGE);
                enumSet.add(EPlayerSaveType.MAGICSTAGESTAR);
                enumSet.add(EPlayerSaveType.MAGICSTAGEEXP);
            }
            //gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.MAGIC_STAGE_UP,1,enumSet));

            Message message = new Message(MessageCommand.MAGIC_STAGE_UP_MESSAGE, request.getChannel());
            message.setShort(player.getMagicStage());
            message.setByte(player.getMagicStageStar());
            message.setInt(player.getMagicStageExp());
            gameRole.sendMessage(message);
            gameRole.savePlayer(enumSet);
        } else {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_MAX_LEVEL);
        }
    }

    /**
     * 605 法宝转盘
     *
     * @param request
     */
    public void processMagicTurntable(Message request) {
        byte num = player.getDayData().getMagicTurntable();
        if (num < SectionDefine.MAGIC_TURNTABLE_NUM) {
            MagicTurntableData data = SectionModel.getMagicTurntableData(++num);
            EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);

            if (!gameRole.getPackManager().useGoods(data.getItemCost(), EGoodsChangeType.MAGIC_TURNTABLE_CONSUME, enumSet)) {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
                return;
            }

            player.getDayData().setMagicTurntable(num);
            enumSet.add(EPlayerSaveType.DAYDATA);

            int multiplyIndex = GameCommon.getRandomIndex(data.getMultiplyChance());
            int numIndex = GameCommon.getRandomIndex(data.getNumChance());
            int goodsNum = data.getMultiplyValue()[multiplyIndex] * data.getNumValue()[numIndex];
            gameRole.getPackManager().addGoods(new DropData(data.getRewardData().getT(), data.getRewardData().getG(), goodsNum), EGoodsChangeType.MAGIC_TURNTABLE_ADD, enumSet);

            Message message = new Message(MessageCommand.MAGIC_TURNTABLE_MESSAGE, request.getChannel());
            message.setByte(num);
            message.setByte(multiplyIndex);
            message.setByte(numIndex);
            gameRole.sendMessage(message);
            gameRole.savePlayer(enumSet);
        } else {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_NUM_MAX);
        }
    }

    /**
     * 610 坐骑直升丹
     *
     * @param request
     */
    public void processMountPill(Message request) {
//		byte idx=request.readByte();
//		Character character=player.getCharacter(idx);
//		if(character==null){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		EnumSet<EPlayerSaveType> enumSet=EnumSet.noneOf(EPlayerSaveType.class);
//		if (!gameRole.getPackManager().useGoods(new DropData(EGoodsType.ITEM,GoodsDefine.ITEM_ID_MOUNT_UP_PILL,1), EGoodsChangeType.MOUNT_UP_PILL_CONSUME,enumSet)) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//			return;
//		}
//		//5阶以下的坐骑等级提升1级，高于5阶坐骑使用后获得3000经验
//		if(character.getMountStage()<6){
//			character.setMountStage((short)(character.getMountStage()+1));
//			player.getSmallData().setHorseTime(System.currentTimeMillis());
//			enumSet.add(EPlayerSaveType.SMALLDATA);
//			enumSet.add(EPlayerSaveType.CHA_MOUNTJIEDUAN);
//		}else{
//			//升星成功
//			byte[] state=character.getWing().addMountExpPill((short)(3000));
//			if (state[0]==1) {
//				player.getSmallData().setHorseTime(System.currentTimeMillis());
//				enumSet.add(EPlayerSaveType.SMALLDATA);
//				enumSet.add(EPlayerSaveType.CHA_MOUNTJIEDUAN);
//				enumSet.add(EPlayerSaveType.CHA_MOUNTSTAR);
//				enumSet.add(EPlayerSaveType.CHA_MOUNTEXP);
//			}
//			if (state[1]==1) {
//				gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.WING_REACH_STAGE,character.getMountStage(),enumSet));
//			}
//		}
//		
//		this.sendMountUpMessage(idx, character, request);
//		gameRole.saveData(idx, enumSet);
    }

//	/**
//	 * 620 坐骑装备
//	 * @param request
//	 */
//	public void processMountEquip(Message request){
//		byte idx=request.readByte();
//		Character character=player.getCharacter(idx);
//		if(character==null){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		EnumSet<EPlayerSaveType> enumSet=EnumSet.noneOf(EPlayerSaveType.class);
//
//		List<Artifact> tempList=new ArrayList<>();
//		for(int i=0;i<GoodsModel.getMountPosSize();++i){
//			tempList.add(null);
//		}
//
//		for(Artifact artifact:player.getArtifactList()){
//			ArtifactData artifactData=GoodsModel.getArtifactDataById(artifact.getG());
//			if(artifactData.getStage()<=character.getMountStage()){
//				Artifact mountEquip=character.getMountEquip().get(artifactData.getPosition());
//				if(mountEquip==null && tempList.get(artifactData.getPosition())==null){
//					tempList.set(artifactData.getPosition(), artifact);
//				}else{
//					int fighting=0;
//					if(mountEquip!=null){
//						fighting=GoodsModel.getArtifactDataById(mountEquip.getG()).getFighting();
//					}
//					if(artifactData.getFighting()>fighting){
//						if(tempList.get(artifactData.getPosition())==null){
//							tempList.set(artifactData.getPosition(), artifact);
//						}else{
//							if(artifactData.getFighting()>GoodsModel.getArtifactDataById(tempList.get(artifactData.getPosition()).getG()).getFighting()){
//								tempList.set(artifactData.getPosition(), artifact);
//							}
//						}
//					}
//				}
//			}
//		}
//
//		for(int pos=0;pos<tempList.size();++pos){
//			Artifact artifact=tempList.get(pos);
//			if(artifact!=null){
//				gameRole.getPackManager().useGoods(new DropData(EGoodsType.ARTIFACT,artifact.getD(),1), EGoodsChangeType.MOUNT_EQUIP_CONSUME,enumSet);
//				Artifact addArtifact=character.getMountEquip().get(pos);
//				character.getMountEquip().set(pos, artifact);
//				if(addArtifact!=null){
//					gameRole.getPackManager().addArtifact(addArtifact, EGoodsChangeType.MOUNT_EQUIP_ADD);
//				}
//			}
//		}
//
//		Message message=new Message(MessageCommand.MOUNT_EQUIP_MESSAGE,request.getChannel());
//		message.setByte(idx);
//		character.getMountEquipMessage(message);
//		gameRole.sendMessage(message);
//
//		enumSet.add(EPlayerSaveType.CHA_MOUNTEQUIP);
//		gameRole.saveData(idx, enumSet);
//	}

    private void sendMountUpMessage(byte idx, Character character, Message request) {
//		Message message=new Message(MessageCommand.MOUNT_UP_MESSAGE,request.getChannel());
//		message.setByte(idx);
//		character.getMountMessage(message);
//		gameRole.sendMessage(message);
    }

    /**
     * 381 宠物升级
     *
     * @param request
     */
    public void processPetUp(Message request) {
//		byte id=request.readByte();
//		byte level=1;
//		Pet pet=null;
//		if(player.getPet().containsKey(id)){
//			pet=player.getPet().get(id);
//			level=pet.getLevel();
//			++level;
//		}
//		if(level>LevelDefine.MAX_LEVEL_PET){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_MAX_LEVEL);
//			return;
//		}		
//		PetData petdata=SectionModel.getPetData(id, level);
//		if(petdata==null){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		EnumSet<EPlayerSaveType> enumSet=EnumSet.noneOf(EPlayerSaveType.class);
//		if (!gameRole.getPackManager().useGoods(petdata.getCost(), EGoodsChangeType.PET_UP_CONSUME,enumSet)) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//			return;
//		}
//		if(pet==null){
//			pet=new Pet(level);
//			player.getPet().put(id, pet);
//			//gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.PET_UP,pet.getLevel(),enumSet));
//		}else{
//			if(pet.addExp(id,10)){
//				//gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.PET_UP,pet.getLevel(),enumSet));
//			}
//		}
//		enumSet.add(EPlayerSaveType.PET);
//		
//		Message message=new Message(MessageCommand.PET_UP_MESSAGE,request.getChannel());
//		message.setByte(id);
//		message.setByte(pet.getLevel());
//		message.setShort(pet.getExp());
//		gameRole.sendMessage(message);
//		
//		gameRole.savePlayer(enumSet);
    }

    /**
     * 382 宠物出战
     *
     * @param request
     */
    public void processPetShow(Message request) {
//		byte id=request.readByte();
//		if(id==0 || player.getPet().containsKey(id)){
//			Message message=new Message(MessageCommand.PET_SHOW_MESSAGE,request.getChannel());
//			message.setByte(id);
//			gameRole.sendMessage(message);
//			
//			player.setPetShow(id);
//			gameRole.savePlayer(EPlayerSaveType.PETSHOW);
//		}else{
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//		}
    }

    /**
     * 383宠物升阶
     */
    public void processPetStageUp(Message request) {
//		byte id = request.readByte();
//		Pet pet = player.getPet().get(id);
//		if (pet == null) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		int nextStage = pet.getStage()+1;
//		PetStageData stageModel = SectionModel.getPetStageData(id, nextStage);
//		if (stageModel == null) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_STAGE_MAX);
//			return;
//		}
//		EnumSet<EPlayerSaveType> enumSet = EnumSet.of(EPlayerSaveType.PET);
//		//消耗
//		if (!gameRole.getPackManager().useGoods(stageModel.getCost(), EGoodsChangeType.PET_UP_STAGE_CONSUME, enumSet)) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//			return;
//		}
//		//升阶
//		pet.addStage();
//		
//		Message message = new Message(MessageCommand.PET_STAGE_UP_MESSAGE, request.getChannel());
//		message.setByte(id);
//		message.setByte(pet.getStage());
//		gameRole.sendMessage(message);
//		
//		gameRole.savePlayer(enumSet);
    }

    public void processPetBroadcast(Message request) {
        Message msg = BroadcastService.getAllPetMsg(player);
        msg.setChannel(request.getChannel());
        gameRole.sendMessage(msg);
    }

    /**
     * 神羽装备
     *
     * @param request
     */
    public void processWingEquip(Message request) {
//		byte idx = request.readByte();
//		byte pos = request.readByte();
//		short id = request.readShort();
//
//		Character character = player.getCharacter(idx);
//		if (character == null){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		WingGodModelData modelData = SectionModel.getWingGod(id);
//		if (modelData == null){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		if (modelData.getWingStageLimit() > character.getWing().getMountStage()){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//
//        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
//        DropData cost = new DropData(EGoodsType.WING_GODS, id, 1);
//        if (!gameRole.getPackManager().useGoods(cost, EGoodsChangeType.MOUNT_EQUIP_CONSUME, saves)){
//            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//            return;
//        }
//
//		Wing wing = character.getWing();
//		Short from = wing.getEquipment(modelData.getPos());
//		wing.setEquipment(modelData.getPos(), id);
//
//		saves.add(EPlayerSaveType.CHA_MOUNTEQUIP);
//		if (from != null){
//			DropData dropData = new DropData(EGoodsType.WING_GODS, from, 1);
//			gameRole.getPackManager().addGoods(dropData, EGoodsChangeType.MOUNT_EQUIP_ADD, saves);
//		}
//
//		updateWingGodSkill(wing);
//		gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.WING_GOD_EQUIP, 1, saves));
//
//		gameRole.saveData(idx, saves);
//
//		Message message = new Message(request.getCmdId(), request.getChannel());
//		message.setByte(idx);
//		character.getWing().getMessage(message);
//		gameRole.sendMessage(message);
    }

    private void updateWingGodSkill(Wing wing) {
        byte minLevel = getEquipMinLevel(wing);
        WingMasterModelData masterModelData = SectionModel.getWingMaster(minLevel);
        wing.setMaster(masterModelData == null ? 0 : masterModelData.getId());

        WingSkillModelData skillModelData = SectionModel.getWingSkill(minLevel);
        wing.setSkill(skillModelData == null ? 0 : skillModelData.getId());
    }

    public byte getEquipMinLevel(Wing wing) {
        byte min = 0;
        for (byte position = 1; position <= SectionModel.getPosSize(); position++) {
            Short id = wing.getEquipment(position);
            if (id == null) {
                return 0;
            }
            WingGodModelData wingGodModelData = SectionModel.getWingGod(id);
            if (wingGodModelData == null) {
                logger.error("获取玩家:" + gameRole.getPlayerId() + " 神羽等级错误,找不到神羽数据:" + id);
                break;
            }
            if (min == 0 || min > wingGodModelData.getLevel()) {
                min = wingGodModelData.getLevel();
            }
        }
        return min;
    }

    /**
     * 神羽装备合成
     *
     * @param request
     */
    public void processWingGodCraft(Message request) {
        short id = request.readShort();
        WingGodModelData modelData = SectionModel.getWingGod(id);
        if (modelData == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        if (gameRole.getPackManager().getBagFreeCapacity() <= 0) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_BAG_FULL);
            return;
        }
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        if (!gameRole.getPackManager().useGoods(modelData.getCraftCost(), EGoodsChangeType.WING_GOD_CRAFT_CONSUME, saves)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }
        DropData reward = new DropData(EGoodsType.WING_GODS, id, 1);
        gameRole.getPackManager().addGoods(reward, EGoodsChangeType.WING_GOD_CRAFT_ADD, saves);
        gameRole.savePlayer(saves);

        Message message = new Message(request.getCmdId(), request.getChannel());
        gameRole.sendMessage(message);
    }

    /**
     * 神羽(坐骑)装备快速合成
     *
     * @param request
     */
    public void processWingGodQuickCraft(Message request) {
//		byte idx = request.readByte();
//		byte pos = request.readByte();
//		Character cha = player.getCharacter(idx);
//		if (cha == null) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		Wing wing = cha.getWing();
//		Short oldGod = wing.getEquipment(pos);
//		WingGodModelData oldData = oldGod == null ? null : SectionModel.getWingGod(oldGod);
//		byte nextLv = (byte) (oldData == null ? 1: oldData.getLevel() + 1);
//		WingGodModelData nextData = SectionModel.getWingGod(pos, nextLv);
//		if (nextData == null){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_MAX_LEVEL);
//			return;
//		}
//		if (nextData.getWingStageLimit() >  wing.getMountStage()){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		List<DropData> consumes;
//		if (oldGod != null) {
//			consumes = new ArrayList<>();
//			for (DropData dropData : nextData.getCraftCost()) {
//				if (dropData.getG() == oldGod){
//					consumes.add(new DropData(dropData.getT(), dropData.getG(), dropData.getN()-1));
//				}else{
//					consumes.add(dropData);
//				}
//			}
//		}else{
//			consumes = nextData.getCraftCost();
//		}
//		EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
//		if (!gameRole.getPackManager().useGoods(consumes, EGoodsChangeType.WING_GOD_CRAFT_CONSUME, saves)){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//			return;
//		}
//		wing.addEquipment(pos, nextData.getId());
//
//		saves.add(EPlayerSaveType.CHA_MOUNTEQUIP);
//		updateWingGodSkill(wing);
//		gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.WING_GOD_EQUIP, 1, saves));
//		gameRole.saveData(idx, saves);
//
//		Message message = new Message(request.getCmdId(), request.getChannel());
//		message.setByte(idx);
//		wing.getMessage(message);
//		gameRole.sendMessage(message);
    }

    /**
     * 神羽装备转换
     */
    public void processWingGodConverse(Message request) {
        short from = request.readShort();
        short to = request.readShort();

        WingGodModelData fromData = SectionModel.getWingGod(from);
        WingGodModelData toData = SectionModel.getWingGod(to);
        if (fromData == null
                || toData == null
                || fromData.getLevel() != toData.getLevel()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        List<DropData> consumeList = new ArrayList<>();
        consumeList.add(new DropData(EGoodsType.WING_GODS, from, 1));
        consumeList.addAll(fromData.getConverseCost());

        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        if (!gameRole.getPackManager().useGoods(consumeList, EGoodsChangeType.WING_GOD_CONVERSE_CONSUME, saves)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }

        DropData newData = new DropData(EGoodsType.WING_GODS, to, 1);
        gameRole.getPackManager().addGoods(newData, EGoodsChangeType.WING_GOD_CONVERSE_ADD, saves);

        gameRole.savePlayer(saves);

        Message message = new Message(request.getCmdId(), request.getChannel());
        gameRole.sendMessage(message);
    }

    /**
     * 第一组
     *
     * @param request
     */
    public void processGroup1(Message request) {
        gameRole.putMessageQueue(player.getGoodsListMessage());                //301
        gameRole.putMessageQueue(gameRole.getMailManager().getMailList());    //810
        gameRole.putMessageQueue(ChatService.getChatListMsg());                //820
        gameRole.putMessageQueue(gameRole.getPayManager().getPayRecordMsg());    //140
        gameRole.putMessageQueue(gameRole.getEquipManager().getRedEquipBagMsg());    //729
        gameRole.putMessageQueue(player.getDayRefreshMsg());    //112
        gameRole.putMessageQueue(gameRole.getFunctionManager().getShareInfoMessage());//921
        gameRole.putMessageQueue(gameRole.getActivityManager().getActivityMsg());    //1000
        //gameRole.putMessageQueue(gameRole.getActivityManager().getActivityNewMessage());	//1001
        gameRole.putMessageQueue(gameRole.getMissionManager().getDailyListMessage());            //903
        gameRole.putMessageQueue(gameRole.getMissionManager().getDragonBallMessage()); //940
        gameRole.putMessageQueue(gameRole.getMissionManager().getAchievementMissionMessage());    //960
        gameRole.sendTick(request);
    }

    /**
     * 第二组
     *
     * @param request
     */
    public void processGroup2(Message request) {
        gameRole.putMessageQueue(gameRole.getActivityManager().getMonthlyCardMsg());    //135
        gameRole.putMessageQueue(gameRole.getActivityManager().get7DayMessage());        //1003
        gameRole.putMessageQueue(gameRole.getActivityManager().getWelfareMsg());        //141
        gameRole.putMessageQueue(BroadcastService.getAllXunbaoMsg());                    //727
        gameRole.putMessageQueue(gameRole.getBossManager().getCitCueMsg());                //737
        gameRole.putMessageQueue(gameRole.getTitleManager().getTitleInfoMsg(-1));        //365
        gameRole.putMessageQueue(gameRole.getCardManager().getCardBookMessage());        //3201
        gameRole.sendTick(request);
    }

    /**
     * 第三组
     *
     * @param request
     */
    public void processGroup3(Message request) {
        gameRole.putMessageQueue(gameRole.getSkillManager().getHeartSkillMessage());    //860
        gameRole.putMessageQueue(gameRole.getEscortManager().getEscortDetail());        //710
        gameRole.putMessageQueue(gameRole.getEscortManager().getEscortLogRead());        //718
        gameRole.putMessageQueue(player.getGangSkillList());                            //1120
        gameRole.putMessageQueue(gameRole.getFunctionManager().getRankWorshipList());    //830
        gameRole.sendTick(request);
    }


    public void processLimitTaskTip(Message request) {
        Message message = new Message(MessageCommand.LIMIT_TASK_TIP_MESSAGE, request.getChannel());
        message.setByte(player.getSmallData().getLtTipShow());
        gameRole.sendMessage(message);
    }

    public void processLimitTaskTipShow(Message request) {
        player.getSmallData().setLtTipShow((byte) 1);
        gameRole.sendTick(request);
        EnumSet<EPlayerSaveType> enumSet = EnumSet.of(EPlayerSaveType.SMALLDATA);
        gameRole.savePlayer(enumSet);
    }

    public void processStateRecord(Message request) {
        byte key = request.readByte();
        gameRole.sendTick(request);

        if (!player.getSmallData().getStateR().contains(key)) {
            player.getSmallData().getStateR().add(key);
            EnumSet<EPlayerSaveType> enumSet = EnumSet.of(EPlayerSaveType.SMALLDATA);
            gameRole.savePlayer(enumSet);
        }
    }

    public void processStateSearch(Message request) {
        byte key = request.readByte();
        byte state = 0;
        if (player.getSmallData().getStateR().contains(key)) {
            state = 1;
        }
        Message message = new Message(MessageCommand.STATE_SEARCH_MESSAGE, request.getChannel());
        message.setByte(key);
        message.setByte(state);
        gameRole.sendMessage(message);
    }

    /**
     * 存储经验
     *
     * @param request
     */
    public void processStorageExp(Message request) {
        long totalExp = RoleModel.getMaxExpByLevel(player.getLevel());
        if (totalExp <= player.getExp()) {
            player.setExp(player.getExp() - totalExp);
            player.setLevel((short) (player.getLevel() + 1));

            EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
            enumSet.add(EPlayerSaveType.LEVEL);
            enumSet.add(EPlayerSaveType.EXP);
            gameRole.savePlayer(enumSet);
        }
        gameRole.sendUpdateExpMsg(true);
        gameRole.sendTick(request);
    }
}
