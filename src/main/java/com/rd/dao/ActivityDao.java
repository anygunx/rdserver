package com.rd.dao;

import com.rd.activity.EActivityType;
import com.rd.bean.player.Player;
import com.rd.bean.player.PlayerActivity;
import com.rd.dao.db.DBOperator;
import com.rd.game.GameRankManager;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.util.DateUtil;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * 活动数据操作
 *
 * @author Created by U-Demon on 2016年12月5日 下午6:30:47
 * @version 1.0.0
 */
public class ActivityDao {

    public static Logger logger = Logger.getLogger(ActivityDao.class);

    private DBOperator db = new DBOperator();

    /**
     * 获取玩家活动数据
     *
     * @param playerId
     * @return
     */
    public PlayerActivity getPlayerActivity(int playerId) {
        PlayerActivity data = null;
        try {
            ResultSet rs = db.executeQuery("SELECT * FROM activity where playerId = " + playerId);
            if (rs.next()) {
                //读取数据
                data = initPlayerActivity(rs);
            } else {
                //新建数据
                data = createPlayerActivity(playerId);
            }
        } catch (Exception e) {
            logger.error("读取押镖数据发生异常，玩家ID：" + playerId, e);
        } finally {
            db.executeClose();
        }
        return data;
    }

    private PlayerActivity initPlayerActivity(ResultSet rs) {
        PlayerActivity data = new PlayerActivity();
        try {
            data.setPlayerId(rs.getInt("playerId"));
            data.setSignNum(rs.getShort("signNum"));
            data.setSignTime(rs.getLong("signTime"));
            data.setLoginDay(rs.getInt("loginDay"));
            String infos = rs.getString("loginInfo");
            List<Byte> infoList = new ArrayList<>();
            if (infos != null && infos.length() > 0) {
                for (String info : infos.split(",")) {
                    infoList.add(Byte.valueOf(info));
                }
            }
            data.setLoginInfos(infoList);
            String json = rs.getString("day7Mission");
            data.setDay7MissionJson(json);
            data.setMonthlyCardEnd(rs.getTimestamp("monthlyCardEnd"));
            data.setMonthlyCardReward(rs.getLong("monthlyCardReward"));
//			data.setPayRecordJson(rs.getString("payRecord"));
            data.setWelfareJson(rs.getString("welfare"));
            data.setRebatesJson(rs.getString("rebate"));
            data.setInvestsJson(rs.getString("invest"));
            data.setShopJson(rs.getString("shop"));
            data.setTlGiftJson(rs.getString("tlGift"));
            data.setCrashcowTimes(rs.getByte("crashcowtimes"));
            data.setTlHorseJson(rs.getString("tlhorse"));
            data.setShopSpringJson(rs.getString("shopspring"));
            data.setConsume(rs.getInt("consume"));
            data.setTzzp(rs.getInt("tzzp"));
            data.setLastJewelSingle(rs.getLong("lastJewelSingle"));
            data.setBuyOneJson(rs.getString("buyOne"));
            data.setBuyContinue(rs.getLong("buyContinue"));
            data.setShenTongJson(rs.getString("shenTong"));
            data.setPlayerShopRefresh(rs.getLong("playerShopRefresh"));
            data.setPlayerShopJson(rs.getString("playerShopItems"));
            data.setGiftJson(rs.getString("gift"));
            data.setTargetJson(rs.getString("target"));
            data.setLogonRewardsJson(rs.getString("logonRewards"));
            data.setRedpacketJson(rs.getString("redpacket"));
            data.setPayFeast(rs.getByte("payFeast"));
            data.setGoldTreeNum(rs.getShort("goldTreeNum"));
            data.setGoldTreeRewardJson(rs.getString("goldTreeReward"));
            data.setXunbaoCount(rs.getInt("xunbaoCount"));
            data.setXunbaoTime(rs.getLong("xunbaoTime"));
            data.setWishingJson(rs.getString("wishing"));
            data.setPayCountJson(rs.getString("paycountdata"));
            data.setPayConRewardJson(rs.getString("payConReward"));
            data.setFestLogonJson(rs.getString("festLogon"));
            data.setFestWishingJson(rs.getString("festWishing"));
            data.setFestConsume(rs.getInt("festConsume"));
            data.setFestTime(rs.getLong("festTime"));
            data.setFestReward(rs.getByte("festReward"));
            data.setWeekendPay(rs.getInt("weekendPay"));
            data.setWeekendTime(rs.getLong("weekendTime"));
            data.setWeekendReward(rs.getByte("weekendReward"));
            data.setPayConReward2Json(rs.getString("payConReward2"));
            data.setWanbaLogonJson(rs.getString("wanbaLogon"));
            data.setWeekendLogonJson(rs.getString("weekendLogon"));
            data.setLvGiftJson(rs.getString("lvGift"));
            data.setFestPay(rs.getInt("festPay"));
            data.setFestPayTime(rs.getLong("festPayTime"));
            data.setFestPayReward(rs.getByte("festPayReward"));
            data.setPayCumulateJson(rs.getString("payCumulateData"));
            data.setTurntableRound(rs.getInt("turntableRound"));
            data.setTurntableReceiveNumJson(rs.getString("turntableReceiveNum"));
            data.setTodayUsedOrderJson(rs.getString("todayUsedOrder"));
            data.setKamPoDay(rs.getInt("kamPoDay"));
            data.setReceivedLuckScore(rs.getString("receivedLuckScore"));
            data.setKamPoLuckScore(rs.getInt("kamPoLuckScore"));
            data.setTurntableData(rs.getInt("turntableData"));
            data.setPayDailyFirstStatus(rs.getInt("payDailyFirstStatus"));
            data.setFestPayConRewardJson(rs.getString("festPayConReward"));
            data.setFirecrackerLuckScore(rs.getInt("firecrackerLuckScore"));
            data.setReceivedFirecrackerLuckScore(rs.getString("receivedFirecrackerLuckScore"));
            data.setFirecrackerCount(rs.getInt("firecrackerCount"));
            data.setConsumeCumulateFixedJson(rs.getString("consumCumulateReceived"));
            data.setPayCumulateFixedJson(rs.getString("payCumulateFixedData"));
            data.setMonopolyCurrLevel(rs.getInt("monopolyCurrLevel"));
            data.setMonopolyTodayNumReceiveJson(rs.getString("monopolyTodayNumReceive"));
            data.setMonopolyLevelReceivedJson(rs.getString("monopolyLevelReceived"));
            data.setMonopolyTodayPlayLevel(rs.getInt("monopolyTodayPlayLevel"));
            data.setMonopolyTodayNum(rs.getInt("monopolyTodayNum"));
            data.setMonopolyResetNum(rs.getInt("monopolyResetNum"));
            data.setMonopolyCurrSteps(rs.getInt("monopolyCurrSteps"));
            data.setMonopolyNextLevel(rs.getInt("monopolyNextLevel"));
            data.setPuzzleReceivedJson(rs.getString("puzzleReceived"));
            data.setPuzzleRestTime(rs.getInt("puzzleRestTime"));
            data.setNewYearLogonRewardsJson(rs.getString("newYearLogonRewards"));
            data.setReceivedLuckScore2(rs.getString("receivedLuckScore2"));
            data.setKamPo2LuckScore(rs.getInt("kamPo2LuckScore"));
            data.setNewYearLoginDay(rs.getInt("newYearLoginDay"));
            data.setMonopolyUsedOrderJson(rs.getString("monopolyUsedOrder"));
            data.setMonopolyPlayerTime(rs.getInt("monopolyPlayerTime"));
            data.setNoRepeatTurntableNum(rs.getInt("noRepeatTurntableNum"));
            data.setFree(rs.getByte("free"));
            data.setNoRepeatTurntableLuck(rs.getInt("noRepeatTurntableLuck"));
            data.setNoRepeatTurntableAllJson(rs.getString("noRepeatTurntableAll"));
            data.setNoRepeatTurntableReceivedJson(rs.getString("noRepeatTurntableReceived"));
            data.setNoRepeatTurntablePayTime(rs.getLong("noRepeatTurntablePayTime"));
            data.setNoRepeatTurntableRefreshTime(rs.getLong("noRepeatTurntableRefreshTime"));
            data.setNoRepeatTurntableTargetedStr(rs.getString("noRepeatTurntableTargeted"));
            data.setKamPo2Count(rs.getInt("kamPo2Count"));
            data.setKamPo2CostsJson(rs.getString("kamPo2Costs"));
            data.setFourItemsJson(rs.getString("fourItems"));
            data.setReBuiedItemsJson(rs.getString("reBuiedItems"));
            data.setBuiedItemsJson(rs.getString("buiedItems"));
            data.setTreasureFree(rs.getByte("treasureFree"));
            data.setVouchersListJson(rs.getString("vouchersList"));
            data.setTreasureIntegral(rs.getInt("treasureIntegral"));
            data.setTreasureVouchers(rs.getInt("treasureVouchers"));
            data.setTreasuresRefreshTime(rs.getLong("treasuresRefreshTime"));
            data.setBuiedItemsCount(rs.getInt("buiedItemsCount"));
            data.setSetWordsNumsJson(rs.getString("setWordsNums"));
            data.setMonopoly1PlayedStep(rs.getInt("monopoly1PlayedStep"));
            data.setMonopoly1TodayPlayLevel(rs.getInt("monopoly1TodayPlayLevel"));
            data.setMonopoly1CurrLevel(rs.getInt("monopoly1CurrLevel"));
            data.setMonopoly1NextLevel(rs.getInt("monopoly1NextLevel"));
            data.setMonopoly1LevelReceivedJson(rs.getString("monopoly1LevelReceived"));
            data.setMonopoly1TodayStepReceiveJson(rs.getString("monopoly1TodayStepReceive"));
            data.setMonopoly1CurrSteps(rs.getInt("monopoly1CurrSteps"));
            data.setMonopoly1ResetNum(rs.getInt("monopoly1ResetNum"));
            data.setMonopoly1FreeNum(rs.getInt("monopoly1FreeNum"));
            data.setInvestFund(rs.getByte("investfund"));
            data.setSlotNewMachine(rs.getByte("slotnewmachine"));
            data.setSlotMachine(rs.getByte("slotmachine"));
            data.setLimitLimitLimit(rs.getShort("limitlimitlimit"));
            data.setSevenDayJson(rs.getString("sevenday"));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return data;
    }

    public PlayerActivity createPlayerActivity(int playerId) {
        //初始数据
        PlayerActivity data = new PlayerActivity();
        data.setPlayerId(playerId);
        //SQL
        StringBuilder sql = new StringBuilder("INSERT INTO activity(");
        sql.append("playerId,");
        sql.append("signNum,");
        sql.append("signTime,");
        sql.append("loginDay,");
        sql.append("loginInfo");
        sql.append(") VALUES(");
        sql.append(data.getPlayerId()).append(",");
        sql.append(data.getSignNum()).append(",");
        sql.append(data.getSignTime()).append(",");
        sql.append(data.getLoginDay()).append(",'");
        sql.append(data.getLoginJson()).append("'");
        sql.append(");");
        //插入数据
        if (db.executeSql(sql.toString()) < 0)
            return null;
        return data;
    }

    /**
     * 保存玩家登陆活动数据
     *
     * @param activity
     */
    public void updateLoginInfo(PlayerActivity activity) {
        try {
            db.executeSql("update activity set loginDay=" + activity.getLoginDay() + ",loginInfo='" +
                    activity.getLoginJson() + "' where playerId=" + activity.getPlayerId());
        } catch (Exception e) {
            logger.error("保存玩家活动登录数据时发生异常", e);
        }
        return;
    }

    public void updateLoginInfo2Fest(PlayerActivity activity) {
        try {
            db.executeSql("update activity set newYearLoginDay = " + activity.getNewYearLoginDay() + " where playerId=" + activity.getPlayerId());
        } catch (Exception e) {
            logger.error("保存玩家活动登录2节日数据时发生异常", e);
        }
        return;
    }

    public void updateDay7Mission(PlayerActivity activity) {
        try {
            db.executeSql("update activity set day7Mission='" +
                    activity.getDay7MissionJson() + "' where playerId=" + activity.getPlayerId());
        } catch (Exception e) {
            logger.error("保存玩家7日活动登录数据时发生异常", e);
        }
        return;
    }

    /**
     * 获取所有未到期的月卡用户
     *
     * @return
     */
    public List<PlayerActivity> getMonthlyCardList() {
        List<PlayerActivity> pas = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        String endTime = DateUtil.formatDateTime(System.currentTimeMillis());
        sb.append("select playerId, monthlyCardEnd, monthlyCardReward from activity where monthlyCardEnd > '")
                .append(endTime).append("'");
        ResultSet rs = db.executeQuery(sb.toString());
        try {
            while (rs.next()) {
                PlayerActivity pa = new PlayerActivity();
                pa.setPlayerId(rs.getInt(1));
                pa.setMonthlyCardEnd(rs.getTimestamp(2));
                pa.setMonthlyCardReward(rs.getLong(3));
                pas.add(pa);
            }
        } catch (SQLException e) {
            logger.error("读取有效月卡数据时发生异常.", e);
        } finally {
            db.executeClose();
        }
        return pas;
    }

    /**
     * 获取所有的红包
     *
     * @return
     */
    public List<PlayerActivity> getAllRedpackets() {
        List<PlayerActivity> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("select playerId, redpacket from activity");
        ResultSet rs = db.executeQuery(sb.toString());
        try {
            while (rs.next()) {
                PlayerActivity pa = new PlayerActivity();
                pa.setPlayerId(rs.getInt(1));
                pa.setRedpacketJson(rs.getString(2));
                list.add(pa);
            }
        } catch (SQLException e) {
            logger.error("读取红包数据时发生异常.", e);
        } finally {
            db.executeClose();
        }
        return list;
    }

    /**
     * 获取所有终生卡用户
     *
     * @return
     */
    public List<Player> getForeverCardList() {
        List<Player> pas = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("select id, forever, foreverReward from player where forever > 0");
        ResultSet rs = db.executeQuery(sb.toString());
        try {
            while (rs.next()) {
                Player pa = new Player();
                pa.setId(rs.getInt(1));
                pa.setForever(rs.getInt(2));
                pa.setForeverReward(rs.getLong(3));
                pas.add(pa);
            }
        } catch (SQLException e) {
            logger.error("读取有效终生卡数据时发生异常.", e);
        } finally {
            db.executeClose();
        }
        return pas;
    }

    public List<PlayerActivity> getXunBaoTopList() {
        List<PlayerActivity> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("select playerId, xunbaoCount, xunbaoTime from activity where xunbaoCount > 0 ")
                .append("order by xunbaoCount desc, xunbaoTime limit 20");
        ResultSet rs = db.executeQuery(sb.toString());
        try {
            while (rs.next()) {
                PlayerActivity pa = new PlayerActivity();
                pa.setPlayerId(rs.getInt(1));
                pa.setXunbaoCount(rs.getInt(2));
                pa.setXunbaoTime(rs.getLong(3));
                list.add(pa);
            }
        } catch (SQLException e) {
            logger.error("读取寻宝数据时发生异常.", e);
        } finally {
            db.executeClose();
        }
        return list;
    }

    public List<PlayerActivity> getFestTopList() {
        List<PlayerActivity> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("select playerId, festConsume, festTime from activity where festConsume >= ")
                .append(GameRankManager.FEST_VALUE_MIN)
                .append(" order by festConsume desc, festTime limit ").append(GameRankManager.FEST_RANK_MAX);
        ResultSet rs = db.executeQuery(sb.toString());
        try {
            while (rs.next()) {
                PlayerActivity pa = new PlayerActivity();
                pa.setPlayerId(rs.getInt(1));
                pa.setFestConsume(rs.getInt(2));
                pa.setFestTime(rs.getLong(3));
                list.add(pa);
            }
        } catch (SQLException e) {
            logger.error("读取活动排行数据时发生异常.", e);
        } finally {
            db.executeClose();
        }
        return list;
    }

    public List<PlayerActivity> getWeekendTopList() {
        List<PlayerActivity> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("select playerId, weekendPay, weekendTime from activity where weekendPay >= ")
                .append(GameRankManager.WEEKEND_VALUE_MIN)
                .append(" order by weekendPay desc, weekendTime limit ").append(GameRankManager.WEEKEND_RANK_MAX);
        ResultSet rs = db.executeQuery(sb.toString());
        try {
            while (rs.next()) {
                PlayerActivity pa = new PlayerActivity();
                pa.setPlayerId(rs.getInt(1));
                pa.setWeekendPay(rs.getInt(2));
                pa.setWeekendTime(rs.getLong(3));
                list.add(pa);
            }
        } catch (SQLException e) {
            logger.error("读取活动排行数据时发生异常.", e);
        } finally {
            db.executeClose();
        }
        return list;
    }

    public List<PlayerActivity> getFestPayTopList() {
        List<PlayerActivity> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("select playerId, festPay, festPayTime from activity where festPay >= ")
                .append(GameRankManager.FESTPAY_VALUE_MIN)
                .append(" order by festPay desc, festPayTime limit ").append(GameRankManager.FESTPAY_RANK_MAX);
        ResultSet rs = db.executeQuery(sb.toString());
        try {
            while (rs.next()) {
                PlayerActivity pa = new PlayerActivity();
                pa.setPlayerId(rs.getInt(1));
                pa.setFestPay(rs.getInt(2));
                pa.setFestPayTime(rs.getLong(3));
                list.add(pa);
            }
        } catch (SQLException e) {
            logger.error("读取活动排行数据时发生异常.", e);
        } finally {
            db.executeClose();
        }
        return list;
    }

    public void updateMonthlyCardRewardList() {
        try {
            long curr = System.currentTimeMillis();
            StringBuilder sb = new StringBuilder();
            sb.append("update activity set monthlyCardReward=").append(curr)
                    .append(" where monthlyCardEnd>'").append(new Timestamp(curr)).append("'");
            db.executeSql(sb.toString());
        } catch (Exception e) {
            logger.error("保存玩家月卡数据时发生异常", e);
        }
        return;
    }

    public void updateForeverCardRewardList() {
        try {
            long curr = System.currentTimeMillis();
            StringBuilder sb = new StringBuilder();
            sb.append("update player set foreverReward=").append(curr)
                    .append(" where forever > 0");
            db.executeSql(sb.toString());
        } catch (Exception e) {
            logger.error("保存玩家终生卡数据时发生异常", e);
        }
        return;
    }

    public void updateMonthlyCard(PlayerActivity activity) {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("update activity set monthlyCardEnd='").append(new Timestamp(activity.getMonthlyCardEnd().getTime()))
                    .append("', monthlyCardReward=").append(activity.getMonthlyCardReward())
                    .append(" where playerId=").append(activity.getPlayerId());
            db.executeSql(sql.toString());
        } catch (Exception e) {
            logger.error("保存玩家月卡数据时发生异常", e);
        }
        return;
    }

