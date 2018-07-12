package com.rd.game.manager;

import com.rd.bean.boss.Boss;
import com.rd.bean.boss.BossBattlePlayer;
import com.rd.bean.drop.DropData;
import com.rd.bean.fighter.FighterData;
import com.rd.bean.goods.data.EquipData;
import com.rd.bean.player.AppearPlayer;
import com.rd.bean.player.Player;
import com.rd.common.BossService;
import com.rd.common.GameCommon;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.define.ErrorDefine;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.GameEvent;
import com.rd.model.BossModel;
import com.rd.model.FighterModel;
import com.rd.model.GoodsModel;
import com.rd.model.VipModel;
import com.rd.model.data.BossCitData;
import com.rd.model.data.BossMysteryData;
import com.rd.model.data.BossReinData;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;
import com.rd.util.GameUtil;
import org.apache.log4j.Logger;

import java.util.*;

import static com.rd.define.FightDefine.PLAYER_REVIVE_TIME;
import static com.rd.define.FightDefine.PVP_BATTLE_TIME;

public class BossManager {

    private static final Logger logger = Logger.getLogger(BossManager.class);

    //全民BOSS参战次数上限
    public static final short CIT_BOSS_FIGHT_MAX = 10;

    //全民BOSS次数恢复时间
    private static final long CIT_BOSS_FIGHT_RECOVE = DateUtil.HOUR;

    private GameRole role;
    private Player player;

    public BossManager(GameRole role) {
        this.role = role;
        this.player = role.getPlayer();
    }

    public void init() {

    }

    /**
     * 全民BOSS信息
     *
     * @param request
     */
    public void processCitizenInfo(Message request) {
        refreshCizBossCount();
        long curr = System.currentTimeMillis();
        Message msg = new Message(MessageCommand.BOSS_CITIZEN_INFO_MESSAGE, request.getChannel());
        //剩余多少次战斗次数
        short count = player.getCitBossLeft();
        msg.setShort(count);
        //距离下次恢复多少秒
        if (count >= CIT_BOSS_FIGHT_MAX)
            msg.setInt(0);
        else
            msg.setInt((int) ((player.getCitRecover() + CIT_BOSS_FIGHT_RECOVE - curr) / DateUtil.SECOND));

        //BOSS列表
        msg.setShort(BossService.getCitizenBoss().size());
        for (Boss boss : BossService.getCitizenBoss().values()) {
            BossCitData model = BossModel.getCitMap().get(boss.getId());
            msg.setShort(boss.getId());
            long hp = boss.getHp();
            if (hp <= 0)
                msg.setInt(0);
            else
                msg.setInt(boss.getBattlePlayers().size());
            msg.setLong(hp);
            if (hp <= 0) {
                msg.setInt((int) ((boss.getDeadTime() + model.getFuhuoTime() * DateUtil.SECOND - curr) / DateUtil.SECOND));
            } else {
                msg.setInt(0);
            }
        }
        role.sendMessage(msg);
    }

