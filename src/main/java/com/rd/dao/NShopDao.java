package com.rd.dao;

import com.rd.bean.mail.Mail;
import com.rd.bean.shop.NShop;
import com.rd.dao.db.DBOperator;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NShopDao {
    public static Logger logger = Logger.getLogger(ActivityDao.class);

    private DBOperator db = new DBOperator();


    /**
     * 保存玩家登陆活动数据
     *
     * @param activity
     */
    public void updateLoginInfo(NShop shop) {
        try {
            db.executeSql("update shop set data='" + shop.getShopMapJson() + "' where playerId=" + shop.getPlayerId());
        } catch (Exception e) {
            logger.error("保存玩家活动登录数据时发生异常", e);
        }
        return;
    }


    public NShop createPlayerActivity(int playerId) {
        //初始数据
        NShop shop = new NShop();
        shop.setPlayerId(playerId);
        //SQL
        StringBuilder sql = new StringBuilder("INSERT INTO shop(");
        sql.append("playerId");

        sql.append(") VALUES(");
        sql.append(shop.getPlayerId());
        sql.append(");");
        //插入数据
        if (db.executeSql(sql.toString()) < 0)
            return null;
        return shop;
    }


    public NShop selectNShop(int playerId) {
        NShop shop = new NShop();
        ResultSet rs = db.executeQuery("select * from shop where playerId=" + playerId);
        try {
            while (rs.next()) {
                shop.setPlayerId(rs.getInt(1));
                shop.setShopMap(rs.getString(2));
            }
        } catch (SQLException e) {
            logger.error("读取玩家：" + playerId + "的商城发生异常.", e);
        } finally {
            db.executeClose();
        }
        return shop;

    }


    /**
     * 获取玩家的邮件列表
     *
     * @param playerId
     * @return
     */
    public List<Mail> getMailList(int playerId) {
        List<Mail> mails = new ArrayList<>();
        ResultSet rs = db.executeQuery("select * from mail where playerId=" + playerId);
        try {
            while (rs.next()) {
                Mail mail = new Mail();
                mail.setId(rs.getInt(1));
                mail.setTitle(rs.getString(3));
                mail.setContent(rs.getString(4));
                mail.setAtta(rs.getString(5));
                mail.setState(rs.getByte(6));
                mail.setType(rs.getShort(7));
                mail.setSendTime(rs.getString(8));
                mails.add(mail);
            }
        } catch (SQLException e) {
            logger.error("读取玩家：" + playerId + "的邮件列表发生异常.", e);
        } finally {
            db.executeClose();
        }
        return mails;
    }


}