    public void updateForever(Player player) {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("update player set forever=").append(player.getForever())
                    .append(", foreverReward=").append(player.getForeverReward())
                    .append(" where id=").append(player.getId());
            db.executeSql(sql.toString());
        } catch (Exception e) {
            logger.error("保存玩家终生卡数据时发生异常", e);
        }
        return;
    }

    public void updateWishingInfo(PlayerActivity activity) {
        try {
            db.executeSql("update activity set wishing='" + activity.getWishingJson() +
                    "' where playerId=" + activity.getPlayerId());
        } catch (Exception e) {
            logger.error("保存玩家许愿池数据时发生异常", e);
        }
        return;
    }

    public void updateFestWishingInfo(PlayerActivity activity) {
        try {
            db.executeSql("update activity set festWishing='" + activity.getFestWishingJson() +
                    "' where playerId=" + activity.getPlayerId());
        } catch (Exception e) {
            logger.error("保存玩家节日许愿池数据时发生异常", e);
        }
        return;
    }

    public void updatePayConReward(PlayerActivity activity) {
        try {
            db.executeSql("update activity set payConReward='" + activity.getPayConRewardJson() +
                    "' where playerId=" + activity.getPlayerId());
        } catch (Exception e) {
            logger.error("保存玩家连续充值领取数据时发生异常", e);
        }
        return;
    }

    public void updateFestPayConReward(PlayerActivity activity) {
        try {
            db.executeSql("update activity set festPayConReward='" + activity.getFestPayConRewardJson() +
                    "' where playerId=" + activity.getPlayerId());
        } catch (Exception e) {
            logger.error("保存玩家连续充值领取数据时发生异常", e);
        }
        return;
    }

    public void updatePayConReward2(PlayerActivity activity) {
        try {
            db.executeSql("update activity set payConReward2='" + activity.getPayConReward2Json() +
                    "' where playerId=" + activity.getPlayerId());
        } catch (Exception e) {
            logger.error("保存玩家连续充值领取数据时发生异常", e);
        }
        return;
    }

    public void updateFestLogon(PlayerActivity activity) {
        try {
            db.executeSql("update activity set festLogon='" + activity.getFestLogonJson() +
                    "' where playerId=" + activity.getPlayerId());
        } catch (Exception e) {
            logger.error("保存玩家节日登录数据时发生异常", e);
        }
        return;
    }

    public void updateWeekendLogon(PlayerActivity activity) {
        try {
            db.executeSql("update activity set weekendLogon='" + activity.getWeekendLogonJson() +
                    "' where playerId=" + activity.getPlayerId());
        } catch (Exception e) {
            logger.error("保存玩家节日登录数据时发生异常", e);
        }
        return;
    }

    public void updateWanbaLogon(PlayerActivity activity) {
        try {
            db.executeSql("update activity set wanbaLogon='" + activity.getWanbaLogonJson() +
                    "' where playerId=" + activity.getPlayerId());
        } catch (Exception e) {
            logger.error("保存玩家节日登录数据时发生异常", e);
        }
        return;
    }

    public void updateLeichongReward(PlayerActivity activity) {
        try {
            db.executeSql("update activity set paycountdata='" + activity.getPayCountJson() +
                    "' where playerId=" + activity.getPlayerId());
        } catch (Exception e) {
            logger.error("保存玩家累充豪礼数据时发生异常", e);
        }
        return;
    }

