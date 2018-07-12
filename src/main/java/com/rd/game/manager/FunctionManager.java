package com.rd.game.manager;

import com.rd.activity.EActivityType;
import com.rd.bean.artifact.ArtifactBoss;
import com.rd.bean.boss.Boss;
import com.rd.bean.boss.BossBattlePlayer;
import com.rd.bean.dragonball.DragonBall;
import com.rd.bean.drop.DropData;
import com.rd.bean.dungeon.Dungeon;
import com.rd.bean.dungeon.DungeonData;
import com.rd.bean.dungeon.DungeonZhuzaisaodangData;
import com.rd.bean.dungeon.DungeonZhuzaishilianData;
import com.rd.bean.fighter.FighterData;
import com.rd.bean.lianti.Ambit;
import com.rd.bean.mail.Mail;
import com.rd.bean.player.Player;
import com.rd.bean.player.ShareData;
import com.rd.bean.rank.ActivityRank;
import com.rd.bean.relationship.RelatedPlayer;
import com.rd.bean.role.RoleData;
import com.rd.common.BossService;
import com.rd.common.GameCommon;
import com.rd.common.MailService;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.*;
import com.rd.game.GameRankManager;
import com.rd.game.GameRole;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.GameEvent;
import com.rd.model.*;
import com.rd.model.data.*;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import com.rd.net.message.MessageArray;
import com.rd.util.DateUtil;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 一些零散的功能管理
 *
 * @author Created by U-Demon on 2016年11月3日 下午7:34:33
 * @version 1.0.0
 */
public class FunctionManager {

    private static final Logger logger = Logger.getLogger(FunctionManager.class);


    private byte fightResult = FightDefine.FIGHT_RESULT_FAIL;
    private List<DropData> rewardList = new ArrayList<>();

    private GameRole gameRole;
    private Player player;
    private DropData dropData = null;
    public static final short LINGSUI_MAX = 20;

    public FunctionManager(GameRole role) {
        this.gameRole = role;
        this.player = role.getPlayer();
    }

    public void init() {

    }

