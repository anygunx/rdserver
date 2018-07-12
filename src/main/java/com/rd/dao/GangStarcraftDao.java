package com.rd.dao;

import com.rd.bean.gangstarcraft.GangStarcraft;
import com.rd.dao.db.DBOperator;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 公会战 传世争霸
 *
 * @author ---
 * @version 1.0
 * @date 2017年12月28日下午1:18:36
 */
public class GangStarcraftDao {

    private static final Logger logger = Logger.getLogger(GangDao.class);
    private DBOperator dbOperator = new DBOperator();

    /**
     * 获得传世争霸记录
     *
     * @return
     */
    public GangStarcraft getGangStarcraft() {
        GangStarcraft starcraft = null;
        ResultSet rs = dbOperator.executeQuery("select gangid,presidentid,fightday,gangrank,memberrank from gangstarcraft");
        try {
            if (rs.next()) {
                starcraft = new GangStarcraft();
                starcraft.setGangId(rs.getInt(1));
                starcraft.setPresidentId(rs.getInt(2));
                starcraft.setFightDay(rs.getString(3));
                starcraft.setGangRank(rs.getString(4));
                starcraft.setMemberRank(rs.getString(5));
            } else {
                dbOperator.executeSql("insert into gangstarcraft values(0,0,'1900-00-00','','')");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            dbOperator.executeClose();
        }
        return starcraft;
    }

    public int updateGangStarcraft(int gangId, int presidentId, String fightDay, String gangRank, String memberRank) {
        return dbOperator.executeSql("update gangstarcraft set gangid=" + gangId + ", presidentid=" + presidentId + ", fightday='" + fightDay + "',gangrank='" + gangRank + "',memberrank='" + memberRank + "'");
    }
}
