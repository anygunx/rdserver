package com.rd.dao;

import com.rd.bean.pvp.CrossData;
import com.rd.dao.db.DBOperator;
import com.rd.define.GameDefine;
import org.apache.log4j.Logger;

import java.sql.ResultSet;

/**
 * 跨服周边数据数据库代理
 *
 * @author Created by U-Demon on 2016年11月19日 下午7:37:52
 * @version 1.0.0
 */
public class CrossDao {

    private static Logger logger = Logger.getLogger(CrossDao.class);

    private DBOperator db = new DBOperator();

    /**
     * 获取玩家跨服数据
     *
     * @param playerId
     * @return
     */
    public CrossData getPlayerCross(int playerId) {
        CrossData cross = null;
        try {
            ResultSet rs = db.executeQuery("SELECT * FROM crossdata where playerId = " + playerId);
            if (rs.next()) {
                //读取数据
                cross = initPlayerCross(rs);
            } else {
                //新建数据
                cross = createPlayerCross(playerId);
            }
        } catch (Exception e) {
            logger.error("读取跨服数据发生异常，玩家ID：" + playerId, e);
        } finally {
            db.executeClose();
        }
        return cross;
    }

    public CrossData createPlayerCross(int playerId) {
        //初始数据
        CrossData cross = new CrossData();
        cross.setPlayerId(playerId);
        //SQL
        StringBuilder sql = new StringBuilder("INSERT INTO crossdata(");
        sql.append("playerId,");
        sql.append("arenaCount,");
        sql.append("arenaBuy");
        sql.append(") VALUES(");
        sql.append(cross.getPlayerId()).append(",");
        sql.append(cross.getArenaCount()).append(",");
        sql.append(cross.getArenaBuy());
        sql.append(");");
        //插入数据
        if (db.executeSql(sql.toString()) < 0)
            return null;
        return cross;
    }

    private CrossData initPlayerCross(ResultSet rs) {
        CrossData cross = new CrossData();
        try {
            cross.setPlayerId(rs.getInt(1));
            cross.setArenaCount(rs.getInt(2));
            cross.setArenaBuy(rs.getInt(3));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return cross;
    }

    /**
     * 更新玩家竞技场数据
     *
     * @param cross
     * @return
     */
    public int updatePlayerArena(CrossData cross) {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE crossdata SET arenaCount=").append(cross.getArenaCount())
                .append(",arenaBuy=").append(cross.getArenaBuy())
                .append(" WHERE playerId = ").append(cross.getPlayerId());
        int id = -1;
        try {
            id = db.executeSql(sb.toString());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            db.executeClose();
        }
        return id;
    }

    public int dailyClear(int id) {
        return db.executeSql("UPDATE crossdata SET arenaCount = " + GameDefine.ARENA_COUNT + ", arenaBuy = 0 where playerid=" + id);
    }

}
