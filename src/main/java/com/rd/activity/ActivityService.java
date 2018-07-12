package com.rd.activity;

import com.google.common.collect.ImmutableMap;
import com.rd.activity.config.BaseActivityConfig;
import com.rd.activity.data.BaseActivityLogicData;
import com.rd.activity.event.IActivityEvent;
import com.rd.activity.group.ActivityGroupData;
import com.rd.activity.group.ActivityRoundConfig;
import com.rd.bean.player.Player;
import com.rd.bean.player.PlayerActivity;
import com.rd.dao.ActivityDao;
import com.rd.define.ActivityDefine;
import com.rd.define.GameDefine;
import com.rd.game.GameRankManager;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.game.manager.ActivityManager;
import com.lg.util.StringUtil;
import com.rd.model.WelfareModel;
import com.rd.model.resource.ResourceListener;
import com.rd.model.resource.ResourceManager;
import com.rd.task.ETaskType;
import com.rd.task.Task;
import com.rd.task.TaskManager;
import com.rd.util.DateUtil;
import com.rd.util.XmlUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * 活动服务类--作用域全服
 *
 * @author Created by U-Demon on 2016年11月2日 下午8:14:00
 * @version 1.0.0
 */
public class ActivityService {

    private static Logger logger = Logger.getLogger(ActivityService.class);

    private static final String RES_ACTIVITY = "gamedata/activity.xml";
    private static final String NAME_ACTIVITY = "activity";

    private static ActivityDao dao;

    //活动时间配置
    private static Map<EActivityType, BaseActivityConfig> activityConfigs;

    //活动组
    private static Map<EActivityType, ActivityGroupData<? extends BaseActivityLogicData>> activityGroups = new HashMap<>();

    //每日福利的循环
    private static volatile byte welfareLoop = 0;

    /**
     * 摇钱树每日免费次数
     **/
    public static final int CRASH_COW_DAILY_FREE_TIMES = 1;
    /**
     * 摇钱树活动数据mapping
     **/
    public static final Map<Integer, EActivityType> CRASH_COW_TYPE_MAPPING = ImmutableMap.copyOf(new
                                                                                                         HashMap<Integer, EActivityType>() {
                                                                                                             private static final long serialVersionUID = 1L;

                                                                                                             {
                                                                                                                 put(1, EActivityType.CRASHCOW_1);
                                                                                                                 put(2, EActivityType.CRASHCOW_2);
                                                                                                             }
                                                                                                         });

    /**
     * 活动数据初始化
     */
    public static void init() {
        dao = new ActivityDao();
        updateServerData();
    }

    /**
     * 更新活动的服务器数据
     */
    public static void updateServerData() {
        long curr = System.currentTimeMillis();
        int day = DateUtil.getDistanceDay(GameDefine.SERVER_CREATE_TIME, curr);
        if (day < 1) {
            welfareLoop = 0;
        } else {
            welfareLoop = (byte) ((day - 1) % (WelfareModel.getLoopSize() - 1) + 1);
        }
    }

    /**
     * 读取活动数据
     *
     * @param path
     */
    public static void loadData(String path) {
        loadConfigData(path);
        loadRoundData(path);
    }

    /**
     * 通过活动ID获取活动时间配置
     *
     * @param activityId
     * @return
     */
    public static BaseActivityConfig getActivityConfig(EActivityType activityId) {
        return activityConfigs.get(activityId);
    }

    /**
     * 获取活动组数据
     *
     * @param activityId
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T extends BaseActivityLogicData> ActivityGroupData<T> getGroupData(EActivityType activityId) {
        return (ActivityGroupData<T>) activityGroups.get(activityId);
    }

    /**
     * 获取轮次数据
     *
     * @param activityId
     * @param round
     * @return
     */
    public static <T extends BaseActivityLogicData> Map<String, T> getRoundData(EActivityType activityId, int round) {
        ActivityGroupData<T> group = getGroupData(activityId);
        if (group == null)
            return null;
        return group.getRound(round);
    }

    /**
     * 获取当前轮次数据
     *
     * @param activityId
     * @param playerId
     * @param currTime
     * @return
     */
    public static <T extends BaseActivityLogicData> Map<String, T> getRoundData(
            EActivityType activityId, int playerId, long currTime) {
        ActivityGroupData<T> group = getGroupData(activityId);
        if (group == null)
            return null;
        return group.getCurrRound(playerId, currTime);
    }

