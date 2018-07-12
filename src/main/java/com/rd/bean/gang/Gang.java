package com.rd.bean.gang;

import com.alibaba.fastjson.JSON;
import com.google.common.reflect.TypeToken;
import com.rd.bean.drop.DropData;
import com.rd.bean.goods.Goods;
import com.rd.bean.mail.Mail;
import com.rd.bean.player.SimplePlayer;
import com.rd.common.MailService;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.GangDao;
import com.rd.define.EGangLogType;
import com.rd.define.EGoodsChangeType;
import com.rd.define.GameDefine;
import com.rd.define.GangDefine;
import com.rd.game.GameWorld;
import com.rd.game.IGameRole;
import com.rd.model.GangModel;
import com.rd.model.data.GangData;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;
import com.rd.util.StringUtil;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>Title: 公会</p>
 * <p>Description: 公会</p>
 * <p>Company: 北京万游畅想科技有限公司</p>
 *
 * @author ---
 * @version 1.0
 * @data 2016年12月28日 下午1:38:05
 */
public class Gang {

    private static final Logger logger = Logger.getLogger(Gang.class);

    private Lock expLock = new ReentrantLock(false);

    /**
     * ID
     **/
    private int id;

    /**
     * 简单玩家
     **/
    private SimplePlayer simplePlayer;

    /**
     * 名字
     **/
    private String name;

    /**
     * 等级
     **/
    private volatile short level;

    /**
     * 经验
     **/
    private volatile int exp;

    /**
     * 徽章
     **/
    private byte badge;

    /**
     * 宣言
     **/
    private String declaration;

    /**
     * 公告
     **/
    private String notice;

    /**
     * 限制等级
     **/
    private short limitLevel;

    /**
     * 是否自动批准
     **/
    private boolean isAutoAdopt;

    /**
     * 申请列表
     **/
    private Vector<SimplePlayer> applyList = new Vector<>();

    /**
     * 公会日志
     **/
    private Queue<GangLog> gangLogQueue = new ConcurrentLinkedQueue<>();

    /**
     * 每日副本通关
     **/
    private short dungeonPass;

    /**
     * 昨日副本最高通关
     **/
    private GangDungeonFirst dungeonYesterdayFirst = null;

    /**
     * 本日副本最高通关
     **/
    private GangDungeonFirst dungeonTodayFirst = null;

    /**
     * 仓库
     **/
    private List<Goods> storeList = new ArrayList<>();

    private ConcurrentHashMap<Integer, GangMember> memberMap = new ConcurrentHashMap<>();

    private List<GangMember> dungeonRank = new ArrayList<>();

    private volatile boolean isSortRank = true;

    /**
     * 帮会商店商品购买次数
     **/
    private Map<Short, Short> shopLimitNumMap = new HashMap<>();


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SimplePlayer getSimplePlayer() {
        return simplePlayer;
    }

