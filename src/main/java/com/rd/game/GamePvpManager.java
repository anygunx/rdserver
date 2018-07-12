package com.rd.game;

import com.rd.bean.mail.Mail;
import com.rd.bean.pvp.PvpInfo;
import com.rd.bean.pvp.PvpRank;
import com.rd.common.GameCommon;
import com.rd.common.MailService;
import com.rd.dao.PvpDao;
import com.rd.define.EGoodsChangeType;
import com.rd.define.PvpDefine;
import com.rd.model.FighterModel;
import com.rd.model.data.PvpRankData;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GamePvpManager {

    private static Logger logger = Logger.getLogger(GamePvpManager.class);

    private static GamePvpManager gamePvpManager = new GamePvpManager();

    public static GamePvpManager getInstance() {
        return gamePvpManager;
    }

    private volatile boolean isSortRank = true;

    //排行榜
    private List<PvpRank> rankList;

    public List<PvpRank> getRankList() {
        return rankList;
    }

    private GamePvpManager() {

    }

    public void init() {
        this.rankList = new PvpDao().getPvpRank(PvpDefine.RANK_COUNT);
        this.sortRank();
    }

    private void sortRank() {
        if (this.isSortRank) {
            this.isSortRank = false;
            List<PvpRank> tempList = new CopyOnWriteArrayList<>(this.rankList);
            Collections.sort(tempList, new SortByPvpRank());
            if (tempList.size() > PvpDefine.RANK_COUNT) {
                for (int i = tempList.size() - 1; i >= PvpDefine.RANK_COUNT; --i) {
                    tempList.remove(i);
                }
            }
            this.rankList = tempList;
            this.isSortRank = true;
        }
    }

    public void addRank(PvpInfo pvpInfo) {
        boolean isNew = true;
        for (PvpRank rank : this.rankList) {
            if (rank.getPlayer().getId() == pvpInfo.getPlayer().getId()) {
                rank.setPrestige(pvpInfo.getPrestige());
                isNew = false;
            }
        }
        if (isNew) {
            PvpRank rank = new PvpRank();
            rank.setPlayer(pvpInfo.getPlayer());
            rank.setPrestige(pvpInfo.getPrestige());
            this.rankList.add(rank);
        }
        this.sortRank();
    }

    public Message getRankMessage(int playerId) {
        Message message = new Message(MessageCommand.FIELD_PVP_RANK_MESSAGE);
        message.setShort(getRankByPlayerId(playerId));
        message.setShort(this.rankList.size());
        boolean isFirst = true;
        for (PvpRank rank : this.rankList) {
            message.setInt(rank.getPlayer().getId());
            message.setInt(rank.getPrestige());
            message.setString(rank.getPlayer().getName());
            message.setShort(rank.getPlayer().getLevel());
            message.setShort(rank.getPlayer().getRein());
            message.setByte(rank.getPlayer().getVipLevel());
            if (isFirst) {
                isFirst = false;
                rank.getPlayer().getAppearMessage(message);
            }
        }
        return message;
    }

    public short getRankByPlayerId(int playerId) {
        short rankNum = 1;
        for (PvpRank rank : this.rankList) {
            if (rank.getPlayer().getId() == playerId) {
                return rankNum;
            }
            ++rankNum;
        }
        return 0;
    }

    public void resetRank() {
        //排行榜发奖
        short rankNum = 1;
        for (PvpRank rank : this.rankList) {
            logger.info("rank:" + rankNum + " id:" + rank.getPlayer().getId() + " Prestige:" + rank.getPrestige());

            PvpRankData pvpRankData = FighterModel.getPvpRankData(rankNum);
            if (pvpRankData != null) {
                Mail mail = MailService.createMail(pvpRankData.getTitle(), pvpRankData.getContent(),
                        EGoodsChangeType.PVP_RANK_REWARD_ADD, pvpRankData.getRewardList());
                MailService.sendSystemMail(rank.getPlayer().getId(), mail);
            }
            //称号：遭遇榜
            if (rankNum == 1) {
                GameCommon.grantTitle(rank.getPlayer().getId(), 6);
            } else if (rankNum <= 10) {
                GameCommon.grantTitle(rank.getPlayer().getId(), 7);
            }
            ++rankNum;
        }
        //排行榜重置
        new PvpDao().updateResetPrestige();
        this.rankList.clear();
    }
}

class SortByPvpRank implements Comparator<PvpRank> {

    public SortByPvpRank() {
    }

    public int compare(PvpRank rank1, PvpRank rank2) {
        return Integer.valueOf(rank2.getPrestige()).compareTo(Integer.valueOf(rank1.getPrestige()));
    }
}
