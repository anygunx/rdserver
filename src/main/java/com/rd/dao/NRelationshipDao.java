package com.rd.dao;

import com.rd.bean.relationship.NRelatedPlayer;
import com.rd.bean.relationship.RelatedPlayer;
import com.rd.dao.db.DBOperator;
import com.rd.util.DateUtil;
import com.rd.util.StringUtil;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 亲密关系dao
 * Created by XingYun on 2017/5/2.
 */
public class NRelationshipDao {
    private static Logger logger = Logger.getLogger(MailDao.class);

    private DBOperator db = new DBOperator();

    public static NRelationshipDao getInstance() {
        return _instance;
    }

    private static final NRelationshipDao _instance = new NRelationshipDao();

    private NRelationshipDao() {
    }


    private static final String SEPARATOR = ",";

    /**
     * 获取玩家的关系列表
     * playerId` int(11) NOT NULL COMMENT '关注的人',
     * `gz_playerId` int(11) NOT NULL DEFAULT '0' COMMENT '被关注的人',
     * `youqing_value` int(11) NOT NULL DEFAULT '0',
     * `updatetime` bigint(20) NOT NULL DEFAULT '0' COMMENT '添加友情值的时间',
     *
     * @param playerId
     * @return
     */

    public LinkedHashMap<Integer, NRelatedPlayer> getRelations(int playerId) {
        LinkedHashMap<Integer, NRelatedPlayer> relation = new LinkedHashMap<>();
        ResultSet rs = db.executeQuery("select gz_playerId,youqing_value,updatetime from guanzhu where playerId=" + playerId);

        try {

            while (rs != null && rs.next()) {
                int gz_playerId = rs.getInt(1);
                int youqing_value = rs.getInt(2);
                long updatetime = rs.getLong(3);
                NRelatedPlayer pr = new NRelatedPlayer();
                pr.init(gz_playerId, youqing_value, updatetime);
                // List<Integer> relatedIdList = StringUtil.getIntList(rs.getString(2), SEPARATOR);

                relation.put(gz_playerId, pr);
                // resultMap.put(type, relation == null? new LinkedHashMap<>(): relation);
            }
            if (!relation.isEmpty()) {
                getRelation(relation);
            }
            return relation;

        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            db.executeClose();
        }
        return relation;
    }


    public LinkedHashMap<Integer, NRelatedPlayer> getBeiGuanZhu(int playerId) {
        LinkedHashMap<Integer, NRelatedPlayer> relation = new LinkedHashMap<>();
        ResultSet rs = db.executeQuery("select playerId,youqing_value,updatetime from guanzhu where gz_playerId=" + playerId);

        try {

            while (rs != null && rs.next()) {
                int gz_playerId = rs.getInt(1);
                int youqing_value = rs.getInt(2);
                long updatetime = rs.getLong(3);
                NRelatedPlayer pr = new NRelatedPlayer();
                pr.init(gz_playerId, youqing_value, updatetime);
                // List<Integer> relatedIdList = StringUtil.getIntList(rs.getString(2), SEPARATOR);

                relation.put(gz_playerId, pr);
                // resultMap.put(type, relation == null? new LinkedHashMap<>(): relation);
            }
            if (!relation.isEmpty()) {
                getRelation(relation);
            }

            return relation;

        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            db.executeClose();
        }
        return relation;
    }


