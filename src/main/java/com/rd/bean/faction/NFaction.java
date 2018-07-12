package com.rd.bean.faction;

import com.alibaba.fastjson.JSON;
import com.rd.bean.gang.GangDungeonFirst;
import com.rd.bean.gang.GangLog;
import com.rd.bean.goods.Goods;
import com.rd.bean.player.SimplePlayer;
import com.rd.define.*;
import com.rd.game.GameWorld;
import com.rd.game.IGameRole;
import com.rd.model.GangModel;
import com.rd.model.NFactionModel;
import com.rd.model.data.GangData;
import com.rd.model.data.faction.NFactionActiveData;
import com.rd.model.data.faction.NFactionLevelData;
import com.rd.net.message.Message;
import com.rd.util.StringUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NFaction {

    private static final Logger logger = Logger.getLogger(NFaction.class);

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

    private volatile short activeLevel;

    /**
     * 经验
     **/
    private volatile int exp;

    private volatile int activeExp;


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
    /****限制战力****/
    private long limitefight;
    /***上香次数**/
    private short sxCount;


    private int xianghuo;


    public short getSxCount() {
        return sxCount;
    }

    public int getXianghuo() {
        return xianghuo;
    }

    public void setXianghuo(int xianghuo) {
        this.xianghuo = xianghuo;
    }

    public void setSxCount(short sxCount) {
        this.sxCount = sxCount;
    }

    public void addSxCount() {
        this.sxCount += 1;
    }

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

    public Queue<GangLog> getShangxiangLogQueue() {
        return shangxiangLogQueue;
    }

    /**
     * 上香日志
     **/
    private Queue<GangLog> shangxiangLogQueue = new ConcurrentLinkedQueue<>();

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

    private ConcurrentHashMap<Integer, NFactionMember> memberMap = new ConcurrentHashMap<>();

    private List<NFactionMember> dungeonRank = new ArrayList<>();


    private long fight = 0;


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

    public long getLimitefight() {
        return limitefight;
    }

    public void setLimitefight(long limitefight) {
        this.limitefight = limitefight;
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


    public short getActiveLevel() {
        return activeLevel;
    }

    public void setActiveLevel(short activeLevel) {
        this.activeLevel = activeLevel;
    }

    public int getActiveExp() {
        return activeExp;
    }

    public void setActiveExp(int activeExp) {
        this.activeExp = activeExp;
    }

    public Queue<GangLog> getGangLogQueue() {
        return gangLogQueue;
    }

    public void setGangLogQueue(Queue<GangLog> gangLogQueue) {
        this.gangLogQueue = gangLogQueue;
    }

    public String toGangLogQueueJson() {
        Queue<GangLog> log = new ConcurrentLinkedQueue<>();
        log.addAll(gangLogQueue);
        log.addAll(shangxiangLogQueue);
        return JSON.toJSONString(log);
    }

    public void fromGangLogQueueJson(String json) {
        this.gangLogQueue = new ConcurrentLinkedQueue<>();
        this.shangxiangLogQueue = new ConcurrentLinkedQueue<>();
        if (!StringUtil.isEmpty(json)) {
            List<GangLog> list = JSON.parseArray(json, GangLog.class);
            for (GangLog gangLog : list) {
                if (gangLog.getType() == NfactionLogType.INCENSE.getValue()) {
                    this.shangxiangLogQueue.add(gangLog);
                } else {
                    gangLogQueue.add(gangLog);
                }

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

    public List<NFactionMember> getDungeonRank() {
        return dungeonRank;
    }

    public void setDungeonRank(List<NFactionMember> dungeonRank) {
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

    public NFaction() {
        this.level = 1;
        this.exp = 0;
        this.declaration = "欢迎加入帮会";
        this.notice = "欢迎加入帮会";
        this.limitLevel = 0;
        this.isAutoAdopt = true;
    }

    public NFaction(int id, SimplePlayer simplePlayer, String name, short level) {
        this();
        this.id = id;
        this.simplePlayer = simplePlayer;
        this.name = name;
        this.level = level;
    }


    public void getMessage(Message message) {
        message.setInt(id);
        message.setString(this.getPresidentName());
        message.setString(name);
        message.setShort(level);
        //message.setInt(exp); 
        message.setShort(memberMap.size());
        message.setShort(getFactionLevelData().getNum());
        //message.setString(declaration);
        message.setString(notice);
        message.setInt(exp);
    }

    private NFactionLevelData getFactionLevelData() {
        return NFactionModel.getNFactionLevelData(level);


    }

    public void getSimpleMessage(Message message) {
        message.setInt(id);
        message.setString(name);
        message.setString(this.getPresidentName());
        message.setShort(level);
        message.setShort(memberMap.size());
        message.setShort(NFactionDefine.FACTION_LIMITE_COUNT);
        //message.setString(declaration);
        message.setLong(limitefight);
        //message.setShort(limitLevel);
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

    public ConcurrentHashMap<Integer, NFactionMember> getMemberMap() {
        return memberMap;
    }

    public void setMemberMap(ConcurrentHashMap<Integer, NFactionMember> memberMap) {
        this.memberMap = memberMap;
        for (NFactionMember member : memberMap.values()) {
            if (member.getDungeonPass() > 0) {
                this.dungeonRank.add(member);
            }
        }

    }

    public NFactionMember getGangMember(int id) {
        return memberMap.get(id);
    }

    public void removeGangMember(int id) {
        memberMap.remove(id);
        for (NFactionMember member : dungeonRank) {
            if (member.getPlayerId() == id) {
                dungeonRank.remove(member);
                break;
            }
        }
        //将帮会通关数最大者设置为昨日通关数最高和今日通关数最高
        GangDungeonFirst gangDungeonFirst = new GangDungeonFirst();
        NFactionMember gangMember = this.getMaxDungeonPassGangMember();
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

    public NFactionMember getMaxDungeonPassGangMember() {
        int max = Integer.MIN_VALUE;
        NFactionMember gangMember = null;
        for (NFactionMember gm : this.memberMap.values()) {
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
        if (NFactionDefine.GANG_POSITION_PRESIDENT == position) {
            return false;
        } else if (NFactionDefine.GANG_POSITION_MEMBER == position) {
            return true;
        }
        int count = 0;
        for (NFactionMember gangMember : this.memberMap.values()) {
            if (gangMember.getPosition() == position) {
                ++count;
            }
        }
        if (NFactionDefine.GANG_POSITION_VICE_PRESIDENT == position && NFactionDefine.GANG_VICE_PRESIDENT_NUM > count) {
            return true;
        }
        return false;
    }


    public void addLog(GangLog gangLog) {
        this.gangLogQueue.add(gangLog);
        if (this.gangLogQueue.size() > NFactionDefine.GANG_LOG_NUM) {
            this.gangLogQueue.poll();
        }
    }

    public void addSXLog(GangLog gangLog) {
        this.shangxiangLogQueue.add(gangLog);
        if (this.shangxiangLogQueue.size() > NFactionDefine.FACTION_SHAGNXIANG_LOG_LIMITE) {
            this.shangxiangLogQueue.poll();
        }
    }


    public void addExp(int addValue) {
        if (addValue <= 0) {
            return;
        }
        try {
            expLock.lock();
            short maxLevel = (short) NFactionModel.getNFactionLevelMap().size();
            if (level >= maxLevel) {
                return;
            }
            int currentExp = this.getExp() + addValue;
            short currentLevel = this.getLevel();
            NFactionLevelData levelData = NFactionModel.getNFactionLevelData(currentLevel);
            while (currentExp >= levelData.getExp()) {
                ++currentLevel;
                this.addLog(new GangLog(EGangLogType.LEVELUP.getValue(), (byte) currentLevel));
                if (currentLevel >= maxLevel) { // 满级则退出
                    currentLevel = maxLevel;
                    currentExp = 0;
                    break;
                }
                currentExp -= levelData.getExp();
                levelData = NFactionModel.getNFactionLevelData(currentLevel);
            }
            this.exp = currentExp;
            this.level = currentLevel;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            expLock.unlock();
        }
    }


    public void addActiveExp(int addValue) {
        if (addValue <= 0) {
            return;
        }
        try {
            expLock.lock();

            short maxLevel = (short) NFactionModel.getNFactionActiveMap().size();
            if (activeExp >= maxLevel) {
                return;
            }
            int currentExp = this.getExp() + addValue;
            short currentLevel = this.getLevel();
            NFactionActiveData levelData = NFactionModel.getNFactionActiveData(currentLevel);
            while (currentExp >= levelData.getExp()) {
                ++currentLevel;
                this.addLog(new GangLog(EGangLogType.LEVELUP.getValue(), (byte) currentLevel));
                if (currentLevel >= maxLevel) { // 满级则退出
                    currentLevel = maxLevel;
                    currentExp = 0;
                    break;
                }
                currentExp -= levelData.getExp();
                levelData = NFactionModel.getNFactionActiveData(currentLevel);
            }
            this.activeExp = currentExp;
            this.activeLevel = currentLevel;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            expLock.unlock();
        }
    }


}
