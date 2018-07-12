package com.rd.dao;

import com.rd.bean.chat.ChatHistory;
import com.rd.dao.db.DBOperator;
import com.rd.define.ChatDefine;
import com.rd.util.DateUtil;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 聊天dao
 * Created by XingYun on 2017/5/2.
 */
public class ChatDao {
    private static Logger logger = Logger.getLogger(MailDao.class);

    private DBOperator db = new DBOperator();

    public ChatHistory getPrivateHistory(int playerId) {
        ChatHistory history = null;
        StringBuilder sqlBuilder = new StringBuilder(
                "SELECT data, lastUpdateTime ")
                .append("  FROM chat")
                .append(" WHERE playerId= ").append(playerId);
        ResultSet rs = db.executeQuery(sqlBuilder.toString());
        try {
            if (rs == null) {
                return history;
            }
            if (rs.next()) {
                String data = rs.getString(1);
                long lastUpdateTime = rs.getLong(2);
                history = new ChatHistory(playerId);
                history.init(data, lastUpdateTime);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            db.executeClose();
        }
        return history;
    }

    public void createPrivateHistory(int playerId) {
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO chat (playerId, lastUpdateTime) ")
                .append(" VALUES ( ")
                .append(playerId).append(", ")
                .append(System.currentTimeMillis()).append(")");
        try {
            db.executeSql(builder.toString());
        } finally {
            db.executeClose();
        }
    }

//    public void removePrivateHistory(int playerId) {
//        StringBuilder builder = new StringBuilder();
//        builder.append("  DELETE FROM chat ")
//                .append("  WHERE playerId= ").append(playerId);
//        try {
//            db.executeSql(builder.toString());
//        } finally {
//            db.executeClose();
//        }
//    }

    public int updateData(ChatHistory history) {
        StringBuilder builder = new StringBuilder();
        builder.append("UPDATE chat SET data='").append(history.getData()).append("', ")
                .append("               lastUpdateTime=").append(history.getLastUpdateTime())
                .append(" WHERE playerId=").append(history.getPlayerId());
        return db.executeSql(builder.toString());
    }

    /**
     * 清理过期数据
     *
     * @param
     */
    public void clear() {
        try {
            long end = System.currentTimeMillis() - ChatDefine.PRIVATE_HISTORY_REMAIN_TIME * DateUtil.DAY;
            String sql = "DELETE FROM chat WHERE lastUpdateTime <= '" + end + "'";
            db.executeSql(sql);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}
