package com.rd.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.reflect.TypeToken;
import com.rd.bean.player.AppearPlayer;
import com.rd.bean.rank.ActivityRank;
import com.rd.dao.db.DBOperator;
import com.rd.game.GameRankManager;
import com.rd.util.StringUtil;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 全服数据操作
 *
 * @author Created by U-Demon on 2016年12月5日 下午6:30:47
 * @version 1.0.0
 */
public class GlobalDao {

    public static Logger logger = Logger.getLogger(GlobalDao.class);

    public static GlobalDao getInstance() {
        return _instance;
    }

    private static final GlobalDao _instance = new GlobalDao();

    private GlobalDao() {
    }

    //BOSS血量
    private Map<Short, Long> bossHp = new HashMap<>();

    //转生BOSS等级
    private Map<Short, Byte> reinBossLv = new HashMap<>();

    //达标活动排行榜
    private Map<Integer, ActivityRank> targetTop = new HashMap<>();

    //竞技场达标名单
    private List<Long> arenaPick = new ArrayList<>();

    //第一名外形数据（活动有关）
    private volatile Map<String, AppearPlayer> gameActivityAppears = new HashMap<>();

    /**
     * 读取服务器数据
     *
     * @param playerId
     * @return
     */
    public void initData() {
        DBOperator db = new DBOperator();
        try {
            ResultSet rs = db.executeQuery("SELECT * FROM global");
            if (rs.next()) {
                //读取数据
                setBossHpJson(rs.getString(1));
                setReinBossLvJson(rs.getString(2));
                setTargetTopJson(rs.getString(3));
                setArenaPickJson(rs.getString(4));
                setGameActivityAppearsStr(rs.getString(5));
                GameRankManager.getInstance().setTargetRanksStr(rs.getString(6));
            } else {
                //新建数据
                insertData();
            }
        } catch (Exception e) {
            logger.error("读取服务器数据发生异常!", e);
        } finally {
            db.executeClose();
        }
    }

    public void insertData() {
        //初始数据
        //SQL
        //插入数据
        DBOperator db = new DBOperator();
        db.executeSql("INSERT INTO global(bossHp, reinBossLv) VALUES('', '')");
    }

    public void updateBossHp() {
        try {
            DBOperator db = new DBOperator();
            db.executeSql("update global set bossHp='" + getBossHpJson() + "'");
        } catch (Exception e) {
            logger.error("保存BOSS血量数据时发生异常", e);
        }
        return;
    }

    public void updateTargetRanks() {
        try {
            DBOperator db = new DBOperator();
            db.executeSql("update global set targetRanks='" + GameRankManager.getInstance().getTargetRanksJson() + "'");
        } catch (Exception e) {
            logger.error("保存达标排行数据时发生异常", e);
        }
        return;
    }

    public void updateReinBossLv() {
        try {
            DBOperator db = new DBOperator();
            db.executeSql("update global set reinBossLv='" + getReinBossLvJson() + "'");
        } catch (Exception e) {
            logger.error("保存转生BOSS等级数据时发生异常", e);
        }
        return;
    }

    public void updateTargetTop() {
        try {
            DBOperator db = new DBOperator();
            db.executeSql("update global set targetTop='" + getTargetTopJson() + "'");
        } catch (Exception e) {
            logger.error("保存达标活动排行榜数据时发生异常", e);
        }
        return;
    }

    public void updateGameAppearPlayer() {
        try {
            DBOperator db = new DBOperator();
            db.executeSql("update global set gameActivityAppears='" + getGameActivityAppearsJson() + "'");
        } catch (Exception e) {
            logger.error("保存数据时发生异常", e);
        }
        return;
    }

    public void updateArenaPick() {
        try {
            DBOperator db = new DBOperator();
            db.executeSql("update global set arenaPick='" + getArenaPickJson() + "'");
        } catch (Exception e) {
            logger.error("保存竞技场白名单数据时发生异常", e);
        }
        return;
    }

    public void setBossHpJson(String bossHpJson) {
        if (bossHpJson == null || bossHpJson.isEmpty())
            return;
        this.bossHp = JSON.parseObject(bossHpJson, new TypeReference<Map<Short, Long>>() {
        });
    }

    public String getBossHpJson() {
        return JSON.toJSONString(this.bossHp);
    }

    public Map<Short, Long> getBossHp() {
        return bossHp;
    }

    public void setReinBossLvJson(String reinBossLvJson) {
        if (reinBossLvJson == null || reinBossLvJson.isEmpty())
            return;
        this.reinBossLv = JSON.parseObject(reinBossLvJson, new TypeReference<Map<Short, Byte>>() {
        });
    }

    public String getReinBossLvJson() {
        return JSON.toJSONString(this.reinBossLv);
    }

    public Map<Short, Byte> getReinBossLv() {
        return reinBossLv;
    }

    public void setTargetTopJson(String targetTopJson) {
        if (targetTopJson == null || targetTopJson.isEmpty())
            return;
        this.targetTop = JSON.parseObject(targetTopJson, new TypeReference<Map<Integer, ActivityRank>>() {
        });
    }

    public String getTargetTopJson() {
        return JSON.toJSONString(this.targetTop);
    }

    public Map<Integer, ActivityRank> getTargetTop() {
        return targetTop;
    }

    public void setArenaPickJson(String json) {
        if (json == null || json.isEmpty())
            return;
        this.arenaPick = JSON.parseObject(json, new TypeReference<List<Long>>() {
        });
    }

    public String getArenaPickJson() {
        return JSON.toJSONString(this.arenaPick);
    }

    public List<Long> getArenaPick() {
        return arenaPick;
    }

    public Map<String, AppearPlayer> getGameActivityAppears() {
        return gameActivityAppears;
    }

    public String getGameActivityAppearsJson() {
        return StringUtil.obj2Gson(this.gameActivityAppears);
    }

    public void setGameActivityAppears(Map<String, AppearPlayer> gameActivityAppears) {
        this.gameActivityAppears = gameActivityAppears;
    }

    public void setGameActivityAppearsStr(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.gameActivityAppears = StringUtil.gson2Map(json, new TypeToken<Map<String, AppearPlayer>>() {
            });
        }
    }

}
