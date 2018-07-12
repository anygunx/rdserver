package com.rd.common;

import com.alibaba.fastjson.JSON;
import com.rd.bean.drop.DropData;
import com.rd.bean.gang.Gang;
import com.rd.bean.gang.GangMember;
import com.rd.bean.gangstarcraft.*;
import com.rd.bean.mail.Mail;
import com.rd.bean.player.Player;
import com.rd.dao.GangStarcraftDao;
import com.rd.define.EGoodsChangeType;
import com.rd.define.GameDefine;
import com.rd.define.GangDefine;
import com.rd.game.GameGangManager;
import com.rd.game.GameWorld;
import com.rd.game.IGameRole;
import com.rd.model.BattleModel;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import com.rd.task.ETaskType;
import com.rd.task.Task;
import com.rd.task.TaskManager;
import com.rd.util.DateUtil;
import com.rd.util.StringUtil;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 公会战-沙城争霸
 *
 * @author ---
 * @version 1.0
 * @date 2017年12月27日下午6:49:38
 */
public class GangService {
    private static Logger logger = Logger.getLogger(GangService.class);

    //上届霸主公会
    private Gang gangOverlord;
    //上届霸主会长
    private Player playerPresident;
    //战斗日期
    private String fightDay;
    //公会排行记录
    private List<GangStarcraftRank> gangRankList = new ArrayList<>();
    //会员排名记录
    private List<GangStarcraftMemberRank> memberRankList = new ArrayList<>();
    //开启几月
    private byte month;
    //开启几日
    private byte day;
    //开启倒计时
    private int downTimeOpen;
    //结束倒计时
    private int downTimeEnd;
    //争霸状态
    private volatile byte fightState = GangDefine.STARCRAFT_STATE_UNOPEN;
    //是否在更新
    private volatile boolean isUpdate = true;

    //参战者集合
    private Map<Integer, StarcraftFighter> starcraftFighterMap = new ConcurrentHashMap<>();
    //公会排行
    private Vector<StarcraftRank> starcraftGangRankList = new Vector<>();
    //成员排行
    private Vector<StarcraftFighter> starcraftMemberRankList = new Vector<>();

    //争霸进度
    private byte progress;
    //城门BOSS血量
    private volatile int doorBoosHp;

    //采旗者
    private StarcraftFighter collectFighter;
    //彩旗时间
    private volatile long collectTime;

    private final static GangService instance = new GangService();

    public static GangService getPtr() {
        return instance;
    }

    private GangService() {

    }

    public void init() {
        GangStarcraft starcraft = new GangStarcraftDao().getGangStarcraft();
        if (starcraft != null) {
            this.gangOverlord = GameGangManager.getInstance().getGang(starcraft.getGangId());
            IGameRole role = GameWorld.getPtr().getGameRole(starcraft.getPresidentId());
            if (role != null) {
                this.playerPresident = role.getPlayer();
            }
            this.fightDay = starcraft.getFightDay();
            if (!StringUtil.isEmpty(starcraft.getGangRank())) {
                this.gangRankList = JSON.parseArray(starcraft.getGangRank(), GangStarcraftRank.class);
            }
            if (!StringUtil.isEmpty(starcraft.getMemberRank())) {
                this.memberRankList = JSON.parseArray(starcraft.getMemberRank(), GangStarcraftMemberRank.class);
            }
        }
    }

    private static int count = 1;
    private static Task starcraftTask = new Task() {
        @Override
        public void run() {
            try {
                GangService.getPtr().updateState();
                if (GangService.getPtr().getDownTimeEnd() <= 0) {
                    return;
                }
                ++count;
                //刷新并广播排行榜
                if (count % GangDefine.STARCRAFT_RANK_REFRESH == 0) {
                    GangService.getPtr().broadcastRank();
                }
                //刷新并广播目标
                if (count % GangDefine.STARCRAFT_TARGET_REFRESH == 0) {
                    GangService.getPtr().broadcastTarget();
                }
                //广播参战者复活
                GangService.getPtr().broadcastRevive();
                //皇宫殿前 皇宫积分更新
                GangService.getPtr().updateScore();
            } catch (Exception e) {
                logger.error("沙城争霸任务失败", e);
            }
        }

        @Override
        public String name() {
            return "STARCRAFT";
        }
    };

