package com.rd.game.manager;

import com.rd.bean.drop.DropData;
import com.rd.bean.dungeon.*;
import com.rd.bean.dungeon.type.FengmoTypeData;
import com.rd.bean.fighter.FighterData;
import com.rd.bean.player.Player;
import com.rd.common.GameCommon;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.*;
import com.rd.game.GameRankManager;
import com.rd.game.GameRole;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.GameEvent;
import com.lg.bean.game.Dungeon;
import com.rd.model.*;
import com.rd.model.data.DungeonGangData;
import com.rd.model.data.DungeonMaterialData;
import com.rd.model.data.GangSweepData;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;
import com.rd.util.DiceUtil;
import com.rd.util.LogUtil;
import com.rd.util.StringUtil;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class DungeonManager {

    private GameRole gameRole;
    private Player player;

    private List<DropData> rewardList = new ArrayList<>();
    private byte fightResult = FightDefine.FIGHT_RESULT_FAIL;

    private byte dungeonId;
    private byte stageId;

    private Map<Byte, com.rd.bean.dungeon.Dungeon> _dungeonMap;

    private long holyDamage;
    private long holyTimeStamp;

    public DungeonManager(GameRole role) {
        this.gameRole = role;
        this.player = role.getPlayer();
    }

    private void init() {
        this._dungeonMap = gameRole.getDbManager().dungeonDao.getDungeonMap(player.getId());
    }

    private Map<Byte, com.rd.bean.dungeon.Dungeon> getDungeonMap() {
        if (null == _dungeonMap) {
            init();
        }
        return _dungeonMap;
    }

    /**
     * 2003 副本请求
     *
     * @param request
     */
    public void processDungeonRquest(Message request) {
        //副本id
        byte id = request.readByte();
        byte stage = id;
        boolean isEnter = true;

        //神器8激活 材料副本每日次数+1
        //int rewardNum=0;
        //if(player.isGodArtifactActive(7)){
        //	rewardNum+=1;
        //}

        DungeonData data = DungeonModel.getDungeonData(id);
        com.rd.bean.dungeon.Dungeon dungeon = gameRole.getDungeonManager().getDungeon(data.getType());
        if (data.getType() == DungeonDefine.DUNGEON_TYPE_BOSS) {
            stage = request.readByte();
            if (stage > this.getPersonalBossOpenStage()) {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
                isEnter = false;
                return;
            }
        }
        DungeonDaily daily = dungeon.getDungeonDaily(stage);
        if (gameRole.getFightManager().inInstance()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_COPY_ALREADY_IN);
            isEnter = false;
            return;
        } else if (player.getLevelWithRein() < data.getLevelLimit()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_LEVEL_LESS);
            isEnter = false;
            return;
        } else if (player.getVipLevel() < data.getVipLimit()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_VIP_LEVEL_LESS);
            isEnter = false;
            return;
        } else if (daily.getAttackTimes() >= data.getFreeTimes(player.getVipLevel())) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_TIMES_LESS);
            isEnter = false;
            return;
        }
        if (data.getType() == DungeonDefine.DUNGEON_TYPE_GANG && player.getGang() == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GANG_NONE);
            isEnter = false;
            return;
        }
        if (id == DungeonDefine.DUNGEON_SUB_TYPE_CUILIAN && DateUtil.getDistanceDay(player.getCreateTime(), System.currentTimeMillis()) < FunctionModel.getCuilianDay() - 1) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            isEnter = false;
            return;
        }

        if (isEnter) {
            if (data.getType() == DungeonDefine.DUNGEON_TYPE_BOSS) {
                //gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.DUNGEON_BOSS_ENTER,1,null));
            } else if (dungeon.getType() == DungeonDefine.DUNGEON_TYPE_MATERIAL) {
                if (DungeonDefine.DUNGEON_SUB_TYPE_GEM == id) {
                    //gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.DUNGEON_GEM_ENTER,1,null));
                } else if (DungeonDefine.DUNGEON_SUB_TYPE_MERIDIAN == id) {
                    //gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.DUNGEON_MERIDIAN_ENTER,1,null));
                } else if (DungeonDefine.DUNGEON_SUB_TYPE_MIRROR == id) {
                    //gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.DUNGEON_MIRROR_ENTER,1,null));
                } else if (DungeonDefine.DUNGEON_SUB_TYPE_FLUTE == id) {
                    //gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.DUNGEON_FLUTE_ENTER,1,null));
                }
                //gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.DUNGEON_METERIAL_ENTER,1,null));
            } else if (dungeon.getType() == DungeonDefine.DUNGEON_TYPE_DEKARON) {
                //gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.DUNGEON_DEKARON_ENTER,1,null));
            }
        }

        Message message = new Message(MessageCommand.FIGHT_DUNGEON_REQUEST_MESSAGE, request.getChannel());
        message.setByte(id);
        message.setBool(isEnter);
        if (data.getType() == DungeonDefine.DUNGEON_TYPE_BOSS) {
            message.setByte(stage);
        } else if (data.getType() == DungeonDefine.DUNGEON_TYPE_GANG) {
            message.setBool(gameRole.getGangManager().isDungeonPass(dungeon.getPass()));
            message.setShort(gameRole.getGangManager().getCheer());
        }
        gameRole.sendMessage(message);

        if (isEnter) {
            this.clearFightDrop();
            player.setMapType(EMapType.DUNGEON);

            this.dungeonId = id;
            this.stageId = stage;
            this.fightResult = FightDefine.FIGHT_RESULT_SUCCESS;

            if (data.getType() == DungeonDefine.DUNGEON_TYPE_BOSS) {
                DungeonBossData dungeonBossData = DungeonModel.getDungeonBossData(stage);
                FighterData fighterData = FighterModel.getFighterDataById(dungeonBossData.getBossId());
                List<DropData> reward = dungeonBossData.getRewards();
                if (reward != null)
                    this.rewardList.addAll(reward);
                for (byte occupation = 0; occupation < GameDefine.OCCUPATION_NUM; ++occupation) {
                    for (int j = 0; j < 4; ++j) {
                        byte equipPos = GameCommon.getRandomEquipPosition();
                        byte quality = (byte) GameCommon.getRandomIndex(dungeonBossData.getQualityChance());
                        short equipLevel;
                        if (j == 0) {
                            equipLevel = GameCommon.getDropEquipLevel((short) (fighterData.getLevel() - 10));
                        } else {
                            equipLevel = GameCommon.getDropEquipLevel(fighterData.getLevel());
                        }
                        DropData equipData = new DropData();
                        equipData.setT(EGoodsType.EQUIP.getId());
                        equipData.setG(GoodsModel.getEquipId(equipLevel, occupation, equipPos));
                        equipData.setQ(quality);
                        equipData.setN(1);
                        this.rewardList.add(equipData);
                    }
                }
            } else if (data.getType() == DungeonDefine.DUNGEON_TYPE_MATERIAL) {
                DungeonMaterialData materialData = DungeonModel.getDungeonMaterialBossDrop(data.getId());
                this.rewardList.addAll(DropModel.getDropGroupData(materialData.getBossDrop()).getRandomDrop());
                this.rewardList.add(materialData.getReward());
            } else if (data.getType() == DungeonDefine.DUNGEON_TYPE_DEKARON) {
                DungeonDekaronData dungeonDekaronData = DungeonModel.getDungeonDekaronData(dungeon.getPass());
                FighterData fighterData = FighterModel.getFighterDataById(dungeonDekaronData.getFightId());
                if (player.getFighting() > fighterData.getPower()) {
                    this.rewardList.addAll(dungeonDekaronData.getDropData());
                } else {
                    this.fightResult = FightDefine.FIGHT_RESULT_FAIL;
                }
            } else if (data.getType() == DungeonDefine.DUNGEON_TYPE_FENGMOTA) {
                DungeonFengmoData fengmotaData = DungeonModel.getFengmoData(dungeon.getPass());
                FighterData fighterData = FighterModel.getFighterDataById(fengmotaData.getFightId());
                if (player.getFighting() > fighterData.getPower()) {
                    this.rewardList.addAll(fengmotaData.getBattleReward());
                } else {
                    this.fightResult = FightDefine.FIGHT_RESULT_FAIL;
                }
            } else if (data.getType() == DungeonDefine.DUNGEON_TYPE_GANG) {
                DungeonGangData dungeonGangData = DungeonModel.getDungeonGangData(dungeon.getPass());
                FighterData fighterData = FighterModel.getFighterDataById(dungeonGangData.getFightId());
                if (player.getFighting() > fighterData.getPower()) {
                    this.rewardList.addAll(dungeonGangData.getRewards());
                } else {
                    this.fightResult = FightDefine.FIGHT_RESULT_FAIL;
                }
            }
        }
    }

    /**
     * 2004 副本战斗结果
     *
     * @param request
     */
    public void processDungeonResult(Message request) {
        //战斗结果 0：失败 1：成功 2：平局
        byte result = request.readByte();
        //客户端如果失败 服务器也算作失败 平局同样
        if (FightDefine.FIGHT_RESULT_FAIL == result || FightDefine.FIGHT_RESULT_TIE == result) {
            this.fightResult = result;
        }
        //如果地图类型不为副本算作失败
        if (EMapType.DUNGEON != player.getMapType()) {
            this.fightResult = FightDefine.FIGHT_RESULT_FAIL;
        }
        //服务器验证失败
        if (FightDefine.FIGHT_RESULT_FAIL == this.fightResult && FightDefine.FIGHT_RESULT_SUCCESS == result) {
            gameRole.putErrorMessage(ErrorDefine.ERROR_SERVER_FIGHT_FAIL);
        } else {
            this.fightResult = result;
        }

        Message message = new Message(MessageCommand.FIGHT_DUNGEON_RESULT_MESSAGE, request.getChannel());
        message.setByte(this.fightResult);
        if (FightDefine.FIGHT_RESULT_SUCCESS == this.fightResult) {
            EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);

            DungeonData data = DungeonModel.getDungeonData(this.dungeonId);
            com.rd.bean.dungeon.Dungeon dungeon = gameRole.getDungeonManager().getDungeon(data.getType());
            DungeonDaily daily = dungeon.getDungeonDaily(this.stageId);
            daily.addAttackTimes();
            if (DungeonDefine.DUNGEON_TYPE_DEKARON == dungeon.getType()) {
                dungeon.addPass();
                player.setDekaron(dungeon.getPass());
                enumSet.add(EPlayerSaveType.DEKARON);
                gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.DUNGEON_DEKARON_PASS, dungeon.getPass() - 1, enumSet));
                //诛仙台排行榜
                GameRankManager.getInstance().resetTopDekaron(player);
                gameRole.getFunctionManager().sendGameRankSimpleTopMsg(FunctionManager.RANK_SIMPLE_TYPE_DEKARON);
            } else if (DungeonDefine.DUNGEON_TYPE_FENGMOTA == dungeon.getType()) {
                dungeon.addPass();
                player.setFengmota(dungeon.getPass());
                enumSet.add(EPlayerSaveType.FENGMOTA);
                gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.DUNGEON_FENGMOTA_PASS, dungeon.getPass() - 1, enumSet));
                //封魔塔排行榜
                GameRankManager.getInstance().resetTopFengmota(player);
                gameRole.getFunctionManager().sendGameRankSimpleTopMsg(FunctionManager.RANK_SIMPLE_TYPE_FENGMOTA);
            } else if (data.getType() == DungeonDefine.DUNGEON_TYPE_GANG) {
                dungeon.addPass();
                gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.DUNGEON_GANG_PASS, dungeon.getPass() - 1, enumSet));
            } else if (data.getType() == DungeonDefine.DUNGEON_TYPE_MATERIAL) {
                if (DungeonDefine.DUNGEON_SUB_TYPE_MERIDIAN == this.dungeonId) {
                    gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.DUNGEON_MERIDIAN_CLEARANCE, 1, enumSet));
                } else if (DungeonDefine.DUNGEON_SUB_TYPE_MIRROR == this.dungeonId) {
                    gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.DUNGEON_VEINS_CLEARANCE, 1, null));
                } else if (DungeonDefine.DUNGEON_SUB_TYPE_FLUTE == this.dungeonId) {
                    gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.DUNGEON_SQUAMA_CLEARANCE, 1, null));
                } else if (DungeonDefine.DUNGEON_SUB_TYPE_CUILIAN == this.dungeonId) {
                    gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.DUNGEON_CUILIAN_CLEARANCE, 1, null));
                } else if (DungeonDefine.DUNGEON_SUB_TYPE_WING == this.dungeonId) {
                    gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.DUNGEON_WING_CLEARANCE, 1, null));
                } else if (DungeonDefine.DUNGEON_SUB_TYPE_GEM == this.dungeonId) {
                    gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.DUNGEON_GEM_CLEARANCE, 1, null));
                }
                gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.DUNGEON_METERIAL_ENTER, 1, null));
                dungeon.getPassed().add(this.stageId);
            } else if (data.getType() == DungeonDefine.DUNGEON_TYPE_BOSS) {
                gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.DUNGEON_BOSS_SUCC, 1, enumSet));
                if (this.stageId == 9) {
                    gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.KILL_A_REIN_BOSS_TOTAL, 1, enumSet));
                }
                gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.KILL_PERSONAL_BOSS_TOTAL, 1, enumSet));
                enumSet.add(EPlayerSaveType.SMALLDATA);
                dungeon.getPassed().add(this.stageId);
            }

            if (data.getType() == DungeonDefine.DUNGEON_TYPE_BOSS) {
                message.setShort(this.stageId);
                //gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.DUNGEON_BOSS_ENTER, 1,enumSet));
            } else {
                message.setShort(dungeon.getPass());
            }
            message.setByte(this.rewardList.size());
            for (DropData dropData : this.rewardList) {
                dropData.getMessage(message);
            }

            gameRole.getDbManager().dungeonDao.updateDungeon(dungeon);

            this.addDropToRole(EGoodsChangeType.FIGHT_DUNGEON_ADD, enumSet);
            gameRole.getDbManager().playerDao.savePlayer(player, enumSet);

            //记录玩家副本日志
            LogUtil.log(player, new Dungeon(data.getType(), daily.getAttackTimes(), dungeon.getPass()));
        } else {
            this.clearFightDrop();
        }
        player.setMapType(EMapType.FIELD_NORMAL);
        gameRole.sendMessage(message);
    }

    /**
     * 添加掉落到人 身上
     */
    public void addDropToRole(EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        gameRole.getPackManager().addGoods(rewardList, changeType, enumSet);
        clearFightDrop();
    }

    /**
     * 清除战斗掉落
     */
    private void clearFightDrop() {
        fightResult = FightDefine.FIGHT_RESULT_FAIL;
        rewardList.clear();
    }

    public void reset() {
        for (com.rd.bean.dungeon.Dungeon dungeon : getDungeonMap().values()) {
            dungeon.reset();
        }
    }

    public com.rd.bean.dungeon.Dungeon getDungeon(byte type) {
        if (this.getDungeonMap().containsKey(type)) {
            return this.getDungeonMap().get(type);
        } else {
            com.rd.bean.dungeon.Dungeon dungeon = new com.rd.bean.dungeon.Dungeon();
            dungeon.setPlayerId(player.getId());
            dungeon.setType(type);
            synchronized (getDungeonMap()) {
                getDungeonMap().put(type, dungeon);
            }
            gameRole.getDbManager().dungeonDao.insertDungeon(dungeon);
            return dungeon;
        }
    }

    /**
     * 副本状态
     *
     * @param request
     */
    public void processDungeonState(Message request) {
        byte type = request.readByte();
        List<Byte> data = DungeonModel.getDungeonIdList(type);
        com.rd.bean.dungeon.Dungeon dungeon = getDungeon(type);

        Message message = new Message(MessageCommand.DUNGEON_STATE_MESSAGE, request.getChannel());
        message.setByte(dungeon.getType());
        message.setShort(dungeon.getPass());
        if (type == DungeonDefine.DUNGEON_TYPE_BOSS) {
            message.setByte(dungeon.getDailyTimes().size());
            for (Map.Entry<Byte, DungeonDaily> entry : dungeon.getDailyTimes().entrySet()) {
                message.setByte(entry.getKey());
                entry.getValue().getMessage(message);
            }
            message.setByte(dungeon.getPassed().size());
            for (byte passed : dungeon.getPassed()) {
                message.setByte(passed);
            }
        } else {
            message.setByte(data.size());
            for (byte sub : data) {
                message.setByte(sub);
                dungeon.getDungeonDaily(sub).getMessage(message);
            }
            if (type == DungeonDefine.DUNGEON_TYPE_MATERIAL) {
                message.setByte(dungeon.getPassed().size());
                for (byte passed : dungeon.getPassed()) {
                    message.setByte(passed);
                }
            }
        }
        //message.setByte(dungeon.getSweep());
        gameRole.sendMessage(message);
    }

    public void processDungeonBuy(Message request) {
        byte id = request.readByte();

        DungeonData data = DungeonModel.getDungeonData(id);
        com.rd.bean.dungeon.Dungeon dungeon = gameRole.getDungeonManager().getDungeon(data.getType());
        DungeonDaily daily = dungeon.getDungeonDaily(id);

        int times = data.getBuyTimes(player.getVipLevel());
        if (daily.getBuyTimes() < times) {
            EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
            if (data.getType() == DungeonDefine.DUNGEON_TYPE_GANG) {
                daily.addBuyTimes();

                int mountEquipSize = 0;//dungeon.getPass()/5;
//				//20%概率掉坐骑装备
//				List<Short> list=new ArrayList<>();
//				for(int i=1;i<=mountEquipSize;++i){
//					int stage=(i*5)/16+1;
//					ArtifactData artifactData=GoodsModel.getArtifactData(stage, GameCommon.getRandomIndex(GoodsModel.getMountPosSize()), GameCommon.getRandomIndex(0,7000,3000));
//					gameRole.getPackManager().addGoods(new DropData(EGoodsType.ARTIFACT,artifactData.getGoodsId(),1), EGoodsChangeType.DUNGEON_SWEEP_ADD, enumSet);
//					list.add(artifactData.getGoodsId());
//				}
                //扫荡奖励
                List<DropData> rewardList = new ArrayList<>();
                List<Integer> rewardIdList = new ArrayList<>();
                for (int i = 0; i < dungeon.getPass(); i++) {
                    int rewardId = DiceUtil.dice(GangModel.getGangSweepRewardList()).getId();
                    GangSweepData gsd = GangModel.getGangSweepRewardMap().get((short) rewardId);
                    List<DropData> list = gsd.getRewards();
                    for (DropData dd : list) {
                        rewardList.add(dd);
                    }
                    rewardIdList.add(rewardId);
                }
                rewardList = StringUtil.getDropDataSum(rewardList);
                gameRole.getPackManager().addGoods(rewardList, EGoodsChangeType.DUNGEON_SWEEP_ADD, enumSet);
                //26000~31000
//				int gold=(int)(26000+Math.random()*5000);
//				gold*=dungeon.getPass()-1;
//				gameRole.getPackManager().addGoods(new DropData(EGoodsType.GOLD,0,gold), EGoodsChangeType.DUNGEON_SWEEP_ADD, enumSet);

                //派发帮派副本通关消息
                gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.DUNGEON_GANG_PASS, dungeon.getPass() - 1, enumSet));

                Message message = new Message(MessageCommand.DUNGEON_BUY_MESSAGE, request.getChannel());
                message.setByte(id);
                message.setByte(daily.getBuyTimes());
//				message.setByte(mountEquipSize);
//				for(short eid:list){
//					message.setShort(eid);
//				}
//				message.setInt(0);
                //奖励列表
                message.setByte(rewardList.size());
                for (DropData dd : rewardList) {
                    message.setByte(dd.getT());//类型
                    message.setShort(dd.getG());//goodId
                    message.setByte(dd.getQ());//品质
                    message.setInt(dd.getN());//数量
                }
                gameRole.sendMessage(message);

                gameRole.getDbManager().playerDao.savePlayer(player, enumSet);
                gameRole.getDbManager().dungeonDao.updateDungeon(dungeon);
            } else {
                int cost = data.getPrice() + data.getPriceAdd() * daily.getBuyTimes();
                if (gameRole.getPackManager().costCurrency(EGoodsType.DIAMOND, cost, EGoodsChangeType.DUNGEON_SWEEP_CONSUME, enumSet)) {
                    daily.addBuyTimes();

                    if (dungeon.getType() == DungeonDefine.DUNGEON_TYPE_MATERIAL) {
                        //gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.DUNGEON_MATERIAL_PASS, 1,enumSet));
                    }

                    Message message = new Message(MessageCommand.DUNGEON_BUY_MESSAGE, request.getChannel());
                    message.setByte(id);
                    message.setByte(daily.getBuyTimes());
                    gameRole.sendMessage(message);

                    gameRole.getDbManager().playerDao.savePlayer(player, enumSet);
                    gameRole.getDbManager().dungeonDao.updateDungeon(dungeon);
                } else {
                    gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
                }
            }
        } else {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_MAX_BUY);
        }
    }

    public void processDungeonSweep(Message request) {
        byte id = request.readByte();
        DungeonData data = DungeonModel.getDungeonData(id);
        if (data.getType() == DungeonDefine.DUNGEON_TYPE_MATERIAL) {
            com.rd.bean.dungeon.Dungeon dungeon = gameRole.getDungeonManager().getDungeon(data.getType());
            DungeonDaily daily = dungeon.getDungeonDaily(id);
            if (daily.getBuyTimes() < data.getBuyTimes(player.getVipLevel())) {
                EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
                if (!gameRole.getPackManager().costCurrency(EGoodsType.DIAMOND, data.getPrice(), EGoodsChangeType.DUNGEON_SWEEP_CONSUME, enumSet)) {
                    gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
                    return;
                }
                daily.addBuyTimes();

                DungeonMaterialData materialData = DungeonModel.getDungeonMaterialBossDrop(data.getId());
                List<DropData> dropDataList = DropModel.getDropGroupData(materialData.getBossDrop()).getRandomDrop();
                dropDataList.add(materialData.getReward());
                gameRole.getPackManager().addGoods(dropDataList, EGoodsChangeType.DUNGEON_SWEEP_ADD, enumSet);

                if (data.getType() == DungeonDefine.DUNGEON_TYPE_MATERIAL) {
                    if (DungeonDefine.DUNGEON_SUB_TYPE_MERIDIAN == data.getId()) {
                        gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.DUNGEON_MERIDIAN_CLEARANCE, 1, enumSet));
                    } else if (DungeonDefine.DUNGEON_SUB_TYPE_MIRROR == data.getId()) {
                        gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.DUNGEON_VEINS_CLEARANCE, 1, null));
                    } else if (DungeonDefine.DUNGEON_SUB_TYPE_FLUTE == data.getId()) {
                        gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.DUNGEON_SQUAMA_CLEARANCE, 1, null));
                    } else if (DungeonDefine.DUNGEON_SUB_TYPE_CUILIAN == data.getId()) {
                        gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.DUNGEON_CUILIAN_CLEARANCE, 1, null));
                    } else if (DungeonDefine.DUNGEON_SUB_TYPE_WING == data.getId()) {
                        gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.DUNGEON_WING_CLEARANCE, 1, null));
                    } else if (DungeonDefine.DUNGEON_SUB_TYPE_GEM == data.getId()) {
                        gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.DUNGEON_GEM_CLEARANCE, 1, null));
                    }
                    gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.DUNGEON_METERIAL_ENTER, 1, enumSet));
                }

                Message message = new Message(MessageCommand.DUNGEON_SWEEP_MESSAGE, request.getChannel());
                message.setByte(id);
                message.setByte(daily.getBuyTimes());
                gameRole.sendMessage(message);

                gameRole.getDbManager().playerDao.savePlayer(player, enumSet);
                gameRole.getDbManager().dungeonDao.updateDungeon(dungeon);
            } else {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_NUM_MAX);
            }
        } else {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
        }
    }

    /**
     * 2008 材料副本一键扫荡
     *
     * @param request
     */
    public void processMaterialOneKey(Message request) {
        if (player.getVipLevel() < 3) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_VIP_LEVEL_LESS);
            return;
        }

        //副本id
        byte id = request.readByte();

        DungeonData data = DungeonModel.getDungeonData(id);
        if (data.getType() != DungeonDefine.DUNGEON_TYPE_MATERIAL) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        com.rd.bean.dungeon.Dungeon dungeon = gameRole.getDungeonManager().getDungeon(data.getType());
        DungeonDaily daily = dungeon.getDungeonDaily(id);
        if (!dungeon.getPassed().contains(id)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_NOT_PASS);
            return;
        }
        if (gameRole.getFightManager().inInstance()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_COPY_ALREADY_IN);
            return;
        } else if (player.getLevelWithRein() < data.getLevelLimit()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_LEVEL_LESS);
            return;
        } else if (player.getVipLevel() < data.getVipLimit()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_VIP_LEVEL_LESS);
            return;
        } else if (daily.getAttackTimes() >= data.getFreeTimes(player.getVipLevel())) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_TIMES_LESS);
            return;
        }

        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        DungeonMaterialData materialData = DungeonModel.getDungeonMaterialBossDrop(data.getId());
        List<DropData> dropDataList = DropModel.getDropGroupData(materialData.getBossDrop()).getRandomDrop();
        dropDataList.add(materialData.getReward());
        gameRole.getPackManager().addGoods(dropDataList, EGoodsChangeType.DUNGEON_SWEEP_ADD, enumSet);

        Message message = new Message(MessageCommand.DUNGEON_MATERIAL_ONEKEY_MESSAGE, request.getChannel());
        message.setByte(id);
        gameRole.sendMessage(message);

        daily.addAttackTimes();
        gameRole.getDbManager().playerDao.savePlayer(player, enumSet);
        gameRole.getDbManager().dungeonDao.updateDungeon(dungeon);
    }

    /**
     * 2009 个人boss副本一键扫荡
     *
     * @param request
     */
    public void processPersonalBossOneKey(Message request) {
        if (player.getVipLevel() < 4) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_VIP_LEVEL_LESS);
            return;
        }

        //副本id
        byte id = request.readByte();

        DungeonData data = DungeonModel.getDungeonData(DungeonDefine.DUNGEON_TYPE_BOSS);
        com.rd.bean.dungeon.Dungeon dungeon = gameRole.getDungeonManager().getDungeon(DungeonDefine.DUNGEON_TYPE_BOSS);
        DungeonDaily daily = dungeon.getDungeonDaily(id);
        if (daily == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        if (!dungeon.getPassed().contains(id)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_NOT_PASS);
            return;
        }
        if (gameRole.getFightManager().inInstance()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_COPY_ALREADY_IN);
            return;
        } else if (player.getLevelWithRein() < data.getLevelLimit()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_LEVEL_LESS);
            return;
        } else if (player.getVipLevel() < data.getVipLimit()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_VIP_LEVEL_LESS);
            return;
        } else if (daily.getAttackTimes() >= data.getFreeTimes(player.getVipLevel())) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_TIMES_LESS);
            return;
        }

        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        DungeonBossData dungeonBossData = DungeonModel.getDungeonBossData(id);
        FighterData fighterData = FighterModel.getFighterDataById(dungeonBossData.getBossId());
        List<DropData> reward = dungeonBossData.getRewards();
        if (reward != null)
            reward = new ArrayList<DropData>();
        for (byte occupation = 0; occupation < GameDefine.OCCUPATION_NUM; ++occupation) {
            for (int j = 0; j < 4; ++j) {
                byte equipPos = GameCommon.getRandomEquipPosition();
                byte quality = (byte) GameCommon.getRandomIndex(dungeonBossData.getQualityChance());
                short equipLevel;
                if (j == 0) {
                    equipLevel = GameCommon.getDropEquipLevel((short) (fighterData.getLevel() - 10));
                } else {
                    equipLevel = GameCommon.getDropEquipLevel(fighterData.getLevel());
                }
                DropData equipData = new DropData();
                equipData.setT(EGoodsType.EQUIP.getId());
                equipData.setG(GoodsModel.getEquipId(equipLevel, occupation, equipPos));
                equipData.setQ(quality);
                equipData.setN(1);
                reward.add(equipData);
            }
        }

        gameRole.getPackManager().addGoods(reward, EGoodsChangeType.DUNGEON_SWEEP_ADD, enumSet);

        Message message = new Message(MessageCommand.DUNGEON_PERSONALBOSS_ONEKEY_MESSAGE, request.getChannel());
        message.setByte(id);
        gameRole.sendMessage(message);

        daily.addAttackTimes();
        gameRole.getDbManager().playerDao.savePlayer(player, enumSet);
        gameRole.getDbManager().dungeonDao.updateDungeon(dungeon);
    }

    private int getPersonalBossOpenStage() {
        if (player.getRein() > 0) {
            return player.getRein() + 8;
        }
        if (player.getLevel() > 80) {
            return 8;
        }
        return player.getLevel() / 10;
    }

    public DungeonDaily getDungeonDaily(byte dungeonId) {
        DungeonData data = DungeonModel.getDungeonData(dungeonId);
        com.rd.bean.dungeon.Dungeon dungeon = gameRole.getDungeonManager().getDungeon(data.getType());
        DungeonDaily daily = dungeon.getDungeonDaily(dungeonId);
        return daily;
    }

    public short getDungeonGangPass() {
        return (short) (gameRole.getDungeonManager().getDungeon(DungeonDefine.DUNGEON_TYPE_GANG).getPass() - 1);
    }

    /**
     * 封魔塔每日状态
     */
    public void processFengmoDailyState(Message request) {
        com.rd.bean.dungeon.Dungeon dungeon = getDungeon(DungeonDefine.DUNGEON_TYPE_FENGMOTA);
        DungeonFengmoData modelData = DungeonModel.getFengmoData(dungeon.getPass());
        if (modelData == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        FengmoTypeData typeData = dungeon.getTypeData();
        boolean isReceived = typeData.isReceived();
        Message message = new Message(MessageCommand.DUNGEON_FENGMO_DAILY_STATE_MESSAGE, request.getChannel());
        message.setBool(isReceived);
        gameRole.sendMessage(message);
    }

    /**
     * 封魔塔每日奖励领取
     */
    public void processFengmoDailyReceive(Message request) {
        com.rd.bean.dungeon.Dungeon dungeon = getDungeon(DungeonDefine.DUNGEON_TYPE_FENGMOTA);
        DungeonFengmoData modelData = DungeonModel.getFengmoData(dungeon.getPass());
        if (modelData == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        FengmoTypeData typeData = dungeon.getTypeData();
        if (typeData.isReceived()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        typeData.setReceived(true);
        gameRole.getDbManager().dungeonDao.updateDungeon(dungeon);

        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        gameRole.getPackManager().addGoods(modelData.getDailyReward(), EGoodsChangeType.DUNGEON_FENGMO_DAILY_ADD, enumSet);

        Message message = new Message(MessageCommand.DUNGEON_FENGMO_DAILY_RECEIVE_MESSAGE, request.getChannel());
        gameRole.sendMessage(message);
    }

    /**
     * 圣物副本状态 2020
     *
     * @param request
     */
    public void processHolyState(Message request) {
        com.rd.bean.dungeon.Dungeon dungeon = getDungeon(DungeonDefine.DUNGEON_TYPE_HOLY);

        Message message = new Message(MessageCommand.DUNGEON_HOLY_STATE_MESSAGE, request.getChannel());
        dungeon.getDailyTimes().get(dungeon.getType()).getMessage(message);
        gameRole.sendMessage(message);
    }

    /**
     * 圣物副本请求战斗 2021
     *
     * @param request
     */
    public void processHolyFightRequest(Message request) {
        DungeonData data = DungeonModel.getDungeonData(DungeonDefine.DUNGEON_TYPE_HOLY);
        com.rd.bean.dungeon.Dungeon dungeon = this.getDungeon(DungeonDefine.DUNGEON_TYPE_HOLY);
        DungeonDaily daily = dungeon.getDungeonDaily(dungeon.getType());
        if (gameRole.getFightManager().inInstance()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_COPY_ALREADY_IN);
            return;
        } else if (player.getLevelWithRein() < data.getLevelLimit()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_LEVEL_LESS);
            return;
        } else if (player.getVipLevel() < data.getVipLimit()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_VIP_LEVEL_LESS);
            return;
        } else if (daily.getAttackTimes() >= data.getFreeTimes(player.getVipLevel())) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_TIMES_LESS);
            return;
        }

        Message message = new Message(MessageCommand.DUNGEON_HOLY_FIGHT_REQUEST_MESSAGE, request.getChannel());
        daily.getMessage(message);
        gameRole.sendMessage(message);

        this.clearFightDrop();
        player.setMapType(EMapType.DUNGEON);

        this.holyDamage = 0;
        this.holyTimeStamp = System.currentTimeMillis();
        this.fightResult = FightDefine.FIGHT_RESULT_SUCCESS;
    }

    /**
     * 圣物副本战斗通关 2022
     *
     * @param request
     */
    public void processHolyFightPass(Message request) {
        if (FightDefine.FIGHT_RESULT_SUCCESS == this.fightResult) {
            long dis = System.currentTimeMillis() - this.holyTimeStamp;
            if (dis > DungeonDefine.HOLY_FIGHT_TIME) {
                this.fightResult = FightDefine.FIGHT_RESULT_FAIL;
            } else {
                this.holyDamage += 100;
            }
        }

        Message message = new Message(MessageCommand.DUNGEON_HOLY_FIGHT_RESULT_MESSAGE, request.getChannel());
        message.setLong(this.holyDamage);
        gameRole.sendMessage(message);
    }

    /**
     * 圣物副本战斗结束 2023
     *
     * @param request
     */
    public void processHolyFightResult(Message request) {
        //战斗结果 0：失败 1：成功 2：平局
//		byte result=request.readByte();
        //客户端如果失败 服务器也算作失败 平局同样
//		if(FightDefine.FIGHT_RESULT_FAIL==result || FightDefine.FIGHT_RESULT_TIE==result){
//			this.fightResult=result;
//		}
        //如果地图类型不为副本算作失败
        if (EMapType.DUNGEON != player.getMapType()) {
            this.fightResult = FightDefine.FIGHT_RESULT_FAIL;
        }
        //服务器验证失败
//		if(FightDefine.FIGHT_RESULT_FAIL==this.fightResult && FightDefine.FIGHT_RESULT_SUCCESS==result){
//			gameRole.putErrorMessage(ErrorDefine.ERROR_SERVER_FIGHT_FAIL);
//		}else{
//			this.fightResult = result;
//		}

        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);

        Message message = new Message(MessageCommand.DUNGEON_HOLY_FIGHT_RESULT_MESSAGE, request.getChannel());
