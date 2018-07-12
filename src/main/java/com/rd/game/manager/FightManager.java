package com.rd.game.manager;

import com.rd.bean.drop.DropData;
import com.rd.bean.fighter.FighterData;
import com.rd.bean.map.MapData;
import com.rd.bean.player.Player;
import com.rd.common.GameCommon;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.*;
import com.rd.game.GameRankManager;
import com.rd.game.GameRole;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.GameEvent;
import com.rd.model.FighterModel;
import com.rd.model.GoodsModel;
import com.rd.model.MapModel;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class FightManager {

    private GameRole gameRole;
    private Player player;

    private List<DropData> dropDataList = new ArrayList<>();
    private DropData dropExp = new DropData(EGoodsType.EXP.getId(), 0, 0);
    private byte fightResult = FightDefine.FIGHT_RESULT_FAIL;
    private EnumSet<EPlayerSaveType> saveEnumSet = EnumSet.noneOf(EPlayerSaveType.class);
    private int fieldMonsterCount = 0;

    public FightManager(GameRole gameRole) {
        this.gameRole = gameRole;
        this.player = gameRole.getPlayer();
    }

    /**
     * 2001 请求战斗
     *
     * @param request
     */
    public void processFightRequest(Message request) {
        //0:挂机小怪 1:挂机Boss
        byte type = request.readByte();

        boolean isEnter = false;
        if (!this.inInstance()) {
            isEnter = true;
        }
        //请求战斗间隔验证
        long currTime = System.currentTimeMillis();
//		if(currTime-player.getFightRequestTime()<50){
//			gameRole.sendTick(request);
//			return;
//		}
        //时间间隔验证
        player.setFightRequestTime(currTime);

        MapData mapData = MapModel.getMapDataById(player.getMapId());
        //波数验证
        if (type == EMapType.FIELD_BOSS.ordinal() && mapData.getId() != FightDefine.MAP_BIRTH_ID) {
            //未达到打boss条件，随机打小怪
            if (player.getMapWave() < FightDefine.MAP_WAVE_NUM) {
                type = (byte) EMapType.FIELD_NORMAL.ordinal();
            }
        }

        Message message = new Message(MessageCommand.FIGHT_REQUEST_MESSAGE, request.getChannel());
        message.setBool(isEnter);
        message.setByte(type);
        gameRole.sendMessage(message);

        if (isEnter) {
            this.clearFightDrop();

            if (EMapType.FIELD_BOSS.ordinal() == type) {
                player.setMapType(EMapType.FIELD_BOSS);

                FighterData fighterData = FighterModel.getFighterDataById(MapModel.getMapStageBossId(player.getMapStageId()));
                if (player.getFighting() > fighterData.getPower()) {
                    this.fightResult = FightDefine.FIGHT_RESULT_SUCCESS;
                } else {
                    this.fightResult = FightDefine.FIGHT_RESULT_FAIL;
                }
                if (FightDefine.FIGHT_RESULT_SUCCESS == this.fightResult) {
                    //1.boss经验奖励
                    float addExp = 1;
                    //神器5激活 挂机经验获取提高5%
                    if (player.isGodArtifactActive(5)) {
                        addExp += 0.05f;
                    }
                    //关卡神器12 挂机经验增加5% TODO 效果
                    if (player.getArtifactBoss().getId() == 12) {
                        addExp += 0.05f;
                    }
                    this.dropExp.setN((int) (addExp * MapModel.getMapStageRewardDataById(player.getMapStageId()).getBossExp()));
                    //2.boss金币奖励
                    float addGold = 1;
                    //神器4激活 挂机金币获取提高5%
                    if (player.isGodArtifactActive(4)) {
                        addGold += 0.05f;
                    }
                    //神器6激活 挂机金币获取提高5%
                    if (player.isGodArtifactActive(6)) {
                        addGold += 0.05f;
                    }
                    //关卡神器11 挂机金币增加5%
                    if (player.getArtifactBoss().getId() == 11) {
                        addGold += 0.05f;
                    }

                    this.dropDataList.add(new DropData(EGoodsType.GOLD, 0, (int) (addGold * MapModel.getMapStageRewardDataById(player.getMapStageId()).getBossGold())));
                    this.dropDataList.addAll(MapModel.getMapStageRewardDataById(player.getMapStageId()).getRewardList());
                    //3.装备掉落 三职业每职业2~3件
                    //			白	绿 	蓝	紫
                    // 1-30级	 0%	40% 30%	30%
                    //31-60级	10%	40%	40%	10%
                    //60+级		40%	50%	10%	 0%
                    for (byte occupation = 0; occupation < GameDefine.OCCUPATION_NUM; ++occupation) {
                        int num = (int) (Math.random() * 2 + 2);
                        for (int i = 0; i < num; ++i) {
                            byte equipPos = GameCommon.getRandomEquipPosition();
                            short equipLevel = GameCommon.getDropEquipLevel(fighterData.getLevel());
                            byte quality = 0;
                            if (fighterData.getLevel() < 31) {
                                quality = (byte) GameCommon.getRandomIndex(0, 4000, 3000, 3000);
                            } else if (fighterData.getLevel() < 61) {
                                quality = (byte) GameCommon.getRandomIndex(1000, 4000, 4000, 1000);
                            } else {
                                quality = (byte) GameCommon.getRandomIndex(4000, 5000, 1000);
                            }
                            DropData dropData = new DropData();
                            dropData.setT(EGoodsType.EQUIP.getId());
                            dropData.setG(GoodsModel.getEquipId(equipLevel, occupation, equipPos));
                            dropData.setQ(quality);
                            dropData.setN(1);
                            this.dropDataList.add(dropData);
                        }
                    }
                    //4.野外boss材料掉落	30%
                    //宝石碎片、经脉丹、玉笛碎片、铜镜碎片中任意掉落一个
                    if (GameCommon.isWinPercent(30)) {
                        this.dropDataList.add(new DropData(EGoodsType.ITEM, GoodsDefine.FIELD_BOSS_MATERIAL_DROP[GameCommon.getRandomIndex(4)], 1));
                    }
                    //5.固定给10元宝
                    this.dropDataList.add(new DropData(EGoodsType.DIAMOND, 0, 100));

                    if (player.getMapStageId() == 1) {
                        byte quality = 3;

                        DropData dropData = new DropData();
                        dropData.setT(EGoodsType.EQUIP.getId());
                        dropData.setG((short) 1);
                        dropData.setQ(quality);
                        dropData.setN(1);
                        this.dropDataList.add(dropData);
                        dropData = new DropData();
                        dropData.setT(EGoodsType.EQUIP.getId());
                        dropData.setG((short) 127);
                        dropData.setQ(quality);
                        dropData.setN(1);
                        this.dropDataList.add(dropData);
                        dropData = new DropData();
                        dropData.setT(EGoodsType.EQUIP.getId());
                        dropData.setG((short) 253);
                        dropData.setQ(quality);
                        dropData.setN(1);
                        this.dropDataList.add(dropData);
                    }
                }
            } else {
                player.setMapType(EMapType.FIELD_NORMAL);

                this.fightResult = FightDefine.FIGHT_RESULT_SUCCESS;

                //1.小怪经验奖励
                float addExp = 1;
                //神器5激活 挂机经验获取提高5%
                if (player.isGodArtifactActive(5)) {
                    addExp += 0.05f;
                }
                this.dropExp.setN((int) (addExp * MapModel.getMapStageRewardDataById(player.getMapStageId()).getMonsterExp()));
                //2.小怪金币奖励
                float addGold = 1;
                //神器4激活 挂机金币获取提高5%
                if (player.isGodArtifactActive(4)) {
                    addGold += 0.05f;
                }
                //神器6激活 挂机金币获取提高5%
                if (player.isGodArtifactActive(6)) {
                    addGold += 0.05f;
                }
                this.dropDataList.add(new DropData(EGoodsType.GOLD, 0, (int) (addGold * MapModel.getMapStageRewardDataById(player.getMapStageId()).getMonsterGold())));
                //3.装备掉落 根据地图boss等级来判断装备等级,10%的概率掉1件	白 8%  绿2%，无职业区别
                FighterData fighterData = FighterModel.getFighterDataById(MapModel.getMapStageBossId(player.getMapStageId()));
                if (GameCommon.isWinPercent(10)) {
                    byte occupation = GameCommon.getRandomEquipOccupation();
                    byte equipPos = GameCommon.getRandomEquipPosition();
                    short equipLevel = GameCommon.getDropEquipLevel(fighterData.getLevel());

                    DropData dropData = new DropData();
                    dropData.setT(EGoodsType.EQUIP.getId());
                    dropData.setG(GoodsModel.getEquipId(equipLevel, occupation, equipPos));
                    dropData.setQ((byte) GameCommon.getRandomIndex(8000, 2000));
                    dropData.setN(1);
                    this.dropDataList.add(dropData);
                }
            }
        }
        this.clearFightDrop();
    }

    /**
     * 2002 战斗结果
     *
     * @param request
     */
    public void processFightResult(Message request) {
        //战斗结果 0：失败 1：成功 2：平局
        byte result = request.readByte();

        EGoodsChangeType changeType = EGoodsChangeType.FIGHT_BRUSH_MONSTER_ADD;
        //战斗间隔验证
        if (EMapType.FIELD_NORMAL == player.getMapType() && System.currentTimeMillis() - player.getFightRequestTime() < 1000) {
            this.fightResult = FightDefine.FIGHT_RESULT_FAIL;
        }
        //客户端如果失败 服务器也算作失败 平局同样
        if (result == FightDefine.FIGHT_RESULT_FAIL || FightDefine.FIGHT_RESULT_TIE == result) {
            this.fightResult = result;
        }
        //如果地图类型不为野外算作失败
        if (this.inInstance()) {
            this.fightResult = FightDefine.FIGHT_RESULT_FAIL;
        }
        //服务器验证
        FighterData fighterData = FighterModel.getFighterDataById(MapModel.getMapStageBossId(player.getMapStageId()));
        if (player.getFighting() < fighterData.getPower()) {
            this.fightResult = FightDefine.FIGHT_RESULT_FAIL;
        }
        //服务器验证失败
        if (FightDefine.FIGHT_RESULT_FAIL == this.fightResult && FightDefine.FIGHT_RESULT_SUCCESS == result) {
            gameRole.putErrorMessage(ErrorDefine.ERROR_SERVER_FIGHT_FAIL);
        }
        if (fightResult == FightDefine.FIGHT_RESULT_SUCCESS) {
            if (EMapType.FIELD_BOSS == player.getMapType()) {
                MapData mapData = MapModel.getMapDataById(player.getMapId());
                if (mapData.isMapEndStage(player.getMapStageId())) {
                    player.setMapId(mapData.getNextId());
                    player.addMapStageId();
                    player.setMapWave((byte) 0);

                    saveEnumSet.add(EPlayerSaveType.MAPID);
                    saveEnumSet.add(EPlayerSaveType.MAPSTAGEID);
                } else {
                    player.addMapStageId();
                    player.setMapWave((byte) 0);

                    saveEnumSet.add(EPlayerSaveType.MAPSTAGEID);
                }
                //地图关卡排行榜
                GameRankManager.getInstance().resetTopMapStage(player);
                gameRole.getFunctionManager().sendGameRankSimpleTopMsg(FunctionManager.RANK_SIMPLE_TYPE_MAP);
                changeType = EGoodsChangeType.FIGHT_BRUSH_BOSS_ADD;
                //通知过关消息
                gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.MAP_PASS, this.getClearanceStage(), saveEnumSet));
            } else if (EMapType.FIELD_NORMAL == player.getMapType()) {
                player.addMapWave();
                gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.KILL_FIELD_MONSTER, 1, saveEnumSet));
            }
        }

        Message message = new Message(MessageCommand.FIGHT_RESULT_MESSAGE, request.getChannel());
        message.setByte(this.fightResult);
        if (fightResult == FightDefine.FIGHT_RESULT_SUCCESS) {
            message.setShort(this.player.getMapId());
            message.setShort(this.player.getMapStageId());
            message.setByte(this.player.getMapWave());
            message.setByte(this.dropDataList.size());
            for (DropData data : this.dropDataList) {
                data.getMessage(message);
            }
            this.addDropToRole(changeType, saveEnumSet);
            saveEnumSet.add(EPlayerSaveType.REQUESTFIGHTTIME);

            if (EMapType.FIELD_BOSS == player.getMapType() || fieldMonsterCount > 5) {
                gameRole.getDbManager().playerDao.savePlayer(player, saveEnumSet);
                saveEnumSet.clear();
                fieldMonsterCount = 0;
            } else {
                ++fieldMonsterCount;
            }
        } else {
            this.clearFightDrop();
        }
        gameRole.sendMessage(message);
    }

    /**
     * 2011 请求野外小怪战斗
     *
     * @param request
     */
    public void processFieldMonsterFightRequest(Message request) {
        //请求战斗间隔验证
        long currTime = System.currentTimeMillis();
//		if(currTime-player.getFightRequestTime()<50){
//			gameRole.sendTick(request);
//			return;
//		}
        //时间间隔验证
        player.setFightRequestTime(currTime);

        Message message = new Message(MessageCommand.FIGHT_FIELD_MONSTER_REQUEST_MESSAGE, request.getChannel());
        gameRole.sendMessage(message);

        this.clearFightDrop();

        player.setMapType(EMapType.FIELD_NORMAL);

        this.fightResult = FightDefine.FIGHT_RESULT_SUCCESS;

        //1.小怪经验奖励
        float addExp = 1;
        //神器5激活 挂机经验获取提高5%
        if (player.isGodArtifactActive(5)) {
            addExp += 0.05f;
        }
        this.dropExp.setN((int) (addExp * MapModel.getMapStageRewardDataById(player.getMapStageId()).getMonsterExp()));
        //2.小怪金币奖励
        float addGold = 1;
        //神器4激活 挂机金币获取提高5%
        if (player.isGodArtifactActive(4)) {
            addGold += 0.05f;
        }
        //神器6激活 挂机金币获取提高5%
        if (player.isGodArtifactActive(6)) {
            addGold += 0.05f;
        }
        this.dropDataList.add(new DropData(EGoodsType.GOLD, 0, (int) (addGold * MapModel.getMapStageRewardDataById(player.getMapStageId()).getMonsterGold())));
        //3.装备掉落 根据地图boss等级来判断装备等级,10%的概率掉1件	白 8%  绿2%，无职业区别
        FighterData fighterData = FighterModel.getFighterDataById(MapModel.getMapStageBossId(player.getMapStageId()));
        if (GameCommon.isWinPercent(10)) {
            byte occupation = GameCommon.getRandomEquipOccupation();
            byte equipPos = GameCommon.getRandomEquipPosition();
            short equipLevel = GameCommon.getDropEquipLevel(fighterData.getLevel());

            DropData dropData = new DropData();
            dropData.setT(EGoodsType.EQUIP.getId());
            dropData.setG(GoodsModel.getEquipId(equipLevel, occupation, equipPos));
            dropData.setQ((byte) GameCommon.getRandomIndex(8000, 2000));
            dropData.setN(1);
            this.dropDataList.add(dropData);
        }
    }

    /**
     * 2012 野外小怪战斗结果
     *
     * @param request
     */
    public void processFieldMonsterFightResult(Message request) {
        //战斗结果 0：失败 1：成功 2：平局
        byte result = request.readByte();

        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        EGoodsChangeType changeType = EGoodsChangeType.FIGHT_BRUSH_MONSTER_ADD;
        //战斗间隔验证
        if (System.currentTimeMillis() - player.getFightRequestTime() < 1000) {
            this.fightResult = FightDefine.FIGHT_RESULT_FAIL;
        }
        //客户端如果失败 服务器也算作失败 平局同样
        if (result == FightDefine.FIGHT_RESULT_FAIL || FightDefine.FIGHT_RESULT_TIE == result) {
            this.fightResult = result;
        }
        //如果地图类型不为野外算作失败
        if (this.inInstance()) {
            this.fightResult = FightDefine.FIGHT_RESULT_FAIL;
        }
        //服务器验证
        FighterData fighterData = FighterModel.getFighterDataById(MapModel.getMapStageBossId(player.getMapStageId()));
        if (player.getFighting() < fighterData.getPower()) {
            this.fightResult = FightDefine.FIGHT_RESULT_FAIL;
        }
        //服务器验证失败
        if (FightDefine.FIGHT_RESULT_FAIL == this.fightResult && FightDefine.FIGHT_RESULT_SUCCESS == result) {
            gameRole.putErrorMessage(ErrorDefine.ERROR_SERVER_FIGHT_FAIL);
        }
        if (fightResult == FightDefine.FIGHT_RESULT_SUCCESS) {
            player.addMapWave();
            gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.KILL_FIELD_MONSTER, 1, enumSet));
        }

        Message message = new Message(MessageCommand.FIGHT_FIELD_MONSTER_RESULT_MESSAGE, request.getChannel());
        message.setByte(this.fightResult);
        if (fightResult == FightDefine.FIGHT_RESULT_SUCCESS) {
            message.setByte(this.player.getMapWave());
            message.setByte(this.dropDataList.size());
            for (DropData data : this.dropDataList) {
                data.getMessage(message);
            }
            this.addDropToRole(changeType, enumSet);
            enumSet.add(EPlayerSaveType.REQUESTFIGHTTIME);
            gameRole.getDbManager().playerDao.savePlayer(player, enumSet);
        } else {
            this.clearFightDrop();
        }
        gameRole.sendMessage(message);
    }

    /**
     * 2013 请求野外boss战斗
     *
     * @param request
     */
    public void processFieldBossFightRequest(Message request) {
        //请求战斗间隔验证
        long currTime = System.currentTimeMillis();
//		if(currTime-player.getFightRequestTime()<50){
//			gameRole.sendTick(request);
//			return;
//		}
        //时间间隔验证
        player.setFightRequestTime(currTime);

        MapData mapData = MapModel.getMapDataById(player.getMapId());
        //波数验证
        if (mapData.getId() != FightDefine.MAP_BIRTH_ID) {
            //未达到打boss条件，随机打小怪
            if (player.getMapWave() < FightDefine.MAP_WAVE_NUM) {
                processFieldMonsterFightRequest(request);
                return;
            }
        }

        Message message = new Message(MessageCommand.FIGHT_FIELD_BOSS_REQUEST_MESSAGE, request.getChannel());
        gameRole.sendMessage(message);

        this.clearFightDrop();

        player.setMapType(EMapType.FIELD_BOSS);

        FighterData fighterData = FighterModel.getFighterDataById(MapModel.getMapStageBossId(player.getMapStageId()));
        if (player.getFighting() > fighterData.getPower()) {
            this.fightResult = FightDefine.FIGHT_RESULT_SUCCESS;
        } else {
            this.fightResult = FightDefine.FIGHT_RESULT_FAIL;
        }
        if (FightDefine.FIGHT_RESULT_SUCCESS == this.fightResult) {
            //1.boss经验奖励
            float addExp = 1;
            //神器5激活 挂机经验获取提高5%
            if (player.isGodArtifactActive(5)) {
                addExp += 0.05f;
            }
            this.dropExp.setN((int) (addExp * MapModel.getMapStageRewardDataById(player.getMapStageId()).getBossExp()));
            //2.boss金币奖励
            float addGold = 1;
            //神器4激活 挂机金币获取提高5%
            if (player.isGodArtifactActive(4)) {
                addGold += 0.05f;
            }
            //神器6激活 挂机金币获取提高5%
            if (player.isGodArtifactActive(6)) {
                addGold += 0.05f;
            }
            this.dropDataList.add(new DropData(EGoodsType.GOLD, 0, (int) (addGold * MapModel.getMapStageRewardDataById(player.getMapStageId()).getBossGold())));
            //3.装备掉落 三职业每职业2~3件
            //			白	绿 	蓝	紫
            // 1-30级	 0%	40% 30%	30%
            //31-60级	10%	40%	40%	10%
            //60+级		40%	50%	10%	 0%
            for (byte occupation = 0; occupation < GameDefine.OCCUPATION_NUM; ++occupation) {
                int num = (int) (Math.random() * 2 + 2);
                for (int i = 0; i < num; ++i) {
                    byte equipPos = GameCommon.getRandomEquipPosition();
                    short equipLevel = GameCommon.getDropEquipLevel(fighterData.getLevel());
                    byte quality = 0;
                    if (fighterData.getLevel() < 31) {
                        quality = (byte) GameCommon.getRandomIndex(0, 4000, 3000, 3000);
                    } else if (fighterData.getLevel() < 61) {
                        quality = (byte) GameCommon.getRandomIndex(1000, 4000, 4000, 1000);
                    } else {
                        quality = (byte) GameCommon.getRandomIndex(4000, 5000, 1000);
                    }
                    DropData dropData = new DropData();
                    dropData.setT(EGoodsType.EQUIP.getId());
                    dropData.setG(GoodsModel.getEquipId(equipLevel, occupation, equipPos));
                    dropData.setQ(quality);
                    dropData.setN(1);
                    this.dropDataList.add(dropData);
                }
            }
            //4.野外boss材料掉落	30%
            //宝石碎片、经脉丹、玉笛碎片、铜镜碎片中任意掉落一个
            if (GameCommon.isWinPercent(30)) {
                this.dropDataList.add(new DropData(EGoodsType.ITEM, GoodsDefine.FIELD_BOSS_MATERIAL_DROP[GameCommon.getRandomIndex(4)], 1));
            }
            //5.固定给10元宝
            this.dropDataList.add(new DropData(EGoodsType.DIAMOND, 0, 10));

            if (player.getMapStageId() == 1) {
                byte quality = 3;

                DropData dropData = new DropData();
                dropData.setT(EGoodsType.EQUIP.getId());
                dropData.setG((short) 1);
                dropData.setQ(quality);
                dropData.setN(1);
                this.dropDataList.add(dropData);
                dropData = new DropData();
                dropData.setT(EGoodsType.EQUIP.getId());
                dropData.setG((short) 127);
                dropData.setQ(quality);
                dropData.setN(1);
                this.dropDataList.add(dropData);
                dropData = new DropData();
                dropData.setT(EGoodsType.EQUIP.getId());
                dropData.setG((short) 253);
                dropData.setQ(quality);
                dropData.setN(1);
                this.dropDataList.add(dropData);
            }
        }
    }

    /**
     * 2014 野外boss战斗结果
     *
     * @param request
     */
    public void processFieldBossFightResult(Message request) {
        //战斗结果 0：失败 1：成功 2：平局
        byte result = request.readByte();

        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        EGoodsChangeType changeType = EGoodsChangeType.FIGHT_BRUSH_BOSS_ADD;
        //战斗间隔验证
        if (System.currentTimeMillis() - player.getFightRequestTime() < 1000) {
            this.fightResult = FightDefine.FIGHT_RESULT_FAIL;
        }
        //客户端如果失败 服务器也算作失败 平局同样
        if (result == FightDefine.FIGHT_RESULT_FAIL || FightDefine.FIGHT_RESULT_TIE == result) {
            this.fightResult = result;
        }
        //如果地图类型不为野外算作失败
        if (this.inInstance()) {
            this.fightResult = FightDefine.FIGHT_RESULT_FAIL;
        }
        //服务器验证
        FighterData fighterData = FighterModel.getFighterDataById(MapModel.getMapStageBossId(player.getMapStageId()));
        if (player.getFighting() < fighterData.getPower()) {
            this.fightResult = FightDefine.FIGHT_RESULT_FAIL;
        }
        //服务器验证失败
        if (FightDefine.FIGHT_RESULT_FAIL == this.fightResult && FightDefine.FIGHT_RESULT_SUCCESS == result) {
            gameRole.putErrorMessage(ErrorDefine.ERROR_SERVER_FIGHT_FAIL);
        }
        if (fightResult == FightDefine.FIGHT_RESULT_SUCCESS) {
            MapData mapData = MapModel.getMapDataById(player.getMapId());
            if (mapData.isMapEndStage(player.getMapStageId())) {
                player.setMapId(mapData.getNextId());
                player.addMapStageId();
                player.setMapWave((byte) 0);

                enumSet.add(EPlayerSaveType.MAPID);
                enumSet.add(EPlayerSaveType.MAPSTAGEID);
            } else {
                player.addMapStageId();
                player.setMapWave((byte) 0);

                enumSet.add(EPlayerSaveType.MAPSTAGEID);
            }
            //地图关卡排行榜
            GameRankManager.getInstance().resetTopMapStage(player);
            gameRole.getFunctionManager().sendGameRankSimpleTopMsg(FunctionManager.RANK_SIMPLE_TYPE_MAP);
            changeType = EGoodsChangeType.FIGHT_BRUSH_BOSS_ADD;
            //通知过关消息
            gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.MAP_PASS, this.getClearanceStage(), enumSet));
        }

        Message message = new Message(MessageCommand.FIGHT_FIELD_BOSS_RESULT_MESSAGE, request.getChannel());
        message.setByte(this.fightResult);
        if (fightResult == FightDefine.FIGHT_RESULT_SUCCESS) {
            message.setShort(this.player.getMapId());
            message.setShort(this.player.getMapStageId());
            message.setByte(this.player.getMapWave());
            message.setByte(this.dropDataList.size());
            for (DropData data : this.dropDataList) {
                data.getMessage(message);
            }
            this.addDropToRole(changeType, enumSet);
            enumSet.add(EPlayerSaveType.REQUESTFIGHTTIME);
            gameRole.getDbManager().playerDao.savePlayer(player, enumSet);
        } else {
            this.clearFightDrop();
        }
        gameRole.sendMessage(message);
    }

    /**
     * 添加掉落到人 身上
     */
    public void addDropToRole(EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        dropDataList.add(dropExp);
        gameRole.getPackManager().addGoods(dropDataList, changeType, enumSet);
        clearFightDrop();
    }

    /**
     * 清除战斗掉落
     */
    private void clearFightDrop() {
        fightResult = FightDefine.FIGHT_RESULT_FAIL;
        dropDataList.clear();
        dropExp.setN(0);
    }

    /**
     * 得到通过关卡数
     *
     * @return
     */
    public int getClearanceStage() {
        return player.getMapStageId() - 1;
    }

    /**
     * 是否在副本中
     *
     * @return
     */
    public boolean inInstance() {
        if (EMapType.FIELD_NORMAL == player.getMapType() || EMapType.FIELD_BOSS == player.getMapType()) {
            return false;
        }
        return true;
    }
}
