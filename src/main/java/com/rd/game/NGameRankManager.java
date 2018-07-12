package com.rd.game;

import com.rd.bean.mail.Mail;
import com.rd.bean.player.Player;
import com.rd.bean.rank.NJingJiRank;
import com.rd.bean.rank.PlayerRank;
import com.rd.common.MailService;
import com.rd.dao.NRankDao;
import com.rd.define.EGoodsChangeType;
import com.rd.define.NRankType;
import com.rd.model.NJingJiModel;
import com.rd.model.data.jingji.NJingJiChangModel;
import org.apache.log4j.Logger;

import java.util.*;

public class NGameRankManager {
    private static Logger logger = Logger.getLogger(GameRankManager.class);

    private static final NGameRankManager _instance = new NGameRankManager();

    private NGameRankManager() {
    }

    public static NGameRankManager getInstance() {
        return _instance;
    }

    //排行榜数据
    private volatile Map<NRankType, List<PlayerRank>> gameRanks = new HashMap<>();


    private volatile Map<Integer, NJingJiRank> jingjiRankId = new HashMap<>();

    public void init() {
        Map<NRankType, List<PlayerRank>> temp = new HashMap<>();
        List<PlayerRank> sjgCopy = NRankDao.getInstance().getRankList(NRankType.COPY_SJG);
        temp.put(NRankType.COPY_SJG, sjgCopy);
        List<PlayerRank> tm = NRankDao.getInstance().getRankList(NRankType.COPY_TM);
        temp.put(NRankType.COPY_TM, tm);
        List<PlayerRank> mz = NRankDao.getInstance().getRankList(NRankType.COPY_MZ);
        temp.put(NRankType.COPY_MZ, mz);
        gameRanks = temp;
    }

    public List<PlayerRank> getSJGRankList() {
        return gameRanks.get(NRankType.COPY_SJG);
    }

    public List<PlayerRank> getRankByType(NRankType type) {
        return gameRanks.get(type);
    }

    public byte getPlayerRankByPlayerId(int playerId, NRankType type) {
        List<PlayerRank> list = gameRanks.get(type);
        if (list == null || list.isEmpty()) {
            return 0;
        }
        byte i = 1;
        for (PlayerRank playerRank : list) {
            if (playerRank.getId() == playerId) {
                return i;
            }
            i++;
        }
        return 0;

    }


    public void updataRank(Player player, NRankType type, int value) {
        List<PlayerRank> list = gameRanks.get(type);
        synchronized (gameRanks) {
            if (list == null) {
                list = new ArrayList<>();
                gameRanks.put(type, list);
            }
        }
        PlayerRank data = null;
        for (PlayerRank playerRank : list) {
            if (playerRank.getId() == player.getId()) {
                data = playerRank;
                break;
            }
        }
        if (data == null) {
            data = new PlayerRank();
            data.init(player);
            list.add(data);
        }
//		if(NRankType.COPY_SJG==type) {
//			data.setValue(player.getSjgCopyId());
//		}else if(NRankType.COPY_TM==type) {
//			data.setValue(player.getTmMaxCopyId());
//		}else if(NRankType.COPY_MZ==type) {
        data.setValue(value);
        //	}

        Collections.sort(list);
        if (list.size() > 20) {
            list = list.subList(0, 19);
        }
    }


    public void upgradeJingJiRank(int myId, int myrank, int otherId, int otherRank) {

        synchronized (jingjiRankId) {
            NJingJiRank my = jingjiRankId.get(myId);
            NJingJiRank other = jingjiRankId.get(otherId);
            if (my == null) {
                my = new NJingJiRank();
                my.setId(myId);
                my.setRank(otherRank);
                my.setType(1);
                jingjiRankId.put(myId, my);
            } else {
                my.setRank(otherRank);
            }

            if (other == null && otherId > 0) {
                other = new NJingJiRank();
                other.setId(myId);
                other.setRank(myrank);
                other.setType(1);
                jingjiRankId.put(otherId, other);
            } else if (other != null) {
                other.setRank(otherRank);
            }

        }
    }

    public NJingJiRank getNJingJiRank(int rank) {
        synchronized (jingjiRankId) {
            for (NJingJiRank rankData : jingjiRankId.values()) {
                if (rankData.getRank() == rank) {
                    return rankData;
                }
            }
        }
        return null;

    }

    public NJingJiRank getMinRank() {
        NJingJiRank min = null;
        for (NJingJiRank rankData : jingjiRankId.values()) {
            if (min == null) {
                min = rankData;
            } else {
                if (min.getRank() > rankData.getRank()) {
                    min = rankData;
                }
            }
        }
        return min;

    }

    public void sendMail() {

        for (NJingJiRank rankData : jingjiRankId.values()) {
            NJingJiChangModel model = getNJingJiChangModel(rankData.getRank());
            if (model == null) {
                continue;
            }
            Mail pm = MailService.createMail("测试竞技场奖励", "测试竞技场内容",
                    EGoodsChangeType.ARENA_PERSON_ADD, model.getRewards());
            MailService.sendSystemMail(rankData.getId(), pm);
        }


    }

    private NJingJiChangModel getNJingJiChangModel(int rank) {
        Map<Integer, NJingJiChangModel> map = NJingJiModel.getNJingJiChangModelMap();
        for (NJingJiChangModel model : map.values()) {
            boolean isHave = rangeInDefined(rank, model.getRank_min(), model.getRank_max());
            if (isHave) {
                return model;
            }
        }
        return null;

    }

    private boolean rangeInDefined(int rank, int min, int max) {
        return Math.max(min, rank) == Math.min(rank, max);
    }

}