    /**
     * 经脉升级
     *
     * @param request
     */
    public void processMeridianUpgrade(Message request) {
//		byte idx = request.readByte();
//		Character cha = player.getCharacter(idx);
//		//获取下一级数据
//		MeridianModelData next = MeridianModel.getMeridianData(cha.getMeridian() + 1);
//		if (next == null)
//		{
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_MERIDIAN_MAX);
//			return;
//		}
//		EnumSet<EPlayerSaveType> enumSet=EnumSet.of(EPlayerSaveType.CHA_MERIDIAN);
//		//消耗
//		if (next.getCost().getN() > 0)
//		{			
//			if (!gameRole.getPackManager().useGoods(next.getCost(), EGoodsChangeType.MERIDIAN_UP_CONSUME,enumSet))
//			{
//				gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//				return;
//			}
//		}
//		//升级
//		cha.addMeridian(1);
//		//通知经脉升级消息
//		gameRole.getEventManager().notifyEvent(EGameEventType.MERIDIAN_UP.create(gameRole,cha.getMeridian(),enumSet));
//		
//		//发送消息
//		Message msg = new Message(MessageCommand.MERIDIAN_UPGRADE_MESSAGE, request.getChannel());
//		msg.setByte(idx);
//		msg.setShort(cha.getMeridian());
//		gameRole.sendMessage(msg);
//		//保存数据
//		gameRole.saveData(idx, enumSet);
    }

    /**
     * 离线经验领取
     *
     * @param msgs
     */
    public void getOfflineRewardMessage(MessageArray msgs) {
        //离线2分钟后给收益
        long currentTime = System.currentTimeMillis();
        long offlineTime = currentTime - player.getFightRequestTime();
        if (offlineTime > FightDefine.OFFLINE_FIGHT_TIME + FightDefine.OFFLINE_FIGHT_WAVE_TIME) {
            player.setFightRequestTime(currentTime);
            offlineTime -= FightDefine.OFFLINE_FIGHT_TIME;
            int wave = (int) (offlineTime / FightDefine.OFFLINE_FIGHT_WAVE_TIME);
            //5秒1波怪 8小时后 收益 80% 最高24小时

            int gold = 0;
            int exp = 0;
            int equipNum = 0;
            int meltingEquip = 0;
            int totalEquipNum = 0;

            if (wave > FightDefine.OFFLINE_FIGHT_WAVE_24H) {
                wave = FightDefine.OFFLINE_FIGHT_WAVE_24H;
            }
            boolean isExceed8H = true;
            if (wave <= FightDefine.OFFLINE_FIGHT_WAVE_8H) {
                isExceed8H = false;

                gold = MapModel.getMapStageRewardDataById(player.getMapStageId()).getMonsterGold() * wave;
                exp = MapModel.getMapStageRewardDataById(player.getMapStageId()).getMonsterExp() * wave;
                totalEquipNum = (int) (wave * 0.1f);
            } else {
                gold += MapModel.getMapStageRewardDataById(player.getMapStageId()).getMonsterGold() * FightDefine.OFFLINE_FIGHT_WAVE_8H * 0.8f;
                exp += MapModel.getMapStageRewardDataById(player.getMapStageId()).getMonsterExp() * FightDefine.OFFLINE_FIGHT_WAVE_8H * 0.8f;
                totalEquipNum = (int) (FightDefine.OFFLINE_FIGHT_WAVE_8H * 0.08f);
            }
            if (isExceed8H) {
                wave -= FightDefine.OFFLINE_FIGHT_WAVE_8H;

                gold += MapModel.getMapStageRewardDataById(player.getMapStageId()).getMonsterGold() * wave * 0.8f;
                exp += MapModel.getMapStageRewardDataById(player.getMapStageId()).getMonsterExp() * wave * 0.8f;
                totalEquipNum += (int) (wave * 0.08f);
            }

            //金币加成
            float addGold = 1;
            //神器4激活 挂机金币获取提高5%
            if (player.isGodArtifactActive(4)) {
                addGold += 0.05f;
            }
            //神器6激活 挂机金币获取提高5%
            if (player.isGodArtifactActive(6)) {
                addGold += 0.05f;
            }

            //经验加成
            float addExp = 1;
            //神器5激活 挂机经验获取提高5%
            if (player.isGodArtifactActive(5)) {
                addExp += 0.05f;
            }

            //月卡加成
            if (gameRole.getActivityManager().isMonthlyCard()) {
                addGold += 0.1f;
                addExp += 0.1f;
            }
            //终生卡加成
            //if(player.getForever() > 0){
            //	addGold+=0.1f;
            //	addExp+=0.1f;
            //}

            gold *= addGold;
            exp *= addExp;

            EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);

            //EGoodsType.getGoodsType(EGoodsType.GOLD.getId()).getCmd().reward(gameRole, new DropData(EGoodsType.GOLD,0,gold), EGoodsChangeType.OFFLINE_EXP_ADD, enumSet,false);
            //EGoodsType.getGoodsType(EGoodsType.EXP.getId()).getCmd().reward(gameRole, new DropData(EGoodsType.EXP,0,exp), EGoodsChangeType.OFFLINE_EXP_ADD, enumSet,false);

            if (totalEquipNum > player.getEquipBagFreeGrid()) {
                equipNum = player.getEquipBagFreeGrid();
                meltingEquip = totalEquipNum - player.getEquipBagFreeGrid();
            } else {
                equipNum = totalEquipNum;
                meltingEquip = 0;
            }

            FighterData fighterData = FighterModel.getFighterDataById(MapModel.getMapStageBossId(player.getMapStageId()));

            for (int i = 0; i < equipNum; ++i) {
                byte occupation = GameCommon.getRandomEquipOccupation();
                byte equipPos = GameCommon.getRandomEquipPosition();
                short equipLevel = GameCommon.getDropEquipLevel(fighterData.getLevel());

                DropData dropData = new DropData();
                dropData.setT(EGoodsType.EQUIP.getId());
                dropData.setG(GoodsModel.getEquipId(equipLevel, occupation, equipPos));
                dropData.setQ((byte) GameCommon.getRandomIndex(8000, 2000));
                dropData.setN(1);

                //EGoodsType.getGoodsType(EGoodsType.EQUIP.getId()).getCmd().reward(gameRole, dropData, EGoodsChangeType.OFFLINE_EXP_ADD, enumSet,false);
            }
            //熔炼金币
            gold += gameRole.getEquipManager().getMeltingEquipGold(GameCommon.getDropEquipLevel(fighterData.getLevel())) * EquipDefine.MELT_QUALITY_FACTOR[0] * meltingEquip;

            Message message = new Message(MessageCommand.OFFLINE_EXP_MESSAGE);
            message.setLong(offlineTime);
            message.setInt(gold);
            message.setInt(exp);
            message.setShort(equipNum);
            message.setInt(meltingEquip);
            msgs.addMessage(message);

            enumSet.add(EPlayerSaveType.REQUESTFIGHTTIME);
            gameRole.getDbManager().playerDao.savePlayer(player, enumSet);
        }
    }

    /**
     * 领取野外BOSS通关奖励
     *
     * @param request
     */
    public void processFieldBossReward(Message request) {
        if (player.getMapReward() == player.getMapStageId()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_MAP_REWARDED);
            return;
        }

        short rewardStage = (short) (player.getMapReward() + 10);
        if (gameRole.getFightManager().getClearanceStage() < rewardStage) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_MAP_UNREWARD);
            return;
        }

        player.setMapReward(rewardStage);

        //关卡去尾零 奇数给左眼碎片 偶数给右眼碎片
        short goodsId = GoodsDefine.ITEM_ID_LEFT_EYE;
        if (rewardStage / 10 % 2 == 0) {
            goodsId = GoodsDefine.ITEM_ID_RIGHT_EYE;
        }

        EnumSet<EPlayerSaveType> saves = EnumSet.of(EPlayerSaveType.MAPREWARD);
        gameRole.getPackManager().addGoods(new DropData(EGoodsType.ITEM, goodsId, 1), EGoodsChangeType.MAP_REWARD_ADD, saves);

        if (player.getMapReward() == 400) {
            gameRole.getPackManager().addGoods(new DropData(EGoodsType.ITEM, GoodsDefine.ITEM_ID_DOG, 1), EGoodsChangeType.MAP_REWARD_ADD, saves);
        } else if (player.getMapReward() == 800) {
            gameRole.getPackManager().addGoods(new DropData(EGoodsType.ITEM, GoodsDefine.ITEM_ID_MONKEY, 1), EGoodsChangeType.MAP_REWARD_ADD, saves);
        }

        //发送消息
        Message msg = new Message(MessageCommand.REWARD_FIELD_BOSS_REWARD, request.getChannel());
        msg.setShort(player.getMapReward());
        gameRole.sendMessage(msg);
        //保存数据
        gameRole.savePlayer(saves);
    }

    /**
     * 刷新BOSS战次数
     */
    public void refreshBossCount() {
        long curr = System.currentTimeMillis();
        int count = gameRole.getPlayer().getBossCount();
        if (count >= BossModel.BOSS_FIGHT_MAX) {
            //添加增加BOSS挑战令，可以超上限
            gameRole.getPlayer().setBossRecover(curr);
            return;
        }
        int recover = (int) ((curr - gameRole.getPlayer().getBossRecover()) / BossModel.BOSS_FIGHT_TIME);
        count += recover;
        if (count >= BossModel.BOSS_FIGHT_MAX) {
            gameRole.getPlayer().setBossCount(BossModel.BOSS_FIGHT_MAX);
            gameRole.getPlayer().setBossRecover(curr);
        } else {
            gameRole.getPlayer().setBossCount(count);
            gameRole.getPlayer().setBossRecover(gameRole.getPlayer().getBossRecover() + recover * BossModel.BOSS_FIGHT_TIME);
        }
        return;
    }

    /**
     * BOSS列表
     *
     * @param request
     */
    public void processBossList(Message request) {
        refreshBossCount();
        Message msg = new Message(MessageCommand.BOSS_LIST_MESSAGE, request.getChannel());
        short count = player.getBossCount();
        msg.setShort(count);
        if (count >= BossModel.BOSS_FIGHT_MAX)
            msg.setInt(-1);
        else {
            long curr = System.currentTimeMillis();
            msg.setInt((int) ((gameRole.getPlayer().getBossRecover() + BossModel.BOSS_FIGHT_TIME - curr) / 1000));
        }
        msg.setShort(BossService.getBossMap().size());
        for (Boss boss : BossService.getBossMap().values()) {
            boss.getBossMsg(msg, player.getId());
        }
        gameRole.sendMessage(msg);
    }

    /**
     * BOSS排行榜
     *
     * @param request
     */
    public void processBossTop(Message request) {
        int uuid = request.readInt();
        Boss boss = BossService.getBossMap().get(uuid);
        if (boss == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_BOSS_DISAPPEAR);
            return;
        }
        Message msg = new Message(MessageCommand.BOSS_TOP_MESSAGE, request.getChannel());
        BossBattlePlayer bbp = boss.getBattlePlayer(player.getId());
        if (bbp == null)
            msg.setInt(0);
        else
            msg.setInt((int) bbp.getDamage());
        msg.setShort(boss.getRanks().size());
        for (BossBattlePlayer player : boss.getRanks().values()) {
            player.getSimpleMessage(msg);
            msg.setInt((int) bbp.getDamage());
        }
        gameRole.sendMessage(msg);
    }

    /**
     * BOSS战斗
     *
     * @param request
     */
    @SuppressWarnings("unused")
    public void processBossBatStart(Message request) {
        int uuid = request.readInt();
        short random0 = request.readShort();
        byte random1 = request.readByte();
        byte random2 = request.readByte();
        byte relive = request.readByte();
        Boss boss = BossService.getBossMap().get(uuid);
        if (boss == null || boss.getDeadTime() > 0) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_BOSS_DEAD);
            return;
        }
        refreshBossCount();
        //攻击次数不足
        if (player.getBossCount() <= 0) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_BOSS_FIGHT_MAX);
            return;
        }
        //花钱复活
        if (relive == 1) {
            BossBattlePlayer bbp = boss.getBattlePlayer(player.getId());
            if (bbp == null) {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
                return;
            }
            //花钱复活BOSS战的消耗
            int cost = (bbp.getRelive() + 1) * 5;
            if (!gameRole.getPackManager().useGoldAndDiamond(cost, EGoodsChangeType.BOSS_RELIVE_CONSUME)) {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
                return;
            }
            bbp.addRelive();
            bbp.setLastTime(bbp.getLastTime() - BossModel.ATK_CD_TIME);
        }
        Message msg = new Message(MessageCommand.BOSS_BAT_START_MESSAGE, request.getChannel());
        msg.setByte(1);
        gameRole.sendMessage(msg);
    }

    /**
     * BOSS战结束
     *
     * @param request
     */
    public void processBossBatResult(Message request) {
        int uuid = request.readInt();
        int damage = request.readInt();
        Boss boss = BossService.getBossMap().get(uuid);
        Message msg = new Message(MessageCommand.BOSS_BAT_RESULT_MESSAGE, request.getChannel());
        if (boss != null && boss.getDeadTime() == 0) {
            //攻击BOSS
            short result = BossService.attackBoss(player, uuid, damage);
            if (result == 1) {
                msg.setByte(1);
                msg.setInt(boss.getUuid());
                msg.setLong(boss.getHp());
                if (boss.getKiller() != null) {
                    msg.setByte(1);
                    boss.getKiller().getSimpleMessage(msg);
                } else
                    msg.setByte(0);
                EnumSet<EPlayerSaveType> saves = EnumSet.of(EPlayerSaveType.BOSSCOUNT, EPlayerSaveType.BOSSRECOVER);
                BossRewardsData reward = BossModel.getReward(boss.getId());
                if (reward != null) {
                    gameRole.getPackManager().addGoods(reward.getWinnormalReward(),
                            EGoodsChangeType.BOSS_ATK_ADD, saves);
                }
                gameRole.sendMessage(msg);
                //保存数据
                gameRole.savePlayer(saves);
                return;
            }
        }
        msg.setByte(1);
        msg.setInt(uuid);
        msg.setInt(0);
        if (boss != null && boss.getKiller() != null) {
            msg.setByte(1);
            boss.getKiller().getSimpleMessage(msg);
        } else
            msg.setByte(0);
        gameRole.sendMessage(msg);
    }

    /**
     * 请求排行榜数据
     *
     * @param request
     */
    public void processGameRankList(Message request) {
        byte type = request.readByte();
        ERankType rankType = ERankType.getType(type);
        if (rankType == null)
            return;
        Message message = GameRankManager.getInstance().getGameRankMsg(gameRole, rankType);
        message.setChannel(request.getChannel());
        gameRole.sendMessage(message);
    }


    /**
     * 地图关卡排行
     **/
    public static final byte RANK_SIMPLE_TYPE_MAP = 1;
    /**
     * 诛仙台排行
     **/
    public static final byte RANK_SIMPLE_TYPE_DEKARON = 2;
    /**
     * 达标活动排行
     **/
    public static final byte RANK_SIMPLE_TYPE_DABIAO = 3;
    /**
     * 节日达标排行
     **/
    public static final byte RANK_SIMPLE_TYPE_JIERIDABIAO = 4;
    /**
     * 节日充值达标排行
     **/
    public static final byte RANK_SIMPLE_TYPE_JIERIDABIAO_PAY_ = 5;
    /**
     * 封魔塔排行
     **/
    public static final byte RANK_SIMPLE_TYPE_FENGMOTA = 6;
    /**
     * 主宰试炼排行
     **/
    public static final byte RANK_SIMPLE_TYPE_ZHUZAI = 7;

    public void processGameRankSimpleList(Message request) {
        byte type = request.readByte();
        boolean showAll = request.readBoolean();
        Message msg = getGameRankSimpleMsg(type, showAll);
        msg.setChannel(request.getChannel());
        gameRole.sendMessage(msg);
    }

    public void sendGameRankSimpleTopMsg(int type) {
        List<ActivityRank> list = null;
        if (type == RANK_SIMPLE_TYPE_MAP)
            list = GameRankManager.getInstance().getMapStageList();
        else if (type == RANK_SIMPLE_TYPE_DEKARON)
            list = GameRankManager.getInstance().getDekaronList();
        else if (type == RANK_SIMPLE_TYPE_FENGMOTA)
            list = GameRankManager.getInstance().getFengmotaList();
        else if (type == RANK_SIMPLE_TYPE_ZHUZAI)
            list = GameRankManager.getInstance().getZhuzaiList();
        if (list == null)
            return;
        int i = 0;
        //在前三名 更新排行榜
        for (ActivityRank rank : list) {
            if (rank.getId() == gameRole.getPlayerId()) {
                gameRole.putMessageQueue(getGameRankSimpleMsg(type, false));
                break;
            }
            i++;
            if (i >= 3)
                break;
        }
    }

    public Message getGameRankSimpleMsg(int type, boolean showAll) {
        Message msg = new Message(MessageCommand.GAME_RANK_SIMPLE_LIST_MESSAGE);
        try {
            List<ActivityRank> list = null;
            if (type == RANK_SIMPLE_TYPE_MAP)
                list = GameRankManager.getInstance().getMapStageList();
            else if (type == RANK_SIMPLE_TYPE_DEKARON)
                list = GameRankManager.getInstance().getDekaronList();
            else if (type == RANK_SIMPLE_TYPE_FENGMOTA)
                list = GameRankManager.getInstance().getFengmotaList();
            else if (type == RANK_SIMPLE_TYPE_ZHUZAI)
                list = GameRankManager.getInstance().getZhuzaiList();
            msg.setByte(type);
            msg.setBool(showAll);
            int size = showAll ? list.size() : 3;
            if (size > list.size())
                size = list.size();
            if (size > ConstantModel.RANK_CAPACITY)
                size = ConstantModel.RANK_CAPACITY;
            msg.setShort(size);
            int self = 0;
            for (int i = 0; i < size; i++) {
                ActivityRank rank = list.get(i);
                msg.setShort(i + 1);
                msg.setString(rank.getN());
                msg.setByte(rank.getVn());
                msg.setInt(rank.getV2());
                msg.setLong(rank.getM());
                if (rank.getId() == player.getId()) {
                    self = i + 1;
                }
            }
            msg.setShort(self);
        } catch (Exception e) {
            logger.error("获取简易排行榜消息时发生异常", e);
        }
        return msg;
    }

    /**
     * 好友详情
     *
     * @param request
     */
    public void processFriendDetail(Message request) {
//		int fId = request.readInt();
//		IGameRole fRole = GameWorld.getPtr().getGameRole(fId);
//		if (fRole == null) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
//			return;
//		}
//		Player fPlayer = fRole.getPlayer();
//		Message msg = new Message(MessageCommand.GAME_FRIEND_DETAIL_MESSAGE, request.getChannel());
//		msg.setInt(fId);
//		msg.setString(fPlayer.getName());
//		msg.setShort(fPlayer.getRein());
//		msg.setShort(fPlayer.getLevel());
//		msg.setInt(fPlayer.getVip());
//		msg.setByte(fPlayer.getCharacterList().size());
//		fPlayer.updateFighting();
//		for (Character cha : fPlayer.getCharacterList()) {
//			msg.setByte(cha.getOccupation());
//			msg.setLong(cha.getFighting());
//			msg.setShort(cha.getMeridian());
//			msg.setShort(cha.getTongjing());
//			msg.setShort(cha.getYudi());
//			msg.setShort(cha.getZuoyan());
//			msg.setShort(cha.getYouyan());
//			//装备
//			for (Equip equip : cha.getEquipList()) {
//				if (equip==null)
//					msg.setBool(false);
//				else {
//					msg.setBool(true);
//					equip.getMessage(msg);
//				}
//			}
//			//装备槽消息
//			for (EquipSlot slot : cha.getEquipSlotList())
//			{
//				if (slot == null)
//					slot = new EquipSlot();
//				slot.getMessage(msg);
//			}
//			//主宰
//			msg.setByte(cha.getDomList().size());
//			for (Dominate dom : cha.getDomList()) {
//				dom.getMsg(msg);
//			}
//		}
//		fPlayer.getAppearMessage(msg);
//		gameRole.sendMessage(msg);
    }

    /**
     * 118领取在线礼包
     *
     * @param request
     */
    public void processReceiveOnlineGift(Message request) {
//		OnlineGiftData data=FunctionModel.getOnlineGiftData(player.getSmallData().getOnlineGift());
//		if(data!=null){
//			if(player.getOnlineGiftTime()>data.getTime()){
//				player.getSmallData().setOnlineGift((byte)(player.getSmallData().getOnlineGift()+1));
//				player.getSmallData().setOnlineGiftTime(System.currentTimeMillis());
//				EnumSet<EPlayerSaveType> enumSet=EnumSet.of(EPlayerSaveType.SMALLDATA);
//				gameRole.getPackManager().addGoods(data.getRewardData(), EGoodsChangeType.ONLINE_GIFT_ADD, enumSet);
//				
//				Message message=new Message(MessageCommand.RECEIVE_ONLINE_GIFT,request.getChannel());
//				message.setByte(player.getSmallData().getOnlineGift());
//				message.setInt(0);
//				gameRole.sendMessage(message);
//				gameRole.savePlayer(enumSet);
//			}else{
//				gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
//			}
//		}else{
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
//		}
    }

    /**
     * 冲榜奖励信息
     *
     * @param request
     */
    public void processRankRewardInfo(Message request) {
        long curr = System.currentTimeMillis();
        int day = DateUtil.getDistanceDay(GameDefine.SERVER_CREATE_TIME, curr);
        Message msg = new Message(MessageCommand.RANK_REWARD_INFO_MESSAGE, request.getChannel());
        msg.setInt(day);
        gameRole.sendMessage(msg);
    }

    /**
     * 转生
     *
     * @param request
     */
    public void processReinUpgrade(Message request) {
        //等级判断
        if (player.getLevel() < GameDefine.REIN_LV) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_LEVEL_LESS);
            return;
        }
        //下一转
        ReinModelData model = ReinModel.getData(player.getRein() + 1);
        if (model == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_REIN_MAX);
            return;
        }
        EnumSet<EPlayerSaveType> saves = EnumSet.of(EPlayerSaveType.REIN);
        //消耗
        if (!gameRole.getPackManager().useGoods(model.getCost(), EGoodsChangeType.ZHUANSHENG_CONSUME, saves)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_XIUWEI_LESS);
            return;
        }
        player.addRein();
        //发送转生消息
        gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.REIN_REACH_LEVEL, player.getLevelWithRein(), saves));
        //消息
        Message msg = new Message(MessageCommand.REIN_UPGRADE_MESSAGE, request.getChannel());
        msg.setShort(player.getRein());
        gameRole.sendMessage(msg);
        //保存数据
        gameRole.savePlayer(saves);
    }

    public void processReinExchange(Message request) {
        //等级兑换
        EnumSet<EPlayerSaveType> saves = EnumSet.of(EPlayerSaveType.DAYDATA);
        int xiuwei = 0;
        //等级不足
        if (player.getLevel() <= GameDefine.REIN_LV) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_LEVEL_LESS);
            return;
        }
        //次数不足
        if (player.getDayData().getReinEx() >= GameDefine.REIN_EX) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_TIMES_LESS);
            return;
        }
        RoleData expData = RoleModel.getRoleData(player.getLevel() - 1);
        RoleData xiuweiData = RoleModel.getRoleData(player.getLevel());
        if (expData == null || xiuweiData == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        //经验够扣除，不扣除等级
        if (player.getExp() >= expData.getExp()) {
            player.setExp(player.getExp() - expData.getExp());
            saves.add(EPlayerSaveType.EXP);
        }
        //扣除等级
        else {
            player.setLevel((short) (player.getLevel() - 1));
            saves.add(EPlayerSaveType.LEVEL);
        }
        //修为
        xiuwei = 0;//xiuweiData.getXiuwei();
        player.getDayData().addReinEx();
        //获得修为
        DropData add = new DropData(EGoodsType.YUANQI, 0, xiuwei);
        gameRole.getPackManager().addGoods(add, EGoodsChangeType.REIN_EXCHANGE_ADD, saves);
        //消息
        gameRole.sendUpdateExpMsg(true);
        Message msg = new Message(MessageCommand.REIN_EXCHANGE_MESSAGE, request.getChannel());
        msg.setByte(GameDefine.REIN_EX - player.getDayData().getReinEx());
        msg.setInt(xiuwei);
        gameRole.sendMessage(msg);
        //保存数据
        gameRole.savePlayer(saves);
    }

    public void processDomLvUp(Message request) {
//		byte idx = request.readByte();
//		byte pos = request.readByte();
//		Character cha = player.getCharacter(idx);
//		if (cha == null) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
//			return;
//		}
//		if (pos >= cha.getDomList().size()) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
//			return;
//		}
//		Dominate dom = cha.getDomList().get(pos);
//		DomLvData model = DomModel.getLvData(pos, dom.getL()+1);
//		if (model == null) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
//			return;
//		}
//		if (!player.lvValidate(model.getLv())) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_REIN_LESS);
//			return;
//		}
//		EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
//		//消耗
//		if (model.getCost().getN() > 0) {			
//			if (!gameRole.getPackManager().useGoods(model.getCost(), EGoodsChangeType.DOM_LVUP_CONSUME, saves)) {
//				gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//				return;
//			}
//		}
//		//随机数
//		byte succ = 0;
//		int random = GameUtil.getRangedRandom(1, 10000);
//		//成功
//		if (random <= model.getRate()) {
//			dom.addL();
//			saves.add(EPlayerSaveType.CHA_DOM);
//			succ = 1;
//		}
//		//gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.DOM_LEVEL_UP, 1,saves));
//		//消息
//		Message msg = new Message(MessageCommand.DOM_LVUP_MESSAGE, request.getChannel());
//		msg.setByte(idx);
//		msg.setByte(pos);
//		msg.setShort(dom.getL());
//		msg.setByte(succ);
//		gameRole.sendMessage(msg);
//		//保存数据
//		gameRole.saveData(idx, saves);
    }

    public void processDomRankUp(Message request) {
//		byte idx = request.readByte();
//		byte pos = request.readByte();
//		Character cha = player.getCharacter(idx);
//		if (cha == null) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
//			return;
//		}
//		if (pos >= cha.getDomList().size()) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
//			return;
//		}
//		Dominate dom = cha.getDomList().get(pos);
//		if (dom.getR() >= 5) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_MAX_LEVEL);
//			return;
//		}
//		DomRankData model = DomModel.getRankData(pos, dom.getR());
//		EnumSet<EPlayerSaveType> saves = EnumSet.of(EPlayerSaveType.CHA_DOM);
//		//消耗
//		if (!gameRole.getPackManager().useGoods(model.getCost(), EGoodsChangeType.DOM_RANKUP_CONSUME, saves)) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//			return;
//		}
//		dom.addR();
//		//gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.DOM_STAGE_UP, 1,saves));
//		//消息
//		Message msg = new Message(MessageCommand.DOM_RANKUP_MESSAGE, request.getChannel());
//		msg.setByte(idx);
//		msg.setByte(pos);
//		msg.setByte(dom.getR());
//		gameRole.sendMessage(msg);
//		//保存数据
//		gameRole.saveData(idx, saves);
    }

    public void processDomPiece(Message request) {
        short size = request.readShort();
        if (size == 0) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        int total = 0;
        List<DropData> cost = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            short id = request.readShort();
            int num = request.readInt();
            total += num * 5;
            cost.add(new DropData(EGoodsType.ITEM, id, num));
        }
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        //扣除消耗
        if (!gameRole.getPackManager().useGoods(cost, EGoodsChangeType.DOM_PIECE_CONSUME, saves)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }
        //增加
        DropData reward = new DropData(EGoodsType.ITEM, 7, total);
        gameRole.getPackManager().addGoods(reward, EGoodsChangeType.DOM_PIECE_ADD, saves);
        Message msg = new Message(MessageCommand.DOM_PIECE_MESSAGE, request.getChannel());
        msg.setInt(total);
        gameRole.sendMessage(msg);
        //保存数据
        gameRole.savePlayer(saves);
    }

    /**
     * 201 角色解锁
     *
     * @param request
     */
    public void processCharacterUnlock(Message request) {
//		byte occupation=request.readByte();
//		
//		List<Character> characterList = player.getCharacterList();
//		if (characterList.size() >= GameDefine.OCCUPATION_NUM){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		int nextIndex = characterList.size();
//		if(1==nextIndex){
//			if(player.getLevel()<80 && player.getVipLevel()<2){
//				gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_LEVEL_LESS);
//				return;
//			}
//		}
//		if(2==nextIndex){
//			if(player.getRein()<4 && player.getVipLevel()<4){
//				gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_REIN_LESS);
//				return;
//			}
//		}
//		for(Character ch:characterList){
//			if(ch.getOccupation()==occupation){
//				gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//				return;
//			}
//		}
//
//		Character character=player.createCharacter((byte) nextIndex,occupation);
//		if(gameRole.getDbManager().playerDao.createCharacter(character)==GameDefine.INVALID){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		characterList.add(character);
//		
//		Message message = new Message(MessageCommand.PLAYER_CHARACTER_UNLOCK, request.getChannel());
//		message.setByte(nextIndex);
//		character.getMessage(message);
//		gameRole.sendMessage(message);
    }

    public void processModifyHeadIcon(Message request) {
        byte head = request.readByte();
        player.setHead(head);
        Message msg = new Message(MessageCommand.MODIFY_HEAD_ICON_MESSAGE, request.getChannel());
        msg.setByte(head);
        gameRole.sendMessage(msg);
        //保存数据
        EnumSet<EPlayerSaveType> saves = EnumSet.of(EPlayerSaveType.HEAD);
        gameRole.savePlayer(saves);
    }

    /**
     * 921 分享信息
     *
     * @param request
     */
    public void processShareInfo(Message request) {
        Message message = getShareInfoMessage();
        message.setChannel(request.getChannel());
        gameRole.sendMessage(message);
    }

    public Message getShareInfoMessage() {
        long currentTime = System.currentTimeMillis();
        checkShareList(currentTime);
        ShareData shareData = player.getShareData();
        int cd = 0;
        if (!shareData.getShareList().isEmpty()) {
            Long lastShareTime = shareData.getShareList().get(shareData.getShareList().size() - 1);
            long passTime = (currentTime - lastShareTime) / TimeUnit.SECONDS.toMillis(1);
            cd = passTime >= SDKDefine.SHARE_REWARD_INTERVAL ? 0 : (int) (SDKDefine.SHARE_REWARD_INTERVAL - passTime);
        }

        Message message = new Message(MessageCommand.SHARE_INFO_MESSAGE);
        message.setByte(shareData.getShareList().size());
        message.setByte(shareData.getShareNum());
        if (shareData.getReceiveSet().contains((byte) 1)) {
            message.setByte(1);
        } else {
            message.setByte(0);
        }
        if (shareData.getReceiveSet().contains((byte) 2)) {
            message.setByte(1);
        } else {
            message.setByte(0);
        }
        message.setInt(cd);
        return message;
    }

    private void checkShareList(long currentTime) {
        long currentDay = DateUtil.getDayStartTime(currentTime);
        ShareData shareData = player.getShareData();
        for (int i = shareData.getShareList().size() - 1; i >= 0; --i) {
            if (shareData.getShareList().get(i) < currentDay) {
                shareData.getShareList().remove(i);
            }
        }
    }

    /**
     * 922 分享完成
     *
     * @param request
     */
    public void processShareComplete(Message request) {
        long currentTime = System.currentTimeMillis();
        checkShareList(currentTime);
        if (checkShareReward(currentTime)) {
            player.getShareData().getShareList().add(currentTime);
            if (player.getShareData().getShareNum() < 10) {
                player.getShareData().setShareNum((byte) (player.getShareData().getShareNum() + 1));

                Mail mail = MailService.createMail(
                        "邀请奖励",
                        "邀请成功，请领取",
                        EGoodsChangeType.SHARE_ADD,
                        new DropData(EGoodsType.DIAMOND.getId(), 0, 200));
                gameRole.getDbManager().mailDao.insertMail(player.getId(), mail);
                gameRole.getMailManager().addMailAndNotify(mail);

                EnumSet<EPlayerSaveType> enumSet = EnumSet.of(EPlayerSaveType.SHARE);
                gameRole.savePlayer(enumSet);
            }

            EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
            gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.CARRY_ON_SHARE, 1, enumSet));
            gameRole.savePlayer(enumSet);
        }
        processShareInfo(request);
    }

    private boolean checkShareReward(long currentTime) {
        List<Long> shareTimeList = player.getShareData().getShareList();
        if (shareTimeList.isEmpty()) {
            return true;
        }
        // 次数
        if (shareTimeList.size() >= SDKDefine.SHARE_DAILY_REWARD_TIMES) {
            return false;
        }
        // cd
        Long lastShareTime = shareTimeList.get(shareTimeList.size() - 1);
        long passTime = (currentTime - lastShareTime) / TimeUnit.SECONDS.toMillis(1);
        return passTime >= SDKDefine.SHARE_REWARD_INTERVAL;
    }

    /**
     * 923 分享奖励
     *
     * @param request
     */
    public void processShareReward(Message request) {
        byte id = request.readByte();
        ShareModelData data = FunctionModel.getShareModelData(id);
        if (data != null) {
            if (player.getShareData().getShareNum() >= data.getTimes() && !player.getShareData().getReceiveSet().contains(id)) {
                player.getShareData().getReceiveSet().add(id);

                EnumSet<EPlayerSaveType> enumSet = EnumSet.of(EPlayerSaveType.SHARE);
                gameRole.getPackManager().addGoods(data.getRewardList(), EGoodsChangeType.SHARE_ADD, enumSet);
                gameRole.savePlayer(enumSet);

                processShareInfo(request);
            } else {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            }
        } else {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
        }
    }

    /**
     * 371 红装碎片兑换
     *
     * @param request
     */
    public void processRedExchange(Message request) {
        //V4以上才可以兑换 500橙装碎片=>10红装碎片
        if (player.getVipLevel() > 4) {
            EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
            if (gameRole.getPackManager().useGoods(new DropData(EGoodsType.ITEM, GoodsDefine.ITEM_ID_ORANGE_PIECES, 500), EGoodsChangeType.RED_EXCHANGE_CONSUME, enumSet)) {
                gameRole.getPackManager().addGoods(new DropData(EGoodsType.ITEM, GoodsDefine.ITEM_ID_RED_PIECES, 10), EGoodsChangeType.RED_EXCHANGE_ADD, enumSet);
                gameRole.sendTick(request);
                gameRole.savePlayer(enumSet);
            } else {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            }
        } else {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_VIP_LEVEL_LESS);
        }
    }


    private long lastSearchPlayerTime = -1;

    /**
     * 查询玩家信息
     *
     * @param request
     */
    public void processSearchPlayer(Message request) {
        long currentTime = System.currentTimeMillis();
        if (lastSearchPlayerTime != -1 && currentTime - lastSearchPlayerTime < GameDefine.OPERATION_INTERVAL) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_OVER_QUICK);
            return;
        }
        lastSearchPlayerTime = currentTime;

        String name = request.readString();
        // TODO name cache
        List<RelatedPlayer> playerList = gameRole.getDbManager().relationshipDao.getRelatedPlayer(name);
        Message message = new Message(MessageCommand.GAME_SEARCH_PLAYER_MESSAGE, request.getChannel());
        message.setByte(playerList.size());
        for (RelatedPlayer player : playerList) {
            player.getMessage(message, currentTime);
        }
        gameRole.sendMessage(message);
    }

    /**
     * 龙珠升级
     *
     * @param request
     */
    public void processDragonBallLvup(Message request) {
        DragonBall dragonball = player.getDragonBall();
        short next = (short) (dragonball.getLevel() + 1);
        DragonBallModelData modelData = DragonBallModel.getData(next);
        if (modelData == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        if (!gameRole.getPackManager().useGoods(modelData.getConsume(), EGoodsChangeType.DRAGONBALL_LVUP_CONSUME, enumSet)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DRAGONBALL_PIECE_LESS);
            return;
        }
        dragonball.setLevel(next);
        GameEvent event = new GameEvent(EGameEventType.DRAGON_BALL_LEVEL_UP, next, enumSet);
        gameRole.getEventManager().notifyEvent(event);

        gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.DRAGON_BALL_LEVEL_UP, next, enumSet));
        gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.DRAGON_BALL_REACH_LEVEL, next, enumSet));

        enumSet.add(EPlayerSaveType.DRAGON_BALL);
        gameRole.savePlayer(enumSet);
        Message message = new Message(MessageCommand.DRAGON_BALL_LEVELUP, request.getChannel());
        message.setShort(next);
        gameRole.sendMessage(message);
    }

    /**
     * 处理月卡用户龙珠碎片领取
     *
     * @param request
     */
    public void processDragonBallPiecesReceive(Message request) {
        if (!gameRole.getActivityManager().isMonthlyCard()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        DragonBall dragonball = player.getDragonBall();
        if (dragonball.getMothCardAddition() <= 0) {
            return;
        }
        dragonball.setMothCardAddition(0);
        DropData reward = new DropData(EGoodsType.DRAGONBALL_PIECE, 0, dragonball.getMothCardAddition());
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        gameRole.getPackManager().addGoods(reward, EGoodsChangeType.DRAGONBALL_MONTHCARD_ADD, enumSet);

        enumSet.add(EPlayerSaveType.DRAGON_BALL);
        gameRole.savePlayer(enumSet);
        Message message = new Message(MessageCommand.DRAGON_BALL_RECEIVE_PIECE, request.getChannel());
        gameRole.sendMessage(message);
    }

    /**
     * 勋章升级
     *
     * @param request
     */
    public void processMedalLvup(Message request) {
        byte current = player.getMedal();
        byte next = (byte) (current + 1);
        MedalModelData modelData = MedalModel.getData(next);
        if (modelData == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        if (modelData.getLevelLimit() > player.getLevelWithRein()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_LEVEL_LESS);
            return;
        }
        if (modelData.getAchievementLimit() > player.getAchievement()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_ACHIEVEMENT_LESS);
            return;
        }

        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        if (!gameRole.getPackManager().useGoods(modelData.getConsume(), EGoodsChangeType.MEDAL_LVUP_CONSUME, enumSet)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOLD_LESS);
            return;
        }
        gameRole.getPlayer().setMedal(next);
        GameEvent event = new GameEvent(EGameEventType.MEDAL_REACH_LEVEL, next, enumSet);
        gameRole.getEventManager().notifyEvent(event);

        enumSet.add(EPlayerSaveType.MEDAL);
        gameRole.savePlayer(enumSet);
        Message message = new Message(MessageCommand.MEDAL_LEVEL_UP_MESSAGE, request.getChannel());
        message.setByte(next);
        gameRole.sendMessage(message);
    }

    /**
     * 排行榜膜拜
     *
     * @param request
     */
    public void processRankWorship(Message request) {
        byte tab = request.readByte();

        if (tab < 0 || tab > 6) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        Boolean state = player.getDayData().getTopRankWorship().get(tab);
        if (state != null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        player.getDayData().getTopRankWorship().put(tab, true);

        int num = 8000 * ((int) ((player.getLevel() - 60) / 10) + 1) + player.getRein() * 10000;

        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        gameRole.getPackManager().addGoods(new DropData(EGoodsType.GOLD, 0, num), EGoodsChangeType.RANK_WORSHIP_ADD, enumSet);
        gameRole.getPackManager().addGoods(new DropData(EGoodsType.EXP, 0, num), EGoodsChangeType.RANK_WORSHIP_ADD, enumSet);
        enumSet.add(EPlayerSaveType.DAYDATA);
        gameRole.savePlayer(enumSet);

        Message message = new Message(MessageCommand.TOPRANK_WORSHIP_MESSAGE, request.getChannel());
        message.setByte(tab);
        gameRole.sendMessage(message);
    }

    /**
     * 排行榜膜拜列表
     *
     * @param
     */
    public Message getRankWorshipList() {
        Message message = new Message(MessageCommand.TOPRANK_WORSHIP_LIST_MESSAGE);
        int size = player.getDayData().getTopRankWorship().size();
        message.setByte(size);
        for (Byte key : player.getDayData().getTopRankWorship().keySet()) {
            message.setByte(key);
        }
        return message;
    }

    /**
     * 神器碎片激活
     *
     * @param request
     */
    public void processArtifactPieceInvoke(Message request) {
        short id = request.readShort();
        ArtifactBoss artifactBoss = gameRole.getPlayer().getArtifactBoss();
        ArtifactBossModelData modelData = ArtifactModel.getArtifactData(artifactBoss.getId());
        Map<Short, Byte> target = modelData.getPieces();
        if (!target.containsKey(id)) {
            // 错误数据
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        Map<Short, Byte> current = artifactBoss.getPieces();
        byte currentNum = current.containsKey(id) ? current.get(id) : 0;
        if (currentNum >= target.get(id)) {
            // 完成目标数量
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        // 消耗
        DropData dropData = new DropData(EGoodsType.ARTIFACT_PIECES, id, 1);
        if (!gameRole.getPackManager().useGoods(dropData, EGoodsChangeType.ARTIFACT_PIECE_INVOKE, enumSet)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }
        // 激活
        currentNum++;
        current.put(id, currentNum);

        gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.ARTIFACT_PIECE_INVOKE, 1, enumSet));
        if (id == 1) {
            gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.ACTIVE_ARTIFACT_FRAGMENTS_1, 1, enumSet));
        } else if (id == 2) {
            gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.ACTIVE_ARTIFACT_FRAGMENTS_2, 1, enumSet));
        } else if (id == 3) {
            gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.ACTIVE_ARTIFACT_FRAGMENTS_3, 1, enumSet));
        } else if (id == 4) {
            gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.ACTIVE_ARTIFACT_FRAGMENTS_4, 1, enumSet));
        } else if (id == 5) {
            gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.ACTIVE_ARTIFACT_FRAGMENTS_5, 1, enumSet));
        } else if (id == 6) {
            gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.ACTIVE_ARTIFACT_FRAGMENTS_6, 1, enumSet));
        }


        enumSet.add(EPlayerSaveType.ARTIFACT_BOSS);
        gameRole.savePlayer(enumSet);

        gameRole.sendMessage(getArtifactBossMessage(request.getChannel()));
    }

    /**
     * 神器激活
     *
     * @param request
     */
    public void processArtifactInvoke(Message request) {
        ArtifactBoss artifactBoss = gameRole.getPlayer().getArtifactBoss();
        ArtifactBossModelData modelData = ArtifactModel.getArtifactData(artifactBoss.getId());
        Map<Short, Byte> target = modelData.getPieces();
        Map<Short, Byte> current = artifactBoss.getPieces();

        for (Map.Entry<Short, Byte> entry : target.entrySet()) {
            if (!current.containsKey(entry.getKey())
                    || current.get(entry.getKey()) < entry.getValue()) {
                // 没集齐
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
                return;
            }
        }

        // 更替记录
        ArtifactBoss nextData = new ArtifactBoss((byte) (artifactBoss.getId() + 1));
        gameRole.getPlayer().setArtifactBoss(nextData);

        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.ARTIFACT_BOSS_INVOKE, 1, enumSet));
        if (nextData.getId() - 1 == 1) {
            gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.ACTIVE_ARTIFACT_1, nextData.getId(), enumSet));
        } else if (nextData.getId() - 1 == 2) {
            gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.ACTIVE_ARTIFACT_2, nextData.getId(), enumSet));
        }
        enumSet.add(EPlayerSaveType.ARTIFACT_BOSS);
        gameRole.savePlayer(enumSet);

        gameRole.sendMessage(getArtifactBossMessage(request.getChannel()));
    }

    private Message getArtifactBossMessage(Channel channel) {
        ArtifactBoss artifactBoss = gameRole.getPlayer().getArtifactBoss();
        Message message = new Message(MessageCommand.ARTIFACT_BOSS_UPDATE_MESSAGE, channel);
        artifactBoss.getMessage(message);
        return message;
    }

    /**
     * 913五行激活信息
     **/
    public void processFiveElementsActivate(Message request) {
        Message message = new Message(MessageCommand.FIVE_ELEMENTS_ACTIVATE_MESSAGE, request.getChannel());
        for (byte s : player.getFiveElements().getElements()) {
            message.setByte(s);
        }
        gameRole.sendMessage(message);
    }

    /**
     * 910五行激活
     *
     * @param request
     */
    public void processFiveElementsActive(Message request) {
        byte id = request.readByte();
        if (player.getFiveElements().getElements()[id - 1] > 0) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        FiveElementsData fiveData = FunctionModel.getFiveElementsData(id);
        int day = DateUtil.getDistanceDay(GameDefine.SERVER_CREATE_TIME, System.currentTimeMillis()) + 1;
        if (day < fiveData.getDay() && player.getVipLevel() < fiveData.getVip()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_CONDITION_NOT_ENOUGHT);
            return;
        }
        player.getFiveElements().getElements()[id - 1] = 1;

        Message message = new Message(MessageCommand.FIVE_ELEMENTS_ACTIVE_MESSAGE, request.getChannel());
        message.setByte(id);
        gameRole.sendMessage(message);

        EnumSet<EPlayerSaveType> saves = EnumSet.of(EPlayerSaveType.FIVEELEMENT);
        gameRole.savePlayer(saves);
    }

    /**
     * 916五行融合
     *
     * @param request
     */
    public void processFiveElementsFuse(Message request) {
        for (byte s : player.getFiveElements().getElements()) {
            if (s == 0) {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
                return;
            }
        }
        if (player.getFiveElements().getFuse() == 1) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        DropData cost = new DropData(EGoodsType.DIAMOND, 0, ConstantModel.FIVE_ELEMENT_FUSE_COST);
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        if (!gameRole.getPackManager().useGoods(cost, EGoodsChangeType.FIVE_ELEMENT_FUSE_COST, saves)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
            return;
        }
        player.getFiveElements().setFuse((byte) 1);
        player.getSmallData().setFiveFuseTime(System.currentTimeMillis());

        Mail mail = MailService.createMail("五行送礼", "特此献上超值丰厚大礼，请注意领取", EGoodsChangeType.FIVE_ELEMENT_GIFT, new DropData(EGoodsType.BOX.getId(), 153, 1));
        gameRole.getMailManager().addMailAndNotify(mail);

        Message message = new Message(MessageCommand.FIVE_ELEMENTS_FUSE_MESSAGE, request.getChannel());
        gameRole.putMessageQueue(gameRole.getActivityManager().getActivityMsg(EActivityType.FIVE_ELEMENTS));
        gameRole.sendMessage(message);

        saves.add(EPlayerSaveType.SMALLDATA);
        saves.add(EPlayerSaveType.FIVEELEMENT);
        gameRole.savePlayer(saves);
    }

    /**
     * 920五行活动是否完成
     *
     * @param request
     */
    public void processFiveFuseSuccess(Message request) {
        Message message = new Message(MessageCommand.FIVE_ELEMENTS_ISFUSE_MESSAGE, request.getChannel());
        message.setByte(player.getFiveElements().getFuse());
        gameRole.sendMessage(message);
    }

    /**
     * 911五行玩法列表
     *
     * @param request
     */
    public void processFiveElementsList(Message request) {
        this.checkFiveElementsUpReplyTime();
        Message message = new Message(MessageCommand.FIVE_ELEMENTS_LIST_MESSAGE, request.getChannel());
        message.setByte(player.getSmallData().getFiveElementUpNum());
        message.setLong(player.getSmallData().getFiveElementUpTime());
        message.setShort(player.getFiveElements().getFiveLevel());
        gameRole.sendMessage(message);
    }

    /**
     * 912五行玩法升级
     *
     * @param request
     */
    public void processFiveElementsUpgrade(Message request) {
//		this.checkFiveElementsUpReplyTime();
        byte levelUpNum = player.getSmallData().getFiveElementUpNum();
        if (levelUpNum <= 0) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_TIMES_LESS);
            return;
        }
        if (player.getFiveElements().getFiveLevel() >= ConstantModel.FIVE_ELEMENT_MAX_LEVEL) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_MAX_LEVEL);
            return;
        }
        Short lv = player.getFiveElements().getFiveLevel();
        double n;
        if (lv < 20)
            n = 1;
        else if (lv >= 20 && lv < 100)
            n = 2;
        else if (lv >= 100 && lv < 120)
            n = 2.5;
        else if (lv >= 120 && lv < 150)
            n = 4;
        else if (lv >= 150 && lv < 200)
            n = 4.5;
        else if (lv >= 200 && lv < 220)
            n = 9;
        else if (lv >= 220 && lv < 275)
            n = 10;
        else
            n = 15;
        int goldCost;
        if (lv == 0) {
            goldCost = 10000;
        } else {
            goldCost = (int) (n * ((160 * (lv * lv) + 11700 * lv) / 10000) * 10000);
        }
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        DropData cost = new DropData(EGoodsType.GOLD, 0, goldCost);
        if (!gameRole.getPackManager().useGoods(cost, EGoodsChangeType.FIVE_ELEMENT_CONSUME, saves)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOLD_LESS);
            return;
        }
        player.getFiveElements().setFiveLevel((short) (lv + 1));
        player.getSmallData().setFiveElementUpNum((byte) (levelUpNum - 1));
