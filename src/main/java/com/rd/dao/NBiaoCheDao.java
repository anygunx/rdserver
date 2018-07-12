package com.rd.dao;

import com.rd.bean.player.Escort;
import com.rd.bean.player.NBiaoche;
import com.rd.dao.db.DBOperator;
import com.rd.model.ConstantModel;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NBiaoCheDao {

    private static Logger logger = Logger.getLogger(EscortDao.class);

    private DBOperator db = new DBOperator();

    /**
     * 获取玩家押镖数据
     *
     * @param playerId
     * @return
     */
    public NBiaoche getPlayerEscort(int playerId) {
        NBiaoche escort = null;
        try {
            ResultSet rs = db.executeQuery("SELECT * FROM biaoche where playerId = " + playerId);
            if (rs.next()) {
                //读取数据
                escort = initPlayerNBiaoche(rs);
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

    /****
     *
     * 重启服务器 筛选还在押镖进行中的镖车
     */
    public List<NBiaoche> getNBiaocheList() {
        List<NBiaoche> list = new ArrayList<>();
        try {
            ResultSet rs = db.executeQuery("SELECT * FROM biaoche where cargo = 1 and startTime > 0 and arrive=0");
            if (rs.next()) {
                //读取数据
                NBiaoche escort = initPlayerNBiaoche(rs);
                list.add(escort);
            }
            return list;
        } catch (Exception e) {
            //logger.error("读取押镖数据发生异常，玩家ID：" +  , e);
        } finally {
            db.executeClose();
        }

        return list;

    }


    public NBiaoche createPlayerEscort(int playerId) {
        //初始数据
        NBiaoche escort = new NBiaoche();
        escort.setPlayerId(playerId);
        escort.setQuality(1);
        //SQL
        StringBuilder sql = new StringBuilder("INSERT INTO biaoche(");
        sql.append("playerId,");
        sql.append("quality,");
        sql.append("count,");
        sql.append("startTime,");
        sql.append("cargo,");
        sql.append("arrive,");

        sql.append("hurted,");
        sql.append("logs,");
        sql.append("rob,");
//		sql.append("robList,");
        sql.append("refresh");
//		sql.append("flag,");

        sql.append(") VALUES(");
        sql.append(escort.getPlayerId()).append(",");
        sql.append(escort.getQuality()).append(",");
        sql.append(escort.getCount()).append(",");
        sql.append(escort.getStartTime()).append(",");
        sql.append(escort.getCargo()).append(",");
        sql.append(escort.getArrive()).append(",");
        sql.append(escort.getHurted()).append(",'");
        sql.append(escort.getLogJson()).append("',");
        sql.append(escort.getJiebiaoCnt()).append(",");
        sql.append(escort.getRefresh());
// 		sql.append(escort.getRobList()).append("',");

        //	sql.append(escort.getFlag()).append(",");

        sql.append(");");
        //插入数据
        if (db.executeSql(sql.toString()) < 0)
            return null;
        return escort;
    }


    /*****
     *
     *
     * CREATE TABLE `biaoche` (
     `playerId` int(11) NOT NULL,
     `quality` tinyint(4) DEFAULT NULL COMMENT '镖车品质',
     `count` smallint(8) DEFAULT NULL COMMENT '当日运镖次数',
     `startTime` bigint(20) DEFAULT NULL COMMENT '镖车开始时间',
     `cargo` tinyint(4) DEFAULT NULL COMMENT '是否装有货物',
     `arrive` tinyint(4) DEFAULT '0' COMMENT '是否达到',
     `hurted` tinyint(4) DEFAULT NULL COMMENT '本趟镖车被劫次数',
     `logs` text COMMENT '运镖日志',

     `rob` smallint(8) DEFAULT NULL COMMENT '当日劫镖次数',
     `refresh` smallint(8) DEFAULT NULL COMMENT '刷新次数',

     PRIMARY KEY (`playerId`)
     ) ENGINE=MyISAM DEFAULT CHARSET=utf8;
     */

    private NBiaoche initPlayerNBiaoche(ResultSet rs) {
        NBiaoche escort = new NBiaoche();
        try {
            escort.setPlayerId(rs.getInt(1));
            escort.setQuality(rs.getByte(2));
            escort.setCount(rs.getShort(3));
            escort.setStartTime(rs.getLong(4));
            escort.setCargo(rs.getByte(5));
            escort.setArrive(rs.getByte(6));
            escort.setHurted(rs.getByte(7));
            escort.setLogJson(rs.getString(8));
//			escort.setReaded(rs.getByte(9));
            escort.setJiebiaoCnt(rs.getShort(9));
//			escort.setRobListJson(rs.getString(11));
            escort.setRefresh(rs.getShort(10));
//			escort.setFlag(rs.getByte(13));
//			escort.setMatch(rs.getInt(14));
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
    public int updatePlayerEscort(NBiaoche escort) {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE biaoche SET quality=").append(escort.getQuality()).append(",count=").append(escort.getCount())
                .append(",startTime=").append(escort.getStartTime()).append(",cargo=").append(escort.getCargo())
                .append(",arrive=").append(escort.getArrive())
                .append(",hurted=").append(escort.getHurted()).append(",logs='").append(escort.getLogJson())
                .append("',rob=").append(escort.getJiebiaoCnt())
                .append(",refresh=").append(escort.getRefresh())
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
