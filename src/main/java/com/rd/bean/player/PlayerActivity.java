package com.rd.bean.player;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.reflect.TypeToken;
import com.rd.activity.ActivityService;
import com.rd.activity.EActivityType;
import com.rd.activity.data.TreasuresLogicData.BoughtRecord;
import com.rd.bean.data.ShopItem;
import com.rd.util.StringUtil;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 玩家活动数据
 *
 * @author Created by U-Demon on 2016年12月5日 下午5:25:32
 * @version 1.0.0
 */
public class PlayerActivity {

    private ReentrantLock lock = new ReentrantLock();

    //玩家ID
    private int playerId;

    //签到数
    private short signNum;

    //上次签到时间
    private long signTime;

    //登录天数
    private int loginDay = 1;

    //新年登录天数
    private int newYearLoginDay = 0;

    //登录信息  0--未登录   1--登录  2--领取
    private List<Byte> loginInfos = new ArrayList<>();

    //7日活动
    private Map<Short, Activity7Mission> day7Mission = new HashMap<>();

    //月卡结束时间
    private Date monthlyCardEnd;

    //月卡上次领取时间
    private long monthlyCardReward = 0;

    //每日福利记录
    private Map<Byte, Long> welfare = new HashMap<>();

    //百倍返利
    private List<Integer> rebates = new ArrayList<>();

    //投资计划--第0位 购买时间   从1开始   已领取的ID
    private List<Long> invests = new ArrayList<>();

    //商城
    private Map<Integer, Integer> shop = new HashMap<>();
    //春节商城
    private Map<Integer, Integer> shopSpring = new HashMap<>();
    //商城上次刷新时间
    private long playerShopRefresh = 0;
    //商城物品
    private List<ShopItem> playerShopItems = new ArrayList<>();
    //礼包购买记录
    private Map<Integer, Integer> gift = new HashMap<>();
    private List<Byte> lvGift = new ArrayList<>();

    //限时有礼
    private List<Byte> tlGift = new ArrayList<>();
    /**
     * 摇钱次数
     **/
    private byte crashcowTimes = 0;
    /**
     * 限时坐骑
     **/
    private List<Byte> tlHorse = new ArrayList<>();

    //累计消费总额
    private int consume = 0;
    //消费红包
    private List<Integer> redpacket = new ArrayList<>();

    //福袋次数*10000+福袋特殊奖励次数
    private int tzzp = 0;

    //上次宝石单抽时间
    private long lastJewelSingle = 0;

    //一元抢购
    private Map<Integer, Integer> buyOne = new HashMap<>();

    //连续购买。上次购买时间*100+次数
    private long buyContinue;

    //一折神通购买次数
    private List<Integer> shenTong = new ArrayList<>();

    //达标活动领奖
    private List<Byte> target = new ArrayList<>();

    //登录活动领取记录
    private List<Byte> logonRewards = new ArrayList<>();
    //新年登录活动领取记录
    private List<Byte> newYearLogonRewards = new ArrayList<>();

    //充值盛宴领取记录
    private byte payFeast = 0;

    //摇钱树次数
    private short goldTreeNum = 0;
    private List<Byte> goldTreeReward = new ArrayList<>();

    //寻宝次数
    private int xunbaoCount = 0;
    private long xunbaoTime = 0;

    //许愿池数据
    private List<Byte> wishing = new ArrayList<>();
    //节日许愿池
    private List<Byte> festWishing = new ArrayList<>();

    //连续充值领取记录
    private Set<Byte> payConReward = new HashSet<>();
    //节日期间领取记录
    private Set<Byte> festPayConReward = new HashSet<>();
    private List<Byte> payConReward2 = new ArrayList<>();

    //节日登录
    private List<Byte> festLogon = new ArrayList<>();
    private List<Byte> weekendLogon = new ArrayList<>();
    private List<Byte> wanbaLogon = new ArrayList<>();

    //节日登录奖励领取状态
    /**
     * key = 活动id
     * value = 活动领取状态
     */
    private Map<Integer, List<Byte>> festLogonReceived = new HashMap<>();
    //节日登录
    private Map<Integer, List<Byte>> festLogon2 = new HashMap<>();
    //节日消耗元宝
    private int festConsume = 0;
    private long festTime = 0;
    private byte festReward = 0;

    private int weekendPay = 0;
    private long weekendTime = 0;
    private byte weekendReward = 0;

    private int festPay = 0;
    private long festPayTime = 0;
    private byte festPayReward = 0;
    // 累积充值领取状态
    private Set<Integer> payCumulateData = new HashSet<>();
    //元宝王者领取状态
    private int turntableData = 0;
    //元宝王者可领取次数
    private Set<Integer> turntableReceiveNum = new HashSet<>();
    //是否第一次参加元宝王者
    private int turntableRound;
    //每日使用过的订单
    private Map<String, Integer> todayUsedOrder = new HashMap<>();
    // 累计充值领取状态
    private Set<Byte> payCountData = new HashSet<>();
    // 固定时间累积充值领取状态
    private Set<String> payCumulateFixedData = new HashSet<>();

    // 固定时间累积充值领取状态
    private Set<Integer> consumeCumulateFixedData = new HashSet<>();

    //幸运鉴宝所属组
    private int kamPoDay = 0;

    //鉴宝幸运值
    private int kamPoLuckScore = 0;
    //鉴宝2幸运值
    private int kamPo2LuckScore = 0;

    private int firecrackerLuckScore = 0;
    //领取过的幸运值
    private List<Integer> receivedLuckScore = new ArrayList<>();
    //领取过的幸运值2
    private List<Integer> receivedLuckScore2 = new ArrayList<>();
    //领取过的鞭炮幸运值
    private List<Integer> receivedFirecrackerLuckScore = new ArrayList<>();
    //鞭炮抽取次数
    private int firecrackerCount = 0;

    //节日期间每日首冲领奖状态
    private boolean payDailyFirstStatus = false;

    //累计充值达到的天数
    private int festPayConDayCount = 0;

    //大富翁当前层数
    private int monopolyCurrLevel = 0;

    //大富翁今日已玩过的层数
    private int monopolyTodayPlayLevel = 0;

    //大富翁当前层数对应的步数
    private int monopolyCurrSteps = 0;

    //大富翁今日领取次数次数
    private int monopolyTodayNum = 0;

    //大富翁层数已领奖状态
    private List<Integer> monopolyLevelReceived = new ArrayList<>();
    //大富翁每日领取次数
    private List<Integer> monopolyTodayNumReceive = new ArrayList<>();

    //大富翁今日重置次数
    private int monopolyResetNum = 0;

    //大富翁下次层数
    private int monopolyNextLevel = 0;

    //大富翁摇一次筛子 （不存数据库）
    private int diceOne = 0;

    //大富翁摇两次筛子(不存数据库)
    private int diceTwo = 0;

    //大富翁活动期间已用的订单
    private Set<String> monopolyUsedOrder = new HashSet<>();

    //大富翁活动玩家可玩次数
    private int monopolyPlayerTime = 0;

    //步数奖励开始领取id
    private List<Integer> stepIds = new ArrayList<>();

    //拼图剩余领奖次数
    private int puzzleRestTime = 0;
    //已拼的拼图
    private List<Integer> puzzleReceived = new ArrayList<>();

    //消除转盘已领取
    private List<Integer> noRepeatTurntableReceived = new ArrayList<>();

    //消除转盘免费状态(0免费，1非免费)
    private byte free = 0;

    //清除转盘可玩次数
    private int noRepeatTurntableNum = 0;

    //清除转盘幸运值
    private int noRepeatTurntableLuck = 0;

