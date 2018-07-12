package com.rd.dao;

import com.rd.bean.pvp.PvpInfo;
import com.rd.bean.pvp.PvpRank;
import com.rd.dao.db.DBOperator;
import com.rd.game.GameWorld;
import com.rd.game.IGameRole;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PvpDao {

    private DBOperator dbOperator = new DBOperator();

    public int createPlayerPvpInfo(PvpInfo pvpInfo) {
        StringBuilder builder = new StringBuilder();
        builder.append("  INSERT INTO pvp ( playerId, prestige, lastUpdateTime, challengerList, recordList ) ")
                .append(" VALUES ( ")
                .append(pvpInfo.getPlayer().getId()).append(",")
                .append(pvpInfo.getPrestige()).append(", '")
                .append(pvpInfo.getLastUpdateTime()).append("' ,'")
                .append(pvpInfo.toChallengerListJson()).append("', '")
                .append(pvpInfo.toRecordListJson()).append("')");
        int id = -1;
        try {
            id = dbOperator.executeSql(builder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbOperator.executeClose();
        }
        return id;
    }

    public PvpInfo getPlayerPvpInfo(int playerId) {
        StringBuilder builder = new StringBuilder();
        builder.append(" SELECT playerId, prestige, lastUpdateTime, challengerList, recordList, streakwin")
                .append("  FROM pvp ")
                .append(" WHERE playerId =").append(playerId);
        final ResultSet rs = dbOperator.executeQuery(builder.toString());
        if (rs == null) {
            return null;
        }
        PvpInfo info = null;
        try {
            if (rs.next()) {
                IGameRole role = GameWorld.getPtr().getGameRole(playerId);
                if (null != role) {
                    info = new PvpInfo(role.getPlayer());
                    info.setPrestige(rs.getInt(2));
                    info.setLastUpdateTime(rs.getLong(3));
                    info.fromChallengerListJson(rs.getString(4));
                    info.fromRecordListJson(rs.getString(5));
                    info.setStreakWin(rs.getByte(6));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbOperator.executeClose();
        }
        return info;
    }

    public int updateChallengers(PvpInfo info) {
        StringBuilder builder = new StringBuilder();
        builder.append(" UPDATE pvp ")
                .append("   SET challengerList='").append(info.toChallengerListJson()).append("'")
                .append("      ,lastUpdateTime=").append(info.getLastUpdateTime())
                .append(" WHERE playerId=").append(info.getPlayer().getId());
        return dbOperator.executeSql(builder.toString());
    }

    public List<PvpRank> getPvpRank(int rankCount) {
        StringBuilder builder = new StringBuilder();
        builder.append(" SELECT playerId, prestige FROM pvp where prestige>0 order by prestige desc limit ")
                .append(rankCount);
        final ResultSet rs = dbOperator.executeQuery(builder.toString());
        if (rs == null) {
            return null;
        }
        List<PvpRank> list = new CopyOnWriteArrayList<>();
        try {
            while (rs.next()) {
                int playerId = rs.getInt(1);
                IGameRole role = GameWorld.getPtr().getGameRole(playerId);
                if (null != role) {
                    PvpRank rank = new PvpRank();
                    rank.setPrestige(rs.getInt(2));
                    rank.setPlayer(role.getPlayer());
                    list.add(rank);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbOperator.executeClose();
        }
        return list;
    }

    public int updateResult(PvpInfo info) {
        StringBuilder builder = new StringBuilder();
        builder.append(" UPDATE pvp ")
                .append("   SET recordList='").append(info.toRecordListJson()).append("'")
                .append("      ,streakwin=").append(info.getStreakWin())
                .append("      ,prestige=").append(info.getPrestige())
                .append("      ,challengerList='").append(info.toChallengerListJson()).append("'")
                .append("      ,lastUpdateTime=").append(info.getLastUpdateTime())
                .append(" WHERE playerId=").append(info.getPlayer().getId());
        return dbOperator.executeSql(builder.toString());
    }

    public int updateResetPrestige() {
        return dbOperator.executeSql("update pvp set prestige=0,streakwin=0");
    }
}