    /**
     * 初始化活动相关的{@code Task}
     */
    public static void initActivityTask() {
        long curr = System.currentTimeMillis();
        //获取所有活动的轮次信息
        for (EActivityType id : activityConfigs.keySet()) {
            Map<Integer, ActivityRoundConfig> roundConfigs = getRoundConfigs(id);
            if (roundConfigs == null)
                continue;
            for (final ActivityRoundConfig round : roundConfigs.values()) {
                if (round.getStartTime() > curr) {
                    Task taskStart = new Task() {
                        @Override
                        public void run() {
                            IActivityEvent event = round.getId().getEvent();
                            if (event != null) {
                                event.onStart();
                            }
                        }

                        @Override
                        public String name() {
                            return "activityTaskStart";
                        }
                    };
                    TaskManager.getInstance().scheduleDelayTask(ETaskType.ACTIVITY, taskStart,
                            round.getStartTime() - curr + 1000);
                }
                if (round.getEndTime() > curr) {
                    Task taskEnd = new Task() {
                        @Override
                        public void run() {
                            IActivityEvent event = round.getId().getEvent();
                            if (event != null) {
                                event.onEnd();
                            }
                        }

                        @Override
                        public String name() {
                            return "activityTaskEnd";
                        }
                    };
                    long dalay = round.getEndTime() - curr;
                    if (dalay < DateUtil.DAY * 30) {
                        TaskManager.getInstance().scheduleDelayTask(ETaskType.ACTIVITY, taskEnd, dalay);
                    }
                }
            }
        }
    }

    /**
     * 获取活动所有的轮次配置数据
     *
     * @param id
     * @return
     */
    private static Map<Integer, ActivityRoundConfig> getRoundConfigs(EActivityType id) {
        BaseActivityConfig config = activityConfigs.get(id);
        if (config == null)
            return null;
        //玩家类型的活动，无法统一获取轮次配置数据
        if (config.getType() == ActivityDefine.ACTIVITY_CONFIG_TIME_PLAYER)
            return null;
        Map<Integer, ActivityRoundConfig> roundConfigs = new HashMap<>();
        //单次活动，获取第一轮的数据
        if (!config.isLoop()) {
            roundConfigs.put(0, config.getCurrRound(0, config.getStartTime(0) + config.getKeepTime() / 2));
        }
        //多轮次活动
        else {
//			int maxRound = (int) ((config.getEndTime() - config.getStartTime(0))/
//					(config.getKeepTime() + config.getRestTime()));
            int maxRound = config.getRoundTotal();
            for (int i = 0; i < maxRound; i++) {
                long start = config.getStartTime(0) + i * (config.getKeepTime() + config.getRestTime());
                ActivityRoundConfig round = config.getCurrRound(0, start + config.getKeepTime() / 2);
                if (round != null)
                    roundConfigs.put(i, round);
            }
        }
        return roundConfigs;
    }