    private LinkedHashMap<Integer, NRelatedPlayer> getRelation(LinkedHashMap<Integer, NRelatedPlayer> relation) {
        LinkedHashMap<Integer, NRelatedPlayer> resultMap = null;
        StringBuilder sqlBuilder = new StringBuilder(
                "SELECT id,name,head,rein,level,vip,fighting,logouttime,logintime ")
                .append("  FROM player")
                .append(" WHERE id ")
                .append("    IN (").append(StringUtil.getString(relation.keySet(), ",")).append(")");
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
                int fighting = 0;// rs.getInt(7);
//                long logoutTime = DateUtil.parseDataTime(rs.getString(8)).getTime();
//                long loginTime = DateUtil.parseDataTime(rs.getString(9)).getTime();
                NRelatedPlayer pr = relation.get(id);
                pr.init(id, name, head, level, fighting);
                resultMap.put(id, pr);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            db.executeClosePs();
        }
        return resultMap;
    }


    /**
     * 获取玩家的关系列表
     *
     * @param playerId
     * @return
     */

    public LinkedHashMap<Integer, NRelatedPlayer> getBlacks(int playerId) {
        LinkedHashMap<Integer, NRelatedPlayer> relation = new LinkedHashMap<>();
        ResultSet rs = db.executeQuery("select data from backlist where playerId=" + playerId);
        try {
            while (rs != null && rs.next()) {
                relation = new LinkedHashMap<>();
                List<Integer> relatedIdList = StringUtil.getIntList(rs.getString(1), SEPARATOR);

                if (!relatedIdList.isEmpty()) {
                    relation = getRelation(relatedIdList);
                }
                return relation;
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            db.executeClose();
        }
        return relation;
    }

    private LinkedHashMap<Integer, NRelatedPlayer> getRelation(List<Integer> relatedIdList) {
        LinkedHashMap<Integer, NRelatedPlayer> resultMap = new LinkedHashMap<>();
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

            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                byte head = rs.getByte(3);
                short rein = rs.getShort(4);
                short level = rs.getShort(5);
                int vip = rs.getInt(6);
                int fighting = rs.getInt(7);
//                long logoutTime = DateUtil.parseDataTime(rs.getString(8)).getTime();
//                long loginTime = DateUtil.parseDataTime(rs.getString(9)).getTime();
                NRelatedPlayer pr = resultMap.get(id);
                if (pr == null) {
                    pr = new NRelatedPlayer();
                }
                pr.init(id, name, head, level, fighting);
                resultMap.put(id, pr);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            db.executeClosePs();
        }
        return resultMap;
    }


    public LinkedHashMap<Integer, NRelatedPlayer> selectPlayer() {
        LinkedHashMap<Integer, NRelatedPlayer> resultMap = null;
        StringBuilder sqlBuilder = new StringBuilder(
                "SELECT id,name,head,rein,level,vip,fighting,logouttime,logintime ")
                .append("  FROM player")
                .append("  ORDER BY logouttime DESC limit 100 ");

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
//                long logoutTime = DateUtil.parseDataTime(rs.getString(8)).getTime();
//                long loginTime = DateUtil.parseDataTime(rs.getString(9)).getTime();
                NRelatedPlayer pr = new NRelatedPlayer();
                pr.init(id, name, head, level, fighting);
                resultMap.put(id, pr);
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

    public int insertGuanZhu(int playerId, int gz_playerId) {
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO guanzhu (playerId,gz_playerId) ")
                .append(" VALUES ( ")
                .append(playerId).append(",").append(gz_playerId).append(")");
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

    public int insertBlack(int playerId, List<Integer> playerIds) {
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO backlist (playerId,data) ")
                .append(" VALUES ( ").append(playerId).append(",'").append(StringUtil.getString(playerIds, SEPARATOR)).append("' ")
                .append(")");
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

    public int updatBlack(int playerId, List<Integer> playerIds) {
        StringBuilder builder = new StringBuilder();
        builder.append("UPDATE backlist SET data='").append(StringUtil.getString(playerIds, SEPARATOR)).append("' ")
                .append(" WHERE playerId=").append(playerId);

        return db.executeSql(builder.toString());
    }


    /**
     * 存在并发的问题 两个玩家同时操作的话
     *
     * @param playerId
     * @param youqingValue
     * @param time
     * @return
     */
    public int updateGuanzhu(int playerId, int guanzhuPlayerId, int youqingValue, long time) {
        StringBuilder builder = new StringBuilder();
        builder.append("UPDATE guanzhu SET youqing_value='").append(youqingValue).append("' ,").append("updatetime='").append(time).append("' ").
                append(" WHERE playerId=").append(playerId).append(" and ").append("gz_playerId=").append(guanzhuPlayerId);
        return db.executeSql(builder.toString());
    }

    /**
     * @param playerId
     * @return
     */
    public int deleteGuanZhu(int playerId) {
        StringBuilder builder = new StringBuilder();
        builder.append("DELETE FROM guanzhu  ").
                append(" WHERE playerId=").append(playerId);
        return db.executeSql(builder.toString());
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