//      player.getSmallData().setFiveRestTime(System.currentTimeMillis());
        this.checkFiveElementsUpReplyTime();

        Message message = new Message(MessageCommand.FIVE_ELEMENTS_UPGRADE_MESSAGE, request.getChannel());
        message.setByte(player.getSmallData().getFiveElementUpNum());
        message.setLong(player.getSmallData().getFiveElementUpTime());
        message.setShort(player.getFiveElements().getFiveLevel());
        gameRole.sendMessage(message);

        saves.add(EPlayerSaveType.SMALLDATA);
        saves.add(EPlayerSaveType.FIVEELEMENT);
        gameRole.savePlayer(saves);
    }

    /**
     * 913灵阵信息
     *
     * @param request
     */
    public void processLingzhenInfo(Message request) {
        Message message = new Message(MessageCommand.LINGZHEN_LIST_MESSAGE, request.getChannel());
        message.setShort(player.getFiveElements().getMatrixLevel());
        gameRole.sendMessage(message);
    }

    /**
     * 914灵阵升级
     *
     * @param request
     */
    public void processLingzhenLvUp(Message request) {
        int lv = player.getFiveElements().getMatrixLevel() + 1;
        if (lv > ConstantModel.LINGZHEN_MAX_LEVEL) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_MAX_LEVEL);
            return;
        }

        int cost;
        if (lv == 0) {
            cost = 100;
        } else if (lv == 1) {
            cost = 150;
        } else if (lv == 2) {
            cost = 180;
        } else if (lv == 3) {
            cost = 200;
        } else {
            cost = (int) ((0.3D * lv * lv + 30 * lv + 1000) / 5);
        }

        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        DropData cost1 = new DropData(EGoodsType.ITEM, 74, cost);//74进阶丹
        if (!gameRole.getPackManager().useGoods(cost1, EGoodsChangeType.LINGZHEN_LVUP_CONSUME, saves)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }
        player.getFiveElements().setMatrixLevel((short) lv);

        Message message = new Message(MessageCommand.LINGZHEN_UPGRADE_MESSAGE, request.getChannel());
        message.setShort(player.getFiveElements().getMatrixLevel());
        gameRole.sendMessage(message);

        saves.add(EPlayerSaveType.FIVEELEMENT);
        gameRole.savePlayer(saves);
    }

    /**
     * 917五行副本信息
     **/
    public void processFiveElementsCopy(Message request) {
        Message message = new Message(MessageCommand.FIVE_ELEMENTS_COPY_MESSAGE, request.getChannel());
        message.setByte(player.getDayData().getFiveCh());
        message.setByte(player.getSmallData().getFiveState());
        gameRole.sendMessage(message);
    }

    /**
     * 918五行请求挑战副本
     **/
    public void processFiveElementsChallenge(Message request) {
        boolean isEnter = true;
        DungeonData data = DungeonModel.getDungeonData(DungeonDefine.DUNGEON_SUB_TYPE_FIVE);
        if (gameRole.getFightManager().inInstance()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_COPY_ALREADY_IN);
            isEnter = false;
            return;
        } else if (player.getLevelWithRein() < data.getLevelLimit()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_LEVEL_LESS);
            isEnter = false;
            return;
        } else if (player.getVipLevel() < data.getVipLimit()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_VIP_LEVEL_LESS);
            isEnter = false;
            return;
        } else if (player.getDayData().getFiveCh() <= 0) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_TIMES_LESS);
            isEnter = false;
            return;
        }

        Message message = new Message(MessageCommand.FIVE_ELEMENTS_COPY_CHALLENGE_MESSAGE, request.getChannel());
        message.setByte(DungeonDefine.DUNGEON_SUB_TYPE_FIVE);
        gameRole.sendMessage(message);

        if (isEnter) {
            player.setMapType(EMapType.DUNGEON);
            this.fightResult = FightDefine.FIGHT_RESULT_SUCCESS;
        }

    }

    /**
     * 9200 五行副本战斗结果
     **/
    public void processFiveFightResult(Message request) {
        //战斗结果 0：失败 1：成功 2：平局
        byte result = request.readByte();
        //客户端如果失败 服务器也算作失败 平局同样
        if (FightDefine.FIGHT_RESULT_FAIL == result || FightDefine.FIGHT_RESULT_TIE == result) {
            this.fightResult = result;
        }
        //如果地图类型不为副本算作失败
        if (EMapType.DUNGEON != player.getMapType()) {
            this.fightResult = FightDefine.FIGHT_RESULT_FAIL;
        }
        //服务器验证失败
        if (FightDefine.FIGHT_RESULT_FAIL == this.fightResult && FightDefine.FIGHT_RESULT_SUCCESS == result) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_SERVER_FIGHT_FAIL);
            return;
        }

        Message message = new Message(MessageCommand.FIVE_ELEMENTS_FIGHT_DUNGEON_RESULT_MESSAGE, request.getChannel());
        message.setByte(this.fightResult);
        if (FightDefine.FIGHT_RESULT_SUCCESS == this.fightResult) {

            EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
            gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.DUNGEON_FIVE_ELEMENT_CLEARANCE, 1, null));

            message.setByte(result);
            player.getDayData().setFiveCh((byte) (player.getDayData().getFiveCh() - 1));
            player.getSmallData().setFiveState((byte) 1);

            enumSet.add(EPlayerSaveType.DAYDATA);
            enumSet.add(EPlayerSaveType.SMALLDATA);
            gameRole.savePlayer(enumSet);

        }
        player.setMapType(EMapType.FIELD_NORMAL);
        gameRole.sendMessage(message);
    }


    /**
     * 919五行副本领奖
     **/
    public void processFiveElementsAward(Message request) {
        byte id = request.readByte();
        if (player.getSmallData().getFiveState() == 0) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        FiveElementsDungeonData fiveDungeonData = FunctionModel.getFiveElementsDungeonData(id);
        //扣元宝
        DropData cost = new DropData(EGoodsType.DIAMOND, 0, fiveDungeonData.getCost());
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        if (id != 1) {
            if (!gameRole.getPackManager().useGoods(cost, EGoodsChangeType.FIVE_ELEMENT_REWARD_CONSUME, saves)) {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
                return;
            }
        }

        gameRole.getPackManager().addGoods(fiveDungeonData.getReward(), EGoodsChangeType.FIVE_ELEMENT_RECEIVE, saves);
        player.getSmallData().setFiveState((byte) 0);

        Message message = new Message(MessageCommand.FIVE_ELEMENTS_THE_AWARD_MESSAGE, request.getChannel());
        gameRole.sendMessage(message);

        saves.add(EPlayerSaveType.DAYDATA);
        saves.add(EPlayerSaveType.SMALLDATA);
        gameRole.savePlayer(saves);
    }

    /**
     * 9201五行副本活动挑战信息
     **/
    public void processFiveElementActivity(Message request) {
        Message message = new Message(MessageCommand.FIVE_ELEMENTS_ACTIVITY_MESSAGE, request.getChannel());
        message.setByte(player.getDayData().getFiveCostGoldCh());
        gameRole.sendMessage(message);
    }

    /**
     * 9202五行副本活动请求挑战
     **/
    public void processFiveElementsActivityChallenge(Message request) {
        boolean isEnter = true;
        DungeonData data = DungeonModel.getDungeonData(DungeonDefine.DUNGEON_SUB_TYPE_FIVE);
        if (gameRole.getFightManager().inInstance()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_COPY_ALREADY_IN);
            isEnter = false;
            return;
        } else if (player.getLevelWithRein() < data.getLevelLimit()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_LEVEL_LESS);
            isEnter = false;
            return;
        } else if (player.getVipLevel() < data.getVipLimit()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_VIP_LEVEL_LESS);
            isEnter = false;
            return;
        } else if (player.getDayData().getFiveCostGoldCh() == 0) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_TIMES_LESS);
            isEnter = false;
            return;
        }

        FiveElementsActivityData fiveElementsActivityData = FunctionModel.getFiveElementsActivityData((byte) ((SectionDefine.FIVE_ELEMENT_ACTIVITY_TIMES - player.getDayData().getFiveCostGoldCh()) + 1));
        dropData = fiveElementsActivityData.getCost();
        this.rewardList.addAll(fiveElementsActivityData.getReward());

        if (player.getDiamond() < dropData.getN()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
            return;
        }

        Message message = new Message(MessageCommand.FIVE_ELEMENTS_ACTIVITY_CHALLENGE_MESSAGE, request.getChannel());
        message.setByte(DungeonDefine.DUNGEON_SUB_TYPE_FIVE);
        gameRole.sendMessage(message);

        if (isEnter) {
            player.setMapType(EMapType.DUNGEON);
            this.fightResult = FightDefine.FIGHT_RESULT_SUCCESS;
        }
    }

    /**
     * 9203五行副本活动战斗结果
     **/
    public void processFiveActivityFightResult(Message request) {
        //战斗结果 0：失败 1：成功 2：平局
        byte result = request.readByte();
        //客户端如果失败 服务器也算作失败 平局同样
        if (FightDefine.FIGHT_RESULT_FAIL == result || FightDefine.FIGHT_RESULT_TIE == result) {
            this.fightResult = result;
        }
        //如果地图类型不为副本算作失败
        if (EMapType.DUNGEON != player.getMapType()) {
            this.fightResult = FightDefine.FIGHT_RESULT_FAIL;
        }
        //服务器验证失败
        if (FightDefine.FIGHT_RESULT_FAIL == this.fightResult && FightDefine.FIGHT_RESULT_SUCCESS == result) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_SERVER_FIGHT_FAIL);
            return;
        }

        Message message = new Message(MessageCommand.FIVE_ELEMENTS_ACTIVITY_FIGHT_DUNGEON_RESULT_MESSAGE, request.getChannel());
        if (FightDefine.FIGHT_RESULT_SUCCESS == this.fightResult) {

            EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
            gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.DUNGEON_FIVE_ELEMENT_CLEARANCE, 1, null));

            player.getDayData().setFiveCostGoldCh((byte) (player.getDayData().getFiveCostGoldCh() - 1));
            gameRole.getPackManager().addGoods(this.rewardList, EGoodsChangeType.FIVE_ELEMENT_RECEIVE, enumSet);
            gameRole.getPackManager().useGoods(dropData, EGoodsChangeType.FIVE_ELEMENT_ACTIVITY_CONSUME, enumSet);

            message.setByte(result);
            message.setByte(this.rewardList.size());

            for (DropData dropData : this.rewardList) {
                dropData.getMessage(message);
            }

            enumSet.add(EPlayerSaveType.DAYDATA);
            enumSet.add(EPlayerSaveType.SMALLDATA);
            gameRole.savePlayer(enumSet);

        }
        player.setMapType(EMapType.FIELD_NORMAL);
        gameRole.sendMessage(message);
        rewardList.clear();
    }

    /**
     * 5009获取灵髓信息
     **/
    public void processSoulInfoMessage(Message request) {
//		Message message = new Message(request.getCmdId(), request.getChannel());
//		message.setByte(player.getCharacterList().size());
//		for(int i=0;i<player.getCharacterList().size();i++){
//			message.setByte(EquipDefine.EQUIP_POS_NUM);
//			for(int j=0;j<EquipDefine.EQUIP_POS_NUM;j++) {
//				message.setByte(3);
//				for(int s=0;s<3;s++) {
//					message.setShort(player.getCharacterList().get(i).getSoulList().get((byte)j).getId()[s]);
//				}
//			}
//		}
//		DungeonData data=DungeonModel.getDungeonData(DungeonDefine.DUNGEON_TYPE_LINGSUI);
//		Dungeon dungeon=gameRole.getDungeonManager().getDungeon(data.getType());
//		message.setShort(dungeon.getPass()-(short)1);
//		
//		if(!player.getSmallData().isSr() && player.getRein()>3 && DateUtil.getDistanceDay(GameDefine.SERVER_CREATE_TIME, System.currentTimeMillis())>4){
//			Mail mail=MailService.createMail("灵髓助力", "灵髓开启，特此献上超值丰厚大礼，请注意领取", EGoodsChangeType.LINGSUI_OPEN_ADD, new DropData(EGoodsType.BOX.getId(),168,1));
//			gameRole.getMailManager().addMailAndNotify(mail); 
//			player.getSmallData().setSr(true);
//			gameRole.savePlayer(EPlayerSaveType.SMALLDATA);
//		}
//		gameRole.sendMessage(message);
    }

    /**
     * 5002灵髓替换
     **/
    public void processSoulReplace(Message request) {
//		byte idx = request.readByte();
//		byte pos = request.readByte();
//		byte from = request.readByte();
//		byte to = request.readByte();
//		Character cha = player.getCharacter(idx);
//		if(cha == null) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
//			return;
//		}
//		
//		LingSuiModelData fromData = FunctionModel.getLingSuiModelData(to);
//		if (fromData == null){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		
//		EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
//		if(!gameRole.getPackManager().useGoods(new DropData(EGoodsType.SUUL_PIECE, to, 1), EGoodsChangeType.LINGSUI_TIHUAN_CONSUME, saves)) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//			return;
//		}
//		
//		if(cha.getSoulList().get(pos).getId()[from]>0) {
//			DropData newData = new DropData(EGoodsType.SUUL_PIECE, cha.getSoulList().get(pos).getId()[from], 1);
//			gameRole.getPackManager().addGoods(newData, EGoodsChangeType.WING_GOD_CONVERSE_ADD, saves);
//		}
//		
//		cha.getSoulList().get(pos).getId()[from]=to;
//		
//		Message message = new Message(MessageCommand.SOUL_REPLACE_MESSAGE, request.getChannel());
//		message.setByte(idx);
//		message.setByte(pos);
//		message.setByte(from);
//		message.setByte(to);
//		gameRole.sendMessage(message);
//		
//		saves.add(EPlayerSaveType.CHA_SOUL_GOD);
//		gameRole.saveData(idx,saves);

    }


    /**
     * 5003灵髓升级
     **/
    public void processSoulUpLev(Message request) {
//		byte idx = request.readByte();
//		byte pos = request.readByte();
//		byte idy = request.readByte();
//		byte id = request.readByte();
//		Character cha = player.getCharacter(idx);
//		if(cha == null) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
//			return;
//		}
//
//		LingSuiModelData lingSuiData = FunctionModel.getLingSuiModelData((byte) (id+1));
//		if (lingSuiData == null){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		
//		DungeonData data=DungeonModel.getDungeonData(DungeonDefine.DUNGEON_TYPE_LINGSUI);
//		Dungeon dungeon=gameRole.getDungeonManager().getDungeon(data.getType());
//		if(dungeon.getPass() < lingSuiData.getNum()) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_NOT_PASS);
//			return;
//		}
//		
//		 EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
//		 if(gameRole.getPackManager().getLingSuiPieces(id).getN() < 2) {
//			 gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//	         return;
//		 }
//		 
//		 if(cha.getSoulList().get(pos).getId()[idy] != id) {
//			 gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//	         return;
//		 }
//		 
//		 if(!gameRole.getPackManager().useGoods(new DropData(EGoodsType.SUUL_PIECE,id,2), EGoodsChangeType.LINGSUI_UP_CONSUME, saves)) {
//			 gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//	         return;
//		 }
//		 
//		 cha.getSoulList().get(pos).getId()[idy]+=(short)1;
////		 gameRole.getPackManager().addGoods(new DropData(EGoodsType.SUUL_PIECE, (byte)id+1, 1), EGoodsChangeType.WING_GOD_CONVERSE_ADD, saves);
//		 
//		 Message message = new Message(MessageCommand.SOUL_UP_MESSAGE, request.getChannel());
//		 message.setByte(idx);
//		 message.setByte(pos);
//		 message.setByte(idy);
//		 message.setByte((byte)(id+1));
//		 message.setByte(lingSuiData.getLevel());
//		 gameRole.sendMessage(message);
//		 
//		 saves.add(EPlayerSaveType.CHA_SOUL_GOD);
//		 gameRole.saveData(idx,saves);
    }

    /**
     * 5004灵髓 合成
     **/
    public void processSoulCompose(Message request) {
        byte id = request.readByte();
        LingSuiModelData lingsuiData = FunctionModel.getLingSuiModelData(id);
        if (lingsuiData == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        if (player.getLvConvert() < lingsuiData.getNeedlv()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_LEVEL_LESS);
            return;
        }
        DungeonData data = DungeonModel.getDungeonData(DungeonDefine.DUNGEON_TYPE_LINGSUI);
        Dungeon dungeon = gameRole.getDungeonManager().getDungeon(data.getType());
        if (dungeon.getPass() < lingsuiData.getNum()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_NOT_PASS);
            return;
        }

        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        if (!gameRole.getPackManager().useGoods(lingsuiData.getCost(), EGoodsChangeType.LINGSUI_CRAFT_CONSUME, saves)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }
        DropData reward = new DropData(EGoodsType.SUUL_PIECE, id, 1);
        gameRole.getPackManager().addGoods(reward, EGoodsChangeType.LINGSUI_CRAFT_ADD, saves);

        Message message = new Message(request.getCmdId(), request.getChannel());
        gameRole.sendMessage(message);

        gameRole.savePlayer(saves);
    }

    /**
     * 5005灵髓主宰试炼信息
     **/
    public void processSoulCopyInfo(Message request) {
        DungeonData data = DungeonModel.getDungeonData(DungeonDefine.DUNGEON_TYPE_LINGSUI);
        Dungeon dungeon = gameRole.getDungeonManager().getDungeon(data.getType());
        Message message = new Message(MessageCommand.SOUL_COPY_INFO_MESSAGE, request.getChannel());
        message.setShort(dungeon.getPass());
        message.setShort(player.getDayData().getLingsuiBattleTimes());

        if (player.getDayData().getLingsuiSweep() == 0) {
            message.setShort(1);
        } else {
            message.setShort(player.getDayData().getLingsuiSweep());
        }
        message.setShort((short) Math.floor(dungeon.getPass() / 10) - player.getDayData().getLingsuiSweep());
        gameRole.sendMessage(message);
    }


    /**
     * 5006 灵髓主宰试炼请求挑战
     **/
    public void processSoulChallenge(Message request) {
        boolean isEnter = true;
        DungeonData data = DungeonModel.getDungeonData(DungeonDefine.DUNGEON_TYPE_LINGSUI);
        Dungeon dungeon = gameRole.getDungeonManager().getDungeon(data.getType());
        if (gameRole.getFightManager().inInstance()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_COPY_ALREADY_IN);
            isEnter = false;
            return;
        } else if (player.getLevelWithRein() < data.getLevelLimit()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_LEVEL_LESS);
            isEnter = false;
            return;
        } else if (player.getVipLevel() < data.getVipLimit()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_VIP_LEVEL_LESS);
            isEnter = false;
            return;
        } else if (player.getDayData().getLingsuiBattleTimes() == (short) 0) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_CURR_MAX_PASS);
            isEnter = false;
            return;
        } else if (player.getZhuzai() > player.getDayData().getLingsuiBattleTimes()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_CURR_MAX_PASS);
            isEnter = false;
            return;
        }

        Message message = new Message(MessageCommand.SOUL_CHALLENGE_MESSAGE, request.getChannel());
        message.setByte(DungeonDefine.DUNGEON_TYPE_LINGSUI);
        message.setByte(30);
        gameRole.sendMessage(message);

        if (isEnter) {
            player.setMapType(EMapType.DUNGEON);
            this.fightResult = FightDefine.FIGHT_RESULT_SUCCESS;

            DungeonZhuzaishilianData dungeonZhuzaishilianData = DungeonModel.getDungeonZhuzaishilianData(dungeon.getPass());
            FighterData fighterData = FighterModel.getFighterDataById(dungeonZhuzaishilianData.getFightId());
            if (player.getFighting() > fighterData.getPower()) {
                this.rewardList.addAll(dungeonZhuzaishilianData.getBattleReward());
            } else {
                this.fightResult = FightDefine.FIGHT_RESULT_FAIL;
            }
        }

    }

    /**
     * 5007灵髓主宰试炼请求战斗结果
     **/
    public void processSoulFightResult(Message request) {
        //战斗结果 0：失败 1：成功 2：平局
        byte result = request.readByte();
        //客户端如果失败 服务器也算作失败 平局同样
        if (FightDefine.FIGHT_RESULT_FAIL == result || FightDefine.FIGHT_RESULT_TIE == result) {
            this.fightResult = result;
        }
        //如果地图类型不为副本算作失败
        if (EMapType.DUNGEON != player.getMapType()) {
            this.fightResult = FightDefine.FIGHT_RESULT_FAIL;
        }
        //服务器验证失败
        if (FightDefine.FIGHT_RESULT_FAIL == this.fightResult && FightDefine.FIGHT_RESULT_SUCCESS == result) {
            gameRole.putErrorMessage(ErrorDefine.ERROR_SERVER_FIGHT_FAIL);
        } else {
            this.fightResult = result;
        }

        Message message = new Message(MessageCommand.SOUL_FIGHT_DUNGEON_RESULT_MESSAGE, request.getChannel());
        message.setByte(this.fightResult);
        if (FightDefine.FIGHT_RESULT_SUCCESS == this.fightResult) {
            EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);

            DungeonData data = DungeonModel.getDungeonData(DungeonDefine.DUNGEON_TYPE_LINGSUI);
            Dungeon dungeon = gameRole.getDungeonManager().getDungeon(data.getType());

            dungeon.addPass();
            player.setZhuzai(dungeon.getPass());

            gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.DUNGEON_ZHUZAISHILIAN_PASS, dungeon.getPass() - 1, enumSet));
            //排行榜
            GameRankManager.getInstance().resetTopZhuzai(player);
            gameRole.getFunctionManager().sendGameRankSimpleTopMsg(FunctionManager.RANK_SIMPLE_TYPE_ZHUZAI);

            message.setByte(this.rewardList.size());
            for (DropData dropData : this.rewardList) {
                dropData.getMessage(message);
            }