    private void fightReady() {
        count = 1;
        this.progress = GangDefine.STARCRAFT_AREA_DOOR;
        this.doorBoosHp = GangDefine.STARCRAFT_DOOR_HP;
        this.starcraftFighterMap = new ConcurrentHashMap<>();
        this.starcraftGangRankList = new Vector<>();
        this.starcraftMemberRankList = new Vector<>();
        TaskManager.getInstance().schedulePeriodicTask(ETaskType.COMMON, starcraftTask, DateUtil.SECOND, DateUtil.SECOND);
    }

    private void fightEnd() {
        TaskManager.getInstance().cancleTask(ETaskType.COMMON, starcraftTask);
        this.fightDay = DateUtil.formatDay(System.currentTimeMillis());
        this.fightState = GangDefine.STARCRAFT_STATE_UNOPEN;
        this.repeatUpdateState();

        //统计积分排序
        for (StarcraftFighter fighter : this.starcraftMemberRankList) {
            for (StarcraftRank rank : this.starcraftGangRankList) {
                if (fighter.getMember().getGangId() == rank.getId()) {
                    rank.addScore(fighter.getScore());
                }
            }
        }
        this.sortRank();

        Gang gangFirst = null;
        //发奖 第一名帮主奖
        if (this.starcraftGangRankList.size() > 0) {
            StarcraftRank rank = this.starcraftGangRankList.get(0);
            Gang gang = GameGangManager.getInstance().getGang(rank.getId());
            if (gang != null && gang.getSimplePlayer() != null) {
                Mail rewardMail = MailService.createMail("恭喜获得沙城争霸第1名奖励", "恭喜您在专属PVP群战活动：沙城争霸中，力压群雄，战到最后，获得了第1名殊荣！我们为您准备了海量的奖励，助您再创辉煌！", EGoodsChangeType.STARCRAFT_RANK_ADD, BattleModel.getGangRankReward((byte) 0));
                MailService.sendSystemMail(gang.getSimplePlayer().getId(), rewardMail);

                gangFirst = gang;
            }
        }
        //发奖 前5名帮会奖
        int size = this.starcraftGangRankList.size() > 5 ? 5 : this.starcraftGangRankList.size();
        for (int i = 0; i < size; ++i) {
            StarcraftRank rank = this.starcraftGangRankList.get(i);
            Gang gang = GameGangManager.getInstance().getGang(rank.getId());
            if (gang != null) {
                gang.addStore(BattleModel.getGangRankReward((byte) (i + 1)));
            }
        }
        //发奖 前10名参战者奖
        size = this.starcraftMemberRankList.size() > 10 ? 10 : this.starcraftMemberRankList.size();
        for (int i = 0; i < size; ++i) {
            StarcraftFighter figher = this.starcraftMemberRankList.get(i);
            int rank = i + 1;
            Mail rewardMail = MailService.createMail("恭喜获得沙城争霸第" + rank + "名奖励", "恭喜您在专属PVP群战活动：沙城争霸中，力压群雄，战到最后，获得了第" + rank + "名殊荣！我们为您准备了海量的奖励，助您再创辉煌！", EGoodsChangeType.STARCRAFT_RANK_ADD, BattleModel.getGangMemberRankReward((byte) rank));
            MailService.sendSystemMail(figher.getMember().getPlayerId(), rewardMail);
        }

        //上届记录存储
        List<GangStarcraftRank> gangRank = new ArrayList<>();
        for (StarcraftRank rank : this.starcraftGangRankList) {
            GangStarcraftRank gangStarcraftRank = new GangStarcraftRank();
            gangStarcraftRank.setId(rank.getId());
            gangStarcraftRank.setName(rank.getName());
            gangStarcraftRank.setScore(rank.getScore());
            gangRank.add(gangStarcraftRank);
        }
        List<GangStarcraftMemberRank> memberRank = new ArrayList<>();
        for (StarcraftFighter figher : this.starcraftMemberRankList) {
            GangStarcraftMemberRank gangStarcraftMemberRank = new GangStarcraftMemberRank();
            gangStarcraftMemberRank.setId(figher.getMember().getPlayerId());
            gangStarcraftMemberRank.setName(figher.getMember().getSimplePlayer().getName());
            gangStarcraftMemberRank.setScore(figher.getScore());
            Gang gang = GameGangManager.getInstance().getGang(figher.getMember().getGangId());
            if (gang != null) {
                gangStarcraftMemberRank.setGangName(gang.getName());
            }
            memberRank.add(gangStarcraftMemberRank);

            //发放积分达标奖励
            DropData dropData = BattleModel.getScoreReach(figher.getScore());
            if (dropData != null) {
                Mail rewardMail = MailService.createMail("恭喜获得沙城争霸达标奖励", "恭喜您在专属PVP群战活动：沙城争霸中，获得沙城争霸达标奖励！我们为您准备了海量的奖励，助您再创辉煌！", EGoodsChangeType.STARCRAFT_RANK_ADD, dropData);
                MailService.sendSystemMail(figher.getMember().getPlayerId(), rewardMail);
            }
            Message message = new Message(MessageCommand.STARCRAFT_BALANCE_MESSAGE);
            this.getRankMessage(message);
            this.broadcast(message);
        }
        new GangStarcraftDao().updateGangStarcraft(gangFirst == null ? 0 : gangFirst.getId(), gangFirst == null ? 0 : gangFirst.getSimplePlayer().getId(), fightDay, JSON.toJSONString(gangRank), JSON.toJSONString(memberRank));

        this.gangOverlord = gangFirst;
        this.playerPresident = gangFirst == null ? null : (Player) gangFirst.getSimplePlayer();
        this.gangRankList = gangRank;
        this.memberRankList = memberRank;
    }

