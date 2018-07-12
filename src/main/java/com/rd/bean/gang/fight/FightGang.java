package com.rd.bean.gang.fight;

import com.rd.bean.gang.Gang;
import com.rd.bean.gang.GangMember;
import com.rd.bean.mail.Mail;
import com.rd.bean.player.Player;
import com.rd.common.ChatService;
import com.rd.common.MailService;
import com.rd.define.EBroadcast;
import com.rd.define.EGoodsChangeType;
import com.rd.define.GangDefine;
import com.rd.game.GameGangManager;
import com.rd.game.GameWorld;
import com.rd.game.IGameRole;
import com.rd.model.GangModel;
import com.rd.model.data.GangFightRewardData;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FightGang {

    static Logger log = Logger.getLogger(FightGang.class.getName());

    private Gang gang;

    private List<FightTarget> targetList;

    private byte state;

    private byte round;

    private short starNum;

    private int score;

    private List<FightTarget> fightTargetList;

    private Queue<FightGangLog> fightGangLogQueue;

    private short totalStarNum;

    private int totalScore;

    private byte index;

    private Set<Integer> joinPlayerSet;

    public Gang getGang() {
        return gang;
    }

    public List<FightTarget> getTargetList() {
        return targetList;
    }

    public void setState(byte state) {
        this.state = state;
    }

    public byte getState() {
        return state;
    }

    public byte getRound() {
        return round;
    }

    public void setRound(byte round) {
        this.round = round;
    }

    public short getStarNum() {
        return starNum;
    }

    public void setStarNum(short starNum) {
        this.starNum = starNum;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public FightGang(Gang gang, byte index) {
        this.gang = gang;
        this.index = index;
    }

    public List<FightTarget> getFightTargetList() {
        return fightTargetList;
    }

    public Queue<FightGangLog> getFightGangLogQueue() {
        return fightGangLogQueue;
    }

    public void setFightGangLogQueue(Queue<FightGangLog> fightGangLogQueue) {
        this.fightGangLogQueue = fightGangLogQueue;
    }

    public short getTotalStarNum() {
        return totalStarNum;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public byte getIndex() {
        return index;
    }

    public void setIndex(byte index) {
        this.index = index;
    }

    public void fightReady() {
        joinPlayerSet = new HashSet<>();
        List<GangMember> list = new ArrayList<>();
        for (GangMember member : gang.getMemberMap().values()) {
            list.add(member);
            joinPlayerSet.add(member.getPlayerId());
        }
        Collections.sort(list, new SortByGangMemberFighting());

        targetList = new ArrayList<>();
        for (int i = 0; i < GangDefine.GANG_FIGHT_MEMBER_NUM; ++i) {
            if (i < list.size()) {
                IGameRole role = GameWorld.getPtr().getGameRole(list.get(i).getPlayerId());
                targetList.add(new FightTarget(role.getPlayer()));
            }
        }
        this.reset();
    }

    public void reset() {
        this.state = GangDefine.GANG_FIGHT_GANG_STATE_FIGHT;
        for (FightTarget fightTarget : this.targetList) {
            fightTarget.reset();
        }
        this.fightTargetList = new ArrayList<>();
        for (FightTarget fightTarget : this.targetList) {
            this.fightTargetList.add(fightTarget);
        }
        this.fightGangLogQueue = new ConcurrentLinkedQueue<>();
        this.totalStarNum += this.starNum;
        this.totalScore += this.score;
        this.starNum = 0;
        this.score = 0;
    }

    public boolean isLose(FightTarget target) {
        for (FightTarget fightTarget : this.fightTargetList) {
            if (fightTarget == target) {
                this.fightTargetList.remove(fightTarget);
                break;
            }
        }
        return this.fightTargetList.isEmpty();
    }

    public void sendReward(byte rank) {
        log.info("Gang Fight GangID:" + this.gang.getId() + " Name:" + this.gang.getName() + " Rank:" + rank + " Star:" + this.starNum + "Score:" + this.score + " Star:" + this.totalStarNum + "Score:" + this.totalScore);

        if (this.starNum == 0 && this.score == 0 && this.totalStarNum == 0 && this.totalScore == 0) {
            for (int playerId : this.joinPlayerSet) {
                IGameRole role = GameWorld.getPtr().getGameRole(playerId).getPlayer();
                if (role != null) {
                    FightPlayer fightPlayer = GameGangManager.getInstance().getFightPlayer(role.getPlayer());
                    if (fightPlayer != null) {
                        this.starNum += fightPlayer.getStarNum();
                        this.score += fightPlayer.getScore();
                    }
                }
            }
            if (this.starNum == 0 && this.score == 0 && this.totalStarNum == 0 && this.totalScore == 0) {
                return;
            }
        }

        GangFightRewardData gangFightRankData = GangModel.getGangFightReward(rank);
        if (gangFightRankData != null) {
            gang.addStore(gangFightRankData.getStoreReward());
            for (GangMember member : gang.getMemberMap().values()) {
                Mail mail = MailService.createMail(gangFightRankData.getMemberTitle(), gangFightRankData.getMemberContent(), EGoodsChangeType.GANG_FIGHT_WIN_ADD, gangFightRankData.getMemberReward());
                MailService.sendSystemMail(member.getPlayerId(), mail);
            }
        }

        //颁发冠军特殊奖励
        if (1 == rank) {
            for (GangMember member : gang.getMemberMap().values()) {
                if (member.getPosition() == GangDefine.GANG_POSITION_PRESIDENT) {
                    Mail masterMail = MailService.createMail(GangModel.getGangFightChampionMasterReward().getTitle(), GangModel.getGangFightChampionMasterReward().getContent(), EGoodsChangeType.GANG_FIGHT_WIN_ADD, GangModel.getGangFightChampionMasterReward().getReward());
                    MailService.sendSystemMail(member.getPlayerId(), masterMail);
                } else {
                    Mail mail = MailService.createMail(GangModel.getGangFightChampionMemberReward().getTitle(), GangModel.getGangFightChampionMemberReward().getContent(), EGoodsChangeType.GANG_FIGHT_WIN_ADD, GangModel.getGangFightChampionMemberReward().getReward());
                    MailService.sendSystemMail(member.getPlayerId(), mail);
                }
            }
            //跑马灯
            ChatService.broadcastPlayerMsg(new Player(), EBroadcast.GANG_CHAMPION, gang.getName());
        }
    }

    public FightTarget getTargetPlayer(int playerId) {
        for (FightTarget fightTarget : this.targetList) {
            if (fightTarget.getPlayer().getId() == playerId) {
                return fightTarget;
            }
        }
        return null;
    }

    public String getGangName() {
        return gang == null ? "" : gang.getName();
    }

    public void updateFighting(FightGang targetFightGang, int score) {
        short starNum = 0;
        for (FightTarget fightTarget : targetFightGang.getTargetList()) {
            starNum += GangDefine.GANG_FIGHT_STAR[fightTarget.getBeStar()];
        }
        this.starNum = starNum;
        this.score += score;
    }

    public void addLog(String selfName, String targetName, byte beStar, byte star, int score) {
        this.fightGangLogQueue.add(new FightGangLog(selfName, targetName, beStar, star, score));
        if (this.fightGangLogQueue.size() > GangDefine.GANG_LOG_NUM) {
            this.fightGangLogQueue.poll();
        }
    }

    public boolean isJoin(int playerId) {
        return this.joinPlayerSet.contains(playerId);
    }
}

class SortByGangMemberFighting implements Comparator<GangMember> {

    public SortByGangMemberFighting() {
    }

    public int compare(GangMember m1, GangMember m2) {
        return Long.valueOf(m2.getSimplePlayer().getFighting()).compareTo(Long.valueOf(m1.getSimplePlayer().getFighting()));
    }
}