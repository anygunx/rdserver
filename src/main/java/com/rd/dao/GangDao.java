package com.rd.dao;

import com.rd.bean.gang.Gang;
import com.rd.bean.gang.GangMember;
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

public class GangDao {
    private static final Logger logger = Logger.getLogger(GangDao.class);
    private DBOperator dbOperator = new DBOperator();

    /**
     * 获取当前最后的公会id
     *
     * @return
     */
    public int getGangMaxId(int low, int high) {
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

    /**
     * 创建公会
     *
     * @param gang
     * @return
     */
    public boolean createGang(Gang gang, GangMember member) {
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
    public Map<Integer, Gang> getAllGang() {
        String sql = "SELECT ownerid,id,name,badge,level,exp,declaration,notice,limitLevel,autoadopt,apply,log,dungeonpass,dungeonyesterdayfirst,dungeontodayfirst,store,shopLimitNumMap FROM gang where state=" + GangDefine.GANG_STATE_NORMAL;
        ResultSet rs = dbOperator.executeQuery(sql);
        Map<Integer, Gang> gangs = new HashMap<>();
        try {
            while (rs.next()) {
                int playerId = rs.getInt(1);
                IGameRole role = GameWorld.getPtr().getGameRole(playerId);
                if (null != role) {
                    Gang gang = new Gang(rs.getInt(2), role.getPlayer(), rs.getString(3), rs.getByte(4));
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
                    gang.setShopLimitNumMapStr(rs.getString("shopLimitNumMap"));
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

    public Map<Integer, GangMember> getGangMembers(int gangId) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT a.playerid,a.gangid,a.position,a.totaldonate,IFNULL(b.pass-1,0) FROM gangmember a  left join dungeon b on a.playerid=b.playerid and b.type=" + DungeonDefine.DUNGEON_TYPE_GANG);
        builder.append(" WHERE gangid=" + gangId);
        ResultSet rs = dbOperator.executeQuery(builder.toString());
        Map<Integer, GangMember> members = new HashMap<>();
        try {
            while (rs.next()) {
                int playerId = rs.getInt(1);
                IGameRole role = GameWorld.getPtr().getGameRole(playerId);
                if (null != role) {
                    GangMember member = new GangMember();
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

    public int updateDeclaration(int id, String declaration) {
        return dbOperator.executeSql("update gang set declaration='" + declaration + "' where id=" + id);
    }

    public int updateNotice(int id, String notice) {
        return dbOperator.executeSql("update gang set notice='" + notice + "' where id=" + id);
    }

    public int updateLimitLevel(int id, short limitLevel) {
        return dbOperator.executeSql("update gang set limitlevel=" + limitLevel + " where id=" + id);
    }

    public int updateAutoAdopt(int id, boolean autoAdopt) {
        return dbOperator.executeSql("update gang set autoadopt=" + autoAdopt + " where id=" + id);
    }

    public int addGangMember(GangMember member) {
        StringBuilder memberSql = new StringBuilder();
        memberSql.append("insert into gangmember(playerid,gangid,position,totaldonate) values(");
        memberSql.append(member.getPlayerId() + ",");
        memberSql.append(member.getGangId() + ",");
        memberSql.append(member.getPosition() + ",");
        memberSql.append(member.getTotalDonate() + ")");
        return dbOperator.executeSql(memberSql.toString());
    }

    public int updateApply(Gang gang) {
        return dbOperator.executeSql("update gang set apply='" + gang.getApplyListString() + "',log='" + gang.toGangLogQueueJson() + "' where id=" + gang.getId());
    }

    public int removeGangMember(GangMember member) {
        return dbOperator.executeSql("delete from gangmember where gangid=" + member.getGangId() + " and playerid=" + member.getPlayerId());
    }

    public int removeGangAllMember(Gang gang) {
        return dbOperator.executeSql("delete from gangmember where gangid=" + gang.getId());
    }

    public int updateGangMemberPosition(GangMember member) {
        return dbOperator.executeSql("update gangmember set position=" + member.getPosition() + " where gangid=" + member.getGangId() + " and playerid=" + member.getPlayerId());
    }

    public int removeGang(Gang gang) {
        return dbOperator.executeSql("update gang set state=" + GangDefine.GANG_STATE_DISBAND + " where id=" + gang.getId());
    }

    public int updateGangOwner(Gang gang) {
        return dbOperator.executeSql("update gang set ownerid=" + gang.getSimplePlayer().getId() + ",log='" + gang.toGangLogQueueJson() + "' where id=" + gang.getId());
    }

    public int updateGangExp(Gang gang) {
        StringBuilder builder = new StringBuilder();
        builder.append("update gang ")
                .append(" set exp= ").append(gang.getExp())
                .append(" ,level= ").append(gang.getLevel())
                .append(" ,log= '").append(gang.toGangLogQueueJson())
                .append("' where id=").append(gang.getId());
        return dbOperator.executeSql(builder.toString());
    }

    public int updateMember(GangMember member) {
        StringBuilder builder = new StringBuilder();
        builder.append("  update gangmember ")
                .append("    set totalDonate= ").append(member.getTotalDonate())
                .append("  where gangid=").append(member.getGangId())
                .append("	 and playerid=").append(member.getPlayerId());
        return dbOperator.executeSql(builder.toString());
    }

    public int updateDungeonPassFirst(Gang gang) {
        return dbOperator.executeSql("update gang set dungeonpass=" + gang.getDungeonPass() + ",dungeontodayfirst='" + gang.getDungeonToDayFirstJson() + "' where id=" + gang.getId());
    }

    public int updateDungeonPass(Gang gang) {
        return dbOperator.executeSql("update gang set dungeonpass=" + gang.getDungeonPass() + " where id=" + gang.getId());
    }

    public int updateDungeonReset() {
        return dbOperator.executeSql("update gang set dungeonpass=0");
    }

    public int updateShopLimitNumMap(Gang gang) {
        return dbOperator.executeSql("update gang set shopLimitNumMap='" + gang.getShopLimitNumMapJson() + "' where id=" + gang.getId());
    }

    public int updateDungeonFirst(Gang gang) {
        return dbOperator.executeSql("update gang set dungeonyesterdayfirst='" + gang.getDungeonYesterdayFirstJson() + "',dungeontodayfirst='" + gang.getDungeonToDayFirstJson() + "',shopLimitNumMap='" + gang.getShopLimitNumMapJson() + "' where id=" + gang.getId());
    }

    public int updateDungeonTodayFirst(Gang gang) {
        return dbOperator.executeSql("update gang set dungeontodayfirst='" + gang.getDungeonToDayFirstJson() + "' where id=" + gang.getId());
    }

    public int updateStore(Gang gang) {
        return dbOperator.executeSql("update gang set store='" + gang.getStoreListJson() + "' where id=" + gang.getId());
    }

    public String[] getGangFight() {
        String[] data = new String[3];
        String sql = "select fightday,gangrank,memberrank from gangfight";
        ResultSet rs = dbOperator.executeQuery(sql);
        try {
            if (rs.next()) {
                data[0] = rs.getString(1);
                data[1] = rs.getString(2);
                data[2] = rs.getString(3);
            } else {
                dbOperator.executeSql("insert into gangfight values('1900-00-00','','')");
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            dbOperator.executeClose();
        }
        return data;
    }

    public int updateGangFight(String fightDay, String gangRank, String memberRank) {
        return dbOperator.executeSql("update gangfight set fightday='" + fightDay + "',gangRank='" + gangRank + "',memberRank='" + memberRank + "'");
    }

    public int updateLog(Gang gang) {
        return dbOperator.executeSql("update gang set log='" + gang.toGangLogQueueJson() + "' where id=" + gang.getId());
    }
}