    public void setSimplePlayer(SimplePlayer simplePlayer) {
        this.simplePlayer = simplePlayer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short getLevel() {
        return level;
    }

    public void setLevel(short level) {
        this.level = level;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public byte getBadge() {
        return badge;
    }

    public void setBadge(byte badge) {
        this.badge = badge;
    }

    public String getDeclaration() {
        return declaration;
    }

    public void setDeclaration(String declaration) {
        this.declaration = declaration;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public short getLimitLevel() {
        return limitLevel;
    }

    public void setLimitLevel(short limitLevel) {
        this.limitLevel = limitLevel;
    }

    public boolean isAutoAdopt() {
        return isAutoAdopt;
    }

    public void setAutoAdopt(boolean isAutoAdopt) {
        this.isAutoAdopt = isAutoAdopt;
    }

    public Vector<SimplePlayer> getApplyList() {
        return applyList;
    }

    public void setApplyList(Vector<SimplePlayer> applyList) {
        this.applyList = applyList;
    }

    public void initApplyListString(String str) {
        if (!StringUtil.isEmpty(str)) {
            String[] list = str.split(",");
            for (int i = 0; i < list.length; ++i) {
                IGameRole role = GameWorld.getPtr().getGameRole(Integer.parseInt(list[i]));
                this.applyList.add(role.getPlayer());
            }
        }
    }

    public Queue<GangLog> getGangLogQueue() {
        return gangLogQueue;
    }

    public void setGangLogQueue(Queue<GangLog> gangLogQueue) {
        this.gangLogQueue = gangLogQueue;
    }

    public String toGangLogQueueJson() {
        return JSON.toJSONString(gangLogQueue);
    }

    public void fromGangLogQueueJson(String json) {
        this.gangLogQueue = new ConcurrentLinkedQueue<>();
        if (!StringUtil.isEmpty(json)) {
            List<GangLog> list = JSON.parseArray(json, GangLog.class);
            for (GangLog gangLog : list) {
                this.gangLogQueue.add(gangLog);
            }
        }
    }

    public String getPresidentName() {
        return simplePlayer.getName();
    }

    private long modifyTime = GameDefine.NONE;

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public short getDungeonPass() {
        return dungeonPass;
    }

    public void setDungeonPass(short dungeonPass) {
        this.dungeonPass = dungeonPass;
    }

    public List<GangMember> getDungeonRank() {
        return dungeonRank;
    }

    public void setDungeonRank(List<GangMember> dungeonRank) {
        this.dungeonRank = dungeonRank;
    }

    public GangDungeonFirst getDungeonYesterdayFirst() {
        return dungeonYesterdayFirst;
    }

    public void setDungeonYesterdayFirst(GangDungeonFirst dungeonYesterdayFirst) {
        this.dungeonYesterdayFirst = dungeonYesterdayFirst;
    }

    public String getDungeonYesterdayFirstJson() {
        return JSON.toJSONString(dungeonYesterdayFirst);
    }

    public void setDungeonYesterdayFirstJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.dungeonYesterdayFirst = JSON.parseObject(json, GangDungeonFirst.class);
        }
    }

    public GangDungeonFirst getDungeonTodayFirst() {
        return dungeonTodayFirst;
    }

    public void setDungeonTodayFirst(GangDungeonFirst dungeonTodayFirst) {
        this.dungeonTodayFirst = dungeonTodayFirst;
    }

    public String getDungeonToDayFirstJson() {
        return JSON.toJSONString(dungeonTodayFirst);
    }

    public void setDungeonTodayFirstJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.dungeonTodayFirst = JSON.parseObject(json, GangDungeonFirst.class);
        }
    }

    public List<Goods> getStoreList() {
        return storeList;
    }

    public String getStoreListJson() {
        return JSON.toJSONString(storeList);
    }

    public void setStoreList(String json) {
        if (StringUtil.isEmpty(json)) {
            this.storeList = new ArrayList<>();
        } else {
            this.storeList = JSON.parseArray(json, Goods.class);
        }
    }

    public Gang() {
        this.level = 1;
        this.exp = 0;
        this.declaration = "欢迎加入帮会";
        this.notice = "欢迎加入帮会";
        this.limitLevel = 0;
        this.isAutoAdopt = true;
    }

    public Gang(int id, SimplePlayer simplePlayer, String name, byte badge) {
        this();
        this.id = id;
        this.simplePlayer = simplePlayer;
        this.name = name;
        this.badge = badge;
    }

    public void getMessage(Message message) {
        message.setInt(id);
        message.setString(name);
        message.setString(this.getPresidentName());
        message.setShort(level);
        message.setInt(exp);
        message.setByte(badge);
        message.setShort(memberMap.size());
        message.setString(declaration);
        message.setString(notice);
        message.setShort(limitLevel);
        message.setBool(isAutoAdopt);
    }

    public void getSimpleMessage(Message message) {
        message.setInt(id);
        message.setString(name);
        message.setString(this.getPresidentName());
        message.setShort(level);
        message.setByte(badge);
        message.setShort(memberMap.size());
        message.setString(declaration);
        message.setShort(limitLevel);
    }

    public boolean isApplyFull() {
        return applyList.size() >= GangDefine.GANG_APPLY_CAPACITY;
    }

    public int getApply(int playerId) {
        for (int i = 0; i < applyList.size(); i++) {
            if (applyList.get(i).getId() == playerId) {
                return i;
            }
        }
        return -1;
    }

    public boolean isMemberFull() {
        GangData gangData = GangModel.getGangData(level);
        return memberMap.size() >= gangData.getMaxMember();
    }

    public ConcurrentHashMap<Integer, GangMember> getMemberMap() {
        return memberMap;
    }

    public void setMemberMap(ConcurrentHashMap<Integer, GangMember> memberMap) {
        this.memberMap = memberMap;
        for (GangMember member : memberMap.values()) {
            if (member.getDungeonPass() > 0) {
                this.dungeonRank.add(member);
            }
        }
        sortDungeonRank();
    }

    public GangMember getGangMember(int id) {
        return memberMap.get(id);
    }

    public void removeGangMember(int id) {
        memberMap.remove(id);
        for (GangMember member : dungeonRank) {
            if (member.getPlayerId() == id) {
                dungeonRank.remove(member);
                break;
            }
        }
        //将帮会通关数最大者设置为昨日通关数最高和今日通关数最高
        GangDungeonFirst gangDungeonFirst = new GangDungeonFirst();
        GangMember gangMember = this.getMaxDungeonPassGangMember();
        this.dungeonPass = gangMember.getDungeonPass();
        if (dungeonYesterdayFirst != null && dungeonYesterdayFirst.getPlayerId() == id) {
            gangDungeonFirst.setCheer((short) 0);
            gangDungeonFirst.setHead(gangMember.getSimplePlayer().getHead());
            gangDungeonFirst.setName(gangMember.getSimplePlayer().getName());
            gangDungeonFirst.setPass(gangMember.getDungeonPass());
            gangDungeonFirst.setPlayerId(gangMember.getPlayerId());
            this.dungeonYesterdayFirst = gangDungeonFirst;
            this.dungeonTodayFirst = gangDungeonFirst;
        }
    }

    public GangMember getMaxDungeonPassGangMember() {
        int max = Integer.MIN_VALUE;
        GangMember gangMember = null;
        for (GangMember gm : this.memberMap.values()) {
            if (gm.getDungeonPass() > max) {
                max = gm.getDungeonPass();
                gangMember = gm;
            }
        }
        return gangMember;
    }

    public String getApplyListString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < applyList.size(); ++i) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append(applyList.get(i).getId());
        }
        return builder.toString();
    }

    public boolean isHaveApply(int playerId) {
        for (SimplePlayer simplePlayer : this.applyList) {
            if (playerId == simplePlayer.getId()) {
                return true;
            }
        }
        return false;
    }

    public void removeApply(int playerId) {
        for (SimplePlayer simplePlayer : this.applyList) {
            if (playerId == simplePlayer.getId()) {
                this.applyList.remove(simplePlayer);
                break;
            }
        }
    }

    public boolean isHavePosition(byte position) {
        if (GangDefine.GANG_POSITION_PRESIDENT == position) {
            return false;
        } else if (GangDefine.GANG_POSITION_MEMBER == position) {
            return true;
        }
        int count = 0;
        for (GangMember gangMember : this.memberMap.values()) {
            if (gangMember.getPosition() == position) {
                ++count;
            }
        }
        if (GangDefine.GANG_POSITION_VICE_PRESIDENT == position && GangDefine.GANG_VICE_PRESIDENT_NUM > count) {
            return true;
        } else if (GangDefine.GANG_POSITION_MANAGER == position && GangDefine.GANG_MANAGER_NUM > count) {
            return true;
        } else if (GangDefine.GANG_POSITION_CHARGEMAN == position && GangDefine.GANG_CHARGEMAN_NUM > count) {
            return true;
        }
        return false;
    }

    public void addExp(int addValue) {
        if (addValue <= 0) {
            return;
        }
        try {
            expLock.lock();
            short maxLevel = GangModel.getMaxLevel();
            if (level >= maxLevel) {
                return;
            }
            int currentExp = this.getExp() + addValue;
            short currentLevel = this.getLevel();
            GangData levelData = GangModel.getGangData(currentLevel);
            while (currentExp >= levelData.getExp()) {
                ++currentLevel;
                this.addLog(new GangLog(EGangLogType.LEVELUP.getValue(), (byte) currentLevel));
                if (currentLevel >= maxLevel) { // 满级则退出
                    currentLevel = maxLevel;
                    currentExp = 0;
                    break;
                }
                currentExp -= levelData.getExp();
                levelData = GangModel.getGangData(currentLevel);
            }
            this.exp = currentExp;
            this.level = currentLevel;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            expLock.unlock();
        }
    }

    public void addLog(GangLog gangLog) {
        this.gangLogQueue.add(gangLog);
        if (this.gangLogQueue.size() > GangDefine.GANG_LOG_NUM) {
            this.gangLogQueue.poll();
        }
    }

    public void sortDungeonRank() {
        if (this.isSortRank) {
            this.isSortRank = false;
            Collections.sort(dungeonRank, new SortByDungeonRank());
            this.isSortRank = true;
        }
    }

    public Map<Short, Short> getShopLimitNumMap() {
        return shopLimitNumMap;
    }

    public void setShopLimitNumMap(Map<Short, Short> shopLimitNumMap) {
        this.shopLimitNumMap = shopLimitNumMap;
    }

    public String getShopLimitNumMapJson() {
        return StringUtil.obj2Gson(this.shopLimitNumMap);
    }

    public void setShopLimitNumMapStr(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.shopLimitNumMap = StringUtil.gson2Map(json, new TypeToken<Map<Short, Short>>() {
            });
        }
    }

    public void clearShopLimitNumMap() {
        this.shopLimitNumMap.clear();
    }

    public void addDungeonMember(GangMember member) {
        for (GangMember gangMember : dungeonRank) {
            if (member == gangMember) {
                return;
            }
        }
        dungeonRank.add(member);
    }

    public synchronized byte assignStore(int playerId, short gid, short num, String name) {
        if (num < 1) {
            return GameDefine.FALSE;
        }
        Goods goods = null;
        for (Goods g : this.storeList) {
            if (g.getD() == gid) {
                goods = g;
                break;
            }
        }
        if (goods == null) {
            return GameDefine.FALSE;
        }
        if (goods.getN() < num) {
            return GameDefine.FALSE;
        }
        if (this.getGangMember(playerId) == null) {
            return GameDefine.FALSE;
        }
        goods.setN(goods.getN() - num);
        new GangDao().updateStore(this);

        Mail mail = MailService.createMail("帮战分配奖", "众人拾材火焰高，积极参加帮战肯定会有回报，这是帮主为您分配的帮战奖励，再接再厉！", EGoodsChangeType.GANG_FIGHT_ASSIGN_ADD, new DropData(EGoodsType.BOX, gid, num));
        MailService.sendSystemMail(playerId, mail);

        this.addLog(new GangLog(EGangLogType.ASSIGN.getValue(), name, GameWorld.getPtr().getGameRole(playerId).getPlayer().getName(), gid, num));
        return GameDefine.TRUE;
    }

    public synchronized void addStore(List<DropData> reward) {
        for (DropData data : reward) {
            Goods goods = null;
            for (Goods g : this.storeList) {
                if (g.getD() == data.getG()) {
                    goods = g;
                    break;
                }
            }
            if (goods == null) {
                this.storeList.add(new Goods(data.getG(), data.getN()));
            } else {
                goods.setN(goods.getN() + data.getN());
            }
        }
        new GangDao().updateStore(this);
    }

    public byte isImpeachment() {
        IGameRole role = GameWorld.getPtr().getGameRole(this.simplePlayer.getId());
        if (DateUtil.getDistanceDay(role.getPlayer().getLastLoginTime(), System.currentTimeMillis()) >= 6) {
            return GameDefine.TRUE;
        }
        return GameDefine.FALSE;
    }
}

class SortByDungeonRank implements Comparator<GangMember> {

    public SortByDungeonRank() {
    }

    public int compare(GangMember member1, GangMember member2) {
        return Integer.valueOf(member2.getDungeonPass()).compareTo(Integer.valueOf(member1.getDungeonPass()));
    }
}