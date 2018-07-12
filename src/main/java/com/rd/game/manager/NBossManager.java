package com.rd.game.manager;

import com.rd.bean.boss.Boss;
import com.rd.bean.boss.BossBattlePlayer;
import com.rd.bean.boss.NBoss;
import com.rd.bean.drop.DropData;
import com.rd.bean.player.Player;
import com.rd.common.BossService;
import com.rd.common.NBossService;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.define.ErrorDefine;
import com.rd.enumeration.EMessage;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.model.NBossModel;
import com.rd.model.data.copy.quanmin.QianMinBossData;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import com.rd.task.ETaskType;
import com.rd.task.Task;
import com.rd.task.TaskManager;
import com.rd.util.DateUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Map;

public class NBossManager {
    private static final Logger logger = Logger.getLogger(NBossManager.class);

    //全民BOSS参战次数上限
    public static final short CIT_BOSS_FIGHT_MAX = 10;

    //全民BOSS次数恢复时间
    private static final long CIT_BOSS_FIGHT_RECOVE = DateUtil.HOUR;

    private GameRole role;
    private Player player;

    public NBossManager(GameRole role) {
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
        Message msg = new Message(EMessage.BOSS_QUANMIN_PANEL.CMD(), request.getChannel());
        //剩余多少次战斗次数
        short count = player.getCitBossLeft();
        byte buyCnt = player.getQmcount();
        byte total = (byte) (count + buyCnt);
        msg.setByte(total);
        //距离下次恢复多少秒
        if (count >= CIT_BOSS_FIGHT_MAX) {
            msg.setInt(0);
        } else {
            msg.setInt((int) ((player.getCitRecover() + CIT_BOSS_FIGHT_RECOVE - curr) / DateUtil.SECOND));
        }
        //BOSS列表
        msg.setByte(NBossService.getCitizenBoss().size());
        for (NBoss boss : NBossService.getCitizenBoss().values()) {
            QianMinBossData model = NBossModel.getQianMinBossDataMap().get((byte) boss.getId());
            msg.setByte(boss.getId());
            long hp = boss.getHp();

            msg.setInt((int) hp);
            if (hp <= 0) {
                msg.setInt((int) ((boss.getDeadTime() + model.getRebirthtime() * DateUtil.SECOND - curr) / DateUtil.SECOND));

            } else {
                msg.setInt(0);
            }
            if (hp <= 0) {
                msg.setByte(0);
            } else {
                msg.setByte(boss.getRanks().size());
            }
        }
        byte buycount = player.getQmcount();
        msg.setByte(buycount);
        role.sendMessage(msg);
    }

    /**
     * 全民boss
     *
     * @param request
     */
    public boolean processCitizenStart(byte type, int id, Message request) {
        if (role.getCheatManager().requestFrequent(request)) {
            return false;
        }

        //已经在其他战斗中
        for (Boss boss : BossService.getCitizenBoss().values()) {
            if (boss.getId() == id)
                continue;
            if (boss.getBattlePlayers().containsKey(player.getId())) {
                removeCitizenPlayer(boss, player.getId());
                break;
            }
        }
        NBoss boss = NBossService.getCitizenBoss().get(id);
        //BOSS已经死亡
        if (boss == null || boss.getHp() <= 0) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_BOSS_DEAD);
            return false;
        }
        refreshCizBossCount();
        if (player.getCitBossLeft() <= 0) {
            if (player.getQmcount() <= 0) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_BOSS_FIGHT_MAX);
                return false;
            }
            player.addQmcount((byte) -1);
        } else {
            player.changeCitBossLeft(-1);
        }


        BossBattlePlayer bbp = boss.getBattlePlayer(player.getId());
        if (bbp == null) {
            bbp = new BossBattlePlayer(player);
        }

        Message msg = new Message(EMessage.COPY_REQUITE_FIGHT.CMD(), request.getChannel());
        msg.setByte(type);
        msg.setByte(id);
        msg.setBool(true);
        boolean fightReult = false;
        QianMinBossData qm = NBossModel.getQianMinBossDataMap().get(id);
        fightReult = boss.fight(bbp, request, player, qm, msg);

