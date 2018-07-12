package com.rd.dao;

import com.rd.bean.ladder.PlayerLadder;
import com.rd.dao.db.DBOperator;
import com.rd.define.GameDefine;
import com.rd.game.GameRole;
import com.rd.model.ConstantModel;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LadderDao {

    private static Logger logger = Logger.getLogger(LadderDao.class);

    private DBOperator db = new DBOperator();

    public PlayerLadder getPlayerLadder(GameRole gameRole) {
        int playerId = gameRole.getPlayer().getId();
        PlayerLadder ladder = getPlayerLadder(playerId);
        if (ladder == null) {
            ladder = new PlayerLadder();
            //初始化数据
            ladder.setPlayerId(playerId);
            ladder.setStar(0);
            ladder.setTotal(0);
            ladder.setWin(0);
            ladder.setLose(0);
            ladder.setConwin(0);
            ladder.setCount(gameRole.getLadderManager().getFightCountMax());
            ladder.setRecoverTime(0);
            ladder.setBuyCount(0);
            ladder.setScore(0);
            ladder.setLastStar(0);
            ladder.setLastTotal(0);
            ladder.setLastWin(0);
            ladder.setLastRank(0);
            //插入
            insertPlayerLadder(ladder);
        }
        return ladder;
    }

    public PlayerLadder getPlayerLadder(int playerId) {
        PlayerLadder ladder = null;
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("SELECT * FROM ladder ")
                    .append(" WHERE playerId = ").append(playerId);
            ResultSet rs = db.executeQuery(builder.toString());
            if (rs.next()) {
                //读取数据
                ladder = initPlayerLadder(rs);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            db.executeClose();
        }
        return ladder;
    }

    private PlayerLadder initPlayerLadder(ResultSet rs) {
        PlayerLadder ladder = new PlayerLadder();
        try {
            //读取数据
            ladder.setPlayerId(rs.getInt(1));
            ladder.setStar(rs.getInt(2));
            ladder.setTotal(rs.getInt(3));
            ladder.setWin(rs.getInt(4));
            ladder.setLose(rs.getInt(5));
            ladder.setConwin(rs.getInt(6));
            ladder.setCount(rs.getInt(7));
            ladder.setRecoverTime(rs.getLong(8));
            ladder.setBuyCount(rs.getInt(9));
            String fightIdString = rs.getString(10);
            if (fightIdString != null) {
                for (String fightId : fightIdString.split(",")) {
                    if (!fightId.equals(""))
                        ladder.getFightIds().add(Integer.valueOf(fightId));
                }
            }
            ladder.setScore(rs.getInt(11));
            ladder.setLastStar(rs.getInt(12));
            ladder.setLastTotal(rs.getInt(13));
            ladder.setLastWin(rs.getInt(14));
            ladder.setLastRank(rs.getInt(15));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return ladder;
    }

    public int updatePlayerLadder(PlayerLadder ladder) {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ladder SET star=").append(ladder.getStar()).append(",total=").append(ladder.getTotal())
                .append(",win=").append(ladder.getWin()).append(",lose=").append(ladder.getLose())
                .append(",conwin=").append(ladder.getConwin())
                .append(",count=").append(ladder.getCount()).append(",recoverTime=").append(ladder.getRecoverTime())
                .append(",buyCount=").append(ladder.getBuyCount()).append(",fightIds='").append(ladder.toFightIdString())
                .append("',score=").append(ladder.getScore())
                .append(" where playerId=").append(ladder.getPlayerId());
        int id = -1;
        try {
            id = db.executeSql(sb.toString());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            db.executeClose();
        }
        return id;
    }

    private static final String LADDER_PLAYER_FIELDS = " (playerId, star, total, win, lose, conwin, "
            + "count, recoverTime, buyCount, fightIds, score, lastStar, lastTotal, lastWin, lastRank) ";

    private int insertPlayerLadder(PlayerLadder ladder) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ladder").append(LADDER_PLAYER_FIELDS)
                .append(" VALUES ( ").append(ladder.getPlayerId()).append(", ").append(ladder.getStar())
                .append(", ").append(ladder.getTotal()).append(", ").append(ladder.getWin())
                .append(", ").append(ladder.getLose())
                .append(", ").append(ladder.getConwin()).append(", ").append(ladder.getCount())
                .append(", ").append(ladder.getRecoverTime()).append(", ").append(ladder.getBuyCount())
                .append(", '").append(ladder.toFightIdString()).append("'")
                .append(", ").append(ladder.getScore()).append(", ").append(ladder.getLastStar())
                .append(", ").append(ladder.getLastTotal()).append(", ").append(ladder.getLastWin())
                .append(", ").append(ladder.getLastRank())
                .append(") ");
        int id = -1;
        try {
            id = db.executeSql(sb.toString());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            db.executeClose();
        }
        return id;
    }

    /**
     * 根据阶段匹配对手
     *
     * @param ladder
     * @return
     */
    public PlayerLadder getMatchPlayerByStar(PlayerLadder ladder, int starMin, int starMax) {
        PlayerLadder pl = null;
        //随机出匹配的玩家
        StringBuilder query = new StringBuilder();
        query.append("playerId NOT IN(");
        for (int id : ladder.getFightIds()) {
            query.append(id).append(",");
        }
        query.append(ladder.getPlayerId()).append(") AND star BETWEEN ")
                .append(starMin).append(" AND ").append(starMax);
        ResultSet rs = db.executeQuery(getRandomSQL("ladder", "playerId,star,total,win", query.toString()));
        try {
            if (rs.next()) {
                pl = new PlayerLadder();
                pl.setPlayerId(rs.getInt(1));
                pl.setStar(rs.getInt(2));
                pl.setTotal(rs.getInt(3));
                pl.setWin(rs.getInt(4));
            }
        } catch (SQLException e) {
            logger.error("通过星级查找匹配对手时发生异常", e);
        } finally {
            db.executeClose();
        }
        return pl;
    }

    public static String getRandomSQL(String table, String field, String query) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ").append(field).append(" FROM ( SELECT rowNo, FOUND_ROWS() AS ROWS, ")
                .append(field).append(" FROM ( SELECT (@rowNum:=@rowNum+1) AS rowNo, ")
                .append(field).append(" FROM ").append(table).append(", ( SELECT (@rowNum:=0)) AS r ")
                .append("WHERE ").append(query).append(") AS tp) AS t1, (SELECT RAND() AS random) AS t2 ")
                .append("WHERE CASE WHEN t1.rows > 1 THEN t1.rowNo = 1 + FLOOR(ROWS * random) ELSE t1.rowNo = 1 END");
        return builder.toString();
    }

    public static String getStartTimeSortSQL(String field, String query) {
        return "SELECT " + field + " FROM escort WHERE " + query + " ORDER BY startTime desc";
    }

    /**
     * 天梯竞技场每日零点任务
     */
    public int dailyClear(int id) {
        return db.executeSql("UPDATE ladder SET recoverTime=0, buyCount=0, fightIds='' where playerid=" + id);
    }

    public List<PlayerLadder> getAllLadders() {
        List<PlayerLadder> array = new ArrayList<>();
        try {
            ResultSet rs = db.executeQuery("select * from ladder where star>0");
            while (rs.next()) {
                array.add(initPlayerLadder(rs));
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            db.executeClose();
        }
        return array;
    }

    public List<PlayerLadder> getHistory() {
        List<PlayerLadder> array = new ArrayList<>();
        try {
            ResultSet rs = db.executeQuery("SELECT * FROM ladder WHERE lastRank > 0 ORDER BY lastRank LIMIT "
                    + ConstantModel.LADDER_HISTORY_NUM + ";");
            while (rs.next()) {
                array.add(initPlayerLadder(rs));
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            db.executeClose();
        }
        return array;
    }

    public List<PlayerLadder> getTopList() {
        List<PlayerLadder> array = new ArrayList<>();
        try {
            ResultSet rs = db.executeQuery("SELECT a.* FROM ladder a,player b WHERE a.playerId=b.id and b.state=" + GameDefine.PLAYER_STATE_NORMAL + " and score > 0 ORDER BY score DESC LIMIT "
                    + ConstantModel.LADDER_TOP_NUM + ";");
            while (rs.next()) {
                array.add(initPlayerLadder(rs));
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            db.executeClose();
        }
        return array;
    }

    /**
     * 天梯赛季排名
     *
     * @return
     */
    public int seasonRank() {
        int id = -1;
        try {
            //排名
            id = db.executeSql("UPDATE ladder SET lastStar=0, lastTotal=0, lastWin=0, lastRank=0;");
            id = db.executeSql("UPDATE ladder INNER JOIN (SELECT ladder.playerId,"
                    + " (@rowNum:=@rowNum+1) AS rowNo FROM ladder, (SELECT(@rowNum:=0)) b WHERE ladder.star>0 "
                    + " ORDER BY ladder.star DESC, ladder.win*2-ladder.total DESC) b ON ladder.playerId=b.playerId"
                    + " SET ladder.lastRank=b.rowNo, lastStar=star, lastTotal=total, lastWin=win");
        } catch (Exception e) {
            logger.error("进行天梯赛季排名时发生异常！！！", e);
            logger.error(e.getMessage(), e);
        } finally {
            db.executeClose();
        }
        return id;
    }

    /**
     * 重置天梯赛季数据
     *
     * @return
     */
    public int seasonReset() {
        int id = -1;
        try {
            //重置
            id = db.executeSql("UPDATE ladder SET star=0, total=0, win=0, lose=0, conwin=0, "
                    + "fightIds='', score=0");
        } catch (Exception e) {
            logger.error("重置天梯赛季数据时发生异常！！！", e);
            logger.error(e.getMessage(), e);
        } finally {
            db.executeClose();
        }
        return id;
    }

    public int getCurrLadderRank(int score) {
        int rank = -1;
        if (score <= 0)
            return rank;
        try {
            //获取名次
            ResultSet rs = db.executeQuery("SELECT COUNT(playerId) FROM ladder WHERE score > " + score);
            if (rs.next()) {
                rank = rs.getInt(1) + 1;
            }
        } catch (Exception e) {
            logger.error("获取个人天梯名次时发生异常！！！", e);
            logger.error(e.getMessage(), e);
        } finally {
            db.executeClose();
        }
        return rank;
    }

}