//		message.setByte(this.fightResult);
//		if(FightDefine.FIGHT_RESULT_SUCCESS==this.fightResult){
        com.rd.bean.dungeon.Dungeon dungeon = gameRole.getDungeonManager().getDungeon(DungeonDefine.DUNGEON_TYPE_HOLY);
        DungeonDaily daily = dungeon.getDungeonDaily(DungeonDefine.DUNGEON_TYPE_HOLY);
        daily.addAttackTimes();
        daily.addBuyTimes();

        daily.getMessage(message);
        message.setByte(this.rewardList.size());
        for (DropData dropData : this.rewardList) {
            dropData.getMessage(message);
        }

        this.addDropToRole(EGoodsChangeType.FIGHT_DUNGEON_ADD, enumSet);

        //记录玩家副本日志
        LogUtil.log(player, new Dungeon(DungeonDefine.DUNGEON_TYPE_HOLY, daily.getAttackTimes(), dungeon.getPass()));
//		}else{
//			this.clearFightDrop();
//		}

        player.setMapType(EMapType.FIELD_NORMAL);
        gameRole.sendMessage(message);

        gameRole.savePlayer(enumSet);
//		if(null!=dungeon){
        gameRole.getDbManager().dungeonDao.updateDungeon(dungeon);
//		}
    }

    /**
     * 圣物副本领取宝箱 2024
     *
     * @param request
     */
    public void processHolyReceive(Message request) {
        com.rd.bean.dungeon.Dungeon dungeon = gameRole.getDungeonManager().getDungeon(DungeonDefine.DUNGEON_TYPE_HOLY);
        DungeonDaily daily = dungeon.getDungeonDaily(DungeonDefine.DUNGEON_TYPE_HOLY);
        if (1 != daily.getBuyTimes()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);

        //if(!DiamondCmd.gi().consume(gameRole, data, EGoodsChangeType.DUNGEON_HOLY_BOX_ADD, enumSet)){
        //	gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
        //	return;
        //}
        //gameRole.getPackManager().addGoods(data, EGoodsChangeType.DUNGEON_HOLY_BOX_ADD, enumSet);

        daily.addBuyTimes();

        Message message = new Message(MessageCommand.DUNGEON_HOLY_RECEIVE_MESSAGE);
        gameRole.sendMessage(message);

        gameRole.savePlayer(enumSet);
        gameRole.getDbManager().dungeonDao.updateDungeon(dungeon);
    }
}
