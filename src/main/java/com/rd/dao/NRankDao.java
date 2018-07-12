package com.rd.dao;

import com.rd.bean.rank.PlayerRank;
import com.rd.dao.db.DBOperator;
import com.rd.define.NRankType;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NRankDao {


    private NRankDao() {
    }

    private static final NRankDao _instance = new NRankDao();

    public static NRankDao getInstance() {
        return _instance;
    }

    private static Logger logger = Logger.getLogger(NRankDao.class);
    private DBOperator db = new DBOperator();

    public List<PlayerRank> getRankList() {
        StringBuilder sb = new StringBuilder();
        List<PlayerRank> rankList = new ArrayList<>();
        try {
            sb.append(" SELECT id, name, level, vip, fighting, level")
                    .append("  FROM player ")
                    .append(" ORDER BY sjg_copy_id desc")
                    .append(" LIMIT ").append(20);

            if (sb.length() > 0) {
                ResultSet rs = db.executeQuery(sb.toString());
                rankList = createRankList(rs, NRankType.COPY_SJG);
            }
        } catch (Exception e) {
            logger.error("读取排行榜时发生异常.", e);
        } finally {
            db.executeClose();
        }
        return rankList;
    }


    public List<PlayerRank> getRankList(NRankType rankType) {
        StringBuilder sb = new StringBuilder();
        List<PlayerRank> rankList = new ArrayList<>();
        try {
            switch (rankType) {
                case COPY_SJG:
                    sb.append(" SELECT id, name, level, vip, fighting, sjg_copy_id")
                            .append("  FROM player ")
                            .append(" WHERE sjg_copy_id > 0 ")
                            .append(" ORDER BY sjg_copy_id desc")
                            .append(" LIMIT ").append(20);
                    break;

                case COPY_TM:

                    sb.append(" SELECT id, name, level, vip, fighting, tm_copy_max_id")
                            .append("  FROM player ")
                            .append(" WHERE tm_copy_max_id > 0 ")
                            .append(" ORDER BY tm_copy_max_id desc")
                            .append(" LIMIT ").append(20);
                    break;

                case COPY_MZ:
                    sb.append(" SELECT id, name, level, vip, fighting, mz_star")
                            .append("  FROM player ")
                            .append(" WHERE mz_star > 0 ")
                            .append(" ORDER BY mz_star desc")
                            .append(" LIMIT ").append(20);
                    break;

//			case JINGJI:
//				sb.append(" SELECT id, name, level, vip, fighting, mz_star")
//				.append("  FROM player ")
//				 .append(" WHERE jjRank < 5000 ")
//				.append(" ORDER BY mz_star desc")
//				.append(" LIMIT ").append(20);
//				break; 

                default:
                    throw new IllegalArgumentException("RankDao.getRankList() failed. Unexpected rankType=" + rankType);
            }
            if (sb.length() > 0) {
                ResultSet rs = db.executeQuery(sb.toString());
                rankList = createRankList(rs, rankType);
            }
        } catch (Exception e) {
            logger.error("读取排行榜时发生异常.", e);
        } finally {
            db.executeClose();
        }
        return rankList;
    }


    private List<PlayerRank> createRankList(ResultSet rs, NRankType copySjg) throws SQLException {
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
                playerRank.setRank(rank++);
                rankList.add(playerRank);
            }
        } catch (Exception e) {
            return null;
        }
        return rankList;
    }

}
