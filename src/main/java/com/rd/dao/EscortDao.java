package com.rd.dao;

import com.rd.bean.player.Escort;
import com.rd.dao.db.DBOperator;
import com.rd.model.ConstantModel;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 押镖数据库代理
 *
 * @author Created by U-Demon on 2016年11月19日 下午7:37:52
 * @version 1.0.0
 */
public class EscortDao {

    private static Logger logger = Logger.getLogger(EscortDao.class);

    private DBOperator db = new DBOperator();

    /**
     * 获取玩家押镖数据
     *
     * @param playerId
     * @return
     */
    public Escort getPlayerEscort(int playerId) {
        Escort escort = null;
        try {
            ResultSet rs = db.executeQuery("SELECT * FROM escort where playerId = " + playerId);
            if (rs.next()) {
                //读取数据
                escort = initPlayerEscort(rs);
            } else {
                //新建数据
                escort = createPlayerEscort(playerId);
            }
        } catch (Exception e) {
            logger.error("读取押镖数据发生异常，玩家ID：" + playerId, e);
        } finally {
            db.executeClose();
        }
        return escort;
    }

    public Escort createPlayerEscort(int playerId) {
        //初始数据
        Escort escort = new Escort();
        escort.setPlayerId(playerId);
        escort.setQuality(1);
        //SQL
        StringBuilder sql = new StringBuilder("INSERT INTO escort(");
        sql.append("playerId,");
        sql.append("quality,");
        sql.append("count,");
        sql.append("startTime,");
        sql.append("cargo,");
        sql.append("hurted,");
        sql.append("logs,");
        sql.append("rob,");
        sql.append("robList,");
        sql.append("refresh,");
        sql.append("flag,");
        sql.append("matchValue");
        sql.append(") VALUES(");
        sql.append(escort.getPlayerId()).append(",");
        sql.append(escort.getQuality()).append(",");
        sql.append(escort.getCount()).append(",");
        sql.append(escort.getStartTime()).append(",");
        sql.append(escort.getCargo()).append(",");
        sql.append(escort.getHurted()).append(",'");
        sql.append(escort.getLogJson()).append("',");
        sql.append(escort.getRob()).append(",'");
        sql.append(escort.getRobList()).append("',");
        sql.append(escort.getRefresh()).append(",");
        sql.append(escort.getFlag()).append(",");
        sql.append(escort.getMatch());
        sql.append(");");
        //插入数据
        if (db.executeSql(sql.toString()) < 0)
            return null;
        return escort;
    }

    private Escort initPlayerEscort(ResultSet rs) {
        Escort escort = new Escort();
        try {
            escort.setPlayerId(rs.getInt(1));
            escort.setQuality(rs.getByte(2));
            escort.setCount(rs.getShort(3));
            escort.setStartTime(rs.getLong(4));
            escort.setCargo(rs.getByte(5));
            escort.setArrive(rs.getByte(6));
            escort.setHurted(rs.getByte(7));
            escort.setLogJson(rs.getString(8));
            escort.setReaded(rs.getByte(9));
            escort.setRob(rs.getShort(10));
            escort.setRobListJson(rs.getString(11));
            escort.setRefresh(rs.getShort(12));
            escort.setFlag(rs.getByte(13));
            escort.setMatch(rs.getInt(14));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return escort;
    }

    /**
     * 更新玩家押镖数据
     *
     * @param escort
     * @return
     */
    public int updatePlayerEscort(Escort escort) {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE escort SET quality=").append(escort.getQuality()).append(",count=").append(escort.getCount())
                .append(",startTime=").append(escort.getStartTime()).append(",cargo=").append(escort.getCargo())
                .append(",arrive=").append(escort.getArrive())
                .append(",hurted=").append(escort.getHurted()).append(",logs='").append(escort.getLogJson())
                .append("',rob=").append(escort.getRob())
                .append(",robList='").append(escort.getRobListJson())
                .append("',refresh=").append(escort.getRefresh())
                .append(",flag=").append(escort.getFlag())
                .append(",matchValue=").append(escort.getMatch())
                .append(" WHERE playerId = ").append(escort.getPlayerId());
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

    public int updatePlayerEscortReaded(Escort escort) {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE escort SET readed=").append(escort.getReaded())
                .append(" WHERE playerId = ").append(escort.getPlayerId());
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

    public Escort getRobEscortList(Escort escort, List<Integer> ids) {
        Escort result = null;
        StringBuilder sql = new StringBuilder();
        sql.append("playerId NOT IN (");
        for (Integer id : escort.getRobList()) {
            sql.append(id).append(",");
        }
        if (ids != null) {
            for (Integer id : ids) {
                sql.append(id).append(",");
            }
        }
        sql.append(escort.getPlayerId()).append(") ");
        sql.append("AND cargo = 1 AND hurted < ").append(ConstantModel.ESCORT_HURTED);
        ResultSet rs = db.executeQuery(LadderDao.getStartTimeSortSQL("playerId,quality", sql.toString()));
        try {
            if (rs.next()) {
                result = new Escort();
                result.setPlayerId(rs.getInt(1));
                result.setQuality(rs.getByte(2));
            }
        } catch (SQLException e) {
            logger.error("查找劫镖数据时发生异常", e);
            return null;
        } finally {
            db.executeClose();
        }
        return result;
    }

    public int dailyClear(int id) {
        return db.executeSql("UPDATE escort SET count = 0, rob = 0, refresh = 0, robList='' where playerid=" + id);
    }

}
