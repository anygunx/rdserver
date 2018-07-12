package com.rd.activity;

import com.rd.activity.config.BaseActivityConfig;
import com.rd.activity.data.*;
import com.rd.activity.event.*;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * 活动类型
 *
 * @author Created by U-Demon on 2016年11月2日 下午7:16:22
 * @version 1.0.0
 */
public enum EActivityType {
    REBATE(1, "特惠礼包", "tehuilibao", RebateLogicData.class, new RebateEvent()),
    LIMIT_GIFT_VIP(2, "限购礼包", "xiangoulibao", LimitGiftLogicData.class, new LimitGiftVipEvent()),
    LIMIT_GIFT_LV(3, "等级礼包", "chongjijiangli", LimitGiftLogicData.class, new LimitGiftLvEvent()),
    TARGET(4, "达标活动", "open_meiridabiao", TargetLogicData.class, new TargetEvent()),
    LOGON(5, "登录活动", "denglu", LogonLogicData.class, new LogonEvent()),
    PAY_CUMULATE(7, "累计充值", "leijichongzhi", PayCumulateLogicData.class, new PayCumulateEvent(), true),
    PAY_FEAST(8, "充值盛宴", "chongzhishengyan", PayFeastLogicData.class, new PayFeastEvent(), true),
    LEICHONG(13, "累充豪礼", "leichonghaoli", LeiChongLogicData.class, new LeiChongEvent(), true),
    VIPSHOPTL(12, "VIP限时商城", "vipShopXianShi", VipShopLogicData.class, new VipShopXianShiEvent()),
    PAY_CONTINUE(15, "连续充值", "lianxuchongzhi", PayContinueLogicData.class, new PayContinueEvent(), true),
    FEST_LIMIT_GIFT(18, "节日限购包", "xiangoulibao2", LimitGiftLogicData.class, new FestLimitGiftEvent()),
    TURN_TABLE(33, "元宝王者", "yuanbaowangzhe", TurntableLogicData.class, new TurntableEvent(), true),
    TURN_TABLE1(404, "元宝王者1", "yuanbaowangzhe1", TurntableLogicData.class, new TurntableEvent(), true),
    KAM_PO(34, "鉴宝", "xingyunzhuanpan", KamPoLogicData.class, new KamPoEvent()),
    FIVE_ELEMENTS(35, "五行活动", "fiveline", FiveElementLogicData.class, new FiveElementEvent()),
    CONSUM_CUMULATE(36, "固定时间累计消费", "leijixiaofei", ConsumCumulateLogicData.class, new ConsumCumulateFixedEvent(), true),
    PAY_CUMULATE_FIXED109(109, "固定时间累计充值", "dingshichongzhi", PayCumulateLogicData.class, new PayCumulateFixedEvent(109), true, 0),

