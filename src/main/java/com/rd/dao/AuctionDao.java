package com.rd.dao;

import com.rd.bean.auction.BaseAuction;
import com.rd.dao.db.DBOperator;
import com.rd.define.EAuction;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by XingYun on 2017/10/24.
 */
public class AuctionDao {

    public static Logger logger = Logger.getLogger(AuctionDao.class);

    private DBOperator db = new DBOperator();

    /**
     * 获取拍卖行数据
     *
     * @return
     */
    public Map<EAuction, BaseAuction> getShops() {
        Map<EAuction, BaseAuction> map = new HashMap<>();
        String sql = "SELECT id, items, logs FROM auction";
        ResultSet rs = db.executeQuery(sql);
        try {
            while (rs.next()) {
                byte id = rs.getByte(1);
                String items = rs.getString(2);
                String logs = rs.getString(3);
                EAuction type = EAuction.get(id);
                BaseAuction data = type.builder.build();
                data.init(items, logs);
                map.put(type, data);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            db.executeClose();
        }
        return map;
    }

    public int createAuction(BaseAuction auction) {
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT auction (id, items, logs) ")
                .append(" VALUES(")
                .append(auction.getId())
                .append(",'").append(auction.getItemsStr())
                .append("', '").append(auction.getLogsStr()).append("')");
        return db.executeSql(builder.toString());
    }

    public int updateAuction(BaseAuction auctionData) {
        StringBuilder builder = new StringBuilder();
        builder.append("UPDATE auction SET items='").append(auctionData.getItemsStr()).append("', ")
                .append("               logs='").append(auctionData.getLogsStr()).append("' ")
                .append(" WHERE id=").append(auctionData.getId());
        return db.executeSql(builder.toString());
    }

}
