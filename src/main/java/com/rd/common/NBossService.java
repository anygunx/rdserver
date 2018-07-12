package com.rd.common;

import com.rd.bean.boss.BossBattlePlayer;
import com.rd.bean.boss.NBoss;
import com.rd.bean.mail.Mail;
import com.rd.define.EGoodsChangeType;
import com.rd.enumeration.EAttr;
import com.rd.game.manager.NshopManager;
import com.rd.model.CombatModel;
import com.rd.model.NBossModel;
import com.rd.model.data.MonsterData;
import com.rd.model.data.copy.quanmin.QianMinBossData;
import com.rd.task.Task;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class NBossService {

    public static final int RANK_CAPACITY = 5;
    private static Logger logger = Logger.getLogger(NshopManager.class);
    ////////////////////////// 全民BOSS//////////////////////////
    private static Map<Integer, NBoss> citizenBoss = new ConcurrentHashMap<>();

    public static void init() {
        initCitizenBoss();
    }

    /**
     * 初始化全民BOSS
     */
    private static void initCitizenBoss() {
        // 根据表进行初始化
        for (QianMinBossData data : NBossModel.getQianMinBossDataMap().values()) {
            MonsterData fd = CombatModel.getMonsterData(data.getBossId());
            if (fd == null)
                continue;
            NBoss boss = new NBoss();
            boss.setUuid(data.getBossId());
            boss.setId(data.getId());
            boss.initHp(fd.getAttr()[EAttr.HP.ordinal()]);
            boss.setStartTime(System.currentTimeMillis());
            citizenBoss.put(boss.getId(), boss);
        }
        // 20171213 改为触发式updateCaller 不在其他任务里进行。
        // 因为客户端切后台等原因导致战斗5s的检测不合理，去掉了超时战斗的检查。
        // 可能导致ranks里包含已经掉线的玩家，在给客户端下发的数据中使用默认的数据。
        // 介于当前设计中Player的shadow存在服务器10min，极少情况造成影响，最坏情况是掉线玩家数据变成弱鸡。

        // 全民BOSS归属者
//		TaskManager.getInstance().schedulePeriodicTask(ETaskType.COMMON, citBossCallerTask, 1000, 2000);
    }

    public static Map<Integer, NBoss> getCitizenBoss() {
        return citizenBoss;
    }

    // 全民BOSS复活锁
    private static ReentrantLock citReviveLock = new ReentrantLock();
    // 全民BOSS奖励锁
    private static ReentrantLock citRewardLock = new ReentrantLock();

    // 全民BOSS复活的任务
    public static Task citBossReviveTask = new Task() {
        @Override
        public void run() {
            try {
                // FIXME = =和gm同步？
                citReviveLock.lock();
                long curr = System.currentTimeMillis();
                for (NBoss boss : citizenBoss.values()) {
                    if (boss.getHpMax() == 0) {
                        //BossCitData model = BossModel.getCitMap().get(boss.getId());
                        QianMinBossData model = NBossModel.getQianMinBossDataMap().get(boss.getId());
                        // BOSS复活时间
                        if (curr - boss.getDeadTime() >= model.getRebirthtime() * 1000) {
                            fuhuo(boss);
                        }
                    }
                }
            } catch (Exception e) {
                //logger.error("复活全民BOSS时发生异常", e);
            } finally {
                citReviveLock.unlock();
            }
        }

        @Override
        public String name() {
            return "CITBOSSREVIVETASK";
        }
    };


    public static void fuhuo(NBoss boss) {
        MonsterData fd = CombatModel.getMonsterData(boss.getId());
        if (fd != null) {
            boss.initHp(fd.getAttr()[EAttr.HP.ordinal()]);
            boss.setStartTime(System.currentTimeMillis());
//			boss.setReward(false);
            boss.getBattlePlayers().clear();
            boss.clearRanks();
            boss.setDamageMin(0);
            boss.setCaller(null);
            boss.setKiller(null);
            boss.setDeadTime(0);
        }

    }


    public static Task citBossRewardTask = new Task() {
        @Override
        public void run() {
            try {
                // FIXME = =和gm同步？
                citRewardLock.lock();
                for (NBoss boss : citizenBoss.values()) {
                    if (boss.getHp() > 0) {
                        continue;
                    }
                    if (boss.isReward()) {
                        continue;
                    }
                    QianMinBossData model = NBossModel.getQianMinBossDataMap().get((byte) boss.getId());
                    boss.setReward(true);
                    List<BossBattlePlayer> temp = new ArrayList<>();
                    for (BossBattlePlayer bbp : boss.getBattlePlayers().values()) {
                        temp.add(bbp);
                    }
                    Collections.sort(temp);
                    String title = "";
                    String contText = "";
                    for (int i = 0; i < temp.size(); i++) {
                        BossBattlePlayer bbp = temp.get(i);
                        if (i == 0) {
                            title = "测试伤害最多的";
                            contText = "我是伤害最多的玩家";
                        } else {
                            title = "测试全民boss参与奖励";
                            contText = "测试全民boss就是吊炸天";
                        }
                        Mail mail = MailService.createMail(title, contText, EGoodsChangeType.BOSS_REIN_RANK_ADD, model.getReward());
                        MailService.sendSystemMail(bbp.getId(), mail);
                    }
                    boss.getBattlePlayers().clear();

                }
            } catch (Exception e) {
                //logger.error("复活全民BOSS时发生异常", e);
            } finally {
                citRewardLock.unlock();
            }
        }

        @Override
        public String name() {
            return "QUANMINBOSSREWARDTASK";
        }
    };
}