    /**
     * 活动每日执行
     */
    public static void dailyTask() {
        try {
            logger.info("更新活动状态");
            dao.cleanActivity();
            for (GameRole role : GameWorld.getPtr().getOnlineRoles().values()) {
                ActivityManager manager = role.getActivityManager();
                PlayerActivity activityData = manager.getActivityData();
                //activityData.getShop().clear();
                activityData.getShopSpring().clear();
                activityData.getTlGift().clear();
                activityData.setCrashcowTimes((byte) 0);
                if (activityData.getLastJewelSingle() > 0)
                    activityData.setLastJewelSingle(1);
                activityData.getBuyOne().clear();
                activityData.getShenTong().clear();
                activityData.getTarget().clear();
                activityData.setGoldTreeNum(0);
                activityData.getGoldTreeReward().clear();
                activityData.setXunbaoCount(0);
                activityData.setFestConsume(0);
                activityData.setFestReward((byte) 0);
                activityData.setWeekendPay(0);
                activityData.setWeekendReward((byte) 0);
                activityData.getWishing().clear();
                activityData.getFestWishing().clear();
                activityData.getGift().clear();
                activityData.resetPayCumulate();
                activityData.resetTurntableData();
                activityData.setTurntableRound(0);
                activityData.resetDailyLimit();
                activityData.setPayDailyFirstStatus(0);
                activityData.resetConsumeCumulate();
                activityData.setMonopoly1PlayedStep(0);
                activityData.clearMonopoly1TodayStepReceive();
                activityData.setMonopoly1ResetNum(0);
                activityData.setMonopoly1FreeNum(0);
//				activityData.resetMonopolyTodayNumReceive();
//				activityData.setMonopolyCurrLevel(0);
//				activityData.resetMonopolyLevelReceived();
//				activityData.setMonopolyTodayPlayLevel(0);
//				activityData.setMonopolyTodayNum(0);
//				activityData.setMonopolyNextLevel(0);
//				activityData.setMonopolyCurrSteps(0);

                EnumSet<EActivityType> actEnum = EnumSet.noneOf(EActivityType.class);
//				role.putMessageQueue(manager.getActivityMsg(EActivityType.INVEST));
//				role.putMessageQueue(manager.getActivityMsg(EActivityType.TLSHOP));
//				role.putMessageQueue(manager.getActivityMsg(EActivityType.TLSHOP_SPRING));
//				role.putMessageQueue(manager.getActivityMsg(EActivityType.TLGIFT));
//				role.putMessageQueue(manager.getActivityMsg(EActivityType.VIPSHOPFULI));
                actEnum.add(EActivityType.VIPSHOPTL);
                //role.putMessageQueue(manager.getActivityMsg(EActivityType.VIPSHOPTL));
//				role.putMessageQueue(manager.getActivityMsg(EActivityType.CRASHCOW_1));
//				role.putMessageQueue(manager.getActivityMsg(EActivityType.CRASHCOW_2));
//				role.putMessageQueue(manager.getActivityMsg(EActivityType.SPRING_WORD_COLLECTION));
//				role.putMessageQueue(manager.getActivityMsg(EActivityType.SIGN));
//				role.putMessageQueue(manager.getActivityMsg(EActivityType.TLHORSE));
//				role.putMessageQueue(manager.getActivityMsg(EActivityType.JEWEL));
//				role.putMessageQueue(manager.getActivityMsg(EActivityType.BUY_ONE));
//				role.putMessageQueue(manager.getActivityMsg(EActivityType.SHENTONG));
                actEnum.add(EActivityType.LIMIT_GIFT_VIP);
                actEnum.add(EActivityType.LOGON);
                actEnum.add(EActivityType.PAY_CUMULATE);
                actEnum.add(EActivityType.PAY_CONTINUE);
                actEnum.add(EActivityType.TURN_TABLE);
                actEnum.add(EActivityType.KAM_PO);
                actEnum.add(EActivityType.FEST_LIMIT_DAILY_GIFT);
                actEnum.add(EActivityType.FEST_PAY_DAILY_FIRST);
                //role.putMessageQueue(manager.getActivityMsg(EActivityType.LIMIT_GIFT_VIP));
                //role.putMessageQueue(manager.getActivityMsg(EActivityType.LOGON));
                //role.putMessageQueue(manager.getActivityMsg(EActivityType.PAY_CUMULATE));
                //role.putMessageQueue(manager.getActivityMsg(EActivityType.PAY_CONTINUE));
                //role.putMessageQueue(manager.getActivityMsg(EActivityType.TARGET));
                //role.putMessageQueue(manager.getActivityMsg(EActivityType.TURN_TABLE));
                //role.putMessageQueue(manager.getActivityMsg(EActivityType.KAM_PO));
                //role.putMessageQueue(manager.getActivityMsg(EActivityType.FEST_LIMIT_DAILY_GIFT));
                //role.putMessageQueue(manager.getActivityMsg(EActivityType.FEST_PAY_DAILY_FIRST));消息内容没有走大厅下发
                actEnum.add(EActivityType.FEST_PAY_CONTINUE);
                actEnum.add(EActivityType.LUCK_FIRECRACKER);
                actEnum.add(EActivityType.CONSUM_CUMULATE);
                actEnum.add(EActivityType.MONOPOLY);
                actEnum.add(EActivityType.PUZZLE);
                actEnum.add(EActivityType.NEW_YEAR_LOGON);
                actEnum.add(EActivityType.KAM_PO2);
                actEnum.add(EActivityType.NOREPEATTURNTABLE);
                actEnum.add(EActivityType.TREASURES);
                actEnum.add(EActivityType.SET_WORDS);
                actEnum.add(EActivityType.MONOPOLY1);
                actEnum.add(EActivityType.ZHENHUN);
                actEnum.add(EActivityType.DOUBLE);
                actEnum.add(EActivityType.HALL_FAME);
                actEnum.add(EActivityType.TARGET);
                actEnum.add(EActivityType.DAILY_ACTIVITY_405);
                actEnum.add(EActivityType.DAILY_ACTIVITY_72);
                actEnum.add(EActivityType.KAM_PO3);
                actEnum.add(EActivityType.PAY_CUMULATE_FIXED400);
                actEnum.add(EActivityType.FEST_PAY_CONTINUE1);
                actEnum.add(EActivityType.REBATE_N1);
                actEnum.add(EActivityType.TURN_TABLE1);
                actEnum.add(EActivityType.TARGET_DAILY_CONSUME_CUMULATE);

                //role.putMessageQueue(manager.getActivityMsg(EActivityType.FEST_PAY_CONTINUE));
                //role.putMessageQueue(manager.getActivityMsg(EActivityType.LUCK_FIRECRACKER));
                //role.putMessageQueue(manager.getActivityMsg(EActivityType.CONSUM_CUMULATE));
                //role.putMessageQueue(manager.getActivityMsg(EActivityType.MONOPOLY));
                //role.putMessageQueue(manager.getActivityMsg(EActivityType.PUZZLE));
                //role.putMessageQueue(manager.getActivityMsg(EActivityType.NEW_YEAR_LOGON));
                //role.putMessageQueue(manager.getActivityMsg(EActivityType.KAM_PO2));
                //role.putMessageQueue(manager.getActivityMsg(EActivityType.NOREPEATTURNTABLE));

//				role.putMessageQueue(manager.getActivityMsg(EActivityType.RED_PACKET));
//				role.putMessageQueue(manager.getActivityMsg(EActivityType.GOLD_TREE));
//				role.putMessageQueue(manager.getActivityMsg(EActivityType.XUNBAO_RANK));
//				role.putMessageQueue(manager.getActivityMsg(EActivityType.XUNBAO_RANK2));
//				role.putMessageQueue(manager.getActivityMsg(EActivityType.WISHING_WELL));
//				role.putMessageQueue(manager.getActivityMsg(EActivityType.VIPSHOPTL));
//				role.putMessageQueue(manager.getActivityMsg(EActivityType.FEST_LOGON));
                actEnum.add(EActivityType.FEST_LIMIT_GIFT);
                actEnum.add(EActivityType.PAY_CUMULATE_FIXED109);
                actEnum.add(EActivityType.LIMIT_LIMIT_LIMIT);
                actEnum.add(EActivityType.SEVEN_DAY);
                //role.putMessageQueue(manager.getActivityMsg(EActivityType.FEST_LIMIT_GIFT));
//				role.putMessageQueue(manager.getActivityMsg(EActivityType.FEST_WISHING_WELL));
//				role.putMessageQueue(manager.getActivityMsg(EActivityType.WEEKEND_WISHING_WELL));
//				role.putMessageQueue(manager.getActivityMsg(EActivityType.WEEKEND_REBATE));
//				role.putMessageQueue(manager.getActivityMsg(EActivityType.WEEKEND_LOGON));
//				role.putMessageQueue(manager.getActivityMsg(EActivityType.WEEKEND_TARGET));
//				role.putMessageQueue(manager.getActivityMsg(EActivityType.FEST_REBATE));
//				role.putMessageQueue(manager.getActivityMsg(EActivityType.FEST_TARGET));
//				role.putMessageQueue(manager.getActivityMsg(EActivityType.FEST_PAY_TARGET));
//				role.putMessageQueue(manager.getActivityMsg(EActivityType.DUANWU_CONTINUE));
//				role.putMessageQueue(manager.getActivityMsg(EActivityType.WANBA_LOGON));
//				role.putMessageQueue(manager.getActivityMsg(EActivityType.WEEKEND_LIMIT_GIFT));
//				role.putMessageQueue(manager.getActivityMsg(EActivityType.FUDAI));

                role.putMessageQueue(role.getActivityManager().getActivityMsg(actEnum));
                role.getActivityManager().putPayDailyFirstMessage();
            }
            if (ActivityService.getRoundData(EActivityType.TARGET_DAILY_CONSUME_CUMULATE,
                    0, System.currentTimeMillis()) != null) {
                Map<Integer, Player> rankData = GameRankManager.getInstance().getRankData();
                GameRankManager.getInstance().updateTargetConsumeRank(rankData.values());
            }
        } catch (Exception e) {
            logger.error("更新活动状态时发生异常.", e);
        }
    }