    public void updateState() {
        if (this.isUpdate) {
            this.isUpdate = false;
            if (this.fightState == GangDefine.STARCRAFT_STATE_UNOPEN) {
                long currentTime = System.currentTimeMillis();
                String toDay = DateUtil.formatDay(currentTime);
                Calendar c = Calendar.getInstance();
                c.setTime(new Date());
                int week = c.get(Calendar.DAY_OF_WEEK);
                if (week == GangDefine.STARCRAFT_WEEK && !toDay.equals(this.fightDay) && DateUtil.getDistanceDay(GameDefine.SERVER_CREATE_TIME, currentTime) >= GangDefine.STARCRAFT_OS_AFTER) {
                    Date startTime = DateUtil.parseDataTime(toDay + " " + GangDefine.STARCRAFT_START_TIME);
                    this.month = (byte) (c.get(Calendar.MONTH) + 1);
                    this.day = (byte) (c.get(Calendar.DAY_OF_MONTH));
                    this.downTimeOpen = (int) (startTime.getTime() - currentTime);
                    if (this.downTimeOpen < 0) {
                        this.downTimeOpen = 0;
                        this.fightDay = toDay;
                        this.fightState = GangDefine.STARCRAFT_STATE_DOOR;
                        this.fightReady();
                        repeatUpdateState();
                    }
                } else {
                    int disDay = 7;
                    if (DateUtil.getDistanceDay(GameDefine.SERVER_CREATE_TIME, currentTime) < GangDefine.STARCRAFT_OS_AFTER) {
                        disDay += 7;
                    }
                    Date fightDate = DateUtil.getNowTimeBeforeOrAfter(disDay - week);
                    String day = DateUtil.formatDay(fightDate.getTime());
                    Date startTime = DateUtil.parseDataTime(day + " " + GangDefine.STARCRAFT_START_TIME);
                    c.setTime(fightDate);
                    this.month = (byte) (c.get(Calendar.MONTH) + 1);
                    this.day = (byte) (c.get(Calendar.DAY_OF_MONTH));
                    this.downTimeOpen = (int) (startTime.getTime() - currentTime);
                }
            } else if (this.fightState == GangDefine.STARCRAFT_STATE_DOOR) {
                this.calculationDownTimeEnd();
            } else if (this.fightState == GangDefine.STARCRAFT_STATE_FIGHT) {
                this.calculationDownTimeEnd();
                //采集
                long currTime = System.currentTimeMillis();
                if (this.collectFighter != null && this.collectTime > 0 && this.collectTime + GangDefine.STARCRAFT_COLLECT_TIME < currTime) {
                    for (StarcraftRank rank : this.starcraftGangRankList) {
                        if (this.collectFighter.getMember().getGangId() == rank.getId()) {
                            rank.addScore(90000);
                        }
                    }
                    this.fightState = GangDefine.STARCRAFT_STATE_END;
                    repeatUpdateState();
                }
            } else if (this.fightState == GangDefine.STARCRAFT_STATE_END) {
                this.fightEnd();
            }
            this.isUpdate = true;
        }
    }