    FEST_LIMIT_DAILY_GIFT(50, "节日期间每日限购", "xiangoulibao3", LimitGiftLogicData.class, new LimitGiftDailyEvent()),
    FEST_PAY_DAILY_FIRST(51, "节日期间每日首冲", "meirishouchong", PayDailyFirstLogicData.class, new PayDailyFirstEvent()),
    FEST_PAY_CONTINUE(52, "节日期间每日充值", "lianxuchongzhi2", FestPayContinueLogicData.class, new FestPayContinueEvent(), true),
    LUCK_FIRECRACKER(53, "幸运鞭炮", "xingyunbianpao", FirecrackerLogicData.class, new FirecrackerEvent()),
    MONOPOLY(54, "大富翁", "dafuweng", MonopolyLogicData.class, new MonopolyEvent(), true),
    PUZZLE(55, "拼图", "pintu", PuzzleLogicData.class, new PuzzleEvent()),
    NEW_YEAR_LOGON(56, "新年登录", "denglu2", LogonLogicData.class, new LogonEvent2()),
    //	KAM_PO2(57,"鉴宝1","xingyunzhuanpan1",KamPoLogicData2.class, new KamPoEvent2()),
    KAM_PO2(57, "鉴宝2", "xingyunzhuanpan1", KamPoLogicData3.class, new KamPoEvent3()),
    REBATE_N(58, "特惠礼包N", "tehuilibao4", RebateLogicData.class, new RebateEventN()),
    NOREPEATTURNTABLE(59, "消除转盘", "zhizunzhuanpan", NoRepeatTurntableLogicData.class, new NoRepeatTurntableEvent(), true),
    TREASURES(60, "秘宝", "mibao", TreasuresLogicData.class, new TreasuresEvent()),
    SET_WORDS(61, "集字", "jizi", SetWordsLogicData.class, new SetWordsEvent()),
    MONOPOLY1(62, "大富翁1", "dafuweng1", MonopolyLogicData1.class, new MonopolyEvent1(), true),
    DOUBLE(63, "双倍", new DoubleEvent(), true),
    ZHENHUN(64, "镇魂", new ZhenHunEvent(), true),
    INVEST_FUND(65, "投资基金", "touzijijin", InvestFundData.class, new InvestFundEvent(), true),
    SLOT_NEW_MACHINE(66, "新拉霸", "laba_new", SlotMachineData.class, new SlotNewMachineEvent(), false),
    SLOT_MACHINE(67, "拉霸", "laba", SlotMachineData.class, new SlotMachineEvent(), false),
    HALL_FAME(70, "名人堂", new ZhenHunEvent(), true),
    LIMIT_LIMIT_LIMIT(68, "限时限级限购", "open_xiangou", LimitLimitLimitData.class, new LimitLimitLimitEvent(), false),
    SEVEN_DAY(69, "七日开服", "open_qiri", SevenDayLogicData.class, new SevenDayEvent(), false),
    DAILY_ACTIVITY_72(72, "日常活动(指引)", new DailyActivityEvent72(), true),
    PAY_CUMULATE_FIXED400(400, "固定时间累计充值", "dingshichongzhi1", PayCumulateLogicData.class, new PayCumulateFixedEvent1(400), true, 0),
    FEST_PAY_CONTINUE1(401, "节日期间每日充值", "lianxuchongzhi3", FestPayContinueLogicData.class, new FestPayContinueEvent1(), true),
    REBATE_N1(402, "特惠礼包N", "tehuilibao2", RebateLogicData.class, new RebateEventN1()),
    KAM_PO3(403, "鉴宝3", "xingyunzhuanpan2", KamPoLogicData3.class, new KamPoEvent4()),
    DAILY_ACTIVITY_405(405, "日常活动(合服)", new DailyActivityEvent405(), true),
    TARGET_DAILY_CONSUME_CUMULATE(406, "达标每日累计消费", "dabiaorewards", TargetConsumeDaillyCumulateLogicData.class, new TargetDailyConsumeCumulateEvent()),
    // 暂时不用 //
    RED_PACKET(6, "消费红包", "denglu", LogonLogicData.class, new RedPacketEvent()),
    GOLD_TREE(9, "摇钱树", "yaoqianshu", GoldTreeLogicData.class, new GoldTreeEvent()),
    XUNBAO_RANK(10, "寻宝榜", "xunbaobang", XunBaoRankLogicData.class, new XunBaoRankEvent()),
    WISHING_WELL(11, "许愿池", "xuyuanchi", WishingWellLogicData.class, new WishingWellEvent()),
    XUNBAO_RANK2(14, "寻宝榜2", "xunbaobang2", XunBaoRankLogicData.class, new XunBaoRankEvent2()),
    FEST_LOGON(16, "节日登录", "denglu2", LogonLogicData.class, new FestLogonEvent()),
    FEST_REBATE(17, "节日特惠包", "tehuilibao2", RebateLogicData.class, new RebateEvent2()),
    FEST_WISHING_WELL(19, "节日许愿池", "xuyuanchi2", WishingWellLogicData.class, new FestWishingEvent()),
    WEEKEND_TARGET(20, "周末消费达标", "dabiaohuodong2", TargetLogicData.class, new WeekendTargetEvent()),
    FEST_PAY_TARGET(23, "节日充值达标", "dabiaohuodong3", TargetLogicData.class, new FestPayTargetEvent()),
    WEEKEND_LOGON(24, "端午登录", "duanwudenglu", LogonLogicData.class, new WeekendLogonEvent()),
    WEEKEND_REBATE(25, "端午特惠", "duanwutehui", RebateLogicData.class, new RebateEvent3()),
    WEEKEND_LIMIT_GIFT(26, "端午限购", "duanwuxiangou", LimitGiftLogicData.class, new FestLimitGiftEvent2()),
    WEEKEND_WISHING_WELL(27, "端午转盘", "duanwuzhuanpan", WishingWellLogicData.class, new WeekendWishingEvent()),
    FEST_TARGET(28, "节日消费达标", "duanwudabiao", TargetLogicData.class, new FestTargetEvent()),
    DUANWU_CONTINUE(29, "端午连续充值", "duanwuchongzhi", PayContinueLogicData.class, new PayContinueEvent2()),
    WANBA_LOGON(30, "玩吧登录", "wanbazhuanshu", LogonLogicData.class, new WanBaLogonEvent()),
    FUDAI(31, "福袋", "fudai", FuDaiLogicData.class, new FuDaiEvent()),

