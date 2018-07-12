package com.rd.game.manager;

import com.rd.bean.drop.DropData;
import com.rd.bean.nightFight.NightFighter;
import com.rd.bean.player.Player;
import com.rd.common.FightCommon;
import com.rd.common.NightFightService;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.*;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.model.NightFightModel;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.Map.Entry;
import java.util.Set;

public class NightFightManager {

    private GameRole gameRole;
    private Player player;

    public NightFightManager(GameRole gameRole) {
        this.gameRole = gameRole;
        this.player = gameRole.getPlayer();
    }

    public void processJoinFight(Message request) {
        long currTime = System.currentTimeMillis();
        int day = DateUtil.getDistanceDay(GameDefine.SERVER_CREATE_TIME, currTime);
        if (day == 0) {
            Message message = new Message(MessageCommand.NIGHT_FIGHT_JOIN_MESSAGE, request.getChannel());
            message.setByte(-1);
            gameRole.sendMessage(message);
            return;
        }
        if (player.getLevel() < NightFightDefine.OPEN_LEVEL) {
            Message message = new Message(MessageCommand.NIGHT_FIGHT_JOIN_MESSAGE, request.getChannel());
            message.setByte(-2);
            gameRole.sendMessage(message);
            return;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int week = c.get(Calendar.DAY_OF_WEEK);
        String toDay = DateUtil.formatDay(currTime);
        long startTime = DateUtil.parseDataTime(toDay + " " + NightFightDefine.START_TIME).getTime();
        //周一至周五开放
        if (week == 7 || week == 1 || startTime > currTime) {
            Message message = new Message(MessageCommand.NIGHT_FIGHT_JOIN_MESSAGE, request.getChannel());
            message.setByte(-3);
            gameRole.sendMessage(message);
            return;
        }
        long endTime = DateUtil.parseDataTime(toDay + " " + NightFightDefine.END_TIME).getTime();
        if (endTime < currTime) {
            Message message = new Message(MessageCommand.NIGHT_FIGHT_JOIN_MESSAGE, request.getChannel());
            message.setByte(-4);
            gameRole.sendMessage(message);
            return;
        }
        Long exitTime = NightFightService.getInstance().getExitMap().get(player.getId());
        if (exitTime != null) {
            if (exitTime + NightFightDefine.EXIT_INTO_TIME > currTime) {
                Message message = new Message(MessageCommand.NIGHT_FIGHT_JOIN_MESSAGE, request.getChannel());
                message.setByte(-5);
                gameRole.sendMessage(message);
                return;
            } else {
                NightFightService.getInstance().getExitMap().remove(player.getId());
            }
        }

        NightFightService.getInstance().addNightFighter(this.player);

        NightFighter fighter = NightFightService.getInstance().getNightFighter(this.player.getId());
        Message msg = new Message(MessageCommand.NIGHT_FIGHT_EXCHANGE_MESSAGE, request.getChannel());
        msg.setShort(fighter.getExchangeMax());
        gameRole.putMessageQueue(msg);

        Message message = new Message(MessageCommand.NIGHT_FIGHT_JOIN_MESSAGE, request.getChannel());
        message.setByte(1);
        //玩家列表
        message.setShort(NightFightService.getInstance().getFighterList().size());
        for (NightFighter temp : NightFightService.getInstance().getFighterList()) {
            temp.getPlayer().getNightFightAppearMessage(message);
            message.setByte(temp.getCamp());
            message.setByte(temp.getProtectedTime());
            message.setShort(temp.getPoint());
            message.setString(temp.getPlayer().getGangName());
        }

        //剩余时间
        message.setInt((int) (NightFightService.getEndDownTime() / 1000));
        gameRole.sendMessage(message);
    }

    public void processAttack(Message request) {
        int beAttackId = request.readInt();

        NightFighter fightA = NightFightService.getInstance().getNightFighter(this.player.getId());
        NightFighter fightB = NightFightService.getInstance().getNightFighter(beAttackId);

        long currTime = System.currentTimeMillis();
        if (fightA == null || fightB == null || !fightB.isLive() || (fightA.getAttackTime() + 5000) > currTime || (fightB.getAttackTime() + 5000) > currTime) {
            Message message = new Message(MessageCommand.NIGHT_FIGHT_ATTACK_MESSAGE, request.getChannel());
            message.setShort(beAttackId);
            gameRole.sendMessage(message);
            return;
        }
        fightA.setAttackTime(currTime);
        fightB.setAttackTime(currTime);

        //发送战斗结果广播
        Message message = new Message(MessageCommand.NIGHT_FIGHT_FIGHT_RESULT_MESSAGE);

        byte result = FightCommon.playerVsPlayerFormula(this.player, fightB.getPlayer());
        if (result == FightDefine.FIGHT_RESULT_SUCCESS) {
            fightA.addPoint(NightFightDefine.POINT_WIN);
            fightB.addPoint(NightFightDefine.POINT_LOST);
            NightFightService.getInstance().dieNightFighter(fightB.getPlayer().getId());

            message.setInt(player.getId());
            message.setInt(fightB.getPlayer().getId());
        } else {
            fightB.addPoint(NightFightDefine.POINT_WIN);
            fightA.addPoint(NightFightDefine.POINT_LOST);
            NightFightService.getInstance().dieNightFighter(this.player.getId());

            message.setInt(fightB.getPlayer().getId());
            message.setInt(player.getId());
        }

        for (NightFighter nightFighter : NightFightService.getInstance().getFighterList()) {
            GameRole role = GameWorld.getPtr().getOnlineRole(nightFighter.getPlayer().getId());
            if (role != null) {
                role.putMessageQueue(message);
            }
        }

        gameRole.sendTick(request);

        NightFightService.getInstance().sortRank(true);
    }

    public void processExit(Message request) {
        NightFightService.getInstance().exitNightFighter(this.player);
        Message message = new Message(MessageCommand.NIGHT_FIGHT_EXIT_MESSAGE, request.getChannel());
        gameRole.sendMessage(message);
    }

    public void processTarget(Message request) {
        Message message = new Message(MessageCommand.NIGHT_FIGHT_TARGET_MESSAGE, request.getChannel());
        this.getTargetMessage(message);
        gameRole.sendMessage(message);
    }

    /**
     * 攻击怪物
     *
     * @param request
     */
    public void processMonster(Message request) {
        NightFighter fightA = NightFightService.getInstance().getNightFighter(this.player.getId());

        if (fightA.getAttackMonsterTime() != 0 && (fightA.getAttackMonsterTime() + NightFightDefine.ATTACK_MONSTER_TIME) > System.currentTimeMillis()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        fightA.setAttackMonsterTime(System.currentTimeMillis());
        fightA.addPoint(NightFightDefine.POINT_MONSTER_WIN);

        Message message = new Message(MessageCommand.NIGHT_FIGHT_MONSTER_MESSAGE, request.getChannel());
        gameRole.sendMessage(message);

        NightFightService.getInstance().sortRank(true);
    }

    /**
     * 847 夜战兑换战绩
     *
     * @param request
     */
    public void processExchange(Message request) {
        NightFighter fightA = NightFightService.getInstance().getNightFighter(this.player.getId());
        Entry<Short, DropData> entry = NightFightModel.getExchangeFeats(fightA.getExchangeMark());
        if (entry == null || fightA.getPoint() < entry.getKey()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        fightA.setExchangeMark(entry.getKey());

        short nextMax = fightA.getExchangeMax();

        Message message = new Message(MessageCommand.NIGHT_FIGHT_EXCHANGE_MESSAGE, request.getChannel());
        message.setShort(nextMax);
        gameRole.sendMessage(message);

        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        gameRole.getPackManager().addGoods(entry.getValue(), EGoodsChangeType.NIGHT_FIGHT_EXCHANGE_ADD, enumSet);
        gameRole.savePlayer(enumSet);
    }

    private void getTargetMessage(Message message) {
        Set<Integer> targetList = NightFightService.getInstance().getRandomTarget(this.player.getId());
        if (targetList == null) {
            message.setByte(0);
        } else {
            message.setByte(targetList.size());
            for (int index : targetList) {
                NightFighter temp = NightFightService.getInstance().getFighterList().get(index);
                message.setInt(temp.getPlayer().getId());
                message.setString(temp.getPlayer().getName());
            }
        }
    }

    /**
     * 851 夜战复活
     *
     * @param request
     */
    public void processRevive(Message request) {
        byte num = request.readByte();
        if (num < 0 || num > 100) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        NightFighter fighter = NightFightService.getInstance().getNightFighter(this.player.getId());
        if (fighter == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        if (!gameRole.getPackManager().useGoods(new DropData(EGoodsType.DIAMOND, 0, num), EGoodsChangeType.NIGHT_FIGHT_REVIVE_CONSUME, enumSet)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
            return;
        }
        fighter.setDieTime(0);
        gameRole.savePlayer(enumSet);

        NightFightService.getInstance().broadcastInto(fighter);

        gameRole.sendTick(request);
    }

    /**
     * 852 夜战倒计时
     *
     * @param request
     */
    public void processCountDown(Message request) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int week = c.get(Calendar.DAY_OF_WEEK);
        long startTime = 0;
        long curr = System.currentTimeMillis();
        String day = "";
        if (week == 7) {
            Date fightDate = DateUtil.getNowTimeBeforeOrAfter(2);
            day = DateUtil.formatDay(fightDate.getTime());
            startTime = DateUtil.parseDataTime(day + " " + NightFightDefine.START_TIME).getTime();
            startTime = startTime - curr;
        } else if (week == 1) {
            Date fightDate = DateUtil.getNowTimeBeforeOrAfter(1);
            day = DateUtil.formatDay(fightDate.getTime());
            startTime = DateUtil.parseDataTime(day + " " + NightFightDefine.START_TIME).getTime();
            startTime = startTime - curr;
        } else {
            day = DateUtil.formatDay(curr);
            startTime = DateUtil.parseDataTime(day + " " + NightFightDefine.START_TIME).getTime();
            long endTime = DateUtil.parseDataTime(day + " " + NightFightDefine.END_TIME).getTime();
            if (curr > endTime) {
                Date fightDate = DateUtil.getNowTimeBeforeOrAfter(1);
                day = DateUtil.formatDay(fightDate.getTime());
                startTime = DateUtil.parseDataTime(day + " " + NightFightDefine.START_TIME).getTime();
            }
            startTime = startTime - curr;
            if (startTime < 0) {
                startTime = 0;
            }
        }

        Message message = new Message(MessageCommand.NIGHT_FIGHT_COUNTDOWN_MESSAGE, request.getChannel());
        message.setInt((int) startTime);
        gameRole.sendMessage(message);
    }
}
