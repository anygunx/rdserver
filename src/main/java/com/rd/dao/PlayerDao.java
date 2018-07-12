package com.rd.dao;

import com.alibaba.fastjson.annotation.JSONField;
import com.rd.bean.beat.BeatData;
import com.rd.bean.player.Player;
import com.rd.bean.player.SimplePlayer;
import com.rd.bean.rank.ActivityRank;
import com.rd.dao.db.DBOperator;
import com.rd.dao.db.ProxoolDB;
import com.rd.define.GameDefine;
import com.rd.game.GameWorld;
import com.rd.game.IGameRole;
import com.rd.model.VipModel;
import com.rd.model.data.GuanJieData;
import com.rd.util.DateUtil;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.*;

public class PlayerDao {

    private static Logger logger = Logger.getLogger(PlayerDao.class);
    private DBOperator dbOperator = new DBOperator();

    /**
     * 获取范围内最大的playerId
     *
     * @return
     */
    @JSONField(serialize = false)
    public int getPlayerMaxId(int low, int high) {
        StringBuilder builder = new StringBuilder();
        builder.append(" SELECT IFNULL(MAX(id), 0) ")
                .append("  FROM player ")
                .append(" WHERE id BETWEEN ").append(low).append(" AND ").append(high);
        ResultSet rs = dbOperator.executeQuery(builder.toString());
        int maxId = 0;
        try {
            if (rs != null && rs.next()) {
                maxId = rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            dbOperator.executeClose();
        }
        return maxId;
    }

    @JSONField(serialize = false)
    public Player getPlayer(int id) {
        ResultSet rs = dbOperator.executeQuery("SELECT * FROM player WHERE id = " + id);
        Player player = null;
        try {
            if (rs.next()) {
                player = new Player();
                initPlayerField(player, rs);
//                if (player != null) {
//                    List<Character> characterList = new ArrayList<>();
//                    StringBuilder chaSql = new StringBuilder();
//                    chaSql.append("select * from characters  where playerid=")
//                            .append(player.getId()).append(" order by idx");
//                    rs = dbOperator.executeQuery(chaSql.toString());
//                    while (rs.next()) {
//                        Character character = new Character();
//                        initCharacterField(character, rs);
//                        characterList.add(character);
//                    }
//                    player.setCharacterList(characterList);
//                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            dbOperator.executeClose();
        }
        return player;
    }

    @JSONField(serialize = false)
    public Set<Short> getRealServerIds() {
        ResultSet rs = dbOperator.executeQuery("SELECT DISTINCT serverid FROM player");
        Set<Short> result = new HashSet<>();
        try {
            while (rs.next()) {
                result.add(rs.getShort(1));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            dbOperator.executeClose();
        }
        return result;
    }

    /**
     * fixme 这个方法。。。 不能实际用
     *
     * @return
     */
    @JSONField(serialize = false)
    @Deprecated
    public List<Player> getAllPlayerDel() {
        ResultSet rs = dbOperator.executeQuery("SELECT * FROM player where state=" + GameDefine.PLAYER_STATE_NORMAL);
        List<Player> list = new ArrayList<>();
        try {
            while (rs.next()) {
                Player player = new Player();
                initPlayerField(player, rs);
//                List<Character> characterList = new ArrayList<>();
//                StringBuilder chaSql = new StringBuilder();
//                chaSql.append("select * from characters  where playerid=")
//                        .append(player.getId()).append(" order by idx");
//                DBOperator db = new DBOperator();
//                ResultSet rsCha = db.executeQuery(chaSql.toString());
//                if (rsCha == null) {
//                    db.executeClose();
//                    continue;
//                }
//                while (rsCha.next()) {
//                    Character character = new Character();
//                    initCharacterField(character, rsCha);
//                    characterList.add(character);
//                }
//                player.setCharacterList(characterList);
//                list.add(player);
//                db.executeClose();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            dbOperator.executeClose();
        }
        return list;
    }

    //	private static final List<EPlayerSaveType> RANK_PLAYER_FIELDS = new ArrayList<EPlayerSaveType>(){
//		private static final long serialVersionUID = 1L;{
//	}};
    private void initRankPlayerField(Player player, ResultSet rs) {
//		try {
//			player.setId(rs.getInt(EPlayerSaveType.ID.getSql()));
//		} catch (SQLException e) {
//			logger.error("初始化排行榜PlayerFields", e);
//		}
    }

    //	private static final List<EPlayerSaveType> RANK_CHA_FIELDS = new ArrayList<EPlayerSaveType>(){
//		private static final long serialVersionUID = 1L;{
//	}};
    private void initRankChaField(Character cha, ResultSet rs) {
//		try {
//			cha.setId(rs.getInt(EPlayerSaveType.ID.getSql()));
//		} catch (SQLException e) {
//			logger.error("初始化排行榜CharacterFields", e);
//		}
    }

    @JSONField(serialize = false)
    public Map<Integer, Player> getAllPlayer() {
        //首先获取内存数据
//		try {
//			Collection<GameRole> onlines = ImmutableList.copyOf(GameWorld.getPtr().getOnlineRoles().values());
//			for (GameRole role : onlines) {
//				if (role != null && role.getPlayer().getState()==GameDefine.PLAYER_STATE_NORMAL)
//					list.add(role.getPlayer());
//			}
//			Collection<Player> offlines = ImmutableList.copyOf(GameWorld.getPtr().getOfflinePlayer().values());
//			for (Player player : offlines) {
//				if (player != null && player.getState()==GameDefine.PLAYER_STATE_NORMAL)
//					list.add(player);
//			}
//		} catch (Exception e) {
//			logger.error("加入在线，离线玩家数据时发生异常", e);
//		}
        //查询Player的语句
        StringBuilder selectPlayerSql = new StringBuilder();
        selectPlayerSql.append("SELECT * FROM player WHERE state=").append(GameDefine.PLAYER_STATE_NORMAL);
//		StringBuilder notIn = null;
//		if (list.size() > 0) {
//			notIn = new StringBuilder();
//			notIn.append(" NOT IN(");
//			for (int i = 0; i < list.size(); i++) {
//				int id = list.get(i).getId();
//				if (i == list.size()-1)
//					notIn.append(id).append(") ");
//				else
//					notIn.append(id).append(",");
//			}
//			selectPlayerSql.append(" AND id").append(notIn);
//		}
        //查找Player
        Map<Integer, Player> playerMap = new HashMap<>();
        try {
            ResultSet rs = dbOperator.executeQuery(selectPlayerSql.toString());
            while (rs.next()) {
                Player player = new Player();
                initPlayerField(player, rs);
                playerMap.put(player.getId(), player);
            }
        } catch (Exception e) {
            logger.error("查找Player数据异常", e);
        } finally {
            dbOperator.executeClose();
        }
        //查找Characters
//        DBOperator db = new DBOperator();
//        try {
//            StringBuilder selectChaSql = new StringBuilder();
//            selectChaSql.append("SELECT * FROM characters ");
////			if (notIn != null) {
////				selectChaSql.append(" WHERE playerid ").append(notIn);
////			}
//            selectChaSql.append(" ORDER BY playerid, idx");
//            ResultSet rsc = db.executeQuery(selectChaSql.toString());
//            while (rsc.next()) {
//                Character character = new Character();
//                initCharacterField(character, rsc);
//                Player player = playerMap.get(character.getPlayerId());
//                if (player == null)
//                    continue;
//                player.getCharacterList().add(character);
//            }
//        } catch (Exception e) {
//            logger.error("查找Characters数据异常", e);
//        } finally {
//            db.executeClose();
//        }
        return playerMap;
    }

    @JSONField(serialize = false)
    public List<ActivityRank> getPlayerRankMapStage() {
        ResultSet rs = dbOperator.executeQuery("SELECT id, name, vip, mapStageId, fighting FROM player " +
                "WHERE mapStageId >= 1 AND state = " + GameDefine.PLAYER_STATE_NORMAL +
                " ORDER BY mapStageId DESC, fighting DESC LIMIT 200 ");
        List<ActivityRank> list = new ArrayList<>();
        try {
            while (rs.next()) {
                ActivityRank rank = new ActivityRank();
                rank.setId(rs.getInt(1));
                rank.setN(rs.getString(2));
                int vipExp = rs.getInt(3);
                rank.setVn(VipModel.getVipLv(vipExp));
                rank.setV2(rs.getShort(4));
                long fighting = rs.getLong(5);
                if (fighting == 0) {
                    IGameRole role = GameWorld.getPtr().getGameRole(rank.getId());
                    if (role != null) {
                        role.getPlayer().updateFighting();
                        fighting = role.getPlayer().getFighting();
                    }
                }
                rank.setM(fighting);
                list.add(rank);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            dbOperator.executeClose();
        }
        return list;
    }

    @JSONField(serialize = false)
    public List<ActivityRank> getPlayerRankFengmota() {
        ResultSet rs = dbOperator.executeQuery("SELECT id, name, vip, fengmota, fighting FROM player " +
                " WHERE fengmota >= 1 AND state = " + GameDefine.PLAYER_STATE_NORMAL +
                " ORDER BY fengmota DESC, fighting DESC LIMIT 200 ");
        List<ActivityRank> list = new ArrayList<>();
        if (rs == null)
            return list;
        try {
            while (rs.next()) {
                ActivityRank rank = new ActivityRank();
                rank.setId(rs.getInt(1));
                rank.setN(rs.getString(2));
                int vipExp = rs.getInt(3);
                rank.setVn(VipModel.getVipLv(vipExp));
                rank.setV2(rs.getShort(4));
                long fighting = rs.getLong(5);
                if (fighting == 0) {
                    IGameRole role = GameWorld.getPtr().getGameRole(rank.getId());
                    if (role != null) {
                        role.getPlayer().updateFighting();
                        fighting = role.getPlayer().getFighting();
                    }
                }
                rank.setM(fighting);
                list.add(rank);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            dbOperator.executeClose();
        }
        return list;
    }

    @JSONField(serialize = false)
    public List<ActivityRank> getPlayerRankZhuzai() {
        ResultSet rs = dbOperator.executeQuery("SELECT id, name, vip, zhuzai, fighting FROM player " +
                " WHERE zhuzai >= 1 AND state = " + GameDefine.PLAYER_STATE_NORMAL +
                " ORDER BY zhuzai DESC, fighting DESC LIMIT 200 ");
        List<ActivityRank> list = new ArrayList<>();
        if (rs == null)
            return list;
        try {
            while (rs.next()) {
                ActivityRank rank = new ActivityRank();
                rank.setId(rs.getInt(1));
                rank.setN(rs.getString(2));
                int vipExp = rs.getInt(3);
                rank.setVn(VipModel.getVipLv(vipExp));
                rank.setV2(rs.getShort(4));
                long fighting = rs.getLong(5);
                if (fighting == 0) {
                    IGameRole role = GameWorld.getPtr().getGameRole(rank.getId());
                    if (role != null) {
                        role.getPlayer().updateFighting();
                        fighting = role.getPlayer().getFighting();
                    }
                }
                rank.setM(fighting);
                list.add(rank);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            dbOperator.executeClose();
        }
        return list;
    }

    @JSONField(serialize = false)
    public List<ActivityRank> getPlayerRankDekaron() {
        ResultSet rs = dbOperator.executeQuery("SELECT id, name, vip, dekaron, fighting FROM player " +
                "WHERE dekaron >= 1 AND state = " + GameDefine.PLAYER_STATE_NORMAL +
                " ORDER BY dekaron DESC, fighting DESC LIMIT 200 ");
        List<ActivityRank> list = new ArrayList<>();
        if (rs == null)
            return list;
        try {
            while (rs.next()) {
                ActivityRank rank = new ActivityRank();
                rank.setId(rs.getInt(1));
                rank.setN(rs.getString(2));
                int vipExp = rs.getInt(3);
                rank.setVn(VipModel.getVipLv(vipExp));
                rank.setV2(rs.getShort(4));
                long fighting = rs.getLong(5);
                if (fighting == 0) {
                    IGameRole role = GameWorld.getPtr().getGameRole(rank.getId());
                    if (role != null) {
                        role.getPlayer().updateFighting();
                        fighting = role.getPlayer().getFighting();
                    }
                }
                rank.setM(fighting);
                list.add(rank);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            dbOperator.executeClose();
        }
        return list;
    }

    /**
     * 根据account,channel,serverId 或者指定的name
     * 返回已创建角色的id和名称
     *
     * @param account
     * @param channel
     * @param serverId
     * @param name
     * @return null 不存在
     */
    @JSONField(serialize = false)
    public String getCreatedPlayer(String account, short channel, short serverId, String name) {
        PreparedStatement ps = null;
        try {
            ps = dbOperator.getPrepareStatement("SELECT id,name FROM player WHERE ( account=? and channel=? and serverId=? ) or ( name=? )");
            ps.setString(1, account);
            ps.setShort(2, channel);
            ps.setShort(3, serverId);
            ps.setString(4, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString(2);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            dbOperator.executeClose();
        }
        return null;
    }

    @JSONField(serialize = false)
    public Player getPlayer(String account, short channel, short subChannel, short serverId, byte platform) {
        Player player = null;
        String sql = "select * from player where account=? and channel=? and serverid=?";
        PreparedStatement ps = null;
        try {
            ps = dbOperator.getPrepareStatement(sql);
            ps.setString(1, account);
            ps.setShort(2, channel);
            ps.setShort(3, serverId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                player = new Player();
                initPlayerField(player, rs);

                if (player != null) {
//                    List<Character> characterList = new ArrayList<>();
//                    StringBuilder chaSql = new StringBuilder();
//                    chaSql.append("select * from characters  where playerid=")
//                            .append(player.getId()).append(" order by idx");
//                    rs = dbOperator.executeQuery(chaSql.toString());
//                    while (rs.next()) {
//                        Character character = new Character();
//                        initCharacterField(character, rs);
//                        characterList.add(character);
//                    }
//                    player.setCharacterList(characterList);
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            dbOperator.executeClose();
        }
        return player;
    }

    public static void initPlayerField(Player player, ResultSet rs) throws SQLException {
        player.setId(rs.getInt(EPlayerSaveType.ID.getSql()));
        player.setName(rs.getString(EPlayerSaveType.NAME.getSql()));
        player.setHead(rs.getByte(EPlayerSaveType.HEAD.getSql()));
        player.setRein(rs.getShort(EPlayerSaveType.REIN.getSql()));
        player.setExp(rs.getLong(EPlayerSaveType.EXP.getSql()));
        player.setLevel(rs.getShort(EPlayerSaveType.LEVEL.getSql()));
        player.setVip(rs.getInt(EPlayerSaveType.VIP.getSql()));
        player.setGold(rs.getLong(EPlayerSaveType.GOLD.getSql()));
        player.setDiamond(rs.getInt(EPlayerSaveType.DIAMOND.getSql()));
        player.setPoints(rs.getInt(EPlayerSaveType.POINTS.getSql()));
        player.setEquipBagJson(rs.getString(EPlayerSaveType.EQUIPBAG.getSql()));
        player.setItemList(rs.getString(EPlayerSaveType.ITEM.getSql()));
        player.setBoxList(rs.getString(EPlayerSaveType.BOX.getSql()));
        player.setSpirits(rs.getString(EPlayerSaveType.SPIRIT.getSql()));
        player.setMapId(rs.getShort(EPlayerSaveType.MAPID.getSql()));
        player.setMapStageId(rs.getShort(EPlayerSaveType.MAPSTAGEID.getSql()));
        player.setMagicLevel(rs.getShort(EPlayerSaveType.MAGICLEVEL.getSql()));
        player.setMagicLevelStar(rs.getByte(EPlayerSaveType.MAGICLEVELSTAR.getSql()));
        player.setMagicStage(rs.getShort(EPlayerSaveType.MAGICSTAGE.getSql()));
        player.setMagicStageStar(rs.getByte(EPlayerSaveType.MAGICSTAGESTAR.getSql()));
        player.setMagicStageExp(rs.getInt(EPlayerSaveType.MAGICSTAGEEXP.getSql()));
        player.setMeltLv(rs.getShort(EPlayerSaveType.MELTLV.getSql()));
        player.setMeltExp(rs.getInt(EPlayerSaveType.MELTEXP.getSql()));
        player.setGodArtifactList(rs.getString(EPlayerSaveType.GODARTIFACT.getSql()));
        player.setState(rs.getByte(EPlayerSaveType.STATE.getSql()));
        player.setLastLoginTime(DateUtil.parseDataTime(rs.getString(EPlayerSaveType.LOGINTIME.getSql())).getTime());
        player.setLastLogoutTime(DateUtil.parseDataTime(rs.getString(EPlayerSaveType.LOGOUTTIME.getSql())).getTime());
        player.setCreateTime(DateUtil.parseDataTime(rs.getString(EPlayerSaveType.CREATETIME.getSql())).getTime());
        player.setChainMissionJson(rs.getString(EPlayerSaveType.CHAINMISSION.getSql()));
        player.setDailyProgressString(rs.getString(EPlayerSaveType.DAILYMISSION.getSql()));
        player.fromSmallDataJson(rs.getString(EPlayerSaveType.SMALLDATA.getSql()));
        player.setFighting(rs.getLong(EPlayerSaveType.FIGHTING.getSql()));
        player.setBossCount(rs.getShort(EPlayerSaveType.BOSSCOUNT.getSql()));
        player.setGangBossCount(rs.getByte(EPlayerSaveType.GANGBOSSCOUNT.getSql()));
        player.setBossRecover(rs.getLong(EPlayerSaveType.BOSSRECOVER.getSql()));
        player.setCitBossLeft(rs.getShort(EPlayerSaveType.CITBOSSLEFT.getSql()));
        player.setCitRecover(rs.getLong(EPlayerSaveType.CITRECOVE.getSql()));
        player.setCitCueJson(rs.getString(EPlayerSaveType.CITCUE.getSql()));
        player.setHonor(rs.getInt(EPlayerSaveType.HONOR.getSql()));
        player.setArena(rs.getInt(EPlayerSaveType.ARENA.getSql()));
        player.setYuanqi(rs.getInt(EPlayerSaveType.YUANQI.getSql()));
        player.setForever(rs.getInt(EPlayerSaveType.FOREVER.getSql()));
        player.setRedLottery(rs.getByte(EPlayerSaveType.REDLOTTERY.getSql()));
        player.setMapReward(rs.getShort(EPlayerSaveType.MAPREWARD.getSql()));
        player.fromDayDataJson(rs.getString(EPlayerSaveType.DAYDATA.getSql()));
        player.setChannel(rs.getShort("channel"));
        player.setSubChannel(rs.getShort("subchannel"));
        player.setServerId(rs.getShort("serverid"));
        player.setAccount(rs.getString("account"));
        player.setDonate(rs.getInt(EPlayerSaveType.DONATE.getSql()));
        player.fromGangSkillJson(rs.getString(EPlayerSaveType.GANGSKILL.getSql()));
        player.setHuanhuaListJsonData(rs.getString(EPlayerSaveType.HUANHUALIST.getSql()));
        player.setHuanhuaAppearanceJsonData(rs.getString(EPlayerSaveType.HUANHUAAPPEARANCE.getSql()));
        player.setTlPoints(rs.getInt(EPlayerSaveType.TLPOINTS.getSql()));
        player.setRsPoints(rs.getInt(EPlayerSaveType.RSPOINTS.getSql()));
        player.setCdKeyListJson(rs.getString("cdkeys"));
        player.setDekaron(rs.getShort(EPlayerSaveType.DEKARON.getSql()));
        player.setFightRequestTime(rs.getLong(EPlayerSaveType.REQUESTFIGHTTIME.getSql()));
        player.setTitleJson(rs.getString(EPlayerSaveType.TITLE.getSql()));
        player.setPlatform(rs.getByte("platform"));
        player.setShareDataJson(rs.getString(EPlayerSaveType.SHARE.getSql()));
        player.setDailyUpdateMark(rs.getString(EPlayerSaveType.DAILYUPDATEMARK.getSql()));
        player.setAuctionSubscriptions(rs.getString(EPlayerSaveType.AUCTION_SUBSCRIPTIONS.getSql()));
        player.setDragonBall(rs.getString(EPlayerSaveType.DRAGON_BALL.getSql()));
        player.setAchievement(rs.getInt(EPlayerSaveType.ACHIEVEMENT.getSql()));
        player.setAchievementMissionString(rs.getString(EPlayerSaveType.ACHIEVEMENT_MISSION.getSql()));
        player.setMedal(rs.getByte(EPlayerSaveType.MEDAL.getSql()));
        player.setAuctionBoxStr(rs.getString(EPlayerSaveType.AUCTION_BOX.getSql()));
        player.setHeartSkillJson(rs.getString(EPlayerSaveType.HEART_SKILL.getSql()));
        player.setArtifactPiecesStr(rs.getString(EPlayerSaveType.ARTIFACT_PIECES.getSql()));
        player.setArtifactBoss(rs.getString(EPlayerSaveType.ARTIFACT_BOSS.getSql()));
        player.setCombineRune(rs.getString(EPlayerSaveType.COMBINE_RUNE.getSql()));
        player.setCombineRunePiece(rs.getInt(EPlayerSaveType.COMBINE_RUNE_PIECE.getSql()));
        player.setCombineRuneBag(rs.getString(EPlayerSaveType.COMBINE_RUNE_BAG.getSql()));
        player.setWeiWang(rs.getInt(EPlayerSaveType.WEIWANG.getSql()));
        player.setZhanWensJson(rs.getString(EPlayerSaveType.ZHANWEN.getSql()));
        player.setZhanWenJinghua(rs.getInt(EPlayerSaveType.ZHANWEN_JINGHUA.getSql()));
        player.setTimeLimitMissionDataStr(rs.getString(EPlayerSaveType.TLMISSION.getSql()));
        player.setMysteryBossLeft(rs.getShort(EPlayerSaveType.MYSTERYBOSSLEFT.getSql()));
        player.setMysteryCueJson(rs.getString(EPlayerSaveType.MYSTERYCUE.getSql()));
        player.setVipBossCueJson(rs.getString(EPlayerSaveType.VIPBOSSCUE.getSql()));
        player.setWingGodsStr(rs.getString(EPlayerSaveType.WINGGODS.getSql()));
        player.setFengmota(rs.getShort(EPlayerSaveType.FENGMOTA.getSql()));
        player.setCardBagStr(rs.getString(EPlayerSaveType.CARD_BAG.getSql()));
        player.setCardBookStr(rs.getString(EPlayerSaveType.CARD_BOOK.getSql()));
        player.setCardMissionStr(rs.getString(EPlayerSaveType.CARD_MISSION.getSql()));
        player.setConsumeDailyStr(rs.getString(EPlayerSaveType.DAILY_CONSUM.getSql()));
        player.setFiveElementsStr(rs.getString(EPlayerSaveType.FIVEELEMENT.getSql()));
        player.setLastLoginTime2Fest(rs.getLong(EPlayerSaveType.LOGONTIME_FEST.getSql()));
        player.setLingSuiPiecesStr(rs.getString(EPlayerSaveType.SUUL_PIECE.getSql()));
        player.setZhuzai(rs.getShort(EPlayerSaveType.ZHUZAISHILIAN.getSql()));
        player.setFazhenList(rs.getString(EPlayerSaveType.FAZHEN.getSql()));
        player.setVouchers(rs.getInt(EPlayerSaveType.VOUCHERS.getSql()));
        player.setTownSoulTreasureJson(rs.getString(EPlayerSaveType.TOWN_SOUL.getSql()));
        player.setHolyLinesJson(rs.getString(EPlayerSaveType.HOLYLINES.getSql()));
        player.setMysteryIntegral(rs.getInt(EPlayerSaveType.MYSTERY_INTEGRAL.getSql()));
        player.setQizhenIntegral(rs.getInt(EPlayerSaveType.QIZHEN_INTEGRAL.getSql()));
        player.setAmbitJson(rs.getString(EPlayerSaveType.AMBIT.getSql()));


        /***************************************版本分割线  以上是旧版本 以下是新版本****************************************************/

        player.setEquipSlotList(rs.getString(EPlayerSaveType.EQUIPSLOT.getSql()));
        player.setPiFuJsonData(rs.getString(EPlayerSaveType.PIFULIST.getSql()));
        player.setDanYaoList(rs.getString(EPlayerSaveType.DANYAO.getSql()));
        player.setRoleEquipMapJson(rs.getString(EPlayerSaveType.EQUIP.getSql()));
        player.setWearEquipJson(rs.getString(EPlayerSaveType.WEAREQUIP.getSql()));
        player.setPulse(rs.getShort(EPlayerSaveType.PULSE.getSql()));
        player.setReFriendCoin(rs.getInt(EPlayerSaveType.RECEIVEFC.getSql()));
        player.setReFriendCoinCount(rs.getShort(EPlayerSaveType.RECEIVEFCC.getSql()));
        player.setSendFriendCoinCount(rs.getShort(EPlayerSaveType.SENDFCC.getSql()));

        player.setLiLianExp(rs.getInt(EPlayerSaveType.LILIAN_EXP.getSql()));
        player.setLiLianLevel(rs.getShort(EPlayerSaveType.LILIAN_LEVEL.getSql()));

        player.fromNrcDataJson(rs.getString(EPlayerSaveType.RICHANG.getSql()));
        player.setNTADataListJson(rs.getString(EPlayerSaveType.TASKADVANCED.getSql()));


        player.setGrowListJson(rs.getString(EPlayerSaveType.GROW.getSql()));
        player.setGrowEquipListJson(rs.getString(EPlayerSaveType.GROWEQUIP.getSql()));
        player.setSkillListJson(rs.getString(EPlayerSaveType.SKILL.getSql()));
        player.setSjgCopyId(rs.getInt(EPlayerSaveType.SJGCOPY.getSql()));
        player.setTmMaxCopyId(rs.getShort(EPlayerSaveType.TMMAXCOPYID.getSql()));
        player.setMiSartTotal(rs.getShort(EPlayerSaveType.MZSTAR.getSql()));
    }


    private String getCreatePlayerSql(Player player) {
        StringBuilder builder = new StringBuilder("insert player(");
        builder.append("id,");                //1
        builder.append("channel,");            //2
        builder.append("subchannel,");        //3
        builder.append("account,");            //4
        builder.append("serverid,");        //5
        builder.append("name,");            //6
        builder.append("head,");            //6
        builder.append("exp,");                //7
        builder.append("level,");            //8
        builder.append("gold,");            //9
        builder.append("diamond,");            //10
        builder.append("equip,");            //11
        builder.append("item,");            //13
        builder.append("box,");                //14
        builder.append("mapid,");            //15
        builder.append("mapstageid,");        //16
        builder.append("magiclevel,");        //17
        builder.append("state,");            //19
        builder.append("loginTime,");        //20
        builder.append("logoutTime,");        //21
        builder.append("createTime,");        //22
        builder.append("donate,");            //23
        builder.append("requestfighttime,");//24
        builder.append("platform,");        //25
        builder.append("dailyupdatemark,");    //26
        builder.append("combinerune,");    //27
        builder.append("mysteryBossLeft");    //28
        builder.append(") values(");
        builder.append(player.getId() + ",");                            //1
        builder.append(player.getChannel() + ",");                    //2
        builder.append(player.getSubChannel() + ",");                    //3
        builder.append("'" + player.getAccount() + "',");                //4
        builder.append(player.getServerId() + ",");                    //5
        builder.append("'" + player.getName() + "',");                    //6
        builder.append(player.getHead() + ",");                        //6
        builder.append(player.getExp() + ",");                        //7
        builder.append(player.getLevel() + ",");                        //8
        builder.append(player.getGold() + ",");                        //9
        builder.append(player.getDiamond() + ",");                    //10
        builder.append("'" + player.getRoleEquipMapJson() + "',");            //11
        builder.append("'" + player.getItemListJson() + "',");            //13
        builder.append("'" + player.getBoxListJson() + "',");            //14
        builder.append(player.getMapId() + ",");                        //15
        builder.append(player.getMapStageId() + ",");                    //16
        builder.append(player.getMagicLevel() + ",");                    //17
        builder.append(player.getState() + ",");                        //19
        builder.append("'" + DateUtil.formatDateTime(player.getLastLoginTime()) + "',");    //20
        builder.append("'" + DateUtil.formatDateTime(player.getLastLogoutTime()) + "',");    //21
        builder.append("'" + DateUtil.formatDateTime(player.getCreateTime()) + "',");        //22
        builder.append(player.getDonate() + ",");                            //23
        builder.append(player.getFightRequestTime()).append(",");        //24
        builder.append(player.getPlatform()).append(",");                //25
        builder.append(player.getDailyUpdateMark()).append(",");        //26
        builder.append("'" + player.getCombineRuneString() + "'").append(",");                    //27
        builder.append(player.getMysteryBossLeft());                    //28
        builder.append(")");
        return builder.toString();
    }

/*    public static void initCharacterField(Character cha, ResultSet rs) throws SQLException {
        cha.setPlayerId(rs.getInt(EPlayerSaveType.CHA_PID.getSql()));
        cha.setIdx(rs.getByte(EPlayerSaveType.CHA_IDX.getSql()));
        cha.setOccupation(rs.getByte(EPlayerSaveType.CHA_OCCUPATION.getSql()));
        cha.setMeridian(rs.getShort(EPlayerSaveType.CHA_MERIDIAN.getSql()));
        cha.fromEquipListJson(rs.getString(EPlayerSaveType.CHA_EQUIP.getSql()));
        cha.fromSkillListJson(rs.getString(EPlayerSaveType.CHA_SKILL.getSql()));
        cha.setDomListJson(rs.getString(EPlayerSaveType.CHA_DOM.getSql()));
        cha.setEquipSlotList(rs.getString(EPlayerSaveType.CHA_EQUIPSLOT.getSql()));
        cha.setGongJson(rs.getString(EPlayerSaveType.CHA_GONG.getSql()));
        cha.setSpiritJson(rs.getString(EPlayerSaveType.CHA_SPIRIT.getSql()));
        cha.setMountStar(rs.getByte(EPlayerSaveType.CHA_MOUNTSTAR.getSql()));
        cha.setMountStage(rs.getShort(EPlayerSaveType.CHA_MOUNTJIEDUAN.getSql()));
        cha.setMountExp(rs.getInt(EPlayerSaveType.CHA_MOUNTEXP.getSql()));
        cha.setTongjing(rs.getShort(EPlayerSaveType.CHA_TONGJING.getSql()));
        cha.setYudi(rs.getShort(EPlayerSaveType.CHA_YUDI.getSql()));
        cha.setZuoyan(rs.getShort(EPlayerSaveType.CHA_ZUOYAN.getSql()));
        cha.setYouyan(rs.getShort(EPlayerSaveType.CHA_YOUYAN.getSql()));
        cha.setWingsJson(rs.getString(EPlayerSaveType.CHA_WINGS.getSql()));
        cha.setWingShow(rs.getByte(EPlayerSaveType.CHA_WINGSHOW.getSql()));
        cha.setWeaponsJson(rs.getString(EPlayerSaveType.CHA_WEAPONS.getSql()));
        cha.setWeaponShow(rs.getByte(EPlayerSaveType.CHA_WEAPONSHOW.getSql()));
        cha.setArmorsJson(rs.getString(EPlayerSaveType.CHA_ARMORS.getSql()));
        cha.setArmorShow(rs.getByte(EPlayerSaveType.CHA_ARMORSHOW.getSql()));
        cha.setMountsJson(rs.getString(EPlayerSaveType.CHA_MOUNTS.getSql()));
        cha.setMountShow(rs.getByte(EPlayerSaveType.CHA_MOUNTSHOW.getSql()));
        cha.setTitle(rs.getShort(EPlayerSaveType.CHA_TITLE.getSql()));
        cha.setGangSkillJson(rs.getString(EPlayerSaveType.CHA_GANGSKILL.getSql()));
        cha.setGangSkill2Json(rs.getString(EPlayerSaveType.CHA_GANGSKILL2.getSql()));
        cha.getWing().setEquipmentsStr(rs.getString(EPlayerSaveType.CHA_MOUNTEQUIP.getSql()));
        cha.setHeartSkillSlotJson(rs.getString(EPlayerSaveType.CHA_HEART_SKILL_SLOT.getSql()));
        cha.setShenBingStr(rs.getString(EPlayerSaveType.CHA_SHENBING.getSql()));
        cha.setZhanWenJson(rs.getString(EPlayerSaveType.CHA_ZHANWEN.getSql()));
        cha.setSoulListStr(rs.getString(EPlayerSaveType.CHA_SOUL_GOD.getSql()));
        cha.setTownSoulEquipJson(rs.getString(EPlayerSaveType.CHA_TOWN_SOUL.getSql()));
        cha.setHolyGoodsJson(rs.getString(EPlayerSaveType.CHA_HOLYGOODS.getSql()));
    } 
 
    private String getCreateCharacterSql(Character ch) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO characters(")
                .append("playerid").append(",")
                .append("idx").append(",")
                .append("occupation").append(",")
                .append("equip").append(",")
                .append("skill").append(",")
                .append("dom").append(",")
                .append("createtime")
                .append(") VALUES(")
                .append(ch.getPlayerId()).append(",")            //playerId
                .append(ch.getIdx()).append(",")                //idx
                .append(ch.getOccupation()).append(",'")        //occupation
                .append(ch.toEquipListJson()).append("','")        //equipList
                .append(ch.toSkillListJson()).append("','")        //skillList
                .append(ch.getDomListJson()).append("',")        //domList
                .append("'").append(DateUtil.formatDateTime(System.currentTimeMillis())).append("'")//createTime
        		.append(")");
        return sql.toString();
    }*/

    public boolean createPlayer(Player player) {
        boolean isSuccess = false;
        Character ch = player.getCharacter(0);

        Connection conn = ProxoolDB.getConnection();
        Statement st = null;
        try {
            conn.setAutoCommit(false);
            st = conn.createStatement();
            st.addBatch(this.getCreatePlayerSql(player));
//            st.addBatch(this.getCreateCharacterSql(ch));
            st.executeBatch();
            conn.commit();
            isSuccess = true;
        } catch (SQLException e) {
            logger.error("ExecuteBatch MySQL Database Error...", e);
            try {
                if (conn != null)
                    conn.rollback();
            } catch (Exception e1) {
                logger.error("RollBack MySQL Database Error...", e1);
            }
        } finally {
            try {
                if (st != null) {
                    st.close();
                    st = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (Exception e) {
                logger.error("Close MySQL Database Connection Error...", e);
            }
        }
        return isSuccess;
    }

//    public int createCharacter(Character character) {
//        return dbOperator.executeSql(this.getCreateCharacterSql(character));
//    }

    public int savePlayer(Player player, EnumSet<EPlayerSaveType> saves) {
        String sql = getPlayerUpdateSql(player, saves);
        if (sql == null)
            return -1;
        return dbOperator.executeSql(sql);
    }

//    public int saveCharacter(Character cha, EnumSet<EPlayerSaveType> saves) {
//        String sql = getCharacterUpdateSql(cha, saves);
//        if (sql == null)
//            return -1;
//        return dbOperator.executeSql(sql);
//    }

    //操作具有原子性
    public int saveData(Player player, int idx, EnumSet<EPlayerSaveType> saves) {
        int result = -1;
        Connection conn = ProxoolDB.getConnection();
        Statement st = null;
        try {
            conn.setAutoCommit(false);
            st = conn.createStatement();
            String pSQL = this.getPlayerUpdateSql(player, saves);
            if (pSQL != null)
                st.addBatch(pSQL);
//            String cSQL = this.getCharacterUpdateSql(player.getCharacter(idx), saves);
//            if (cSQL != null)
//                st.addBatch(cSQL);
            st.executeBatch();
            conn.commit();
            result = 1;
        } catch (SQLException e) {
            logger.error("ExecuteBatch MySQL Database Error...", e);
            try {
                if (conn != null)
                    conn.rollback();
            } catch (Exception e1) {
                logger.error("RollBack MySQL Database Error...", e1);
            }
        } finally {
            try {
                if (st != null) {
                    st.close();
                    st = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (Exception e) {
                logger.error("Close MySQL Database Connection Error...", e);
            }
        }
        return result;
    }

    private String getPlayerUpdateSql(Player player, EnumSet<EPlayerSaveType> enumSet) {
        if (0 == enumSet.size())
            return null;
        int total = 0;
        StringBuilder sb = new StringBuilder();
        Iterator<EPlayerSaveType> it = enumSet.iterator();
        while (it.hasNext()) {
            boolean isMatch = true;
            EPlayerSaveType type = it.next();
            if (type.getTb() != 0)
                continue;
            switch (type) {

                case EQUIPSLOT:
                    sb.append(type.getSql() + "='" + player.getEquipSlotListJson() + "'");
                    break;
                case PIFULIST:
                    sb.append(type.getSql() + "='" + player.getPiFuJsonData() + "'");
                    break;
                case DANYAO:
                    sb.append(type.getSql() + "='" + player.getDanYaoListJson() + "'");
                    break;
                case EQUIP:
                    sb.append("equip='" + player.getRoleEquipMapJson() + "'");
                    break;
                case WEAREQUIP:
                    sb.append("wearequip='" + player.getWearEquipJson() + "'");
                    break;
                case PULSE:
                    sb.append(type.getSql() + "='" + player.getPulse() + "'");
                    break;

                case RECEIVEFC:
                    sb.append(type.getSql() + "='" + player.getReFriendCoin() + "'");
                    break;
                case RECEIVEFCC:
                    sb.append(type.getSql() + "='" + player.getReFriendCoinCount() + "'");
                    break;
                case SENDFCC:
                    sb.append(type.getSql() + "='" + player.getSendFCC() + "'");
                    break;

                case RICHANG:
                    sb.append(type.getSql() + "='" + player.toNrcDataJson() + "'");
                    break;

                case TASKADVANCED:
                    sb.append(type.getSql() + "='" + player.getNTADataListJson() + "'");
                    break;

                case LILIAN_EXP:
                    sb.append(type.getSql() + "='" + player.getLiLianExp() + "'");
                    break;
                case LILIAN_LEVEL:
                    sb.append(type.getSql() + "='" + player.getLiLianLevel() + "'");
                    break;

                /**************************************************************以下是旧版本  以上是新版本开发*****************************************************************************/
                case HEAD:
                    sb.append("head=" + player.getHead());
                    break;
                case REIN:
                    sb.append("rein=" + player.getRein());
                    break;
                case EXP:
                    sb.append("exp=" + player.getExp());
                    break;
                case LEVEL:
                    sb.append("level=" + player.getLevel());
                    break;
                case GOLD:
                    sb.append("gold=" + player.getGold());
                    break;
                case DIAMOND:
                    sb.append("diamond=" + player.getDiamond());
                    break;
                case VIP:
                    sb.append("vip=" + player.getVip());
                    break;
                case EQUIPBAG:
                    sb.append("equipBag='" + player.getEquipBagJson() + "'");
                    break;
                case ITEM:
                    sb.append("item='" + player.getItemListJson() + "'");
                    break;
                case BOX:
                    sb.append("box='" + player.getBoxListJson() + "'");
                    break;
                case SPIRIT:
                    sb.append("spirit='" + player.getSpiritsJson() + "'");
                    break;
                case GODARTIFACT:
                    sb.append("godArtifact='" + player.getGodArtifactListJson() + "'");
                    break;
                case MAGICLEVEL:
                    sb.append("magiclevel=" + player.getMagicLevel());
                    break;
                case STATE:
                    sb.append("state=" + player.getState());
                    break;
                case CHAINMISSION:
                    sb.append("chainmission='" + player.getChainMissionJson() + "'");
                    break;
                case DAILYMISSION:
                    sb.append("dailymission='" + player.toDailyProgressString() + "'");
                    break;
                case SMALLDATA:
                    sb.append("smalldata='" + player.toSmallDataJson() + "'");
                    break;
                case MELTLV:
                    sb.append("meltLv=" + player.getMeltLv());
                    break;
                case MELTEXP:
                    sb.append("meltExp=" + player.getMeltExp());
                    break;
                case HONOR:
                    sb.append("honor=" + player.getHonor());
                    break;
                case ARENA:
                    sb.append("arena=" + player.getArena());
                    break;
                case YUANQI:
                    sb.append("yuanqi=" + player.getYuanqi());
                    break;
                case POINTS:
                    sb.append("points=" + player.getPoints());
                    break;
                case MAPID:
                    sb.append("mapid=" + player.getMapId());
                    break;
                case MAPSTAGEID:
                    sb.append("mapstageid=" + player.getMapStageId());
                    break;
                case BOSSCOUNT:
                    sb.append("bossCount=" + player.getBossCount());
                    break;
                case BOSSRECOVER:
                    sb.append("bossRecover=" + player.getBossRecover());
                    break;
                case REDLOTTERY:
                    sb.append("redLottery=" + player.getRedLottery());
                    break;
                case GANGBOSSCOUNT:
                    sb.append("gangBossCount=" + player.getGangBossCount());
                    break;
                case CITBOSSLEFT:
                    sb.append(type.getSql() + "=" + player.getCitBossLeft());
                    break;
                case CITRECOVE:
                    sb.append(type.getSql() + "=" + player.getCitRecover());
                    break;
                case CITCUE:
                    sb.append(type.getSql() + "='" + player.getCitCueJson() + "'");
                    break;
                case FIGHTING:
                    sb.append("fighting=" + player.getFighting());
                    break;
                case MAPREWARD:
                    sb.append("mapReward=").append(player.getMapReward());
                    break;
                case DAYDATA:
                    sb.append("daydata='").append(player.toDayDataJson()).append("'");
                    break;
                case LOGINTIME:
                    sb.append("loginTime='").append(DateUtil.formatDateTime(player.getLastLoginTime())).append("'");
                    break;
                case LOGOUTTIME:
                    sb.append("logoutTime='").append(DateUtil.formatDateTime(player.getLastLogoutTime())).append("'");
                    break;
                case DONATE:
                    sb.append("donate=" + player.getDonate());
                    break;
                case GANGSKILL:
                    sb.append("gangskill='" + player.toGangSkillJson() + "'");
                    break;
                case HUANHUALIST:
                    sb.append("huanhualist='" + player.getHuanhuaListJsonData() + "'");
                    break;
                case HUANHUAAPPEARANCE:
                    sb.append("huanhuaappearance='" + player.getHuanhuaAppearanceJsonData() + "'");
                    break;
                case TLPOINTS:
                    sb.append("tlpoints=" + player.getTlPoints());
                    break;
                case RSPOINTS:
                    sb.append(type.getSql() + "=" + player.getRsPoints());
                    break;
                case MAGICLEVELSTAR:
                    sb.append("magiclevelstar=" + player.getMagicLevelStar());
                    break;
                case MAGICSTAGE:
                    sb.append("magicstage=" + player.getMagicStage());
                    break;
                case MAGICSTAGESTAR:
                    sb.append("magicstagestar=" + player.getMagicStageStar());
                    break;
                case MAGICSTAGEEXP:
                    sb.append("magicstageexp=" + player.getMagicStageExp());
                    break;
                case DEKARON:
                    sb.append("dekaron=" + player.getDekaron());
                    break;
                case REQUESTFIGHTTIME:
                    sb.append("requestfighttime=" + player.getFightRequestTime());
                    break;
                case TITLE:
                    sb.append(type.getSql() + "='" + player.getTitleJson() + "'");
                    break;
                case SHARE:
                    sb.append(type.getSql() + "='" + player.getShareDataJson() + "'");
                    break;
                case AUCTION_SUBSCRIPTIONS:
                    sb.append(type.getSql()).append("='").append(player.getAuctionSubscriptionsStr()).append("'");
                    break;
                case DRAGON_BALL:
                    sb.append(type.getSql()).append("='").append(player.getDragonBallStr()).append("'");
                    break;
                case ACHIEVEMENT:
                    sb.append(type.getSql()).append("=").append(player.getAchievement());
                    break;
                case ACHIEVEMENT_MISSION:
                    sb.append(type.getSql()).append("='").append(player.getAchievementMissionStr()).append("'");
                    break;
                case MEDAL:
                    sb.append(type.getSql()).append("=").append(player.getMedal());
                    break;
                case AUCTION_BOX:
                    sb.append(type.getSql()).append("='").append(player.getAuctionBoxJson()).append("'");
                    break;
                case HEART_SKILL:
                    sb.append(type.getSql()).append("='").append(player.getHeartSkillJson()).append("'");
                    break;
                case ARTIFACT_PIECES:
                    sb.append(type.getSql()).append("='").append(player.getArtifactPiecesJson()).append("'");
                    break;
                case ARTIFACT_BOSS:
                    sb.append(type.getSql()).append("='").append(player.getArtifactBossJson()).append("'");
                    break;
                case COMBINE_RUNE:
                    sb.append(type.getSql()).append("='").append(player.getCombineRuneString()).append("'");
                    break;
                case COMBINE_RUNE_PIECE:
                    sb.append(type.getSql()).append("=").append(player.getCombineRunePiece());
                    break;
                case COMBINE_RUNE_BAG:
                    sb.append(type.getSql()).append("='").append(player.getCombineRuneBagJson()).append("'");
                    break;
                case WEIWANG:
                    sb.append(type.getSql()).append("='").append(player.getWeiWang()).append("'");
                    break;
                case ZHANWEN:
                    sb.append(type.getSql()).append("='").append(player.getZhanWensJson()).append("'");
                    break;
                case ZHANWEN_JINGHUA:
                    sb.append(type.getSql()).append("='").append(player.getZhanWenJinghua()).append("'");
                    break;
                case TLMISSION:
                    sb.append(type.getSql()).append("='").append(player.getTimeLimitMissionJson()).append("'");
                    break;
                case MYSTERYBOSSLEFT:
                    sb.append(type.getSql() + "=" + player.getMysteryBossLeft());
                    break;
                case MYSTERYCUE:
                    sb.append(type.getSql() + "='" + player.getMysteryCueJson() + "'");
                    break;
                case VIPBOSSCUE:
                    sb.append(type.getSql() + "='" + player.getVipBossCueJson() + "'");
                    break;
                case WINGGODS:
                    sb.append(type.getSql()).append("='").append(player.getWingGodsJson()).append("'");
                    break;
                case HOLYLINES:
                    sb.append(type.getSql()).append("='").append(player.getHolyLinesJson()).append("'");
                    break;
                case FENGMOTA:
                    sb.append("fengmota=" + player.getFengmota());
                    break;
                case ZHUZAISHILIAN:
                    sb.append("zhuzai=" + player.getZhuzai());
                    break;
                case CARD_BAG:
                    sb.append(type.getSql()).append("='").append(player.getCardBagJson()).append("' ");
                    break;
                case CARD_BOOK:
                    sb.append(type.getSql()).append("='").append(player.getCardBookJson()).append("'");
                    break;
                case CARD_MISSION:
                    sb.append(type.getSql()).append("='").append(player.getCardMissionJson()).append("'");
                    break;
                case DAILY_CONSUM:
                    sb.append(type.getSql()).append("='").append(player.getConsumeDaily()).append("'");
                    break;
                case FIVEELEMENT:
                    sb.append(type.getSql() + "='" + player.getFiveElementsJson() + "'");
                    break;
                case AMBIT:
                    sb.append(type.getSql() + "='" + player.getAmbitJson() + "'");
                    break;
                case LOGONTIME_FEST:
                    sb.append(type.getSql() + "=" + player.getLastLoginTime2Fest());
                    break;
                case SUUL_PIECE:
                    sb.append(type.getSql()).append("='").append(player.getLingSuiPiecesJson()).append("'");
                    break;
                case FAZHEN:
                    sb.append(type.getSql() + "='" + player.getFazhenListJson() + "'");
                    break;
                case VOUCHERS:
                    sb.append(type.getSql() + "=" + player.getVouchers());
                    break;
                case TOWN_SOUL:
                    sb.append(type.getSql() + "='" + player.getTownSoulTreasureJson() + "'");
                    break;
                case MYSTERY_INTEGRAL:
                    sb.append(type.getSql() + "=" + player.getMysteryIntegral());
                    break;
                case QIZHEN_INTEGRAL:
                    sb.append(type.getSql() + "=" + player.getQizhenIntegral());
                    break;
//                case TRAIN_ITEM:
//                	sb.append(type.getSql()+ "='" + player.getTrainItemsStr()+"'");
//                	break;
//                case TRAIN_ITEM_EQUIP:
//                	sb.append(type.getSql()+ "=" + player.getTrainItemEquipsStr());
//                	break;
                case GROW:
                    sb.append(type.getSql() + "='" + player.getGrowListJson() + "'");
                    break;
                case GROWEQUIP:
                    sb.append(type.getSql() + "='" + player.getGrowEquipListJson() + "'");
                    break;
                case SKILL:
                    sb.append(type.getSql() + "='" + player.getSkillListJson() + "'");
                    break;
                case SJGCOPY:
                    sb.append(type.getSql() + "=" + player.getSjgCopyId());
                    break;
                case TMMAXCOPYID:
                    sb.append(type.getSql() + "=" + player.getTmMaxCopyId());
                    break;
                case MZSTAR:
                    sb.append(type.getSql() + "=" + player.getMiSartTotal());
                    break;

                default:
                    isMatch = false;
                    break;
            }
            if (isMatch) {
                total++;
                sb.append(",");
            }
        }
        if (total == 0)
            return null;
        String fields = sb.toString();
        if (fields.endsWith(","))
            fields = fields.substring(0, fields.length() - 1);
        sb = new StringBuilder();
        sb.append("UPDATE player SET ").append(fields).append(" WHERE id=" + player.getId());
        return sb.toString();
    }

    /*    private String getCharacterUpdateSql(Character cha, EnumSet<EPlayerSaveType> enumSet) {
            if (0 == enumSet.size())
                return null;
            int total = 0;
            StringBuilder sb = new StringBuilder();
            Iterator<EPlayerSaveType> it = enumSet.iterator();
            while (it.hasNext()) {
                boolean isMatch = true;
                EPlayerSaveType type = it.next();
                if (type.getTb() != 1)
                    continue;
                switch (type) {
    //			case CHA_PID:
    //				sb.append(type.getSql()+"="+character.getPlayerId());
    //				break;
    //			case CHA_IDX:
    //				sb.append(type.getSql()+"="+character.getIdx());
    //				break;
                    case CHA_MERIDIAN:
                        sb.append(type.getSql() + "=" + cha.getMeridian());
                        break;
                    case CHA_EQUIP:
                        sb.append(type.getSql() + "='" + cha.toEquipListJson() + "'");
                        break;
                    case CHA_SKILL:
                        sb.append(type.getSql() + "='" + cha.toSkillListJson() + "'");
                        break;
                    case CHA_DOM:
                        sb.append(type.getSql() + "='" + cha.getDomListJson() + "'");
                        break;
                    case CHA_EQUIPSLOT:
                        sb.append(type.getSql() + "='" + cha.getEquipSlotListJson() + "'");
                        break;
                    case CHA_GONG:
                        sb.append(type.getSql() + "='" + cha.getGongJson() + "'");
                        break;
                    case CHA_SPIRIT:
                        sb.append(type.getSql() + "='" + cha.getSpiritJson() + "'");
                        break;
                    case CHA_TONGJING:
                        sb.append(type.getSql() + "=" + cha.getTongjing());
                        break;
                    case CHA_YUDI:
                        sb.append(type.getSql() + "=" + cha.getYudi());
                        break;
                    case CHA_ZUOYAN:
                        sb.append(type.getSql() + "=" + cha.getZuoyan());
                        break;
                    case CHA_YOUYAN:
                        sb.append(type.getSql() + "=" + cha.getYouyan());
                        break;
                    case CHA_MOUNTSTAR:
                        sb.append(type.getSql() + "=" + cha.getMountStar());
                        break;
                    case CHA_MOUNTJIEDUAN:
                        sb.append(type.getSql() + "=" + cha.getMountStage());
                        break;
                    case CHA_MOUNTEXP:
                        sb.append(type.getSql() + "=" + cha.getMountExp());
                        break;
                    case CHA_WINGS:
                        sb.append(type.getSql() + "='" + cha.getWingsJson() + "'");
                        break;
                    case CHA_WINGSHOW:
                        sb.append(type.getSql() + "=" + cha.getWingShow());
                        break;
                    case CHA_WEAPONS:
                        sb.append(type.getSql() + "='" + cha.getWeaponsJson() + "'");
                        break;
                    case CHA_WEAPONSHOW:
                        sb.append(type.getSql() + "=" + cha.getWeaponShow());
                        break;
                    case CHA_ARMORS:
                        sb.append(type.getSql() + "='" + cha.getArmorsJson() + "'");
                        break;
                    case CHA_ARMORSHOW:
                        sb.append(type.getSql() + "=" + cha.getArmorShow());
                        break;
                    case CHA_MOUNTS:
                        sb.append(type.getSql() + "='" + cha.getMountsJson() + "'");
                        break;
                    case CHA_MOUNTSHOW:
                        sb.append(type.getSql() + "=" + cha.getMountShow());
                        break;
                    case CHA_TITLE:
                        sb.append(type.getSql() + "=" + cha.getTitle());
                        break;
                    case CHA_GANGSKILL:
                        sb.append(type.getSql() + "='" + cha.getGangSkillJson() + "'");
                        break;
                    case CHA_GANGSKILL2:
                        sb.append(type.getSql() + "='" + cha.getGangSkill2Json() + "'");
                        break;
                    case CHA_MOUNTEQUIP:
                        sb.append(type.getSql() + "='" + cha.getWing().getEquipmentsJson() + "'");
                        break;
                    case CHA_HEART_SKILL_SLOT:
                        sb.append(type.getSql() + "='" + cha.getHeartSkillSlotJson() + "'");
                        break;
                    case CHA_SHENBING:
                        sb.append(type.getSql()).append("='").append(cha.getShenBingJson()).append("'");
                        break;
                    case CHA_ZHANWEN:
                        sb.append(type.getSql()).append("='").append(cha.getZhanWenJson()).append("'");
                        break;
                    case CHA_SOUL_GOD:
                        sb.append(type.getSql()).append("='").append(cha.getSoulListJson()).append("'");
                        break;
                    case CHA_TOWN_SOUL:
                        sb.append(type.getSql()).append("='").append(cha.getTownSoulEquipJson()).append("'");
                        break;
                    case CHA_HOLYGOODS:
                        sb.append(type.getSql()).append("='").append(cha.getHolyGoodsJson()).append("'");
                        break;
                    default:
                        isMatch = false;
                        break;
                }
                if (isMatch) {
                    total++;
                    sb.append(",");
                }
            }
            if (total == 0)
                return null;
            String fields = sb.toString();
            if (fields.endsWith(","))
                fields = fields.substring(0, fields.length() - 1);
            sb = new StringBuilder();
            sb.append("UPDATE characters SET ").append(fields).append(" WHERE playerid=").append(cha.getPlayerId())
                    .append(" AND idx=").append(cha.getIdx());
            return sb.toString();
        }
    */
    public static final String SIMPLE_PLAYER_FIELDS = " id, name, level, vip, fighting, rein ";

    @JSONField(serialize = false)
    public SimplePlayer getRandomPlayerByFighting(List<Integer> excludeList, int flow, int fhigh, String comp) {
        SimplePlayer playerRank = null;
        StringBuilder builder = new StringBuilder();
        builder.append(" SELECT ").append(SIMPLE_PLAYER_FIELDS)
                .append("  FROM ")
                .append(" (SELECT rowNo, FOUND_ROWS() AS rows, ").append(SIMPLE_PLAYER_FIELDS)
                .append("	 FROM ( SELECT (@rowNum:=@rowNum+1) as rowNo, ").append(SIMPLE_PLAYER_FIELDS)
                .append("			FROM player , (SELECT (@rowNum :=0)) AS r ")
                .append("	WHERE id NOT IN ( ");
        Iterator<Integer> iterator = excludeList.iterator();
        while (iterator.hasNext()) {
            builder.append(iterator.next());
            if (iterator.hasNext()) {
                builder.append(",");
            }
        }
        builder.append(" ) ")
                .append("	  AND " + comp + " BETWEEN ").append(flow).append(" AND ").append(fhigh).append(" ) AS tp ")
                .append("  )AS t1,")
                .append(" (SELECT RAND() as random) AS t2 ")
                .append(" WHERE CASE WHEN t1.rows > 1 THEN t1.rowNo = 1 + FLOOR(rows * random) ELSE t1.rowNo = 1 END ");
        ResultSet rs = dbOperator.executeQuery(builder.toString());
        try {
            if (rs.next()) {
                playerRank = new SimplePlayer();
                playerRank.setId(rs.getInt(1));
                playerRank.setName(rs.getString(2));
                playerRank.setLevel(rs.getShort(3));
                playerRank.setVip(rs.getInt(4));
                playerRank.setFighting(rs.getInt(5));
                playerRank.setRein(rs.getShort(6));
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            dbOperator.executeClose();
        }
        return playerRank;
    }

    public int updatePlayerMelt(Player player) {
        return dbOperator.executeSql("update player set meltLv=" + player.getMeltLv() + ",meltExp=" + player.getMeltExp() +
                " where id=" + player.getId());
    }

    public int updatePlayerVip(Player player) {
        StringBuilder sb = new StringBuilder();
        sb.append("  UPDATE player ")
                .append("   SET vip=").append(player.getVip())
                .append(" WHERE id=").append(player.getId());
        return dbOperator.executeSql(sb.toString());
    }

    public Map<String, BeatData> rewardBeatLevel() {
        Map<String, BeatData> datas = new HashMap<>();
        ResultSet rs = dbOperator.executeQuery("select channel,account,num,title,content from player_beat_level where state=0");
        try {
            while (rs.next()) {
                BeatData data = new BeatData();
                data.setChannel(rs.getInt(1));
                data.setAccount(rs.getString(2));
                data.setNum(rs.getInt(3));
                data.setTitle(rs.getString(4));
                data.setContent(rs.getString(5));
                datas.put(data.getChannel() + "_" + data.getAccount(), data);
            }
        } catch (SQLException e) {
            logger.error("读取封测等级数据异常", e);
        } finally {
            dbOperator.executeClose();
        }
        return datas;
    }

    public Map<String, BeatData> rewardBeatFight() {
        Map<String, BeatData> datas = new HashMap<>();
        ResultSet rs = dbOperator.executeQuery("select channel,account,num,title,content from player_beat_fighting where state=0");
        try {
            while (rs.next()) {
                BeatData data = new BeatData();
                data.setChannel(rs.getInt(1));
                data.setAccount(rs.getString(2));
                data.setNum(rs.getInt(3));
                data.setTitle(rs.getString(4));
                data.setContent(rs.getString(5));
                datas.put(data.getChannel() + "_" + data.getAccount(), data);
            }
        } catch (SQLException e) {
            logger.error("读取封测战力数据异常", e);
        } finally {
            dbOperator.executeClose();
        }
        return datas;
    }

    public Map<String, BeatData> rewardBeatLogin() {
        Map<String, BeatData> datas = new HashMap<>();
        ResultSet rs = dbOperator.executeQuery("select channel,account from player_beat_login where state=0");
        try {
            while (rs.next()) {
                BeatData data = new BeatData();
                data.setChannel(rs.getInt(1));
                data.setAccount(rs.getString(2));
                datas.put(data.getChannel() + "_" + data.getAccount(), data);
            }
        } catch (SQLException e) {
            logger.error("读取封测登录数据异常", e);
        } finally {
            dbOperator.executeClose();
        }
        return datas;
    }

    public int updateBeatLevel(int channel, String account) {
        return dbOperator.executeSql("UPDATE player_beat_level SET state=1 WHERE channel=" + channel + " and account='" + account + "'");
    }

    public int updateBeatFight(int channel, String account) {
        return dbOperator.executeSql("UPDATE player_beat_fighting SET state=1 WHERE channel=" + channel + " and account='" + account + "'");
    }

    public int updateBeatLogin(int channel, String account) {
        return dbOperator.executeSql("UPDATE player_beat_login SET state=1 WHERE channel=" + channel + " and account='" + account + "'");
    }

    public int clearTLPoints() {
        return dbOperator.executeSql("UPDATE player set tlpoints=0 where tlpoints<>0");
    }

    public int updateCDKey(Player player) {
        return dbOperator.executeSql("update player set cdkeys='" + player.getCDKeyListJson() + "'where id=" + player.getId());
    }

    public int updateTitle(Player player) {
        return dbOperator.executeSql("update player set title='" + player.getTitleJson() + "'where id=" + player.getId());
    }

    public int resetDaily(int id, String date) {
        return dbOperator.executeSql("update player set dailymission='', gangBossCount = 0, daydata='', dailyupdatemark='" + date + "' where id=" + id);
    }

    public int updateWeiWang(Map<Byte, GuanJieData> guanJieDataMap) {
        StringBuffer sb = new StringBuffer();

        sb.append("UPDATE player SET weiwang = CASE \n");

        for (byte i = (byte) guanJieDataMap.size(); i >= 1; i--) {
            GuanJieData guanJieData = guanJieDataMap.get(i);
            sb.append("WHEN weiwang >= ");
            sb.append(guanJieData.getNeed());
            sb.append(" THEN weiwang -");
            sb.append(guanJieData.getIncome());
            sb.append(" \n");
        }

        sb.append("ELSE weiwang END");

        return dbOperator.executeSql(sb.toString());

    }

    public int restMysteryBossLeft() {

        return dbOperator.executeSql("update player set mysteryBossLeft = 3");
    }

    public int freeze(int playerId) {
        return dbOperator.executeSql("UPDATE player set state=" + GameDefine.PLAYER_STATE_FREEZE + " where id=" + playerId);
    }

    public List<Integer> getPlayerByVip(int serverId, int vip) {
        List<Integer> list = new ArrayList<>();
        ResultSet rs = dbOperator.executeQuery("select id from player where serverid=" + serverId + " and vip>=" + vip);
        try {
            while (rs != null && rs.next()) {
                list.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            dbOperator.executeClose();
        }
        return list;
    }
}
