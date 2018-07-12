package com.rd.game;

import com.alibaba.fastjson.JSON;
import com.rd.bean.gang.Gang;
import com.rd.bean.gang.GangMember;
import com.rd.bean.gang.GangTurntableLog;
import com.rd.bean.gang.fight.*;
import com.rd.bean.mail.Mail;
import com.rd.bean.player.Player;
import com.rd.bean.player.SimplePlayer;
import com.rd.common.ChatService;
import com.rd.common.MailService;
import com.rd.dao.GangDao;
import com.rd.define.*;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.ISimplePlayerListener;
import com.rd.game.event.type.GameGangCreateEvent;
import com.rd.model.GangModel;
import com.rd.model.data.GangFightRankData;
import com.rd.util.DateUtil;
import com.rd.util.StringUtil;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>Title: 游戏公会管理</p>
 * <p>Description: 游戏公会管理</p>
 * <p>Company: 北京万游畅想科技有限公司</p>
 *
 * @author ---
 * @version 1.0
 * @data 2016年12月28日 下午2:53:56
 */
public class GameGangManager implements ISimplePlayerListener {

    private static final Logger logger = Logger.getLogger(GameGangManager.class.getName());

    private static GameGangManager gameGangManager = new GameGangManager();

    public static GameGangManager getInstance() {
        return gameGangManager;
    }

    private AtomicInteger idGenerator;

    private Map<Integer, Gang> gangMap;

    private List<Gang> gangList;

    private GangDao gangDao = new GangDao();

    private volatile boolean isSortGang = true;

    private short pageAll;

    private Queue<GangTurntableLog> gangTurntableLogQueue = new ConcurrentLinkedQueue<>();

    private final String READY_TIME = "21:00:00";
    private final String FIGHT_TIME = "21:30:00";
    private final byte FIGHT_WEEK = 1;

    private List<FightGang> fightGangList = new ArrayList<>();
    private List<FightRound> roundList = new ArrayList<>();
    private List<FightGang> fightGangRankList = new ArrayList<>();
    private List<FightPlayer> fightPlayerRankList = new ArrayList<>();
    private Map<Integer, FightPlayer> fightPlayerMap = new ConcurrentHashMap<>();

    private volatile byte fightState;
    private volatile boolean isUpdate = true;
    private long downTime;
    private long fightTime;

    private volatile boolean isMemberRankUpdate = true;

    private String fightDay;
    private List<FightGangRankData> gangRankHistoryList;
    private List<FightGangRankData> memberRankHistoryList;

    private GameGangManager() {

    }

    public void init() {
        int serverId = GameDefine.getServerId();
        int idLow = GameDefine.getIdLow(serverId);
        int idHigh = GameDefine.getIdHigh(serverId);
        int maxId = gangDao.getGangMaxId(idLow, idHigh);
        this.idGenerator = new AtomicInteger(maxId == 0 ? idLow : maxId);
        logger.info("GameGangManager.initIdGenerator() generator=" + idGenerator.get());
        //  this.loadGang();
        this.loadGangFight();
        GameWorld.getPtr().addSimplePlayerListener(this);
    }

    public void loadGang() {
        gangMap = new ConcurrentHashMap<>(gangDao.getAllGang());
        gangList = new ArrayList<>();
        for (Gang gang : gangMap.values()) {
            ConcurrentHashMap<Integer, GangMember> members = new ConcurrentHashMap<>(gangDao.getGangMembers(gang.getId()));
            gang.setMemberMap(members);
            gangList.add(gang);
        }
        this.sortGang();
        this.calculatePage();
    }

    private void loadGangFight() {
        try {
            String[] data = this.gangDao.getGangFight();
            if (StringUtil.isEmpty(data[0])) {
                this.fightDay = "";
            } else {
                this.fightDay = data[0];
            }
            if (StringUtil.isEmpty(data[1])) {
                this.gangRankHistoryList = new ArrayList<>();
            } else {
                this.gangRankHistoryList = JSON.parseArray(data[1], FightGangRankData.class);
            }
            if (StringUtil.isEmpty(data[2])) {
                this.memberRankHistoryList = new ArrayList<>();
            } else {
                this.memberRankHistoryList = JSON.parseArray(data[2], FightGangRankData.class);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());

            this.fightDay = "";
            this.gangRankHistoryList = new ArrayList<>();
            this.memberRankHistoryList = new ArrayList<>();
        }
    }