//			synchronized(NBossManager.class) {
//				int oldHp=(int)boss.getHp();
//				int  hp=CombatSystem.pveDungeonN(msg, player, qm.getBossid(),qm.getMonsterids(),oldHp, CombatDef.ROUND_FIVE);
//				// fightReult=boss.changeCitHp(bbp,hp);
//				fightReult=boss.changeCitHp2(bbp,hp,request);
//				bbp.addDamage(oldHp-hp);
//			}

        boss.addRankSync(bbp);
        boss.getBattlePlayers().put(bbp.getId(), bbp);

        //保存数据
        EnumSet<EPlayerSaveType> saves = EnumSet.of(EPlayerSaveType.CITBOSSLEFT, EPlayerSaveType.CITRECOVE);
        role.sendMessage(msg);
        updateBossMsg(request, boss);
        role.savePlayer(saves);
        return fightReult;

    }

    /**
     * 更新
     */
    private void updateBossMsg(Message request, NBoss boss) {
        long curr = System.currentTimeMillis();
        TaskManager.getInstance().scheduleDelayTask(ETaskType.COMMON, new Task() {
            @Override
            public void run() {
                for (GameRole role : GameWorld.getPtr().getOnlineRoles().values()) {
                    if (role.getPlayerId() == player.getId()) {
                        continue;
                    }
                    if (!role.isOnline()) {
                        continue;
                    }

                    Message msg = new Message(EMessage.BOSS_QUANMIN_UPDATE_HP.CMD());
                    QianMinBossData model = NBossModel.getQianMinBossDataMap().get((byte) boss.getId());

                    msg.setByte(boss.getId());
                    long hp = boss.getHp();

                    msg.setInt((int) hp);
                    if (hp <= 0) {
                        msg.setInt((int) ((boss.getDeadTime() + model.getRebirthtime() * DateUtil.SECOND - curr) / DateUtil.SECOND));

                    } else {
                        msg.setInt(0);
                    }
                    if (hp <= 0) {
                        msg.setByte(0);
                    } else {
                        msg.setByte(boss.getRanks().size());
                    }
                    role.putMessageQueue(msg);
                    //role.sendMessage(msg);
                }

            }

            @Override
            public String name() {
                return "bossupdatemsg";
            }
        }, 0);

    }

    /***
     * 已经结束战斗击杀了
     */
    public void resultFinish(boolean isDead) {
        if (!isDead) {
            return;
        }
        TaskManager.getInstance().scheduleDelayTask(ETaskType.COMMON, NBossService.citBossRewardTask,
                10);
    }


    /**
     * 查看某个全民boss 的争夺 或者击杀记录
     */
    public void processBossRank(Message request) {
        byte id = request.readByte();
        byte type = request.readByte();
        QianMinBossData data = NBossModel.getQianMinBossDataMap().get(id);
        if (data == null) {
            return;
        }
        NBoss boss = NBossService.getCitizenBoss().get(id);
        if (boss == null) {
            return;
        }
        Map<Integer, BossBattlePlayer> map = boss.getRanks();
        Message msg = new Message(EMessage.BOSS_QUANMIN_RANK.CMD(), request.getChannel());
        msg.setByte(id);
        msg.setByte(type);
        msg.setByte(map.size());
        int rank = 0;
        for (BossBattlePlayer bt : map.values()) {
            if (type == 1) {
                msg.setByte(++rank);
            } else {
                msg.setLong(bt.getDeadBossTime());
            }
            msg.setString(bt.getName());
            if (type == 1) {
                msg.setInt((int) bt.getDamage());
            } else {
                msg.setInt((int) bt.getFighting());
            }
        }
        role.sendMessage(msg);
    }

    /***
     *
     * 购买次数
     */
    public void processBuyCont(Message request) {
        if (player.getCitBossLeft() > 0) {
            return;
        }
        byte count = player.getQmBossBuyCount();
        if (count > getBuyCount()) {
            return;
        }
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        DropData cost = new DropData(EGoodsType.DIAMOND, 0, 50);
        if (!role.getPackManager().useGoods(cost, EGoodsChangeType.SKILL_UP_CONSUME, enumSet, false)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_NOT_ENOUGHT);
            return;
        }
        Message msg = new Message(EMessage.BOSS_QUANMIN_BUY_COUNT.CMD(), request.getChannel());
        msg.setByte(getBuyCount() - count);
        role.sendMessage(msg);
        player.addQmBossBuyCount((byte) 1);
        player.addQmcount((byte) 3);
        role.savePlayer(enumSet);
    }


    public byte getBuyCount() {
        if (role.getPlayer().getVip() >= 6 && role.getPlayer().getVip() < 9) {
            return 2;
        } else if (role.getPlayer().getVip() >= 9 && role.getPlayer().getVip() < 11) {
            return 3;
        } else if (role.getPlayer().getVip() >= 11) {
            return 4;
        }
        return 0;
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
    public void processBossFuHuoTiXiangSet(Message request) {
        short size = request.readShort();
        if (player.getCitCue() == null)
            player.setCitCue(new ArrayList<Short>());
        player.getCitCue().clear();
        Message msg = new Message(EMessage.BOSS_QUANMIN_TIXING.CMD(), request.getChannel());
        msg.setShort(size);
        for (int i = 0; i < size; i++) {
            short id = request.readShort();
            player.getCitCue().add(request.readShort());
            msg.setShort(id);
        }

        role.sendMessage(msg);
        EnumSet<EPlayerSaveType> saves = EnumSet.of(EPlayerSaveType.CITCUE);
        role.savePlayer(saves);
    }


    /**
     * 花钱复活boss
     */
    public void processBossFuHuo(Message request) {
        byte bossId = request.readByte();
        if (player.getVip() < 6) {
            role.putErrorMessage(ErrorDefine.ERROR_VIP_LEVEL_LESS);
            return;
        }
        NBoss boss = NBossService.getCitizenBoss().get(bossId);
        if (boss == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        if (boss.getHp() > 0) {
            return;
        }
        BossBattlePlayer self = boss.getBattlePlayer(player.getId());
//	if (self == null) {
//		role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//		return;
//	}
        long curr = System.currentTimeMillis();
        Message msg = new Message(EMessage.BOSS_QUANMIN_FUHUO.CMD(), request.getChannel());
//	if (self.getDeadTime() + PLAYER_REVIVE_TIME <= curr) {
//		msg.setByte(bossId);
//		role.sendMessage(msg);
//		return;
//	}
        //花钱复活
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        QianMinBossData qm = NBossModel.getQianMinBossDataMap().get(bossId);
        if (qm == null) {
            return;
        }

        //消耗

        if (!role.getPackManager().useGoods(qm.getRebirthcost(), EGoodsChangeType.BOSS_REVIVE_CONSUME, saves)) {
            if (!role.getPackManager().useGoods(qm.getRebirthitem(), EGoodsChangeType.BOSS_REVIVE_CONSUME, saves)) {
                role.putErrorMessage(ErrorDefine.ERROR_DIAMOND_LESS);
                return;
            }
        }

        boss.fuhuo(request);
        //NBossService.fuhuo(boss,request);
        msg.setByte(bossId);
        role.sendMessage(msg);
        role.savePlayer(saves);
    }


}