//	public void updatePayRecord(PlayerActivity activity)
//	{
//		try {
//			db.executeSql("update activity set payRecord='"+activity.getPayRecordJson()+
//					"' where playerId="+activity.getPlayerId());
//		} catch (Exception e) {
//			logger.error("保存玩家充值记录数据时发生异常", e);
//		}
//		return;
//	}

    public void updateWelfareJson(PlayerActivity activity) {
        try {
            db.executeSql("update activity set welfare='" + activity.getWelfareJson() +
                    "' where playerId=" + activity.getPlayerId());
        } catch (Exception e) {
            logger.error("保存玩家每日福利记录数据时发生异常", e);
        }
        return;
    }

    public void updateRebateJson(PlayerActivity activity) {
        try {
            db.executeSql("update activity set rebate='" + activity.getRebatesJson() +
                    "' where playerId=" + activity.getPlayerId());
        } catch (Exception e) {
            logger.error("保存玩家百倍返利记录数据时发生异常", e);
        }
        return;
    }

    public void updateInvestJson(PlayerActivity activity) {
        try {
            db.executeSql("update activity set invest='" + activity.getInvestsJson() +
                    "' where playerId=" + activity.getPlayerId());
        } catch (Exception e) {
            logger.error("保存玩家投资计划数据时发生异常", e);
        }
        return;
    }

    public void updateShopJson(PlayerActivity activity) {
        try {
            db.executeSql("update activity set shop='" + activity.getShopJson() +
                    "' where playerId=" + activity.getPlayerId());
        } catch (Exception e) {
            logger.error("保存玩家限时商城数据时发生异常", e);
        }
        return;
    }

    public void updateGiftJson(PlayerActivity activity) {
        try {
            db.executeSql("update activity set gift='" + activity.getGiftJson() +
                    "' where playerId=" + activity.getPlayerId());
        } catch (Exception e) {
            logger.error("保存玩家礼包购买数据时发生异常", e);
        }
        return;
    }

    public void clearShopJson() {
        try {
            db.executeSql("update activity set shop=''");
        } catch (Exception e) {
            logger.error("清空玩家限时商城数据时发生异常", e);
        }
        return;
    }

    public void updatePlayerShop(PlayerActivity activity) {
        try {
            db.executeSql("update activity set playerShopRefresh=" + activity.getPlayerShopRefresh() +
                    ",playerShopItems='" + activity.getPlayerShopJson() +
                    "' where playerId=" + activity.getPlayerId());
        } catch (Exception e) {
            logger.error("保存玩家刷新商城数据时发生异常", e);
        }
        return;
    }

    public void updateTlGiftJson(PlayerActivity activity) {
        try {
            db.executeSql("update activity set tlGift='" + activity.getTlGiftJson() +
                    "' where playerId=" + activity.getPlayerId());
        } catch (Exception e) {
            logger.error("保存玩家限时有礼数据时发生异常", e);
        }
        return;
    }

    public void updateLvGiftJson(PlayerActivity activity) {
        try {
            db.executeSql("update activity set lvGift='" + activity.getLvGiftJson() +
                    "' where playerId=" + activity.getPlayerId());
        } catch (Exception e) {
            logger.error("保存玩家限时有礼数据时发生异常", e);
        }
        return;
    }

    public void updateBuyOneJson(PlayerActivity activity) {
        try {
            db.executeSql("update activity set buyOne='" + activity.getBuyOneJson() +
                    "' where playerId=" + activity.getPlayerId());
        } catch (Exception e) {
            logger.error("保存玩家一元抢购数据时发生异常", e);
        }
        return;
    }

    public void updateBuyContinue(PlayerActivity activity) {
        try {
            db.executeSql("update activity set buyContinue=" + activity.getBuyContinue() +
                    " where playerId=" + activity.getPlayerId());
        } catch (Exception e) {
            logger.error("保存玩家连续购买数据时发生异常", e);
        }
        return;
    }

    public void updateShenTong(PlayerActivity activity) {
        try {
            db.executeSql("update activity set shenTong='" + activity.getShenTongJson() +
                    "' where playerId=" + activity.getPlayerId());
        } catch (Exception e) {
            logger.error("保存玩家一折神通数据时发生异常", e);
        }
        return;
    }

    public void updateTarget(PlayerActivity activity) {
        try {
            db.executeSql("update activity set target='" + activity.getTargetJson() +
                    "' where playerId=" + activity.getPlayerId());
        } catch (Exception e) {
            logger.error("保存玩家达标活动领奖数据时发生异常", e);
        }
        return;
    }

    public void updateXunbao(PlayerActivity activity) {
        try {
            db.executeSql("update activity set xunbaoCount=" + activity.getXunbaoCount() + ", xunbaoTime="
                    + activity.getXunbaoTime() + " where playerId=" + activity.getPlayerId());
        } catch (Exception e) {
            logger.error("保存玩家寻宝数据时发生异常", e);
        }
        return;
    }

    public void updateLogonRewards(PlayerActivity activity) {
        try {
            db.executeSql("update activity set logonRewards='" + activity.getLogonRewardsJson() +
                    "' where playerId=" + activity.getPlayerId());
        } catch (Exception e) {
            logger.error("保存玩家达标活动领奖数据时发生异常", e);
        }
        return;
    }

    public void updateNewYearLogonRewards(PlayerActivity activity) {
        try {
            db.executeSql("update activity set newYearLogonRewards='" + activity.getNewYearLogonRewardsJson() +
                    "' where playerId=" + activity.getPlayerId());
        } catch (Exception e) {
            logger.error("保存玩家新年登录活动领奖数据时发生异常", e);
        }
        return;
    }

    public void updateCrashcowTimes(PlayerActivity activity) {
        try {
            db.executeSql("update activity set crashcowtimes=" + activity.getCrashcowTimes() +
                    " where playerId=" + activity.getPlayerId());
        } catch (Exception e) {
            logger.error("保存玩家摇钱次数时发生异常", e);
        }
        return;
    }

    public void clearCrashCowTimes() {
        try {
            db.executeSql("update activity set crashcowtimes=0 where crashcowtimes<>0");
        } catch (Exception e) {
            logger.error("清理玩家摇钱次数时发生异常", e);
        }
        return;
    }

    public void clearTLGift() {
        try {
            db.executeSql("UPDATE activity SET tlGift=NULL WHERE tlGift IS NOT NULL");
        } catch (Exception e) {
            logger.error("清理玩家限时有礼发生异常", e);
        }
        return;
    }

    public void updateTLHorseJson(PlayerActivity activity) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET tlhorse='").append(activity.getTlHorseJson()).append("' ")
                    .append(" WHERE playerId=").append(activity.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家限时坐骑数据时发生异常", e);
        }
        return;
    }

    public void updateRedpacketJson(PlayerActivity activity) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET redpacket='").append(activity.getRedpacketJson()).append("' ")
                    .append(" WHERE playerId=").append(activity.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家限时坐骑数据时发生异常", e);
        }
        return;
    }

    public void updateGoldTree(PlayerActivity activity) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET goldTreeReward='").append(activity.getGoldTreeRewardJson()).append("' ")
                    .append(", goldTreeNum = ").append(activity.getGoldTreeNum())
                    .append(" WHERE playerId=").append(activity.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家摇钱树数据时发生异常", e);
        }
        return;
    }

    public void updatePayFeast(PlayerActivity activity) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET payFeast=").append(activity.getPayFeast())
                    .append(" WHERE playerId=").append(activity.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家限时坐骑数据时发生异常", e);
        }
        return;
    }

    public void updateSign(PlayerActivity activityData) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET signNum=").append(activityData.getSignNum()).append(",signTime=").append(activityData.getSignTime())
                    .append(" WHERE playerId= " + activityData.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("更新玩家签到数据时发生异常", e);
        }
    }

    public void clearSignData() {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET signNum=0 ,signTime=0 ")
                    .append(" WHERE signNum<>0 || signTime <>0 ");
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("清理玩家签到数据时发生异常", e);
        }
        return;
    }

    public void updateShopSpringJson(PlayerActivity activity) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET shopspring='").append(activity.getShopSpringJson()).append("' ")
                    .append(" WHERE playerId= " + activity.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家春节限时商城数据时发生异常", e);
        }
        return;
    }

    public void updateTzzp(PlayerActivity activity) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET tzzp=").append(activity.getTzzp())
                    .append(" WHERE playerId= " + activity.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家投资转盘数据时发生异常", e);
        }
        return;
    }

    public void updateLastJewelSingle(PlayerActivity activity) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET lastJewelSingle=").append(activity.getLastJewelSingle())
                    .append(" WHERE playerId= " + activity.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家宝石活动数据时发生异常", e);
        }
        return;
    }

    public void clearLastJewelSingle() {
        try {
            db.executeSql("UPDATE activity SET lastJewelSingle=1 WHERE lastJewelSingle>0");
        } catch (Exception e) {
            logger.error("充值玩家宝石活动数据时发生异常", e);
        }
        return;
    }

    public void clearBuyOne() {
        try {
            db.executeSql("UPDATE activity SET buyOne=''");
        } catch (Exception e) {
            logger.error("充值玩家宝石活动数据时发生异常", e);
        }
        return;
    }

    public void clearShenTong() {
        try {
            db.executeSql("UPDATE activity SET shenTong=''");
        } catch (Exception e) {
            logger.error("一折神通活动数据时发生异常", e);
        }
        return;
    }

    public void clearShopSpring() {
        try {
            db.executeSql("UPDATE activity SET shopspring='' WHERE shopspring<>'' ");
        } catch (Exception e) {
            logger.error("清空玩家春节限时商城数据时发生异常", e);
        }
        return;
    }

    public void updateConsumeData(PlayerActivity activity) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET consume=").append(activity.getConsume())
                    .append(" WHERE playerId= " + activity.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家累计消费总额数据时发生异常", e);
        }
        return;
    }

    public void updateFestConsumeData(PlayerActivity activity) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET festConsume=").append(activity.getFestConsume()).append(",")
                    .append("	festTime=").append(activity.getFestTime()).append(",")
                    .append("	festReward=").append(activity.getFestReward())
                    .append(" WHERE playerId= " + activity.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家累计消费总额数据时发生异常", e);
        }
        return;
    }

    public void updateWeekendPayData(PlayerActivity activity) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET weekendPay=").append(activity.getWeekendPay()).append(",")
                    .append("	weekendTime=").append(activity.getWeekendTime()).append(",")
                    .append("	weekendReward=").append(activity.getWeekendReward())
                    .append(" WHERE playerId= " + activity.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家累计充值总额数据时发生异常", e);
        }
        return;
    }

    public void updateFestPayData(PlayerActivity activity) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET festPay=").append(activity.getFestPay()).append(",")
                    .append("	festPayTime=").append(activity.getFestPayTime()).append(",")
                    .append("	festPayReward=").append(activity.getFestPayReward())
                    .append(" WHERE playerId= " + activity.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家累计充值总额数据时发生异常", e);
        }
        return;
    }

    public void updatePayCumulate(PlayerActivity activity) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET payCumulateData='").append(activity.getPayCumulateJson()).append("'")
                    .append(" WHERE playerId= " + activity.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家累计充数据时发生异常", e);
        }
        return;
    }

    public void updatePayCumulateFixed(PlayerActivity activity) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET payCumulateFixedData='").append(activity.getPayCumulateFixedJson()).append("'")
                    .append(" WHERE playerId= " + activity.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家固定时间累计充数据时发生异常", e);
        }
        return;
    }

    public void updateTurntable(PlayerActivity activity) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET turntableData=" + activity.getTurntableData())
                    .append(" WHERE playerId= " + activity.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家累计充数据时发生异常", e);
        }
        return;
    }

    public void updateTurntableRound(PlayerActivity activity) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET turntableRound= " + activity.getTurntableRound())
                    .append(" WHERE playerId= " + activity.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家累计充数据时发生异常", e);
        }
        return;
    }

    public void clearPayFeast() {
        try {
            db.executeSql(" UPDATE activity SET payFeast = 0; ");
        } catch (Exception e) {
            logger.error("保存玩家累计消费总额数据时发生异常", e);
        }
        return;
    }

    public void clearConsumeData() {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET consume=0 ")
                    .append(" WHERE consume<>0 ");
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("清理玩家累计消费总额数据时发生异常", e);
        }
        return;
    }

    public void clearRebate(EActivityType type) {
        StringBuilder sb = new StringBuilder();
        sb.append("select playerId, rebate from activity;");
        ResultSet rs = db.executeQuery(sb.toString());
        try {
            while (rs.next()) {
                PlayerActivity pa = new PlayerActivity();
                pa.setPlayerId(rs.getInt(1));
                pa.setRebatesJson(rs.getString(2));
                if (pa.getRebates() == null || pa.getRebates().size() == 0)
                    continue;
                boolean save = false;
                for (int i = pa.getRebates().size() - 1; i >= 0; i--) {
                    if (pa.getRebates().get(i) / 10000 == type.getId()) {
                        pa.getRebates().remove(i);
                        save = true;
                    }
                }
                if (save) {
                    StringBuilder upSql = new StringBuilder();
                    upSql.append("UPDATE activity SET rebate = '").append(pa.getRebatesJson())
                            .append("' where playerId=").append(pa.getPlayerId()).append(";");
                    new DBOperator().executeSql(upSql.toString());
                }
            }
        } catch (SQLException e) {
            logger.error("读取有效月卡数据时发生异常.", e);
        } finally {
            db.executeClose();
        }
        return;
    }

    public void clearTargetReward() {
        try {
            db.executeSql("UPDATE activity SET target = '[]';");
        } catch (Exception e) {
            logger.error("清理玩家累计消费总额数据时发生异常", e);
        }
    }

    public void clearGoldTree() {
        try {
            db.executeSql("UPDATE activity SET goldTreeNum = 0, goldTreeReward = '';");
        } catch (Exception e) {
            logger.error("清理玩家摇钱树数据时发生异常", e);
        }
    }

    public void clearXunbao() {
        try {
            db.executeSql("UPDATE activity SET xunbaoCount = 0, xunbaoTime = 0;");
        } catch (Exception e) {
            logger.error("清理玩家寻宝数据时发生异常", e);
        }
    }

    public void clearFestRank() {
        try {
            db.executeSql("UPDATE activity SET festConsume = 0, festTime = 0, festReward = 0;");
        } catch (Exception e) {
            logger.error("清理玩家活动排行数据时发生异常", e);
        }
    }

    public void clearWeekendRank() {
        try {
            db.executeSql("UPDATE activity SET weekendPay = 0, weekendTime = 0, weekendReward = 0;");
        } catch (Exception e) {
            logger.error("清理玩家活动排行数据时发生异常", e);
        }
    }

    public void clearFestPayRank() {
        try {
            db.executeSql("UPDATE activity SET festPay = 0, festPayTime = 0, festPayReward = 0;");
        } catch (Exception e) {
            logger.error("清理玩家活动排行数据时发生异常", e);
        }
    }

    public void clearWishingWell() {
        try {
            db.executeSql("UPDATE activity SET wishing = '', festWishing = '';");
        } catch (Exception e) {
            logger.error("清理玩家许愿池数据时发生异常", e);
        }
    }

    public void cleaGift() {
        try {
            db.executeSql("UPDATE activity SET gift = '';");
        } catch (Exception e) {
            logger.error("清理玩家许愿池数据时发生异常", e);
        }
    }

    public void cleaFuDai() {
        try {
            db.executeSql("UPDATE activity SET tzzp = 0;");
        } catch (Exception e) {
            logger.error("清理玩家许愿池数据时发生异常", e);
        }
    }

    public void clearFestLogon() {
        try {
            db.executeSql("UPDATE activity SET festLogon = '';");
            for (GameRole role : GameWorld.getPtr().getOnlineRoles().values()) {
                role.getActivityManager().getActivityData().getFestLogon().clear();
            }
        } catch (Exception e) {
            logger.error("清理玩家节日登录数据时发生异常", e);
        }
    }

    public void clearWeekendLogon() {
        try {
            db.executeSql("UPDATE activity SET weekendLogon = '';");
            for (GameRole role : GameWorld.getPtr().getOnlineRoles().values()) {
                role.getActivityManager().getActivityData().getWeekendLogon().clear();
            }
        } catch (Exception e) {
            logger.error("清理玩家节日登录数据时发生异常", e);
        }
    }

    public void clearWanbaLogon() {
        try {
            db.executeSql("UPDATE activity SET wanbaLogon = '';");
            for (GameRole role : GameWorld.getPtr().getOnlineRoles().values()) {
                role.getActivityManager().getActivityData().getWanbaLogon().clear();
            }
        } catch (Exception e) {
            logger.error("清理玩家节日登录数据时发生异常", e);
        }
    }

    public int cleanActivity() {
        return db.executeSql("update activity set shopspring='',tlGift=NULL,crashcowtimes=0,lastJewelSingle=1,buyOne='',shenTong='',target = '[]',goldTreeNum = 0, goldTreeReward = '',xunbaoCount = 0, xunbaoTime = 0,festConsume = 0, festTime = 0, festReward = 0,weekendPay = 0, weekendTime = 0, weekendReward = 0,festPay = 0, festPayTime = 0, festPayReward = 0,wishing = '', festWishing = '',gift = '', payCumulateData='', turntableReceiveNum = '', todayUsedOrder = '', turntableRound = 0, turntableData = 0, consumCumulateReceived = null, payDailyFirstStatus = 0, monopoly1PlayedStep = 0, monopoly1TodayStepReceive = null, monopoly1ResetNum = 0, monopoly1FreeNum = 0");
    }

    public int getTurntableRound(int activityId) {
        ResultSet result = db.executeQuery("select turntableRound from activity where playerId = " + activityId);
        int i = 0;
        try {
            if (result.next()) {
                i = result.getInt(1);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return i;
    }

    public void updateTurntableReceiveNums(PlayerActivity activity) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET turntableReceiveNum='").append(activity.getTurntableReceiveNumJson()).append("'")
                    .append(" WHERE playerId= " + activity.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家累计充数据时发生异常", e);
        }
        return;

    }

    public void updateTodayUsedOrder(PlayerActivity activity) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET todayUsedOrder='").append(activity.getTodayUsedOrderJson()).append("'")
                    .append(" WHERE playerId= " + activity.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家累计充数据时发生异常", e);
        }
        return;

    }

    public String getTodayUsedOrder(PlayerActivity activity) {
        ResultSet result = db.executeQuery("select todayUsedOrder from activity where playerId = " + activity.getPlayerId());
        String i = "";
        try {
            if (result.next()) {
                i = result.getString(1);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return i;
    }

    public int getKamPoDay(PlayerActivity activity) {
        ResultSet result = db.executeQuery("select kamPoDay from activity where playerId = " + activity.getPlayerId());
        int i = 0;
        try {
            if (result.next()) {
                i = result.getInt(1);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return i;
    }

    public void updateKamPoDay(PlayerActivity activity) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET kamPoDay=" + activity.getKamPoDay())
                    .append(" WHERE playerId= " + activity.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家幸运鉴宝所属组时发生异常", e);
        }
        return;
    }

    public int getTurntableData(PlayerActivity activity) {
        ResultSet result = db.executeQuery("select turntableData from activity where playerId = " + activity.getPlayerId());
        int i = 0;
        try {
            if (result.next()) {
                i = result.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }

    public void updateReceiveLuckScore(PlayerActivity activity) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET receivedLuckScore='").append(activity.getReceivedLuckScoreJson()).append("'")
                    .append(" WHERE playerId= " + activity.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家累计充数据时发生异常", e);
        }
        return;

    }

    public void updateReceiveLuckScore2(PlayerActivity activity) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET receivedLuckScore2='").append(activity.getReceivedLuckScore2Json()).append("'")
                    .append(" WHERE playerId= " + activity.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家累计充数据时发生异常", e);
        }
        return;

    }

    public void updateReceiveFirescrackerLuckScore(PlayerActivity activity) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET receivedFirecrackerLuckScore='").append(activity.getReceivedFirecrackerLuckScoreJson()).append("'")
                    .append(" WHERE playerId= " + activity.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家累计充数据时发生异常", e);
        }
        return;

    }

    public void updateKamPoLuckScore(PlayerActivity activity) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET kamPoLuckScore='").append(activity.getKamPoLuckScore()).append("'")
                    .append(" WHERE playerId= " + activity.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家累计充数据时发生异常", e);
        }
        return;
    }

    public void updateKamPo2LuckScore(PlayerActivity activity) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET kamPo2LuckScore='").append(activity.getKamPo2LuckScore()).append("'")
                    .append(" WHERE playerId= " + activity.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家累计充数据时发生异常", e);
        }
        return;
    }

    public void updateKamPo2(PlayerActivity activity) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET kamPo2Count=").append(activity.getKamPo2Count())
                    .append(" ,kamPo2Costs='").append(activity.getKamPo2CostsStr()).append("'")
                    .append(" WHERE playerId= " + activity.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家累计充数据时发生异常", e);
        }
        return;
    }

    public List<PlayerActivity> getPlayerActivitys2KamPo() {
        List<PlayerActivity> datas = new ArrayList<>();
        ResultSet result = db.executeQuery("select playerId,kamPo2Count,kamPo2Costs from activity where kamPo2Count is not null or kamPo2Costs is not null");
        try {
            while (result.next()) {
                PlayerActivity data = new PlayerActivity();
                data.setPlayerId(result.getInt(1));
                data.setKamPo2Count(result.getInt(2));
                data.setKamPo2CostsJson(result.getString(3));
                datas.add(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datas;
    }

    public void updateFirecrackerLuckScore(PlayerActivity activity) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET firecrackerLuckScore='").append(activity.getFirecrackerLuckScore()).append("'")
                    .append(" WHERE playerId= " + activity.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家累计充数据时发生异常", e);
        }
        return;
    }

    public void updatePayDailyFirstStatus(PlayerActivity activity) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET payDailyFirstStatus=").append(activity.isPayDailyFirstStatus())
                    .append(" WHERE playerId= " + activity.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家累计充数据时发生异常", e);
        }
        return;
    }

    public void updatePayConDayCount(PlayerActivity activity) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET festPayConDayCount=").append(activity.getFestPayConDayCount())
                    .append(" WHERE playerId= " + activity.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家累计充数据时发生异常", e);
        }
        return;
    }

    public Boolean getTest() {
        ResultSet result = db.executeQuery("select * from activity where playerId = 300014");
        try {
            if (result.next()) {
                return result.getBoolean(62);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public List<PlayerActivity> getPlayerActivitys() {
        List<PlayerActivity> datas = new ArrayList<>();
        ResultSet result = db.executeQuery("select playerId,receivedLuckScore2, kamPo2LuckScore from activity where kamPo2LuckScore > 0");
        try {
            while (result.next()) {
                PlayerActivity data = new PlayerActivity();
                data.setPlayerId(result.getInt(1));
                data.setKamPoLuckScore(result.getInt(3));
                data.setReceivedLuckScore(result.getString(2));
                datas.add(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datas;

    }

    public List<PlayerActivity> getPlayerActivitys2Monopoly1() {
        List<PlayerActivity> datas = new ArrayList<>();
        ResultSet result = db.executeQuery("select playerId,monopoly1LevelReceived,monopoly1TodayPlayLevel from activity where monopoly1TodayPlayLevel > 0");
        try {
            while (result.next()) {
                PlayerActivity data = new PlayerActivity();
                data.setPlayerId(result.getInt(1));
                data.setMonopoly1LevelReceivedJson(result.getString(2));
                data.setMonopoly1TodayPlayLevel(result.getInt(3));
                datas.add(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datas;
    }

    public List<PlayerActivity> getPlayerActivitys2Monopoly() {
        List<PlayerActivity> datas = new ArrayList<>();
        ResultSet result = db.executeQuery("select playerId,monopolyLevelReceived,monopolyTodayPlayLevel from activity where monopolyTodayPlayLevel > 0");
        try {
            while (result.next()) {
                PlayerActivity data = new PlayerActivity();
                data.setPlayerId(result.getInt(1));
                data.setMonopolyLevelReceivedJson(result.getString(2));
                data.setMonopolyTodayPlayLevel(result.getInt(3));
                datas.add(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datas;
    }

    public void clearMonoply() {
        db.executeSql("update activity set monopolyCurrLevel = 0, monopolyLevelReceived = null, monopolyTodayPlayLevel = 0, monopolyTodayNum = 0, monopolyNextLevel = 0, monopolyCurrSteps = 0");
    }

    public List<PlayerActivity> getPlayerActivitys2() {
        List<PlayerActivity> datas = new ArrayList<>();
        ResultSet result = db.executeQuery("select playerId,receivedLuckScore, kamPoLuckScore from activity where kamPoLuckScore > 0");
        try {
            while (result.next()) {
                PlayerActivity data = new PlayerActivity();
                data.setPlayerId(result.getInt(1));
                data.setKamPoLuckScore(result.getInt(3));
                data.setReceivedLuckScore(result.getString(2));
                datas.add(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datas;

    }

    public List<Integer> getPlayerActivitysFestPayCon() {
        List<Integer> datas = new ArrayList<>();
        ResultSet result = db.executeQuery("select playerId from activity where festPayConReward is not null or festPayConDayCount != 0");
        try {
            while (result.next()) {
                datas.add(result.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datas;
    }

    public void updateActivityFestPayCon() {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET festPayConReward = null, festPayConDayCount = 0 ")
                    .append("where playerId in (");
            for (Integer i : this.getPlayerActivitysFestPayCon()) {
                builder.append(i + ",");
            }
            builder.append("null)");
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("清空玩家节日期间每日充值数据时发生异常", e);
        }
        return;
    }

    public List<Integer> getPlayerActivitysFirstDaily() {
        List<Integer> datas = new ArrayList<>();
        ResultSet result = db.executeQuery("select playerId from activity where payDailyFirstStatus is  not null");
        try {
            while (result.next()) {
                datas.add(result.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datas;
    }

    public void updateActivityFirstDaily() {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET payDailyFirstStatus = null ")
                    .append("where playerId in(");
            for (Integer i : this.getPlayerActivitysFirstDaily()) {
                builder.append(i + ",");
            }
            builder.append("null)");
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("清空玩家节日期间每日充值数据时发生异常", e);
        }
        return;
    }

    public List<Integer> getPlayerActivitysLimitDaily() {
        List<Integer> datas = new ArrayList<>();
        ResultSet result = db.executeQuery("select playerId from activity where gift is  not null");
        try {
            while (result.next()) {
                datas.add(result.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datas;
    }

    public void updateActivityLimitDaily() {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET gift = null ")
                    .append("where playerId in(");
            for (Integer i : this.getPlayerActivitysLimitDaily()) {
                builder.append(i + ",");
            }
            builder.append("null)");
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("清空玩家节日期间每日充值数据时发生异常", e);
        }
        return;
    }

    public void updateFirecrackerCost(PlayerActivity activityData) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET firecrackerCount=").append(activityData.getFirecrackerCount())
                    .append(" WHERE playerId= " + activityData.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家幸运鞭炮时发生异常", e);
        }
        return;

    }

    public void updateConsumCumulateReceived(PlayerActivity activity) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET consumCumulateReceived='").append(activity.getConsumeCumulateFixedJson()).append("'")
                    .append(" WHERE playerId= " + activity.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家累计消费数据时发生异常", e);
        }
        return;
    }

    public List<Integer> getPlayerActivitysFirecracker() {
        List<Integer> datas = new ArrayList<>();
        ResultSet result = db.executeQuery("select playerId from activity where receivedFirecrackerLuckScore is not null or firecrackerCount is not null");
        try {
            while (result.next()) {
                datas.add(result.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datas;
    }

    public void updateActivityFirecracker() {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET firecrackerCount = null and receivedFirecrackerLuckScore = null ")
                    .append("where playerId in(");
            for (Integer i : this.getPlayerActivitysFirecracker()) {
                builder.append(i + ",");
            }
            builder.append("null)");
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("清空玩家幸运鞭炮数据时发生异常", e);
        }
        return;
    }

    public List<Integer> getPlayerActivitysConsumeCumulate() {
        List<Integer> datas = new ArrayList<>();
        ResultSet result = db.executeQuery("select playerId from activity where consumCumulateReceived is not null");
        try {
            while (result.next()) {
                datas.add(result.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datas;
    }

    public void updateActivityConsumeCumulate() {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET consumCumulateReceived = null ")
                    .append("where playerId in(");
            for (Integer i : this.getPlayerActivitysConsumeCumulate()) {
                builder.append(i + ",");
            }
            builder.append("null)");
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("清空玩家累计消费数据时发生异常", e);
        }
        return;
    }

    public void updateMonopolyLevelReceived(PlayerActivity activityData) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET monopolyLevelReceived= '").append(activityData.getMonopolyLevelReceivedJson()).append("'")
                    .append(" WHERE playerId= " + activityData.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家大富翁已领取层数数据时发生异常", e);
        }
        return;

    }

    public void updateMonopolyTodayNumReceive(PlayerActivity activityData) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET monopolyTodayNumReceive = '").append(activityData.getMonopolyTodayNumReceiveJson()).append("'")
                    .append(" WHERE playerId= " + activityData.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家大富翁今日次数已领取数据时发生异常", e);
        }
        return;
    }

    public void updateMonopolyTodayPlayLevel(PlayerActivity activityData) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET monopolyTodayPlayLevel=").append(activityData.getMonopolyTodayPlayLevel())
                    .append(" WHERE playerId= " + activityData.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家大富翁今日次数已玩层数数据时发生异常", e);
        }
        return;
    }

    public void updateMonopolyTodayNum(PlayerActivity activityData) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET monopolyTodayNum=").append(activityData.getMonopolyTodayNum())
                    .append(" WHERE playerId= " + activityData.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家大富翁今日次数数据时发生异常", e);
        }
        return;

    }

    public void updateMonopolyCurrLevel(PlayerActivity activityData) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET monopolyCurrLevel=").append(activityData.getMonopolyCurrLevel())
                    .append(" WHERE playerId= " + activityData.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家大富翁当前层数数据时发生异常", e);
        }
        return;
    }

    public void updateMonopolyPlayerTime(PlayerActivity activityData) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET monopolyPlayerTime=").append(activityData.getMonopolyPlayerTime())
                    .append(" WHERE playerId= " + activityData.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家大富翁可玩次数数据时发生异常", e);
        }
        return;

    }

    public void updateMonopolyRechargeOrder(PlayerActivity activityData) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET monopolyUsedOrder= '").append(activityData.getMonopolyUsedOrderStr()).append("'")
                    .append(" WHERE playerId= " + activityData.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家大富翁充值数据时发生异常", e);
        }
        return;
    }

    public void clearMonopolyData() {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET monopolyUsedOrder = null, monopolyPlayerTime = null ")
                    .append("where playerId in(");
            for (Integer i : this.getMonopolyDataPlayerId()) {
                builder.append(i + ",");
            }
            builder.append("null)");
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("清空玩家累计消费数据时发生异常", e);
        }
        return;
    }

    public void clearMonopoly1Data() {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET monopoly1PlayedStep = null, monopoly1TodayPlayLevel = null, monopoly1CurrLevel = null, monopoly1NextLevel = null, monopoly1LevelReceived = null, monopoly1TodayStepReceive = null, monopoly1CurrSteps = null, monopoly1ResetNum = null, monopoly1FreeNum = null")
                    .append(" where playerId in(");
            for (Integer i : this.getMonopoly1DataPlayerId()) {
                builder.append(i + ",");
            }
            builder.append("null)");
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("清空玩家探宝2数据时发生异常", e);
        }
        return;
    }

    private List<Integer> getMonopoly1DataPlayerId() {
        List<Integer> datas = new ArrayList<>();
        ResultSet result = db.executeQuery("select playerId from activity where monopoly1PlayedStep is not null or monopoly1TodayPlayLevel is not null or monopoly1CurrLevel is not null or monopoly1NextLevel is not null or monopoly1LevelReceived is not null or monopoly1TodayStepReceive is not null or monopoly1CurrSteps is not null or monopoly1ResetNum is not null or monopoly1FreeNum is not null");
        try {
            while (result.next()) {
                datas.add(result.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datas;
    }

    private List<Integer> getMonopolyDataPlayerId() {
        List<Integer> datas = new ArrayList<>();
        ResultSet result = db.executeQuery("select playerId from activity where monopolyUsedOrder is not null or monopolyPlayerTime is not null ");
        try {
            while (result.next()) {
                datas.add(result.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datas;
    }

    public void updateMonopolyNextLevel(PlayerActivity activityData) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET monopolyNextLevel=").append(activityData.getMonopolyNextLevel())
                    .append(" WHERE playerId= " + activityData.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家大富翁每日奖励层数重置次数数据时发生异常", e);
        }
        return;
    }

    public void updateMonopolyCurrSteps(PlayerActivity activityData) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET monopolyCurrSteps=").append(activityData.getMonopolyCurrSteps())
                    .append(" WHERE playerId= " + activityData.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家大富翁每日奖励层数重置次数数据时发生异常", e);
        }
        return;
    }

    public void updatePuzzleReceived(PlayerActivity activityData) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET puzzleReceived= '").append(activityData.getPuzzleReceivedJson()).append("'")
                    .append(" WHERE playerId= " + activityData.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家拼图数据时发生异常", e);
        }
        return;
    }

    public void updatePuzzleRestTime(PlayerActivity activityData) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET puzzleRestTime= ").append(activityData.getPuzzleRestTime())
                    .append(" WHERE playerId= " + activityData.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存玩家拼图领奖剩余次数数据时发生异常", e);
        }
        return;
    }

    private List<Integer> getPuzzleDataPlayerId() {
        List<Integer> datas = new ArrayList<>();
        ResultSet result = db.executeQuery("select playerId from activity where puzzleReceived is not null or puzzleRestTime is not null ");
        try {
            while (result.next()) {
                datas.add(result.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datas;
    }

    public void updatePuzzData() {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET puzzleReceived = null, puzzleRestTime = null ")
                    .append("where playerId in(");
            for (Integer i : this.getPuzzleDataPlayerId()) {
                builder.append(i + ",");
            }
            builder.append("null)");
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("清空玩家累计消费数据时发生异常", e);
        }
        return;
    }

    public List<Integer> getPlayerActivitysNewYearLogon() {
        List<Integer> datas = new ArrayList<>();
        ResultSet result = db.executeQuery("select playerId from activity where newYearLogonRewards is not null or newYearLoginDay !=0");
        try {
            while (result.next()) {
                datas.add(result.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datas;
    }

    public void updateNewYearLogon() {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET newYearLogonRewards = null, newYearLoginDay= 0 ")
                    .append("where playerId in(");
            for (Integer i : this.getPlayerActivitysNewYearLogon()) {
                builder.append(i + ",");
            }
            builder.append("null)");
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("清空玩家新年登录活动数据时发生异常", e);
        }
        return;
    }

    public void updateNewYearLogonStartTime(long startTime) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET newYearLoginStartTime = " + startTime);
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("清空玩家新年登录活动数据时发生异常", e);
        }
        return;
    }

    public void updateNoRepeatTurntable(PlayerActivity activityData) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET noRepeatTurntableNum = " + activityData.getNoRepeatTurntableNum())
                    .append(", free = " + activityData.getFree())
                    .append(", noRepeatTurntableLuck = " + activityData.getNoRepeatTurntableLuck())
                    .append(", noRepeatTurntableAll = '").append(activityData.getNoRepeatTurntableAllStr()).append("'")
                    .append(", noRepeatTurntableReceived = '").append(activityData.getNoRepeatTurntableReceivedStr()).append("'")
                    .append(", noRepeatTurntablePayTime = " + activityData.getNoRepeatTurntablePayTime())
                    .append(", noRepeatTurntableTargeted = '").append(activityData.getNoRepeatTurntableTargetedJson()).append("'")
                    .append(" WHERE playerId= " + activityData.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存至尊转盘数据时发生异常", e);
        }
        return;
    }

    public void updateNoRepeatTurntableRefreshTime(PlayerActivity activityData) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET noRepeatTurntableRefreshTime = " + activityData.getNoRepeatTurntableRefreshTime())
                    .append(", free = " + activityData.getFree())
                    .append(" WHERE playerId= " + activityData.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存至尊转盘刷新时间数据时发生异常", e);
        }
        return;
    }

    public List<PlayerActivity> getPlayerActivitys2NoRepeatTurntable() {
        List<PlayerActivity> datas = new ArrayList<>();
        ResultSet result = db.executeQuery("select playerId,noRepeatTurntableNum,free,noRepeatTurntableLuck,noRepeatTurntableAll,noRepeatTurntableReceived,noRepeatTurntablePayTime from activity where noRepeatTurntableNum > 0 or noRepeatTurntableLuck > 0 or noRepeatTurntableAll is not null or noRepeatTurntableReceived is not null");
        try {
            while (result.next()) {
                PlayerActivity data = new PlayerActivity();
                data.setPlayerId(result.getInt(1));
                data.setNoRepeatTurntableNum(result.getInt(2));
                data.setFree(result.getByte(3));
                data.setNoRepeatTurntableLuck(result.getInt(4));
                data.setNoRepeatTurntableAllJson(result.getString(5));
                data.setNoRepeatTurntableReceivedJson(result.getString(6));
                data.setNoRepeatTurntablePayTime(result.getLong(7));
                datas.add(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datas;
    }

    public void updateTreasureVouchers(PlayerActivity activityData) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET vouchersList = '" + activityData.getVouchersListStr()).append("'")
                    .append(", treasureVouchers = " + activityData.getTreasureVouchers())
                    .append(" WHERE playerId= " + activityData.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存秘宝代金券数据时发生异常", e);
        }
        return;
    }

    public void updateTreasureFree(PlayerActivity activityData) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET treasureFree = " + activityData.getTreasureFree())
                    .append(" WHERE playerId= " + activityData.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存秘宝免费刷新状态数据时发生异常", e);
        }
        return;
    }

    public void updateTreasureRefresh(PlayerActivity activityData) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET reBuiedItems = '" + activityData.getReBuiedItemsStr()).append("'")
                    .append(", fourItems = '" + activityData.getFourItemsStr()).append("'")
                    .append(", treasuresRefreshTime = " + activityData.getTreasuresRefreshTime())
                    .append(", treasureFree = " + activityData.getTreasureFree())
                    .append(", treasureIntegral = " + activityData.getTreasureIntegral())
                    .append(" WHERE playerId= " + activityData.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存秘宝刷新数据时发生异常", e);
        }
        return;
    }

    public List<PlayerActivity> getPlayerActivitys2Treasure() {
        List<PlayerActivity> datas = new ArrayList<>();
        ResultSet result = db.executeQuery("select playerId from activity where reBuiedItems is not null or fourItems is not null or buiedItems is not null or buiedItemsCount is not null or treasureFree is not null or vouchersList is not null or treasureIntegral is not null or treasureVouchers is not null or treasuresRefreshTime is not null");
        try {
            while (result.next()) {
                PlayerActivity data = new PlayerActivity();
                data.setPlayerId(result.getInt(1));
                datas.add(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datas;

    }

    public void updateTreasure(PlayerActivity activityData) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET reBuiedItems = null")
                    .append(", fourItems = null")
                    .append(", buiedItems = null")
                    .append(", buiedItemsCount = null")
                    .append(", treasureFree = null")
                    .append(", vouchersList = null")
                    .append(", treasureIntegral = null")
                    .append(", treasureVouchers = null")
                    .append(", treasuresRefreshTime = null")
                    .append(" WHERE playerId= " + activityData.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("清除秘宝数据时发生异常", e);
        }
        return;
    }

    public void updateTreasureFourItems(PlayerActivity activityData) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET fourItems = '" + activityData.getFourItemsStr()).append("'")
                    .append(" WHERE playerId= " + activityData.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存秘宝4个道具数据时发生异常", e);
        }
        return;
    }

    public void updateTreasureBuy(PlayerActivity activityData) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET buiedItems = '" + activityData.getBuiedItemsStr()).append("'")
                    .append(", reBuiedItems = '" + activityData.getReBuiedItemsStr()).append("'")
                    .append(", treasureIntegral = " + activityData.getTreasureIntegral())
                    .append(", treasureVouchers = " + activityData.getTreasureVouchers())
                    .append(" WHERE playerId= " + activityData.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存秘宝购买数据时发生异常", e);
        }
        return;
    }

    public void updateSetWords(PlayerActivity activityData) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET setWordsNums = null")
                    .append(" WHERE playerId= " + activityData.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存集字数据时发生异常", e);
        }
        return;
    }

    public void updateSetWordsNum(PlayerActivity activityData) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET setWordsNums ='" + activityData.getSetWordsNumsStr()).append("'")
                    .append(" WHERE playerId= " + activityData.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存集字数据时发生异常", e);
        }
        return;
    }

    public List<PlayerActivity> getPlayerActivitys2SetWords() {
        List<PlayerActivity> datas = new ArrayList<>();
        ResultSet result = db.executeQuery("select playerId from activity where setWordsNums is not null");
        try {
            while (result.next()) {
                PlayerActivity data = new PlayerActivity();
                data.setPlayerId(result.getInt(1));
                datas.add(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datas;
    }

    public void clearPayCumulateFixed() {
        try {
            db.executeSql("UPDATE activity set payCumulateFixedData=''");
        } catch (Exception e) {
            logger.error("清除定时累计充值数据时发生异常", e);
        }
        return;
    }

    public void updateInvestFund(PlayerActivity activityData) {
        try {
            db.executeSql("UPDATE activity set investfund=" + activityData.getInvestFund() + " WHERE playerId=" + activityData.getPlayerId());
        } catch (Exception e) {
            logger.error("保存投资基金数据时发生异常", e);
        }
        return;
    }

    public void updateSlotNewMachine(PlayerActivity activityData) {
        try {
            db.executeSql("UPDATE activity set slotnewmachine=" + activityData.getSlotNewMachine() + " WHERE playerId=" + activityData.getPlayerId());
        } catch (Exception e) {
            logger.error("保存新拉霸数据时发生异常", e);
        }
        return;
    }

    public void clearSlotNewMachine() {
        try {
            db.executeSql("UPDATE activity set slotnewmachine=0");
        } catch (Exception e) {
            logger.error("清除新拉霸数据时发生异常", e);
        }
        return;
    }

    public void updateSlotMachine(PlayerActivity activityData) {
        try {
            db.executeSql("UPDATE activity set slotmachine=" + activityData.getSlotMachine() + " WHERE playerId=" + activityData.getPlayerId());
        } catch (Exception e) {
            logger.error("保存拉霸数据时发生异常", e);
        }
        return;
    }

    public void clearSlotMachine() {
        try {
            db.executeSql("UPDATE activity set slotmachine=0");
        } catch (Exception e) {
            logger.error("清除拉霸数据时发生异常", e);
        }
        return;
    }

    public void updataLimitLimitLimit(PlayerActivity data) {
        try {
            db.executeSql("UPDATE activity set limitlimitlimit=" + data.getLimitLimitLimit() + " WHERE playerId=" + data.getPlayerId());
        } catch (Exception e) {
            logger.error("更新限时限级限购数据时发生异常", e);
        }
        return;
    }

    public void clearLimitLimitLimit() {
        try {
            db.executeSql("UPDATE activity set limitlimitlimit=''");
        } catch (Exception e) {
            logger.error("清除限时限级限购数据时发生异常", e);
        }
        return;
    }

    public void updataSevenDay(PlayerActivity data) {
        try {
            db.executeSql("UPDATE activity set sevenday='" + data.getSevenDayJson() + "' WHERE playerId=" + data.getPlayerId());
        } catch (Exception e) {
            logger.error("更新七日活动数据时发生异常", e);
        }
        return;
    }

    /**
     * 探宝2抽取数据更新
     *
     * @param activityData
     */
    public void updateMonopoly4Dice(PlayerActivity activityData) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET monopoly1PlayedStep =" + activityData.getMonopoly1PlayedStep())
                    .append(", monopoly1TodayPlayLevel =" + activityData.getMonopoly1TodayPlayLevel())
                    .append(", monopoly1CurrLevel =" + activityData.getMonopoly1CurrLevel())
                    .append(", monopoly1NextLevel =" + activityData.getMonopoly1NextLevel())
                    .append(", monopoly1CurrSteps =" + activityData.getMonopoly1CurrSteps())
                    .append(", monopoly1ResetNum =" + activityData.getMonopoly1ResetNum())
                    .append(", monopoly1FreeNum =" + activityData.getMonopoly1FreeNum())
                    .append(", monopoly1LevelReceived ='" + activityData.getMonopoly1LevelReceivedStr()).append("'")
                    .append(" WHERE playerId= " + activityData.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存探宝2抽取数据时发生异常", e);
        }
        return;
    }

    /**
     * 探宝2抽取数据更新
     *
     * @param activityData
     */
    public void updateMonopoly4CurrLevel(PlayerActivity activityData) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET  monopoly1CurrLevel =" + activityData.getMonopoly1CurrLevel())
                    .append(" WHERE playerId= " + activityData.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存探宝2当前层数据时发生异常", e);
        }
        return;
    }

    /**
     * 探宝2抽取数据更新
     *
     * @param activityData
     */
    public void updateMonopoly4NextLevel(PlayerActivity activityData) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET  monopoly1NextLevel =" + activityData.getMonopoly1NextLevel())
                    .append(" WHERE playerId= " + activityData.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存探宝2下一层数据时发生异常", e);
        }
        return;
    }

    /**
     * 探宝2领取数据更新
     *
     * @param activityData
     */
    public void updateMonopoly4Receive(PlayerActivity activityData) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(" UPDATE activity ")
                    .append("   SET monopoly1LevelReceived ='" + activityData.getMonopoly1LevelReceivedStr()).append("'")
                    .append(", monopoly1TodayStepReceive ='" + activityData.getMonopoly1TodayStepReceive()).append("'")
                    .append(" WHERE playerId= " + activityData.getPlayerId());
            db.executeSql(builder.toString());
        } catch (Exception e) {
            logger.error("保存探宝2抽数据时发生异常", e);
        }
        return;
    }

//	public void updateNewYearLogonDay(int day,int playerId) {
//		try {
//			StringBuilder builder = new StringBuilder();
//			builder.append(" UPDATE activity ")
//					.append("   SET newYearLoginDay = "+day)
//					.append(" WHERE playerId= " + playerId);
//			db.executeSql(builder.toString());
//		} catch (Exception e) {
//			logger.error("清空玩家新年登录活动数据时发生异常", e);
//		}
//		return;
//	}
}