    private void calculationDownTimeEnd() {
        long currentTime = System.currentTimeMillis();
        String toDay = DateUtil.formatDay(currentTime);
        Date endTime = DateUtil.parseDataTime(toDay + " " + GangDefine.STARCRAFT_END_TIME);
        this.downTimeEnd = (int) (endTime.getTime() - currentTime);
        if (this.downTimeEnd < 0) {
            this.fightState = GangDefine.STARCRAFT_STATE_END;
            repeatUpdateState();
        }
    }

    private void repeatUpdateState() {
        this.isUpdate = true;
        this.updateState();
    }

    public StarcraftFighter starcraftEnter(Player player) {
        if (player.getGang() == null) {
            return null;
        }
        StarcraftFighter fighter = this.getStarcraftFight(player);
        fighter.setArea(GangDefine.STARCRAFT_AREA_DOOR);
        this.sortRank();
        return fighter;
    }

    public StarcraftFighter getStarcraftFight(int playerId) {
        return this.starcraftFighterMap.get(playerId);
    }

    public StarcraftFighter getStarcraftFight(Player player) {
        int id = player.getId();
        if (this.starcraftFighterMap.containsKey(id)) {
            return this.starcraftFighterMap.get(id);
        }
        long currTime = System.currentTimeMillis();
        Gang gang = player.getGang();
        GangMember member = gang.getGangMember(id);
        StarcraftFighter fighter = new StarcraftFighter(member);
        fighter.setArea(GangDefine.STARCRAFT_AREA_DOOR);
        fighter.setEnterTime(currTime);
        this.starcraftFighterMap.put(id, fighter);
        this.starcraftMemberRankList.addElement(fighter);
        int gangId = member.getGangId();
        boolean notExist = true;
        for (StarcraftRank starcraftRank : this.starcraftGangRankList) {
            if (starcraftRank.getId() == gangId) {
                notExist = false;
                break;
            }
        }
        if (notExist) {
            StarcraftRank starcraftRank = new StarcraftRank();
            starcraftRank.setId(gangId);
            starcraftRank.setName(gang.getName());
            starcraftRank.setEnterTime(currTime);
            this.starcraftGangRankList.addElement(starcraftRank);
        }
        return fighter;
    }

    /**
     * 是否开启战斗
     *
     * @return
     */
    public boolean isOpenFight() {
        if (this.fightState == GangDefine.STARCRAFT_STATE_DOOR || this.fightState == GangDefine.STARCRAFT_STATE_FIGHT) {
            return true;
        }
        return false;
    }

