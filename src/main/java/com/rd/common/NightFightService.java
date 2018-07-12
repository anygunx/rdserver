package com.rd.common;

import com.rd.bean.drop.DropData;
import com.rd.bean.mail.Mail;
import com.rd.bean.nightFight.NightFighter;
import com.rd.bean.player.Player;
import com.rd.define.EGoodsChangeType;
import com.rd.define.NightFightDefine;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.model.NightFightModel;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import com.rd.task.ETaskType;
import com.rd.task.Task;
import com.rd.task.TaskManager;
import com.rd.util.DateUtil;
import org.apache.commons.lang3.RandomUtils;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * @author ---
 * @version 1.0
 */
public class NightFightService {

    private static Logger logger = Logger.getLogger(NightFightService.class);

    private static NightFightService instance = new NightFightService();

    public static NightFightService getInstance() {
        return instance;
    }

    private List<NightFighter> fighterList = new ArrayList<NightFighter>();

    private Map<Integer, Long> exitMap = new HashMap<Integer, Long>();

    private volatile boolean isInit = false;
    private volatile String rewardDate = "";

    public void setInit(boolean isInit) {
        this.isInit = isInit;
    }

    public void setRewardDate(String rewardDate) {
        this.rewardDate = rewardDate;
    }

    public List<NightFighter> getFighterList() {
        return fighterList;
    }

    public Map<Integer, Long> getExitMap() {
        return exitMap;
    }

    public NightFightService() {

    }

    public static Task rewardTask = new Task() {
        @Override
        public void run() {
            try {
                List<NightFighter> list = new ArrayList<>();
                for (NightFighter fighter : NightFightService.getInstance().fighterList) {
                    list.add(fighter);
                }
                Collections.sort(list, new SortByPoint());

                short rank;
                List<DropData> rankData = NightFightModel.getRankReward(NightFightModel.getMaxRank());
                for (int i = 0; i < list.size(); ++i) {
                    rank = (short) (i + 1);

                    NightFighter figter = list.get(i);
                    figter.setRank(rank);

                    Mail rewardMail;
                    if (rank < NightFightModel.getMaxRank()) {
                        List<DropData> rewardData = NightFightModel.getRankReward((byte) rank);
                        rewardMail = MailService.createMail("恭喜获得夜战比奇第" + rank + "名奖励", "恭喜您在专属PVP群战活动：夜战比奇中，力压群雄，战到最后，获得了第" + rank + "名殊荣！我们为您准备了海量的奖励，助您再创辉煌！", EGoodsChangeType.NIGHT_FIGHT_REWARD_ADD, rewardData);
                    } else {
                        rewardMail = MailService.createMail("恭喜获得夜战比奇第" + rank + "名奖励", "恭喜您在专属PVP群战活动：夜战比奇中，力压群雄，战到最后，获得了第" + rank + "名殊荣！我们为您准备了海量的奖励，助您再创辉煌！", EGoodsChangeType.NIGHT_FIGHT_REWARD_ADD, rankData);
                    }
                    MailService.sendSystemMail(figter.getPlayer().getId(), rewardMail);
                }

                for (NightFighter fighter : NightFightService.getInstance().fighterList) {
                    Message message = new Message(MessageCommand.NIGHT_FIGHT_REWARD_MESSAGE);
                    message.setShort(fighter.getRank());
                    message.setShort(fighter.getPoint());
                    message.setByte(NightFightDefine.RANK_BALANCE_SHOW < list.size() ? NightFightDefine.RANK_BALANCE_SHOW : list.size());
                    for (int i = 0; i < list.size(); ++i) {
                        if (i >= NightFightDefine.RANK_BALANCE_SHOW) {
                            break;
                        }
                        message.setString(list.get(i).getPlayer().getName());
                        message.setString(list.get(i).getPlayer().getGang() == null ? "" : list.get(i).getPlayer().getGang().getName());
                        message.setShort(list.get(i).getPoint());
                    }
                    GameRole gr = GameWorld.getPtr().getOnlineRole(fighter.getPlayer().getId());
                    if (gr != null) {
                        gr.putMessageQueue(message);
                    }
                }
            } catch (Exception e) {
                logger.error("夜战比奇发放奖励时发生异常", e);
            } finally {
                NightFightService.getInstance().setRewardDate(DateUtil.formatDay(System.currentTimeMillis()));
                NightFightService.getInstance().setInit(false);
            }
        }

        @Override
        public String name() {
            return "rewardTask";
        }

        class SortByPoint implements Comparator<NightFighter> {

            public SortByPoint() {
            }

            public int compare(NightFighter fighter1, NightFighter fighter2) {
                return Integer.valueOf(fighter2.getPoint()).compareTo(Integer.valueOf(fighter1.getPoint()));
            }
        }
    };