    //清除转盘奖励id
    private List<Integer> noRepeatTurntableAll = new ArrayList<>();

    //清除转盘随机id
    private int noRepeatTurntableRandomId = -1;

    //清除转盘充值超过20的时间
    private long noRepeatTurntablePayTime = 0;

    //清除转盘免费刷新
    private long noRepeatTurntableRefreshTime = 0;

    //清除转盘已达到分段
    private List<Integer> noRepeatTurntableTargeted = new ArrayList<>();

    //清除转盘所在分段id
    private int noRepeatTurnSegmentId = 1;

    private int kamPo2Count = 0;

    private int kamPo2Cost = 0;

    private List<Integer> kamPo2Ids = new ArrayList<>();

    private List<Integer> kamPo2Costs = new ArrayList<>(Arrays.asList(1));

    private int kamPo2RandomNum = 0;

    //秘宝4个道具
    private List<Integer> fourItems = new ArrayList<>();

    //本次刷新购买过的道具(每次刷新会清空)最多4个
    private List<Integer> reBuiedItems = new ArrayList<>();

    //购买过的道具(活动结束清空)
    private List<BoughtRecord> buiedItems = new ArrayList<>();

    //购买过的道具的次数
    private int buiedItemsCount = 0;

    //秘宝免费状态（1：不免费，0：免费）
    private byte treasureFree = 0;

    //秘宝已领取过的代金券（活动结束清空）
    private List<Integer> vouchersList = new ArrayList<>();

    //秘宝积分
    private int treasureIntegral = 0;

    //秘宝剩余代金券
    private int treasureVouchers = 0;

    //秘宝刷新时间
    private long treasuresRefreshTime = 0;

    //集字活动次数
    private Map<Integer, Integer> setWordsNums = new HashMap<>();

    //大富翁1当前层数
    private int monopoly1CurrLevel = 0;

    //大富翁下次层数
    private int monopoly1NextLevel = 0;

    //大富翁活动玩家已玩步数
    private int monopoly1PlayedStep = 0;

    //大富翁当前层数对应的步数
    private int monopoly1CurrSteps = 0;

    //大富翁今日已玩过的层数
    private int monopoly1TodayPlayLevel = 0;

    //大富翁每日步数奖励领取
    private List<Integer> monopoly1TodayStepReceive = new ArrayList<>();

    //大富翁层数已领奖状态
    private List<Integer> monopoly1LevelReceived = new ArrayList<>();

    //大富翁摇一次筛子 （不存数据库）
    private int monopoly1DiceOne = 0;

    //步数奖励开始领取id
    private List<Integer> monopoly1StepIds = new ArrayList<>();
    //投资基金
    private byte investFund;

    private int monopoly1ResetNum = 0;

    private int monopoly1RewardStepId = 0;

    private int monopoly1FreeNum = 0;

    //是否停止状态(不存数据库)
    private byte monopoly1Status = 0;

    //新拉霸
    private byte slotNewMachine;
    //拉霸
    private byte slotMachine;

    //限时限级限购
    private short limitLimitLimit;

    //七日开服活动
    private Map<String, Byte> sevenDay = new HashMap<>();