    public Gang getGang(int gangId) {
        return null;
    }

    public synchronized short createGang(GameRole gameRole, String name, byte badge) {
        if (!checkGangName(name)) {
            return ErrorDefine.ERROR_NAME_DUPLICATE;
        }
        int gangId = generateGangId();
        Gang gang = new Gang(gangId, gameRole.getPlayer(), name, badge);

        GangMember member = new GangMember(gameRole.getPlayer(), gangId, GangDefine.GANG_POSITION_PRESIDENT, gameRole.getDungeonManager().getDungeonGangPass());
        if (gangDao.createGang(gang, member)) {
            gang.getMemberMap().put(member.getPlayerId(), member);
            gangMap.put(gang.getId(), gang);
            gangList.add(gang);
            this.calculatePage();
            gameRole.getPlayer().setGang(gang);

            //帮派创建事件
            GameGangCreateEvent event = EGameEventType.GANG_CREATE.create(gameRole, 1, null);
            gameRole.getEventManager().notifyEvent(event);
        } else {
            return ErrorDefine.ERROR_OPERATION_FAILED;
        }
        return GameDefine.NONE;
    }


    private boolean checkGangName(String name) {
        for (Gang gang : this.gangMap.values()) {
            if (gang.getName().equals(name)) {
                return false;
            }
        }
        return true;
    }

    private int generateGangId() {
        return idGenerator.incrementAndGet();
    }

    public void updateSingleHandler(SimplePlayer simplePlayer) {
//		for(Gang gang:gangMap.values()){
//			if(gang.getSimplePlayer().getId()==simplePlayer.getId()){
//				gang.setSimplePlayer(simplePlayer);
//			}
//			for(GangMember memeber:gang.getMemberMap().values()){
//				if(memeber.getPlayerId()==simplePlayer.getId()){
//					memeber.setSimplePlayer(simplePlayer);
//				}
//			}
//		}
    }

    public synchronized void removeGang(Gang gang) {
        gangMap.remove(gang.getId());
        gangList.remove(gang);
        this.calculatePage();
    }

    public void setPlayerGang(Player player) {
//		for(Gang gang:gangMap.values()){
//			if(gang.getMemberMap().containsKey(player.getId())){
//				player.setGang(gang);
//				return;
//			}
//		}
    }

    public void sortGang() {
        if (this.isSortGang) {
            this.isSortGang = false;
            Collections.sort(gangList, new SortByGang());
            this.isSortGang = true;
        }
    }

    private void calculatePage() {
        int size = this.gangList.size();
        if (size % GangDefine.GANG_LIST_PAGE_NUM == 0) {
            this.pageAll = (short) (size / GangDefine.GANG_LIST_PAGE_NUM);
        } else {
            this.pageAll = (short) (size / GangDefine.GANG_LIST_PAGE_NUM + 1);
        }
    }

    public List<Gang> getGangList() {
        return gangList;
    }

    public short getPageAll() {
        return pageAll;
    }

    public Queue<GangTurntableLog> getGangTurntableLogQueue() {
        return gangTurntableLogQueue;
    }

    public void setGangTurntableLogQueue(Queue<GangTurntableLog> gangTurntableLogQueue) {
        this.gangTurntableLogQueue = gangTurntableLogQueue;
    }

    public String toGangTurntableLogQueueJson() {
        return JSON.toJSONString(gangTurntableLogQueue);
    }

    public void fromGangLogQueueJson(String json) {
        this.gangTurntableLogQueue = new ConcurrentLinkedQueue<>();
        if (!StringUtil.isEmpty(json)) {
            List<GangTurntableLog> list = JSON.parseArray(json, GangTurntableLog.class);
            for (GangTurntableLog log : list) {
                this.gangTurntableLogQueue.add(log);
            }
        }
    }

    public void addGangTurntableLog(GangTurntableLog log) {
        this.gangTurntableLogQueue.add(log);
        if (this.gangTurntableLogQueue.size() > GangDefine.GANG_LOG_NUM) {
            this.gangTurntableLogQueue.poll();
        }
    }

    public void resetDungeon() {
        GangDao dao = new GangDao();
        for (Gang gang : gangMap.values()) {
//			gang.setDungeonPass((short)0);
            gang.setDungeonYesterdayFirst(gang.getDungeonTodayFirst());
//			gang.setDungeonTodayFirst(null);
            gang.clearShopLimitNumMap();
            dao.updateDungeonFirst(gang);
        }
        dao.updateDungeonReset();
    }

