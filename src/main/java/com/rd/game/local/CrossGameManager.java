package com.rd.game.local;

import com.rd.bean.drop.DropData;
import com.rd.bean.player.ArenaChallenge;
import com.rd.bean.player.BattlePlayer;
import com.rd.bean.player.Player;
import com.rd.bean.pvp.CrossData;
import com.rd.bean.rank.PlayerRank;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.CrossDao;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.define.ErrorDefine;
import com.rd.define.GameDefine;
import com.rd.game.GameRole;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;
import com.rd.util.StringUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * 跨服管理器
 *
 * @author U-Demon Created on 2017年5月20日 下午4:30:43
 * @version 1.0.0
 */
public class CrossGameManager {

    private static final Logger logger = Logger.getLogger(CrossGameManager.class);

    private GameRole role;

    private Player player;

    //跨服周边数据
    private CrossData crossData;

    public CrossGameManager(GameRole role) {
        this.role = role;
        this.player = role.getPlayer();
    }

    //挑战列表
    private List<ArenaChallenge> challenges = new ArrayList<>();

    public void init() {
        this.crossData = new CrossDao().getPlayerCross(player.getId());
    }

    /**
     * 竞技场挑战数据
     *
     * @param request
     */
    public void processArenaChallengeInfo(Message request) {
        //向PVP服更新数据
        GameHttpManager.gi().sendBattlePlayerInfo(player);
        //是否刷新数据
        boolean refresh = false;
        if (!ArenaGameService.open) {
            challenges.clear();
            refresh = false;
        } else {
//			refresh = challenges.size() == 0;
            refresh = true;
        }
        Message msg = getArenaChallengeMsg(refresh);
        msg.setChannel(request.getChannel());
        role.sendMessage(msg);
    }

    private Message getArenaChallengeMsg(boolean refresh) {
        //消息
        Message msg = new Message(MessageCommand.CROSS_ARENA_CHALLENGE_INFO_MESSAGE);
        //挑战信息
        String result = GameHttpManager.gi().getArenaRankInfo(player, refresh);
        String[] infos = result.split(GameHttpManager.SPLIT);
        if (infos == null || infos.length < 2) {
            msg.setByte(0);
            msg.setInt(24 * 3600);
            msg.setByte(0);
            msg.setByte(0);
            msg.setInt(GameDefine.ARENA_COUNT);
            msg.setInt(0);
            msg.setByte(0);
            return msg;
        }
        int rank = Integer.valueOf(infos[0]);
        if (infos.length > 2) {
            challenges = StringUtil.gson2ListAC(infos[2]);
            for (ArenaChallenge ranrrk : challenges) {
                ranrrk.decodeName();
            }
        }
        long curr = System.currentTimeMillis();
        boolean open = ArenaGameService.open;
        if (open) {
            if (ArenaGameService.currRoundEndTime <= curr || curr - DateUtil.getDayStartTime(GameDefine.SERVER_CREATE_TIME)
                    < DateUtil.DAY * 3)
                open = false;
        }
        msg.setByte(open ? 1 : 0);
        if (open)
            msg.setInt((int) ((ArenaGameService.currRoundEndTime - curr) / 1000));
        else
            msg.setInt((int) ((ArenaGameService.nextRoundStartTime - curr) / 1000));
        //个人排名
        msg.setByte(rank);
        //服务器排名
        if (infos[1].equals("NO")) {
            msg.setByte(0);
        } else {
            String[] serverRanks = infos[1].split(";");
            msg.setByte(serverRanks.length);
            for (String sr : serverRanks) {
                msg.setString(sr);
            }
        }
        //挑战次数
        msg.setInt(crossData.getArenaCount());
        //购买次数
        msg.setInt(crossData.getArenaBuy());
        //挑战列表
        List<ArenaChallenge> list = challenges;
        //如果挑战者为空时显示前5名
        if (challenges.size() == 0) {
            list = ArenaGameService.lastArenaRank;
        }
        if (list == null) {
            msg.setByte(0);
        } else {
            msg.setByte(list.size());
            for (ArenaChallenge challenge : list) {
                challenge.getMessage(msg);
                challenge.getAppearCha().getMsg(msg);
            }
        }
        return msg;
    }