    public String getLoginJson() {
        StringBuilder sb = new StringBuilder();
        for (byte b : loginInfos) {
            sb.append(b).append(",");
        }
        return sb.toString();
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public short getSignNum() {
        return signNum;
    }

    public void setSignNum(short signNum) {
        this.signNum = signNum;
    }

    public long getSignTime() {
        return signTime;
    }

    public void setSignTime(long signTime) {
        this.signTime = signTime;
    }

    public int getLoginDay() {
        return loginDay;
    }

    public void setLoginDay(int loginDay) {
        this.loginDay = loginDay;
    }

    public void addLoginDay(Player player, long curr) {
        try {
            this.lock.lock();
            this.loginDay++;
            player.setLastLoginTime(curr);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.lock.unlock();
        }
    }

    public int getNewYearLoginDay() {
        return this.newYearLoginDay;
    }

    public void setNewYearLoginDay(int newYearLoginDay) {
        this.newYearLoginDay = newYearLoginDay;
    }

    public void addNewYearLoginDay(Player player, long curr) {
        try {
            this.lock.lock();
            this.newYearLoginDay++;
            player.setLastLoginTime2Fest(curr);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.lock.unlock();
        }
    }

    public List<Byte> getLoginInfos() {
        return loginInfos;
    }

    public void setLoginInfos(List<Byte> loginInfos) {
        this.loginInfos = loginInfos;
    }

    public Map<Short, Activity7Mission> getDay7Mission() {
        return day7Mission;
    }

    public void setDay7MissionJson(String json) {
        if (json != null && json.trim().length() > 0) {
            List<Activity7Mission> missions = JSON.parseArray(json, Activity7Mission.class);
            for (Activity7Mission mission : missions) {
                this.day7Mission.put(mission.getId(), mission);
            }
        }
    }

    public String getDay7MissionJson() {
        return JSON.toJSONString(day7Mission.values());
    }

    public void setDay7Mission(Map<Short, Activity7Mission> day7Mission) {
        this.day7Mission = day7Mission;
    }

    public Date getMonthlyCardEnd() {
        return monthlyCardEnd;
    }

    public void setMonthlyCardEnd(Date monthlyCardEnd) {
        this.monthlyCardEnd = monthlyCardEnd;
    }

    public long getMonthlyCardReward() {
        return monthlyCardReward;
    }

    public void setMonthlyCardReward(long monthlyCardReward) {
        this.monthlyCardReward = monthlyCardReward;
    }

    public Map<Byte, Long> getWelfare() {
        return welfare;
    }

    public void setWelfareJson(String welfareJson) {
        if (welfareJson == null || welfareJson.isEmpty())
            return;
        this.welfare = JSON.parseObject(welfareJson, new TypeReference<Map<Byte, Long>>() {
        });
    }

    public String getWelfareJson() {
        return JSON.toJSONString(this.welfare);
    }

    public List<Integer> getRebates() {
        return rebates;
    }

    public void setRebatesJson(String rebatesJson) {
        if (rebatesJson == null || rebatesJson.isEmpty())
            return;
        this.rebates = JSON.parseObject(rebatesJson, new TypeReference<List<Integer>>() {
        });
    }

    public String getRebatesJson() {
        return JSON.toJSONString(this.rebates);
    }

    public List<Long> getInvests() {
        return invests;
    }

    public void setInvestsJson(String investsJson) {
        if (investsJson == null || investsJson.isEmpty())
            return;
        this.invests = JSON.parseObject(investsJson, new TypeReference<List<Long>>() {
        });
    }

    public String getInvestsJson() {
        return JSON.toJSONString(this.invests);
    }

    public void setShopJson(String shopJson) {
        if (shopJson == null || shopJson.isEmpty())
            return;
        this.shop = JSON.parseObject(shopJson, new TypeReference<Map<Integer, Integer>>() {
        });
    }

    public String getShopJson() {
        return JSON.toJSONString(this.shop);
    }

    public Map<Integer, Integer> getShop() {
        return shop;
    }

    public void setGiftJson(String giftJson) {
        if (giftJson == null || giftJson.isEmpty())
            return;
        this.gift = JSON.parseObject(giftJson, new TypeReference<Map<Integer, Integer>>() {
        });
    }

    public String getGiftJson() {
        return JSON.toJSONString(this.gift);
    }

    public Map<Integer, Integer> getGift() {
        return gift;
    }

    public Set<Integer> getDailyLimitGiftId() {
        Set<Integer> set = new HashSet<>();
        for (Integer id : gift.keySet()) {
            int goodId = id % (2001 * 1000);
            int num = goodId + 2001 * 1000;
            if (id == num) {
                set.add(id);
            }
        }
        return set;
    }

    public void resetDailyLimit() {
        for (Integer id : this.getDailyLimitGiftId()) {
            for (Integer id2 : this.gift.keySet()) {
                if (id == id2) {
                    this.gift.remove(id);
                }
            }
        }
    }

    public long getPlayerShopRefresh() {
        return playerShopRefresh;
    }

    public void setPlayerShopRefresh(long playerShopRefresh) {
        this.playerShopRefresh = playerShopRefresh;
    }

    public List<ShopItem> getPlayerShopItems() {
        return playerShopItems;
    }

    public void setPlayerShopItems(List<ShopItem> playerShopItems) {
        this.playerShopItems = playerShopItems;
    }

    public String getPlayerShopJson() {
        return JSON.toJSONString(this.playerShopItems);
    }

    public void setPlayerShopJson(String json) {
        if (json != null && json.length() > 0)
            this.playerShopItems = JSON.parseObject(json, new TypeReference<List<ShopItem>>() {
            });
    }

    public Map<Integer, Integer> getShopBuy(EActivityType type) {
        Map<Integer, Integer> result = new HashMap<>();
        for (Entry<Integer, Integer> entry : shop.entrySet()) {
            int shopType = ActivityService.getShopType(entry.getKey());
            if (shopType == type.getId()) {
                result.put(ActivityService.getShopId(entry.getKey()), entry.getValue());
            }
        }
        return result;
    }

    public Map<Integer, Integer> getGiftBuy(EActivityType type) {
        Map<Integer, Integer> result = new HashMap<>();
        for (Entry<Integer, Integer> entry : gift.entrySet()) {
            int shopType = ActivityService.getShopType(entry.getKey());
            if (shopType == type.getId()) {
                result.put(ActivityService.getShopId(entry.getKey()), entry.getValue());
            }
        }
        return result;
    }

    public void setShopSpringJson(String shopJson) {
        if (StringUtil.isEmpty(shopJson))
            return;
        this.shopSpring = JSON.parseObject(shopJson, new TypeReference<Map<Integer, Integer>>() {
        });
    }

    public String getShopSpringJson() {
        return JSON.toJSONString(this.shopSpring);
    }

    public Map<Integer, Integer> getShopSpring() {
        return shopSpring;
    }

    public Map<Integer, Integer> getShopSpringBuy(EActivityType type) {
        Map<Integer, Integer> result = new HashMap<>();
        for (Entry<Integer, Integer> entry : shopSpring.entrySet()) {
            int shopType = ActivityService.getShopType(entry.getKey());
            if (shopType == type.getId()) {
                result.put(ActivityService.getShopId(entry.getKey()), entry.getValue());
            }
        }
        return result;
    }

    public void setTlGiftJson(String tlGiftJson) {
        if (tlGiftJson == null || tlGiftJson.isEmpty())
            return;
        this.tlGift = JSON.parseObject(tlGiftJson, new TypeReference<List<Byte>>() {
        });
    }

    public String getTlGiftJson() {
        return JSON.toJSONString(this.tlGift);
    }

    public List<Byte> getTlGift() {
        return tlGift;
    }

    public void setLvGiftJson(String lvGiftJson) {
        if (lvGiftJson == null || lvGiftJson.isEmpty())
            return;
        this.lvGift = JSON.parseObject(lvGiftJson, new TypeReference<List<Byte>>() {
        });
    }

    public String getLvGiftJson() {
        return JSON.toJSONString(this.lvGift);
    }

    public List<Byte> getLvGift() {
        return lvGift;
    }

    public void setBuyOneJson(String buyOneJson) {
        if (buyOneJson == null || buyOneJson.isEmpty())
            return;
        this.buyOne = JSON.parseObject(buyOneJson, new TypeReference<Map<Integer, Integer>>() {
        });
    }

    public String getBuyOneJson() {
        return JSON.toJSONString(this.buyOne);
    }

    public Map<Integer, Integer> getBuyOne() {
        return this.buyOne;
    }

    public long getBuyContinueLast() {
        return this.buyContinue / 100;
    }

    public long getBuyContinueNum() {
        return this.buyContinue % 100;
    }

    public long getBuyContinue() {
        return buyContinue;
    }

    public void setBuyContinue(long last, int num) {
        this.buyContinue = last * 100 + num;
    }

    public void setBuyContinue(long buyContinue) {
        this.buyContinue = buyContinue;
    }

    public void setShenTongJson(String shenTongJson) {
        if (shenTongJson == null || shenTongJson.isEmpty())
            return;
        this.shenTong = JSON.parseObject(shenTongJson, new TypeReference<List<Integer>>() {
        });
    }

    public String getShenTongJson() {
        return JSON.toJSONString(this.shenTong);
    }

    public List<Integer> getShenTong() {
        return this.shenTong;
    }

    public void setTargetJson(String targetJson) {
        if (targetJson == null || targetJson.isEmpty())
            return;
        this.target = JSON.parseObject(targetJson, new TypeReference<List<Byte>>() {
        });
    }

    public String getTargetJson() {
        return JSON.toJSONString(this.target);
    }

    public List<Byte> getTarget() {
        return this.target;
    }

    public void setLogonRewardsJson(String json) {
        if (json == null || json.isEmpty())
            return;
        this.logonRewards = JSON.parseObject(json, new TypeReference<List<Byte>>() {
        });
    }

    public String getLogonRewardsJson() {
        return JSON.toJSONString(this.logonRewards);
    }

    public List<Byte> getLogonRewards() {
        return logonRewards;
    }

    public void setGoldTreeRewardJson(String json) {
        if (json == null || json.isEmpty())
            return;
        this.goldTreeReward = JSON.parseObject(json, new TypeReference<List<Byte>>() {
        });
    }

    public String getGoldTreeRewardJson() {
        return JSON.toJSONString(this.goldTreeReward);
    }

    public List<Byte> getGoldTreeReward() {
        return goldTreeReward;
    }

    public short getGoldTreeNum() {
        return goldTreeNum;
    }

    public void setGoldTreeNum(int goldTreeNum) {
        this.goldTreeNum = (short) goldTreeNum;
    }

    public void addGoldTreeNum() {
        this.goldTreeNum++;
    }

    public byte getCrashcowTimes() {
        return crashcowTimes;
    }

    public void setCrashcowTimes(byte crashcowTimes) {
        this.crashcowTimes = crashcowTimes;
    }

    public List<Byte> getTLHorseList() {
        return tlHorse;
    }

    public void setTlHorseJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.tlHorse = JSON.parseObject(json, new TypeReference<List<Byte>>() {
            });
        }
    }

    public String getTlHorseJson() {
        return JSON.toJSONString(this.tlHorse);
    }

    public List<Integer> getRedpacket() {
        return redpacket;
    }

    public void setRedpacketJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.redpacket = JSON.parseObject(json, new TypeReference<List<Integer>>() {
            });
        }
    }

    public String getRedpacketJson() {
        return JSON.toJSONString(this.redpacket);
    }

    public int getRedpacketTotal() {
        if (this.redpacket.size() < 2)
            return 0;
        int total = 0;
        for (int i = 1; i < this.redpacket.size(); i++) {
            total += this.redpacket.get(i);
        }
        return total;
    }

    public int getConsume() {
        return consume;
    }

    public void setConsume(int consume) {
        this.consume = consume;
    }

    public void addConsume(int add) {
        this.consume += add;
    }

    public int getTzzp() {
        return tzzp;
    }

    public void setTzzp(int tzzp) {
        this.tzzp = tzzp;
    }

    public long getLastJewelSingle() {
        return lastJewelSingle;
    }

    public void setLastJewelSingle(long lastJewelSingle) {
        this.lastJewelSingle = lastJewelSingle;
    }

    public byte getPayFeast() {
        return payFeast;
    }

    public void setPayFeast(byte payFeast) {
        this.payFeast = payFeast;
    }

    public int getXunbaoCount() {
        return xunbaoCount;
    }

    public void setXunbaoCount(int xunbaoCount) {
        this.xunbaoCount = xunbaoCount;
    }

    public void addXunbaoCount(int add) {
        this.xunbaoCount += add;
    }

    public long getXunbaoTime() {
        return xunbaoTime;
    }

    public void setXunbaoTime(long xunbaoTime) {
        this.xunbaoTime = xunbaoTime;
    }

    public List<Byte> getWishing() {
        return wishing;
    }

    public void setWishing(List<Byte> list) {
        this.wishing = list;
    }

    public void setWishingJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.wishing = JSON.parseObject(json, new TypeReference<List<Byte>>() {
            });
        }
    }

    public String getWishingJson() {
        return JSON.toJSONString(this.wishing);
    }

    public void setFestWishingJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.festWishing = JSON.parseObject(json, new TypeReference<List<Byte>>() {
            });
        }
    }

    public String getFestWishingJson() {
        return JSON.toJSONString(this.festWishing);
    }

    public List<Byte> getFestWishing() {
        return festWishing;
    }

    public void setFestWishing(List<Byte> festWishing) {
        this.festWishing = festWishing;
    }

    public Set<Byte> getPayConReward() {
        return payConReward;
    }

    public Set<Byte> getFestPayConReward() {
        return festPayConReward;
    }

    public void setPayConRewardJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.payConReward = JSON.parseObject(json, new TypeReference<Set<Byte>>() {
            });
        }
    }

    public void setFestPayConRewardJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.festPayConReward = JSON.parseObject(json, new TypeReference<Set<Byte>>() {
            });
        }
    }

    public String getPayConRewardJson() {
        return JSON.toJSONString(this.payConReward);
    }

    public String getFestPayConRewardJson() {
        return JSON.toJSONString(this.festPayConReward);
    }

    public List<Byte> getPayConReward2() {
        return payConReward2;
    }

    public void setPayConReward2Json(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.payConReward2 = JSON.parseObject(json, new TypeReference<List<Byte>>() {
            });
        }
    }

    public String getPayConReward2Json() {
        return JSON.toJSONString(this.payConReward2);
    }

    public String getFestLogonJson() {
        return JSON.toJSONString(this.festLogon);
    }

    public void setFestLogonJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.festLogon = JSON.parseObject(json, new TypeReference<List<Byte>>() {
            });
        }
    }

    public List<Byte> getFestLogon() {
        return festLogon;
    }

    public List<Byte> getWeekendLogon() {
        return weekendLogon;
    }

    public String getWeekendLogonJson() {
        return JSON.toJSONString(this.weekendLogon);
    }

    public void setWeekendLogonJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.weekendLogon = JSON.parseObject(json, new TypeReference<List<Byte>>() {
            });
        }
    }

    public String getWanbaLogonJson() {
        return JSON.toJSONString(this.wanbaLogon);
    }

    public void setWanbaLogonJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.wanbaLogon = JSON.parseObject(json, new TypeReference<List<Byte>>() {
            });
        }
    }

    public List<Byte> getWanbaLogon() {
        return wanbaLogon;
    }

    public byte getFestReward() {
        return festReward;
    }

    public void setFestReward(byte festReward) {
        this.festReward = festReward;
    }

    public void addFestReward() {
        this.festReward++;
    }

    public int getFestConsume() {
        return festConsume;
    }

    public void setFestConsume(int festConsume) {
        this.festConsume = festConsume;
    }

    public void addFestConsume(int consume) {
        this.festConsume += consume;
    }

    public long getFestTime() {
        return festTime;
    }

    public void setFestTime(long festTime) {
        this.festTime = festTime;
    }

    public int getWeekendPay() {
        return weekendPay;
    }

    public void setWeekendPay(int weekendPay) {
        this.weekendPay = weekendPay;
    }

    public void addWeekendPay(int add) {
        this.weekendPay += add;
    }

    public long getWeekendTime() {
        return weekendTime;
    }

    public void setWeekendTime(long weekendTime) {
        this.weekendTime = weekendTime;
    }

    public byte getWeekendReward() {
        return weekendReward;
    }

    public void setWeekendReward(byte weekendReward) {
        this.weekendReward = weekendReward;
    }

    public void addWeekendReward() {
        this.weekendReward++;
    }

    public int getFestPay() {
        return festPay;
    }

    public void setFestPay(int festPay) {
        this.festPay = festPay;
    }

    public void addFestPay(int add) {
        this.festPay += add;
    }

    public long getFestPayTime() {
        return festPayTime;
    }

    public void setFestPayTime(long festPayTime) {
        this.festPayTime = festPayTime;
    }

    public byte getFestPayReward() {
        return festPayReward;
    }

    public void setFestPayReward(byte festPayReward) {
        this.festPayReward = festPayReward;
    }

    public void addFestPayReward() {
        this.festPayReward++;
    }

    public Set<Integer> getPayCumulateData() {
        return payCumulateData;
    }

    public Set<Integer> getTurntableReceiveNum() {
        return turntableReceiveNum;
    }

    public void addTurntableReceivceNum(int cost) {
        turntableReceiveNum.add(cost);
    }

    public boolean isPayCumulateReceived(int cost) {
        return payCumulateData.contains(cost);
    }

    public void receivePayCumulate(int cost) {
        payCumulateData.add(cost);
    }

    public void receiveTurntableData(int cost) {
        turntableData = cost;
        turntableReceiveNum.remove(cost);
    }

    public String getTurntableReceiveNumJson() {
        return StringUtil.obj2Gson(turntableReceiveNum);
    }

    public void setTurntableReceiveNumJson(String str) {
        if (StringUtil.isEmpty(str)) {
            return;
        }
        this.turntableReceiveNum = StringUtil.gson2set(str, new TypeToken<Set<Integer>>() {
        });
    }

    public String getPayCumulateJson() {
        return StringUtil.obj2Gson(payCumulateData);
    }

    public void setPayCumulateJson(String str) {
        if (StringUtil.isEmpty(str)) {
            return;
        }
        this.payCumulateData = StringUtil.gson2set(str, new TypeToken<Set<Integer>>() {
        });
    }

    public void resetPayCumulate() {
        this.payCumulateData.clear();
    }

    public int getTurntableData() {
        return turntableData;
    }

    public String getTurntableJson() {
        return StringUtil.obj2Gson(turntableData);
    }

    public boolean isTurnTableReceived(int cost) {
        return turntableData > cost || !turntableReceiveNum.contains(cost);
    }

    public void receiveTurntable(int cost) {
        turntableData = cost;
    }

    public void setTurntableData(int cost) {
        turntableData = cost;
    }

    public void resetTurntableData() {
        turntableData = 0;
        turntableReceiveNum.clear();
        todayUsedOrder.clear();
    }

    public Set<Byte> getPayCountData() {
        return payCountData;
    }

    public boolean isPayCountReceived(byte day) {
        return payCountData.contains(day);
    }

    public void receivePayCount(byte day) {
        payCountData.add(day);
    }

    public String getPayCountJson() {
        return StringUtil.obj2Gson(payCountData);
    }

    public void setPayCountJson(String str) {
        if (StringUtil.isEmpty(str)) {
            return;
        }
        this.payCountData = StringUtil.gson2set(str, new TypeToken<Set<Byte>>() {
        });
    }

    public Set<String> getPayCumulateFixedData() {
        return payCumulateFixedData;
    }

    public boolean isPayCumulateFixedReceived(String key) {
        return payCumulateFixedData.contains(key);
    }

    public void receivePayCumulateFixed(String key) {
        payCumulateFixedData.add(key);
    }

    public String getPayCumulateFixedJson() {
        return StringUtil.obj2Gson(payCumulateFixedData);
    }

    public void setPayCumulateFixedJson(String str) {
        if (StringUtil.isEmpty(str)) {
            return;
        }
        this.payCumulateFixedData = StringUtil.gson2set(str, new TypeToken<Set<String>>() {
        });
    }

    public Set<Integer> getConsumeCumulateFixedData() {
        return consumeCumulateFixedData;
    }

    public boolean isConsumeCumulateFixedReceived(Integer key) {
        return consumeCumulateFixedData.contains(key);
    }

    public void receiveConsumeCumulateFixed(Integer key) {
        consumeCumulateFixedData.add(key);
    }

    public String getConsumeCumulateFixedJson() {
        return StringUtil.obj2Gson(consumeCumulateFixedData);
    }

    public void setConsumeCumulateFixedJson(String str) {
        if (StringUtil.isEmpty(str)) {
            return;
        }
        this.consumeCumulateFixedData = StringUtil.gson2set(str, new TypeToken<Set<Integer>>() {
        });
    }

    public void resetConsumeCumulate() {
        this.consumeCumulateFixedData.clear();
    }

    public int getTurntableRound() {
        return turntableRound;
    }

    public void setTurntableRound(int turntableRound) {
        this.turntableRound = turntableRound;
    }

    public Map<String, Integer> getTodayUsedOrder() {
        return todayUsedOrder;
    }

    public void setTodayUsedOrder(Map<String, Integer> todayUsedOrder) {
        this.todayUsedOrder = todayUsedOrder;
    }

    public String getTodayUsedOrderJson() {
        return StringUtil.obj2Gson(todayUsedOrder);
    }

    public Map<String, Integer> setTodayUsedOrderJson(String str) {
        if (StringUtil.isEmpty(str)) {
            return new HashMap<>();
        }
        this.todayUsedOrder = StringUtil.gson2Map(str, new TypeToken<Map<String, Integer>>() {
        });
        return this.todayUsedOrder;
    }

    public int getKamPoLuckScore() {
        return kamPoLuckScore;
    }

    public int getKamPo2LuckScore() {
        return kamPo2LuckScore;
    }

    public void setKamPoLuckScore(int kamPoLuckScore) {
        this.kamPoLuckScore = kamPoLuckScore;
    }

    public void setKamPo2LuckScore(int kamPo2LuckScore) {
        this.kamPo2LuckScore = kamPo2LuckScore;
    }

    public int addKamPoLuckScore(int kamPoLuckScore) {
        return this.kamPoLuckScore = this.kamPoLuckScore + kamPoLuckScore;
    }

    public int addKamPo2LuckScore(int kamPo2LuckScore) {
        return this.kamPo2LuckScore = this.kamPo2LuckScore + kamPo2LuckScore;
    }

    public int getFirecrackerLuckScore() {
        return firecrackerLuckScore;
    }

    public void setFirecrackerLuckScore(int firecrackerLuckScore) {
        this.firecrackerLuckScore = firecrackerLuckScore;
    }

    public int addFirecrackerLuckScore(int firecrackerLuckScore) {
        return this.firecrackerLuckScore = this.firecrackerLuckScore + firecrackerLuckScore;
    }

    public int getKamPoDay() {
        return kamPoDay;
    }

    public void setKamPoDay(int kamPoDay) {
        this.kamPoDay = kamPoDay;
    }

    public boolean addReceivedFirecrackerLuckScore(Integer score) {
        if (this.receivedFirecrackerLuckScore.contains(score)) {
            return false;
        }
        return this.receivedFirecrackerLuckScore.add(score);
    }

    public List<Integer> getReceivedFirecrackerLuckScore() {
        return this.receivedFirecrackerLuckScore;
    }

    public String getReceivedFirecrackerLuckScoreJson() {
        return StringUtil.obj2Gson(this.receivedFirecrackerLuckScore);
    }

    public void resetReceivedFirecrackerLuckScore() {
        this.receivedFirecrackerLuckScore.clear();
    }

    public List<Integer> setReceivedFirecrackerLuckScore(String str) {
        if (StringUtil.isEmpty(str)) {
            return new ArrayList();
        }
        this.receivedFirecrackerLuckScore = new ArrayList(StringUtil.gson2List(str, new TypeToken<List<Integer>>() {
        }));
        return this.receivedFirecrackerLuckScore;
    }

    public boolean addReceivedLuckScore(Integer score) {
        if (this.receivedLuckScore.contains(score)) {
            return false;
        }
        return this.receivedLuckScore.add(score);
    }

    public boolean addReceivedLuckScore2(Integer score) {
        if (this.receivedLuckScore2.contains(score)) {
            return false;
        }
        return this.receivedLuckScore2.add(score);
    }

    public List<Integer> getReceivedLuckScore() {
        return this.receivedLuckScore;
    }

    public List<Integer> getReceivedLuckScore2() {
        return this.receivedLuckScore2;
    }

    public String getReceivedLuckScoreJson() {
        return StringUtil.obj2Gson(this.receivedLuckScore);
    }

    public String getReceivedLuckScore2Json() {
        return StringUtil.obj2Gson(this.receivedLuckScore2);
    }

    public void resetReceivedLuckScore() {
        this.receivedLuckScore.clear();
    }

    public void resetReceivedLuckScore2() {
        this.receivedLuckScore2.clear();
    }

    public List<Integer> setReceivedLuckScore(String str) {
        if (StringUtil.isEmpty(str)) {
            return new ArrayList();
        }
        this.receivedLuckScore = new ArrayList(StringUtil.gson2List(str, new TypeToken<List<Integer>>() {
        }));
        return this.receivedLuckScore;
    }

    public List<Integer> setReceivedLuckScore2(String str) {
        if (StringUtil.isEmpty(str)) {
            return new ArrayList();
        }
        this.receivedLuckScore2 = new ArrayList(StringUtil.gson2List(str, new TypeToken<List<Integer>>() {
        }));
        return this.receivedLuckScore2;
    }

    public boolean isPayDailyFirstStatus() {
        return payDailyFirstStatus;
    }

    public void setPayDailyFirstStatus(int i) {
        if (i == 1) {
            this.payDailyFirstStatus = true;
        } else {

            this.payDailyFirstStatus = false;
        }
    }

    public int getFestPayConDayCount() {
        return festPayConDayCount;
    }

    public void setFestPayConDayCount(int festPayConDayCount) {
        this.festPayConDayCount = festPayConDayCount;
    }

    public int getFirecrackerCount() {
        return firecrackerCount;
    }

    public void resetFirecrackerCount() {
        this.firecrackerCount = 0;
    }

    public void firecrackerCountAutoIncrement() {
        this.firecrackerCount++;
    }

    public void setFirecrackerCount(int firecrackerCount) {
        this.firecrackerCount = firecrackerCount;
    }

    public int getMonopolyCurrLevel() {
        return monopolyCurrLevel;
    }

    public void setMonopolyCurrLevel(int monopolyCurrLevel) {
        this.monopolyCurrLevel = monopolyCurrLevel;
    }

    public int getMonopolyCurrSteps() {
        return monopolyCurrSteps;
    }

    public void setMonopolyCurrSteps(int monopolyCurrSteps) {
        this.monopolyCurrSteps = monopolyCurrSteps;
    }

    public int getMonopolyTodayNum() {
        return monopolyTodayNum;
    }

    public void setMonopolyTodayNum(int monopolyTodayNum) {
        this.monopolyTodayNum = monopolyTodayNum;
    }

    public List<Integer> getMonopolyLevelReceived() {
        return monopolyLevelReceived;
    }

    public void setMonopolyLevelReceived(List<Integer> monopolyLevelReceived) {
        this.monopolyLevelReceived = monopolyLevelReceived;
    }

    public void setMonopolyLevelReceivedJson(String str) {
        if (!StringUtil.isEmpty(str)) {
            this.monopolyLevelReceived = StringUtil.gson2List(str, new TypeToken<List<Integer>>() {
            });
        }
    }


    public void addMonopolyLevelReceived(Integer level) {
        this.monopolyLevelReceived.add(level);
    }

    public String getMonopolyLevelReceivedJson() {
        return StringUtil.obj2Gson(this.monopolyLevelReceived);
    }

    public void resetMonopolyLevelReceived() {
        this.monopolyLevelReceived.clear();
    }

    public void resetMonopolyTodayNumReceive() {
        this.monopolyTodayNumReceive.clear();
    }

    public List<Integer> getMonopolyTodayNumReceive() {
        return this.monopolyTodayNumReceive;
    }

    public void setMonopolyTodayNumReceive(List<Integer> monopolyTodayNumReceive) {
        this.monopolyTodayNumReceive = monopolyTodayNumReceive;
    }

    public void addMonopolyTodayNumReceive(int num) {
        this.monopolyTodayNumReceive.add(num);
    }

    public void removeMonopolyTodayReceive(Integer num) {
        this.monopolyTodayNumReceive.remove(num);
    }

    public String getMonopolyTodayNumReceiveJson() {
        return StringUtil.obj2Gson(this.monopolyTodayNumReceive);
    }

    public void setMonopolyTodayNumReceiveJson(String str) {
        if (!StringUtil.isEmpty(str)) {
            this.monopolyTodayNumReceive = StringUtil.gson2List(str, new TypeToken<List<Integer>>() {
            });
        }
    }

    public int getMonopolyTodayPlayLevel() {
        return monopolyTodayPlayLevel;
    }

    public void setMonopolyTodayPlayLevel(int monopolyTodayPlayLevel) {
        this.monopolyTodayPlayLevel = monopolyTodayPlayLevel;
    }

    public int getMonopolyResetNum() {
        return monopolyResetNum;
    }

    public void setMonopolyResetNum(int monopolyResetNum) {
        this.monopolyResetNum = monopolyResetNum;
    }

    public int getMonopolyNextLevel() {
        return monopolyNextLevel;
    }

    public void setMonopolyNextLevel(int monopolyNextLevel) {
        this.monopolyNextLevel = monopolyNextLevel;
    }

    public int getDiceOne() {
        return diceOne;
    }

    public void setDiceOne(int diceOne) {
        this.diceOne = diceOne;
    }

    public int getDiceTwo() {
        return diceTwo;
    }

    public void setDiceTwo(int diceTwo) {
        this.diceTwo = diceTwo;
    }

    public List<Integer> getStepIds() {
        return stepIds;
    }

    public void setStepIds(List<Integer> stepIds) {
        this.stepIds = stepIds;
    }

    public void addStepId(int step) {
        this.stepIds.add(step);
    }

    public void resetStepIds() {
        this.stepIds.clear();
    }

    public int getPuzzleRestTime() {
        return puzzleRestTime;
    }

    public void setPuzzleRestTime(int puzzleRestTime) {
        this.puzzleRestTime = puzzleRestTime;
    }

    public List<Integer> getPuzzleReceived() {
        return puzzleReceived;
    }

    public void setPuzzleReceived(List<Integer> puzzleReceived) {
        this.puzzleReceived = puzzleReceived;
    }

    public void addPuzzleReceived(int receive) {
        this.puzzleReceived.add(receive);
    }

    public void resetPuzzleReceived() {
        this.puzzleReceived.clear();
    }

    public String getPuzzleReceivedJson() {
        return StringUtil.obj2Gson(this.puzzleReceived);
    }

    public void setPuzzleReceivedJson(String str) {
        if (!StringUtil.isEmpty(str)) {
            this.puzzleReceived = StringUtil.gson2List(str, new TypeToken<List<Integer>>() {
            });
        }
    }

    public Map<Integer, List<Byte>> getFestLogonReceived() {
        return festLogonReceived;
    }

    public String getFestLogonReceivedJson() {
        return StringUtil.obj2Gson(this.festLogonReceived);
    }

    public void setFestLogonReceivedStr(String str) {
        if (!StringUtil.isEmpty(str)) {
            this.festLogonReceived = StringUtil.gson2Map(str, new TypeToken<Map<Integer, List<Byte>>>() {
            });
        }
    }

    public void setFestLogonReceived(Map<Integer, List<Byte>> festLogonReceived) {
        this.festLogonReceived = festLogonReceived;
    }

    public Map<Integer, List<Byte>> getFestLogon2() {
        return festLogon2;
    }

    public void setFestLogon2(Map<Integer, List<Byte>> festLogon2) {
        this.festLogon2 = festLogon2;
    }

    public String getFestLogon2Json() {
        return StringUtil.obj2Gson(this.festLogon2);
    }

    public void setFestLogon2Str(String str) {
        if (!StringUtil.isEmpty(str)) {
            this.festLogon2 = StringUtil.gson2Map(str, new TypeToken<Map<Integer, List<Byte>>>() {
            });
        }
    }


    public void setNewYearLogonRewardsJson(String json) {
        if (json == null || json.isEmpty())
            return;
        this.newYearLogonRewards = JSON.parseObject(json, new TypeReference<List<Byte>>() {
        });
    }

    public String getNewYearLogonRewardsJson() {
        return JSON.toJSONString(this.newYearLogonRewards);
    }

    public List<Byte> getNewYearLogonRewards() {
        return newYearLogonRewards;
    }

    public Set<String> getMonopolyUsedOrder() {
        return monopolyUsedOrder;
    }

    public String getMonopolyUsedOrderStr() {
        return StringUtil.obj2Gson(this.monopolyUsedOrder);
    }

    public void setMonopolyUsedOrder(Set<String> monopolyUsedOrder) {
        this.monopolyUsedOrder = monopolyUsedOrder;
    }

    public void setMonopolyUsedOrderJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.monopolyUsedOrder = StringUtil.gson2set(json, new TypeToken<Set<String>>() {
            });
        }
    }

    public int getMonopolyPlayerTime() {
        return monopolyPlayerTime;
    }

    public void setMonopolyPlayerTime(int monopolyPlayerTime) {
        this.monopolyPlayerTime = monopolyPlayerTime;
    }

    public String getNoRepeatTurntableReceivedStr() {
        return StringUtil.obj2Gson(this.noRepeatTurntableReceived);
    }

    public List<Integer> getNoRepeatTurntableReceived() {
        return noRepeatTurntableReceived;
    }

    public void setNoRepeatTurntableReceivedJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.noRepeatTurntableReceived = StringUtil.gson2List(json, new TypeToken<List<Integer>>() {
            });
        }
    }

    public void setNoRepeatTurntableReceived(List<Integer> noRepeatTurntableReceived) {
        this.noRepeatTurntableReceived = noRepeatTurntableReceived;
    }

    public void clearNoRepeatTurntableReceived() {
        this.noRepeatTurntableReceived.clear();
    }

    public byte getFree() {
        return free;
    }

    public void setFree(byte free) {
        this.free = free;
    }

    public int getNoRepeatTurntableNum() {
        return noRepeatTurntableNum;
    }

    public void setNoRepeatTurntableNum(int noRepeatTurntableNum) {
        this.noRepeatTurntableNum = noRepeatTurntableNum;
    }

    public int getNoRepeatTurntableLuck() {
        return noRepeatTurntableLuck;
    }

    public void setNoRepeatTurntableLuck(int noRepeatTurntableLuck) {
        this.noRepeatTurntableLuck = noRepeatTurntableLuck;
    }

    public List<Integer> getNoRepeatTurntableAll() {
        return noRepeatTurntableAll;
    }

    public String getNoRepeatTurntableAllStr() {
        return StringUtil.obj2Gson(this.noRepeatTurntableAll);
    }

    public void setNoRepeatTurntableAll(List<Integer> noRepeatTurntableAll) {
        this.noRepeatTurntableAll = noRepeatTurntableAll;
    }

    public void setNoRepeatTurntableAllJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.noRepeatTurntableAll = StringUtil.gson2List(json, new TypeToken<List<Integer>>() {
            });
        }
    }

    public void clearNoRepeatTurntableAll() {
        this.noRepeatTurntableAll.clear();
    }

    public int getNoRepeatTurntableRandomId() {
        return noRepeatTurntableRandomId;
    }

    public void setNoRepeatTurntableRandomId(int noRepeatTurntableRandomId) {
        this.noRepeatTurntableRandomId = noRepeatTurntableRandomId;
    }

    public long getNoRepeatTurntablePayTime() {
        return noRepeatTurntablePayTime;
    }

    public void setNoRepeatTurntablePayTime(long noRepeatTurntablePayTime) {
        this.noRepeatTurntablePayTime = noRepeatTurntablePayTime;
    }

    public long getNoRepeatTurntableRefreshTime() {
        return noRepeatTurntableRefreshTime;
    }

    public void setNoRepeatTurntableRefreshTime(long noRepeatTurntableRefreshTime) {
        this.noRepeatTurntableRefreshTime = noRepeatTurntableRefreshTime;
    }

    public List<Integer> getNoRepeatTurntableTargeted() {
        return noRepeatTurntableTargeted;
    }

    public String getNoRepeatTurntableTargetedJson() {
        return StringUtil.obj2Gson(this.noRepeatTurntableTargeted);
    }

    public void setNoRepeatTurntableTargetedStr(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.noRepeatTurntableTargeted = StringUtil.gson2List(json, new TypeToken<List<Integer>>() {
            });
        }
    }

    public void setNoRepeatTurntableTargeted(List<Integer> noRepeatTurntableTargeted) {
        this.noRepeatTurntableTargeted = noRepeatTurntableTargeted;
    }

    public void clearNoRepeatTurntableTargeted() {
        this.noRepeatTurntableTargeted.clear();
    }

    public int getNoRepeatTurnSegmentId() {
        return noRepeatTurnSegmentId;
    }

    public void setNoRepeatTurnSegmentId(int noRepeatTurnSegmentId) {
        this.noRepeatTurnSegmentId = noRepeatTurnSegmentId;
    }

    public int getKamPo2Count() {
        return kamPo2Count;
    }

    public void setKamPo2Count(int kamPo2Count) {
        this.kamPo2Count = kamPo2Count;
    }

    public void kamPo2AutoCount() {
        this.kamPo2Count++;
    }

    public void resetKamPo2Count() {
        this.kamPo2Count = 0;
    }

    public int getKamPo2Cost() {
        return kamPo2Cost;
    }

    public void setKamPo2Cost(int kamPo2Cost) {
        this.kamPo2Cost = kamPo2Cost;
    }

    public List<Integer> getKamPo2Ids() {
        return kamPo2Ids;
    }

    public void setKamPo2Ids(List<Integer> kamPo2Ids) {
        this.kamPo2Ids = kamPo2Ids;
    }

    public List<Integer> getKamPo2Costs() {
        return kamPo2Costs;
    }

    public void clearKamPo2Costs() {
        this.kamPo2Costs.clear();
    }

    public void setKamPo2Costs(List<Integer> kamPo2Costs) {
        this.kamPo2Costs = kamPo2Costs;
    }

    public String getKamPo2CostsStr() {
        return StringUtil.obj2Gson(this.kamPo2Costs);
    }

    public void setKamPo2CostsJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.kamPo2Costs = StringUtil.gson2List(json, new TypeToken<List<Integer>>() {
            });
        }
    }

    public void clearKamPo2Ids() {
        this.kamPo2Ids.clear();
    }

    public int getKamPo2RandomNum() {
        return kamPo2RandomNum;
    }

    public void setKamPo2RandomNum(int kamPo2RandomNum) {
        this.kamPo2RandomNum = kamPo2RandomNum;
    }

    public void reduceAutoKamPo2RandomNum() {
        this.kamPo2RandomNum--;
    }

    public List<Integer> getFourItems() {
        return fourItems;
    }

    public String getFourItemsStr() {
        return StringUtil.obj2Gson(this.fourItems);
    }

    public void setFourItems(List<Integer> fourItems) {
        this.fourItems = fourItems;
    }

    public void setFourItemsJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.fourItems = StringUtil.gson2List(json, new TypeToken<List<Integer>>() {
            });
        }
    }

    public void clearFourItems() {
        this.fourItems.clear();
    }

    public List<Integer> getReBuiedItems() {
        return reBuiedItems;
    }

    public String getReBuiedItemsStr() {
        return StringUtil.obj2Gson(this.reBuiedItems);
    }

    public void setReBuiedItems(List<Integer> reBuiedItems) {
        this.reBuiedItems = reBuiedItems;
    }

    public void setReBuiedItemsJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.reBuiedItems = StringUtil.gson2List(json, new TypeToken<List<Integer>>() {
            });
        }
    }

    public void clearReBuiedItems() {
        this.reBuiedItems.clear();
    }

    public List<BoughtRecord> getBuiedItems() {
        return buiedItems;
    }

    public void addBuiedItems(BoughtRecord br) {
        this.buiedItemsCount++;
        if (this.buiedItemsCount > 50) {
            buiedItems.set(this.buiedItemsCount % 50 - 1, br);
        } else {
            buiedItems.add(br);
        }
    }

    public String getBuiedItemsStr() {
        return StringUtil.obj2Gson(this.buiedItems);
    }

    public void setBuiedItems(List<BoughtRecord> buiedItems) {
        this.buiedItems = buiedItems;
    }

    public void setBuiedItemsJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.buiedItems = StringUtil.gson2List(json, new TypeToken<List<BoughtRecord>>() {
            });
        }
    }

    public byte getTreasureFree() {
        return treasureFree;
    }

    public void setTreasureFree(byte treasureFree) {
        this.treasureFree = treasureFree;
    }

    public List<Integer> getVouchersList() {
        return vouchersList;
    }

    public String getVouchersListStr() {
        return StringUtil.obj2Gson(this.vouchersList);
    }

    public void setVouchersList(List<Integer> vouchersList) {
        this.vouchersList = vouchersList;
    }

    public void setVouchersListJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.vouchersList = StringUtil.gson2List(json, new TypeToken<List<Integer>>() {
            });
        }
    }

    public int getTreasureIntegral() {
        return treasureIntegral;
    }

    public void setTreasureIntegral(int treasureIntegral) {
        this.treasureIntegral = treasureIntegral;
    }

    public void addTreasureIntegral(int treasureIntegral) {
        this.treasureIntegral += treasureIntegral;
    }

    public int getTreasureVouchers() {
        return treasureVouchers;
    }

    public void setTreasureVouchers(int treasureVouchers) {
        this.treasureVouchers = treasureVouchers;
    }

    public void addTreasureVouchers(int treasureVouchers) {
        this.treasureVouchers += treasureVouchers;
    }

    public long getTreasuresRefreshTime() {
        return treasuresRefreshTime;
    }

    public void setTreasuresRefreshTime(long treasuresRefreshTime) {
        this.treasuresRefreshTime = treasuresRefreshTime;
    }

    public int getBuiedItemsCount() {
        return buiedItemsCount;
    }

    public void setBuiedItemsCount(int buiedItemsCount) {
        this.buiedItemsCount = buiedItemsCount;
    }

    public Map<Integer, Integer> getSetWordsNums() {
        return setWordsNums;
    }

    public String getSetWordsNumsStr() {
        return StringUtil.obj2Gson(this.setWordsNums);
    }

    public void setSetWordsNums(Map<Integer, Integer> setWordsNums) {
        this.setWordsNums = setWordsNums;
    }

    public void setSetWordsNumsJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.setWordsNums = StringUtil.gson2Map(json, new TypeToken<Map<Integer, Integer>>() {
            });
        }
    }

    public void addSetWordsNums(int id) {
        int num = this.setWordsNums.get(id);
        num++;
        this.setWordsNums.put(id, num);
    }

    public int getMonopoly1CurrLevel() {
        return monopoly1CurrLevel;
    }

    public void setMonopoly1CurrLevel(int monopoly1CurrLevel) {
        this.monopoly1CurrLevel = monopoly1CurrLevel;
    }

    public int getMonopoly1PlayedStep() {
        return monopoly1PlayedStep;
    }

    public void setMonopoly1PlayedStep(int monopoly1PlayedStep) {
        this.monopoly1PlayedStep = monopoly1PlayedStep;
    }

    public void addMonopoly1PlayedStep(int step) {
        this.monopoly1PlayedStep += step;
    }

    public int getMonopoly1NextLevel() {
        return monopoly1NextLevel;
    }

    public void setMonopoly1NextLevel(int monopoly1NextLevel) {
        this.monopoly1NextLevel = monopoly1NextLevel;
    }

    public int getMonopoly1CurrSteps() {
        return monopoly1CurrSteps;
    }

    public void setMonopoly1CurrSteps(int monopoly1CurrSteps) {
        this.monopoly1CurrSteps = monopoly1CurrSteps;
    }

    public int getMonopoly1TodayPlayLevel() {
        return monopoly1TodayPlayLevel;
    }

    public void setMonopoly1TodayPlayLevel(int monopoly1TodayPlayLevel) {
        this.monopoly1TodayPlayLevel = monopoly1TodayPlayLevel;
    }

    public List<Integer> getMonopoly1TodayStepReceive() {
        return monopoly1TodayStepReceive;
    }

    public String getMonopoly1TodayStepReceiveStr() {
        return StringUtil.obj2Gson(this.monopoly1TodayStepReceive);
    }

    public void setMonopoly1TodayStepReceive(List<Integer> monopoly1TodayStepReceive) {
        this.monopoly1TodayStepReceive = monopoly1TodayStepReceive;
    }

    public void setMonopoly1TodayStepReceiveJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.monopoly1TodayStepReceive = StringUtil.gson2List(json, new TypeToken<List<Integer>>() {
            });
        }
    }

    public void addMonopoly1TodayStepReceive(int step) {
        this.monopoly1TodayStepReceive.add(step);
    }

    public void clearMonopoly1TodayStepReceive() {
        this.monopoly1TodayStepReceive.clear();
    }

    public List<Integer> getMonopoly1LevelReceived() {
        return monopoly1LevelReceived;
    }

    public String getMonopoly1LevelReceivedStr() {
        return StringUtil.obj2Gson(this.monopoly1LevelReceived);
    }

    public void setMonopoly1LevelReceived(List<Integer> monopoly1LevelReceived) {
        this.monopoly1LevelReceived = monopoly1LevelReceived;
    }

    public void setMonopoly1LevelReceivedJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.monopoly1LevelReceived = StringUtil.gson2List(json, new TypeToken<List<Integer>>() {
            });
        }
    }

    public void addMonopoly1LevelReceived(Integer level) {
        this.monopoly1LevelReceived.add(level);
    }

    public void resetMonopoly1LevelReceived() {
        this.monopoly1LevelReceived.clear();
    }

    public int getMonopoly1DiceOne() {
        return monopoly1DiceOne;
    }

    public void setMonopoly1DiceOne(int monopoly1DiceOne) {
        this.monopoly1DiceOne = monopoly1DiceOne;
    }

    public List<Integer> getMonopoly1StepIds() {
        return monopoly1StepIds;
    }

    public void setMonopoly1StepIds(List<Integer> monopoly1StepIds) {
        this.monopoly1StepIds = monopoly1StepIds;
    }

    public void resetMonopoly1StepIds() {
        this.monopoly1StepIds.clear();
    }

    public void addMonopoly1StepId(int step) {
        this.monopoly1StepIds.add(step);
    }

    public byte getInvestFund() {
        return investFund;
    }

    public void setInvestFund(byte investFund) {
        this.investFund = investFund;
    }

    public int getMonopoly1ResetNum() {
        return monopoly1ResetNum;
    }

    public void setMonopoly1ResetNum(int monopoly1ResetNum) {
        this.monopoly1ResetNum = monopoly1ResetNum;
    }

    public void addMonopoly1ResetNum() {
        this.monopoly1ResetNum += 1;
    }

    public int getMonopoly1RewardStepId() {
        return monopoly1RewardStepId;
    }

    public void setMonopoly1RewardStepId(int monopoly1RewardStepId) {
        this.monopoly1RewardStepId = monopoly1RewardStepId;
    }

    public int getMonopoly1FreeNum() {
        return monopoly1FreeNum;
    }

    public void setMonopoly1FreeNum(int monopoly1FreeNum) {
        this.monopoly1FreeNum = monopoly1FreeNum;
    }

    public void addMonopoly1FreeNum() {
        this.monopoly1FreeNum += 1;
    }

    public byte getMonopoly1Status() {
        return monopoly1Status;
    }

    public void setMonopoly1Status(byte monopoly1Status) {
        this.monopoly1Status = monopoly1Status;
    }

    public byte getSlotNewMachine() {
        return slotNewMachine;
    }

    public void setSlotNewMachine(byte slotNewMachine) {
        this.slotNewMachine = slotNewMachine;
    }

    public byte getSlotMachine() {
        return slotMachine;
    }

    public void setSlotMachine(byte slotMachine) {
        this.slotMachine = slotMachine;
    }

    public short getLimitLimitLimit() {
        return limitLimitLimit;
    }

    public void setLimitLimitLimit(short limitLimitLimit) {
        this.limitLimitLimit = limitLimitLimit;
    }

    public Map<String, Byte> getSevenDay() {
        return sevenDay;
    }

    public String getSevenDayJson() {
        return JSON.toJSONString(sevenDay);
    }

    public void setSevenDayJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.sevenDay = JSON.parseObject(json, new TypeReference<Map<String, Byte>>() {
            });
        }
    }
}
