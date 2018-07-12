package com.rd.activity.event;

import com.rd.activity.ActivityService;
import com.rd.activity.EActivityType;
import com.rd.activity.config.BaseActivityConfig;
import com.rd.activity.data.TargetLogicData;
import com.rd.activity.group.ActivityGroupData;
import com.rd.activity.group.ActivityRoundConfig;
import com.rd.bean.drop.DropData;
import com.rd.bean.mail.Mail;
import com.rd.bean.player.Player;
import com.rd.bean.rank.ActivityRank;
import com.rd.common.MailService;
import com.rd.define.EGoodsChangeType;
import com.rd.game.GameRankManager;
import com.rd.game.GameRole;
import com.rd.model.activity.Activity7Model;
import com.rd.model.data.DaBiaoData;
import com.rd.net.message.Message;

import java.util.ArrayList;
import java.util.List;

public class TargetEvent implements IActivityEvent {
    /** 宝石达标 **/
//	public static final byte TYPE_JEWEL = 1;
    /**
     * 翅膀战斗力
     **/
    public static final byte TYPE_WING_FIGHTING = 1;
    /** 坐骑达标 **/
//	public static final byte TYPE_HORSE = 2;
    /**
     * 宝石战斗力
     **/
    public static final byte TYPE_JEWEL_FIGHTING = 2;
    /** 玉笛达标 **/
//	public static final byte TYPE_YUDI = 3;
    /**
     * 龙纹战斗力
     **/
    public static final byte TYPE_DRAGONPATTERN_FIGHTING = 3;
    /**
     * 龙鳞战斗力
     **/
    public static final byte TYPE_DRAGONSCALE_FIGHTING = 4;
    /** 经脉战力达标 **/
//	public static final byte TYPE_JINGMAI = 4;
    /**
     * 经脉战力达标
     **/
    public static final byte TYPE_JINGMAI_FIGHTING = 5;
    /**
     * 龙珠战斗力
     **/
    public static final byte TYPE_DRAGONBALL_FIGHTING = 6;
    /** 转生达标 **/
//	public static final byte TYPE_REIN = 5;
    /**
     * 战力达标
     **/
    public static final byte TYPE_FIGHT_FIGHTING = 7;

    @Override
    public boolean onStart() {
        return true;
    }

    @Override
    public boolean onEnd() {
        this.dailyExecute();
        return true;
    }

    @Override
    public void getMessage(Message msg, GameRole role) {
        long curr = System.currentTimeMillis();
        BaseActivityConfig configData = ActivityService.getActivityConfig(EActivityType.TARGET);
        ActivityRoundConfig currRound = configData.getCurrRound(0, curr);
        if (currRound == null)
            return;
        ActivityGroupData<TargetLogicData> group = ActivityService.getGroupData(EActivityType.TARGET);
        //活动数据轮次
        int round = group.getDataRound(currRound.getRound());
        msg.setByte(round);
//		TargetLogicData logic = group.getRound(round).get(round+"");
//		//领取记录
//		int result = role.getActivityManager().getTargetRank(logic);
//		//自己的信息
//		msg.setByte(result);
    }

    @Override
    public boolean isOpen(GameRole role) {
        return true;
    }

    public void dailyExecute() {
        BaseActivityConfig configData = ActivityService.getActivityConfig(EActivityType.TARGET);
        ActivityRoundConfig currRound = configData.getCurrRound(0, System.currentTimeMillis() - 3600000);
        if (currRound == null)
            return;
        GameRankManager.getInstance().copyHistoryTargetRank(currRound.getRound());
        List<ActivityRank> ranks = GameRankManager.getInstance().getHistoryTargetRanks();
        if (ranks == null)
            return;
        ActivityGroupData<TargetLogicData> group = ActivityService.getGroupData(EActivityType.TARGET);
        //活动数据轮次
        int round = group.getDataRound(currRound.getRound());
        TargetLogicData logic = group.getRound(round).get(round + "");
        DaBiaoData data = Activity7Model.getDaBiaoReward(logic.getId());
        for (int i = 0; i < ranks.size(); i++) {
            String title = "", content = "";
            List<DropData> rewards = new ArrayList<>();
            ActivityRank rank = ranks.get(i);
            if (i == 0) {
                title = data.getFirstTitle();
                content = data.getFirstContent();
                rewards.addAll(data.getFirstReward());
            } else if (i == 1) {
                title = data.getSecondTitle();
                content = data.getSecondContent();
                rewards.addAll(data.getSecondReward());
            } else if (i == 2) {
                title = data.getThirdTitle();
                content = data.getThirdContent();
                rewards.addAll(data.getThirdReward());
            } else if (i < 20) {
                title = data.getForthTitle();
                content = data.getForthContent();
                rewards.addAll(data.getForthReward());
            }
            if (i < 20) {
                Mail mail = MailService.createMail(title, content, EGoodsChangeType.TARGET_REWARD_ADD, rewards);
                MailService.sendSystemMail(rank.getId(), mail);
            }
        }
    }

    public static ActivityRank getActivityRank(Player player, byte type) {
        ActivityRank rank = new ActivityRank();
        rank.setId(player.getId());
        rank.setN(player.getName());
        rank.setVn(player.getVipLevel());
        rank.setM(System.currentTimeMillis());
//		List<Character> characterList = player.getCharacterList();
        switch (type) {
//		case TYPE_JEWEL_FIGHTING://翅膀战斗力
//			int fighting_jewal = 0;
//			for(Character cha : characterList) {
//				fighting_jewal += cha.getJewelFighting();
//			}
//			rank.setV1(fighting_jewal);
//			break;
//		case TYPE_WING_FIGHTING:
//			int fighting_wing = 0;
//			for (Character cha : player.getCharacterList()) {
//				fighting_wing += cha.getWingFighting();
//			}
//			rank.setV1(fighting_wing);
//			break;
//		case TYPE_DRAGONPATTERN_FIGHTING:
//			int fighting_dragonPattern = 0;
//			for (Character cha : player.getCharacterList()) {
//				fighting_dragonPattern += cha.getTongjingFighting();
//			}
//			rank.setV1(fighting_dragonPattern);
//			break;
            case TYPE_JINGMAI_FIGHTING:
                // 经脉战力
                rank.setV1(player.getJingMaiFighting());
                break;
            case TYPE_FIGHT_FIGHTING:
                rank.setV1((int) player.getFighting());
                break;
            case TYPE_DRAGONBALL_FIGHTING:
                rank.setV1(player.getDragonBallFighting());
                break;
            case TYPE_DRAGONSCALE_FIGHTING:
                int fighting_dragonBall = 0;
//			for(Character cha : characterList) {
//				fighting_dragonBall += cha.getYudiFighting();
//			}
                rank.setV1(fighting_dragonBall);
                break;
            default:
                break;
        }
        return rank;
    }


}