    public static Task campTask = new Task() {
        @Override
        public void run() {
            try {
                for (int i = 0; i < NightFightService.getInstance().fighterList.size(); ++i) {
                    NightFightService.getInstance().fighterList.get(i).setCamp(i);
                }

                Message message = new Message(MessageCommand.NIGHT_FIGHT_TARGET_MESSAGE);
                message.setShort(NightFightService.getInstance().fighterList.size());
                for (NightFighter temp : NightFightService.getInstance().fighterList) {
                    message.setInt(temp.getPlayer().getId());
                    message.setByte(temp.getCamp());
                }
                for (NightFighter fighter : NightFightService.getInstance().fighterList) {
                    GameRole gr = GameWorld.getPtr().getOnlineRole(fighter.getPlayer().getId());
                    if (gr != null) {
                        gr.putMessageQueue(message);
                    }
                }

                if (NightFightService.getEndDownTime() > DateUtil.MINUTE * 5) {
                    TaskManager.getInstance().scheduleDelayTask(ETaskType.COMMON, campTask, DateUtil.MINUTE * 5);
                }
            } catch (Exception e) {
                logger.error("夜战比奇更新阵营时发生异常", e);
            }
        }

        @Override
        public String name() {
            return "campTask";
        }
    };

    public static Task reviveTask = new Task() {
        @Override
        public void run() {
            try {
                while (true) {
                    if (NightFightService.getEndDownTime() < 1000) {
                        break;
                    }
                    Thread.sleep(1000);
                    long currTime = System.currentTimeMillis();
                    for (NightFighter fighter : NightFightService.getInstance().fighterList) {
                        if (fighter.getDieTime() > 0 && fighter.getDieTime() + NightFightDefine.RELIVE_TIME < currTime) {
                            fighter.setDieTime(0);
                            Message message = getNightFightIntoMessage(fighter);
                            for (NightFighter nightFighter : NightFightService.getInstance().fighterList) {
                                GameRole gr = GameWorld.getPtr().getOnlineRole(nightFighter.getPlayer().getId());
                                if (gr != null) {
                                    gr.putMessageQueue(message);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("夜战比奇玩家复活时发生异常", e);
            }
        }

        @Override
        public String name() {
            return "reviveTask";
        }
    };

    public NightFighter getNightFighter(int playerId) {
        Iterator<NightFighter> iterator = this.fighterList.iterator();
        while (iterator.hasNext()) {
            NightFighter fighter = iterator.next();
            if (fighter.getPlayer().getId() == playerId) {
                return fighter;
            }
        }
        return null;
    }

    /**
     * 随机得到攻击目标
     *
     * @param playerId
     * @return
     */
    public Set<Integer> getRandomTarget(int playerId) {
        List<Player> target = new ArrayList<Player>();
        for (NightFighter nightFighter : this.fighterList) {
            if (nightFighter.isLive() && nightFighter.getPlayer().getId() != playerId) {
                target.add(nightFighter.getPlayer());
            }
        }
        int size = target.size();
        int targetNum = size < NightFightDefine.TARGET_NUM ? size : NightFightDefine.TARGET_NUM;

        if (targetNum == 0) {
            return null;
        }
        Set<Integer> set = new HashSet<Integer>();
        while (true) {
            set.add(RandomUtils.nextInt(0, size));
            if (set.size() == targetNum) {
                return set;
            }
        }
    }

    /**
     * 添加入场并广播
     *
     * @param player
     */
    public synchronized void addNightFighter(Player player) {
        if (!this.isInit) {
            String day = DateUtil.formatDay(System.currentTimeMillis());
            if (!rewardDate.equals(day)) {
                this.isInit = true;
                fighterList = new ArrayList<NightFighter>();
                exitMap = new HashMap<Integer, Long>();
                TaskManager.getInstance().scheduleDelayTask(ETaskType.COMMON, rewardTask, NightFightService.getEndDownTime() + 1000);
                TaskManager.getInstance().scheduleDelayTask(ETaskType.COMMON, campTask, DateUtil.MINUTE * 5);
                TaskManager.getInstance().scheduleDelayTask(ETaskType.COMMON, reviveTask, 1000);
            }
        }

        for (NightFighter nightFighter : this.fighterList) {
            if (nightFighter.getPlayer().getId() == player.getId()) {
                return;
            }
        }

        NightFighter fighter = new NightFighter(player, this.fighterList.size());
        this.fighterList.add(fighter);
        this.sortRank(false);

        //发送入场广播
        broadcastInto(fighter);
    }

    public void broadcastInto(NightFighter fighter) {
        //发送入场广播
        Message message = getNightFightIntoMessage(fighter);
        for (NightFighter nightFighter : this.fighterList) {
            GameRole gr = GameWorld.getPtr().getOnlineRole(nightFighter.getPlayer().getId());
            if (gr != null) {
                gr.putMessageQueue(message);
            }
        }
    }

    public void dieNightFighter(int fightId) {
        for (NightFighter nightFighter : this.fighterList) {
            if (nightFighter.getPlayer().getId() == fightId) {
                nightFighter.setDieTime(System.currentTimeMillis());
                break;
            }
        }
    }

    public void exitNightFighter(Player player) {
        for (NightFighter nightFighter : this.fighterList) {
            if (nightFighter.getPlayer().getId() == player.getId()) {
                this.exitMap.put(nightFighter.getPlayer().getId(), System.currentTimeMillis());
                this.fighterList.remove(nightFighter);
                break;
            }
        }
        //发送出场广播
        Message message = new Message(MessageCommand.NIGHT_FIGHT_BROADCAST_EXIT_MESSAGE);
        message.setInt(player.getId());
        for (NightFighter nightFighter : this.fighterList) {
            GameRole gr = GameWorld.getPtr().getOnlineRole(nightFighter.getPlayer().getId());
            if (gr != null)
                gr.putMessageQueue(message);
        }
    }

    public void sortRank(boolean isBroadcast) {
        Collections.sort(this.fighterList, new SortByPoint());

        if (isBroadcast) {
            Message message = new Message(MessageCommand.NIGHT_FIGHT_RANK_MESSAGE);
            message.setShort(NightFightService.getInstance().getFighterList().size());
            for (NightFighter temp : NightFightService.getInstance().getFighterList()) {
                message.setInt(temp.getPlayer().getId());
                message.setShort(temp.getPoint());
            }
            for (NightFighter nightFighter : this.fighterList) {
                GameRole gr = GameWorld.getPtr().getOnlineRole(nightFighter.getPlayer().getId());
                if (gr != null)
                    gr.putMessageQueue(message);
            }
        }
    }

    class SortByPoint implements Comparator<NightFighter> {

        public SortByPoint() {
        }

        public int compare(NightFighter fighter1, NightFighter fighter2) {
            return Integer.valueOf(fighter2.getPoint()).compareTo(Integer.valueOf(fighter1.getPoint()));
        }
    }

    public static long getEndDownTime() {
        long currTime = System.currentTimeMillis();
        long endTime = DateUtil.parseDataTime(DateUtil.formatDay(currTime) + " " + NightFightDefine.END_TIME).getTime();
        long time = endTime - currTime;
        return time > 0 ? time : 0;
    }

    private static Message getNightFightIntoMessage(NightFighter fighter) {
        Message message = new Message(MessageCommand.NIGHT_FIGHT_BROADCAST_INTO_MESSAGE);
        fighter.getPlayer().getNightFightAppearMessage(message);
        message.setByte(fighter.getCamp());
        message.setByte(fighter.getProtectedTime());
        message.setString(fighter.getPlayer().getGangName());
        return message;
    }
}

