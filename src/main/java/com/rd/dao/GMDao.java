package com.rd.dao;

import com.rd.bean.player.Player;
import com.rd.dao.db.DBOperator;
import com.rd.game.GameWorld;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * 仅供GM修改数据使用
 *
 * @author Created by U-Demon on 2016年10月29日 下午5:51:57
 * @version 1.0.0
 */
public class GMDao {
    private static final Logger logger = Logger.getLogger(GMDao.class);
    private DBOperator dbOperator = new DBOperator();

    private static final GMDao _instance = new GMDao();

    public GMDao() {
    }

    ;

    public static GMDao getInstance() {
        return _instance;
    }

    ;

    public int gmUpdatePlayerBaseInfo(Player player) {
        PlayerDao dao = new PlayerDao();
        EnumSet<EPlayerSaveType> set = EnumSet.noneOf(EPlayerSaveType.class);
        for (EPlayerSaveType type : EPlayerSaveType.values()) {
            set.add(type);
        }
        return dao.savePlayer(player, set);
//		return dbOperator.executeSql("update player set exp="+player.getExp()+",level="+player.getLevel()+
//				",gold="+player.getGold()+",diamond="+player.getDiamond()+",horselevel="+player.getHorseLevel()+
//				",horsebless="+player.getHorseBless()+",weaponlevel="+player.getWeaponLevel()+
//				",weaponbless="+player.getWeaponBless()+",magiclevel="+player.getMagicLevel()+
//				",magicbless="+player.getMagicBless()+",winglevel="+player.getWingLevel()+
//				",wingbless="+player.getWingBless()+",clotheslevel="+player.getClothesLevel()+
//				",clothesbless="+player.getClothesBless()+
//				" where id="+player.getId());
    }

    public Player gmGetPlayerInfo(int serverId, String account) {
        Player player = null;
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("SELECT * FROM player WHERE serverid = ")
                    .append(serverId)
                    .append(" AND account = '")
                    .append(account).append("'");
            ResultSet rs = dbOperator.executeQuery(builder.toString());
            if (rs.next()) {
                player = new Player();
                //读取数据
                PlayerDao.initPlayerField(player, rs);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            dbOperator.executeClose();
        }
        return player;
    }

    public Map<String, Integer> getAllPay(String startTime, String endTime) {
        Map<String, Integer> result = new HashMap<>();
        DBOperator db = new DBOperator();
        DBOperator db2 = new DBOperator();
        ResultSet rs = db.executeQuery("SELECT account,SUM(diamond) FROM pay WHERE createTime>='"
                + startTime + "' AND createTime<='" + endTime + "' GROUP BY account;");
        try {
            while (rs.next()) {
                result.put(rs.getString(1), rs.getInt(2));
            }
            db2.executeSql("UPDATE activity set weekendPay=0 and weekendTime = 0;");
        } catch (SQLException e) {
            logger.error("读取充值数据时发生异常.", e);
        } finally {
            db.executeClose();
            db2.executeClose();
        }
        return result;
    }

    public void updateWeekendPay(String account, int num) {
        DBOperator db = new DBOperator();
        DBOperator db2 = new DBOperator();
        try {
            ResultSet rs = db.executeQuery("SELECT id FROM player WHERE account='" + account + "'");
            while (rs.next()) {
                db2.executeSql("UPDATE activity set weekendPay=" + num + ", weekendTime=" + System.currentTimeMillis() + " where playerId = " + rs.getInt(1) + ";");
                break;
            }
        } catch (SQLException e) {
            logger.error("读取充值数据时发生异常.", e);
        } finally {
            db.executeClose();
            db2.executeClose();
        }
    }

    public Player gmGetPlayerInfoByName(int serverId, String name) {
        Player player = null;
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("SELECT * FROM player WHERE serverId = ")
                    .append(serverId)
                    .append(" AND name like '%")
                    .append(name).append("%'");
            ResultSet rs = dbOperator.executeQuery(builder.toString());
            if (rs.next()) {
                player = new Player();
                //读取数据
                PlayerDao.initPlayerField(player, rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbOperator.executeClose();
        }
        return player;
    }

    public Player gmGetPlayerInfoByNameAccurate(int serverId, String name) {
        Player player = null;
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("SELECT * FROM player WHERE serverId = ")
                    .append(serverId)
                    .append(" AND name = '")
                    .append(name).append("'");
            ResultSet rs = dbOperator.executeQuery(builder.toString());
            if (rs.next()) {
                player = new Player();
                //读取数据
                PlayerDao.initPlayerField(player, rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbOperator.executeClose();
        }
        return player;
    }

    public String gmGetServerInfo() {
        StringBuilder result = new StringBuilder();
        try {
            ResultSet rs = dbOperator.executeQuery("SELECT count(id) FROM player;");
            if (rs.next()) {
                result.append("注册人数：").append(rs.getInt(1)).append(",");
            }
            result.append("在线人数：").append(GameWorld.getPtr().getOnlineRoles().size()).append(",");
            ResultSet rss = new DBOperator().executeQuery("SELECT sum(money) FROM pay;");
            if (rss.next()) {
                result.append("充值：").append(rss.getInt(1)).append(",");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbOperator.executeClose();
        }
        return result.toString();
    }

}
