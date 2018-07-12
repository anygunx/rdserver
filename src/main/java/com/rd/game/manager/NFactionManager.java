package com.rd.game.manager;

import com.rd.bean.drop.DropData;
import com.rd.bean.faction.NFaction;
import com.rd.bean.faction.NFactionMember;
import com.rd.bean.gang.Gang;
import com.rd.bean.gang.GangLog;
import com.rd.bean.gang.GangMember;
import com.rd.bean.gang.GangMission;
import com.rd.bean.player.DayData;
import com.rd.bean.player.Player;
import com.rd.bean.player.SimplePlayer;
import com.rd.bean.task.handler.ITaskHandler;
import com.rd.bean.task.handler.NFactionTaskHandler;
import com.rd.common.GameCommon;
import com.rd.common.GangService;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.*;
import com.rd.enumeration.EMessage;
import com.rd.game.*;
import com.rd.game.event.GameEvent;
import com.rd.game.event.IEventListener;
import com.rd.model.NFactionModel;
import com.rd.model.WordSensitiveModel;
import com.rd.model.data.faction.*;
import com.rd.net.message.Message;

import java.util.*;

/**
 * 帮会系统
 */
public class NFactionManager implements IEventListener {


    private GameRole gameRole;
    private Player player;
    private List<ITaskHandler> handlers = new ArrayList<>();

    public NFactionManager(GameRole role) {
        this.gameRole = role;
        this.player = role.getPlayer();
        addFilters();

    }

    private void addFilters() {
        handlers.add(NFactionTaskHandler.getInstance());
    }