    REBATE3(10002, "百倍返利", "baibeifanli", RebateLogicData.class, new RebateEvent3()),
    INVEST(10003, "投资计划", "touzijihua", InvestLogicData.class, new InvestEvent()),
    TLSHOP(10004, "限时商城", "xianshishop", TLShopLogicData.class, new TLShopEvent()),
    TURNPLATE(10005, "转盘", "zhuanpan", DialLogicData.class, new DialEvent()),
    TLGIFT(10006, "限时有礼", "xianshiyouli", TLGiftLogicData.class, new TLGiftEvent()),
    RANK(10007, "冲榜奖励", "chongbangjiangli", RankLogicData.class, new RankEvent()),
    VIPSHOPFULI(10008, "VIP福利商城", "vipShopFuLi", VipShopLogicData.class, new VipShopFuLiEvent()),
    CRASHCOW_1(10010, "摇钱树1", "crashcow", DialLogicData.class, new CrashCowEvent()),
    SPRING_WORD_COLLECTION(10011, "新春集字", "springWordCollection", SpringWordCollectionLogicData.class, new SpringWordCollectionEvent()),
    TLHORSE(10012, "限时坐骑", "xianshizuoji", TLHorseLogicData.class, new TLHorseEvent()),
    CRASHCOW_2(10013, "摇钱树2", "crashcow", DialLogicData.class, new CrashCowEvent()),
    SIGN(10014, "春节签到", "signchunjie", SignLogicData.class, new SignEvent()),
    TLSHOP_SPRING(10015, "春节限时商城", "chunjiexianshishop", TLShopLogicData.class, new TLShopSpringEvent()),
    CONSUME(10016, "累计消费", "leijixiaofei", ConsumeLogicData.class, new ConsumeEvent()),
    REBATE2(10018, "返利盛宴", "qianbeifanli", RebateLogicData.class, new RebateEvent2()),
    JEWEL(10019, "至尊宝石", "baoshishilianchou", JewelLogicData.class, new JewelEvent()),
    BUY_CONTINUE(10020, "持续购买", "chixugoumai", BuyContinueLogicData.class, new BuyContinueEvent()),
    BUY_ONE(10021, "一元抢购", "yiyuanqiangou", BuyOneLogicData.class, new BuyOneEvent()),
    SHENTONG(10022, "一折神通", "yizheshentong", ShenTongLogicData.class, new ShenTongEvent()),;

    public static final Map<Integer, EActivityType> valueMap;
    public static final EnumSet<EActivityType> payActivities;

    static {
        valueMap = new HashMap<>();
        for (EActivityType type : EActivityType.values()) {
            valueMap.put(type.id, type);
        }

        payActivities = EnumSet.noneOf(EActivityType.class);
        for (EActivityType type : EActivityType.values()) {
            if (type.isPay) {
                payActivities.add(type);
            }
        }
    }

    //活动ID
    private final int id;
    //活动名称
    private final String name;
    //是否充值活动
    private final boolean isPay;
    //活动具体数据的xml
    private final String xml;
    //活动具体数据的类型
    private Class<? extends BaseActivityLogicData> clazz;
    //活动开始、结束的事件
    private IActivityEvent event;
    //固定时间指定轮次
    private final int round;
    //是否只显示时间
    private final boolean isOnlyShow;

    EActivityType(int id, String name, IActivityEvent event, boolean isOnlyShowActivity) {
        this(id, name, "", null, event, false, -1, isOnlyShowActivity);
    }

    EActivityType(int id, String name, String xml, Class<? extends BaseActivityLogicData> clazz, IActivityEvent event) {
        this(id, name, xml, clazz, event, false, -1, false);
    }

    EActivityType(int id, String name, String xml, Class<? extends BaseActivityLogicData> clazz, IActivityEvent event, boolean isPayActivity) {
        this(id, name, xml, clazz, event, isPayActivity, -1, false);
    }

    EActivityType(int id, String name, String xml, Class<? extends BaseActivityLogicData> clazz, IActivityEvent event, boolean isPayActivity, int round) {
        this(id, name, xml, clazz, event, isPayActivity, round, false);
    }

    EActivityType(int id, String name, String xml, Class<? extends BaseActivityLogicData> clazz, IActivityEvent event, boolean isPayActivity, int round, boolean isOnlyShowActivity) {
        this.id = id;
        this.name = name;
        this.xml = xml;
        this.clazz = clazz;
        this.event = event;
        this.isPay = isPayActivity;
        this.round = round;
        this.isOnlyShow = isOnlyShowActivity;
    }

    public BaseActivityConfig getConfig() {
        return ActivityService.getActivityConfig(this);
    }

    public static EActivityType getType(int id) {
        return valueMap.get(id);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getXml() {
        return xml;
    }

    public Class<? extends BaseActivityLogicData> getClazz() {
        return clazz;
    }

    public IActivityEvent getEvent() {
        return event;
    }

    public int getRound() {
        return round;
    }

    public boolean isOnlyShow() {
        return isOnlyShow;
    }
}