    /**
     * 排行榜消息体
     *
     * @param message
     */
    public void getRankMessage(Message message) {
        message.setShort(this.starcraftMemberRankList.size());
        for (StarcraftFighter rank : this.starcraftMemberRankList) {
            message.setInt(rank.getMember().getPlayerId());
            message.setInt(rank.getMember().getGangId());
            message.setString(rank.getMember().getSimplePlayer().getName());
            message.setInt(rank.getScore());
            message.setByte(rank.getArea());
            message.setByte(rank.getMember().getSimplePlayer().getHead());
            message.setByte(rank.getMember().getPosition());
            message.setLong(rank.getMember().getSimplePlayer().getFighting());
        }
        message.setShort(this.starcraftGangRankList.size());
        for (StarcraftRank rank : this.starcraftGangRankList) {
            message.setInt(rank.getId());
            message.setString(rank.getName());
            message.setInt(rank.getScore());
        }
    }

    /**
     * 得到区域信息
     *
     * @param message
     * @param area
     * @param gangId
     * @param isHaveTarget
     */
    public void getAreaMessage(Message message, byte area, int gangId) {
        int[] num = {0, 0};
        List<StarcraftFighter> list = new ArrayList<>();
        List<StarcraftFighter> target = new ArrayList<>();
        for (StarcraftFighter fighter : this.starcraftFighterMap.values()) {
            if (fighter.getArea() == area) {
                ++num[0];
                list.add(fighter);
            }
            if (fighter.getMember().getGangId() == gangId) {
                ++num[1];
            } else {
                if (area != GangDefine.STARCRAFT_AREA_DOOR && fighter.getArea() == area) {
                    target.add(fighter);
                }
            }
        }
        //区域人数
        //区域同帮
        message.setShort(num[0]);
        message.setByte(num[1]);
        //区域参战者外形
        message.setByte(list.size());
        for (StarcraftFighter fighter : list) {
            fighter.getMember().getSimplePlayer().getFightAppearMessage(message);
        }
        //是否有目标
        if (area != GangDefine.STARCRAFT_AREA_DOOR) {
            //目标数量
            int size = target.size() > GangDefine.STARCRAFT_TARGET_MAX[area] ? GangDefine.STARCRAFT_TARGET_MAX[area] : target.size();
            message.setByte(size);
            for (int i = 0; i < size; ++i) {
                target.get(i).getMember().getSimplePlayer().getFightAppearMessage(message);
            }
        }
    }

    /**
     * 伤害城门boss
     *
     * @param damage
     */
    public void hurtDoorBoss(int damage) {
        if (this.fightState != GangDefine.STARCRAFT_STATE_DOOR) {
            return;
        }
        int percent = (int) (GangDefine.STARCRAFT_DOOR_HP * 0.1f);
        int current = this.doorBoosHp / percent;
        this.doorBoosHp -= damage;
        int after = this.doorBoosHp / percent;
        //掉10%血更新一次数据
        if (current != after) {
            Message message = new Message(MessageCommand.STARCRAFT_ATTACK_DOOR_MESSAGE);
            message.setInt(this.doorBoosHp);
            this.broadcast(message);
        }
        if (this.doorBoosHp <= 0) {
            this.fightState = GangDefine.STARCRAFT_STATE_FIGHT;
            this.progress = GangDefine.STARCRAFT_AREA_INSIDE;
            //结算城门boss积分奖励
            Map<Integer, Integer> rankMap = new HashMap<>();
            for (StarcraftFighter fighter : this.starcraftMemberRankList) {
                Integer count = rankMap.get(fighter.getMember().getGangId());
                if (count == null) {
                    rankMap.put(fighter.getMember().getGangId(), 1);
                } else {
                    rankMap.put(fighter.getMember().getGangId(), ++count);
                }
                fighter.setScore(GangDefine.STARCRAFT_DOOR_SCORE);
            }
            for (StarcraftRank rank : this.starcraftGangRankList) {
                Integer count = rankMap.get(rank.getId());
                if (count == null) {
                    rank.setScore(0);
                } else {
                    rank.setScore(count * GangDefine.STARCRAFT_DOOR_SCORE);
                }
            }
            this.sortRank();

            Message message = new Message(MessageCommand.STARCRAFT_ATTACK_DEAD_MESSAGE);
            message.setByte(this.progress);
            this.getRankMessage(message);
            this.broadcast(message);
        }
    }

