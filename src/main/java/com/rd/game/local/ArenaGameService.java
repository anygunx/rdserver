package com.rd.game.local;

import com.rd.bean.drop.DropData;
import com.rd.bean.mail.Mail;
import com.rd.bean.player.ArenaChallenge;
import com.rd.bean.rank.PlayerRank;
import com.rd.common.MailService;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.GlobalDao;
import com.rd.define.EGoodsChangeType;
import com.rd.define.ERankType;
import com.rd.define.GameDefine;
import com.rd.game.GameRankManager;
import com.rd.model.KuaFuModel;
import com.rd.model.data.ArenaPersonModelData;
import com.rd.model.data.ArenaServerModelData;
import com.rd.util.DateUtil;
import com.rd.util.StringUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * 竞技场游戏服
 *
 * @author U-Demon Created on 2017年5月17日 下午3:15:34
 * @version 1.0.0
 */
public class ArenaGameService {

    private static final Logger logger = Logger.getLogger(ArenaGameService.class);

    //战力榜前100名参加
    public static final int FIGHT_RANK = 100;
    //周2/4/6开放
    public static final int[] OPEN_DAY = {6};
    public static final long OPEN_STARTTIME = 11 * DateUtil.HOUR - DateUtil.MINUTE;
    public static final long OPEN_ENDTIME = 20 * DateUtil.HOUR;

    //胜负奖励竞技点
    public static final List<DropData> ARENA_WIN_REWARD = new ArrayList<DropData>() {
        private static final long serialVersionUID = 1L;

        {
            add(new DropData(EGoodsType.ARENA, 0, 200));
            add(new DropData(EGoodsType.GOLD, 0, 300000));
        }
    };
    public static final List<DropData> ARENA_LOSE_REWARD = new ArrayList<DropData>() {
        private static final long serialVersionUID = 1L;

        {
            add(new DropData(EGoodsType.ARENA, 0, 60));
            add(new DropData(EGoodsType.GOLD, 0, 100000));
        }
    };

    //开放状态
    public static volatile boolean open = false;
    public static long currRoundStartTime = 0;
    public static volatile long currRoundEndTime = 0;
    public static volatile long nextRoundStartTime = 0;

    //上赛季服务器排名
    public static volatile byte lastServerRank = 0;
    //服务器折扣
    public static volatile byte arenaShopDisc = 0;

    //可参加的玩家列表
    private static List<Integer> fighters = new ArrayList<>();

    //上赛季前5
    public static List<ArenaChallenge> lastArenaRank = new ArrayList<>();

    /**
     * 刷新开放状态
     */
    public static void onUpdateState() {
        long curr = System.currentTimeMillis();
        //今天是周几:0-6
        int dayOfWeek = DateUtil.getWeek() - 1;
        //是否开放
        boolean isOpen = false;
        for (int i = 0; i < OPEN_DAY.length; i++) {
            int day = OPEN_DAY[i];
            //判断是否开放
            if (dayOfWeek == day) {
                long day0 = DateUtil.getDayStartTime(curr);
                long dayStart = day0 + OPEN_STARTTIME;
                long dayEnd = day0 + OPEN_ENDTIME;
                if (curr >= dayStart && curr <= dayEnd) {
                    isOpen = true;
                    currRoundStartTime = dayStart;
                    currRoundEndTime = dayEnd;
                }
            }
        }
        //发放竞技场奖励
        if (open && !isOpen) {
            sendArenaReward();
            refreshShopDisc();
        }
        open = isOpen;
        //清空上赛季排名和折扣
        if (open) {
            lastServerRank = 0;
            arenaShopDisc = 100;
        }
        if (!open) {
            //上赛季前5
            if (lastArenaRank == null || lastArenaRank.size() == 0) {
                String json = GameHttpManager.gi().arenaRankTop();
                lastArenaRank = StringUtil.gson2ListAC(json);
                if (lastArenaRank != null) {
                    for (ArenaChallenge ac : lastArenaRank) {
                        ac.decodeName();
                    }
                }
            }
            //商城折扣
            refreshShopDisc();
            //未开放时计算下一轮开放时间
            int count = 0;
            while (true) {
                curr += DateUtil.HOUR * 3;
                dayOfWeek = DateUtil.getWeek(curr) - 1;
                boolean find = false;
                for (int i = 0; i < OPEN_DAY.length; i++) {
                    int day = OPEN_DAY[i];
                    //判断是否开放
                    if (dayOfWeek == day) {
                        long day0 = DateUtil.getDayStartTime(curr);
                        long dayStart = day0 + OPEN_STARTTIME;
                        long dayEnd = day0 + OPEN_ENDTIME;
                        if (curr >= dayStart && curr <= dayEnd) {
                            nextRoundStartTime = dayStart + 65000;
                            find = true;
                            break;
                        }
                    }
                }
                count++;
                if (find || count >= 56)
                    break;
            }
        }
    }