    /**
     * 公会创建
     *
     * @param request
     */
    public void processFactionCreate(Message request) {
        String name = request.readString().trim();
        byte badge = request.readByte();

        if (player.getFaction() != null) {
            return;
        }
        if (badge != NFactionDefine.FACTION_LEVEL_1 && badge != NFactionDefine.FACTION_LEVEL_2) {
            return;
        }
        int cost = 0;
        if (NFactionDefine.FACTION_LEVEL_1 == badge) {
            cost = NFactionDefine.CREATE_FACTION_1_DIAMOND;
        } else {
            cost = NFactionDefine.CREATE_FACTION_2_DIAMOND;

        }
        if (player.getDiamond() < cost) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
            return;
        }
        if (!GameCommon.checkReservedWord(name)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_INVALID_STRING);
            return;
        }
        if (!WordSensitiveModel.checkSensitive(name)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_INVALID_STRING);
            return;
        }
        if (name.length() == 0 || name.length() > GangDefine.GANG_LIMIT_NAME_LENGTH) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_STRING_LENGTH_LIMIT);
            return;
        }
        name = GameCommon.getFormatName(name, player.getServerId());
        if (player.getGang() != null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        short errorCode = NGameFactionManager.getInstance().createFaction(gameRole, name, badge);
        if (errorCode > 0) {
            gameRole.sendErrorTipMessage(request, errorCode);
            return;
        }

        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        DropData costDrop = new DropData(EGoodsType.DIAMOND, 0, cost);
        if (!gameRole.getPackManager().useGoods(costDrop, EGoodsChangeType.CREATE_GANG_CONSUME, enumSet, false)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_NOT_ENOUGHT);
            return;
        }

        Message msg = new Message(EMessage.FACTION_CREATE.CMD(), request.getChannel());
        msg.setByte((byte) 1);
        gameRole.sendMessage(msg);
        gameRole.savePlayer(enumSet);
    }

    /**
     * 更改帮派名称
     */
    public void processChangeFactionName(Message request) {
        String name = request.readString().trim();
        if (player.getFaction() == null) {
            return;
        }
        if (!GameCommon.checkReservedWord(name)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_INVALID_STRING);
            return;
        }
        if (!WordSensitiveModel.checkSensitive(name)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_INVALID_STRING);
            return;
        }
        if (name.length() == 0 || name.length() > GangDefine.GANG_LIMIT_NAME_LENGTH) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_STRING_LENGTH_LIMIT);
            return;
        }

        if (player.getDiamond() < NFactionDefine.FACTION_CHANCE_NAME) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
            return;
        }
        NFaction faction = player.getFaction();
        if (this.isPermissionNone(faction, request)) {
            return;
        }

        boolean isHave = NGameFactionManager.getInstance().checkGangName(name);
        if (isHave) {
            return;
        }

        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        DropData costDrop = new DropData(EGoodsType.DIAMOND, 0, NFactionDefine.FACTION_CHANGE_COST);
        if (!gameRole.getPackManager().useGoods(costDrop, EGoodsChangeType.FACTION_CHANGE_NAME, enumSet, false)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_NOT_ENOUGHT);
            return;
        }

        short errorCode = NGameFactionManager.getInstance().changeName(name, faction);
        if (errorCode > GameDefine.NONE) {
            return;
        }
        Message msg = new Message(EMessage.FACTION_NAME_CHANGE.CMD(), request.getChannel());
        msg.setByte(1);
        gameRole.sendMessage(msg);
    }


    /**
     * 公会公告修改
     *
     * @param request
     */
    public void processFactionDeclarationNote(Message request) {
        if (this.isGangNone(request)) {
            return;
        }
        NFaction gang = player.getFaction();
        if (this.isOperationQuick(gang, request)) {
            return;
        }
        if (this.isPermissionNone(gang, request)) {
            return;
        }

        String declaration = request.readString();
        if (declaration.length() > NFactionDefine.GANG_DECLARATION_MAX_LENGTH) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_CHAT_LENGTH_LONG);
            return;
        }
        if (!GameCommon.checkReservedWord(declaration)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_INVALID_STRING);
            return;
        }
        declaration = WordSensitiveModel.replaceSensitive(declaration);
        Message message = new Message(EMessage.FACTION_NAME_DECLARATION.CMD(), request.getChannel());
        message.setString(declaration);
        gameRole.sendMessage(message);
        if (!gang.getDeclaration().equals(declaration)) {
            gang.setNotice(declaration);
            gameRole.getDbManager().gangDao.updateDeclaration(gang.getId(), declaration);
        }
    }

    /**
     * 帮派列表
     *
     * @param request
     */
    public void processFactionList(Message request) {
        Message msg = new Message(EMessage.FACTION_LIST.CMD(), request.getChannel());
        List<NFaction> list = NGameFactionManager.getInstance().getFactionList();
        msg.setShort(list.size());
        for (NFaction nFaction : list) {
            nFaction.getSimpleMessage(msg);
        }
        gameRole.sendMessage(msg);
    }

    /**
     * 帮派申请
     *
     * @param request
     */
    public void processFactionApply(Message request) {
        if (player.getFaction() != null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        int gangId = request.readInt();
        NFaction gang = NGameFactionManager.getInstance().getFaction(gangId);
        if (gang == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        if (gang.isApplyFull()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GANG_APPLICATIONS_FULL);
            return;
        }
        if (gang.getApply(gameRole.getPlayerId()) != -1) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GANG_ALREADY_APPLY);
            return;
        }
        if (gameRole.getPlayer().getRein() < gang.getLimitLevel()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_LEVEL_LESS);
            return;
        }
        if (gang.isMemberFull()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GANG_MEMBER_FULL);
            return;
        }
        if (player.getFighting() < gang.getLimitefight()) {
            return;
        }
        byte state = NFactionDefine.FACTION_APPLY_STATE2;
        if (gang.isAutoAdopt()) {
            NFactionMember gangMember = new NFactionMember(player, gangId, NFactionDefine.GANG_POSITION_MEMBER, gameRole.getDungeonManager().getDungeonGangPass());
            gang.getMemberMap().put(gangMember.getPlayerId(), gangMember);
            gang.addLog(new GangLog(NfactionLogType.JOIN.getValue(), player.getName()));
            player.setFaction(gang);
            // this.processFactionInfo(request);
            //gameRole.getDbManager().nFactionDao.addGangMember(gangMember);
        } else {
            //gameRole.sendErrorTipMessage(request,ErrorDefine.ERROR_GANG_ALREADY_APPLY);
            gang.getApplyList().add(player);
            state = NFactionDefine.FACTION_APPLY_STATE1;
            gameRole.getDbManager().nFactionDao.updateApply(gang);
        }
        Message message = new Message(EMessage.FACTION_APPLY.CMD(), request.getChannel());
        message.setByte(state);
        gameRole.sendMessage(message);
    }

    /**
     * 设置是否自动 加入帮派
     */
    public void processSetAutoAddFaction(Message request) {
        if (this.isGangNone(request)) {
            return;
        }
        NFaction gang = player.getFaction();
        if (this.isOperationQuick(gang, request)) {
            return;
        }
        if (this.isPermissionNone(gang, request)) {
            return;
        }
        byte isAuto = request.readByte();
        long fight = request.readLong();
        gang.setAutoAdopt(isAuto == 1 ? true : false);
        if (fight > 0) {
            gang.setLimitefight(fight);
        }
        Message message = new Message(EMessage.FACTION_AUTO_FACTION_APPLY.CMD(), request.getChannel());
        message.setByte(isAuto);
        message.setLong(fight);
        gameRole.sendMessage(message);
        //gameRole.getDbManager().nFactionDao.updateApply(gang);

    }


    /**
     * 公会申请列表
     *
     * @param request
     */
    public void processFactionApplyList(Message request) {
        if (this.isGangNone(request)) {
            return;
        }
        NFaction gang = player.getFaction();
        if (this.isOperationQuick(gang, request)) {
            return;
        }
        if (this.isPermissionNone(gang, request)) {
            return;
        }

        Message message = new Message(EMessage.FACTION_APPLY_LIST.CMD(), request.getChannel());
        message.setByte(gang.isAutoAdopt() ? 1 : 0);
        message.setLong(0);
        message.setByte(gang.getApplyList().size());
        for (SimplePlayer simplePlayer : gang.getApplyList()) {
            simplePlayer.getSimpleMessage(message);
        }
        gameRole.sendMessage(message);
    }

    /**
     * 帮派信息
     */
    public void processFactionInfo(Message request) {
        Message message = new Message(EMessage.FACTION_INFO.CMD(), request.getChannel());
        if (player.getFaction() == null) {
            message.setByte(0);
        } else {
            message.setByte(1);
            NFaction faction = player.getFaction();
            faction.getMessage(message);
            NFactionMember member = faction.getGangMember(player.getId());
            message.setByte(member.getPosition());
        }
        gameRole.sendMessage(message);
    }


    /**
     * 是否有公会
     *
     * @return
     */
    private boolean isGangNone(Message request) {
        if (player.getFaction() == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GANG_NONE);
            return true;
        }
        return false;
    }

    /**
     * 操作是否太快
     *
     * @param gang
     * @param request
     * @return
     */
    private boolean isOperationQuick(NFaction gang, Message request) {
        long currentTime = System.currentTimeMillis();
        if (GameDefine.NONE != gang.getModifyTime() && currentTime - gang.getModifyTime() < GameDefine.OPERATION_INTERVAL) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_OVER_QUICK);
            return true;
        }
        gang.setModifyTime(currentTime);
        return false;
    }

    /**
     * 是否有权限
     *
     * @return
     */
    private boolean isPermissionNone(NFaction gang, Message request) {
        NFactionMember member = gang.getGangMember(player.getId());
        if (GangDefine.GANG_POSITION_PRESIDENT != member.getPosition() && GangDefine.GANG_POSITION_VICE_PRESIDENT != member.getPosition()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GANG_PERMISSION_NONE);
            return true;
        }
        return false;
    }


    /**
     * 1112 公会踢人
     *
     * @param request
     */
    public void processFactionDismiss(Message request) {
        if (this.isGangNone(request)) {
            return;
        }

        NFaction gang = player.getFaction();
        if (this.isOperationQuick(gang, request)) {
            return;
        }
        if (this.isPermissionNone(gang, request)) {
            return;
        }

        int playerId = request.readInt();

        NFactionMember disMissMember = gang.getGangMember(playerId);
        NFactionMember member = gang.getGangMember(player.getId());
        if (member.getPosition() < disMissMember.getPosition()) {
            Message message = new Message(EMessage.FACTION_DISMISS.CMD(), request.getChannel());
            message.setInt(playerId);
            gameRole.sendMessage(message);

            IGameRole role = GameWorld.getPtr().getGameRole(playerId);
            Player dismissPlayer = role.getPlayer();
            dismissPlayer.setGang(null);
            if (role.isOnline()) {
                role.getGameRole().getGangManager().processGangInfo(null);
            }
            gang.removeGangMember(playerId);
            gang.addLog(new GangLog(NfactionLogType.DISMISS.getValue(), dismissPlayer.getName(), player.getName()));
            gameRole.getDbManager().nFactionDao.removeGangMember(disMissMember);

        } else {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
        }
    }


    /**
     * 1881 公会审核
     *
     * @param request
     */
    public void processFactionAdopt(Message request) {
        if (this.isGangNone(request)) {
            return;
        }
        NFaction gang = player.getFaction();
        if (this.isOperationQuick(gang, request)) {
            return;
        }
        if (this.isPermissionNone(gang, request)) {
            return;
        }
        if (gang.isMemberFull()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GANG_MEMBER_FULL);
            return;
        }

        int playerId = request.readInt();
        byte isAdopt = request.readByte();

        if (!gang.isHaveApply(playerId)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }


        if (isAdopt == 1) {
            IGameRole role = GameWorld.getPtr().getGameRole(playerId);
            Player applyPlayer = role.getPlayer();
            if (applyPlayer.getFaction() != null) {
                gameRole.putErrorMessage(ErrorDefine.ERROR_GANG_PLAYER_JOINED);

            } else {
                applyPlayer.setFaction(gang);
                if (role.isOnline()) {
                    //role.getGameRole().getNFactionManager().processFactionInfo(request);
                }
                NFactionMember gangMember = new NFactionMember(applyPlayer, gang.getId(), GangDefine.GANG_POSITION_MEMBER, gameRole.getDungeonManager().getDungeonGangPass());
                gang.getMemberMap().put(gangMember.getPlayerId(), gangMember);
                //gang.addLog(new GangLog(EGangLogType.JOIN.getValue(), applyPlayer.getName()));
                gameRole.getDbManager().nFactionDao.addGangMember(gangMember);
            }
        }

        Message message = new Message(EMessage.FACTION_ADOPT.CMD(), request.getChannel());
        message.setInt(playerId);
        message.setByte(isAdopt);
        gameRole.sendMessage(message);
        gang.removeApply(playerId);
        //gameRole.getDbManager().nFactionDao.updateApply(gang);
    }


    public boolean isBangZhu() {
        NFaction faction = player.getFaction();
        if (faction == null) {
            return false;
        }
        return faction.getSimplePlayer().getId() == player.getId();
    }


    /**
     * 1111 公会任命
     *
     * @param request
     */
    public void processFactionAppoint(Message request) {
        if (this.isGangNone(request)) {
            return;
        }
        NFaction gang = player.getFaction();
        if (this.isOperationQuick(gang, request)) {
            return;
        }
        if (this.isPermissionNone(gang, request)) {
            return;
        }

        int playerId = request.readInt();
        byte position = request.readByte();

        NFactionMember memberA = gang.getGangMember(player.getId());
        NFactionMember memberB = gang.getGangMember(playerId);
        if (memberA.getPosition() < memberB.getPosition() && memberA.getPosition() < position) {
            if (gang.isHavePosition(position)) {
                memberB.setPosition(position);
                Message message = new Message(EMessage.FACTION_APPOINT.CMD(), request.getChannel());
                message.setInt(playerId);
                message.setByte(position);
                gameRole.sendMessage(message);

                IGameRole role = GameWorld.getPtr().getGameRole(playerId);
                if (role.isOnline()) {
                    role.getGameRole().getGangManager().processGangMemberList(null);
                }
                //gang.addLog(new GangLog(NfactionLogType.APPOINT.getValue(),memberB.getSimplePlayer().getName(),player.getName(),position));
                gameRole.getDbManager().nFactionDao.updateGangMemberPosition(memberB);
            } else {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GANG_POSITION_FULL);
            }
        } else if (memberA.getPosition() == NFactionDefine.GANG_POSITION_PRESIDENT && NFactionDefine.GANG_POSITION_PRESIDENT == position) {
            memberA.setPosition(NFactionDefine.GANG_POSITION_MEMBER);
            memberB.setPosition(NFactionDefine.GANG_POSITION_PRESIDENT);

            IGameRole role = GameWorld.getPtr().getGameRole(playerId);
            if (role.isOnline()) {
                role.getGameRole().getGangManager().processGangMemberList(null);
            }
            gang.setSimplePlayer(role.getPlayer());
            //gang.addLog(new GangLog(NfactionLogType.APPOINT.getValue(),memberB.getSimplePlayer().getName(),player.getName(),GangDefine.GANG_POSITION_PRESIDENT));
            //this.processGangMemberList(request);
            gameRole.getDbManager().nFactionDao.updateGangOwner(gang);
            gameRole.getDbManager().nFactionDao.updateGangMemberPosition(memberA);
            gameRole.getDbManager().nFactionDao.updateGangMemberPosition(memberB);
        } else {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
        }
    }


    /**
     * 1113 公会解散
     *
     * @param request
     */
    public void processGangOver(Message request) {
        if (this.isGangNone(request)) {
            return;
        }
        Gang gang = player.getGang();
        GangMember member = gang.getGangMember(player.getId());
        if (member.getPosition() == GangDefine.GANG_POSITION_PRESIDENT) {
            GameGangManager.getInstance().removeGang(gang);

            for (GangMember gangMember : gang.getMemberMap().values()) {
                IGameRole role = GameWorld.getPtr().getGameRole(gangMember.getPlayerId());
                role.getPlayer().setGang(null);
                if (role.isOnline()) {
                    role.getGameRole().getGangManager().processGangInfo(null);
                }
            }

            gameRole.getDbManager().gangDao.removeGang(gang);
            gameRole.getDbManager().gangDao.removeGangAllMember(gang);
        } else {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
        }
        gameRole.sendTick(request);
    }

    /**
     * 1114 公会退出
     *
     * @param request
     */
    public void processFactionExit(Message request) {
        if (this.isGangNone(request)) {
            return;
        }
        if (GangService.getPtr().isOpenFight()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GANG_FIGHTING_EXIT);
            return;
        }
        NFaction gang = player.getFaction();
        NFactionMember member = gang.getGangMember(player.getId());
        if (NFactionDefine.GANG_POSITION_PRESIDENT != member.getPosition()) {
            gang.removeGangMember(member.getPlayerId());
            gang.addLog(new GangLog(NfactionLogType.EXIT.getValue(), player.getName()));

            Message message = new Message(EMessage.FACTION_EXIT.CMD(), request.getChannel());
            message.setByte((byte) 1);
            gameRole.sendMessage(message);
            player.setFaction(null);
            gameRole.getDbManager().nFactionDao.removeGangMember(member);
//	        gameRole.getDbManager().nFactionDao.updateDungeonTodayFirst(gang);
//        	gameRole.getDbManager().nFactionDao.updateDungeonFirst(gang);
//        	gameRole.getDbManager().nFactionDao.updateDungeonPass(gang);
        } else {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
        }

    }

    /**
     * 帮派技能
     */
    public void processFactionSkillPanel(Message request) {
        Message msg = new Message(EMessage.FACTION_SKILL_PANEL.CMD(), request.getChannel());
        int[] skills = player.getFactionSkill();
        msg.setByte(skills.length);
        for (int skill : skills) {
            NFactionSkillData factionSkill = NFactionModel.getNFactionSkillDataMap().get(skill);
            if (factionSkill == null) {
                msg.setShort(0);
            } else {
                msg.setShort(factionSkill.getLevel());
            }
        }
        gameRole.sendMessage(msg);
    }


    /**
     * 公会技能学习
     *
     * @param request
     */
    public void processFactionSkill(Message request) {
        if (this.isGangNone(request)) {
            return;
        }

        byte pos = request.readByte();
        if (pos < 0 || pos > NFactionDefine.FACTION_SKILL_COUNT) {
            return;
        }
        int skill = player.getFactionSkill()[pos];
        short level = 0;
        if (skill > 0) {
            NFactionSkillData factionSkill = NFactionModel.getNFactionSkillDataMap().get(skill);
            level = factionSkill.getLevel();
        }
        level = (short) (level + 1);
        NFactionSkillData factionSkill = NFactionModel.getfactionSkillKey(pos, level);
        if (factionSkill == null) {
            return;
        }
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        if (!gameRole.getPackManager().useGoods(factionSkill.getCost_item(), EGoodsChangeType.FACTION_SKILL_CONSUME, enumSet)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }

        player.getFactionSkill()[pos] = factionSkill.getId();
        Message message = new Message(EMessage.FACTION_SKILL_UPGRADE.CMD(), request.getChannel());
        message.setByte(pos);
        gameRole.sendMessage(message);
        //通知公会技能升级消息
        //gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.GANG_SKILL_UP,1,enumSet));

        enumSet.add(EPlayerSaveType.FACTION_SKILL);
        gameRole.savePlayer(enumSet);
    }


    /**
     * 1121 公会日志
     *
     * @param request
     */
    public void processFactionLog(Message request) {
        if (this.isGangNone(request)) {
            return;
        }
        NFaction gang = player.getFaction();
        Message message = new Message(EMessage.FACTION_LOG.CMD(), request.getChannel());
        message.setByte(gang.getGangLogQueue().size());
        for (GangLog log : gang.getGangLogQueue()) {
            message.setByte(log.getType());
            message.setString(log.getFirstName());
            if (log.getType() == NfactionLogType.DISMISS.getValue()) {
                message.setString(log.getSecondName());
            }
            message.setLong(log.getTime());

        }
        gameRole.sendMessage(message);
    }

    /**
     * 1875 帮会任务列表
     *
     * @param request
     */
    public void processFactionTaskList(Message request) {
        NFaction faction = player.getFaction();
        if (faction == null) {
            return;
        }
        Message message = new Message(EMessage.FACTION_TASK_LIST.CMD(), request.getChannel());


        message.setShort(faction.getActiveLevel());
        message.setInt(faction.getActiveExp());
        message.setByte(player.getDayData().getGangMissionList().size());
        for (GangMission mission : player.getDayData().getGangMissionList()) {
            mission.getMessage(message);
        }
        gameRole.sendMessage(message);
    }


    /****
     *
     * 帮派任务每日活跃领取奖励面板
     * **/
    public void processFactionActiveMeiRiRewardPanel(Message request) {
        if (isGangNone(request)) {
            return;
        }
        Message msg = new Message(EMessage.FACTION_MEIRI_ACTIVE_REWARD_LIST.CMD(), request.getChannel());
        Set<Integer> list = player.getDayData().getFactionMeiRiTaskReward();
        msg.setByte(list.size());
        for (Integer id : list) {
            msg.setByte(id);
        }
        msg.setShort(player.getFaction().getActiveExp());
        gameRole.sendMessage(msg);
    }


    /****
     *
     * 帮派任务每日活跃获取奖励的领取
     * **/
    public void processFactionActiveMeiRiReward(Message request) {
        NFaction faction = player.getFaction();
        if (faction == null) {
            return;
        }
        byte id = request.readByte();
        if (faction.getActiveLevel() < id) {
            return;
        }
        NFactionActiveData data = NFactionModel.getNFactionActiveData(id);
        if (data == null) {
            return;
        }
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        gameRole.getPackManager().addGoods(data.getRewards(), EGoodsChangeType.FIGHT_DUNGEON_ADD, enumSet);
        Message msg = new Message(EMessage.FACTION_MEIRI_ACTIVE_REWARD.CMD(), request.getChannel());

        msg.setByte(id);
        gameRole.sendMessage(msg);
        gameRole.savePlayer(enumSet);
    }


    /***
     *
     * 帮派活跃等级提升
     */
    public void processFactionActiveUpLevel(Message request) {
        NFaction faction = player.getFaction();
        if (faction == null) {
            return;
        }
        faction.setActiveExp(700);
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        addAcviveExp(faction, 0, enumSet);
        Message msg = new Message(EMessage.FACTION_ACTIVE_UP_LEVEL.CMD(), request.getChannel());

        msg.setInt(faction.getActiveExp());
        msg.setShort(faction.getActiveLevel());
        gameRole.sendMessage(msg);

    }


    /**
     * 升级
     */
    public void addAcviveExp(NFaction faction, int exp, EnumSet<EPlayerSaveType> enumSet) {
        int maxLevel = NFactionModel.getNFactionActiveMap().size();
        if (faction.getActiveLevel() >= maxLevel) {
            return;
        }
        NFactionActiveData data = NFactionModel.getNFactionActiveData(faction.getActiveLevel() + 1);
        if (data == null) {
            return;
        }
        synchronized (NFactionManager.class) {
            int reult = faction.getActiveExp() + 0;
            int total = data.getExp();
            if (reult < total) {
                return;
            }
            reult -= total;
//			short level=faction.getActiveLevel(); 
//			while(reult>=total) { 
//				++level;
//				if(level>maxLevel) {
//					level=(short)maxLevel;
//					reult=0;
//					break;
//				}
//				reult-=total;
//				data= NFacTtionModel.getNFactionActiveData(level);
//				total=data.getExp(); 
//			}
            faction.setActiveExp(reult);
            faction.setActiveLevel(data.getLv());
        }

    }


    /**
     * 1125 帮会任务更新
     *
     * @param request
     */
    public void updateTask(NFactionTaskData taskData, GameEvent event) {
        if (player.getFaction() == null) {
            return;
        }
        GangMission gangMission = null;
        for (GangMission mission : player.getDayData().getGangMissionList()) {
            if (mission.getId() == taskData.getId()) {
                gangMission = mission;
            }
        }
        if (gangMission == null) {
            gangMission = new GangMission();
            gangMission.setId((byte) taskData.getId());
            gangMission.setProgress((byte) 1);
            player.getDayData().getGangMissionList().add(gangMission);

        } else {
            if (gangMission.getProgress() < taskData.getNum()) {
                gangMission.setProgress((byte) (gangMission.getProgress() + 1));
            }
        }

        synchronized (NFactionManager.class) {
            player.getFaction().addActiveExp(taskData.getRewards());
        }

        gameRole.getDbManager().nFactionDao.updateGangExp(player.getFaction());
        event.addPlayerSaveType(EPlayerSaveType.DAYDATA);
        //event.addPlayerSaveType(EPlayerSaveType.DONATE);


    }


    /**
     * 成员列表
     */
    public void processFactionMember(Message request) {
        NFaction faction = player.getFaction();
        if (faction == null) {
            return;
        }
        Message msg = new Message(EMessage.FACTION_MEMBER.CMD(), request.getChannel());
        msg.setByte(faction.getMemberMap().size());
        for (NFactionMember member : faction.getMemberMap().values()) {
            member.getMessage(msg);
        }

        gameRole.sendMessage(msg);
    }

    /**
     * 上香面板
     **/
    public void processSXPanel(Message request) {
        if (this.isGangNone(request)) {
            return;
        }
        NFaction faction = player.getFaction();
        Message msg = new Message(EMessage.FACTION_SHAGNXIANG_PANEL.CMD(), request.getChannel());
        DayData dayData = player.getDayData();
        msg.setByte(dayData.getIncense());
        msg.setShort(faction.getXianghuo());
        msg.setShort(player.getFaction().getSxCount());
        dayData.getSxRewards();
        if (dayData.getSxRewards() == null) {
            msg.setByte(0);
        } else {
            msg.setByte(dayData.getSxRewards().length);
            for (byte id : dayData.getSxRewards()) {
                msg.setByte(id);
            }
        }
        gameRole.sendMessage(msg);
    }

    public void processSXJiLu(Message request) {
        NFaction faction = player.getFaction();
        if (faction == null) {
            return;
        }

        Queue<GangLog> list = faction.getShangxiangLogQueue();

        Message msg = new Message(EMessage.FACTION_SHAGNXIANG_JILU.CMD(), request.getChannel());
        msg.setByte(list.size());
        for (GangLog gangLog : list) {
            msg.setByte(gangLog.getByteData());
            msg.setString(gangLog.getFirstName());
            msg.setLong(gangLog.getTime());
        }
        gameRole.sendMessage(msg);
    }

    /**
     * 上香奖励领取
     **/
    public void processSXRewardLingqul(Message request) {
        if (this.isGangNone(request)) {
            return;
        }

        byte id = request.readByte();
        NFactionSXRewardData data = NFactionModel.getNFactionSXRewardDataMap(id);
        if (data == null) {
            return;
        }

        if (data.getNeedFrag() > player.getFaction().getXianghuo()) {
            return;
        }
        byte[] rewards = player.getDayData().getSxRewards();
        if (rewards == null) {
            rewards = new byte[NFactionDefine.FACTION_SX_REWARD_NUM];
        }
        if (rewards[id - 1] > 0) {
            return;
        }
        rewards[id - 1] = id;
        Message msg = new Message(EMessage.FACTION_SHAGNXIANG_REARD_LINGQU.CMD(), request.getChannel());
        msg.setByte(id);
        gameRole.sendMessage(msg);
    }


    /**
     * 公会上香
     *
     * @param request
     */
    public void processFactionShangXiang(Message request) {
        if (this.isGangNone(request)) {
            return;
        }
        NFaction gang = player.getFaction();
        if (this.isOperationQuick(gang, request)) {
            return;
        }
        byte id = request.readByte();
        int idNum = id;
        NFactionSXData gangIncenseData = NFactionModel.getNFactionSXDataMap().get(idNum);
        if (gangIncenseData == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        if (player.getDayData().getIncense() >= NFactionDefine.GANG_INCENSE_NUM) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GANG_INCENSE_LESS);
            return;
        }


        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        // check