    /**
     * 传送
     *
     * @param move
     */
    public boolean move(StarcraftFighter starcraftFighter, byte move) {
        boolean isMove = false;
        if (move <= this.progress) {
            isMove = true;
        } else {
            if (this.progress == GangDefine.STARCRAFT_AREA_DOOR) {
                isMove = false;
            } else if (move == this.progress + 1) {
                if (move == GangDefine.STARCRAFT_AREA_FRONT && starcraftFighter.getFeat() < GangDefine.STARCRAFT_AREA_FRONT_NEED_FEAT) {
                    isMove = false;
                } else {
                    this.progress = move;
                    //广播进度更新
                    Message message = new Message(MessageCommand.STARCRAFT_AREA_PROCESS_MESSAGE);
                    message.setByte(this.progress);
                    this.broadcast(message);
                    isMove = true;
                }
            }
        }
        if (isMove && move == GangDefine.STARCRAFT_AREA_FRONT && move > starcraftFighter.getArea()) {
            if (starcraftFighter.getFeat() < GangDefine.STARCRAFT_AREA_FRONT_NEED_FEAT) {
                isMove = false;
            } else {
                starcraftFighter.setFeat((short) (starcraftFighter.getFeat() - GangDefine.STARCRAFT_AREA_FRONT_NEED_FEAT));
            }
        }

        //广播玩家传送
        if (isMove) {
            starcraftFighter.setArea(move);
            this.broadcastMove(starcraftFighter);
        }
        return isMove;
    }

    /**
     * 广播玩家传送
     *
     * @param fighter
     */
    private void broadcastMove(StarcraftFighter starcraftFighter) {
        Message message = new Message(MessageCommand.STARCRAFT_BROADCAST_MOVE_MESSAGE);
        message.setByte(starcraftFighter.getArea());
        starcraftFighter.getMember().getSimplePlayer().getFightAppearMessage(message);
        this.broadcast(message);
    }

    /**
     * 广播排行榜信息
     */
    public void broadcastRank() {
        this.sortRank();
        Message message = new Message(MessageCommand.STARCRAFT_BROADCAST_RANK_MESSAGE);
        this.getRankMessage(message);
        this.broadcast(message);
    }

    /**
     * 广播目标信息
     */
    public void broadcastTarget() {
        for (StarcraftFighter fighter : this.starcraftFighterMap.values()) {
            IGameRole role = GameWorld.getPtr().getGameRole(fighter.getMember().getPlayerId());
            if (role != null && role.getGameRole() != null) {
                List<StarcraftFighter> targetList = new ArrayList<>();
                for (StarcraftFighter target : this.starcraftFighterMap.values()) {
                    if (target.getDeadTime() == 0 && target.getMember().getGangId() != fighter.getMember().getGangId() && target.getArea() == fighter.getArea()) {
                        targetList.add(target);
                        if (targetList.size() == GangDefine.STARCRAFT_TARGET_MAX[fighter.getArea()]) {
                            break;
                        }
                    }
                }
                Message message = new Message(MessageCommand.STARCRAFT_TARGET_MESSAGE);
                int size = targetList.size();
                message.setByte(size);
                for (int i = 0; i < size; ++i) {
                    targetList.get(i).getMember().getSimplePlayer().getFightAppearMessage(message);
                }
                role.getGameRole().putMessageQueue(message);
            }
        }
    }

