package com.rd.dao;

import com.rd.bean.faction.NFaction;
import com.rd.bean.faction.NFactionMember;
import com.rd.dao.db.DBOperator;
import com.rd.dao.db.ProxoolDB;
import com.rd.define.DungeonDefine;
import com.rd.define.GangDefine;
import com.rd.game.GameWorld;
import com.rd.game.IGameRole;
import com.rd.util.DateUtil;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class NFactionDao {

    private static final Logger logger = Logger.getLogger(NFactionDao.class);
    private DBOperator dbOperator = new DBOperator();


    /**
     * 创建公会
     *
     * @param gang
     * @return
     */
    public boolean createGang(NFaction gang, NFactionMember member) {
        boolean isSuccess = true;
        StringBuilder gangSql = new StringBuilder();
        gangSql.append("insert into gang(id,ownerid,name,level,exp,badge,declaration,notice,limitlevel,autoadopt,state,createtime) values(");
        gangSql.append(gang.getId() + ",");
        gangSql.append(member.getPlayerId() + ",");
        gangSql.append("'" + gang.getName() + "',");
        gangSql.append(gang.getLevel() + ",");
        gangSql.append(gang.getExp() + ",");
        gangSql.append(gang.getBadge() + ",");
        gangSql.append("'" + gang.getDeclaration() + "',");
        gangSql.append("'" + gang.getNotice() + "',");
        gangSql.append(gang.getLimitLevel() + ",");
        gangSql.append(gang.isAutoAdopt() + ",");
        gangSql.append(GangDefine.GANG_STATE_NORMAL + ",");
        gangSql.append("'" + DateUtil.formatDateTime(System.currentTimeMillis()) + "')");

        StringBuilder memberSql = new StringBuilder();
        memberSql.append("insert into gangmember(playerid,gangid,position,totaldonate,dungeonpass) values(");
        memberSql.append(member.getPlayerId() + ",");
        memberSql.append(member.getGangId() + ",");
        memberSql.append(member.getPosition() + ",");
        memberSql.append(member.getTotalDonate() + ",");
        memberSql.append(member.getDungeonPass() + ")");

        Connection connection = ProxoolDB.getConnection();
        PreparedStatement ps = null;
        try {
            connection.setAutoCommit(false);
            ps = connection.prepareStatement(gangSql.toString());
            ps.executeUpdate();
            ps.executeUpdate(memberSql.toString());
            connection.commit();
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (Exception e1) {
                logger.error(e1.getMessage(), e1);
            }
            logger.error(e.getMessage(), e);
            isSuccess = false;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
                if (connection != null) {
                    connection.close();
                    connection = null;
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return isSuccess;
    }


    /**
     * 得到所有帮派
     *
     * @return
     */
    public Map<Integer, NFaction> getAllGang() {
        String sql = "SELECT ownerid,id,name,badge,level,exp,declaration,notice,limitLevel,autoadopt,apply,log,dungeonpass,dungeonyesterdayfirst,dungeontodayfirst,store,shopLimitNumMap FROM gang where state=" + GangDefine.GANG_STATE_NORMAL;
        ResultSet rs = dbOperator.executeQuery(sql);
        Map<Integer, NFaction> gangs = new HashMap<>();
        try {
            while (rs.next()) {
                int playerId = rs.getInt(1);
                IGameRole role = GameWorld.getPtr().getGameRole(playerId);
                if (null != role) {
                    NFaction gang = new NFaction(rs.getInt(2), role.getPlayer(), rs.getString(3), rs.getByte(4));
                    gang.setLevel(rs.getShort(5));
                    gang.setExp(rs.getInt(6));
                    gang.setDeclaration(rs.getString(7));
                    gang.setNotice(rs.getString(8));
                    gang.setLimitLevel(rs.getShort(9));
                    gang.setAutoAdopt(rs.getBoolean(10));
                    gang.initApplyListString(rs.getString(11));
                    gang.fromGangLogQueueJson(rs.getString(12));
                    gang.setDungeonPass(rs.getShort(13));
                    gang.setDungeonYesterdayFirstJson(rs.getString(14));
                    gang.setDungeonTodayFirstJson(rs.getString(15));
                    gang.setStoreList(rs.getString(16));
                    //gang.setShopLimitNumMapStr(rs.getString("shopLimitNumMap"));
                    gangs.put(gang.getId(), gang);
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            dbOperator.executeClose();
        }
        return gangs;
    }


    public Map<Integer, NFactionMember> getGangMembers(int gangId) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT a.playerid,a.gangid,a.position,a.totaldonate,IFNULL(b.pass-1,0) FROM gangmember a  left join dungeon b on a.playerid=b.playerid and b.type=" + DungeonDefine.DUNGEON_TYPE_GANG);
        builder.append(" WHERE gangid=" + gangId);
        ResultSet rs = dbOperator.executeQuery(builder.toString());
        Map<Integer, NFactionMember> members = new HashMap<>();
        try {
            while (rs.next()) {
                int playerId = rs.getInt(1);
                IGameRole role = GameWorld.getPtr().getGameRole(playerId);
                if (null != role) {
                    NFactionMember member = new NFactionMember();
                    member.setSimplePlayer(role.getPlayer());
                    member.setGangId(rs.getInt(2));
                    member.setPosition(rs.getByte(3));
                    member.setTotalDonate(rs.getInt(4));
                    member.setDungeonPass(rs.getShort(5));
                    members.put(member.getPlayerId(), member);
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            dbOperator.executeClose();
        }
        return members;
    }

    public int addGangMember(NFactionMember member) {
        StringBuilder memberSql = new StringBuilder();
        memberSql.append("insert into gangmember(playerid,gangid,position,totaldonate) values(");
        memberSql.append(member.getPlayerId() + ",");
        memberSql.append(member.getGangId() + ",");
        memberSql.append(member.getPosition() + ",");
        memberSql.append(member.getTotalDonate() + ")");
        return dbOperator.executeSql(memberSql.toString());
    }

    public int removeGangMember(NFactionMember member) {
        return dbOperator.executeSql("delete from gangmember where gangid=" + member.getGangId() + " and playerid=" + member.getPlayerId());
    }

    public int updateApply(NFaction gang) {
        return dbOperator.executeSql("update gang set apply='" + gang.getApplyListString() + "',log='" + gang.toGangLogQueueJson() + "' where id=" + gang.getId());
    }

    public int updateGangMemberPosition(NFactionMember member) {
        return dbOperator.executeSql("update gangmember set position=" + member.getPosition() + " where gangid=" + member.getGangId() + " and playerid=" + member.getPlayerId());
    }

    public int updateGangOwner(NFaction gang) {
        return dbOperator.executeSql("update gang set ownerid=" + gang.getSimplePlayer().getId() + ",log='" + gang.toGangLogQueueJson() + "' where id=" + gang.getId());
    }


    public int updateGangExp(NFaction gang) {
        StringBuilder builder = new StringBuilder();
        builder.append("update gang ")
                .append(" set exp= ").append(gang.getExp())
                .append(" ,level= ").append(gang.getLevel())
                .append(" ,log= '").append(gang.toGangLogQueueJson())
                .append("' where id=").append(gang.getId());
        return dbOperator.executeSql(builder.toString());
    }


    /**
     * 获取当前最后的公会id
     *
     * @return
     */
    public int getFactionMaxId(int low, int high) {
        StringBuilder builder = new StringBuilder();
        builder.append(" SELECT IFNULL(MAX(id), 0) ")
                .append("  FROM gang ")
                .append(" WHERE id BETWEEN ").append(low).append(" AND ").append(high);
        ResultSet rs = dbOperator.executeQuery(builder.toString());
        int maxId = 0;
        try {
            if (rs != null && rs.next()) {
                maxId = rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            dbOperator.executeClose();
        }
        return maxId;
    }


}
