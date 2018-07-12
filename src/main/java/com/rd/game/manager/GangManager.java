package com.rd.game.manager;

import com.google.common.base.Preconditions;
import com.rd.bean.drop.DropData;
import com.rd.bean.gang.*;
import com.rd.bean.gang.fight.*;
import com.rd.bean.gangstarcraft.StarcraftFighter;
import com.rd.bean.goods.Goods;
import com.rd.bean.player.Player;
import com.rd.bean.player.SimplePlayer;
import com.rd.common.FightCommon;
import com.rd.common.GameCommon;
import com.rd.common.GangBossService;
import com.rd.common.GangService;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.*;
import com.rd.game.GameGangManager;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.game.IGameRole;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.GameEvent;
import com.rd.game.event.IEventListener;
import com.rd.model.*;
import com.rd.model.data.*;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import com.rd.util.StringUtil;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.Map.Entry;

/**
 * <p>Title: 公会管理</p>
 * <p>Description: 公会管理</p>
 * <p>Company: 北京万游畅想科技有限公司</p>
 *
 * @author ---
 * @version 1.0
 * @data 2016年12月28日 下午2:40:08
 */
public class GangManager implements IEventListener {
    private static final Logger logger = Logger.getLogger(GangManager.class);
    private GameRole gameRole;
    private Player player;
    private GameGangManager GGM = GameGangManager.getInstance();

    public GangManager(GameRole role) {
        this.gameRole = role;
        this.player = role.getPlayer();
    }

    /**
     * 1101 公会信息
     *
     * @param request
     */
    public void processGangInfo(Message request) {
        Message message = new Message(MessageCommand.GANG_INFO_MESSAGE);
        if (player.getGang() == null) {
            message.setBool(false);
        } else {
            message.setBool(true);
            Gang gang = player.getGang();
            gang.getMessage(message);
            //今日普通上香次数
            message.setByte(player.getDayData().getIncense());
            //今日vip上香次数
            message.setByte(player.getDayData().getIncenseVip());
            //帮会转盘已转次数
            message.setByte(player.getDayData().getGangTurnableNum());
        }
        if (request != null) {
            message.setChannel(request.getChannel());
            gameRole.sendMessage(message);
        } else {
            gameRole.putMessageQueue(message);
        }
    }

    /**
     * 1102 公会列表
     *
     * @param request
     */
    public void processGangList(Message request) {
        short page = request.readShort();
        short pageAll = GameGangManager.getInstance().getPageAll();
        if (page > pageAll) {
            page = pageAll;
        }
        if (page < 1) {
            page = 1;
        }
        int startNum = (page - 1) * GangDefine.GANG_LIST_PAGE_NUM;
        int endNum = page * GangDefine.GANG_LIST_PAGE_NUM;
        if (endNum > GameGangManager.getInstance().getGangList().size()) {
            endNum = GameGangManager.getInstance().getGangList().size();
        }

        Message message = new Message(MessageCommand.GANG_LIST_MESSAGE, request.getChannel());
        message.setShort(page);
        message.setShort(pageAll);
        message.setByte(endNum - startNum);
        for (int i = startNum; i < endNum; ++i) {
            GameGangManager.getInstance().getGangList().get(i).getSimpleMessage(message);
        }
        gameRole.sendMessage(message);
    }