    public void processArenaChallengeRefresh(Message request) {
        //是否开放
        if (!ArenaGameService.open) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_CHALLENGER_IN_BATTLE);
            return;
        }
        //消息频繁
        if (role.getCheatManager().requestFrequent(request)) {
            return;
        }
        //是否在名单中
        if (!ArenaGameService.inPicks(player.getId())) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_ARENA_NO_PICK);
            return;
        }
        //挑战信息
        String result = GameHttpManager.gi().getArenaRankInfo(player, true);
        String[] infos = result.split(GameHttpManager.SPLIT);
        if (infos == null || infos.length < 3) {
            return;
        }
        challenges = StringUtil.gson2ListAC(infos[2]);
        for (PlayerRank ranrrk : challenges) {
            ranrrk.decodeName();
        }
        //消息
        Message msg = new Message(MessageCommand.CROSS_ARENA_CHALLENGE_REFRESH_MESSAGE, request.getChannel());
        //挑战列表
        msg.setByte(challenges.size());
        for (ArenaChallenge challenge : challenges) {
            challenge.getMessage(msg);
            challenge.getAppearCha().getMsg(msg);
        }
        role.sendMessage(msg);
    }

    /**
     * 开始竞技场战斗
     *
     * @param request
     */
    public void processArenaBattle(Message request) {
        int rank = request.readByte();
        int selfRank = request.readByte();
        //消息频繁
        if (role.getCheatManager().requestFrequent(request)) {
            return;
        }
        if (System.currentTimeMillis() - DateUtil.getDayStartTime(GameDefine.SERVER_CREATE_TIME)
                < DateUtil.DAY * 3) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_CHALLENGER_IN_BATTLE);
            return;
        }
        //是否开放
        if (!ArenaGameService.open) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_CHALLENGER_IN_BATTLE);
            return;
        }
        //是否在名单中
        if (!ArenaGameService.inPicks(player.getId())) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_ARENA_NO_PICK);
            return;
        }
        //次数
        if (crossData.getArenaCount() <= 0) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_ARENA_FIGHT_COUNT_FULL);
            return;
        }
        //挑战
        String result = GameHttpManager.gi().arenaBattleFight(player, rank, selfRank);
        if (result.equals("NoRank")) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        if (result.equals("NoPlayer")) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        if (result.equals("NoSelfRank")) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_ARENA_CHALLENGER_ALTERED);
            return;
        }
        //向PVP服更新数据
        GameHttpManager.gi().sendBattlePlayerInfo(player);
        //扣除次数
        crossData.addArenaCount(-1);
        String[] infos = result.split(GameHttpManager.SPLIT);
        Message msg = new Message(MessageCommand.CROSS_ARENA_BATTLE_MESSAGE, request.getChannel());
        //挑战次数
        boolean succ = infos[0].equals("succ");
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        //战斗结果
        if (succ) {
            msg.setByte(1);
            role.getPackManager().addGoods(ArenaGameService.ARENA_WIN_REWARD, EGoodsChangeType.ARENA_FIGHT_ADD, saves);
        } else {
            msg.setByte(0);
            role.getPackManager().addGoods(ArenaGameService.ARENA_LOSE_REWARD, EGoodsChangeType.ARENA_FIGHT_ADD, saves);
        }
        //前后名次
        msg.setByte(Integer.valueOf(infos[1]));
        msg.setByte(Integer.valueOf(infos[2]));
        //是否玩家
        if (infos[3].equals("1")) {
            msg.setBool(false);
            BattlePlayer enemy = StringUtil.gson2Obj(infos[4], BattlePlayer.class);
            enemy.decodeName();
            enemy.getMsg(msg);
        } else {
            msg.setBool(true);
            PlayerRank enemy = StringUtil.gson2Obj(infos[4], PlayerRank.class);
            enemy.decodeName();
            msg.setInt(enemy.getId());
            msg.setString(enemy.getName());
            msg.setLong(enemy.getFighting());
        }
        role.sendMessage(msg);
        //保存数据
        role.savePlayer(saves);
        new CrossDao().updatePlayerArena(crossData);
        //战斗胜利刷新排名及列表
        if (succ) {
            //挑战信息
            String challenge = GameHttpManager.gi().getArenaRankInfo(player, true);
            String[] cs = challenge.split(GameHttpManager.SPLIT);
            if (cs == null || cs.length < 3)
                return;
            challenges = StringUtil.gson2ListAC(cs[2]);
            for (ArenaChallenge ranrrk : challenges) {
                ranrrk.decodeName();
            }
        }
    }

    /**
     * 竞技场排行榜
     *
     * @param request
     */
    public void processArenaRankList(Message request) {
        String result = GameHttpManager.gi().arenaRankList();
        Message msg = new Message(MessageCommand.CROSS_ARENA_RANK_LIST_MESSAGE, request.getChannel());
        if (result.equals("fail")) {
            msg.setByte(0);
        } else {
            String[] rs = result.split(GameHttpManager.SPLIT);
            List<PlayerRank> list = StringUtil.gson2ListPR(rs[0]);
            msg.setByte(list.size());
            for (PlayerRank pr : list) {
                msg.setShort(pr.getValue2());
                pr.decodeName();
                pr.getSimpleMessage(msg);
            }
        }
        role.sendMessage(msg);
    }

    /**
     * 竞技场次数购买
     *
     * @param request
     */
    public void processArenaCountBuy(Message request) {
        if (crossData.getArenaCount() >= GameDefine.ARENA_COUNT) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_ARENA_BUY_COUNT_FULL);
            return;
        }
        //花费
        int cost = (crossData.getArenaBuy() + 1) * 10;
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        if (!role.getPackManager().useGoods(new DropData(EGoodsType.DIAMOND, 0, cost),
                EGoodsChangeType.ARENA_BUY_CONSUME, saves)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
            return;
        }
        crossData.addArenaCount(1);
        crossData.addArenaBuy(1);
        Message msg = new Message(MessageCommand.CROSS_ARENA_BATTLE_COUNT_BUY, request.getChannel());
        msg.setInt(crossData.getArenaCount());
        msg.setInt(crossData.getArenaBuy());
        role.sendMessage(msg);
    }

    /**
     * 竞技场战斗记录
     *
     * @param request
     */
    public void processArenaRecord(Message request) {

    }

    public void resetCrossData() {
        crossData.setArenaCount(GameDefine.ARENA_COUNT);
        crossData.setArenaBuy(0);
    }

}