    /**
     * 读取活动具体数据
     *
     * @param path
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void loadRoundData(String path) {
        for (EActivityType type : activityConfigs.keySet()) {
            if (type.getXml() == null || "".equals(type.getXml())) {
                ActivityGroupData group = new ActivityGroupData(type);
                activityGroups.put(type, group);
                continue;
            }
            String xml = "gamedata/" + type.getXml() + ".xml";
            ActivityGroupData group = new ActivityGroupData(type);
            group.loadXml(path, xml);
            activityGroups.put(type, group);
        }
    }

    /**
     * 读取活动配置文件
     *
     * @param path
     */
    private static void loadConfigData(String path) {
        final File file = new File(path, RES_ACTIVITY);
        ResourceListener listener = new ResourceListener() {

            @Override
            public void onResourceChange(File file) {
                try {
                    Map<EActivityType, BaseActivityConfig> dataMap = new HashMap<>();
                    Document doc = XmlUtils.load(file);
                    Element root = doc.getDocumentElement();
                    Element[] elements = XmlUtils.getChildrenByName(root, "activity");
                    for (int i = 0; i < elements.length; i++) {
                        int id = Integer.valueOf(XmlUtils.getAttribute(elements[i], "id"));
                        if (EActivityType.getType(id) == null)
                            continue;
                        String name = XmlUtils.getAttribute(elements[i], "name");
                        String startTimeStr = XmlUtils.getAttribute(elements[i], "startTime");
                        String endTimeStr = XmlUtils.getAttribute(elements[i], "endTime");
                        boolean loop = Boolean.valueOf(XmlUtils.getAttribute(elements[i], "loop"));
                        float keepDay = Float.valueOf(XmlUtils.getAttribute(elements[i], "keepDay"));
                        float restDay = Float.valueOf(XmlUtils.getAttribute(elements[i], "restDay"));
                        int roundTotal = Integer.valueOf(XmlUtils.getAttribute(elements[i], "roundTime"));

                        String openServerRange = XmlUtils.getAttribute(elements[i], "openServerRange");
                        if (!StringUtil.isEmpty(openServerRange)) {
                            boolean isNotRange = true;
                            String[] ranges = openServerRange.split("#");
                            for (String range : ranges) {
                                String[] openServer = range.split(";");
                                long sStart = DateUtil.parseDataTime(openServer[0]).getTime();
                                long sEnd = DateUtil.parseDataTime(openServer[1]).getTime();
                                if (GameDefine.SERVER_CREATE_TIME > sStart && sEnd > GameDefine.SERVER_CREATE_TIME) {
                                    isNotRange = false;
                                    break;
                                }
                            }
                            if (isNotRange) {
                                startTimeStr = "2018-01-01 00:00:00";
                                endTimeStr = "2018-01-02 00:00:00";
                            }
                        }

                        BaseActivityConfig activity = BaseActivityConfig.createActivityConfig(
                                id, name, startTimeStr, endTimeStr, loop, keepDay, restDay, roundTotal);
                        activity.setOrder(Byte.valueOf(XmlUtils.getAttribute(elements[i], "order")));
                        String servers = XmlUtils.getAttribute(elements[i], "servers");
                        if (servers != null && servers.length() > 0) {
                            activity.initServers(servers);
                        }
                        dataMap.put(activity.getId(), activity);
                    }
                    activityConfigs = ImmutableMap.copyOf(dataMap);
                } catch (Exception e) {
                    logger.error("加载活动数据出错...", e);
                }
            }

            @Override
            public File listenedFile() {
                return file;
            }

            @Override
            public String toString() {
                return NAME_ACTIVITY;
            }
        };
        listener.onResourceChange(file);
        ResourceManager.getInstance().addResourceListener(listener);
    }

    public static int getShopType(int shopKey) {
        return shopKey / 10000;
    }

    public static int getShopId(int shopKey) {
        return shopKey % 10000;
    }

    public static byte getWelfareLoop() {
        return welfareLoop;
    }

    public static int getShopKey(int shopType, int id) {
        return shopType * 10000 + id;
    }

    public static void setWelfareLoop(byte welfareLoop) {
        ActivityService.welfareLoop = welfareLoop;
    }

}