    /**
     * 1103 公会创建
     *
     * @param request
     */
    public void processGangCreate(Message request) {
        if (GangDefine.CREATE_GANG_DIAMOND <= player.getDiamond()) {
            //if(player.getVipLevel()>0){
            String name = request.readString().trim();
            byte badge = request.readByte();
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

            short errorCode = GameGangManager.getInstance().createGang(gameRole, name, badge);
            if (errorCode > 0) {
                gameRole.sendErrorTipMessage(request, errorCode);
                return;
            }

            EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
            gameRole.getPackManager().useGoods(new DropData(EGoodsType.DIAMOND, 0, GangDefine.CREATE_GANG_DIAMOND), EGoodsChangeType.CREATE_GANG_CONSUME, enumSet);

            processGangInfo(request);

            //	        ChatService.broadcastPlayerMsg(player, EBroadcast.GangCreate, name);

            gameRole.savePlayer(enumSet);
        } else {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_VIP_LEVEL_LESS);
        }
//		}else{
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
//		}
    }

    /**
     * 1104 公会搜索
     *
     * @param request
     */
    public void processGangSearch(Message request) {
        int gangId = request.readInt();
        Gang gang = GameGangManager.getInstance().getGang(gangId);

        Message message = new Message(MessageCommand.GANG_SEARCH_MESSAGE, request.getChannel());
        if (null == gang) {
            message.setBool(false);
        } else {
            message.setBool(true);
            gang.getSimpleMessage(message);
        }
        gameRole.sendMessage(message);
    }

    /**
     * 1105 公会宣言修改
     *
     * @param request
     */
    public void processGangDeclarationModify(Message request) {
        if (this.isGangNone(request)) {
            return;
        }
        Gang gang = player.getGang();
        if (this.isOperationQuick(gang, request)) {
            return;
        }
        if (this.isPermissionNone(gang, request)) {
            return;
        }

        String declaration = request.readString();
        if (declaration.length() > GangDefine.GANG_DECLARATION_MAX_LENGTH) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_CHAT_LENGTH_LONG);
            return;
        }
        if (!GameCommon.checkReservedWord(declaration)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_INVALID_STRING);
            return;
        }

        declaration = WordSensitiveModel.replaceSensitive(declaration);

        Message message = new Message(MessageCommand.GANG_DECLARATION_MODIFY_MESSAGE, request.getChannel());
        message.setString(declaration);
        gameRole.sendMessage(message);

        if (!gang.getDeclaration().equals(declaration)) {
            gang.setDeclaration(declaration);
            gameRole.getDbManager().gangDao.updateDeclaration(gang.getId(), declaration);
        }
    }

    /**
     * 1106 公会公告修改
     *
     * @param request
     */
    public void processGangNoticeModify(Message request) {
        if (this.isGangNone(request)) {
            return;
        }
        Gang gang = player.getGang();
        if (this.isOperationQuick(gang, request)) {
            return;
        }
        if (this.isPermissionNone(gang, request)) {
            return;
        }

        String notice = request.readString();
        if (notice.length() > GangDefine.GANG_NOTICE_MAX_LENGTH) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_CHAT_LENGTH_LONG);
            return;
        }
        if (!GameCommon.checkReservedWord(notice)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_INVALID_STRING);
            return;
        }

        notice = WordSensitiveModel.replaceSensitive(notice);

        Message message = new Message(MessageCommand.GANG_NOTICE_MODIFY_MESSAGE, request.getChannel());
        message.setString(notice);
        gameRole.sendMessage(message);

        if (!gang.getNotice().equals(notice)) {
            gang.setNotice(notice);
            gameRole.getDbManager().gangDao.updateNotice(gang.getId(), notice);
        }
    }

    /**
     * 1107 公会转生等级限制
     *
     * @param request
     */
    public void processGangLimitLevel(Message request) {
        if (this.isGangNone(request)) {
            return;
        }
        Gang gang = player.getGang();
        if (this.isOperationQuick(gang, request)) {
            return;
        }
        if (this.isPermissionNone(gang, request)) {
            return;
        }

        short limitLevel = request.readShort();

        if (limitLevel > ReinModel.getReinMax()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_REIN_MAX);
            return;
        }

        Message message = new Message(MessageCommand.GANG_LIMIT_LEVEL_MESSAGE, request.getChannel());
        message.setShort(limitLevel);
        gameRole.sendMessage(message);

        if (limitLevel != gang.getLimitLevel()) {
            gang.setLimitLevel(limitLevel);
            gameRole.getDbManager().gangDao.updateLimitLevel(gang.getId(), limitLevel);
        }
    }

    /**
     * 1108 公会自动审核
     *
     * @param request
     */
    public void processGangAutoAdopt(Message request) {
        if (this.isGangNone(request)) {
            return;
        }
        Gang gang = player.getGang();
        if (this.isOperationQuick(gang, request)) {
            return;
        }
        if (this.isPermissionNone(gang, request)) {
            return;
        }

        boolean isAuto = request.readBoolean();

        Message message = new Message(MessageCommand.GANG_AUTO_ADOPT_MESSAGE, request.getChannel());
        message.setBool(isAuto);
        gameRole.sendMessage(message);

        if (isAuto != gang.isAutoAdopt()) {
            gang.setAutoAdopt(isAuto);
            gameRole.getDbManager().gangDao.updateAutoAdopt(gang.getId(), isAuto);
        }
    }

    /**
     * 1109 公会成员列表
     *
     * @param request
     */
    public void processGangMemberList(Message request) {
        if (this.isGangNone(request)) {
            return;
        }
        Gang gang = player.getGang();
        Message message = new Message(MessageCommand.GANG_MEMBER_LIST_MESSAGE);

        message.setShort(gang.getMemberMap().size());
        for (GangMember member : gang.getMemberMap().values()) {
            member.getMessage(message);
        }
        //是否可弹劾
        message.setByte(gang.isImpeachment());
        if (request != null) {
            message.setChannel(request.getChannel());
            gameRole.sendMessage(message);
        } else {
            gameRole.putMessageQueue(message);
        }
    }

    /**
     * 1110 公会申请
     *
     * @param request
     */
    public void processGangApply(Message request) {
        if (player.getGang() != null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        int gangId = request.readInt();
        Gang gang = GameGangManager.getInstance().getGang(gangId);
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
        if (gang.isAutoAdopt()) {
            GangMember gangMember = new GangMember(player, gangId, GangDefine.GANG_POSITION_MEMBER, gameRole.getDungeonManager().getDungeonGangPass());
            gang.getMemberMap().put(gangMember.getPlayerId(), gangMember);
            gang.addLog(new GangLog(EGangLogType.JOIN.getValue(), player.getName()));
            player.setGang(gang);
            this.processGangInfo(request);

            gameRole.getDbManager().gangDao.addGangMember(gangMember);
        } else {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GANG_ALREADY_APPLY);

            gang.getApplyList().add(player);
            gameRole.getDbManager().gangDao.updateApply(gang);
        }
    }

    /**
     * 1111 公会任命
     *
     * @param request
     */
    public void processGangAppoint(Message request) {
        if (this.isGangNone(request)) {
            return;
        }
        Gang gang = player.getGang();
        if (this.isOperationQuick(gang, request)) {
            return;
        }
        if (this.isPermissionNone(gang, request)) {
            return;
        }

        int playerId = request.readInt();
        byte position = request.readByte();

        GangMember memberA = gang.getGangMember(player.getId());
        GangMember memberB = gang.getGangMember(playerId);
        if (memberA.getPosition() < memberB.getPosition() && memberA.getPosition() < position) {
            if (gang.isHavePosition(position)) {
                memberB.setPosition(position);
                Message message = new Message(MessageCommand.GANG_APPOINT_MESSAGE, request.getChannel());
                message.setInt(playerId);
                message.setByte(position);
                gameRole.sendMessage(message);

                IGameRole role = GameWorld.getPtr().getGameRole(playerId);
                if (role.isOnline()) {
                    role.getGameRole().getGangManager().processGangMemberList(null);
                }
                gang.addLog(new GangLog(EGangLogType.APPOINT.getValue(), memberB.getSimplePlayer().getName(), player.getName(), position));
                gameRole.getDbManager().gangDao.updateGangMemberPosition(memberB);
            } else {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GANG_POSITION_FULL);
            }
        } else if (memberA.getPosition() == GangDefine.GANG_POSITION_PRESIDENT && GangDefine.GANG_POSITION_PRESIDENT == position) {
            memberA.setPosition(GangDefine.GANG_POSITION_MEMBER);
            memberB.setPosition(GangDefine.GANG_POSITION_PRESIDENT);

            IGameRole role = GameWorld.getPtr().getGameRole(playerId);
            if (role.isOnline()) {
                role.getGameRole().getGangManager().processGangMemberList(null);
            }
            gang.setSimplePlayer(role.getPlayer());
            gang.addLog(new GangLog(EGangLogType.APPOINT.getValue(), memberB.getSimplePlayer().getName(), player.getName(), GangDefine.GANG_POSITION_PRESIDENT));
            this.processGangMemberList(request);
            gameRole.getDbManager().gangDao.updateGangOwner(gang);
            gameRole.getDbManager().gangDao.updateGangMemberPosition(memberA);
            gameRole.getDbManager().gangDao.updateGangMemberPosition(memberB);
        } else {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
        }
    }

    /**
     * 1112 公会踢人
     *
     * @param request
     */
    public void processGangDismiss(Message request) {
        if (this.isGangNone(request)) {
            return;
        }
        if (GangService.getPtr().isOpenFight()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GANG_FIGHTING_EXIT);
            return;
        }
        Gang gang = player.getGang();
        if (this.isOperationQuick(gang, request)) {
            return;
        }
        if (this.isPermissionNone(gang, request)) {
            return;
        }

        int playerId = request.readInt();

        GangMember disMissMember = gang.getGangMember(playerId);
        GangMember member = gang.getGangMember(player.getId());
        if (member.getPosition() < disMissMember.getPosition()) {
            Message message = new Message(MessageCommand.GANG_DISMISS_MESSAGE, request.getChannel());
            message.setInt(playerId);
            gameRole.sendMessage(message);

            IGameRole role = GameWorld.getPtr().getGameRole(playerId);
            Player dismissPlayer = role.getPlayer();
            dismissPlayer.setGang(null);
            if (role.isOnline()) {
                role.getGameRole().getGangManager().processGangInfo(null);
            }
            gang.removeGangMember(playerId);
            gang.addLog(new GangLog(EGangLogType.DISMISS.getValue(), dismissPlayer.getName(), player.getName()));
            gameRole.getDbManager().gangDao.removeGangMember(disMissMember);
            gameRole.getDbManager().gangDao.updateDungeonTodayFirst(gang);
            gameRole.getDbManager().gangDao.updateDungeonFirst(gang);
            gameRole.getDbManager().gangDao.updateDungeonPass(gang);
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
    public void processGangExit(Message request) {
        if (this.isGangNone(request)) {
            return;
        }
        if (GangService.getPtr().isOpenFight()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GANG_FIGHTING_EXIT);
            return;
        }
        Gang gang = player.getGang();
        GangMember member = gang.getGangMember(player.getId());
        if (GangDefine.GANG_POSITION_PRESIDENT != member.getPosition()) {
            gang.removeGangMember(member.getPlayerId());
            gang.addLog(new GangLog(EGangLogType.EXIT.getValue(), player.getName()));

            Message message = new Message(MessageCommand.GANG_EXIT_MESSAGE, request.getChannel());
            gameRole.sendMessage(message);

            player.setGang(null);
            gameRole.getDbManager().gangDao.removeGangMember(member);
            gameRole.getDbManager().gangDao.updateDungeonTodayFirst(gang);
            gameRole.getDbManager().gangDao.updateDungeonFirst(gang);
            gameRole.getDbManager().gangDao.updateDungeonPass(gang);
        } else {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
        }
    }

    /**
     * 1115 公会申请列表
     *
     * @param request
     */
    public void processGangApplyList(Message request) {
        if (this.isGangNone(request)) {
            return;
        }
        Gang gang = player.getGang();
        if (this.isOperationQuick(gang, request)) {
            return;
        }
        if (this.isPermissionNone(gang, request)) {
            return;
        }

        Message message = new Message(MessageCommand.GANG_APPLY_LIST_MESSAGE, request.getChannel());
        message.setShort(gang.getApplyList().size());
        for (SimplePlayer simplePlayer : gang.getApplyList()) {
            simplePlayer.getSimpleMessage(message);
        }
        gameRole.sendMessage(message);
    }

    /**
     * 1116 公会审核
     *
     * @param request
     */
    public void processGangAdopt(Message request) {
        if (this.isGangNone(request)) {
            return;
        }
        Gang gang = player.getGang();
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
        boolean isAdopt = request.readBoolean();

        if (!gang.isHaveApply(playerId)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        byte result = isAdopt == true ? (byte) 1 : (byte) 2;
        if (isAdopt) {
            IGameRole role = GameWorld.getPtr().getGameRole(playerId);
            Player applyPlayer = role.getPlayer();
            if (applyPlayer.getGang() != null) {
                gameRole.putErrorMessage(ErrorDefine.ERROR_GANG_PLAYER_JOINED);
                result = 0;
            } else {
                applyPlayer.setGang(gang);
                if (role.isOnline()) {
                    role.getGameRole().getGangManager().processGangInfo(null);
                }
                GangMember gangMember = new GangMember(applyPlayer, gang.getId(), GangDefine.GANG_POSITION_MEMBER, gameRole.getDungeonManager().getDungeonGangPass());
                gang.getMemberMap().put(gangMember.getPlayerId(), gangMember);
                gang.addLog(new GangLog(EGangLogType.JOIN.getValue(), applyPlayer.getName()));
                gameRole.getDbManager().gangDao.addGangMember(gangMember);
            }
        }

        Message message = new Message(MessageCommand.GANG_ADOPT_MESSAGE, request.getChannel());
        message.setInt(playerId);
        message.setByte(result);
        gameRole.sendMessage(message);

        gang.removeApply(playerId);
        gameRole.getDbManager().gangDao.updateApply(gang);
    }

    /**
     * 1117 公会捐献
     *
     * @param request
     */
    public void processDonate(Message request) {
        if (this.isGangNone(request)) {
            return;
        }
        Gang gang = player.getGang();
        if (this.isOperationQuick(gang, request)) {
            return;
        }
        byte id = request.readByte();
        GangIncenseData gangIncenseData = GangModel.getGangIncenseData(id);
        if (gangIncenseData == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        if (gangIncenseData.getCost().getT() == EGoodsType.GOLD.getId() && player.getDayData().getIncense() >= GangDefine.GANG_INCENSE_NUM) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GANG_INCENSE_LESS);
            return;
        }
        if (gangIncenseData.getCost().getT() == EGoodsType.DIAMOND.getId() && player.getDayData().getIncenseVip() >= VipModel.getVipWeal(player.getVipLevel(), EVipType.GANGTURNTABLE)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GANG_INCENSE_LESS);
            return;
        }

        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        // check
        if (!gameRole.getPackManager().useGoods(gangIncenseData.getCost(), EGoodsChangeType.GANG_DONATE_CONSUME, saves)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }

        if (gangIncenseData.getCost().getT() == EGoodsType.DIAMOND.getId()) {
            player.getDayData().setIncenseVip((byte) (player.getDayData().getIncenseVip() + 1));
        } else {
            player.getDayData().setIncense((byte) (player.getDayData().getIncense() + 1));
        }

        gang.addExp(gangIncenseData.getGangExp());
        gang.addLog(new GangLog(EGangLogType.INCENSE.getValue(), player.getName(), id));
        gameRole.getDbManager().gangDao.updateGangExp(gang);
        gameRole.getPackManager().addGoods(new DropData(EGoodsType.DONATE, 0, gangIncenseData.getDonate()), EGoodsChangeType.GANG_DONATE_ADD, saves);

        Message message = new Message(MessageCommand.GANG_DONATE_MESSAGE, request.getChannel());
        message.setByte(id);
        message.setByte(player.getDayData().getIncense());
        message.setByte(player.getDayData().getIncenseVip());
        gameRole.sendMessage(message);

        saves.add(EPlayerSaveType.DAYDATA);

        gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.GANG_DONATE, 1, saves));
        gameRole.savePlayer(saves);

        GameGangManager.getInstance().sortGang();
    }

    /**
     * 1118 公会技能学习
     *
     * @param request
     */
    public void processSkill(Message request) {
//    	if(this.isGangNone(request)){
//			return;
//		}
//		
//		byte idx=request.readByte();
//    	byte skillId=request.readByte();
//    	
//    	Character character=player.getCharacter(idx);
//		if(character==null){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		
//		Gang gang=player.getGang();
//		GangData gangData=GangModel.getGangData(gang.getLevel());
//		
//		GangSkillData data;
//		
//		short level=character.getGangSkillLevel(skillId);
//		++level;
//		if(gangData.getSkillMax()>=level){
//			data=GangModel.getGangSkillData(skillId,level);
//		}else{
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GANG_LEVEL_LESS);
//			return;
//		}
//    	
//    	if(data==null){
//    		gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//    	}else{    	
//    		EnumSet<EPlayerSaveType> enumSet=EnumSet.noneOf(EPlayerSaveType.class);
//			if (!gameRole.getPackManager().useGoods(data.getCostList(), EGoodsChangeType.GANG_SKILL_CONSUME,enumSet)) {
//				gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//				return;
//			}
//			
//			character.addGangSkillLevel(skillId, level);
//    		
//    		Message message = new Message(MessageCommand.GANG_SKILL_LEVELUP, request.getChannel());
//    		message.setByte(idx);
//    		message.setByte(skillId);
//    		message.setShort(level);
//            gameRole.sendMessage(message); 
//            
//            //通知公会技能升级消息
//			//gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.GANG_SKILL_UP,1,enumSet));
//            
//            enumSet.add(EPlayerSaveType.CHA_GANGSKILL);
//            gameRole.saveData(idx, enumSet);
//    	}
    }

    /**
     * 1122 公会特殊技能学习
     *
     * @param request
     */
    public void processSkill2(Message request) {
//    	if(this.isGangNone(request)){
//			return;
//		}
//		
//		byte idx=request.readByte();
//    	byte skillId=request.readByte();
//    	
//    	Character character=player.getCharacter(idx);
//		if(character==null){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		
//		Gang gang=player.getGang();
//		GangData gangData=GangModel.getGangData(gang.getLevel());
//		
//		GangSkill2Data data;
//		short level=character.getGangSkill2Level(skillId);
//		++level;
//		if(gangData.getSkill2Max()>=level){
//			data=GangModel.getGangSkill2Data(skillId,level);
//		}else{
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GANG_LEVEL_LESS);
//			return;
//		}
//    	
//    	if(data==null){
//    		gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//    	}else{    	
//    		EnumSet<EPlayerSaveType> enumSet=EnumSet.noneOf(EPlayerSaveType.class);
//			if (!gameRole.getPackManager().useGoods(data.getCostList(), EGoodsChangeType.GANG_SKILL_CONSUME,enumSet)) {
//				gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//				return;
//			}
//			
//			character.addGangSkill2Level(skillId, data.getExp(), data.getExpMax());
//    		
//    		Message message = new Message(MessageCommand.GANG_SKILL2_LEVELUP, request.getChannel());
//    		message.setByte(idx);
//    		character.getGangSkill2(skillId).getMessage(message);
//            gameRole.sendMessage(message); 
//            
//            //通知公会技能升级消息
//			//gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.GANG_SKILL_UP,1,enumSet));
//            
//			enumSet.add(EPlayerSaveType.CHA_GANGSKILL2);
//            gameRole.saveData(idx, enumSet);
//    	}
    }

    /**
     * 1119 公会转盘
     *
     * @param request
     */
    public void processDial(Message request) {
        if (this.isGangNone(request)) {
            return;
        }
        Gang gang = player.getGang();
        GangData gangData = GangModel.getGangData(gang.getLevel());
        Preconditions.checkNotNull(gangData, "GangManager.processDial() failed. Unexpected level = " + gang.getLevel());

        byte turntableNum = player.getDayData().getGangTurnableNum();
        if (turntableNum >= gangData.getTurntableMax()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        ++turntableNum;

        //消耗
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        if (!gameRole.getPackManager().useGoods(new DropData(EGoodsType.DONATE, 0, 100), EGoodsChangeType.GANG_DIAL_CONSUME, saves)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }
        //增加
        List<DropData> reward = DropModel.getDropGroupData(GangDefine.GANG_TURNTABLE_DROPID).getRandomDrop();

        for (DropData data : reward) {
            if (data.getT() == EGoodsType.ITEM.getId() && data.getG() == GoodsDefine.ITEM_ID_LEFT_EYE) {
                GameGangManager.getInstance().addGangTurntableLog(new GangTurntableLog(player.getName(), data.getT(), data.getG()));
            } else if (data.getT() == EGoodsType.ITEM.getId() && data.getG() == GoodsDefine.ITEM_ID_RIGHT_EYE) {
                GameGangManager.getInstance().addGangTurntableLog(new GangTurntableLog(player.getName(), data.getT(), data.getG()));
            } else if (data.getT() == EGoodsType.BOX.getId() && data.getG() == GoodsDefine.BOX_ID_ORANGE_SOUL_GIFT) {
                GameGangManager.getInstance().addGangTurntableLog(new GangTurntableLog(player.getName(), data.getT(), data.getG()));
            }
        }

        gameRole.getPackManager().addGoods(reward, EGoodsChangeType.GANG_DIAL_ADD, saves);

        //发送消息
        Message msg = new Message(MessageCommand.GANG_DIAL_MESSAGE, request.getChannel());
        msg.setByte(turntableNum);
        msg.setByte(reward.size());
        for (DropData data : reward) {
            data.getMessage(msg);
        }
        gameRole.sendMessage(msg);

        player.getDayData().setGangTurnableNum(turntableNum);
        saves.add(EPlayerSaveType.DAYDATA);
        gameRole.savePlayer(saves);
    }

    /**
     * 1121 公会日志
     *
     * @param request
     */
    public void processLog(Message request) {
        if (this.isGangNone(request)) {
            return;
        }
        Gang gang = player.getGang();
        Message message = new Message(MessageCommand.GANG_LOG_MESSAGE, request.getChannel());
        message.setByte(gang.getGangLogQueue().size());
        for (GangLog log : gang.getGangLogQueue()) {
            message.setByte(log.getType());
            if (log.getType() == EGangLogType.LEVELUP.getValue()) {
                message.setByte(log.getByteData());
            } else {
                message.setString(log.getFirstName());
                if (log.getType() == EGangLogType.DISMISS.getValue()) {
                    message.setString(log.getSecondName());
                } else if (log.getType() == EGangLogType.INCENSE.getValue()) {
                    message.setByte(log.getByteData());
                } else if (log.getType() == EGangLogType.APPOINT.getValue()) {
                    message.setString(log.getSecondName());
                    message.setByte(log.getByteData());
                } else if (log.getType() == EGangLogType.ASSIGN.getValue()) {
                    message.setString(log.getSecondName());
                    message.setShort(log.getBoxId());
                    message.setShort(log.getBoxNum());
                } else if (log.getType() == EGangLogType.IMPEACHMENT.getValue()) {
                    message.setString(log.getSecondName());
                }
            }
        }
        gameRole.sendMessage(message);
    }

    /**
     * 1123 帮会转盘日志
     *
     * @param request
     */
    public void processTurntableLog(Message request) {
        Queue<GangTurntableLog> queue = GameGangManager.getInstance().getGangTurntableLogQueue();
        Message message = new Message(MessageCommand.GANG_TURNTABLE_LOG, request.getChannel());
        message.setByte(queue.size());
        for (GangTurntableLog log : queue) {
            log.getMessage(message);
        }
        gameRole.sendMessage(message);
    }

    /**
     * 1124 帮会任务列表
     *
     * @param request
     */
    public void processMissionList(Message request) {
        Message message = new Message(MessageCommand.GANG_MISSION_LIST, request.getChannel());
        message.setByte(player.getDayData().getGangMissionList().size());
        for (GangMission mission : player.getDayData().getGangMissionList()) {
            mission.getMessage(message);
        }
        gameRole.sendMessage(message);
    }

    /**
     * 1125 帮会任务更新
     *
     * @param request
     */
    public void updateMission(GangMissionData gangMissionData, GameEvent event) {
        if (player.getGang() != null) {
            GangMission gangMission = null;
            for (GangMission mission : player.getDayData().getGangMissionList()) {
                if (mission.getId() == gangMissionData.getId()) {
                    gangMission = mission;
                }
            }
            boolean isUpdate = false;
            if (gangMission == null) {
                gangMission = new GangMission();
                gangMission.setId(gangMissionData.getId());
                gangMission.setProgress((byte) 1);
                player.getDayData().getGangMissionList().add(gangMission);
                isUpdate = true;
            } else {
                if (gangMission.getProgress() < gangMissionData.getCount()) {
                    gangMission.setProgress((byte) (gangMission.getProgress() + 1));
                    isUpdate = true;
                }
            }
            if (isUpdate) {
                player.getGang().addExp(gangMissionData.getGangExp());
                gameRole.getDbManager().gangDao.updateGangExp(player.getGang());
                gameRole.getPackManager().addGoods(new DropData(EGoodsType.DONATE, 0, gangMissionData.getGangDonate()), EGoodsChangeType.GANG_MISSION_ADD, event.getPlayerSave());

                event.addPlayerSaveType(EPlayerSaveType.DAYDATA);
                event.addPlayerSaveType(EPlayerSaveType.DONATE);

                Message message = new Message(MessageCommand.GANG_MISSION_UPDATE);
                gangMission.getMessage(message);
                gameRole.putMessageQueue(message);
            }
        }
    }

    /**
     * 1126 帮会副本每日通关奖励
     *
     * @param request
     */
    public void processDungeonPassReceive(Message request) {
        if (this.isGangNone(request)) {
            return;
        }
        Gang gang = player.getGang();
        if (!player.getDayData().isGangDungeonRecv() && gang.getDungeonPass() > 4) {
            player.getDayData().setGangDungeonRecv(true);
            EnumSet<EPlayerSaveType> enumSet = EnumSet.of(EPlayerSaveType.DAYDATA);

            List<Short> list = new ArrayList<>();

//			int artifactNum=gang.getDungeonPass()/20;
//			if(artifactNum<1 && GameCommon.isWinPercent(5*gang.getDungeonPass())){
//				artifactNum=1;
//			}
//			//坐骑装备
//			for(int i=1;i<=artifactNum;++i){
//				int stage=(int)(Math.random()*(i*20/16+1))+i;
//				ArtifactData artifactData=GoodsModel.getArtifactData(stage, GameCommon.getRandomIndex(GoodsModel.getMountPosSize()), GameCommon.getRandomIndex(6500,3500));
//				gameRole.getPackManager().addGoods(new DropData(EGoodsType.ARTIFACT,artifactData.getGoodsId(),1), EGoodsChangeType.GANG_DUNGEON_PASS_ADD, enumSet);
//				list.add(artifactData.getGoodsId());
//			}
            short startId = 0;
            short endId = gang.getDungeonPass();
            List<DropData> rewardList = new ArrayList<>();
            List<Short> rewardIdList = GangModel.getGangTongGuanRewardList(startId, endId);
            for (Short id : rewardIdList) {
                GangTongGuanData gtgd = GangModel.getGangTongGuanRewardMap().get(id);
                for (DropData dd : gtgd.getRewards()) {
                    rewardList.add(dd);
                }
            }
            rewardList = StringUtil.getDropDataSum(rewardList);
            gameRole.getPackManager().addGoods(rewardList, EGoodsChangeType.GANG_DUNGEON_PASS_ADD, enumSet);
            //9000~11000
//			int gold=(int)(9000+Math.random()*2000);
//			gold*=gang.getDungeonPass()/5;
//			gameRole.getPackManager().addGoods(new DropData(EGoodsType.GOLD,0,gold), EGoodsChangeType.GANG_DUNGEON_PASS_ADD, enumSet);

            Message message = new Message(MessageCommand.GANG_DUNGEON_PASS_RECEIVE_MESSAGE, request.getChannel());
//			message.setByte(list.size());
//			for(short id:list){
//				message.setShort(id);
//			}
            //TODO 去掉
//			message.setInt(0);
            //奖励列表
            message.setByte(rewardList.size());
            for (DropData data : rewardList) {
                message.setByte(data.getT());//类型
                message.setShort(data.getG());//goodId
                message.setByte(data.getQ());//品质
                message.setInt(data.getN());//数量
            }
            gameRole.sendMessage(message);

            gameRole.getDbManager().playerDao.savePlayer(player, enumSet);
        } else {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
        }
    }

    /**
     * 1127  帮会副本排行榜
     *
     * @param request
     */
    public void processDungeonRank(Message request) {
        if (this.isGangNone(request)) {
            return;
        }
        Gang gang = player.getGang();
        gang.sortDungeonRank();

        int pass = 0;

        GangMember gangMember = gang.getGangMember(player.getId());
        for (GangMember member : gang.getMemberMap().values()) {
            if (member.getDungeonPass() > gangMember.getDungeonPass()) {
                ++pass;
            }
        }

        Message message = new Message(MessageCommand.GANG_DUNGEON_RANK_MESSAGE, request.getChannel());
        int size = gang.getDungeonRank().size() > GangDefine.GANG_DUNGEON_RANK_NUM ? GangDefine.GANG_DUNGEON_RANK_NUM : gang.getDungeonRank().size();
        message.setByte(size);
        for (int i = 0; i < size; ++i) {
            GangMember member = gang.getDungeonRank().get(i);
            message.setString(member.getSimplePlayer().getName());
            message.setInt(member.getSimplePlayer().getVip());
            message.setShort(member.getDungeonPass());
        }
        message.setShort(pass);
        if (gang.getDungeonYesterdayFirst() == null) {
            message.setBool(false);
        } else {
            message.setBool(true);
            message.setInt(gang.getDungeonYesterdayFirst().getPlayerId());
            message.setString(gang.getDungeonYesterdayFirst().getName());
            message.setByte(gang.getDungeonYesterdayFirst().getHead());
            message.setShort(gang.getDungeonYesterdayFirst().getPass());
            message.setShort(gang.getDungeonYesterdayFirst().getCheer());
        }
        message.setBool(player.getDayData().isGangDungeonCheer());
        message.setBool(player.getDayData().isGangDungeonRecv());
        message.setShort(gang.getDungeonPass());
        gameRole.sendMessage(message);
    }

    /**
     * 1128  帮会副本助威
     *
     * @param request
     */
    public void processDungeonCheer(Message request) {
        if (player.getDayData().isGangDungeonCheer()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
        } else {
            if (this.isGangNone(request)) {
                return;
            }
            Gang gang = player.getGang();
            gang.getDungeonYesterdayFirst().addCheer();

            Message message = new Message(MessageCommand.GANG_DUNGEON_CHEER_MESSAGE, request.getChannel());
            message.setShort(gang.getDungeonYesterdayFirst().getCheer());
            gameRole.sendMessage(message);

            player.getDayData().setGangDungeonCheer(true);
            gameRole.savePlayer(EPlayerSaveType.DAYDATA);
            gameRole.getDbManager().gangDao.updateDungeonFirst(gang);
        }
    }

    /**
     * 1129  帮会弹劾帮主
     *
     * @param request
     */
    public void processImpeachment(Message request) {
        if (this.isGangNone(request)) {
            return;
        }
        Gang gang = player.getGang();
        if (gang.isImpeachment() == GameDefine.TRUE) {
            EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
            if (!gameRole.getPackManager().useGoods(new DropData(EGoodsType.DIAMOND, 0, 500), EGoodsChangeType.GANG_IMPEACHMENT_CONSUME, enumSet)) {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
                return;
            }

            GangMember memberA = gang.getGangMember(player.getId());
            GangMember memberB = gang.getGangMember(gang.getSimplePlayer().getId());
            memberA.setPosition(GangDefine.GANG_POSITION_PRESIDENT);
            memberB.setPosition(GangDefine.GANG_POSITION_MEMBER);
            gang.setSimplePlayer(player);

            this.processGangMemberList(request);

            gang.addLog(new GangLog(EGangLogType.IMPEACHMENT.getValue(), memberB.getSimplePlayer().getName(), player.getName()));

            gameRole.getDbManager().gangDao.updateGangOwner(gang);
            gameRole.getDbManager().gangDao.updateGangMemberPosition(memberA);
            gameRole.getDbManager().gangDao.updateGangMemberPosition(memberB);
            gameRole.getDbManager().gangDao.updateLog(gang);
        } else {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
        }
    }

    public void processBossInfo(Message request) {
        Message infoMsg = getBossInfoMsg();
        gameRole.putMessageQueue(infoMsg);
        Message listMsg = getBossListMsg(player.getId());
        listMsg.setChannel(request.getChannel());
        gameRole.sendMessage(listMsg);
    }

    /**
     * 获得BOSS信息
     *
     * @return
     */
    public Message getBossInfoMsg() {
        Message msg = new Message(MessageCommand.GANG_BOSS_INFO_MESSAGE);
        //距离下次刷新剩余多少秒
        msg.setInt(GangBossService.getNextRefreshTime());
        //剩余多少次挑战次数
        msg.setByte(GangBossService.FIGHT_MAX - player.getGangBossCount());
        return msg;
    }

    //BOSS列表信息
    public Message getBossListMsg(int playerId) {
        Gang gang = player.getGang();
        Message msg = new Message(MessageCommand.GANG_BOSS_LIST_MESSAGE);
        if (gang == null) {
            msg.setByte(0);
        } else {
            boolean open = GangBossService.isOpen();
            Map<Byte, GangBoss> bossMap = GangBossService.getAllBoss(gang.getId());
            msg.setByte(bossMap.size());
            for (GangBoss boss : bossMap.values()) {
                msg.setByte(boss.getId());
                //BOSS不在开放时间内
                if (!open) {
                    msg.setByte(GangBossService.BOSS_STATE_CLOSED);
                } else {
                    byte state = boss.getState(playerId);
                    msg.setByte(state);
                }
            }
        }
        return msg;
    }

    /**
     * BOSS开始战斗
     *
     * @param request
     */
    public void processBossStart(Message request) {
        byte bossId = request.readByte();
        Gang gang = player.getGang();
        //未加入公会
        if (gang == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GANG_NONE);
            return;
        }
        //不在开放时间内
        if (!GangBossService.isOpen()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GANG_BOSS_CLOSED);
            return;
        }
        GangBoss boss = GangBossService.getBoss(gang.getId(), bossId);
        if (boss == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        byte state = boss.getState(player.getId());
        if (state != GangBossService.BOSS_STATE_READY) {
            gameRole.putMessageQueue(getBossListMsg(player.getId()));
            gameRole.sendErrorTipMessage(request, (short) (110 + state));
            return;
        }
        long curr = System.currentTimeMillis();
        boss.setFightStartTime(curr);
        SimplePlayer sp = new SimplePlayer();
        sp.init(player);
        boss.setBattle(sp);
        //发送消息
        Message msg = new Message(MessageCommand.GANG_BOSS_START_MESSAGE, request.getChannel());
        msg.setByte(bossId);
        gameRole.sendMessage(msg);
        Message bossListMsg = getBossListMsg(0);
        //广播给公会其他人
        for (int mId : gang.getMemberMap().keySet()) {
            if (mId == player.getId())
                continue;
            GameRole gr = GameWorld.getPtr().getOnlineRole(mId);
            if (gr != null)
                gr.putMessageQueue(bossListMsg);
        }
    }

    /**
     * BOSS战斗结果
     *
     * @param request
     */
    public void processBossResult(Message request) {
        byte bossId = request.readByte();
        byte result = request.readByte();
        Gang gang = player.getGang();
        //未加入公会
        if (gang == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GANG_NONE);
            return;
        }
        GangBoss boss = GangBossService.getBoss(gang.getId(), bossId);
        if (boss == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        //战斗超时
        if (boss.getBattle() == null || boss.getBattle().getId() != player.getId()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_BATTLE_TIMEOUT);
            return;
        }
        Message msg = new Message(MessageCommand.GANG_BOSS_RESULT_MESSAGE, request.getChannel());
        //失败
        if (result == 0) {
            boss.setFightStartTime(0);
            boss.setBattle(null);
            gameRole.putMessageQueue(getBossListMsg(player.getId()));
            msg.setByte(result);
            msg.setByte(bossId);
            gameRole.sendMessage(msg);
            //广播给公会其他人
            Message bossListMsg = getBossListMsg(0);
            for (int mId : gang.getMemberMap().keySet()) {
                if (mId == player.getId())
                    continue;
                GameRole gr = GameWorld.getPtr().getOnlineRole(mId);
                if (gr != null)
                    gr.putMessageQueue(bossListMsg);
            }
            return;
        }
        //成功
        long curr = System.currentTimeMillis();
        boss.setDeadTime(curr);
        boss.setFightStartTime(0);
        boss.setBattle(null);
        //扣除次数
        player.addGangBossCount();
        EnumSet<EPlayerSaveType> saves = EnumSet.of(EPlayerSaveType.GANGBOSSCOUNT);
        //奖励
        GangBossModelData model = GangModel.getGangBossData(bossId);
        if (model != null) {
            gameRole.getPackManager().addGoods(model.getRewards(), EGoodsChangeType.GANG_BOSS_REWARD_ADD, saves);
            //帮会经验
            gang.addExp(model.getRewards().get(0).getN());
//			gang.addLog(new GangLog(EGangLogType.BOSS.getValue(),player.getName(),bossId));
            gameRole.getDbManager().gangDao.updateGangExp(gang);
        }
        //消息
        gameRole.putMessageQueue(getBossInfoMsg());
        gameRole.putMessageQueue(getBossListMsg(player.getId()));
        msg.setByte(result);
        msg.setByte(bossId);
        gameRole.sendMessage(msg);
        //保存
        gameRole.savePlayer(saves);
        //广播给公会其他人
        Message bossListMsg = getBossListMsg(0);
        for (int mId : gang.getMemberMap().keySet()) {
            if (mId == player.getId())
                continue;
            GameRole gr = GameWorld.getPtr().getOnlineRole(mId);
            if (gr != null)
                gr.putMessageQueue(bossListMsg);
        }
        return;
    }

    /**
     * 公会战信息 1140
     *
     * @param request
     */
    public void processFightInfo(Message request) {
        GGM.updateState();
        Message message = new Message(MessageCommand.GANG_FIGHT_INFO_MESSAGE, request.getChannel());
        message.setByte(GGM.getFightState());
        message.setLong(GGM.getDownTime());
        if (GGM.getFightState() != GangDefine.GANG_FIGHT_STATE_NONE) {
            message.setByte(GGM.getFightGangList().size());
            for (FightGang fightGang : GGM.getFightGangList()) {
                message.setByte(fightGang.getRound());
                message.setString(fightGang.getGangName());
                message.setByte(fightGang.getState());
            }
            message.setByte(this.isJoinFight());
        }
        gameRole.sendMessage(message);
    }

    /**
     * 帮会战参战信息 1141
     *
     * @param request
     */
    public void processFightJoinInfo(Message request) {
        GGM.updateState();
        if (this.isJoinFight() == GameDefine.TRUE) {
            FightRound fightRound = GGM.getCurrentFightRound();
            FightGang fightGangA = fightRound.getSelfFightGang(player.getGang().getId());
            FightGang fightGangB = fightRound.getTargetFightGang(player.getGang().getId());
            if (fightGangA != null && fightGangB != null) {
                FightPlayer fightPlayer = GGM.getFightPlayer(player);
                if (fightPlayer.getFightTargetList() == null) {
                    this.fightTargetRefresh(fightGangB, fightPlayer);
                }

                Message message = new Message(MessageCommand.GANG_FIGHT_JOIN_INFO_MESSAGE, request.getChannel());
                message.setLong(GGM.getDownTime());
                message.setShort(fightGangA.getStarNum());
                message.setLong(fightGangA.getScore());
                message.setString(fightGangB.getGangName());
                message.setShort(fightGangB.getStarNum());
                message.setLong(fightGangB.getScore());
                message.setByte(fightGangB.getTargetList().size());
                message.setByte(fightGangB.getFightTargetList().size());
                message.setByte(fightPlayer.getFightTargetList().size());
                for (FightTarget fightTarget : fightPlayer.getFightTargetList()) {
                    fightTarget.getPlayer().getSimpleMessage(message);
                    message.setByte(fightTarget.getBeStar());
                }
                message.setByte(fightPlayer.getStarNum());
                message.setInt(fightPlayer.getScore());
                message.setByte(fightPlayer.getFightCount());
                message.setByte(fightGangA.getFightGangLogQueue().size());
                for (FightGangLog log : fightGangA.getFightGangLogQueue()) {
                    log.getMessage(message);
                }
                gameRole.sendMessage(message);
            } else {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            }
        } else {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
        }
    }

    /**
     * 帮会战战斗请求 1142
     *
     * @param request
     */
    public void processFightRequest(Message request) {
        int targetId = request.readInt();
        byte star = request.readByte();

        if (this.isJoinFight() == GameDefine.TRUE) {
            FightPlayer fightPlayer = GGM.getFightPlayer(player);
            if (fightPlayer.getFightCount() < GangDefine.GANG_FIGHT_ATTACK_NUM) {
                FightRound fightRound = GGM.getCurrentFightRound();
                FightGang fightGangA = fightRound.getSelfFightGang(player.getGang().getId());
                FightGang fightGangB = fightRound.getTargetFightGang(player.getGang().getId());
                if (fightGangA != null && fightGangB != null) {
                    FightTarget fightTarget = fightGangB.getTargetPlayer(targetId);
                    if (fightTarget.getBeStar() < star) {
                        fightPlayer.setAttackStar(star);

                        Message message = new Message(MessageCommand.GANG_FIGHT_REQUEST_MESSAGE, request.getChannel());
                        message.setByte(star);
                        fightTarget.getPlayer().updateFighting();
                        fightTarget.getPlayer().getBaseSimpleMessage(message);
                        fightTarget.getPlayer().getAppearMessage(message);
                        fightTarget.getPlayer().getAttrFighting(message);
                        gameRole.sendMessage(message);
                    } else {
                        gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
                    }
                } else {
                    gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
                }
            } else {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_NUM_MAX);
            }
        } else {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
        }
    }

    /**
     * 帮会战战斗结果 1143
     *
     * @param request
     */
    public void processFightResult(Message request) {
        int targetId = request.readInt();
        byte result = request.readByte();

        if (this.isJoinFight() == GameDefine.TRUE) {
            FightRound fightRound = GGM.getCurrentFightRound();
            FightGang fightGangA = fightRound.getSelfFightGang(player.getGang().getId());
            FightGang fightGangB = fightRound.getTargetFightGang(player.getGang().getId());
            if (fightGangA != null && fightGangB != null) {
                FightPlayer fightPlayer = GGM.getFightPlayer(player);
                byte starNum = 0;
                int score = 0;
                if (fightPlayer.getFightCount() < GangDefine.GANG_FIGHT_ATTACK_NUM) {
                    FightTarget fightTarget = fightGangB.getTargetPlayer(targetId);
                    byte beStar = 0;
                    if (result == FightDefine.FIGHT_RESULT_SUCCESS) {
                        if (fightTarget.getBeStar() < fightPlayer.getAttackStar()) {
                            starNum = this.getFightWinStar(fightPlayer.getAttackStar(), fightTarget.getBeStar());
                            score = this.getFightWinScore(fightPlayer.getAttackStar(), fightTarget.getPlayer().getFighting());
                            beStar = fightPlayer.getAttackStar();
                            fightTarget.setBeStar(fightPlayer.getAttackStar());
                            fightPlayer.addStarNum(starNum);
                            fightPlayer.addScore(score);
                            fightPlayer.setAttackStar((byte) 0);
                            if (fightTarget.getBeStar() == GangDefine.GANG_FIGHT_PLAYER_STATE_THREE) {
                                if (fightGangB.isLose(fightTarget)) {
                                    fightGangB.setState(GangDefine.GANG_FIGHT_GANG_STATE_OUT);
                                    fightGangA.setState(GangDefine.GANG_FIGHT_GANG_STATE_WIN);
                                }
                            }
                            fightGangA.updateFighting(fightGangB, score);
                            GGM.sortFightPlayerMember();
                        } else {
                            result = 2;
                        }
                    }
                    fightPlayer.addFightCount();
                    fightGangA.addLog(player.getName(), fightTarget.getPlayer().getName(), beStar, starNum, score);
                } else {
                    result = 2;
                }
                Message message = new Message(MessageCommand.GANG_FIGHT_RESULT_MESSAGE, request.getChannel());
                message.setByte(result);
                message.setShort(starNum);
                message.setInt(score);
                gameRole.sendMessage(message);
            } else {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            }
        } else {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
        }
    }

    /**
     * 帮会战排名 1144
     *
     * @param request
     */
    public void processFightRank(Message request) {
        Message message = new Message(MessageCommand.GANG_FIGHT_RANK_MESSAGE, request.getChannel());
        message.setByte(GGM.getGangRankHistoryList().size());
        for (FightGangRankData fightGangRankData : GGM.getGangRankHistoryList()) {
            message.setString(fightGangRankData.getName());
            message.setShort(fightGangRankData.getStarNum());
            message.setInt(fightGangRankData.getScore());
        }
        gameRole.sendMessage(message);
    }

    /**
     * 帮会战奖励分配 1145
     *
     * @param request
     */
    public void processFightRewardAssign(Message request) {
        if (this.isGangNone(request)) {
            return;
        }
        GangMember member = player.getGang().getGangMember(player.getId());
        if (GangDefine.GANG_POSITION_PRESIDENT != member.getPosition() && GangDefine.GANG_POSITION_VICE_PRESIDENT != member.getPosition()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GANG_PERMISSION_NONE);
            return;
        }
        int playerId = request.readInt();
        short gid = request.readShort();
        short num = request.readShort();

        Message message = new Message(MessageCommand.GANG_FIGHT_REWARD_ASSIGN_MESSAGE, request.getChannel());
        message.setByte(player.getGang().assignStore(playerId, gid, num, player.getName()));
        gameRole.putMessageQueue(this.getStoreMessage());
        gameRole.sendMessage(message);

        gameRole.getDbManager().gangDao.updateLog(player.getGang());
    }

    /**
     * 帮会战仓库 1146
     *
     * @param request
     */
    public void processFightStore(Message request) {
        if (this.isGangNone(request)) {
            return;
        }
        Message message = this.getStoreMessage();
        message.setChannel(request.getChannel());
        gameRole.sendMessage(message);
    }

    /**
     * 帮会战个人排名 1147
     *
     * @param request
     */
    public void processFightMemberRank(Message request) {
        Message message = new Message(MessageCommand.GANG_FIGHT_MEMBER_RANK_MESSAGE, request.getChannel());
        if (GGM.getFightState() == GangDefine.GANG_FIGHT_STATE_FIGHT) {
            int size = GGM.getFightPlayerRankList().size() > GangDefine.GANG_FIGHT_MEMBER_RANK_NUM ? GangDefine.GANG_FIGHT_MEMBER_RANK_NUM : GGM.getFightPlayerRankList().size();
            message.setByte(size);
            for (int i = 0; i < size; ++i) {
                FightPlayer fightPlayer = GGM.getFightPlayerRankList().get(i);
                message.setString(fightPlayer.getPlayer().getName());
                message.setShort(fightPlayer.getStarNum());
                message.setInt(fightPlayer.getScore());
            }
            message.setShort(this.getFightMemberRank(player.getId()));
            FightPlayer self = GGM.getFightPlayer(player);
            if (self == null) {
                message.setShort(0);
                message.setInt(0);
            } else {
                message.setShort(self.getStarNum());
                message.setInt(self.getScore());
            }
        } else {
            message.setByte(GGM.getMemberRankHistoryList().size());
            FightGangRankData self = null;
            int rank = 0, count = 1;
            for (FightGangRankData fightGangRankData : GGM.getMemberRankHistoryList()) {
                message.setString(fightGangRankData.getName());
                message.setShort(fightGangRankData.getStarNum());
                message.setInt(fightGangRankData.getScore());
                if (fightGangRankData.getId() == player.getId()) {
                    self = fightGangRankData;
                    rank = count;
                }
                ++count;
            }
            message.setShort(rank);
            if (self == null) {
                message.setShort(0);
                message.setInt(0);
            } else {
                message.setShort(self.getStarNum());
                message.setInt(self.getScore());
            }
        }
        gameRole.sendMessage(message);
    }

    /**
     * 帮会战参战刷新 1148
     *
     * @param request
     */
    public void processFightRefresh(Message request) {
        if (this.isJoinFight() == GameDefine.TRUE) {
            FightRound fightRound = GGM.getCurrentFightRound();
            FightGang fightGangB = fightRound.getTargetFightGang(player.getGang().getId());
            if (fightGangB != null) {
                FightPlayer fightPlayer = GGM.getFightPlayer(player);
                this.fightTargetRefresh(fightGangB, fightPlayer);

                Message message = new Message(MessageCommand.GANG_FIGHT_JOIN_REFRESH_MESSAGE, request.getChannel());
                message.setByte(fightGangB.getTargetList().size());
                message.setByte(fightGangB.getFightTargetList().size());
                message.setByte(fightPlayer.getFightTargetList().size());
                for (FightTarget fightTarget : fightPlayer.getFightTargetList()) {
                    fightTarget.getPlayer().getSimpleMessage(message);
                    message.setByte(fightTarget.getBeStar());
                }
                gameRole.sendMessage(message);
            } else {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            }
        } else {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
        }
    }

    /**
     * 帮会战参战 1149
     *
     * @param request
     */
    public void processFightJoin(Message request) {
        byte isJoin = 0;
        if (player.getGang() != null && GGM.getFightState() == GangDefine.GANG_FIGHT_STATE_FIGHT) {
            FightRound fightRound = GGM.getCurrentFightRound();
            if (fightRound != null) {
                FightGang fightGangA = fightRound.getSelfFightGang(player.getGang().getId());
                if (fightGangA != null && fightGangA.getState() == GangDefine.GANG_FIGHT_GANG_STATE_WIN) {
                    FightPlayer fightPlayer = GGM.getFightPlayer(player);
                    if (fightPlayer != null && fightPlayer.getScore() == 0 && fightPlayer.getStarNum() == 0) {
                        fightPlayer.addStarNum((byte) 1);
                        fightPlayer.addScore(100);
                        isJoin = 1;
                    }
                }
            }
        }
        Message message = new Message(MessageCommand.GANG_FIGHT_JOIN_MESSAGE, request.getChannel());
        message.setByte(isJoin);
        gameRole.sendMessage(message);
    }

    private void fightTargetRefresh(FightGang targetGang, FightPlayer fightPlayer) {
        Collections.shuffle(targetGang.getFightTargetList());
        int size = targetGang.getFightTargetList().size() > GangDefine.GANG_FIGHT_TARGET_MEMBER_NUM ? GangDefine.GANG_FIGHT_TARGET_MEMBER_NUM : targetGang.getFightTargetList().size();
        List<FightTarget> fightTargetList = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            FightTarget fightTarget = targetGang.getFightTargetList().get(i);
            fightTargetList.add(fightTarget);
        }
        fightPlayer.setFightTargetList(fightTargetList);
    }

    private int getFightMemberRank(int playerId) {
        int rank = 1;
        for (FightPlayer fightPlayer : GGM.getFightPlayerRankList()) {
            if (fightPlayer.getPlayer().getId() == playerId) {
                return rank;
            }
            ++rank;
        }
        return 0;
    }

    private Message getStoreMessage() {
        Message message = new Message(MessageCommand.GANG_FIGHT_STORE_MESSAGE);
        message.setByte(player.getGang().getStoreList().size());
        for (Goods goods : player.getGang().getStoreList()) {
            goods.getMessage(message);
        }
        return message;
    }

    /**
     * 获取公会商店购买信息 1150
     *
     * @param request
     * @return
     */
    public void getGangShopBuyMessage(Message request) {
        Message message = new Message(MessageCommand.GANG_SHOP_BUY_INIT_MESSAGE, request.getChannel());
        Gang gang = player.getGang();
        Map<Short, GangShopModelData> gangShopMap = GangModel.getGangShopMap();
        Map<Short, Short> map = gang.getShopLimitNumMap();
        for (Short key : gangShopMap.keySet()) {
            if (!map.containsKey(key)) {
                map.put(key, (short) 0);
            }
        }
        message.setByte(map.size());
        for (Entry<Short, Short> entry : map.entrySet()) {
            message.setShort(entry.getKey());
            message.setShort(entry.getValue());
        }
        gameRole.sendMessage(message);
    }

    /**
     * 帮会商店 1151
     *
     * @param request
     */
    public void processGangShopBuyMessage(Message request) {
        if (this.isGangNone(request)) {
            return;
        }
        Gang gang = player.getGang();
        short id = request.readShort();
        GangShopModelData gsmd = GangModel.getGangShopMap().get(id);
        if (gsmd == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        List<DropData> costs = gsmd.getCosts();
        if (!gameRole.getPackManager().useGoods(costs, EGoodsChangeType.GANG_SHOP_CONSUME, saves)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }
        Map<Short, GangShopModelData> gangShopMap = GangModel.getGangShopMap();
        Map<Short, Short> map = gang.getShopLimitNumMap();
        for (Short key : gangShopMap.keySet()) {
            if (!map.containsKey(key)) {
                map.put(key, (short) 0);
            }
        }
        short limitNum = gang.getShopLimitNumMap().get(id);
        List<DropData> rewards = gsmd.getRewards();
        gameRole.getPackManager().addGoods(rewards, EGoodsChangeType.GANG_SHOP_ADD, saves);
        if (gsmd.getLimitNum() != 0 && gsmd.getLimitNum() <= limitNum) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_TIMES_LESS);
            return;
        }
        gang.getShopLimitNumMap().put(id, ++limitNum);
        Message message = new Message(MessageCommand.GANG_SHOP_BUY_MESSAGE, request.getChannel());
        message.setShort(id);
        message.setShort(gang.getShopLimitNumMap().get(id));
        gameRole.sendMessage(message);
        gameRole.savePlayer(saves);
        gameRole.getDbManager().gangDao.updateShopLimitNumMap(gang);
    }

    /**
     * 更新捐献值
     *
     * @param addValue
     */
    public void updateTotalDonate(int addValue) {
        try {
            Player player = gameRole.getPlayer();
            Gang gang = player.getGang();
            if (gang == null) {
                return;
            }
            GangMember member = gang.getGangMember(player.getId());
            if (member == null) {
                return;
            }
            member.setTotalDonate(member.getTotalDonate() + addValue);
            gameRole.getDbManager().gangDao.updateMember(member);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 是否有公会
     *
     * @return
     */
    private boolean isGangNone(Message request) {
        if (player.getGang() == null) {
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
    private boolean isOperationQuick(Gang gang, Message request) {
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
    private boolean isPermissionNone(Gang gang, Message request) {
        GangMember member = gang.getGangMember(player.getId());
        if (GangDefine.GANG_POSITION_PRESIDENT != member.getPosition() && GangDefine.GANG_POSITION_VICE_PRESIDENT != member.getPosition() && GangDefine.GANG_POSITION_MANAGER != member.getPosition()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GANG_PERMISSION_NONE);
            return true;
        }
        return false;
    }

    public void handleEvent(GameEvent event) {
        if (event.getType() == EGameEventType.DUNGEON_GANG_PASS) {
            Gang gang = player.getGang();
            if (gang != null) {
                GangMember member = gang.getGangMember(player.getId());
                member.setDungeonPass((short) event.getData());
                gang.addDungeonMember(member);
                GangDungeonFirst gangDungeonFirst = new GangDungeonFirst();
                //每次选取最大为昨日通关数最高
                GangMember gangMember = gang.getMaxDungeonPassGangMember();
                gangDungeonFirst.setCheer((short) 0);
                gangDungeonFirst.setHead(gangMember.getSimplePlayer().getHead());
                gangDungeonFirst.setName(gangMember.getSimplePlayer().getName());
                gangDungeonFirst.setPass(gangMember.getDungeonPass());
                gangDungeonFirst.setPlayerId(gangMember.getPlayerId());
                gang.setDungeonTodayFirst(gangDungeonFirst);
                gang.setDungeonPass(gangMember.getDungeonPass());
                gameRole.getDbManager().gangDao.updateDungeonPassFirst(gang);
//				if(gang.getDungeonPass()<member.getDungeonPass()){
//					gang.setDungeonPass(member.getDungeonPass());
//					GangDungeonFirst dungeonFirst=new GangDungeonFirst();
//					dungeonFirst.setPlayerId(player.getId());
//					dungeonFirst.setName(player.getName());
//					dungeonFirst.setHead(player.getHead());
//					dungeonFirst.setPass(member.getDungeonPass());
//					gang.setDungeonTodayFirst(dungeonFirst);
//					gameRole.getDbManager().gangDao.updateDungeonPassFirst(gang);
//				}
            }
        }
    }

    public boolean isDungeonPass(short pass) {
        for (GangMember member : player.getGang().getMemberMap().values()) {
            if (member.getDungeonPass() >= pass) {
                return true;
            }
        }
        return false;
    }

    public short getCheer() {
        if (player.getGang().getDungeonYesterdayFirst() != null && player.getGang().getDungeonYesterdayFirst().getPlayerId() == player.getId()) {
            return player.getGang().getDungeonYesterdayFirst().getCheer();
        }
        return 0;
    }

    private byte isJoinFight() {
        if (player.getGang() == null) {
            return GameDefine.FALSE;
        }
        if (GGM.getFightState() == GangDefine.GANG_FIGHT_STATE_FIGHT) {
            FightRound fightRound = GGM.getCurrentFightRound();
            if (fightRound != null) {
                FightGang fightGangA = fightRound.getSelfFightGang(player.getGang().getId());
                FightGang fightGangB = fightRound.getTargetFightGang(player.getGang().getId());
                if (fightGangA != null && fightGangB != null && fightGangA.getState() == GangDefine.GANG_FIGHT_GANG_STATE_FIGHT && fightGangB.getState() == GangDefine.GANG_FIGHT_GANG_STATE_FIGHT && fightGangA.isJoin(player.getId())) {
                    return GameDefine.TRUE;
                }
            }
        }
        return GameDefine.FALSE;
    }

    public byte getFightWinStar(byte ackStar, byte beStar) {
        return (byte) (GangDefine.GANG_FIGHT_STAR[ackStar] - GangDefine.GANG_FIGHT_STAR[beStar]);
    }

    public int getFightWinScore(byte ackStar, long fighting) {
        long score = (long) GangDefine.GANG_FIGHT_SCORE[ackStar] * fighting;
        if (score < GangDefine.GANG_FIGHT_SCORE_RATIO) {
            return 1;
        }
        return (int) (score / GangDefine.GANG_FIGHT_SCORE_RATIO);
    }

    /**
     * 3020 传世争霸信息
     *
     * @param request
     */
    public void processStarcraftInfo(Message request) {
//		GangService.getPtr().updateState();
//		Gang gang = GangService.getPtr().getGangOverlord();
//		
//		Message message = new Message(MessageCommand.STARCRAFT_INFO_MESSAGE,request.getChannel());
//		message.setByte(GangService.getPtr().getMonth());//开启几月
//		message.setByte(GangService.getPtr().getDay());//开启几日
//		message.setInt(GangService.getPtr().getDownTimeOpen());//开启倒计时
//		int state = player.getDayData().getGangReward();
//		if(state==0){
//			if(gang!=null && player.getGang()!=null && gang.getId()==player.getGang().getId()){
//				state = 1;
//			}
//		}
//		message.setByte(state);//每日奖励领取
//		if(gang == null){
//			message.setByte(0);//是否有上届记录
//		}else{
//			Player player = GangService.getPtr().getPlayerPresident();
//			
//			message.setByte(1);
//			message.setString(gang.getName());
//			message.setString(player.getName());
//			player.getCharacterList().get(0).getAppearMsg(message);
//			message.setShort(GangService.getPtr().getGangRankList().size());
//			for(GangStarcraftRank rank : GangService.getPtr().getGangRankList()){
//				message.setString(rank.getName());
//				message.setInt(rank.getScore());
//			}
//			message.setShort(GangService.getPtr().getMemberRankList().size());
//			for(GangStarcraftMemberRank rank : GangService.getPtr().getMemberRankList()){
//				message.setString(rank.getName());
//				message.setString(rank.getGangName());
//				message.setInt(rank.getScore());
//			}
//		}
//		gameRole.sendMessage(message);
    }

    /**
     * 3021 传世争霸参战
     *
     * @param request
     */
    public void processStarcraftEnter(Message request) {
        GangService.getPtr().updateState();
        if (!GangService.getPtr().isOpenFight()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_STARCRAFT_UNOPEN);
            return;
        }
        StarcraftFighter fighter = GangService.getPtr().starcraftEnter(player);
        if (fighter == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        GangService.getPtr().updateState();
        fighter.setArea(GangDefine.STARCRAFT_AREA_DOOR);
        fighter.setFeat((short) 0);

        Message message = new Message(MessageCommand.STARCRAFT_ENTER_MESSAGE, request.getChannel());
        //boss血量 变动广播
        message.setInt(GangService.getPtr().getDoorBoosHp());
        //争霸进度 1：城门 2：城内 3殿前 4：皇宫 变动广播
        message.setByte(GangService.getPtr().getProgress());
        //结束倒计时
        message.setInt(GangService.getPtr().getDownTimeEnd() / 1000);
        //排行榜
        GangService.getPtr().getRankMessage(message);
        //城门场景玩家数据
        GangService.getPtr().getAreaMessage(message, fighter.getArea(), fighter.getMember().getGangId());
        if (GangService.getPtr().getCollectTime() > 0 && GangService.getPtr().getCollectFighter() != null) {
            if (GangService.getPtr().getCollectFighter().getMember().getPlayerId() == fighter.getMember().getPlayerId()) {
                GangService.getPtr().broadcastCollectStop(fighter.getMember().getPlayerId());

                message.setByte(0);
            } else {
                message.setByte(1);
                long dt = System.currentTimeMillis() - GangService.getPtr().getCollectTime();
                message.setShort((short) (dt / 1000));
                Gang gang = GameGangManager.getInstance().getGang(GangService.getPtr().getCollectFighter().getMember().getGangId());
                message.setString(gang.getName());
                GangService.getPtr().getCollectFighter().getMember().getSimplePlayer().getFightAppearMessage(message);
            }
        } else {
            message.setByte(0);
        }
        gameRole.sendMessage(message);
    }

    /**
     * 3022 传世争霸 攻击城门boss
     *
     * @param request
     */
    public void processStarcraftAttackDoor(Message request) {
        if (!GangService.getPtr().isOpenFight()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_STARCRAFT_UNOPEN);
            return;
        }
        int damage = request.readInt();
        GangService.getPtr().hurtDoorBoss(damage);
        Message message = new Message(MessageCommand.STARCRAFT_ATTACK_DOOR_MESSAGE, request.getChannel());
        message.setInt(GangService.getPtr().getDoorBoosHp());
        gameRole.sendMessage(message);
    }

    /**
     * 3024 传世争霸 传送
     *
     * @param request
     */
    public void processStarcraftMove(Message request) {
        if (!GangService.getPtr().isOpenFight()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_STARCRAFT_UNOPEN);
            return;
        }
        StarcraftFighter fighter = GangService.getPtr().getStarcraftFight(player);
        if (fighter == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        byte move = request.readByte();
        if (!GangService.getPtr().move(fighter, move)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_FEAT_LESS);
            return;
        }

        if (move != GangDefine.STARCRAFT_AREA_PALACE && GangService.getPtr().getCollectTime() > 0 && GangService.getPtr().getCollectFighter() != null) {
            if (GangService.getPtr().getCollectFighter().getMember().getPlayerId() == fighter.getMember().getPlayerId()) {
                GangService.getPtr().broadcastCollectStop(fighter.getMember().getPlayerId());
            }
        }

        Message message = new Message(MessageCommand.STARCRAFT_MOVE_MESSAGE, request.getChannel());
        message.setByte(fighter.getArea());
        message.setShort(fighter.getFeat());
        GangService.getPtr().getAreaMessage(message, fighter.getArea(), fighter.getMember().getGangId());
        gameRole.sendMessage(message);
    }

    /**
     * 3029 传世争霸 攻击目标
     *
     * @param request
     */
    public void processStarcraftAttack(Message request) {
        if (!GangService.getPtr().isOpenFight()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_STARCRAFT_UNOPEN);
            return;
        }
        int beAttackId = request.readInt();

        StarcraftFighter fightA = GangService.getPtr().getStarcraftFight(player.getId());
        StarcraftFighter fightB = GangService.getPtr().getStarcraftFight(beAttackId);

        long currTime = System.currentTimeMillis();
        if (fightA == null || fightB == null || fightA.getDeadTime() != 0 || fightB.getDeadTime() != 0 || (fightA.getAttackTime() + GangDefine.STARCRAFT_FIGHT_DOWNTIME) > currTime || (fightB.getAttackTime() + GangDefine.STARCRAFT_FIGHT_DOWNTIME) > currTime) {
            Message msg = new Message(MessageCommand.STARCRAFT_ATTACK_TARGET_MESSAGE, request.getChannel());
            msg.setByte(2);
            gameRole.sendMessage(msg);
            return;
        }
        if (fightA.getArea() != fightB.getArea()) {
            Message msg = new Message(MessageCommand.STARCRAFT_ATTACK_TARGET_MESSAGE, request.getChannel());
            msg.setByte(3);
            gameRole.sendMessage(msg);
            return;
        }
        if (fightA.getMember().getGangId() == fightB.getMember().getGangId()) {
            Message msg = new Message(MessageCommand.STARCRAFT_ATTACK_TARGET_MESSAGE, request.getChannel());
            msg.setByte(4);
            gameRole.sendMessage(msg);
            return;
        }

        fightA.setAttackTime(currTime);
        fightB.setAttackTime(currTime);

        //发送战斗结果广播
        Message message = new Message(MessageCommand.STARCRAFT_ATTACK_RESULT_MESSAGE);

        byte result = FightCommon.playerVsPlayerFormula(this.player, (Player) fightB.getMember().getSimplePlayer());
        if (result == FightDefine.FIGHT_RESULT_SUCCESS) {
            fightA.addScore(GangDefine.STARCRAFT_ATTACK_SCORE);
            fightA.addFeat(GangDefine.STARCRAFT_ATTACK_FEAT);
            fightB.addScore(GangDefine.STARCRAFT_BEATTACK_SCORE);
            fightB.setFeat((short) 0);
            fightB.setDeadTime(currTime);

            message.setInt(player.getId());
            message.setString(player.getName());
            message.setString(player.getGangName());
            message.setInt(fightB.getMember().getPlayerId());
            message.setString(fightB.getMember().getSimplePlayer().getName());

            GangService.getPtr().addScore(fightA.getMember().getGangId(), GangDefine.STARCRAFT_ATTACK_SCORE);
            GangService.getPtr().addScore(fightB.getMember().getGangId(), GangDefine.STARCRAFT_BEATTACK_SCORE);
        } else {
            fightB.addScore(GangDefine.STARCRAFT_ATTACK_SCORE);
            fightB.addFeat(GangDefine.STARCRAFT_ATTACK_FEAT);
            fightA.addScore(GangDefine.STARCRAFT_BEATTACK_SCORE);
            fightA.setFeat((short) 0);
            fightA.setDeadTime(currTime);

            message.setInt(fightB.getMember().getPlayerId());
            message.setString(fightB.getMember().getSimplePlayer().getName());
            Gang gang = GameGangManager.getInstance().getGang(fightB.getMember().getGangId());
            message.setString(gang.getName());
            message.setInt(player.getId());
            message.setString(player.getName());

            GangService.getPtr().addScore(fightB.getMember().getGangId(), GangDefine.STARCRAFT_ATTACK_SCORE);
            GangService.getPtr().addScore(fightA.getMember().getGangId(), GangDefine.STARCRAFT_BEATTACK_SCORE);
        }

        if (GangService.getPtr().getCollectFighter() == null) {
            message.setInt(0);
        } else {
            if (fightA.getMember().getPlayerId() == GangService.getPtr().getCollectFighter().getMember().getPlayerId()) {
                message.setInt(fightA.getMember().getPlayerId());

                GangService.getPtr().setCollectTime(0);
                GangService.getPtr().setCollectFighter(null);
            } else if (fightB.getMember().getPlayerId() == GangService.getPtr().getCollectFighter().getMember().getPlayerId()) {
                message.setInt(fightB.getMember().getPlayerId());

                GangService.getPtr().setCollectTime(0);
                GangService.getPtr().setCollectFighter(null);
            } else {
                message.setInt(0);
            }
        }
        GangService.getPtr().broadcast(message);

        Message msg = new Message(MessageCommand.STARCRAFT_ATTACK_TARGET_MESSAGE, request.getChannel());
        msg.setByte(1);
        msg.setShort(fightA.getFeat());
        msg.setInt(fightA.getScore());
        gameRole.sendMessage(msg);
    }

    /**
     * 3030 传世争霸 攻击守卫
     *
     * @param request
     */
    public void processStarcraftAttackGuard(Message request) {
        if (!GangService.getPtr().isOpenFight()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_STARCRAFT_UNOPEN);
            return;
        }
        StarcraftFighter fightA = GangService.getPtr().getStarcraftFight(player.getId());

        long currTime = System.currentTimeMillis();
        if (fightA == null || (fightA.getAttackTime() != 0 && (fightA.getAttackTime() + GangDefine.STARCRAFT_FIGHT_DOWNTIME) > System.currentTimeMillis())) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        if (fightA.getArea() != GangDefine.STARCRAFT_AREA_INSIDE) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        fightA.setAttackTime(currTime);

        fightA.addScore(GangDefine.STARCRAFT_ATTACK_GUARD_SCORE);
        fightA.addFeat(GangDefine.STARCRAFT_ATTACK_GUARD_FEAT);

        GangService.getPtr().addScore(fightA.getMember().getGangId(), GangDefine.STARCRAFT_ATTACK_GUARD_SCORE);

        Message message = new Message(MessageCommand.STARCRAFT_ATTACK_GUARD_MESSAGE, request.getChannel());
        message.setShort(fightA.getFeat());
        message.setInt(fightA.getScore());
        gameRole.sendMessage(message);
    }

    /**
     * 3033 传世争霸 采旗
     *
     * @param request
     */
    public void processStarcraftCollect(Message request) {
        if (!GangService.getPtr().isOpenFight()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_STARCRAFT_UNOPEN);
            return;
        }
        StarcraftFighter fightA = GangService.getPtr().getStarcraftFight(player.getId());
        if (fightA == null || fightA.getArea() != GangDefine.STARCRAFT_AREA_PALACE) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        Message message = new Message(MessageCommand.STARCRAFT_COLLECT_MESSAGE, request.getChannel());
        if (GangService.getPtr().getCollectTime() > 0 && GangService.getPtr().getCollectFighter() != null) {
            message.setByte(1);
            message.setInt(GangService.getPtr().getCollectFighter().getMember().getGangId());
        } else {
            message.setByte(0);
            GangService.getPtr().setCollectTime(System.currentTimeMillis());
            GangService.getPtr().setCollectFighter(fightA);
            Message msg = new Message(MessageCommand.STARCRAFT_BROADCAST_COLLECT_MESSAGE, request.getChannel());
            msg.setShort(GangDefine.STARCRAFT_COLLECT_TIME / 1000);
            msg.setString(player.getGangName());
            fightA.getMember().getSimplePlayer().getFightAppearMessage(msg);
            GangService.getPtr().broadcast(msg);
        }
        gameRole.sendMessage(message);
    }

    /**
     * 3037 传世争霸 领取奖励
     *
     * @param request
     */
    public void processStarcraftReward(Message request) {
        Gang gang = GangService.getPtr().getGangOverlord();
        if (gang != null && player.getGang() != null) {
            if (gang.getId() == player.getGang().getId()) {
                if (player.getDayData().getGangReward() == 0) {
                    player.getDayData().setGangReward((byte) 2);

                    EnumSet<EPlayerSaveType> enumSet = EnumSet.of(EPlayerSaveType.DAYDATA);
                    gameRole.getPackManager().addGoods(BattleModel.getGangRewardData(), EGoodsChangeType.STARCRAFT_REWARD_ADD, enumSet);

                    Message message = new Message(MessageCommand.STARCRAFT_REWARD_MESSAGE, request.getChannel());
                    gameRole.sendMessage(message);

                    gameRole.savePlayer(enumSet);
                }
            }
        }
        gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
    }

    /**
     * 3038 传世争霸 倒计时
     *
     * @param request
     */
    public void processStarcraftCountDown(Message request) {
        GangService.getPtr().updateState();

        Message message = new Message(MessageCommand.STARCRAFT_COUNTDOWN_MESSAGE, request.getChannel());
        message.setInt(GangService.getPtr().getDownTimeOpen());//开启倒计时
        gameRole.sendMessage(message);
    }
}
