package com.rd.model;

import com.rd.bean.ladder.LadderRankInfo;
import com.rd.bean.ladder.PlayerLadder;
import com.rd.dao.LadderDao;
import com.rd.define.GameDefine;
import com.rd.define.LadderDefine.LadderRGS;
import com.rd.game.GameWorld;
import com.rd.game.IGameRole;
import com.rd.model.data.LadderModelData;
import com.rd.model.data.LadderSeasonReward;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import com.rd.util.DateUtil;
import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 天梯xml数据
 *
 * @author Created by U-Demon on 2016年11月2日 下午5:00:37
 * @version 1.0.0
 */
public class LadderModel {

    private static Logger log = Logger.getLogger(LadderModel.class);

    //开服当天几点开始--距离0点时间
    public static final long FIRST_TIME = 0 * DateUtil.HOUR;
    //持续多久
    public static final long LAST_TIME = 167 * DateUtil.HOUR;
    //结束之后多久结算奖励
    public static final long REWARD_TIME = 30 * DateUtil.MINUTE;
    //结束之后多久开放
    public static final long START_TIME = 1 * DateUtil.HOUR;

    //赛季开始和结束时间，每天更新
    private static long SEASON_FIRST_TIME = 0;
    public volatile static long SEASON_OPEN_TIME = 0;
    public volatile static long SEASON_CLOSE_TIME = 0;
    public volatile static long SEASON_REWARD_TIME = 0;

    //上赛季战绩
//	public volatile static List<LadderRankInfo> history;
    //排行榜
//	private static Comparator<LadderRankInfo> comparator = new Comparator<LadderRankInfo>() {
//		@Override
//		public int compare(LadderRankInfo o1, LadderRankInfo o2) {
//			if (o1.getPlayerId() == o2.getPlayerId())
//				return 0;
//			if (o1.getScore() <= o2.getScore())
//				return 1;
//			else
//				return -1;
//		}
//	};
    public static List<LadderRankInfo> topList = new LinkedList<>();
    private static ReentrantLock topLock = new ReentrantLock();

    //赛季奖励数据
    private static final String SR_PATH = "gamedata/ttjjc.xml";
    private static final String SR_NAME = "ttjjcModel";
    //段位奖励
    private static Map<Integer, LadderSeasonReward> rankRewardMap;
    //排名奖励
    private static Map<Integer, LadderSeasonReward> orderRewardMap;

    //场次奖励数据
    private static final String FR_PATH = "gamedata/ttre.xml";
    private static final String FR_NAME = "ttreModel";
    private static Map<Integer, Map<Integer, LadderModelData>> ladderMap;
    //星级对应的换算关系
    private static Map<Integer, LadderRGS> RGS;
    //每阶对应的最小星级
    private static Map<Integer, Integer> RMS = new HashMap<>();
    //最大星级数
    public static int MAX_STAR = 0;

    /**
     * 读取数据
     */
    public static void loadData(String path) {
        loadSeasonReward(path);
        loadFightReward(path);
    }

    public static void loadSeasonReward(String path) {
        final File file = new File(path, SR_PATH);
        ResourceListener listener = new ResourceListener() {
            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Integer, LadderSeasonReward> tempRank = new HashMap<>();
                    Map<Integer, LadderSeasonReward> tempOrder = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    //加载奖励
                    Element[] elements = XmlUtils.getChildrenByName(root, "ttjjc");
                    for (int i = 0; i < elements.length; i++) {
                        Element elem = elements[i];
                        LadderSeasonReward data = new LadderSeasonReward();
                        int ranking = Integer.valueOf(XmlUtils.getAttribute(elem, "ranking"));
                        data.setReward(StringUtil.getRewardDropList(XmlUtils.getAttribute(elem, "rewards")));
                        data.setTitle(XmlUtils.getAttribute(elem, "title"));
                        data.setContent(XmlUtils.getAttribute(elem, "content"));
                        //段位奖励
                        if (ranking == 0) {
                            int lv = Integer.valueOf(XmlUtils.getAttribute(elem, "lv"));
                            data.setRank(lv);
                            tempRank.put(data.getRank(), data);
                        } else {
                            data.setRank(ranking);
                            tempOrder.put(data.getRank(), data);
                        }
                    }
                    rankRewardMap = tempRank;
                    orderRewardMap = tempOrder;
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("加载天梯竞技场赛季奖励数据出错...", e);
                }
            }

