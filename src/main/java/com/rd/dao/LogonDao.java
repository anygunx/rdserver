package com.rd.dao;

import com.rd.bean.data.GameServer;
import com.rd.dao.db.DBOperator;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class LogonDao {

    private static Logger logger = Logger.getLogger(LogonDao.class);

    private DBOperator db = new DBOperator();

    /**
     * 获取所有的游戏服
     *
     * @return
     */
    public Map<Integer, GameServer> getAllGameServer() {
        Map<Integer, GameServer> servers = new HashMap<>();
        ResultSet rs = db.executeQuery("SELECT * FROM server;");
        try {
            while (rs.next()) {
                GameServer server = new GameServer();
                server.setId(rs.getShort(1));
                server.setName(rs.getString(2));
                server.setInnerHost(rs.getString(4));
                server.setPort(rs.getShort(5));
                server.setState(rs.getByte(6));
                server.setCreateTime(rs.getTimestamp(7));
                server.setPvp(rs.getInt(12));
                servers.put(server.getId(), server);
            }
        } catch (SQLException e) {
            logger.error("读取服务器列表发生异常.", e);
        } finally {
            db.executeClose();
        }
        return servers;
    }

}
