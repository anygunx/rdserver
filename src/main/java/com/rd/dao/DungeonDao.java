package com.rd.dao;

import com.rd.bean.dungeon.Dungeon;
import com.rd.bean.dungeon.IDungeonTypeData;
import com.rd.dao.db.DBOperator;
import com.rd.define.DungeonDefine;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DungeonDao {
    private static final Logger logger = Logger.getLogger(DungeonDao.class);
    private DBOperator dbOperator = new DBOperator();

    public Map<Byte, Dungeon> getDungeonMap(int playerId) {
        Map<Byte, Dungeon> map = new HashMap<>();
        ResultSet rs = dbOperator.executeQuery("SELECT playerid,type,pass,dailytimes,sweep,typedata,passed FROM dungeon WHERE playerid=" + playerId);
        try {
            while (rs.next()) {
                Dungeon dungeon = new Dungeon();
                dungeon.setPlayerId(rs.getInt(1));
                dungeon.setType(rs.getByte(2));
                dungeon.setPass(rs.getShort(3));
                dungeon.setDailyTimesJson(rs.getString(4));
                dungeon.setSweep(rs.getByte(5));
                IDungeonTypeData typeData = DungeonDefine.EDungeon.builder(dungeon.getType(), rs.getString(6));
                dungeon.setTypeData(typeData);
                dungeon.setPassedJson(rs.getString(7));
                map.put(dungeon.getType(), dungeon);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            dbOperator.executeClose();
        }
        return map;
    }

    public int insertDungeon(Dungeon dungeon) {
        return dbOperator.executeSql("INSERT INTO dungeon( playerid, type, pass, dailytimes, sweep )" +
                "VALUES( " + dungeon.getPlayerId() + ", " + dungeon.getType() + ", " + dungeon.getPass() + ",'" + dungeon.getDailyTimesJson() + " '," + dungeon.getSweep() + ")"
        );
    }

    public int updateDungeon(Dungeon dungeon) {
        return dbOperator.executeSql("update dungeon set pass=" + dungeon.getPass() + ",dailytimes='" + dungeon.getDailyTimesJson() + "',sweep=" + dungeon.getSweep() + ",typedata='" + dungeon.getTypeDataJson() + "'" + ",passed='" + dungeon.getPassedJson() + "' where playerid=" + dungeon.getPlayerId() + " and type=" + dungeon.getType());
    }

    public int resetDungeon(int id) {
        return dbOperator.executeSql("update dungeon set dailytimes='{}',sweep=1, typedata='{}' where playerid=" + id);
    }
}
