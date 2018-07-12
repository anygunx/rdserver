package com.rd.dao;

import com.google.common.reflect.TypeToken;
import com.rd.bean.fight.monstersiege.GameMonsterAttacker;
import com.rd.bean.fight.monstersiege.PlayerMonsterData;
import com.rd.bean.fight.monstersiege.PlayerMonsterRecord;
import com.rd.dao.db.DBOperator;
import com.rd.model.MonsterSiegeModel;
import com.rd.util.StringUtil;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlayerMonsterSiegeDao {
    private static Logger logger = Logger.getLogger(PlayerMonsterSiegeDao.class);

    private DBOperator db = new DBOperator();

    public PlayerMonsterData getData(int playerId) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(" SELECT id, name, head, score, times, times_ts, box_mask, records ")
                .append("     FROM player_monster_siege ")
                .append("    WHERE id=").append(playerId);
        ResultSet rs = db.executeQuery(sqlBuilder.toString());
        try {
            if (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                byte head = rs.getByte(3);
                int score = rs.getInt(4);
                byte times = rs.getByte(5);
                long times_ts = rs.getLong(6);
                byte box_mask = rs.getByte(7);
                String recordsStr = rs.getString(8);
                List<PlayerMonsterRecord> recordList = StringUtil.isEmpty(recordsStr) ?
                        new ArrayList<>() :
                        StringUtil.gson2List(recordsStr, new TypeToken<List<PlayerMonsterRecord>>() {
                        });
                return new PlayerMonsterData(id, name, head, times_ts, times, score, box_mask, recordList);
            }
        } catch (SQLException e) {
            logger.error("获取玩家怪物攻城数据失败：", e);
        } finally {
            db.executeClose();
        }
        return null;
    }


    public int insert(PlayerMonsterData data) {
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO player_monster_siege (id,  name, head, score, times, times_ts, box_mask, records) ")
                .append(" VALUES ( ")
                .append(data.getId()).append(", '")
                .append(data.getName()).append("', ")
                .append(data.getHead()).append(", ")
                .append(data.getScore()).append(", ")
                .append(data.getTimes()).append(", ")
                .append(data.getTs()).append(", ")
                .append(data.getBoxMask()).append(", '")
                .append(data.getRecordsJson()).append("')");
        int id = -1;
        try {
            id = db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("插入玩家怪物攻城数据失败：" + builder.toString() + "\n" + e);
        } finally {
            db.executeClose();
        }
        return id;
    }

    public void update(PlayerMonsterData data) {
        StringBuilder builder = new StringBuilder();
        builder.append("  UPDATE player_monster_siege ")
                .append("    SET score=").append(data.getScore()).append(", ")
                .append("        times=").append(data.getTimes()).append(", ")
                .append("        times_ts=").append(data.getTs()).append(", ")
                .append("        box_mask=").append(data.getBoxMask()).append(", ")
                .append("        records='").append(data.getRecordsJson()).append("' ")
                .append("  WHERE id=").append(data.getId());
        try {
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("更新玩家怪物攻城数据失败：" + builder.toString() + "\n" + e);
        } finally {
            db.executeClose();
        }
    }

    /**
     * 增加积分
     * 可能用于离线更新
     *
     * @param score
     */
    public void addScore(int playerId, int score) {
        StringBuilder builder = new StringBuilder();
        builder.append("  UPDATE player_monster_siege ")
                .append("    SET score=score+").append(score)
                .append("  WHERE id=").append(playerId);
        try {
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("增加玩家怪物攻城积分失败：" + builder.toString() + "\n" + e);
        } finally {
            db.executeClose();
        }
    }

    public void clear() {
        StringBuilder builder = new StringBuilder();
        builder.append(" TRUNCATE table player_monster_siege ");
        try {
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("清理玩家怪物攻城数据失败：" + builder.toString() + "\n" + e);
        } finally {
            db.executeClose();
        }
    }

    public List<GameMonsterAttacker> getRankList() {
        List<GameMonsterAttacker> rankList = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(" SELECT id, name, head, score ")
                .append("     FROM player_monster_siege ")
                .append("    WHERE score>0 ")
                .append("    ORDER BY score DESC LIMIT ").append(MonsterSiegeModel.RANK_CAPACITY);
        ResultSet rs = db.executeQuery(sqlBuilder.toString());
        try {
            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                byte head = rs.getByte(3);
                int score = rs.getInt(4);
                GameMonsterAttacker attacker = new GameMonsterAttacker(id, name, head, score);
                rankList.add(attacker);
            }
        } catch (SQLException e) {
            logger.error("获取怪物城排行数据失败：", e);
        } finally {
            db.executeClose();
        }
        return rankList;
    }

}