    /**
     * 全民BOSS战斗开始
     *
     * @param request
     */
    public void processCitizenStart(Message request) {
        if (role.getCheatManager().requestFrequent(request)) {
            return;
        }
        short id = request.readShort();
        long curr = System.currentTimeMillis();
        //已经在其他战斗中
        for (Boss boss : BossService.getCitizenBoss().values()) {
            if (boss.getId() == id)
                continue;
            if (boss.getBattlePlayers().containsKey(player.getId())) {
                removeCitizenPlayer(boss, player.getId());
                break;
            }
        }
        Boss boss = BossService.getCitizenBoss().get(id);
        //BOSS已经死亡
        if (boss == null || boss.getHp() <= 0) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_BOSS_DEAD);
            return;
        }
        BossBattlePlayer bbp = boss.getBattlePlayer(player.getId());
        if (bbp == null) {
            //第一次进扣除次数
            refreshCizBossCount();
            if (player.getCitBossLeft() <= 0) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_BOSS_FIGHT_MAX);
                return;
            }
            player.changeCitBossLeft(-1);
            bbp = new BossBattlePlayer(player);
        }
        //进入BOSS战时间
        if (boss.getRanks().size() == 0)
            boss.setCaller(bbp);

        // 刷新数据
        boss.addRank(bbp);
        boss.getBattlePlayers().put(bbp.getId(), bbp);
        //保存数据
        EnumSet<EPlayerSaveType> saves = EnumSet.of(EPlayerSaveType.CITBOSSLEFT, EPlayerSaveType.CITRECOVE);
        //发送消息
        Message msg = new Message(MessageCommand.BOSS_CITIZEN_START_MESSAGE);
        msg.setShort(id);
        int count = player.getCitBossLeft();
        msg.setShort(count);
        //距离下次恢复多少秒
        if (count >= CIT_BOSS_FIGHT_MAX)
            msg.setInt(0);
        else
            msg.setInt((int) ((player.getCitRecover() + CIT_BOSS_FIGHT_RECOVE - curr) / DateUtil.SECOND));
        //BOSS剩余血量
        msg.setLong(boss.getHp());
        role.putMessageQueue(msg);
        role.savePlayer(saves);
        //排行榜外形
        role.putMessageQueue(getBossCitAppearMsg(boss));
        //排行榜消息
        role.putMessageQueue(getBossTopMsg(boss));
        role.sendTick(request);
        //广播给其他战斗的玩家
        Message appearMsg = new Message(MessageCommand.BOSS_CITIZEN_APPEAR_MESSAGE);
        appearMsg.setByte(1);
        appearMsg.setInt(player.getId());
        appearMsg.setString(player.getName());
        player.getAppearMessage(appearMsg);
        appearMsg.setByte(player.getHead());
        appearMsg.setLong(player.getFighting());
        for (BossBattlePlayer bp : boss.getBattlePlayers().values()) {
            if (bp.getId() != player.getId()) {
                GameRole gr = GameWorld.getPtr().getOnlineRole(bp.getId());
                if (gr != null)
                    gr.putMessageQueue(appearMsg);
            }
        }
    }

    private void removeCitizenPlayer(Boss boss, int playerId) {
        boolean isCaller = boss.isCaller(playerId);
        boss.removePlayer(playerId);

        Message quitMsg = new Message(MessageCommand.BOSS_CITIZEN_QUIT_MESSAGE);
        quitMsg.setShort(boss.getId());
        quitMsg.setInt(playerId);
        GameRole quitRole = GameWorld.getPtr().getOnlineRole(playerId);
        if (quitRole != null) {
            quitRole.putMessageQueue(quitMsg);
        }

        // 广播
        if (!isCaller) {
            return;
        }
        for (BossBattlePlayer battlePlayer : boss.getBattlePlayers().values()) {
            GameRole gr = GameWorld.getPtr().getOnlineRole(battlePlayer.getId());
            if (gr == null)
                continue;
            gr.putMessageQueue(quitMsg);
        }
    }

    private Message getBossCitAppearMsg(Boss boss) {
        List<BossBattlePlayer> list = new ArrayList<>();
        BossBattlePlayer caller = boss.getCaller();
        if (caller != null) {
            // 没人打
            caller = boss.getBattlePlayer(caller.getId());
            list.add(caller);

            for (BossBattlePlayer bbp : boss.getRanks().values()) {
                if (bbp != null && bbp.getId() != caller.getId())
                    list.add(bbp);
            }
        }
        Message appearMsg = new Message(MessageCommand.BOSS_CITIZEN_APPEAR_MESSAGE);
        appearMsg.setByte(list.size());
        for (BossBattlePlayer bbp : list) {
            GameRole gr = GameWorld.getPtr().getOnlineRole(bbp.getId());
            Player player = (gr == null) ? GameWorld.getPtr().getOfflinePlayer(bbp.getId()) : gr.getPlayer();
            if (player != null) {
                appearMsg.setInt(player.getId());
                appearMsg.setString(player.getName());
                player.getAppearMessage(appearMsg);
                appearMsg.setByte(player.getHead());
                appearMsg.setLong(player.getFighting());
            } else {
                appearMsg.setInt(1);
                appearMsg.setString("");
                AppearPlayer appear = new AppearPlayer();
                appear.getMessage(appearMsg);
                appearMsg.setByte(1);
                appearMsg.setLong(1);
            }
        }
        return appearMsg;
    }

    private Message getBossTopAppearMsg(Boss boss) {
        Message appearMsg = new Message(MessageCommand.BOSS_REIN_APPEAR_MESSAGE);
        long curr = System.currentTimeMillis();
        int size = 0;
        List<BossBattlePlayer> list = new ArrayList<>();
        for (BossBattlePlayer bbp : boss.getRanks().values()) {
            //玩家已经死亡
            if (bbp.getDeadTime() > 0 && bbp.getDeadTime() + BossService.REVIVE_TIME > curr)
                continue;
            //玩家已离线
            if (curr - bbp.getLastTime() > BossService.FIGHT_OUTTIME)
                continue;
            list.add(bbp);
            size++;
            if (size >= 5)
                break;
        }
        appearMsg.setByte(list.size());
        for (BossBattlePlayer bbp : list) {
            GameRole gr = GameWorld.getPtr().getOnlineRole(bbp.getId());
            if (gr != null) {
                appearMsg.setInt(gr.getPlayerId());
                appearMsg.setString(gr.getPlayer().getName());
                gr.getPlayer().getAppearMessage(appearMsg);
            } else {
                appearMsg.setInt(1);
                appearMsg.setString("");
                appearMsg.setByte(1); //法宝外形
                appearMsg.setByte(1); //角色数量
                appearMsg.setByte(1);    //翅膀外形
                appearMsg.setByte(1);    //坐骑外形
                appearMsg.setShort(1);    //武器外形
                appearMsg.setShort(3);    //衣服外形
            }
        }
        return appearMsg;
    }

    /**
     * 全民BOSS战斗退出
     *
     * @param request
     */
    public void processCitizenQuit(Message request) {
        short id = request.readShort();
        Boss boss = BossService.getCitizenBoss().get(id);
        if (boss == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        if (!boss.getBattlePlayers().containsKey(role.getPlayerId())) {
            role.sendTick(request);
            return;
        }
        removeCitizenPlayer(boss, role.getPlayerId());
        role.sendTick(request);
//			//退出的玩家是当前BOSS的归属者
//			if (boss.getCaller().getId() == player.getId()) {
//				resetCitizenCaller(boss, 0);
//				Message topMsg = getBossTopMsg(boss);
//				//广播给所有人
//				for (BossBattlePlayer fp : boss.getBattlePlayers().values()) {
//					GameRole gr = GameWorld.getPtr().getOnlineRole(fp.getId());
//					if (gr == null)
//						continue;
//					gr.putMessageQueue(topMsg);
//				}
//			}
//		Message msg = new Message(MessageCommand.BOSS_CITIZEN_QUIT_MESSAGE, request.getChannel());
//		msg.setShort(id);
//		role.sendMessage(msg);
    }

    public void processCitizenPK(Message request) {
        short bossId = request.readShort();
        //int pkId = request.readInt();
        Boss boss = BossService.getCitizenBoss().get(bossId);
        if (boss == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        BossBattlePlayer caller = boss.getCaller();
        if (caller == null || caller.getId() == player.getId()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        long curr = System.currentTimeMillis();
        BossBattlePlayer self = boss.getBattlePlayer(player.getId());
        if (self.getDeadTime() + PLAYER_REVIVE_TIME > curr) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        if (boss.getPkTime() + PVP_BATTLE_TIME > curr) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_BE_ATTACK);
            return;
        }
        int[] state = boss.fightForBelonging(player);

        if (state[0] != ErrorDefine.ERROR_NONE) {
            role.sendErrorTipMessage(request, (short) state[0]);
            return;
        }

        Message deadMsg = new Message(MessageCommand.BOSS_CITIZEN_PK_MESSAGE);
        deadMsg.setInt(state[1]);
        deadMsg.setInt(state[2]);
        Message topMsg = getBossTopMsg(boss);
        //广播给所有人
        for (BossBattlePlayer fp : boss.getBattlePlayers().values()) {
            GameRole gr = GameWorld.getPtr().getOnlineRole(fp.getId());
            if (gr == null)
                continue;
            gr.putMessageQueue(topMsg);
            gr.putMessageQueue(deadMsg);
        }
        role.sendTick(request);
    }

    public void processCitizenRevive(Message request) {
        short bossId = request.readShort();
        Boss boss = BossService.getCitizenBoss().get(bossId);
        if (boss == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        BossBattlePlayer self = boss.getBattlePlayer(player.getId());
        if (self == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        long curr = System.currentTimeMillis();
        Message msg = new Message(MessageCommand.BOSS_CITIZEN_PLAYER_REVIVE_MESSAGE, request.getChannel());
        if (self.getDeadTime() + PLAYER_REVIVE_TIME <= curr) {
            role.sendMessage(msg);
            return;
        }
        //花钱复活
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        //消耗
        DropData cost = new DropData(EGoodsType.DIAMOND, 0, 200);
        if (!role.getPackManager().useGoods(cost, EGoodsChangeType.BOSS_REVIVE_CONSUME, saves)) {
            role.putErrorMessage(ErrorDefine.ERROR_DIAMOND_LESS);
            return;
        }
        self.setDeadTime(0);

        role.sendMessage(msg);
        role.savePlayer(saves);
    }

    /**
     * 全民BOSS战斗
     *
     * @param request
     */
    public void processCitizenFight(Message request) {
        short id = request.readShort();
        int damage = request.readInt();
        if (damage < 0) {
            damage = 0;
        }
        Boss boss = BossService.getCitizenBoss().get(id);
        logger.info(player.getName() + " `s damage=" + damage + " boss=" + id + " curHp=" + boss.getHp());
        Message msg = new Message(MessageCommand.BOSS_CITIZEN_FIGHT_MESSAGE, request.getChannel());
        msg.setShort(id);
        //BOSS已经死亡
        if (boss == null || boss.getHp() <= 0) {
            msg.setLong(0);
            role.sendMessage(msg);
            return;
        }
        //未参战
        if (boss.getBattlePlayer(player.getId()) == null) {
            role.putErrorMessage(ErrorDefine.ERROR_BOSS_CITIZEN_START);
            msg.setLong(boss.getHp());
            role.sendMessage(msg);
            return;
        }
        //攻击BOSS
        BossService.atkCitizenBoss(player, boss, damage);
        //排行榜消息
        role.putMessageQueue(getBossTopMsg(boss));
        //发送消息
        msg.setLong(boss.getHp());
        role.sendMessage(msg);
    }

    /**
     * 更新全民BOSS参战次数
     */
    public void refreshCizBossCount() {
        if (player.getCitBossLeft() >= CIT_BOSS_FIGHT_MAX) {
            return;
        }
        long curr = System.currentTimeMillis();
        if (player.getCitRecover() <= 0) {
            player.setCitBossLeft(CIT_BOSS_FIGHT_MAX);
            player.setCitRecover(curr);
        } else {
            long pass = curr - player.getCitRecover();
            if (pass > 0) {
                int count = (int) (pass / CIT_BOSS_FIGHT_RECOVE);
                player.changeCitBossLeft(count);
                if (player.getCitBossLeft() >= CIT_BOSS_FIGHT_MAX) {
                    player.setCitBossLeft(CIT_BOSS_FIGHT_MAX);
                    player.setCitRecover(curr);
                } else {
                    player.setCitRecover(player.getCitRecover() + count * CIT_BOSS_FIGHT_RECOVE);
                }
            }
        }
    }

    /**
     * 全民BOSS提醒设置
     *
     * @param request
     */
    public void processCitizenCue(Message request) {
        short size = request.readShort();
        if (player.getCitCue() == null)
            player.setCitCue(new ArrayList<Short>());
        player.getCitCue().clear();
        for (int i = 0; i < size; i++) {
            player.getCitCue().add(request.readShort());
        }
        EnumSet<EPlayerSaveType> saves = EnumSet.of(EPlayerSaveType.CITCUE);
        role.savePlayer(saves);
    }

    public Message getCitCueMsg() {
        Message msg = new Message(MessageCommand.BOSS_CITIZEN_CUE_MESSAGE);
        //所有的BOSS
        if (player.getCitCue() == null) {
            msg.setShort(BossModel.getCitMap().size());
            for (short id : BossModel.getCitMap().keySet()) {
                msg.setShort(id);
            }
        } else {
            msg.setShort(player.getCitCue().size());
            for (short id : player.getCitCue()) {
                msg.setShort(id);
            }
        }
        return msg;
    }

    public Message getBossTopMsg(Boss boss) {
        long damage = 0;
        BossBattlePlayer self = boss.getBattlePlayer(player.getId());
        if (self != null)
            damage = self.getDamage();
        Message msg = new Message(MessageCommand.BOSS_CITIZEN_TOP_MESSAGE);
        msg.setInt((int) damage);
        List<BossBattlePlayer> list = new ArrayList<>();
        BossBattlePlayer caller = boss.getCaller();
        if (caller != null) {
            caller = boss.getBattlePlayer(caller.getId());
            list.add(caller);
            for (BossBattlePlayer bbp : boss.getRanks().values()) {
                if (bbp != null && bbp.getId() != caller.getId())
                    list.add(bbp);
            }
        }
        msg.setShort(list.size());
        for (BossBattlePlayer bbp : list) {
            msg.setInt(bbp.getId());
            msg.setString(bbp.getName());
            msg.setInt((int) bbp.getDamage());
            msg.setInt((int) bbp.getFighting());
        }
        return msg;
    }

    /**
     * 全民BOSS奖励
     *
     * @param request
     */
    public void processCitizenReward(Message request) {
        short bossId = request.readShort();
        Message msg = new Message(MessageCommand.BOSS_CITIZEN_REWARD_MESSAGE, request.getChannel());
        Boss boss = BossService.getCitizenBoss().get(bossId);
        if (boss == null || boss.getHp() > 0) {
            msg.setByte(0);
            role.sendMessage(msg);
            return;
        }
        BossBattlePlayer bbp = boss.getBattlePlayer(role.getPlayerId());
        if (bbp == null || bbp.getDamage() == 0) {
            msg.setByte(2);
            role.sendMessage(msg);
            return;
        }
        if (boss.getRanks() == null || boss.getRanks().size() == 0) {
            msg.setByte(3);
            role.sendMessage(msg);
            return;
        }
        bbp.setDamage(0);
        BossCitData model = BossModel.getCitMap().get(boss.getId());
        //是否伤害第一
        int first = boss.getFirstRank(bbp.getId());
        //奖励数量
        int num = 0;
        if (bbp.getRein() <= 0) {
            if (bbp.getLevel() / 10 >= BossService.CIT_LV_EQUIP.length)
                num = BossService.CIT_LV_EQUIP[BossService.CIT_LV_EQUIP.length - 1];
            else
                num = BossService.CIT_LV_EQUIP[bbp.getLevel() / 10];
        } else {
            if (bbp.getRein() >= BossService.CIT_REIN_EQUIP.length)
                num = BossService.CIT_REIN_EQUIP[BossService.CIT_REIN_EQUIP.length - 1];
            else
                num = BossService.CIT_REIN_EQUIP[bbp.getRein()];
        }
        List<DropData> rewards = new ArrayList<>();
        rewards.addAll(model.getRewards());
        //使用哪个随机
        int[] rates = null;
        if (first == 1) {
            if (bbp.getRein() >= 3)
                rates = BossService.CIT_FIRST_HIG;
            else
                rates = BossService.CIT_FIRST_LOW;
        } else {
            if (bbp.getRein() >= 3)
                rates = BossService.CIT_COMMON_HIG;
            else
                rates = BossService.CIT_COMMON_LOW;
        }
        FighterData fd = FighterModel.getFighterDataById(model.getModelId());
        //随机装备
        for (int i = 0; i < num; i++) {
            int random = GameUtil.getRangedRandom(1, 100);
            int quality = GameUtil.getRatesIndex(rates, random);
            EquipData equipData = GoodsModel.getRandomDataByLv(fd.getLevel());
            if (equipData == null)
                continue;
            DropData drop = new DropData(EGoodsType.EQUIP.getId(), equipData.getGoodsId(),
                    (byte) quality, 1);
            rewards.add(drop);
        }
        //消息
        msg.setByte(1);
        msg.setByte(first);
        msg.setByte(rewards.size());
        for (DropData data : rewards) {
            msg.setByte(data.getT());
            msg.setShort(data.getG());
            msg.setByte(data.getQ());
            msg.setInt(data.getN());
        }
        role.sendMessage(msg);
        //增加物品
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        role.getPackManager().addGoods(rewards, EGoodsChangeType.CIT_BOSS_ADD, saves);
        role.savePlayer(saves);
    }

    /**
     * 转生BOSS列表信息
     *
     * @param request
     */
    public void processReinInfo(Message request) {
        long curr = System.currentTimeMillis();
        long start = DateUtil.getDayStartTime(curr) + BossService.BOSS_REIN_START;
        long end = start + BossService.BOSS_REIN_DURATION;
        Message msg = new Message(MessageCommand.BOSS_REIN_INFO_MESSAGE, request.getChannel());
        //是否有已经参战的BOSS
        Boss fightBoss = BossService.getFightReinBoss(player.getId());
        //战斗CD
        int revive = 0;
        if (fightBoss != null) {
            BossBattlePlayer bbp = fightBoss.getBattlePlayer(player.getId());
            revive = (int) ((bbp.getDeadTime() + BossService.REVIVE_TIME - curr) / DateUtil.SECOND);
        }
        msg.setInt(revive < 0 ? 0 : revive);
        //距离下次转生BOSS战剩余多少秒
        int left = 0;
        if (curr < start)
            left = (int) ((start - curr) / DateUtil.SECOND);
        else if (curr >= end)
            left = (int) ((start + DateUtil.DAY - curr) / DateUtil.SECOND);
        msg.setInt(left);
        msg.setShort(BossService.getReinBoss().size());
        for (Boss boss : BossService.getReinBoss().values()) {
            msg.setShort(boss.getId());
            msg.setLong(boss.getHpMax());
            msg.setLong(boss.getHp());
        }
        role.sendMessage(msg);
    }

    /**
     * 转生BOSS开始战斗
     *
     * @param request
     */
    public void processReinStart(Message request) {
        //正在参战的BOSS
        Boss boss = BossService.getFightReinBoss(player.getId());
        if (boss == null) {
            BossReinData reinData = BossModel.getReinData(player);
            //等级不足
            if (reinData == null) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_LEVEL_LESS);
                return;
            }
            boss = BossService.getReinBoss().get(reinData.getId());
            if (boss == null || boss.getHp() <= 0) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_BOSS_DEADED);
                return;
            }
            BossBattlePlayer bbp = new BossBattlePlayer(player);
            boss.getBattlePlayers().put(bbp.getId(), bbp);
        } else {
            if (boss.getHp() <= 0) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_BOSS_DEADED);
                return;
            }
        }
        BossBattlePlayer bbp = boss.getBattlePlayer(player.getId());
        long curr = System.currentTimeMillis();
        if (bbp.getDeadTime() > 0 && bbp.getDeadTime() + BossService.REVIVE_TIME > curr) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_FIGHT_DEAD);
            return;
        }
        //发送消息
        Message msg = new Message(MessageCommand.BOSS_REIN_START_MESSAGE);
        msg.setShort(boss.getId());
        msg.setLong(boss.getHpMax());
        //BOSS剩余血量
        msg.setLong(boss.getHp());
        role.putMessageQueue(msg);
        //BOSS目标
        role.putMessageQueue(BossService.getReinTargetMsg(boss));
        //排行榜外形
        role.putMessageQueue(getBossTopAppearMsg(boss));
        role.sendTick(request);
        //广播给其他战斗的玩家
        Message appearMsg = new Message(MessageCommand.BOSS_REIN_APPEAR_MESSAGE);
        appearMsg.setByte(1);
        appearMsg.setInt(player.getId());
        appearMsg.setString(player.getName());
        player.getAppearMessage(appearMsg);
        for (BossBattlePlayer bp : boss.getBattlePlayers().values()) {
            if (bp.getId() != player.getId()) {
                GameRole gr = GameWorld.getPtr().getOnlineRole(bp.getId());
                if (gr != null)
                    gr.putMessageQueue(appearMsg);
            }
        }
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        GameEvent event = new GameEvent(EGameEventType.REIN_BOSS, 1, enumSet);
        role.getEventManager().notifyEvent(event);
        role.savePlayer(enumSet);
    }

    /**
     * 转生BOSS战斗
     *
     * @param request
     */
    public void processReinFight(Message request) {
        short bossId = request.readShort();
        int damage = request.readInt();
        Boss boss = BossService.getReinBoss().get(bossId);
        Message msg = new Message(MessageCommand.BOSS_REIN_FIGHT_MESSAGE, request.getChannel());
        msg.setShort(bossId);
        //BOSS已经死亡
        if (boss == null || boss.getHp() <= 0) {
            msg.setByte(0);
            msg.setLong(0);
            role.sendMessage(msg);
            return;
        }
        BossBattlePlayer bbp = boss.getBattlePlayer(player.getId());
        if (bbp == null) {
            role.putErrorMessage(ErrorDefine.ERROR_BOSS_CITIZEN_START);
            msg.setByte(1);
            msg.setLong(boss.getHp());
            role.sendMessage(msg);
            return;
        }
        if (damage > 0 && bbp.getDeadTime() > 0 &&
                bbp.getDeadTime() + BossService.REVIVE_TIME > System.currentTimeMillis()) {
            msg.setByte(1);
            msg.setLong(boss.getHp());
            role.sendMessage(msg);
            return;
        }
        //攻击BOSS
        BossService.atkReinBoss(player, boss, damage);
        //排行榜消息
        role.putMessageQueue(getReinTopMsg(boss));
        //发送消息
        msg.setByte(1);
        msg.setLong(boss.getHp());
        role.sendMessage(msg);
    }

    /**
     * 转生BOSS排行榜
     *
     * @param boss
     * @return
     */
    private Message getReinTopMsg(Boss boss) {
        long damage = 0;
        BossBattlePlayer self = boss.getBattlePlayer(player.getId());
        if (self != null)
            damage = self.getDamage();
        Message msg = new Message(MessageCommand.BOSS_REIN_TOP_MESSAGE);
        msg.setLong(damage);
        msg.setShort(boss.getRanks().size());
        for (BossBattlePlayer bbp : boss.getRanks().values()) {
            msg.setInt(bbp.getId());
            msg.setString(bbp.getName());
            msg.setLong(bbp.getDamage());
        }
        return msg;
    }

    /**
     * 转生BOSS复活
     *
     * @param request
     */
    public void processReinRevive(Message request) {
        byte yuanbao = request.readByte();
        Boss boss = BossService.getFightReinBoss(player.getId());
        if (boss == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_BOSS_DEADED);
            return;
        }
        BossBattlePlayer bbp = boss.getBattlePlayer(player.getId());
        if (bbp == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_BOSS_DEADED);
            return;
        }
        long curr = System.currentTimeMillis();
        int left = 0;
        if (bbp.getDeadTime() > 0 && bbp.getDeadTime() + BossService.REVIVE_TIME > curr)
            left = (int) ((bbp.getDeadTime() + BossService.REVIVE_TIME - curr) / 1000);
        //花钱复活
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        if (left > 0 && yuanbao == 1) {
            //消耗
            DropData cost = new DropData(EGoodsType.DIAMOND, 0, 200);
            if (role.getPackManager().useGoods(cost, EGoodsChangeType.BOSS_REVIVE_CONSUME, saves)) {
                left = 0;
                bbp.setLastTime(0);
                bbp.setDeadTime(0);
            } else {
                role.putErrorMessage(ErrorDefine.ERROR_DIAMOND_LESS);
            }
        } else if (left <= 0) {
            left = 0;
            bbp.setLastTime(0);
            bbp.setDeadTime(0);
        }
        Message msg = new Message(MessageCommand.BOSS_REIN_REVIVE_MESSAGE, request.getChannel());
        msg.setInt(left);
        role.sendMessage(msg);
        if (saves.size() > 0)
            role.savePlayer(saves);
        //广播给其他战斗的玩家
        if (left <= 0) {
            Message appearMsg = new Message(MessageCommand.BOSS_REIN_APPEAR_MESSAGE);
            appearMsg.setByte(1);
            appearMsg.setInt(player.getId());
            appearMsg.setString(player.getName());
            player.getAppearMessage(appearMsg);
            for (BossBattlePlayer bp : boss.getBattlePlayers().values()) {
                if (bp.getId() != player.getId()) {
                    GameRole gr = GameWorld.getPtr().getOnlineRole(bp.getId());
                    if (gr != null)
                        gr.putMessageQueue(appearMsg);
                }
            }
        }
    }

    /**
     * 转生BOSS上次排行
     *
     * @param request
     */
    public void processReinHistory(Message request) {
        short bossId = request.readShort();
        Boss boss = BossService.getReinBoss().get(bossId);
        Message msg = new Message(MessageCommand.BOSS_REIN_HISTORY_MESSAGE, request.getChannel());
        if (boss == null) {
            msg.setByte(0);
            msg.setBool(false);
        } else {
            //上次排行榜
            if (boss.getHistory() == null) {
                msg.setByte(0);
            } else {
                msg.setByte(boss.getHistory().size());
                for (BossBattlePlayer bbp : boss.getHistory()) {
                    msg.setInt(bbp.getId());
                    msg.setString(bbp.getName());
                    msg.setLong(bbp.getDamage());
                    msg.setByte(VipModel.getVipLv(bbp.getVip()));
                }
            }
            //击杀者
            if (boss.getKiller() == null)
                msg.setBool(false);
            else {
                msg.setBool(true);
                msg.setString(boss.getLastKiller().getName());
                msg.setLong(boss.getLastKiller().getDamage());
            }
        }
        role.sendMessage(msg);
    }

    //////////////////////////////////////////////////////////////////////////////秘境BOSS开始/////////////////////////////////////////////////////////////////////////////////////

    /**
     * 秘境BOSS信息
     *
     * @param request
     */
    public void processMysteryInfo(Message request) {

        long curr = System.currentTimeMillis();
        Message msg = new Message(MessageCommand.BOSS_MYSTERY_INFO_MESSAGE, request.getChannel());

        //剩余多少次战斗次数
        short count = player.getMysteryBossLeft();

        msg.setShort(count);

        short level = player.getLevel();
        short rein = player.getRein();
        int levelCount = 0;

        if (rein == 0) {
            if (level >= 90) {
                levelCount = 89;
            } else {
                levelCount = level;
            }
        } else {
            levelCount = 90 + (rein - 1) * 10;
        }

        msg.setByte(2);

        Map<String, ArrayList<Boss>> map = new HashMap<>();
        for (Boss boss : BossService.getMysteryBoss().values()) {
            short id = boss.getId();
            BossMysteryData model = BossModel.getMysteryMap().get(id);
            String levelScope = model.getLevelScope();
            if (map.containsKey(levelScope)) {
                map.get(levelScope).add(boss);
            } else {
                ArrayList<Boss> list = new ArrayList<>();
                list.add(boss);
                map.put(levelScope, list);
            }
        }

        Set<String> levelStr = map.keySet();
        for (String str : levelStr) {
            String[] split = str.split("#");
            int min = Integer.parseInt(split[0]);
            int max = Integer.parseInt(split[1]);

            if (levelCount >= min && levelCount <= max) {
                ArrayList<Boss> bossList = map.get(str);
                Boss firstBoss = bossList.get(0);
                BossMysteryData firstModel = BossModel.getMysteryMap().get(firstBoss.getId());
                Boss nextBoss = bossList.get(1);
                BossMysteryData nextModel = BossModel.getMysteryMap().get(nextBoss.getId());
                setMessage(msg, firstBoss, firstModel, curr);
                setMessage(msg, nextBoss, nextModel, curr);
                break;
            }

        }

        role.sendMessage(msg);
    }

    private void setMessage(Message msg, Boss boss, BossMysteryData Model, long curr) {

        msg.setShort(boss.getId());
        long hp = boss.getHp();
        msg.setLong(hp);
        if (hp <= 0) {
            msg.setInt((int) ((boss.getDeadTime() + Model.getFuhuoTime() * DateUtil.SECOND - curr) / DateUtil.SECOND));
        } else {
            msg.setInt(0);
        }
    }


    /**
     * 秘境BOSS战斗开始
     *
     * @param request
     */
    public void processMysteryStart(Message request) {

        if (role.getCheatManager().requestFrequent(request)) {
            return;
        }
        short id = request.readShort();
        //已经在其他战斗中
        for (Boss boss : BossService.getMysteryBoss().values()) {
            if (boss.getId() == id)
                continue;
            if (boss.getBattlePlayers().containsKey(player.getId())) {
                removeMysteryPlayer(boss, player.getId());
                break;
            }
        }
        Boss boss = BossService.getMysteryBoss().get(id);
        //BOSS已经死亡
        if (boss == null || boss.getHp() <= 0) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_BOSS_DEAD);
            return;
        }
        BossBattlePlayer bbp = boss.getBattlePlayer(player.getId());
        if (bbp == null) {
            //第一次进扣除次数
            if (player.getMysteryBossLeft() <= 0) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_BOSS_FIGHT_MAX);
                return;
            }
            player.changeMysteryBossLeft(-1);
            bbp = new BossBattlePlayer(player);
        }
        //进入BOSS战时间
        if (boss.getRanks().size() == 0)
            boss.setCaller(bbp);
        // 刷新数据
        boss.addRank(bbp);
        boss.getBattlePlayers().put(bbp.getId(), bbp);
        //保存数据
        EnumSet<EPlayerSaveType> saves = EnumSet.of(EPlayerSaveType.MYSTERYBOSSLEFT);

        //发送消息
        Message msg = new Message(MessageCommand.BOSS_MYSTERY_START_MESSAGE);
        msg.setShort(id);
        int count = player.getMysteryBossLeft();
        msg.setShort(count);
        //BOSS剩余血量
        msg.setLong(boss.getHp());
        role.putMessageQueue(msg);
        role.savePlayer(saves);
        //排行榜外形
        role.putMessageQueue(getBossMysteryAppearMsg(boss));
        //排行榜消息
        role.putMessageQueue(getMysteryBossTopMsg(boss));
        role.sendTick(request);
        //广播给其他战斗的玩家
        Message appearMsg = new Message(MessageCommand.BOSS_MYSTERY_APPEAR_MESSAGE);
        appearMsg.setByte(1);
        appearMsg.setInt(player.getId());
        appearMsg.setString(player.getName());
        player.getAppearMessage(appearMsg);
        appearMsg.setByte(player.getHead());
        appearMsg.setLong(player.getFighting());
        for (BossBattlePlayer bp : boss.getBattlePlayers().values()) {
            if (bp.getId() != player.getId()) {
                GameRole gr = GameWorld.getPtr().getOnlineRole(bp.getId());
                if (gr != null)
                    gr.putMessageQueue(appearMsg);
            }
        }

    }

    private void removeMysteryPlayer(Boss boss, int playerId) {
        boolean isCaller = boss.isCaller(playerId);
        boss.removePlayer(playerId);

        Message quitMsg = new Message(MessageCommand.BOSS_MYSTERY_QUIT_MESSAGE);
        quitMsg.setShort(boss.getId());
        quitMsg.setInt(playerId);
        GameRole quitRole = GameWorld.getPtr().getOnlineRole(playerId);
        if (quitRole != null) {
            quitRole.putMessageQueue(quitMsg);
        }

        // 广播
        if (!isCaller) {
            return;
        }
        for (BossBattlePlayer battlePlayer : boss.getBattlePlayers().values()) {
            GameRole gr = GameWorld.getPtr().getOnlineRole(battlePlayer.getId());
            if (gr == null)
                continue;
            gr.putMessageQueue(quitMsg);
        }
    }

    /**
     * 秘境BOSS战斗
     *
     * @param request
     */
    public void processMysteryFight(Message request) {

        short id = request.readShort();
        int damage = request.readInt();

        if (damage < 0) {
            damage = 0;
        }

        Boss boss = BossService.getMysteryBoss().get(id);
        Message msg = new Message(MessageCommand.BOSS_MYSTERY_FIGHT_MESSAGE, request.getChannel());
        msg.setShort(id);
        //BOSS已经死亡
        if (boss == null || boss.getHp() <= 0) {
            msg.setLong(0);
            role.sendMessage(msg);
            return;
        }
        //未参战
        if (boss.getBattlePlayer(player.getId()) == null) {
            role.putErrorMessage(ErrorDefine.ERROR_BOSS_CITIZEN_START);
            msg.setLong(boss.getHp());
            role.sendMessage(msg);
            return;
        }
        //攻击BOSS
        BossService.atkMysteryBoss(player, boss, damage);
        //排行榜消息
        role.putMessageQueue(getMysteryBossTopMsg(boss));
        //发送消息
        msg.setLong(boss.getHp());
        role.sendMessage(msg);
    }


    /**
     * 秘境BOSS战斗退出
     *
     * @param request
     */
    public void processMysteryQuit(Message request) {
        short id = request.readShort();
        Boss boss = BossService.getMysteryBoss().get(id);
        if (boss == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        if (!boss.getBattlePlayers().containsKey(role.getPlayerId())) {
            role.sendTick(request);
            return;
        }
        removeMysteryPlayer(boss, role.getPlayerId());
        role.sendTick(request);
//			//退出的玩家是当前BOSS的归属者
//			if (boss.getCaller().getId() == player.getId()) {
//				resetCitizenCaller(boss, 0);
//				Message topMsg = getMysteryBossTopMsg(boss);
//				//广播给所有人
//				for (BossBattlePlayer fp : boss.getBattlePlayers().values()) {
//					GameRole gr = GameWorld.getPtr().getOnlineRole(fp.getId());
//					if (gr == null)
//						continue;
//					gr.putMessageQueue(topMsg);
//				}
//			}
//		Message msg = new Message(MessageCommand.BOSS_MYSTERY_QUIT_MESSAGE, request.getChannel());
//		msg.setShort(id);
//		role.sendMessage(msg);

    }

    /**
     * 秘境BOSS战斗奖励
     *
     * @param request
     */
    public void processMysteryReward(Message request) {

        short bossId = request.readShort();
        Message msg = new Message(MessageCommand.BOSS_MYSTERY_REWARD_MESSAGE, request.getChannel());
        Boss boss = BossService.getMysteryBoss().get(bossId);
        if (boss == null || boss.getHp() > 0) {
            msg.setByte(0);
            role.sendMessage(msg);
            return;
        }
        BossBattlePlayer bbp = boss.getBattlePlayer(role.getPlayerId());
        if (bbp == null || bbp.getDamage() == 0) {
            msg.setByte(2);
            role.sendMessage(msg);
            return;
        }
        if (boss.getRanks() == null || boss.getRanks().size() == 0) {
            msg.setByte(3);
            role.sendMessage(msg);
            return;
        }
        bbp.setDamage(0);
        BossMysteryData model = BossModel.getMysteryMap().get(boss.getId());
        //是否伤害第一
        int first = boss.getFirstRank(bbp.getId());
        //奖励数量
        int num = 0;
        if (bbp.getRein() <= 0) {
            if (bbp.getLevel() / 10 >= BossService.CIT_LV_EQUIP.length)
                num = BossService.CIT_LV_EQUIP[BossService.CIT_LV_EQUIP.length - 1];
            else
                num = BossService.CIT_LV_EQUIP[bbp.getLevel() / 10];
        } else {
            if (bbp.getRein() >= BossService.CIT_REIN_EQUIP.length)
                num = BossService.CIT_REIN_EQUIP[BossService.CIT_REIN_EQUIP.length - 1];
            else
                num = BossService.CIT_REIN_EQUIP[bbp.getRein()];
        }
        List<DropData> rewards = new ArrayList<>();
        rewards.addAll(model.getRewards());
        //使用哪个随机
        int[] rates = null;
        if (first == 1) {
            if (bbp.getRein() >= 3)
                rates = BossService.CIT_FIRST_HIG;
            else
                rates = BossService.CIT_FIRST_LOW;
        } else {
            if (bbp.getRein() >= 3)
                rates = BossService.CIT_COMMON_HIG;
            else
                rates = BossService.CIT_COMMON_LOW;
        }
        FighterData fd = FighterModel.getFighterDataById(model.getModelId());
        //随机装备
        for (int i = 0; i < num; i++) {
            int random = GameUtil.getRangedRandom(1, 100);
            int quality = GameUtil.getRatesIndex(rates, random);
            EquipData equipData = GoodsModel.getRandomDataByLv(fd.getLevel());
            if (equipData == null)
                continue;
            DropData drop = new DropData(EGoodsType.EQUIP.getId(), equipData.getGoodsId(),
                    (byte) quality, 1);
            rewards.add(drop);
        }
        //消息
        msg.setByte(1);
        msg.setByte(first);
        msg.setByte(rewards.size());
        for (DropData data : rewards) {
            msg.setByte(data.getT());
            msg.setShort(data.getG());
            msg.setByte(data.getQ());
            msg.setInt(data.getN());
        }
        role.sendMessage(msg);
        //增加物品
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        role.getPackManager().addGoods(rewards, EGoodsChangeType.MYSTERY_BOSS_ADD, saves);
        role.savePlayer(saves);

    }

    /**
     * 秘境BOSS提醒设置
     *
     * @param request
     */
    public void processMysteryCue(Message request) {

        short size = request.readShort();
        if (player.getMysteryCue() == null)
            player.setMysteryCue(new ArrayList<Short>());
        player.getMysteryCue().clear();
        for (int i = 0; i < size; i++) {
            player.getMysteryCue().add(request.readShort());
        }
        EnumSet<EPlayerSaveType> saves = EnumSet.of(EPlayerSaveType.MYSTERYCUE);
        role.savePlayer(saves);

    }

    public Message getMysteryMsg() {
        Message msg = new Message(MessageCommand.BOSS_MYSTERY_CUE_MESSAGE);
        //所有的BOSS
        if (player.getMysteryCue() == null) {
            msg.setShort(BossModel.getMysteryMap().size());
            for (short id : BossModel.getMysteryMap().keySet()) {
                msg.setShort(id);
            }
        } else {
            msg.setShort(player.getMysteryCue().size());
            for (short id : player.getMysteryCue()) {
                msg.setShort(id);
            }
        }
        return msg;
    }

    /**
     * 秘境BOSS争夺归属权
     *
     * @param request
     */
    public void processMysteryPK(Message request) {

        short bossId = request.readShort();
        //int pkId = request.readInt();
        Boss boss = BossService.getMysteryBoss().get(bossId);
        if (boss == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        BossBattlePlayer caller = boss.getCaller();
        if (caller == null || caller.getId() == player.getId()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        long curr = System.currentTimeMillis();
        BossBattlePlayer self = boss.getBattlePlayer(player.getId());
        if (self.getDeadTime() + PLAYER_REVIVE_TIME > curr) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        if (boss.getPkTime() + PVP_BATTLE_TIME > curr) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_BE_ATTACK);
            return;
        }

        int[] state = boss.fightForBelonging(player);

        if (state[0] != ErrorDefine.ERROR_NONE) {
            role.sendErrorTipMessage(request, (short) state[0]);
            return;
        }

        Message deadMsg = new Message(MessageCommand.BOSS_MYSTERY_PK_MESSAGE);
        deadMsg.setInt(state[1]);
        deadMsg.setInt(state[2]);
        Message topMsg = getMysteryBossTopMsg(boss);
        //广播给所有人
        for (BossBattlePlayer fp : boss.getBattlePlayers().values()) {
            GameRole gr = GameWorld.getPtr().getOnlineRole(fp.getId());
            if (gr == null)
                continue;
            gr.putMessageQueue(topMsg);
            gr.putMessageQueue(deadMsg);
        }
        role.sendTick(request);
    }

    /**
     * 秘境BOSS玩家复活
     *
     * @param request
     */
    public void processMysteryRevive(Message request) {

        short bossId = request.readShort();
        Boss boss = BossService.getMysteryBoss().get(bossId);
        if (boss == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        BossBattlePlayer self = boss.getBattlePlayer(player.getId());
        if (self == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        long curr = System.currentTimeMillis();
        Message msg = new Message(MessageCommand.BOSS_MYSTERY_PLAYER_REVIVE_MESSAGE, request.getChannel());
        if (self.getDeadTime() + PLAYER_REVIVE_TIME <= curr) {
            role.sendMessage(msg);
            return;
        }
        //花钱复活
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        //消耗
        DropData cost = new DropData(EGoodsType.DIAMOND, 0, 200);
        if (!role.getPackManager().useGoods(cost, EGoodsChangeType.BOSS_REVIVE_CONSUME, saves)) {
            role.putErrorMessage(ErrorDefine.ERROR_DIAMOND_LESS);
            return;
        }
        self.setDeadTime(0);

        role.sendMessage(msg);
        role.savePlayer(saves);
    }

    /**
     * 秘境BOSS外形数据
     *
     * @param boss
     * @return
     */
    private Message getBossMysteryAppearMsg(Boss boss) {

        List<BossBattlePlayer> list = new ArrayList<>();
        BossBattlePlayer caller = boss.getCaller();
        if (caller != null) {
            caller = boss.getBattlePlayer(caller.getId());
            list.add(caller);

            for (BossBattlePlayer bbp : boss.getRanks().values()) {
                if (bbp != null && bbp.getId() != caller.getId())
                    list.add(bbp);
            }
        }
        Message appearMsg = new Message(MessageCommand.BOSS_MYSTERY_APPEAR_MESSAGE);
        appearMsg.setByte(list.size());
        for (BossBattlePlayer bbp : list) {
            GameRole gr = GameWorld.getPtr().getOnlineRole(bbp.getId());
            Player player = (gr == null) ? GameWorld.getPtr().getOfflinePlayer(bbp.getId()) : gr.getPlayer();
            if (player != null) {
                appearMsg.setInt(player.getId());
                appearMsg.setString(player.getName());
                player.getAppearMessage(appearMsg);
                appearMsg.setByte(player.getHead());
                appearMsg.setLong(player.getFighting());
            } else {
                appearMsg.setInt(1);
                appearMsg.setString("");
                AppearPlayer appear = new AppearPlayer();
                appear.getMessage(appearMsg);
                appearMsg.setByte(1);
                appearMsg.setLong(1);
            }
        }
        return appearMsg;
    }

    /**
     * 秘境BOSS排行榜
     *
     * @param boss
     * @return
     */
    public Message getMysteryBossTopMsg(Boss boss) {
        long damage = 0;
        BossBattlePlayer self = boss.getBattlePlayer(player.getId());
        if (self != null)
            damage = self.getDamage();
        Message msg = new Message(MessageCommand.BOSS_MYSTERY_TOP_MESSAGE);
        msg.setInt((int) damage);
        List<BossBattlePlayer> list = new ArrayList<>();
        BossBattlePlayer caller = boss.getCaller();
        if (caller != null) {
            caller = boss.getBattlePlayer(caller.getId());
            list.add(caller);
            for (BossBattlePlayer bbp : boss.getRanks().values()) {
                if (bbp != null && bbp.getId() != caller.getId())
                    list.add(bbp);
            }
        }
        msg.setShort(list.size());
        for (BossBattlePlayer bbp : list) {
            msg.setInt(bbp.getId());
            msg.setString(bbp.getName());
            msg.setInt((int) bbp.getDamage());
            msg.setInt((int) bbp.getFighting());
        }
        return msg;
    }


    /////////////////////////////////////////////////////////////////////BOSS之家开始/////////////////////////////////////////////////////////////////////////////

    /**
     * BOSS之家信息
     *
     * @param request
     */
    public void processVipBossInfo(Message request) {

        long curr = System.currentTimeMillis();

        Message msg = new Message(MessageCommand.BOSS_VIP_INFO_MESSAGE, request.getChannel());

        //BOSS之家层数
        byte layer = request.readByte();
        Map<Short, Boss> layerBossMap = BossService.getVipBoss().get(layer);
        //设置刷新倒计时
        msg.setInt((int) ((BossService.getVipBossTime() + DateUtil.MINUTE * 30 - curr) / DateUtil.SECOND));

        //BOSS列表
        msg.setShort(layerBossMap.size());
        List<Short> idList = new ArrayList<>(layerBossMap.keySet());
        Collections.sort(idList);

        for (Short id : idList) {
            Boss boss = layerBossMap.get(id);
            msg.setShort(boss.getId());
            long hp = boss.getHp();
            if (hp <= 0)
                msg.setByte(0);
            else
                msg.setByte(1);

            msg.setLong(hp);
        }

        role.sendMessage(msg);
    }

    /**
     * BOSS之家战斗开始
     *
     * @param request
     */
    public void processVipBossStart(Message request) {

        if (role.getCheatManager().requestFrequent(request)) {
            return;
        }
        byte layer = request.readByte();
        short id = request.readShort();

        Map<Short, Boss> layerBossMap = BossService.getVipBoss().get(layer);
        //已经在其他战斗中
        for (Boss boss : layerBossMap.values()) {
            if (boss.getId() == id)
                continue;
            if (boss.getBattlePlayers().containsKey(player.getId())) {
//				boss.removePlayer(player.getId());
                removeVipPlayer(boss, player.getId());
                break;
            }
        }
        Boss boss = layerBossMap.get(id);
        //BOSS已经死亡
        if (boss == null || boss.getHp() <= 0) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_BOSS_DEAD);
            return;
        }
        BossBattlePlayer bbp = boss.getBattlePlayer(player.getId());
        if (bbp == null) {
            bbp = new BossBattlePlayer(player);
        }
        //进入BOSS战时间
        if (boss.getRanks().size() == 0)
            boss.setCaller(bbp);
        boss.addRank(bbp);
        boss.getBattlePlayers().put(bbp.getId(), bbp);

        //发送消息
        Message msg = new Message(MessageCommand.BOSS_VIP_START_MESSAGE);
        msg.setShort(id);
        //BOSS剩余血量
        msg.setLong(boss.getHp());
        role.putMessageQueue(msg);
        //排行榜外形
        role.putMessageQueue(getBossVipAppearMsg(boss));
        //排行榜消息
        role.putMessageQueue(getVipBossTopMsg(boss));
        role.sendTick(request);
        //广播给其他战斗的玩家
        Message appearMsg = new Message(MessageCommand.BOSS_VIP_APPEAR_MESSAGE);
        appearMsg.setByte(1);
        appearMsg.setInt(player.getId());
        appearMsg.setString(player.getName());
        player.getAppearMessage(appearMsg);
        appearMsg.setByte(player.getHead());
        appearMsg.setLong(player.getFighting());
        for (BossBattlePlayer bp : boss.getBattlePlayers().values()) {
            if (bp.getId() != player.getId()) {
                GameRole gr = GameWorld.getPtr().getOnlineRole(bp.getId());
                if (gr != null)
                    gr.putMessageQueue(appearMsg);
            }
        }

    }

    private void removeVipPlayer(Boss boss, int playerId) {
        boolean isCaller = boss.isCaller(playerId);
        boss.removePlayer(playerId);

        Message quitMsg = new Message(MessageCommand.BOSS_VIP_QUIT_MESSAGE);
        quitMsg.setShort(boss.getId());
        quitMsg.setInt(playerId);
        GameRole quitRole = GameWorld.getPtr().getOnlineRole(playerId);
        if (quitRole != null) {
            quitRole.putMessageQueue(quitMsg);
        }

        // 广播
        if (!isCaller) {
            return;
        }
        for (BossBattlePlayer battlePlayer : boss.getBattlePlayers().values()) {
            GameRole gr = GameWorld.getPtr().getOnlineRole(battlePlayer.getId());
            if (gr == null)
                continue;
            gr.putMessageQueue(quitMsg);
        }
    }

    /**
     * BOSS之家战斗
     *
     * @param request
     */
    public void processVipBossFight(Message request) {

        byte layer = request.readByte();
        short id = request.readShort();
        int damage = request.readInt();

        if (damage < 0) {
            damage = 0;
        }

        Boss boss = BossService.getVipBoss().get(layer).get(id);
        Message msg = new Message(MessageCommand.BOSS_VIP_FIGHT_MESSAGE, request.getChannel());
        msg.setShort(id);
        //BOSS已经死亡
        if (boss == null || boss.getHp() <= 0) {
            msg.setLong(0);
            role.sendMessage(msg);
            return;
        }
        //未参战
        if (boss.getBattlePlayer(player.getId()) == null) {
            role.putErrorMessage(ErrorDefine.ERROR_BOSS_CITIZEN_START);
            msg.setLong(boss.getHp());
            role.sendMessage(msg);
            return;
        }
        //攻击BOSS
        BossService.atkVipBoss(player, boss, damage);
        //排行榜消息
        role.putMessageQueue(getVipBossTopMsg(boss));
        //发送消息
        msg.setLong(boss.getHp());
        role.sendMessage(msg);
    }

    /**
     * BOSS之家战斗退出
     *
     * @param request
     */
    public void processVipBossQuit(Message request) {

        byte layer = request.readByte();
        short id = request.readShort();
        Boss boss = BossService.getVipBoss().get(layer).get(id);
        if (boss == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        if (!boss.getBattlePlayers().containsKey(role.getPlayerId())) {
            role.sendTick(request);
            return;
        }
        removeMysteryPlayer(boss, role.getPlayerId());
        role.sendTick(request);

//			//退出的玩家是当前BOSS的归属者
//			if (boss.getCaller().getId() == player.getId()) {
//				resetCitizenCaller(boss, 0);
//				Message topMsg = getMysteryBossTopMsg(boss);
//				//广播给所有人
//				for (BossBattlePlayer fp : boss.getBattlePlayers().values()) {
//					GameRole gr = GameWorld.getPtr().getOnlineRole(fp.getId());
//					if (gr == null)
//						continue;
//					gr.putMessageQueue(topMsg);
//				}
//			}
//		Message msg = new Message(MessageCommand.BOSS_VIP_QUIT_MESSAGE, request.getChannel());
//		msg.setShort(id);
//		role.sendMessage(msg);
//
    }

    /**
     * BOSS之家战斗奖励
     *
     * @param request
     */
    public void processVipBossReward(Message request) {

        byte layer = request.readByte();
        short bossId = request.readShort();
        Message msg = new Message(MessageCommand.BOSS_VIP_REWARD_MESSAGE, request.getChannel());
        Boss boss = BossService.getVipBoss().get(layer).get(bossId);
        if (boss == null || boss.getHp() > 0) {
            msg.setByte(0);
            role.sendMessage(msg);
            return;
        }
        BossBattlePlayer bbp = boss.getBattlePlayer(role.getPlayerId());
        if (bbp == null || bbp.getDamage() == 0) {
            msg.setByte(2);
            role.sendMessage(msg);
            return;
        }
        if (boss.getRanks() == null || boss.getRanks().size() == 0) {
            msg.setByte(3);
            role.sendMessage(msg);
            return;
        }
        bbp.setDamage(0);
        BossMysteryData model = BossModel.getMysteryMap().get(boss.getId());
        //是否伤害第一
        int first = boss.getFirstRank(bbp.getId());
        //奖励数量
        int num = 0;
        if (bbp.getRein() <= 0) {
            if (bbp.getLevel() / 10 >= BossService.CIT_LV_EQUIP.length)
                num = BossService.CIT_LV_EQUIP[BossService.CIT_LV_EQUIP.length - 1];
            else
                num = BossService.CIT_LV_EQUIP[bbp.getLevel() / 10];
        } else {
            if (bbp.getRein() >= BossService.CIT_REIN_EQUIP.length)
                num = BossService.CIT_REIN_EQUIP[BossService.CIT_REIN_EQUIP.length - 1];
            else
                num = BossService.CIT_REIN_EQUIP[bbp.getRein()];
        }
        List<DropData> rewards = new ArrayList<>();
        rewards.addAll(model.getRewards());
        //使用哪个随机
        int[] rates = null;
        if (first == GameCommon.True) {
            if (bbp.getRein() >= 3)
                rates = BossService.CIT_FIRST_HIG;
            else
                rates = BossService.CIT_FIRST_LOW;
        } else {
            if (bbp.getRein() >= 3)
                rates = BossService.CIT_COMMON_HIG;
            else
                rates = BossService.CIT_COMMON_LOW;
        }
        FighterData fd = FighterModel.getFighterDataById(model.getModelId());
        //随机装备
        for (int i = 0; i < num; i++) {
            int random = GameUtil.getRangedRandom(1, 100);
            int quality = GameUtil.getRatesIndex(rates, random);
            EquipData equipData = GoodsModel.getRandomDataByLv(fd.getLevel());
            if (equipData == null)
                continue;
            DropData drop = new DropData(EGoodsType.EQUIP.getId(), equipData.getGoodsId(),
                    (byte) quality, 1);
            rewards.add(drop);
        }
        //消息
        msg.setByte(1);
        msg.setByte(first);
        msg.setByte(rewards.size());
        for (DropData data : rewards) {
            msg.setByte(data.getT());
            msg.setShort(data.getG());
            msg.setByte(data.getQ());
            msg.setInt(data.getN());
        }
        role.sendMessage(msg);
        //增加物品
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        role.getPackManager().addGoods(rewards, EGoodsChangeType.VIP_BOSS_ADD, saves);
        role.savePlayer(saves);

    }


    /**
     * BOSS之家提醒设置
     *
     * @param request
     */
    public void processVipBossCue(Message request) {

        short size = request.readShort();

        if (player.getVipBossCue() == null)
            player.setVipBossCue(new ArrayList<Short>());

        player.getVipBossCue().clear();

        for (int i = 0; i < size; i++) {
            player.getVipBossCue().add(request.readShort());
        }

        EnumSet<EPlayerSaveType> saves = EnumSet.of(EPlayerSaveType.VIPBOSSCUE);
        role.savePlayer(saves);

    }

    public Message getVipBossMsg() {

        Message msg = new Message(MessageCommand.BOSS_VIP_CUE_MESSAGE);
        //所有的BOSS
        if (player.getVipBossCue() == null) {

            Set<Byte> layers = BossModel.getVipBossMap().keySet();

            short bossCount = 0;

            for (byte layer : layers) {
                bossCount += BossModel.getVipBossMap().get(layer).size();
            }
            msg.setShort(bossCount);

            for (byte layer : layers) {
                for (short id : BossModel.getVipBossMap().get(layer).keySet()) {
                    msg.setShort(id);
                }
            }
        } else {

            msg.setShort(player.getVipBossCue().size());

            for (short id : player.getVipBossCue()) {
                msg.setShort(id);
            }
        }
        return msg;
    }

    /**
     * BOSS之家争夺归属权
     *
     * @param request
     */
    public void processVipBossPK(Message request) {

        byte layer = request.readByte();
        short bossId = request.readShort();

        Boss boss = BossService.getVipBoss().get(layer).get(bossId);
        if (boss == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        BossBattlePlayer caller = boss.getCaller();
        if (caller == null || caller.getId() == player.getId()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        long curr = System.currentTimeMillis();
        BossBattlePlayer self = boss.getBattlePlayer(player.getId());

        if (self.getDeadTime() + PLAYER_REVIVE_TIME > curr) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        if (boss.getPkTime() + PVP_BATTLE_TIME > curr) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_BE_ATTACK);
            return;
        }

        int[] state = boss.fightForBelonging(player);

        if (state[0] != ErrorDefine.ERROR_NONE) {
            role.sendErrorTipMessage(request, (short) state[0]);
            return;
        }

        Message deadMsg = new Message(MessageCommand.BOSS_VIP_PK_MESSAGE);
        deadMsg.setInt(state[1]);
        deadMsg.setInt(state[2]);
        Message topMsg = getVipBossTopMsg(boss);
        //广播给所有人
        for (BossBattlePlayer fp : boss.getBattlePlayers().values()) {
            GameRole gr = GameWorld.getPtr().getOnlineRole(fp.getId());
            if (gr == null)
                continue;
            gr.putMessageQueue(topMsg);
            gr.putMessageQueue(deadMsg);
        }
        role.sendTick(request);

    }

    /**
     * BOSS之家玩家复活
     *
     * @param request
     */
    public void processVipBossRevive(Message request) {

        byte layer = request.readByte();
        short bossId = request.readShort();
        Boss boss = BossService.getVipBoss().get(layer).get(bossId);
        if (boss == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        BossBattlePlayer self = boss.getBattlePlayer(player.getId());
        if (self == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        long curr = System.currentTimeMillis();
        Message msg = new Message(MessageCommand.BOSS_VIP_PLAYER_REVIVE_MESSAGE, request.getChannel());
        if (self.getDeadTime() + PLAYER_REVIVE_TIME <= curr) {
            role.sendMessage(msg);
            return;
        }
        //花钱复活
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        //消耗
        DropData cost = new DropData(EGoodsType.DIAMOND, 0, 200);
        if (!role.getPackManager().useGoods(cost, EGoodsChangeType.BOSS_REVIVE_CONSUME, saves)) {
            role.putErrorMessage(ErrorDefine.ERROR_DIAMOND_LESS);
            return;
        }
        self.setDeadTime(0);

        role.sendMessage(msg);
        role.savePlayer(saves);
    }

    /**
     * BOSS之家外形数据
     *
     * @param boss
     * @return
     */
    private Message getBossVipAppearMsg(Boss boss) {

        List<BossBattlePlayer> list = new ArrayList<>();
        BossBattlePlayer caller = boss.getCaller();
        if (caller != null) {
            caller = boss.getBattlePlayer(caller.getId());
            list.add(caller);
            for (BossBattlePlayer bbp : boss.getRanks().values()) {
                if (bbp != null && bbp.getId() != caller.getId())
                    list.add(bbp);
            }
        }
        Message appearMsg = new Message(MessageCommand.BOSS_VIP_APPEAR_MESSAGE);
        appearMsg.setByte(list.size());
        for (BossBattlePlayer bbp : list) {
            GameRole gr = GameWorld.getPtr().getOnlineRole(bbp.getId());
            Player player = (gr == null) ? GameWorld.getPtr().getOfflinePlayer(bbp.getId()) : gr.getPlayer();
            if (player != null) {
                appearMsg.setInt(player.getId());
                appearMsg.setString(player.getName());
                player.getAppearMessage(appearMsg);
                appearMsg.setByte(player.getHead());
                appearMsg.setLong(player.getFighting());
            } else {
                appearMsg.setInt(1);
                appearMsg.setString("");
                AppearPlayer appear = new AppearPlayer();
                appear.getMessage(appearMsg);
                appearMsg.setByte(1);
                appearMsg.setLong(1);
            }
        }
        return appearMsg;
    }

    /**
     * BOSS之家排行榜
     *
     * @param boss
     * @return
     */
    public Message getVipBossTopMsg(Boss boss) {
        long damage = 0;
        BossBattlePlayer self = boss.getBattlePlayer(player.getId());
        if (self != null)
            damage = self.getDamage();
        Message msg = new Message(MessageCommand.BOSS_VIP_TOP_MESSAGE);
        msg.setInt((int) damage);
        List<BossBattlePlayer> list = new ArrayList<>();
        BossBattlePlayer caller = boss.getCaller();
        if (caller != null) {
            caller = boss.getBattlePlayer(caller.getId());
            list.add(caller);
            for (BossBattlePlayer bbp : boss.getRanks().values()) {
                if (bbp != null && bbp.getId() != caller.getId())
                    list.add(bbp);
            }
        }
        msg.setShort(list.size());
        for (BossBattlePlayer bbp : list) {
            msg.setInt(bbp.getId());
            msg.setString(bbp.getName());
            msg.setInt((int) bbp.getDamage());
            msg.setInt((int) bbp.getFighting());
        }
        return msg;
    }

}
