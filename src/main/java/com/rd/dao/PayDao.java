package com.rd.dao;

import com.rd.bean.pay.OrderData;
import com.rd.dao.db.DBOperator;
import com.lg.bean.IPlayer;
import com.rd.util.DateUtil;
import com.rd.util.StringUtil;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by XingYun on 2016/11/23.
 */
public class PayDao {

    private static Logger logger = Logger.getLogger(PayDao.class);

    private DBOperator db = new DBOperator();

    public Map<String, OrderData> getOrders(int playerId) {
        Map<String, OrderData> map = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT channel, subchannel, account, platform, serverid, orderid, money, diamond, createtime ")
                .append(" FROM pay ")
                .append("WHERE playerId=").append(playerId);
        try {
            ResultSet rs = db.executeQuery(sql.toString());
            while (rs != null && rs.next()) {
                OrderData orderData = new OrderData();
                orderData.setPlayerId(playerId);
                orderData.setChannelId(rs.getShort(1));
                orderData.setSubChannel(rs.getShort(2));
                orderData.setAccount(rs.getString(3));
                orderData.setPlatform(rs.getByte(4));
                orderData.setServerId(rs.getShort(5));
                orderData.setOrderId(rs.getString(6));
                orderData.setAmount(rs.getInt(7));
                orderData.setDiamond(rs.getInt(8));
                orderData.setCreateTime(DateUtil.parseDataTime(rs.getString(9)).getTime());
                map.put(orderData.getUID(), orderData);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            db.executeClose();
        }
        return map;
    }

    /**
     * 创建订单
     *
     * @param orderData
     * @param formatData
     * @return
     */
    public int createPay(OrderData orderData, String formatData) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(" INSERT INTO ")
                .append("           pay (playerId, channel, subchannel, account, platform, serverid, orderid, money, diamond, createtime)")
                .append("        values (")
                .append(orderData.getPlayerId()).append(",")
                .append(orderData.getChannelId()).append(",")
                .append(orderData.getSubChannel()).append(",'")
                .append(orderData.getAccount()).append("',")
                .append(orderData.getPlatform()).append(",'")
                .append(orderData.getServerId()).append("','")
                .append(orderData.getOrderId()).append("',")
                .append(orderData.getAmount()).append(",")
                .append(orderData.getDiamond()).append(",'")
                .append(formatData).append("')");
        logger.info("createPay=" + sqlBuilder.toString());
        return db.executeSql(sqlBuilder.toString());
    }

    /**
     * 检查订单是否重复
     *
     * @param orderData
     * @return
     */
    @Deprecated
    public boolean isPayExisted(OrderData orderData) {
        boolean result = true;
        ResultSet rs = db.executeQuery("SELECT orderid FROM pay WHERE orderid='" + orderData.getOrderId() + "'AND channel=" + orderData.getChannelId());
        try {
            if (!rs.next()) {
                result = false;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            db.executeClose();
        }
        logger.info("Existed order=" + StringUtil.obj2Gson(orderData));
        return result;
    }


    @Deprecated
    public int getTodayPay(IPlayer player) {
        int sum = 0;
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT sum(money) from pay WHERE channel=").append(player.getChannel())
                .append(" and subchannel=").append(player.getSubChannel())
                .append(" and platform=").append(player.getPlatform())
                .append(" and account='").append(player.getAccount())
                .append("' and serverid=").append(player.getServerId())
                .append(" and DATE_FORMAT(createtime,'%Y-%m-%d') = DATE_FORMAT(now(),'%Y-%m-%d')");
        ResultSet rs = db.executeQuery(sql.toString());
        try {
            if (rs.next()) {
                sum = rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error("获取当天充值金额是发生异常。", e);
        } finally {
            db.executeClose();
        }
        return sum;
    }

    @Deprecated
    public int getDayPay(IPlayer player,
                         String startTime, String endTime) {
        int sum = 0;
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT sum(money) from pay WHERE channel=").append(player.getChannel())
                .append(" and subchannel=").append(player.getSubChannel())
                .append(" and platform=").append(player.getPlatform())
                .append(" and account='").append(player.getAccount())
                .append("' and serverid=").append(player.getServerId())
                .append(" and createtime BETWEEN '").append(startTime).append("' AND '").append(endTime).append("'");
        try {
            ResultSet rs = db.executeQuery(sql.toString());
            if (rs != null && rs.next()) {
                sum = rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            db.executeClose();
        }
        return sum;
    }

    @Deprecated
    public int getDiamondPay(IPlayer player, String startTime, String endTime) {
        int sum = 0;
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT sum(diamond) from pay WHERE channel=").append(player.getChannel())
                .append(" and subchannel=").append(player.getSubChannel())
                .append(" and platform=").append(player.getPlatform())
                .append(" and account='").append(player.getAccount())
                .append("' and serverid=").append(player.getServerId())
                .append(" and createtime BETWEEN '").append(startTime).append("' AND '").append(endTime).append("'");
        try {
            ResultSet rs = db.executeQuery(sql.toString());
            if (rs != null && rs.next()) {
                sum = rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            db.executeClose();
        }
        return sum;
    }


    @Deprecated
    public int getPayCountByFlag(IPlayer player, int rmb) {
        int num = 0;
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT count(orderid) from pay WHERE channel=").append(player.getChannel())
                .append(" and subchannel=").append(player.getSubChannel())
                .append(" and platform=").append(player.getPlatform())
                .append(" and account='").append(player.getAccount())
                .append("' and serverid=").append(player.getServerId())
                .append(" and money=").append(rmb)
                .append(" and payFlag = 1 ");
        try {
            ResultSet rs = db.executeQuery(sql.toString());
            if (rs.next()) {
                num = rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            db.executeClose();
        }
        return num;
    }

    @Deprecated
    public int isFirstPayed(IPlayer player) {
        int num = 0;
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT count(orderid) from pay WHERE channel=").append(player.getChannel())
                .append(" and subchannel=").append(player.getSubChannel())
                .append(" and platform=").append(player.getPlatform())
                .append(" and account='").append(player.getAccount())
                .append("' and serverid=").append(player.getServerId())
                .append(" and money!=28")
                .append(" and payFlag = 1 ");
        try {
            ResultSet rs = db.executeQuery(sql.toString());
            if (rs.next()) {
                num = rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            db.executeClose();
        }
        return num > 0 ? 1 : 0;
    }

    @Deprecated
    public Map<Integer, Integer> getPayCountByFlag(IPlayer player) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT money,COUNT(orderid) FROM pay WHERE channel=").append(player.getChannel())
                .append(" and subchannel=").append(player.getSubChannel())
                .append(" and platform=").append(player.getPlatform())
                .append(" and account='").append(player.getAccount())
                .append("' and serverid=").append(player.getServerId())
                .append(" and payFlag = 1 ")
                .append(" GROUP BY money ");
        Map<Integer, Integer> map = new HashMap<>();
        try {
            if (db == null)
                return map;
            ResultSet rs = db.executeQuery(sql.toString());
            if (rs == null)
                return map;
            while (rs.next()) {
                map.put(rs.getInt(1), rs.getInt(2));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            db.executeClose();
        }
        return map;
    }

}