    public void updateState() {
        if (this.isUpdate) {
            try {
                this.isUpdate = false;
                long currentTime = System.currentTimeMillis();
                if (this.fightState == GangDefine.GANG_FIGHT_STATE_NONE) {
                    String toDay = DateUtil.formatDay(currentTime);
                    Calendar c = Calendar.getInstance();
                    c.setTime(new Date());
                    int week = c.get(Calendar.DAY_OF_WEEK);
                    if (week == this.FIGHT_WEEK && !toDay.equals(this.fightDay) && DateUtil.getDistanceDay(GameDefine.SERVER_CREATE_TIME, currentTime) > 0) {
                        Date startTime = DateUtil.parseDataTime(toDay + " " + READY_TIME);
                        this.downTime = startTime.getTime() - currentTime;
                        if (this.downTime < 0) {
                            //判断是否开启公会战
                            boolean isNext = true;
                            int size = this.gangList.size() > GangDefine.GANG_FIGHT_GANG_NUM ? GangDefine.GANG_FIGHT_GANG_NUM : this.gangList.size();
                            for (int i = 0; i < size; ++i) {
                                if (this.gangList.get(i).getMemberMap().size() >= GangDefine.GANG_FIGHT_MEMBER_NUM) {
                                    isNext = false;
                                    break;
                                }
                            }
                            if (isNext) {
                                this.fightDay = toDay;
                                this.repeatUpdateState();
                            } else {
                                this.fightReset();
                                //1-8
                                this.addFightGang(0);
                                this.addFightGang(7);
                                //4-5
                                this.addFightGang(3);
                                this.addFightGang(4);
                                //2-7
                                this.addFightGang(1);
                                this.addFightGang(6);
                                //3-6
                                this.addFightGang(2);
                                this.addFightGang(5);

                                this.newFightRound(this.fightGangList, this.getCurrentRound());
                                this.fightState = GangDefine.GANG_FIGHT_STATE_READY;
                                this.repeatUpdateState();
                            }
                        }
                    } else {
                        Date fightDate = DateUtil.getNowTimeBeforeOrAfter(8 - week);
                        String day = DateUtil.formatDay(fightDate.getTime());
                        Date startTime = DateUtil.parseDataTime(day + " " + READY_TIME);
                        this.downTime = startTime.getTime() - currentTime;
                    }
                } else if (this.fightState == GangDefine.GANG_FIGHT_STATE_READY) {
                    String toDay = DateUtil.formatDay(currentTime);
                    Date startTime = DateUtil.parseDataTime(toDay + " " + FIGHT_TIME);
                    this.downTime = startTime.getTime() - currentTime;
                    if (currentTime > startTime.getTime()) {
                        this.fightDay = toDay;
                        this.fightTime = currentTime;
                        this.fightState = GangDefine.GANG_FIGHT_STATE_FIGHT;
                        this.repeatUpdateState();
                    }
                } else if (this.fightState == GangDefine.GANG_FIGHT_STATE_FIGHT) {
                    this.downTime = fightTime + GangDefine.GANG_FIGHT_TIME - currentTime;
                    boolean isOver = true;
                    FightRound fightRound = this.getCurrentFightRound();
                    for (FightBattle fightBattle : fightRound.getBattleList()) {
                        if (fightBattle.getFightGangOne().getState() == GangDefine.GANG_FIGHT_GANG_STATE_FIGHT || fightBattle.getFightGangTwo().getState() == GangDefine.GANG_FIGHT_GANG_STATE_FIGHT) {
                            isOver = false;
                            break;
                        }
                    }
                    if (this.downTime < 0 || isOver) {
                        List<FightGang> sortFightGang = new ArrayList<>();
                        for (FightBattle fightBattle : fightRound.getBattleList()) {
                            FightGang fightGangOne = fightBattle.getFightGangOne();
                            FightGang fightGangTwo = fightBattle.getFightGangTwo();
                            if (fightGangOne.getState() == GangDefine.GANG_FIGHT_GANG_STATE_FIGHT && fightGangTwo.getState() == GangDefine.GANG_FIGHT_GANG_STATE_FIGHT) {
                                if (this.compareBattle(fightGangOne, fightGangTwo)) {
                                    fightGangOne.setState(GangDefine.GANG_FIGHT_GANG_STATE_WIN);
                                    fightGangTwo.setState(GangDefine.GANG_FIGHT_GANG_STATE_OUT);
                                } else {
                                    fightGangOne.setState(GangDefine.GANG_FIGHT_GANG_STATE_OUT);
                                    fightGangTwo.setState(GangDefine.GANG_FIGHT_GANG_STATE_WIN);
                                }
                            } else if (fightGangOne.getState() == GangDefine.GANG_FIGHT_GANG_STATE_OUT && fightGangTwo.getState() == GangDefine.GANG_FIGHT_GANG_STATE_OUT) {
                                if (fightGangOne.getIndex() < fightGangTwo.getIndex()) {
                                    fightGangOne.setState(GangDefine.GANG_FIGHT_GANG_STATE_WIN);
                                } else {
                                    fightGangTwo.setState(GangDefine.GANG_FIGHT_GANG_STATE_WIN);
                                }
                            } else if (fightGangOne.getState() == GangDefine.GANG_FIGHT_GANG_STATE_FIGHT && fightGangTwo.getState() == GangDefine.GANG_FIGHT_GANG_STATE_OUT) {
                                fightGangOne.setState(GangDefine.GANG_FIGHT_GANG_STATE_WIN);
                            } else if (fightGangOne.getState() == GangDefine.GANG_FIGHT_GANG_STATE_OUT && fightGangTwo.getState() == GangDefine.GANG_FIGHT_GANG_STATE_FIGHT) {
                                fightGangTwo.setState(GangDefine.GANG_FIGHT_GANG_STATE_WIN);
                            }
                            if (fightGangOne.getGang() != null && fightGangOne.getState() == GangDefine.GANG_FIGHT_GANG_STATE_OUT) {
                                sortFightGang.add(fightGangOne);
                            }
                            if (fightGangTwo.getGang() != null && fightGangTwo.getState() == GangDefine.GANG_FIGHT_GANG_STATE_OUT) {
                                sortFightGang.add(fightGangTwo);
                            }
                        }
                        Collections.sort(sortFightGang, new SortByFightGang());
                        for (FightGang fightGang : sortFightGang) {
                            this.fightGangRankList.add(fightGang);
                        }
                        if (fightRound.getBattleList().size() == 1) {
                            this.fightState = GangDefine.GANG_FIGHT_STATE_NONE;

                            FightGang firstGang = null;
                            if (fightRound.getBattleList().get(0).getFightGangOne().getState() == GangDefine.GANG_FIGHT_GANG_STATE_WIN) {
                                firstGang = fightRound.getBattleList().get(0).getFightGangOne();
                            } else if (fightRound.getBattleList().get(0).getFightGangTwo().getState() == GangDefine.GANG_FIGHT_GANG_STATE_WIN) {
                                firstGang = fightRound.getBattleList().get(0).getFightGangTwo();
                            }
                            if (firstGang == null || firstGang.getGang() == null) {
                                this.fightState = GangDefine.GANG_FIGHT_STATE_SHOW;
                                this.repeatUpdateState();

                                gangDao.updateGangFight(this.fightDay, "", "");
                                logger.error("帮战数据出错...");
                            } else {
                                firstGang.setRound((byte) (this.getCurrentRound() + 1));
                                this.fightGangRankList.add(firstGang);
                                Collections.reverse(this.fightGangRankList);

                                //公会战结束 发放奖励
                                for (int i = 0; i < this.fightGangRankList.size(); ++i) {
                                    this.fightGangRankList.get(i).sendReward((byte) (i + 1));
                                }

                                Collections.sort(this.fightPlayerRankList, new SortByFightPlayer());

                                for (int i = 0; i < this.fightPlayerRankList.size(); ++i) {
                                    GangFightRankData gangFightRankData = GangModel.getGangFightRank((byte) (i + 1));
                                    if (gangFightRankData == null) {
                                        break;
                                    }
                                    FightPlayer fPlayer = this.fightPlayerRankList.get(i);

                                    logger.info("Gang Fight PlayerID:" + fPlayer.getPlayer().getId() + " Name:" + fPlayer.getPlayer().getName() + " Rank:" + (i + 1) + " Star:" + fPlayer.getStarNum() + " Score:" + fPlayer.getScore());

                                    if (fPlayer.getStarNum() == 0 && fPlayer.getScore() == 0) {
                                        continue;
                                    }

                                    Mail mail = MailService.createMail(gangFightRankData.getTitle(), gangFightRankData.getContent(), EGoodsChangeType.GANG_FIGHT_WIN_ADD, gangFightRankData.getReward());
                                    MailService.sendSystemMail(fPlayer.getPlayer().getId(), mail);

                                    if (i == 0) {
                                        ChatService.broadcastPlayerMsg(fPlayer.getPlayer(), EBroadcast.GANG_PERSONAL);
                                    }
                                }

                                //记录公会战状态
                                this.gangRankHistoryList.clear();
                                this.memberRankHistoryList.clear();
                                for (FightGang fightGang : this.fightGangRankList) {
                                    this.gangRankHistoryList.add(new FightGangRankData(fightGang.getGang().getId(), fightGang.getGangName(), (short) (fightGang.getTotalStarNum() + fightGang.getStarNum()), fightGang.getTotalScore() + fightGang.getScore()));
                                }
                                for (FightPlayer fightPlayer : this.fightPlayerRankList) {
                                    if (fightPlayer.getStarNum() == 0 && fightPlayer.getScore() == 0) {
                                        continue;
                                    }
                                    this.memberRankHistoryList.add(new FightGangRankData(fightPlayer.getPlayer().getId(), fightPlayer.getPlayer().getName(), fightPlayer.getStarNum(), fightPlayer.getScore()));
                                }
                                gangDao.updateGangFight(this.fightDay, JSON.toJSONString(this.gangRankHistoryList), JSON.toJSONString(this.memberRankHistoryList));

                                this.fightState = GangDefine.GANG_FIGHT_STATE_SHOW;
                                this.repeatUpdateState();
                            }
                        } else {
                            List<FightGang> fightGangList = new ArrayList<>();
                            for (FightBattle fightBattle : fightRound.getBattleList()) {
                                if (fightBattle.getFightGangOne().getState() == GangDefine.GANG_FIGHT_GANG_STATE_WIN) {
                                    fightGangList.add(fightBattle.getFightGangOne());
                                }
                                if (fightBattle.getFightGangTwo().getState() == GangDefine.GANG_FIGHT_GANG_STATE_WIN) {
                                    fightGangList.add(fightBattle.getFightGangTwo());
                                }
                            }
                            this.newFightRound(fightGangList, this.getCurrentRound());
                            this.fightTime = currentTime;
                            this.repeatUpdateState();
                        }
                    }
                } else if (this.fightState == GangDefine.GANG_FIGHT_STATE_SHOW) {
                    Date startTime = DateUtil.parseDataTime(this.fightDay + " 23:59:59");
                    this.downTime = startTime.getTime() - currentTime;
                    if (this.downTime < 0) {
                        this.fightReset();
                        this.repeatUpdateState();
                    }
                }
                this.isUpdate = true;
            } catch (Exception e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            } finally {
                this.isUpdate = true;
            }
        }
    }