    /**
     * 广播参赛者复活
     */
    public void broadcastRevive() {
        long currTime = System.currentTimeMillis();
        for (StarcraftFighter fighter : this.starcraftFighterMap.values()) {
            if (fighter.getDeadTime() > 0 && fighter.getDeadTime() + GangDefine.STARCRAFT_REVIVE_TIME < currTime) {
                fighter.setDeadTime(0);
                fighter.setArea(GangDefine.STARCRAFT_AREA_DOOR);

                Message message = new Message(MessageCommand.STARCRAFT_REVIVE_MESSAGE);
                IGameRole role = GameWorld.getPtr().getGameRole(fighter.getMember().getPlayerId());
                if (role != null && role.getGameRole() != null) {
                    role.getGameRole().putMessageQueue(message);
                }
            }
        }
    }

    /**
     * 广播
     */
    public void broadcast(Message message) {
        for (StarcraftFighter fighter : this.starcraftFighterMap.values()) {
            IGameRole role = GameWorld.getPtr().getGameRole(fighter.getMember().getPlayerId());
            if (role != null && role.getGameRole() != null) {
                role.getGameRole().putMessageQueue(message);
            }
        }
    }

    /**
     * 皇宫殿前 皇宫积分更新
     */
    public void updateScore() {
        for (StarcraftFighter fighter : this.starcraftFighterMap.values()) {
            if (fighter.getArea() == GangDefine.STARCRAFT_AREA_FRONT) {
                if (fighter.getArea() == fighter.getRecordArea()) {
                    fighter.setCount((byte) (fighter.getCount() + 1));
                    if (fighter.getCount() >= 5) {
                        fighter.addScore(2);
                        fighter.setCount((byte) 0);
                    }
                } else {
                    fighter.setCount((byte) 0);
                }
            } else if (fighter.getArea() == GangDefine.STARCRAFT_AREA_PALACE) {
                if (fighter.getArea() == fighter.getRecordArea()) {
                    fighter.setCount((byte) (fighter.getCount() + 1));
                    if (fighter.getCount() >= 5) {
                        fighter.addScore(4);
                        fighter.setCount((byte) 0);
                    }
                } else {
                    fighter.setCount((byte) 0);
                }
            }
        }
    }

    /**
     * 广播采旗中断
     *
     * @param playerId
     */
    public void broadcastCollectStop(int playerId) {
        GangService.getPtr().setCollectTime(0);
        GangService.getPtr().setCollectFighter(null);

        Message msg = new Message(MessageCommand.STARCRAFT_COLLECT_STOP_MESSAGE);
        msg.setInt(playerId);
        GangService.getPtr().broadcast(msg);
    }

    /**
     * 添加积分
     *
     * @param gangId
     * @param score
     */
    public void addScore(int gangId, int score) {
        for (StarcraftRank gangRank : this.starcraftGangRankList) {
            if (gangRank.getId() == gangId) {
                gangRank.addScore(score);
                return;
            }
        }
    }

    private void sortRank() {
        Collections.sort(this.starcraftGangRankList);
        Collections.sort(this.starcraftMemberRankList);
    }

    public Gang getGangOverlord() {
        return this.gangOverlord;
    }

    public Player getPlayerPresident() {
        return this.playerPresident;
    }

    public List<GangStarcraftRank> getGangRankList() {
        return gangRankList;
    }

    public List<GangStarcraftMemberRank> getMemberRankList() {
        return memberRankList;
    }

    public byte getMonth() {
        return month;
    }

    public byte getDay() {
        return day;
    }

    public int getDownTimeOpen() {
        return downTimeOpen;
    }

    public int getDownTimeEnd() {
        return downTimeEnd;
    }

    public Vector<StarcraftRank> getStarcraftGangRankList() {
        return starcraftGangRankList;
    }

    public Vector<StarcraftFighter> getStarcraftMemberRankList() {
        return starcraftMemberRankList;
    }

    public byte getProgress() {
        return progress;
    }

    public int getDoorBoosHp() {
        return doorBoosHp;
    }

    public StarcraftFighter getCollectFighter() {
        return collectFighter;
    }

    public void setCollectFighter(StarcraftFighter collectFighter) {
        this.collectFighter = collectFighter;
    }

    public long getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(long collectTime) {
        this.collectTime = collectTime;
    }
}