//		if (!gameRole.getPackManager().useGoods(gangIncenseData.getCost(), EGoodsChangeType.FACTION_SHAGNXIANG_CONSUME, saves))
//		{
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//			return;
//		} 
        DayData dayData = player.getDayData();
        dayData.setIncense(id);
        int num = gangIncenseData.getReward_fire().getN();
        player.getDayData().setXiangHuo(dayData.getXiangHuo() + num);
        gang.addExp(gangIncenseData.getReward_gong().getN());
        gang.addSXLog(new GangLog(EGangLogType.INCENSE.getValue(), player.getName(), id));
        synchronized (NFactionManager.class) {
            gang.addSxCount();
            gang.setXianghuo(num + gang.getXianghuo());
        }

        gameRole.getDbManager().nFactionDao.updateGangExp(gang);
        //gameRole.getPackManager().addGoods(gangIncenseData.getReward_capital(), EGoodsChangeType.GANG_DONATE_ADD, saves);

        Message message = new Message(EMessage.FACTION_SHAGNXIANG.CMD(), request.getChannel());
        message.setByte(player.getDayData().getIncense());
        message.setShort(gang.getSxCount());
        message.setShort(gang.getXianghuo());
        message.setLong(System.currentTimeMillis());
        gameRole.sendMessage(message);
        saves.add(EPlayerSaveType.DAYDATA);
        //gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.GANG_DONATE,1,saves));
        gameRole.savePlayer(saves);
    }


    public void handleEvent(GameEvent event) {
        for (ITaskHandler handler : handlers) {
            handler.handleEvent(gameRole, event);
        }
    }


}
