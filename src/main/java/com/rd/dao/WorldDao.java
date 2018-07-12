package com.rd.dao;

import com.rd.dao.db.DBOperator;
import org.apache.log4j.Logger;

import java.sql.ResultSet;

/**
 * 世界DAO
 *
 * @author ---
 * @version 1.0
 * @date 2018年3月12日下午4:39:45
 */
public class WorldDao {

    private final static Logger logger = Logger.getLogger(WorldDao.class.getName());

    private DBOperator db = new DBOperator();

    public String getTownSoulTurntableRecord() {
        String result = "";
        try {
            ResultSet rs = db.executeQuery("select townsoulturntablerecord from world");
            if (rs != null && rs.next()) {
                result = rs.getString(1);
            } else {
                db.executeSql("insert into world(townsoulturntablerecord) values('')");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            db.executeClose();
        }
        return result;
    }

    public int updateTownsoulturntablerecord(String record) {
        int id = -1;
        try {
            id = db.executeSql("update world set townsoulturntablerecord='" + record + "'");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            db.executeClose();
        }
        return id;
    }
}
