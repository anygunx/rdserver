package com.rd.dao;

import com.rd.dao.db.DBOperator;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 封测数据库代理
 *
 * @author Created by U-Demon on 2016年11月19日 下午7:37:52
 * @version 1.0.0
 */
public class FengCeDao {

    private static Logger logger = Logger.getLogger(FengCeDao.class);

    private DBOperator db = new DBOperator();

    /**
     * 获取AVU战力榜数据
     *
     * @return
     */
    public Map<String, Integer> getAvuFight() {
        Map<String, Integer> result = new HashMap<>();
        try {
            ResultSet rs = db.executeQuery("SELECT id, account FROM aa_avu_fight_rank");
            while (rs.next()) {
                //读取数据
                result.put(rs.getString(2), rs.getInt(1));
            }
        } catch (Exception e) {
            logger.error("读取AVU战力榜数据发生异常：", e);
        } finally {
            db.executeClose();
        }
        return result;
    }

    public boolean hasTable(String table) {
        try {
            ResultSet rs = db.executeQuery("SHOW TABLES LIKE '" + table + "'");
            while (rs.next()) {
                return true;
            }
        } catch (Exception e) {
            logger.error("查看是否存在数据表发生异常：", e);
        } finally {
            db.executeClose();
        }
        return false;
    }

    /**
     * 获取AVU等级榜数据
     *
     * @return
     */
    public Map<String, Integer> getAvuLevel() {
        Map<String, Integer> result = new HashMap<>();
        try {
            ResultSet rs = db.executeQuery("SELECT id, account FROM aa_avu_level_rank");
            while (rs.next()) {
                //读取数据
                result.put(rs.getString(2), rs.getInt(1));
            }
        } catch (Exception e) {
            logger.error("读取AVU等级榜数据发生异常：", e);
        } finally {
            db.executeClose();
        }
        return result;
    }

    /**
     * 获取AVU登录数据
     *
     * @return
     */
    public List<String> getAvuLogin() {
        List<String> result = new ArrayList<>();
        try {
            ResultSet rs = db.executeQuery("SELECT account FROM aa_avu_login");
            while (rs.next()) {
                //读取数据
                result.add(rs.getString(1));
            }
        } catch (Exception e) {
            logger.error("读取AVU等级榜数据发生异常：", e);
        } finally {
            db.executeClose();
        }
        return result;
    }

    /**
     * 获取白鹭战力榜数据
     *
     * @return
     */
    public Map<String, Integer> getEgretFight() {
        Map<String, Integer> result = new HashMap<>();
        try {
            ResultSet rs = db.executeQuery("SELECT id, account FROM aa_egret_fight_rank");
            while (rs.next()) {
                //读取数据
                result.put(rs.getString(2), rs.getInt(1));
            }
        } catch (Exception e) {
            logger.error("读取白鹭战力榜数据发生异常：", e);
        } finally {
            db.executeClose();
        }
        return result;
    }

    /**
     * 获取白鹭等级榜数据
     *
     * @return
     */
    public Map<String, Integer> getEgretLevel() {
        Map<String, Integer> result = new HashMap<>();
        try {
            ResultSet rs = db.executeQuery("SELECT id, account FROM aa_egret_level_rank");
            while (rs.next()) {
                //读取数据
                result.put(rs.getString(2), rs.getInt(1));
            }
        } catch (Exception e) {
            logger.error("读取白鹭等级榜数据发生异常：", e);
        } finally {
            db.executeClose();
        }
        return result;
    }

    /**
     * 获取白鹭登录数据
     *
     * @return
     */
    public List<String> getEgretLogin() {
        List<String> result = new ArrayList<>();
        try {
            ResultSet rs = db.executeQuery("SELECT account FROM aa_egret_login");
            while (rs.next()) {
                //读取数据
                result.add(rs.getString(1));
            }
        } catch (Exception e) {
            logger.error("读取白鹭等级榜数据发生异常：", e);
        } finally {
            db.executeClose();
        }
        return result;
    }

    /**
     * 获取白鹭充值数据
     *
     * @return
     */
    public Map<String, Integer> getEgretPay() {
        Map<String, Integer> result = new HashMap<>();
        try {
            ResultSet rs = db.executeQuery("SELECT account, total_money FROM aa_egret_pay");
            while (rs.next()) {
                //读取数据
                result.put(rs.getString(1), rs.getInt(2));
            }
        } catch (Exception e) {
            logger.error("读取白鹭充值数据发生异常：", e);
        } finally {
            db.executeClose();
        }
        return result;
    }

}
