package com.rd.dao;

import com.google.common.reflect.TypeToken;
import com.rd.bean.fight.monstersiege.GameMonsterAttacker;
import com.rd.bean.fight.monstersiege.GameMonsterData;
import com.rd.bean.fight.monstersiege.state.MonsterSiegeDefine;
import com.rd.dao.db.DBOperator;
import com.rd.util.StringUtil;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class GameMonsterSiegeDao {
    private static Logger logger = Logger.getLogger(GameMonsterSiegeDao.class);

    private DBOperator db = new DBOperator();

    public HashMap<Short, GameMonsterData> getMonsters() {
        HashMap<Short, GameMonsterData> map = new HashMap<>();
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(" SELECT id, state, deadline, attackers, rewardFlag")
                .append("     FROM monster_siege ");
        ResultSet rs = db.executeQuery(sqlBuilder.toString());
        try {
            while (rs.next()) {
                short id = rs.getShort(1);
                byte stateId = rs.getByte(2);
                long deadline = rs.getLong(3);
                String attStr = rs.getString(4);
                boolean rewardFlag = rs.getBoolean(5);
                Map<Integer, GameMonsterAttacker> attackers = StringUtil.isEmpty(attStr) ?
                        new HashMap<>() :
                        StringUtil.gson2Map(attStr, new TypeToken<Map<Integer, GameMonsterAttacker>>() {
                        });
                MonsterSiegeDefine.EMonsterSiegeState state = MonsterSiegeDefine.EMonsterSiegeState.getType(stateId);
                GameMonsterData data = new GameMonsterData(id, state, attackers, deadline, rewardFlag);
                map.put(id, data);
            }
        } catch (SQLException e) {
            logger.error("获取怪物攻城数据失败：", e);
        } finally {
            db.executeClose();
        }
        return map;
    }


    public int insertMonster(GameMonsterData monsterData) {
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO monster_siege (id, state, deadline, attackers ) ")
                .append(" VALUES ( ")
                .append(monsterData.getId()).append(", ")
                .append(monsterData.getState()).append(", ")
                .append(monsterData.getDeadline()).append(", '")
                .append(monsterData.getAttackersJson()).append("')");
        int id = -1;
        try {
            id = db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("插入怪物数据失败：" + builder.toString() + "\n" + e);
        } finally {
            db.executeClose();
        }
        return id;
    }

    public void updateMonster(GameMonsterData monster) {
        StringBuilder builder = new StringBuilder();
        builder.append("  UPDATE monster_siege ")
                .append("    SET state=").append(monster.getState()).append(", ")
                .append("        deadline=").append(monster.getDeadline()).append(", ")
                .append("        attackers='").append(monster.getAttackersJson()).append("', ")
                .append("        rewardFlag=").append(monster.isRewardFlag())
                .append("  WHERE id=").append(monster.getId());
        try {
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("更新怪物攻城数据失败：" + builder.toString() + "\n" + e);
        } finally {
            db.executeClose();
        }
    }

    public void clear() {
        StringBuilder builder = new StringBuilder();
        builder.append(" TRUNCATE table monster_siege ");
        try {
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("清理怪物攻城数据失败：" + builder.toString() + "\n" + e);
        } finally {
            db.executeClose();
        }
    }

}