    private void addFightGang(int index) {
        Gang gang = null;
        if (this.gangList.size() > index) {
            gang = this.gangList.get(index);
        }
        FightGang fightGang;
        if (gang == null || gang.getMemberMap().size() < GangDefine.GANG_FIGHT_MEMBER_NUM) {
            fightGang = new FightGang(null, (byte) index);
            fightGang.setState(GangDefine.GANG_FIGHT_GANG_STATE_OUT);
        } else {
            fightGang = new FightGang(gang, (byte) index);
            fightGang.fightReady();
        }
        this.fightGangList.add(fightGang);

        logger.info("帮战参加帮会 index:" + index + " name:" + fightGang.getGangName());
    }

    private int getCurrentRound() {
        return this.roundList.size() + 1;
    }

    private FightBattle newFightBattle(FightGang fg1, FightGang fg2, int round) {
        FightBattle fightBattle = new FightBattle();
        fightBattle.setFightGangOne(fg1);
        fightBattle.setFightGangTwo(fg2);
        fightBattle.updateGangState();
        return fightBattle;
    }

    private boolean compareBattle(FightGang one, FightGang two) {
        if (one.getState() == GangDefine.GANG_FIGHT_GANG_STATE_FIGHT && two.getState() == GangDefine.GANG_FIGHT_GANG_STATE_FIGHT) {
            if (one.getStarNum() > two.getStarNum()) {
                return true;
            } else if (one.getStarNum() == two.getStarNum()) {
                if (one.getScore() > two.getScore()) {
                    return true;
                } else if (one.getScore() == two.getScore()) {
                    return one.getIndex() < two.getIndex();
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    private void repeatUpdateState() {
        this.isUpdate = true;
        this.updateState();
    }

    private void fightReset() {
        this.fightState = GangDefine.GANG_FIGHT_STATE_NONE;
        this.fightGangList.clear();
        this.roundList.clear();
        this.fightGangRankList.clear();
        this.fightPlayerRankList.clear();
        this.fightPlayerMap.clear();
    }

    private void newFightRound(List<FightGang> fightGangList, int round) {
        for (FightPlayer fightPlayer : this.fightPlayerMap.values()) {
            fightPlayer.reset();
        }

        FightRound fightRound = new FightRound();
        for (int i = 0; i < fightGangList.size(); ++i) {
            FightGang fg1 = fightGangList.get(i);
            if (fg1.getGang() == null || fg1.getGang().getMemberMap().size() < GangDefine.GANG_FIGHT_MEMBER_NUM) {
                fg1.setState(GangDefine.GANG_FIGHT_GANG_STATE_OUT);
            }
            ++i;
            FightGang fg2 = fightGangList.get(i);
            if (fg2.getGang() == null || fg2.getGang().getMemberMap().size() < GangDefine.GANG_FIGHT_MEMBER_NUM) {
                fg2.setState(GangDefine.GANG_FIGHT_GANG_STATE_OUT);
            }
            fg1.setRound((byte) round);
            fg2.setRound((byte) round);
            fightRound.getBattleList().add(this.newFightBattle(fg1, fg2, round));
        }
        this.roundList.add(fightRound);
    }

    public byte getFightState() {
        return fightState;
    }

    public long getDownTime() {
        return downTime;
    }

    public List<FightGang> getFightGangList() {
        return fightGangList;
    }

    public FightRound getCurrentFightRound() {
        if (this.roundList.isEmpty()) {
            return null;
        } else {
            return this.roundList.get(this.roundList.size() - 1);
        }
    }

    public FightPlayer getFightPlayer(Player player) {
        if (this.fightPlayerMap.containsKey(player.getId())) {
            return this.fightPlayerMap.get(player.getId());
        }
        FightPlayer fightPlayer = new FightPlayer(player);
        this.fightPlayerMap.put(player.getId(), fightPlayer);
        this.fightPlayerRankList.add(fightPlayer);
        return fightPlayer;
    }

    public void sortFightPlayerMember() {
        if (isMemberRankUpdate) {
            this.isMemberRankUpdate = false;
            Collections.sort(this.fightPlayerRankList, new SortByFightPlayer());
            this.isMemberRankUpdate = true;
        }
    }

    public List<FightPlayer> getFightPlayerRankList() {
        return fightPlayerRankList;
    }

    public List<FightGangRankData> getGangRankHistoryList() {
        return gangRankHistoryList;
    }

    public List<FightGangRankData> getMemberRankHistoryList() {
        return memberRankHistoryList;
    }
}

class SortByGang implements Comparator<Gang> {

    public SortByGang() {
    }

    public int compare(Gang gang1, Gang gang2) {
        if (gang1.getLevel() > gang2.getLevel()) {
            return -1;
        } else if (gang1.getLevel() == gang2.getLevel()) {
            return Integer.valueOf(gang2.getExp()).compareTo(Integer.valueOf(gang1.getExp()));
        }
        return 1;
    }
}

class SortByFightGang implements Comparator<FightGang> {

    public SortByFightGang() {
    }

    public int compare(FightGang fg1, FightGang fg2) {
        if (fg2.getStarNum() > fg1.getStarNum()) {
            return -1;
        } else if (fg2.getStarNum() == fg1.getStarNum()) {
            if (fg1.getScore() == fg2.getScore()) {
                return Integer.valueOf(fg2.getIndex()).compareTo(Integer.valueOf(fg1.getIndex()));
            } else {
                return Integer.valueOf(fg1.getScore()).compareTo(Integer.valueOf(fg2.getScore()));
            }
        }
        return 1;
    }
}

class SortByFightPlayer implements Comparator<FightPlayer> {

    public SortByFightPlayer() {
    }

    public int compare(FightPlayer fp1, FightPlayer fp2) {
        if (fp1.getStarNum() > fp2.getStarNum()) {
            return -1;
        } else if (fp2.getStarNum() == fp1.getStarNum()) {
            return Integer.valueOf(fp2.getScore()).compareTo(Integer.valueOf(fp1.getScore()));
        }
        return 1;
    }
}