    private static void refreshShopDisc() {
        if (open) {
            arenaShopDisc = 100;
            return;
        }
        arenaShopDisc = 100;
        //商城折扣
        int i = 1;
        List<Integer> list = new ArrayList<>();
        if (lastArenaRank == null)
            return;
        for (ArenaChallenge ac : lastArenaRank) {
            if (ac.getId() > 0 && GameDefine.containServer(ac.getValue2())) {
//			if (ac.getId() > 0 && GameDefine.getServerId()==ac.getValue2()) {
                list.add(i);
            }
            i++;
            if (i > 3)
                break;
        }
        if (list.size() == 0) {
            arenaShopDisc = 100;
        } else if (list.size() == 1) {
            if (list.get(0) == 1)
                arenaShopDisc = 70;
            else if (list.get(0) == 2)
                arenaShopDisc = 80;
            else if (list.get(0) == 3)
                arenaShopDisc = 90;
        } else if (list.size() == 2) {
            if (list.contains(1)) {
                if (list.contains(2))
                    arenaShopDisc = 55;
                else if (list.contains(3))
                    arenaShopDisc = 60;
            } else if (list.contains(2) && list.contains(3)) {
                arenaShopDisc = 65;
            }
        } else if (list.size() == 3) {
            arenaShopDisc = 50;
        } else {
            arenaShopDisc = 100;
        }
    }

    /**
     * 发放竞技场奖励
     */
    private static void sendArenaReward() {
        try {
            String result = GameHttpManager.gi().arenaRankList();
            if (result.equals("fail")) {
                logger.error("并不发放奖励");
                return;
            }
            String[] rs = result.split(GameHttpManager.SPLIT);
            if (rs.length > 1) {
                List<PlayerRank> list = StringUtil.gson2ListPR(rs[0]);
                lastServerRank = Byte.valueOf(rs[1]);
                //发放全服奖励
                ArenaServerModelData serverModel = KuaFuModel.getArenaServerReward(lastServerRank);
                if (serverModel != null) {
                    Mail mail = MailService.createMail(serverModel.getTitle(), serverModel.getContent(),
                            EGoodsChangeType.ARENA_SERVER_ADD, serverModel.getRewards());
                    MailService.sendGlobalMail(mail);
                }
                //发放个人奖励
                for (int i = 0; i < list.size(); i++) {
                    PlayerRank pr = list.get(i);
                    if (pr != null && pr.getId() > 0 && GameDefine.containServer(pr.getValue2())) {
                        ArenaPersonModelData personModel = KuaFuModel.getArenaPersonReward(i + 1);
                        Mail pm = MailService.createMail(personModel.getTitle(), personModel.getContent(),
                                EGoodsChangeType.ARENA_PERSON_ADD, personModel.getRewards());
                        MailService.sendSystemMail(pr.getId(), pm);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("发放竞技场奖励时发生异常", e);
        }
    }

    /**
     * 刷新可参加玩家列表
     */
    public static void onUpdateFighters() {
        if (!open) {
            fighters.clear();
        }
        if (fighters.size() > 0)
            return;
        //先去数据库中查找
        List<Long> picks = GlobalDao.getInstance().getArenaPick();
        if (picks.size() > 0) {
            //数据未失效
            long time = picks.get(0);
            if (time >= currRoundStartTime && time < currRoundEndTime) {
                for (int i = 1; i < picks.size(); i++) {
                    fighters.add(picks.get(i).intValue());
                }
                return;
            }
        }
        //战力榜
        List<PlayerRank> ranks = GameRankManager.getInstance().getGameRanks(ERankType.FIGHTING);
        if (ranks == null)
            return;
        int rank = 1;
        picks.clear();
        picks.add(System.currentTimeMillis());
        for (PlayerRank pr : ranks) {
            fighters.add(pr.getId());
            picks.add(Long.valueOf(pr.getId()));
            rank++;
            if (rank > FIGHT_RANK)
                break;
        }
        GlobalDao.getInstance().updateArenaPick();
    }

    public static boolean inPicks(int playerId) {
        if (fighters.contains(playerId))
            return true;
        List<PlayerRank> ranks = GameRankManager.getInstance().getGameRanks(ERankType.FIGHTING);
        if (ranks != null) {
            for (PlayerRank rank : ranks) {
                if (rank.getId() == playerId)
                    return true;
            }
        }
        return false;
    }

}
