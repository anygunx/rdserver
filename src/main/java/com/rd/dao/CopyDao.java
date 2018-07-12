package com.rd.dao;

import com.rd.bean.copy.cailiao.CLCopy;
import com.rd.bean.copy.sjg.SJGCopy;
import com.rd.dao.db.DBOperator;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 副本数据库操作管理
 *
 * @author MyPC
 */
public class CopyDao {
    private static final Logger logger = Logger.getLogger(CopyDao.class);
    private DBOperator dbOperator = new DBOperator();

    public CLCopy getCLCopyByPlayerId(int playerId) {
        CLCopy cl = null;
        ResultSet rs = dbOperator.executeQuery("SELECT playerid,dailytimes,passed FROM copy WHERE playerid=" + playerId);
        try {
            while (rs.next()) {
                CLCopy insta = new CLCopy();
                insta.setPlayerId(rs.getInt(1));
                //insta.setPass(rs.getShort(2));
                insta.setDailyTimesJson(rs.getString(2));
                //insta.setSweep(rs.getByte(5));
                insta.setPassedJson(rs.getString(3));
                cl = insta;
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            dbOperator.executeClose();
        }
        return cl;
    }


    public Map<String, CLCopy> getCLCopy(int playerId) {
        Map<String, CLCopy> map = new HashMap<>();
        ResultSet rs = dbOperator.executeQuery("SELECT playerid,dailytimes,typedata,passed,type,pass,sweep FROM copy WHERE playerid=" + playerId);
        try {
            while (rs.next()) {
                CLCopy insta = new CLCopy();
                insta.setPlayerId(rs.getInt(1));
                //insta.setPass(rs.getShort(2));
                insta.setDailyTimesJson(rs.getString(2));
                insta.setDatatype((rs.getByte(3)));
                insta.setPassedJson(rs.getString(4));
                String str = rs.getString(5);
                String strs[] = str.split("_");
                insta.setDatatype(Byte.parseByte(strs[0].trim()));
                insta.setSubType(Byte.parseByte(strs[1].trim()));
                insta.setPass(rs.getShort(6));
                insta.setSweep(rs.getByte(7));
                map.put(insta.getDatatype() + "_" + insta.getSubType(), insta);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            dbOperator.executeClose();
        }
        return map;
    }


    public int insertCLCopy(CLCopy dungeon) {
        String type = dungeon.getDatatype() + "_" + dungeon.getSubType();
        System.out.println();
        return dbOperator.executeSql("INSERT INTO copy (playerid,type )" +
                "VALUES( " + dungeon.getPlayerId() + ",'" + type + " ')"
        );
    }

    public int updateDungeon(CLCopy dungeon) {
        String type = dungeon.getDatatype() + "_" + dungeon.getSubType();
        return dbOperator.executeSql("update copy set pass=" + dungeon.getPass() + ",dailytimes='" + dungeon.getDailyTimesJson() + "',type='" + type + "',sweep=" + dungeon.getSweep() + ",passed='" + dungeon.getPassedJson() + "' where playerid=" + dungeon.getPlayerId() + " and  type='" + type + "'");
    }


    public SJGCopy getSJGCopyByPlayerId(int playerId) {
        SJGCopy cl = null;
        ResultSet rs = dbOperator.executeQuery("SELECT playerid,pass FROM copy_shuijinggong WHERE playerid=" + playerId);
        try {
            while (rs.next()) {
                SJGCopy insta = new SJGCopy();
                insta.setPlayerid(rs.getInt(1));
                insta.setPassid(rs.getByte(2));
                cl = insta;
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            dbOperator.executeClose();
        }
        return cl;
    }


    public CLCopy getSJGCopyAll(int playerId) {
        CLCopy cl = null;
        ResultSet rs = dbOperator.executeQuery("SELECT playerid,pass FROM copy  ");
        try {

            while (rs.next()) {
                CLCopy insta = new CLCopy();
                insta.setPlayerId(rs.getInt(1));
                insta.setPass(rs.getShort(2));
                cl = insta;
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            dbOperator.executeClose();
        }
        return cl;
    }


}
