package com.rd.game.manager;

import com.google.common.collect.Sets;
import com.rd.bean.dungeon.Dungeon;
import com.rd.bean.dungeon.DungeonDaily;
import com.rd.bean.mission.CardMission;
import com.rd.bean.mission.CardMissionData;
import com.rd.bean.mission.RoleChainMission;
import com.rd.bean.mission.TLMissionData;
import com.rd.bean.mission.handler.*;
import com.rd.bean.player.Player;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.*;
import com.rd.game.GameRole;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.EMissionUpdateType;
import com.rd.game.event.GameEvent;
import com.rd.game.event.IEventListener;
import com.rd.model.GuanJieModel;
import com.rd.model.MissionModel;
import com.rd.model.data.mission.*;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * 任务管理器
 * Created by XingYun on 2016/5/24.
 */
public class MissionManager implements IEventListener {
    private static final Logger logger = Logger.getLogger(MissionManager.class);
    private GameRole gameRole;
    private Player player;
    private List<IMissionHandler> handlers = new ArrayList<>();

    public MissionManager(GameRole gameRole) {
        this.gameRole = gameRole;
        this.player = gameRole.getPlayer();
        addFilters();
    }

    public void init() {
        RoleChainMission chainMission = player.getChainMission();
        if (chainMission == null) {
            chainMission = new RoleChainMission();
            player.setChainMission(chainMission);
            newChainMission(chainMission, MissionModel.getFirstChainMissionId());
        } else {
            this.simulateGameEvent(chainMission.getId());
        }
        updateDailyProcess(player.getDailyProgress());
        // FIXME map
        List<Short> dragonBallProcess = player.getDayData().getDragonballProcess();
        Map<Short, MissionDailyModelData> modelMap = MissionModel.getDragonBallMissions();
        if (dragonBallProcess.size() < modelMap.size()) {
            for (int id = dragonBallProcess.size(); id < modelMap.size(); id++) {
                dragonBallProcess.add((short) 0);
            }
        }
    }

    private void addFilters() {
        handlers.add(ChainMissionHandler.getInstance());
        handlers.add(DailyMissionHandler.getInstance());
        handlers.add(GangMissionHandler.getInstance());
        handlers.add(DragonBallMissionHandler.getInstance());
        handlers.add(AchievementMissionHandler.getInstance());
        handlers.add(TLMissionHandler.getInstance());
        handlers.add(CardMissionHandler.getInstance());
    }

    /**
     * 每日重置
     */
    public void reset() {
        updateDailyProcess(null);
    }

    private void updateDailyProcess(short[] process) {
        if (process == null) {
            short[] dailyProgress = new short[MissionModel.getMissionDailyDataMap().size() + 1];
            for (MissionDailyModelData data : MissionModel.getMissionDailyDataMap().values()) {
                dailyProgress[data.getId()] = 0;
            }
            player.setDailyProgress(dailyProgress);
        } else {
            //兼容每日任务有XML新增任务
            if (process.length < MissionModel.getMissionDailyDataMap().size() + 1) {
                short[] dailyProgress = new short[MissionModel.getMissionDailyDataMap().size() + 1];
                for (MissionDailyModelData data : MissionModel.getMissionDailyDataMap().values()) {
                    dailyProgress[data.getId()] = 0;
                }
                for (int i = 0; i < process.length; ++i) {
                    dailyProgress[i] = process[i];
                }
                player.setDailyProgress(dailyProgress);
            }
        }

    }

    private void newChainMission(RoleChainMission chainMission, short missionId) {
        MissionModelData resData = MissionModel.getMissionChainData(missionId);
        if (resData == null) {
            return;
        }
        chainMission.setNewMission(missionId);
        this.simulateGameEvent(missionId);
    }

    private void saveEventData(GameEvent event) {
        //处理需要接收者存储的数据集
        if (event.getSelfPlaySave() != null) {
            gameRole.savePlayer(event.getSelfPlaySave());
        }
    }

    public void handleEvent(GameEvent event) {
        for (IMissionHandler handler : handlers) {
            handler.handleEvent(gameRole, event);
        }
        this.saveEventData(event);
    }