//			player.getDayData().setLingsuiBattleTimes((short) (player.getDayData().getLingsuiBattleTimes() - 1));
            gameRole.getDbManager().dungeonDao.updateDungeon(dungeon);

            enumSet.add(EPlayerSaveType.DAYDATA);
            enumSet.add(EPlayerSaveType.ZHUZAISHILIAN);
            gameRole.getPackManager().addGoods(rewardList, EGoodsChangeType.FIGHT_DUNGEON_ADD, enumSet);
            gameRole.getDbManager().playerDao.savePlayer(player, enumSet);

        }
        player.setMapType(EMapType.FIELD_NORMAL);
        gameRole.sendMessage(message);
        rewardList.clear();
    }

    /**
     * 5008灵髓扫荡
     **/
    public void processSoulSweep(Message request) {
        DungeonData data = DungeonModel.getDungeonData(DungeonDefine.DUNGEON_TYPE_LINGSUI);
        Dungeon dungeon = gameRole.getDungeonManager().getDungeon(data.getType());

        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        DungeonZhuzaisaodangData saodangData = DungeonModel.getDungeonZhuzaisaodangData((short) (player.getDayData().getLingsuiSweep() + 1));
        if (saodangData == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        String[] lv = saodangData.getTips();
        if (player.getZhuzai() < Integer.valueOf(lv[1])) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GUANKA_NOT_ENOUGHT);
            return;
        }

        //消耗
        if (!gameRole.getPackManager().useGoods(saodangData.getCost(), EGoodsChangeType.LINGSUI_UP_CONSUME, enumSet)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
            return;
        }

        gameRole.getPackManager().addGoods(saodangData.getReward(), EGoodsChangeType.MAP_REWARD_ADD, enumSet);
        player.getDayData().setLingsuiSweep((short) (player.getDayData().getLingsuiSweep() + 1));

        Message message = new Message(MessageCommand.SOUL_SWEEP_MESSAGE, request.getChannel());
        message.setShort(saodangData.getId());
        message.setShort((saodangData.getId() + 1));
        message.setShort((short) Math.floor(dungeon.getPass() / 10) - player.getDayData().getLingsuiSweep());
        gameRole.sendMessage(message);

        enumSet.add(EPlayerSaveType.DAYDATA);
        gameRole.getDbManager().playerDao.savePlayer(player, enumSet);
        gameRole.getDbManager().dungeonDao.updateDungeon(dungeon);
    }

    /**
     * 6001 法阵图鉴信息
     **/
    public void processFaZhenInfo(Message request) {
        Message message = new Message(MessageCommand.FAZHEN_INFO_MESSAGE, request.getChannel());
        message.setByte(player.getFazhenList().size());
        for (int i = 0; i < player.getFazhenList().size(); i++) {
            message.setByte(player.getFazhenList().get(i).getT());
            message.setShort(player.getFazhenList().get(i).getLev());
        }
        gameRole.sendMessage(message);
    }

    /**
     * 6002 法阵激活
     **/
    public void processFaZhenAct(Message request) {
        byte type = request.readByte();
        byte idx = request.readByte();

        FaZhenModelData fazhenData = FunctionModel.getFaZhenModelData(type, (short) (player.getFazhenList().get(idx).getLev() + 1));
        if (fazhenData == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        if (!gameRole.getPackManager().useGoods(fazhenData.getCost(), EGoodsChangeType.FAZHEN_UP_CONSUME, saves)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }

        player.getFazhenList().get(idx).setLev(fazhenData.getLev());

        Message message = new Message(MessageCommand.FAZHEN_ACTIVATE_MESSAGE, request.getChannel());
        message.setByte(type);
        message.setShort(fazhenData.getLev());
        gameRole.sendMessage(message);

        saves.add(EPlayerSaveType.FAZHEN);
        gameRole.savePlayer(saves);
    }

    /**
     * 210圣物信息
     */
    public void processHolyGoodsInfo(Message request) {
//		Message message = new Message(MessageCommand.HOLYGOODS_INFO_MESSAGE, request.getChannel());
//		message.setByte(player.getCharacterList().size());
//		for(Character ch:player.getCharacterList()){
//			ch.getHolyGoods().getMessage(message);
//		}
//		gameRole.sendMessage(message);
    }

    /**
     * 211圣物升级
     */
    public void processHolyGoodsUp(Message request) {
//		byte idx=request.readByte();
//		Character character=player.getCharacter(idx);
//		if(character==null){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		
//		HolyGoods holyGoods = character.getHolyGoods();
//		if(holyGoods.getStage()==FunctionModel.getHolyGoodsMaxStage() && holyGoods.getStar()==FunctionModel.getHolyGoodsMaxStar()){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_MAX_LEVEL);
//			return;
//		}
//		EnumSet<EPlayerSaveType> enumSet=EnumSet.noneOf(EPlayerSaveType.class);
//		HolyGoodsData holyGoodsData = FunctionModel.getHolyGoodsData(holyGoods.getStage(), holyGoods.getStar());
//		if(holyGoodsData.getCost().getN()>0) {
//			if (!gameRole.getPackManager().useGoods(holyGoodsData.getCost(), EGoodsChangeType.HOLYGOODS_UP_CONSUME,enumSet)) {
//				gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//				return;
//			}
//		}
//		
//		holyGoods.addExp(holyGoodsData.getCost().getN()*GameCommon.getRandomIndex(0,80,20),holyGoodsData.getExp());
//		
//		Message message=new Message(MessageCommand.HOLYGOODS_UP_MESSAGE,request.getChannel());
//		message.setByte(holyGoods.getStage());
//		message.setByte(holyGoods.getStar());
//		message.setShort(holyGoods.getExp());
//		gameRole.sendMessage(message);
//		
//		enumSet.add(EPlayerSaveType.CHA_HOLYGOODS);
//		gameRole.saveData(idx, enumSet);
    }

    /**
     * 212装备圣纹
     */
    public void processHolyLinesEquip(Message request) {
//		byte index = request.readByte();
//		byte id = request.readByte();
//		
//		Character character = player.getCharacter(index);
//		if (character == null){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		HolyLinesData holyLinesData = FunctionModel.getHolyLinesData(id);
//		if (holyLinesData == null){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		HolyGoods holyGoods = character.getHolyGoods();
//		if (holyLinesData.getStageLimit() > holyGoods.getStage()){
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
//			return;
//		}
//		
//		EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
//        if (!gameRole.getPackManager().useGoods(new DropData(EGoodsType.HOLYLINES, id, 1), EGoodsChangeType.HOLYLINES_EQUIP_CONSUME, saves)){
//            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
//            return;
//        }
//        
//        Byte from = holyGoods.getLinesEquip()[holyLinesData.getPos()];
//		if (from != 0){
//			gameRole.getPackManager().addGoods(new DropData(EGoodsType.HOLYLINES, from, 1), EGoodsChangeType.HOLYLINES_EQUIP_ADD, saves);
//		}
//		from=id;
//       
//        //gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.HOLYLINES_EQUIP, 1, saves));
//        
//		Message message = new Message(request.getCmdId(), request.getChannel());
//		message.setByte(index);
//		message.setByte(id);
//		gameRole.sendMessage(message);
//		
//		saves.add(EPlayerSaveType.CHA_HOLYGOODS);
//		gameRole.saveData(index, saves);
    }

    /**
     * 213圣纹合成
     */
    public void processHolyLinesCombine(Message request) {
        byte id = request.readByte();
        HolyLinesData holyLinesData = FunctionModel.getHolyLinesData(id);
        if (holyLinesData == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        if (!gameRole.getPackManager().useGoods(holyLinesData.getCombineCost(), EGoodsChangeType.HOLYLINES_COMBINE_CONSUME, saves)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }
        DropData reward = new DropData(EGoodsType.HOLYLINES, id, 1);
        gameRole.getPackManager().addGoods(reward, EGoodsChangeType.HOLYLINES_COMBINE_ADD, saves);
        gameRole.savePlayer(saves);

        Message message = new Message(request.getCmdId(), request.getChannel());
        message.setByte(id);
        gameRole.sendMessage(message);
    }

    /**
     * 214圣纹转换
     */
    public void processHolyLinesTransform(Message request) {
        byte from = request.readByte();
        byte to = request.readByte();

        HolyLinesData fromData = FunctionModel.getHolyLinesData(from);
        HolyLinesData toData = FunctionModel.getHolyLinesData(from);
        if (fromData == null
                || toData == null
                || fromData.getLevel() != toData.getLevel()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        if (gameRole.getPackManager().getHolyLines(from) == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        if (!gameRole.getPackManager().useGoods(fromData.getTransformCost(), EGoodsChangeType.HOLYLINES_TRANSFORM_CONSUME, saves)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }
        if (!gameRole.getPackManager().useGoods(new DropData(EGoodsType.HOLYLINES, from, 1), EGoodsChangeType.HOLYLINES_TRANSFORM_CONSUME, saves)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }

        DropData newData = new DropData(EGoodsType.HOLYLINES, to, 1);
        gameRole.getPackManager().addGoods(newData, EGoodsChangeType.HOLYLINES_TRANSFORM_ADD, saves);

        Message message = new Message(request.getCmdId(), request.getChannel());
        gameRole.sendMessage(message);

        gameRole.savePlayer(saves);
    }

    /**
     * 220境界信息
     */
    public void processAmbitInfo(Message request) {
        Message message = new Message(MessageCommand.AMBIT_INFO_MESSAGE, request.getChannel());
        player.getAmbit().getMessage(message);
        gameRole.sendMessage(message);
    }

    /**
     * 221境界-进阶
     */
    public void processAmbitUp(Message request) {
        Ambit ambit = player.getAmbit();
        if (ambit.getStage() == FunctionModel.getAmbitMaxStage() && ambit.getStar() == FunctionModel.getAmbitMaxStar()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_MAX_LEVEL);
            return;
        }
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        AmbitData ambitData = FunctionModel.getAmbitData(ambit.getStage(), ambit.getStar());
        if (ambitData.getCost().getN() > 0) {
            if (!gameRole.getPackManager().useGoods(ambitData.getCost(), EGoodsChangeType.AMBIT_UP_CONSUME, enumSet)) {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
                return;
            }
        }
        ambit.addExp(ambitData.getCost().getN(), ambitData.getExp());

        Message message = new Message(MessageCommand.AMBIT_UP_MESSAGE, request.getChannel());
        message.setByte(ambit.getStage());
        message.setByte(ambit.getStar());
        message.setShort(ambit.getExp());
        gameRole.sendMessage(message);

        enumSet.add(EPlayerSaveType.AMBIT);
        gameRole.savePlayer(enumSet);
    }

    /**
     * 222境界-光环激活
     */
    public void processAmbitHaloActive(Message request) {
        byte id = request.readByte();
        //模型数据
        FashionModelData model = EquipModel.getHaloFashionData(id);
        if (model == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        //已经激活该永久光环
        Integer endTime = player.getAmbit().getHalo().get(id);
        if (endTime != null && endTime == -1) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_HALO_REPEAT);
            return;
        }
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        //消耗
        if (!gameRole.getPackManager().useGoods(model.getCost(), EGoodsChangeType.HALO_ACTIVE_CONSUME, saves)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }
        //永久光环
        int time = -1;
        if (model.getTime() == -1) {
            player.getAmbit().getHalo().put(id, time);
        }
        //时间限制
        else {
            long current = System.currentTimeMillis();
            if (endTime == null || endTime < current)
                endTime = (int) (current / DateUtil.SECOND);
            endTime += model.getTime();
            player.getAmbit().getHalo().put(id, endTime);
            time = endTime;
        }
        //消息
        Message msg = new Message(MessageCommand.AMBIT_HALO_ACTIVE_MESSAGE, request.getChannel());
        msg.setByte(id);
        msg.setInt((int) time);
        gameRole.sendMessage(msg);
        //保存数据

        saves.add(EPlayerSaveType.AMBIT);
        gameRole.savePlayer(saves);
    }

    /**
     * 223境界-光环幻化
     */
    public void processAmbitHaloEndue(Message request) {
        byte id = request.readByte();
        //如果卸下 id = 0
        if (id != 0) {
            //未激活该光环
            Integer endTime = player.getAmbit().getHalo().get(id);
            if (endTime == null) {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_HALO_NO_ACTIVE);
                return;
            }
            long curr = System.currentTimeMillis();
            if (endTime != -1 && endTime * DateUtil.SECOND <= curr) {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_HALO_NO_ACTIVE);
                return;
            }
        }

        //消息
        Message msg = new Message(MessageCommand.AMBIT_HALO_ENDUE_MESSAGE, request.getChannel());
        msg.setByte(id);
        gameRole.sendMessage(msg);
        //保存数据

        player.getAmbit().setHaloShow(id);
        gameRole.savePlayer(EPlayerSaveType.AMBIT);
    }

    /**
     * 224境界-技能
     */
    public void processAmbitSkill(Message request) {
        Ambit ambit = player.getAmbit();
        int needStage = (ambit.getSkill() + 1) * 3;
        if (ambit.getStage() < needStage) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        ambit.setSkill((byte) (ambit.getSkill() + 1));
        //消息
        Message msg = new Message(MessageCommand.AMBIT_SKILL_MESSAGE, request.getChannel());
        msg.setByte(ambit.getSkill());
        gameRole.sendMessage(msg);
        //保存数据
        gameRole.savePlayer(EPlayerSaveType.AMBIT);
    }

    private void checkFiveElementsUpReplyTime() {
        if (player.getSmallData().getFiveElementUpNum() < SectionDefine.FIVE_ELEMENTS_UP_MAX_TIMES) {
            if (player.getSmallData().getFiveRestTime() == 0) {
                player.getSmallData().setFiveElementUpNum(SectionDefine.FIVE_ELEMENTS_UP_MAX_TIMES);
                player.getSmallData().setFiveElementUpTime(0);
                player.getSmallData().setFiveRestTime(System.currentTimeMillis());
            } else {
                int time = (int) ((System.currentTimeMillis() - player.getSmallData().getFiveRestTime()) / (90 * 60 * 1000));
                if (player.getSmallData().getFiveElementUpNum() + time >= SectionDefine.FIVE_ELEMENTS_UP_MAX_TIMES) {
                    player.getSmallData().setFiveElementUpNum(SectionDefine.FIVE_ELEMENTS_UP_MAX_TIMES);
                    player.getSmallData().setFiveRestTime(System.currentTimeMillis());
                } else {
                    if ((90 * 60 * 1000) - (System.currentTimeMillis() - player.getSmallData().getFiveRestTime()) > 0) {
                        player.getSmallData().setFiveElementUpNum((byte) (player.getSmallData().getFiveElementUpNum()));
                    } else {
                        player.getSmallData().setFiveElementUpNum((byte) (player.getSmallData().getFiveElementUpNum() + time));
                        player.getSmallData().setFiveRestTime(System.currentTimeMillis());
                    }
                }
                player.getSmallData().setFiveElementUpTime((90 * 60 * 1000) - (System.currentTimeMillis() - player.getSmallData().getFiveRestTime()));
            }
        } else {
            player.getSmallData().setFiveElementUpTime(0);
            player.getSmallData().setFiveElementUpNum(SectionDefine.FIVE_ELEMENTS_UP_MAX_TIMES);
            player.getSmallData().setFiveRestTime(0);
        }
    }
}
