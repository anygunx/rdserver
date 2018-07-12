package com.rd.dao;

import com.rd.bean.relationship.RelatedPlayer;
import com.rd.dao.db.DBOperator;
import com.rd.util.DateUtil;
import com.rd.util.StringUtil;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 亲密关系dao
 * Created by XingYun on 2017/5/2.
 */
public class RelationshipDao {
    private static Logger logger = Logger.getLogger(MailDao.class);

    private DBOperator db = new DBOperator();

    private static final String SEPARATOR = ",";

    /**
     * 获取玩家的关系列表
     *
     * @param playerId
     * @return
     */

    public Map<Byte, LinkedHashMap<Integer, RelatedPlayer>> getRelations(int playerId) {
        Map<Byte, LinkedHashMap<Integer, RelatedPlayer>> resultMap = new HashMap<>();
        ResultSet rs = db.executeQuery("select type, data from relationship where playerId=" + playerId);
        try {
            while (rs != null && rs.next()) {
                byte type = rs.getByte(1);
                List<Integer> relatedIdList = StringUtil.getIntList(rs.getString(2), SEPARATOR);
                LinkedHashMap<Integer, RelatedPlayer> relation = null;
                if (!relatedIdList.isEmpty()) {
                    relation = getRelation(relatedIdList);
                }
                resultMap.put(type, relation == null ? new LinkedHashMap<>() : relation);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            db.executeClose();
        }
        return resultMap;
    }

    private LinkedHashMap<Integer, RelatedPlayer> getRelation(List<Integer> relatedIdList) {
        LinkedHashMap<Integer, RelatedPlayer> resultMap = null;
        StringBuilder sqlBuilder = new StringBuilder(
                "SELECT id,name,head,rein,level,vip,fighting,logouttime,logintime ")
                .append("  FROM player")
                .append(" WHERE id ")
                .append("    IN (").append(StringUtil.getString(relatedIdList, ",")).append(")");
        ResultSet rs = db.executeQuery(sqlBuilder.toString());
        try {
            if (rs == null) {
                return resultMap;
            }
            resultMap = new LinkedHashMap<>();
            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                byte head = rs.getByte(3);
                short rein = rs.getShort(4);
                short level = rs.getShort(5);
                int vip = rs.getInt(6);
                int fighting = rs.getInt(7);
                long logoutTime = DateUtil.parseDataTime(rs.getString(8)).getTime();
                long loginTime = DateUtil.parseDataTime(rs.getString(9)).getTime();
                RelatedPlayer relatedPlayer = new RelatedPlayer();
                relatedPlayer.init(id, name, head, rein, level, vip, fighting, loginTime, logoutTime);
                resultMap.put(id, relatedPlayer);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            db.executeClosePs();
        }
        return resultMap;
    }


    public List<RelatedPlayer> getRelatedPlayer(String targetName) {
        List<RelatedPlayer> resultList = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder(
                "SELECT id,name,head,rein,level,vip,fighting,logouttime,logintime ")
                .append("  FROM player")
                .append(" WHERE name like '%").append(targetName).append("%'");
        ResultSet rs = db.executeQuery(sqlBuilder.toString());
        try {
            if (rs == null) {
                return null;
            }
            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                byte head = rs.getByte(3);
                short rein = rs.getShort(4);
                short level = rs.getShort(5);
                int vip = rs.getInt(6);
                int fighting = rs.getInt(7);
                long logoutTime = DateUtil.parseDataTime(rs.getString(8)).getTime();
                long loginTime = DateUtil.parseDataTime(rs.getString(9)).getTime();
                RelatedPlayer relatedPlayer = new RelatedPlayer();
                relatedPlayer.init(id, name, head, rein, level, vip, fighting, loginTime, logoutTime);
                resultList.add(relatedPlayer);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            db.executeClosePs();
        }
        return resultList;
    }

    public int insertRelation(int playerId, byte relationType) {
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO relationship (playerId, type) ")
                .append(" VALUES ( ")
                .append(playerId).append(", ")
                .append(relationType).append(")");
        int id = -1;
        try {
            id = db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            db.executeClose();
        }
        return id;
    }

    public int updateRelation(int playerId, byte type, LinkedHashMap<Integer, RelatedPlayer> relation) {
        StringBuilder builder = new StringBuilder();
        builder.append("UPDATE relationship SET data='").append(StringUtil.getString(relation.keySet(), SEPARATOR)).append("' ")
                .append(" WHERE playerId=").append(playerId)
                .append("   AND type=").append(type);
        return db.executeSql(builder.toString());
    }

    public int updateRelations(int playerId, Map<Byte, LinkedHashMap<Integer, RelatedPlayer>> relations, Byte... types) {
        StringBuilder builder = new StringBuilder();
        builder.append("UPDATE relationship SET data=(")
                .append(" CASE ");
        for (byte type : types) {
            builder.append("  WHEN type=").append(type)
                    .append(" THEN '").append(StringUtil.getString(relations.get(type).keySet(), SEPARATOR)).append("' ");
        }
        builder.append(" END) ")
                .append(" WHERE playerId=").append(playerId);
        return db.executeSql(builder.toString());
    }

}
