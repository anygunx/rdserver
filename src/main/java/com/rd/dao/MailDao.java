package com.rd.dao;

import com.rd.bean.mail.Mail;
import com.rd.dao.db.DBOperator;
import com.rd.define.GameDefine;
import com.rd.model.ConstantModel;
import com.rd.util.DateUtil;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 邮件操作
 *
 * @author Created by U-Demon on 2016年11月7日 下午3:36:39
 * @version 1.0.0
 */
public class MailDao {

    private static Logger logger = Logger.getLogger(MailDao.class);

    private DBOperator db = new DBOperator();

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

    public int insertMail(int playerId, Mail mail) {
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO mail (playerId, title, content, atta, state, type, sendTime) ")
                .append(" VALUES ( ")
                .append(playerId).append(", '")
                .append(mail.getTitle()).append("', '")
                .append(mail.getContent()).append("', '")
                .append(mail.getAttrJson()).append("',")
                .append(mail.getState()).append(",")
                .append(mail.getType()).append(", '")
                .append(mail.getSendTime()).append("')");
        int id = -1;
        try {
            id = db.executeSql(builder.toString());
            mail.setId(id);
        } catch (Exception e) {
            logger.error("插入玩家：" + playerId + "的邮件发生异常.", e);
        } finally {
            db.executeClose();
        }
        return id;
    }

    public int updateStateById(int id, byte state) {
        return db.executeSql("update mail set state=" + state + " where id=" + id);
    }

    /**
     * 批量修改邮件状态
     *
     * @param ids
     * @param state
     * @return
     */
    public int updateStateBatch(List<Integer> ids, byte state) {
        try {
            StringBuilder sb = new StringBuilder();
            for (int id : ids) {
                sb.append(id).append(",");
            }
            String idStr = sb.substring(0, sb.length() - 1);
            String sql = "UPDATE mail SET state=" + state + " WHERE id in (" + idStr + ")";
            return db.executeSql(sql);
        } catch (Exception e) {
            logger.error("更新邮件状态时发生异常。", e);
        }
        return -1;
    }

    /**
     * 清理全服过期邮件--30天
     *
     * @param end
     */
    public void clearMails() {
        try {
            long end = System.currentTimeMillis() - ConstantModel.EXPIRE_TOTAL * DateUtil.DAY;
            String endTime = DateUtil.formatDateTime(end);
            String sql = "DELETE FROM mail WHERE sendTime <= '" + endTime + "'";
            db.executeSql(sql);
        } catch (Exception e) {
            logger.error("执行清理过期邮件时发生异常。", e);
        }
    }

    /**
     * 删除个人过期邮件
     */
    public void deleteMails(int playerId) {
        try {
            long end = System.currentTimeMillis() - ConstantModel.EXPIRE_NO_ATTA * DateUtil.DAY;
            String endTime = DateUtil.formatDateTime(end);
            StringBuilder sql = new StringBuilder();
            sql.append("DELETE FROM mail ")
                    .append("WHERE playerId = ").append(playerId)
                    .append(" AND sendtime < '").append(endTime).append("' ")
                    .append(" AND state >= ").append(GameDefine.MAIL_STATE_REWARDED);//已领取的或者没附件的
            db.executeSql(sql.toString());
        } catch (Exception e) {
            logger.error("执行删除个人过期邮件时发生异常。", e);
        }
    }

    /**
     * 批量增加邮件
     *
     * @param receiveIds
     * @param mail
     */
    public Map<Integer, Integer> addMailBatch(List<Integer> receiveIds, Mail mail) {
        StringBuilder mailSB = new StringBuilder(" '").append(mail.getTitle()).append("', '")
                .append(mail.getContent()).append("', '")
                .append(mail.getAttrJson()).append("', ")
                .append(mail.getState()).append(", ")
                .append(mail.getType()).append(", '")
                .append(mail.getSendTime()).append("'");

        StringBuilder builder = new StringBuilder();
        builder.append(" INSERT INTO mail (playerId, title, content, atta, state, type, sendtime) ")
                .append(" VALUES  ");
        Iterator<Integer> ite = receiveIds.iterator();
        while (ite.hasNext()) {
            builder.append(" ( ").append(ite.next()).append(" , ").append(mailSB).append(" ) ");
            if (ite.hasNext())
                builder.append(" , ");
        }
        List<Integer> idList = db.executeMultiSql(builder.toString());
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < idList.size(); i++) {
            map.put(receiveIds.get(i), idList.get(i));
        }
        return map;
    }

}