            @Override
            public String toString() {
                return SR_NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    private static void loadFightReward(String path) {
        final File file = new File(path, FR_PATH);

        ResourceListener listener = new ResourceListener() {
            @Override
            public void onResourceChange(File file) {
                try {
                    Map<Integer, Map<Integer, LadderModelData>> tempRewards = new HashMap<>();
                    Map<Integer, LadderRGS> tempRgs = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    //加载奖励
                    Element[] elements = XmlUtils.getChildrenByName(root, "ttre");
                    //保证表是从低到高的顺序配置的
                    int lastId = -1;
                    int star = 0;
                    for (Element elem : elements) {
                        LadderModelData data = new LadderModelData();
                        int rank = Integer.valueOf(XmlUtils.getAttribute(elem, "type"));
                        int grade = Integer.valueOf(XmlUtils.getAttribute(elem, "lv"));
                        data.setRank(rank);
                        data.setGrade(grade);
                        //判断表的rank grade顺序是否正确
                        if (rank * 100 + grade <= lastId) {
                            throw new IllegalArgumentException("数据表ttre.xml配置的星级顺序必须是从小到大的！！！");
                        }
                        lastId = rank * 100 + grade;
                        int maxStar = Integer.valueOf(XmlUtils.getAttribute(elem, "maxstar"));
                        if (maxStar < 1)
                            maxStar = 1;
                        data.setMaxStar(maxStar);
                        data.setWinReward(StringUtil.getRewardDropList(XmlUtils.getAttribute(elem, "winreward")));
                        data.setLostReward(StringUtil.getRewardDropList(XmlUtils.getAttribute(elem, "lostre")));
                        if (tempRewards.get(rank) == null) {
                            tempRewards.put(rank, new HashMap<Integer, LadderModelData>());
                            RMS.put(rank, star);
                        }
                        tempRewards.get(rank).put(grade, data);
                        for (int i = 0; i < maxStar; i++) {
                            LadderRGS lr = new LadderRGS(rank, grade, i);
                            tempRgs.put(star, lr);
                            star++;
                        }
                    }
                    ladderMap = tempRewards;
                    RGS = tempRgs;
                    MAX_STAR = star - 1;
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("加载天梯竞技场场次奖励数据出错...", e);
                }
            }

            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public String toString() {
                return FR_NAME;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static void refreshSeasonTime() {
        //第一赛季的开始时间
        if (SEASON_FIRST_TIME == 0) {
            // 本周一+偏移
            SEASON_FIRST_TIME = DateUtil.getWeekStartTime(GameDefine.SERVER_CREATE_TIME) + FIRST_TIME;
        }
        long curr = System.currentTimeMillis();
        //赛季数
        int count = (int) ((curr - SEASON_FIRST_TIME) / (LAST_TIME + START_TIME));
        SEASON_OPEN_TIME = SEASON_FIRST_TIME + count * (LAST_TIME + START_TIME);
        SEASON_CLOSE_TIME = SEASON_OPEN_TIME + LAST_TIME;
        SEASON_REWARD_TIME = SEASON_CLOSE_TIME + REWARD_TIME;
    }

    /**
     * 读取榜单数据
     */
    public static void loadRankList() {
        try {
            LadderDao dao = new LadderDao();
//			log.info("从数据库读取天梯竞技场信息开始");
//			List<LadderRankInfo> hisTemp = new ArrayList<>();
//			//历史战绩
//			List<PlayerLadder> historyDatas = dao.getHistory();
//			for (int i = 1; i <= historyDatas.size(); i++)
//			{
//				LadderRankInfo rankInfo = new LadderRankInfo();
//				PlayerLadder ladder = historyDatas.get(i-1);
//				rankInfo.initHis(ladder);
//				rankInfo.setRank(i);
//				hisTemp.add(rankInfo);
//			}
//			history = hisTemp;
            //排行榜
            topLock.lock();
            topList.clear();
            List<PlayerLadder> topDatas = dao.getTopList();
            for (int i = 1; i <= topDatas.size(); i++) {
                LadderRankInfo rankInfo = new LadderRankInfo();
                PlayerLadder ladder = topDatas.get(i - 1);
                rankInfo.initTop(ladder);
                rankInfo.setRank(i);
                topList.add(rankInfo);
            }
            log.info("从数据库读取天梯竞技场信息完毕");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        } finally {
            topLock.unlock();
        }
    }

    public static void refreshRankLadderInfo() {
        try {
            log.info("天梯竞技场排行榜信息更新开始");
            LadderDao dao = new LadderDao();
            topLock.lock();
            //排行榜
            Iterator<LadderRankInfo> ite = topList.iterator();
            int i = 0;
            while (ite.hasNext()) {
                i++;
                LadderRankInfo rankInfo = ite.next();
                if (i > ConstantModel.LADDER_TOP_NUM)
                    break;
                PlayerLadder ladder = dao.getPlayerLadder(rankInfo.getId());
                rankInfo.initTop(ladder);
            }
            log.info("天梯竞技场排行榜信息更新完毕");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        } finally {
            topLock.unlock();
        }
    }

    /**
     * 重新计算排行榜
     *
     * @param myself
     */
    public static void resetTopList(LadderRankInfo myself) {
        try {
            topLock.lock();
            Iterator<LadderRankInfo> ite = topList.iterator();
            while (ite.hasNext()) {
                LadderRankInfo delObj = ite.next();
                if (delObj.getId() == myself.getId()) {
                    topList.remove(delObj);
                    break;
                }
            }
            topList.add(myself);
            Collections.sort(topList);
        } catch (Exception e) {
            log.error("修改排行榜时发生异常", e);
        } finally {
            topLock.unlock();
        }
    }

    /**
     * 刷新排行榜的信息
     */
    public static void refreshRankPlayerInfo() {
        try {
            log.info("天梯竞技场排行榜角色信息更新开始");
            //历史战绩
//			for (LadderRankInfo rankInfo : history)
//			{
//				Player player = GameWorld.getPtr().getPlayer(rankInfo.getPlayerId());
//				if (player != null)
//					rankInfo.init(player);
//			}
            topLock.lock();
            //排行榜
            Iterator<LadderRankInfo> ite = topList.iterator();
            int i = 0;
            while (ite.hasNext()) {
                i++;
                LadderRankInfo rankInfo = ite.next();
                if (i > ConstantModel.LADDER_TOP_NUM)
                    break;
                IGameRole role = GameWorld.getPtr().getGameRole(rankInfo.getId());
                if (role != null)
                    rankInfo.init(role.getPlayer());
            }
            log.info("天梯竞技场排行榜角色信息更新完毕");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        } finally {
            topLock.unlock();
        }
    }

    public static LadderSeasonReward getRankReward(int rank) {
        return rankRewardMap.get(rank);
    }

    public static LadderSeasonReward getOrderReward(int order) {
        return orderRewardMap.get(order);
    }

    public static LadderModelData getFightReward(int rank) {
        return ladderMap.get(rank).get(1);
    }

    /**
     * 获取星级对应的RGS
     *
     * @param star
     * @return
     */
    public static LadderRGS getRGS(int star) {
        return RGS.get(star);
    }

    public static int getRankMinStar(int rank) {
        if (RMS.containsKey(rank))
            return RMS.get(rank);
        if (rank <= 0)
            return 1;
        return MAX_STAR + 1;
    }

}
