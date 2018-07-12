package com.rd.dao;

import com.rd.bean.rank.PlayerRank;
import com.rd.dao.db.DBOperator;
import com.rd.define.ERankType;
import com.rd.model.ConstantModel;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 排行榜DAO
 *
 * @author Created by U-Demon on 2016年12月14日 下午12:00:42
 * @version 1.0.0
 */
public class RankDao {

    private static Logger logger = Logger.getLogger(RankDao.class);

    private DBOperator db = new DBOperator();

    private RankDao() {
    }

    private static final RankDao _instance = new RankDao();

    public static RankDao getInstance() {
        return _instance;
    }

    public List<PlayerRank> getRankList(ERankType rankType) {
        StringBuilder sb = new StringBuilder();
        List<PlayerRank> rankList = new ArrayList<>();
        int value2 = 0;
        try {
            switch (rankType) {
                case LEVEL:
                    sb.append(" SELECT id, name, level, vip, fighting, level")
                            .append("  FROM player ")
                            .append(" ORDER BY level desc, exp desc")
                            .append(" LIMIT ").append(ConstantModel.RANK_MAX);
                    break;

                case FIGHTING:
                    sb.append(" SELECT id, name, level, vip, fighting, fighting")
                            .append("  FROM player ")
                            .append(" WHERE fighting > 0 ")
                            .append(" ORDER BY fighting desc")
                            .append(" LIMIT ").append(ConstantModel.RANK_MAX);
                    break;

                case WING:

                    break;

                default:
                    throw new IllegalArgumentException("RankDao.getRankList() failed. Unexpected rankType=" + rankType);
            }
            if (sb.length() > 0) {
                ResultSet rs = db.executeQuery(sb.toString());
                rankList = createRankList(rs, rankType, value2);
            }
        } catch (Exception e) {
            logger.error("读取排行榜时发生异常.", e);
        } finally {
            db.executeClose();
        }
        return rankList;
    }

    private List<PlayerRank> createRankList(ResultSet rs, ERankType rankType, int value2) throws SQLException {
        List<PlayerRank> rankList = new ArrayList<>();
        try {
            int rank = 1;
            while (rs.next()) {
                PlayerRank playerRank = new PlayerRank();
                playerRank.setId(rs.getInt(1));
                playerRank.setName(rs.getString(2));
                playerRank.setLevel(rs.getShort(3));
                playerRank.setVip(rs.getInt(4));
                playerRank.setFighting(rs.getLong(5));
                playerRank.setValue(rs.getInt(6));
                playerRank.setValue2(value2);
                playerRank.setRank(rank++);
                rankList.add(playerRank);
            }
        } catch (Exception e) {
            return null;
        }
        return rankList;
    }

}