    public void processChainMissionReceive(Message request) {
        RoleChainMission chainMission = gameRole.getPlayer().getChainMission();
        if (chainMission == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        MissionModelData resData = MissionModel.getMissionChainData(chainMission.getId());
        if (resData == null) {
            // 这个结构要求不能随便删除数据
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        if (chainMission.getS() != MissionDefine.MISSION_STATE_COMPLETED) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        // update
        chainMission.setS(MissionDefine.MISSION_STATE_RECEIVED);
        if (MissionDefine.MISSION_END != resData.getNext()) {
            newChainMission(chainMission, resData.getNext());
        }
        //gameRole.getDBManager().playerDao.updatePlayerChainMission(gameRole.getPlayer());
        // reward
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        gameRole.getPackManager().addGoods(resData.getRewardList(), EGoodsChangeType.CHAIN_MISSION_ADD, enumSet);
        enumSet.add(EPlayerSaveType.CHAINMISSION);
        gameRole.getDbManager().playerDao.savePlayer(player, enumSet);

        Message message = getChainMessage();
        message.setChannel(request.getChannel());
        gameRole.sendMessage(message);
    }

    public Message getChainMessage() {
        RoleChainMission chainMission = player.getChainMission();
        Message message = new Message(MessageCommand.MISSION_CHAIN_UPDATE_MESSAGE);
        chainMission.getMessage(message);
        return message;
    }

    public Message getDailyListMessage() {
        Message message = new Message(MessageCommand.MISSION_DAILY_LIST_MESSAGE);
        message.setByte(player.getDailyProgress().length - 1);
        for (int i = 1; i < player.getDailyProgress().length; ++i) {
            message.setShort(player.getDailyProgress()[i]);
        }
        return message;
    }

    public Message getDailyUpdateMessage(int id) {
        Message message = new Message(MessageCommand.MISSION_DAILY_UPDATE_MESSAGE);
        message.setByte(id);
        message.setShort(player.getDailyProgress()[id]);
        return message;
    }

    // FIXME 这个要重新整 把这些都做成条件检查 不做触发完成
    private void simulateGameEvent(short missionId) {
        MissionModelData chainData = MissionModel.getMissionChainData(missionId);
        EGameEventType type = chainData.getEventType();
        if (chainData.getUpdateType() == EMissionUpdateType.INCREMENT) {
            GameEvent event = null;
            switch (type) {
                case DUNGEON_SQUAMA_CLEARANCE:
                    Dungeon dungeon1 = gameRole.getDungeonManager().getDungeon(DungeonDefine.DUNGEON_TYPE_MATERIAL);
                    DungeonDaily daily1 = dungeon1.getDungeonDaily(DungeonDefine.DUNGEON_SUB_TYPE_FLUTE);
                    if (daily1.getAttackTimes() > 0) {
                        event = new GameEvent(EGameEventType.DUNGEON_SQUAMA_CLEARANCE, 1, null);
                    }
                    break;
                case DUNGEON_VEINS_CLEARANCE:
                    Dungeon dungeon2 = gameRole.getDungeonManager().getDungeon(DungeonDefine.DUNGEON_TYPE_MATERIAL);
                    DungeonDaily daily2 = dungeon2.getDungeonDaily(DungeonDefine.DUNGEON_SUB_TYPE_MIRROR);
                    if (daily2.getAttackTimes() > 0) {
                        event = new GameEvent(EGameEventType.DUNGEON_VEINS_CLEARANCE, 1, null);
                    }
                    break;
                case DUNGEON_WING_CLEARANCE:
                    Dungeon dungeon3 = gameRole.getDungeonManager().getDungeon(DungeonDefine.DUNGEON_TYPE_MATERIAL);
                    DungeonDaily daily3 = dungeon3.getDungeonDaily(DungeonDefine.DUNGEON_SUB_TYPE_WING);
                    if (daily3.getAttackTimes() > 0) {
                        event = new GameEvent(EGameEventType.DUNGEON_WING_CLEARANCE, 1, null);
                    }
                    break;
                case DUNGEON_GEM_CLEARANCE:
                    Dungeon dungeon4 = gameRole.getDungeonManager().getDungeon(DungeonDefine.DUNGEON_TYPE_MATERIAL);
                    DungeonDaily daily4 = dungeon4.getDungeonDaily(DungeonDefine.DUNGEON_SUB_TYPE_GEM);
                    if (daily4.getAttackTimes() > 0) {
                        event = new GameEvent(EGameEventType.DUNGEON_GEM_CLEARANCE, 1, null);
                    }
                    break;
                case DUNGEON_MERIDIAN_CLEARANCE:
                    Dungeon dungeon5 = gameRole.getDungeonManager().getDungeon(DungeonDefine.DUNGEON_TYPE_MATERIAL);
                    DungeonDaily daily5 = dungeon5.getDungeonDaily(DungeonDefine.DUNGEON_SUB_TYPE_MERIDIAN);
                    if (daily5.getAttackTimes() > 0) {
                        event = new GameEvent(EGameEventType.DUNGEON_MERIDIAN_CLEARANCE, 1, null);
                    }
                    break;
                case DUNGEON_CUILIAN_CLEARANCE:
                    Dungeon dungeon6 = gameRole.getDungeonManager().getDungeon(DungeonDefine.DUNGEON_TYPE_MATERIAL);
                    DungeonDaily daily6 = dungeon6.getDungeonDaily(DungeonDefine.DUNGEON_SUB_TYPE_CUILIAN);
                    if (daily6.getAttackTimes() > 0) {
                        event = new GameEvent(EGameEventType.DUNGEON_CUILIAN_CLEARANCE, 1, null);
                    }
                    break;
                default:
                    break;
            }
            if (event != null) {
                ChainMissionHandler.getInstance().handleEvent(gameRole, event);
            }
            return;
        }
        GameEvent event = null;
        switch (type) {
            case MAP_PASS:
                //TODO extend event
                event = new GameEvent(type, gameRole.getFightManager().getClearanceStage(), null);
                break;
            case PLAYER_REACH_LEVEL:
                event = new GameEvent(type, player.getLevel(), null);
                break;
            case GEM_REACH_LEVEL:
                int gemLevel = 0;
//	    		for(Character ch:player.getCharacterList()){
//	    			for(EquipSlot slot : ch.getEquipSlotList()){
//	    				if(slot.getJ()>gemLevel){
//	    					gemLevel=slot.getJ();
//	    				}
//	    			}
//	    		}
                event = EGameEventType.GEM_REACH_LEVEL.create(gameRole, gemLevel, null);
                break;
            case MEDAL_REACH_LEVEL:
                event = new GameEvent(type, player.getMedal(), null);
                break;
            case DUNGEON_DEKARON_PASS:
                Dungeon dungeon = gameRole.getDungeonManager().getDungeon(DungeonDefine.DUNGEON_TYPE_DEKARON);
                event = new GameEvent(EGameEventType.DUNGEON_DEKARON_PASS, dungeon.getPass() - 1, null);
                break;
            case LADDER_MATCH:
                event = new GameEvent(type, gameRole.getLadderManager().getStar(), null);
                break;
            case ACTIVE_ARTIFACT_FRAGMENTS_1:
                if (player.getArtifactBoss().getPieces().get((short) 1) != null || player.getArtifactBoss().getId() > 1) {
                    event = new GameEvent(type, 1, null);
                }
                break;
            case ACTIVE_ARTIFACT_FRAGMENTS_2:
                if (player.getArtifactBoss().getPieces().get((short) 2) != null || player.getArtifactBoss().getId() > 1) {
                    event = new GameEvent(type, 1, null);
                }
                break;
            case ACTIVE_ARTIFACT_1:
                if (player.getArtifactBoss().getId() > 1) {
                    event = new GameEvent(type, 1, null);
                }
                break;
            case ACTIVE_ARTIFACT_FRAGMENTS_3:
                if (player.getArtifactBoss().getPieces().get((short) 3) != null || player.getArtifactBoss().getId() > 2) {
                    event = new GameEvent(type, 1, null);
                }
                break;
            case ACTIVE_ARTIFACT_FRAGMENTS_4:
                if (player.getArtifactBoss().getPieces().get((short) 4) != null || player.getArtifactBoss().getId() > 2) {
                    event = new GameEvent(type, 1, null);
                }
                break;
            case ACTIVE_ARTIFACT_FRAGMENTS_5:
                if (player.getArtifactBoss().getPieces().get((short) 5) != null || player.getArtifactBoss().getId() > 2) {
                    event = new GameEvent(type, 1, null);
                }
                break;
            case ACTIVE_ARTIFACT_FRAGMENTS_6:
                if (player.getArtifactBoss().getPieces().get((short) 6) != null || player.getArtifactBoss().getId() > 2) {
                    event = new GameEvent(type, 1, null);
                }
                break;
            case ACTIVE_ARTIFACT_2:
                if (player.getArtifactBoss().getId() > 2) {
                    event = new GameEvent(type, 1, null);
                }
                break;
            case REIN_REACH_LEVEL:
                event = new GameEvent(type, player.getRein(), null);
                break;
            case VIP_REACH_LEVEL:
                event = new GameEvent(type, player.getVipLevel(), null);
                break;
            case GUANJIE_REACH_LEVEL:
                byte guanjieLevel = GuanJieModel.getData(player.getWeiWang()).getLevel();
                event = new GameEvent(type, guanjieLevel, null);
                break;
            case DRAGON_BALL_REACH_LEVEL:
                event = new GameEvent(type, player.getDragonBall().getLevel(), null);
                break;
            case ACTIVE_MONTH_CARD:
                if (gameRole.getActivityManager().isMonthlyCard()) {
                    event = new GameEvent(type, 1, null);
                }
                break;
            case WING_REACH_STAGE:
                int wingStage = 0;
//	    		for(Character ch:player.getCharacterList()){
//    				if(ch.getMountStage()>wingStage){
//    					wingStage=ch.getMountStage();
//    				}
//	    		}
                event = new GameEvent(type, wingStage, null);
                break;
            case MIRROR_UP:
                // TODO 就是default 留着检查用
                event = type.simulate(gameRole);
                break;
            case COMBINE_RUNE_NUM:
                event = new GameEvent(EGameEventType.COMBINE_RUNE_NUM, player.getCombineRuneTotalNum(), null);
                break;
            case CASTING_SOUL_TOTAL_UP:
                event = new GameEvent(EGameEventType.CASTING_SOUL_TOTAL_UP, player.getCastingSoulTotalNum(), null);
                break;
            case COMPLETE_FIRST_PAY:
                if (gameRole.getPayManager().hasFirstPay()) {
                    event = new GameEvent(EGameEventType.COMPLETE_FIRST_PAY, 1, null);
                }
                break;
            case DRAGON_BALL_LEVEL_UP:
                if (player.getDragonBall().getLevel() > 0) {
                    event = new GameEvent(EGameEventType.DRAGON_BALL_LEVEL_UP, 1, null);
                }
                break;
            case FIVE_ELEMENT_UP:
                event = new GameEvent(type, player.getFiveElements().getFiveLevel(), null);
                break;
            default:
                break;
        }
        if (event != null) {
            ChainMissionHandler.getInstance().handleEvent(gameRole, event);
            this.saveEventData(event);
        }
    }

    /**
     * 龙珠任务列表
     *
     * @return
     */
    public Message getDragonBallMessage() {
        Message message = new Message(MessageCommand.DRAGON_BALL_GET_MISSIONS_MESSAGE);
        List<Short> missionList = player.getDayData().getDragonballProcess();
        message.setByte(missionList.size());
        for (int i = 0; i < missionList.size(); i++) {
            message.setShort(missionList.get(i));
        }
        message.setInt(getDragonBallTotal());
        message.setByte(player.getDayData().getDragonballBoxMask());
        return message;
    }

    /**
     * 龙珠任务信息
     *
     * @return
     */
    public Message getDragonBallMissionMessage(short id) {
        Message message = new Message(MessageCommand.DRAGON_BALL_UPDATE_MISSION_MESSAGE);
        message.setByte(id);
        message.setShort(player.getDayData().getDragonballProcess(id));
        message.setInt(getDragonBallTotal());
        return message;
    }

    /**
     * 领取龙珠任务宝箱
     *
     * @param request
     */
    public void processReceiveDragonBallBox(Message request) {
        byte boxId = request.readByte();
        MissionBoxModelData boxData = MissionModel.getDragonBallBox(boxId);
        if (boxData == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        // 领取过
        if (player.getDayData().isBoxReceived(boxId)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        // 活跃不够
        if (boxData.getScore() > getDragonBallTotal()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        player.getDayData().addDragonBallBox(boxId);
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        enumSet.add(EPlayerSaveType.DAYDATA);
        gameRole.getPackManager().addGoods(boxData.getRewardList(), EGoodsChangeType.DRAGONBALL_BOX_ADD, enumSet);

        gameRole.savePlayer(enumSet);
        Message message = new Message(MessageCommand.DRAGON_BALL_RECEIVE_BOX_MESSAGE, request.getChannel());
        message.setByte(player.getDayData().getDragonballBoxMask());
        gameRole.sendMessage(message);
    }

    /**
     * 龙珠任务今日获得总奖励
     *
     * @return
     */
    private int getDragonBallTotal() {
        int total = 0;
        List<Short> recordList = player.getDayData().getDragonballProcess();
        for (short idx = 0; idx < recordList.size(); idx++) {
            short id = (short) (idx + 1);
            MissionDailyModelData modelData = MissionModel.getDragonballMission(id);
            if (modelData == null) {
                continue;
            }
            int process = recordList.get(idx);
            if (process < modelData.getCount()) {
                // 没完成
                continue;
            }
            total += modelData.getScore() * recordList.get(idx);
        }
        return total;
    }

    public Message getAchievementMissionMessage(short id) {
        Message message = new Message(MessageCommand.ACHIEVEMENT_UPDATE_MISSION_MESSAGE);
        message.setByte(1); //size FIXME
        getAchievementMission(message, id);
        return message;
    }

    private void getAchievementMission(Message message, short id) {
        RoleChainMission mission = player.getAchievement(id);
        message.setShort(id);
        message.setByte(mission.getS());
    }

    public Message getAchievementMissionMessage() {
        Message message = new Message(MessageCommand.ACHIEVEMENT_GET_MISSIONS_MESSAGE);
        Map<Short, RoleChainMission> missions = player.getAchievementMission();
        message.setByte(missions.size());
        for (Map.Entry<Short, RoleChainMission> mission : missions.entrySet()) {
            message.setShort(mission.getKey());
            message.setByte(mission.getValue().getS());
        }
        return message;
    }

    /**
     * 领取成就任务
     *
     * @param request
     */
    public void processReceiveAchievementMission(Message request) {
        short id = request.readShort();
        Set<Short> targets;
        if (id < 0) {
            // 全部领取
            targets = getAchievementCompleted();
        } else {
            targets = new HashSet<>();
            targets.add(id);
        }
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        try {
            for (Short missionId : targets) {
                // 不是可领取状态
                Byte state = gameRole.getPlayer().getAchievement(missionId).getS();
                if (state != MissionDefine.MISSION_STATE_COMPLETED) {
                    gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
                    continue;
                }
                this.doReceiveAchievement(missionId, enumSet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        gameRole.savePlayer(enumSet);

        Message message = new Message(MessageCommand.ACHIEVEMENT_UPDATE_MISSION_MESSAGE, request.getChannel());
        message.setByte(targets.size());
        for (Short missionId : targets) {
            getAchievementMission(message, missionId);
        }
        gameRole.sendMessage(message);
    }

    private void doReceiveAchievement(Short id, EnumSet<EPlayerSaveType> enumSet) {
        // 更新状态
        RoleChainMission mission = player.getAchievement(id);
        mission.setS(MissionDefine.MISSION_STATE_RECEIVED);
        enumSet.add(EPlayerSaveType.ACHIEVEMENT_MISSION);

        // 发奖励
        MissionModelData modelData = MissionModel.getAchievementMission(id);
        gameRole.getPackManager().addGoods(modelData.getRewardList(), EGoodsChangeType.DRAGONBALL_BOX_ADD, enumSet);
    }

    /**
     * 获取可领取的成就集合
     *
     * @return
     */
    private Set<Short> getAchievementCompleted() {
        Set<Short> targets = new HashSet<>();
        for (Map.Entry<Short, RoleChainMission> mission : player.getAchievementMission().entrySet()) {
            if (mission.getValue().getS() == MissionDefine.MISSION_STATE_COMPLETED) {
                targets.add(mission.getKey());
            }
        }
        return targets;
    }


    public void processGetTLMission(Message request) {
        updateTLMission();
        Message message = getTLMissionMessage();
        message.setChannel(request.getChannel());
        gameRole.sendMessage(message);
    }


    /**
     * 更新限时任务组
     */
    public void updateTLMission() {
        if (player.getLevel() < GameDefine.FUNCTION_TLMISSION_OPEN_LEVEL) {
            // 没开启
            return;
        }
        TLMissionData data = gameRole.getPlayer().getTLMissionData();
        TLMissionGroupModelData modelData = MissionModel.getTLGroup(data.getGroupId());
        if (modelData == null) {
            return;
        }
        if (!data.isOpen()) {
            // 该开启了还没开启
            data.setDeadline(System.currentTimeMillis() + modelData.getTime());
            gameRole.getPlayer().setTLMissionData(data);
            // 不频繁 为了清晰在这里存储
            gameRole.savePlayer(EPlayerSaveType.TLMISSION);
            logger.info("TLMissions open whit playerId=" + player.getId() + ",ts=" + System.currentTimeMillis());

            gameRole.putMessageQueue(gameRole.getMissionManager().getTLMissionMessage());
        } else if (data.isDead() || data.isCompleted(modelData.getMissions())) {
            // 已过期 or 全部完成
            byte nextGroup = (byte) (data.getGroupId() + 1);
            TLMissionGroupModelData nextData = MissionModel.getTLGroup(nextGroup);
            if (nextData == null) {
                //最后一组
                return;
            }
            // 更新任务组
            data.setGroupId(nextGroup);
            data.setDeadline(data.getDeadline() + nextData.getTime());
            gameRole.getPlayer().setTLMissionData(data);
            // 不频繁 为了清晰在这里存储
            gameRole.savePlayer(EPlayerSaveType.TLMISSION);
            logger.info("TLMissions update with playerId=" + player.getId() + ",group=" + nextGroup + ",ts=" + System.currentTimeMillis());
        }
    }

    public Message getTLMissionMessage() {
        TLMissionData data = gameRole.getPlayer().getTLMissionData();
        Message message = new Message(MessageCommand.TLMISSION_UPDATE_MESSAGE);
        if (!data.isValid()) {
            message.setByte(0);
        } else {
            message.setByte(data.getGroupId());
            // 剩余时间
            int restTime = (int) ((data.getDeadline() - System.currentTimeMillis()) / DateUtil.SECOND);
            message.setInt(restTime > 0 ? restTime : 0);
            TLMissionGroupModelData groupModelData = MissionModel.getTLGroup(data.getGroupId());
            // 只发当前组
            Set<Short> missionsInGroup = Sets.intersection(groupModelData.getMissions(), data.getMissions().keySet());
            message.setByte(missionsInGroup.size());
            for (Short id : missionsInGroup) {
                RoleChainMission mission = data.getMissions().get(id);
                mission.getMessage(message);
            }
        }
        return message;
    }

    public void processReceiveTLMission(Message request) {
        short id = request.readShort();
        updateTLMission();
        TLMissionData data = gameRole.getPlayer().getTLMissionData();
        if (!data.isValid()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        TLMissionGroupModelData groupModelData = MissionModel.getTLGroup(data.getGroupId());
        if (groupModelData == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        if (!groupModelData.getMissions().contains(id)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        MissionModelData modelData = MissionModel.getTLMission(id);
        if (modelData == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        RoleChainMission mission = data.getMissions().get(id);
        if (mission == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        if (mission.getS() != MissionDefine.MISSION_STATE_COMPLETED) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        // update
        mission.setS(MissionDefine.MISSION_STATE_RECEIVED);
        // reward
        gameRole.getPackManager().addGoods(modelData.getRewardList(), EGoodsChangeType.TLMISSION_ADD, saves);
        saves.add(EPlayerSaveType.TLMISSION);
        gameRole.savePlayer(saves);

        Message message = new Message(MessageCommand.TLMISSION_RECEIVE_MESSAGE, request.getChannel());
        message.setShort(id);
        gameRole.sendMessage(message);
    }

    public Map<Short, RoleChainMission> getUpdateMissions(GameEvent event,
                                                          Map<Short, RoleChainMission> currentMissions,
                                                          Map<Short, EMissionUpdateType> eventMissions) {
        Map<Short, RoleChainMission> updates = new HashMap<>();
        for (Map.Entry<Short, EMissionUpdateType> entry : eventMissions.entrySet()) {
            RoleChainMission mission = currentMissions.get(entry.getKey());
            if (mission == null) {
                mission = new RoleChainMission();
                mission.setNewMission(entry.getKey());
            }
            if (!mission.isUncompleted()) {
                continue;
            }
            boolean update = false;
            switch (entry.getValue()) {
                case INCREMENT:
                    mission.setC((short) (mission.getC() + event.getData()));
                    update = true;
                    break;
                case BIGGER:
                    if (event.getData() >= mission.getC()) {
                        mission.setC((short) event.getData());
                        update = true;
                    }
                    break;
                case BIGGER_TOTAL:
                    int totalData = event.getTotalData();
                    if (totalData >= mission.getC()) {
                        mission.setC((short) totalData);
                        update = true;
                    }
                    break;
                default:
                    throw new IllegalArgumentException("MissionManager.handleEvent() getUpdateMissions. Unexpected updateType="
                            + entry.getValue());
            }
            if (update) {
                updates.put(mission.getId(), mission);
            }
        }
        return updates;
    }

    /** ----------------------------------------------- 卡牌相关-----------------------------------------------**/
    /**
     * 更新卡牌任务组
     */
    public void updateCardMission() {
//		if (player.getLevel() < GameDefine.FUNCTION_CARD_MISSION_OPEN_LEVEL) {
//			// 没开启
//			return;
//		}
//		CardMissionData data = gameRole.getPlayer().getCardMissionData();
//		CardMissionGroupModelData modelData = MissionModel.getCardGroup(data.getGroupId());
//		if (modelData == null) {
//			return;
//		}
//		if (!data.isOpen()) {
//			// 该开启了还没开启
//			data.setDeadline(System.currentTimeMillis() + modelData.getKeepTime());
//			gameRole.getPlayer().setCardMissionData(data);
//			// 不频繁 为了清晰在这里存储
//			gameRole.savePlayer(EPlayerSaveType.CARD_MISSION);
//			logger.info("CardMissions open whit playerId=" + player.getId() + ",ts=" + System.currentTimeMillis());
//
//			gameRole.putMessageQueue(gameRole.getMissionManager().getCardMissionMessage());
//		} else if (data.isDead() || data.isCompleted(modelData.getRewards())){
        // 已过期 or 全部完成
//			byte nextGroup = (byte) (data.getGroupId() + 1);
//			TLMissionGroupModelData nextData = MissionModel.getTLGroup(nextGroup);
//			if (nextData == null) {
//				//最后一组
//				return;
//			}
//			// 更新任务组
//			data.setGroupId(nextGroup);
//			data.setDeadline(data.getDeadline() + nextData.getTime());
//			gameRole.getPlayer().setTLMissionData(data);
//			// 不频繁 为了清晰在这里存储
//			gameRole.savePlayer(EPlayerSaveType.TLMISSION);
//			logger.info("TLMissions update with playerId=" + player.getId() + ",group=" + nextGroup + ",ts=" + System.currentTimeMillis());
//		}
    }

    /**
     * 990 卡牌限时任务列表
     *
     * @param request
     */
    public void processGetCardMission(Message request) {
        Message message = this.getCardMissionMessage();
        message.setChannel(request.getChannel());
        gameRole.sendMessage(message);
    }

    public Message getCardMissionMessage() {
        CardMission data = gameRole.getPlayer().getCardMission();
        Message message = new Message(MessageCommand.CARD_MISSION_MESSAGE);
        message.setByte(data.getId());
        for (CardMissionData missionData : data.getMissions()) {
            message.setByte(missionData.getState());
            for (RoleChainMission mission : missionData.getMissions()) {
                message.setByte(mission.getId());
                message.setShort(mission.getC());
            }
        }
        return message;
    }

    /**
     * 992 卡牌任务领取奖励
     */
    public void processReceiveCardMission(Message request) {
        byte cardMissionId = request.readByte();

        CardMission data = gameRole.getPlayer().getCardMission();
        CardMissionData mission = null;
        for (CardMissionData missionData : data.getMissions()) {
            if (missionData.getId() == cardMissionId) {
                mission = missionData;
                break;
            }
        }
        if (mission == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        if (mission.getState() != MissionDefine.MISSION_STATE_COMPLETED) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        mission.setState(MissionDefine.MISSION_STATE_RECEIVED);

        EnumSet<EPlayerSaveType> saves = EnumSet.of(EPlayerSaveType.CARD_MISSION);

        CardMissionReward rewardData = MissionModel.getCardReward(mission.getId());
        gameRole.getPackManager().addGoods(rewardData.getReward(), EGoodsChangeType.CARD_MISSION_ADD, saves);

        Message message = new Message(MessageCommand.CARD_MISSION_RECEIVE_MESSAGE, request.getChannel());
        message.setByte(cardMissionId);
        message.setByte(mission.getState());
        gameRole.sendMessage(message);

        gameRole.savePlayer(saves);
    }
}
