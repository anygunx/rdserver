package com.rd.game;

import com.google.common.base.Preconditions;
import com.rd.bean.drop.DropData;
import com.rd.bean.goods.TimeGoods;
import com.rd.bean.mail.Mail;
import com.rd.bean.player.Player;
import com.rd.bean.relationship.NRelatedPlayer;
import com.rd.common.BossService;
import com.rd.common.BroadcastService;
import com.rd.common.ChatService;
import com.rd.common.MailService;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.*;
import com.rd.dao.db.DBManager;
import com.rd.define.*;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.EventManager;
import com.rd.game.event.GameEvent;
import com.rd.game.event.IEventListener;
import com.rd.game.manager.*;
import com.lg.bean.game.LevelUp;
import com.lg.bean.game.Login;
import com.rd.model.GuanJieModel;
import com.rd.model.RoleModel;
import com.rd.model.data.GuanJieData;
import com.rd.model.data.VipModelData;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import com.rd.net.message.MessageArray;
import com.rd.net.message.MessageQueue;
import com.rd.task.ETaskType;
import com.rd.task.Task;
import com.rd.task.TaskManager;
import com.rd.util.DateUtil;
import com.rd.util.LogUtil;
import org.apache.log4j.Logger;

import java.util.EnumSet;
import java.util.LinkedHashMap;

//import com.rd.game.local.CrossGameManager;

public class GameRole implements IGameRole, IEventListener {

    private static Logger logger = Logger.getLogger(GameRole.class);

    private NEquipManager nEquipManager;
    private NSkinMnager nPiFuMnager;
    private NFaBaoManager nPulseManager;
    private NRelationManager nRelationManager;
    private NSkillManager nSkillManager;
    private NDungeonManager nCopyManager;
    private NBossManager nBossManager;
    private NshopManager nshopManager;
    private TeamManager teamManager;

    private NRiChangManager nRiChangManager;
    private NTaskManager nTaskManager;

    private NFactionManager nFactionManager;

    private NTaskAdvancedManager nTaskAdvancedManager;
    private NHuSongManger huSongManger;
    private NChatManager nChatManager;
    private NJingJiChangManager nJingJiChangManager;

    public NSkillManager getNSkillManager() {
        if (null == nSkillManager) {
            nSkillManager = new NSkillManager(this);
        }
        return nSkillManager;
    }

    public NChatManager getNChatManager() {
        if (null == nChatManager) {
            nChatManager = new NChatManager(this);
        }
        return nChatManager;
    }


    public NTaskManager getNTaskManager() {
        if (null == nTaskManager) {
            nTaskManager = new NTaskManager(this);
        }
        return nTaskManager;
    }


    public NRiChangManager getNRiChangManager() {
        if (null == nRiChangManager) {
            nRiChangManager = new NRiChangManager(this);
        }
        return nRiChangManager;
    }


    public NEquipManager getNEquipManager() {
        if (null == nEquipManager) {
            nEquipManager = new NEquipManager(this);
        }
        return nEquipManager;
    }

    public NSkinMnager getNPiFuManager() {
        if (null == nPiFuMnager) {
            nPiFuMnager = new NSkinMnager(this);
        }
        return nPiFuMnager;
    }

    public NFaBaoManager NPulseManager() {
        if (null == nPulseManager) {
            nPulseManager = new NFaBaoManager(this);
        }
        return nPulseManager;
    }

    public NRelationManager getNRelationManager() {
        if (null == nRelationManager) {
            nRelationManager = new NRelationManager(this);
        }
        return nRelationManager;
    }

    public NDungeonManager getNCopyManager() {
        if (null == nCopyManager) {
            nCopyManager = new NDungeonManager(this);
        }
        return nCopyManager;
    }

    public NBossManager getNBossManager() {
        if (null == nBossManager) {
            nBossManager = new NBossManager(this);
        }
        return nBossManager;
    }

    public NshopManager getNshopManager() {
        if (null == nshopManager) {
            nshopManager = new NshopManager(this);
        }
        return nshopManager;
    }

    public TeamManager getTeamManager() {
        if (this.teamManager == null) {
            this.teamManager = new TeamManager(this);
        }
        return this.teamManager;
    }


    public NFactionManager getNFactionManager() {
        if (this.nFactionManager == null) {
            this.nFactionManager = new NFactionManager(this);
        }
        return this.nFactionManager;
    }


    public NTaskAdvancedManager getNTaskAdvancedManager() {
        if (this.nTaskAdvancedManager == null) {
            this.nTaskAdvancedManager = new NTaskAdvancedManager(this);
        }
        return this.nTaskAdvancedManager;
    }

    public NHuSongManger getNHuSongManger() {
        if (null == huSongManger) {
            huSongManger = new NHuSongManger(this);
        }
        return huSongManger;
    }

    public NJingJiChangManager getNJingJiChangManager() {
        if (null == nJingJiChangManager) {
            nJingJiChangManager = new NJingJiChangManager(this);
        }
        return nJingJiChangManager;
    }

    /*************************************以上是新版本的开发  以下是旧版本**************************************************************************/
    private MessageQueue messageQueue;

    private Player player;

    private DBManager _dbManager;
    private CheatManager _cheatManager;
    private FightManager _fightManager;
    private PackManager _packManager;
    private SkillManager _skillManger;
    private EquipManager _equipManager;
    private EventManager _eventManager;
    private DungeonManager _dungeonManager;
    private FunctionManager _functionManager;
    private LadderManager _ladderManager;
    private ActivityManager _activityManager;
    private ShopManager _shopManager;
    private MailManager _mailManager;
    private MissionManager missionManager;
    private ChatManager _chatManager;
    //private SpiritManager spiritManager;
    private EscortManager _escortManager;
    //private SmallDataManager smallDataManager;
    private GangManager _gangManager;
    private BossManager _bossManager;
    private SectionManager sectionManager;
    private FunctionManager functionManager;
    private PvpManager _pvpManager;
    private TitleManager _titleManager;
    private BeatManager _beatManager;
    //private TestManager testManager;
    //private RelationshipManager relationshipManager;
    //private CrossGameManager crossManager;
    //private AuctionManager auctionManager;
    private NightFightManager _nightFightManager;
    private LianTiManager _lianTiManager;
    private GuanJieManager _guanJieManager;
    private ZhanWenManager _zhanWenManager;
    private PayManager _payManager;
    private MonsterSiegeManager _monsterSiegeManager;
    private CardManager _cardManager;

    private CombatManager _combatManager;

    private GrowManager _growManager;

    //上次心跳时间
    private long lastBeatTime = -1;
    //上次消息时间
    private long lastTickTime = -1;
    // 广告买量添加
    private String ad;
    private String spid;

    public GameRole(Player player) {
        this.player = player;
        //this.beatManager = new BeatManager(this);
        this.messageQueue = new MessageQueue();
        //this.dbManager=new DBManager();
        //this.cheatManager = new CheatManager(this);
        //this.fightManager=new FightManager(this);
        //this.packManager=new PackManager(this);
        //this.skillManger=new SkillManager(this);
        //this.equipManager=new EquipManager(this);
        //this.eventManager=new EventManager(this);
        //this.dungeonManager=new DungeonManager(this);
        //this.functionManager=new FunctionManager(this);
        //this.ladderManager = new LadderManager(this);
        //this.activityManager = new ActivityManager(this);
        //this.shopManager = new ShopManager(this);
        //this.mailManager = new MailManager(this);
        this.missionManager = new MissionManager(this);
        //this.chatManager = new ChatManager(this);
        //this.spiritManager = new SpiritManager(this);
        //this.escortManager = new EscortManager(this);
        //this.smallDataManager = new SmallDataManager(this);
        //this.gangManager = new GangManager(this);
        //this.bossManager = new BossManager(this);
        this.sectionManager = new SectionManager(this);
        //this.pvpManager = new PvpManager(this);
        //this.titleManager = new TitleManager(this);
        //this.testManager = new TestManager(this);
        //this.relationshipManager = new RelationshipManager(this);
        //this.crossManager = new CrossGameManager(this);
        //this.auctionManager = new AuctionManager(this);
        //this.nightFightManager = new NightFightManager(this);
        //this.lianTiManager = new LianTiManager(this);
        //this.guanJieManager = new GuanJieManager(this);
        //this.zhanWenManager = new ZhanWenManager(this);
        //this.payManager = new PayManager(this);
        //this.monsterSiegeManager = new MonsterSiegeManager(this);
        //this.cardManager = new CardManager(this);
    }

    public void init() {
        //this._cheatManager.init();
        //this._eventManager.init();
        //this._payManager.init();
        //this._dungeonManager.init();
        //this._ladderManager.init();
        //this._activityManager.init();
        //this._shopManager.init();
        //this._mailManager.init();
        this.missionManager.init();
        //this._escortManager.init();
        //this._bossManager.init();
        //this._titleManager.init();
        //this.relationshipManager.init();
        //this._chatManager.init();
        //this.crossManager.init();
        //this.auctionManager.init();
        this.sectionManager.init();
        //this._monsterSiegeManager.init();
        getNRelationManager().init();
        getNCopyManager().init();
    }

    public void enterGame(Message request, boolean isNew) {
        //进入游戏需要重置的状态
        this.resetEnter();
        this.getPackManager().onEnterGame();
        this.getActivityManager().handleLogin();
        this.player.getCardMission().updateMission();

        MessageArray msgs = new MessageArray(request.getChannel());
        if (isNew) {
            msgs.addMessage(missionManager.getChainMessage());    //901
            //msgs.addMessage(player.getPlayerMessage());	//106
            msgs.addMessage(getTitleManager().getTitleInfoMsg(-1));
            msgs.addMessage(player.getPlayerMessage());    //106
            msgs.addMessage(player.getGoodsListMessage());    //106
        } else {
            //离线奖励消息第一个发
            getFunctionManager().getOfflineRewardMessage(msgs);
            msgs.addMessage(getTitleManager().getTitleInfoMsg(-1));
            //msgs.addMessage(getSkillManager().getHeartSkillMessage());
            msgs.addMessage(player.getGoodsListMessage());
            msgs.addMessage(missionManager.getChainMessage());
            msgs.addMessage(missionManager.getDailyListMessage());
            msgs.addMessage(missionManager.getDragonBallMessage());
            msgs.addMessage(missionManager.getAchievementMissionMessage());
            msgs.addMessage(getMailManager().getMailList());
            msgs.addMessage(getEscortManager().getEscortDetail());
            msgs.addMessage(getEscortManager().getEscortLogRead());
            //msgs.addMessage(relationshipManager.getBlackListMessage());
            msgs.addMessage(ChatService.getChatListMsg());
            if (player.getGang() != null)
                msgs.addMessage(ChatService.getGangChatListMsg(player.getGang().getId()));
            msgs.addMessage(getActivityManager().getMonthlyCardMsg());
            msgs.addMessage(getActivityManager().get7DayMessage());
            msgs.addMessage(getPayManager().getPayRecordMsg());
            msgs.addMessage(getActivityManager().getWelfareMsg());
            msgs.addMessage(getActivityManager().getActivityMsg());
            //msgs.addMessage(activityManager.getActivityNewMessage());
            msgs.addMessage(BroadcastService.getAllXunbaoMsg());
            msgs.addMessage(player.getLimitGoodsListMessage());
            msgs.addMessage(player.getGangSkillList());
            msgs.addMessage(getBossManager().getCitCueMsg());
            msgs.addMessage(getEquipManager().getRedEquipBagMsg());
            msgs.addMessage(player.getDayRefreshMsg());
            msgs.addMessage(getFunctionManager().getShareInfoMessage());
            //msgs.addMessage(auctionManager.getUpdateSubscribeMessage());
            msgs.addMessage(getFunctionManager().getRankWorshipList());
            msgs.addMessage(getCardManager().getCardBookMessage());
            msgs.addMessage(player.getPlayerMessage());
            //渠道判断
//			if(player.getChannel()==1004){
//				msgs.addMessage(smallDataManager.getDeskInfoMessage());
//			}


        }
        //进入游戏消息 最后发
        Message enterMsg = new Message(MessageCommand.ENTER_GAME_MESSAGE);
        msgs.addMessage(enterMsg);

        getMonsterSiegeManager().onEnterGame();
        msgs.pack();
        GameWorld.getPtr().sendMessage(request.getChannel(), msgs.getBuf());

        //记录玩家登录日志
        LogUtil.log(player, new Login((int) player.getFighting()));

        // 发送登录事件
        if (!isNew) {
//			GameEvent event = EGameEventType.ENTER_GAME.create(this, 1, null);
//			getEventManager().notifyEvent(event);
            GameEvent event = EGameEventType.EVER_DAY_LOGIN.create(this, 1, null);
            getEventManager().notifyEvent(event);
        }

        player.getRoleEquipMap().put((short) 211, 1);
        player.getRoleEquipMap().put((short) 241, 1);
        player.getRoleEquipMap().put((short) 245, 1);
    }

//	/**
//	 * 每日首次登陆检测
//	 */
//	private void checkNewEnter() {
//		long ts = System.currentTimeMillis();
//		long dayStart = DateUtil.getDayStartTime(ts);
//		if (player.getLastLoginTime() != player.getLastLogoutTime()  //新号
//				&& player.getLastLoginTime() > dayStart){	// 今天登陆过了
//			return;
//		}
//		// 现在跨天重置loginTime好像在活动里。。
//		GameEvent event = new GameEvent(EGameEventType.NEW_ENTER_GAME, 1, null);
//		getEventManager().notifyEvent(event);
//	}

    public boolean changeDiamond(int diamond, EGoodsChangeType changeType) {
        if (diamond == 0) {
            return true;
        }
        Preconditions.checkArgument(changeType.checkValue(diamond), "GameRole.addDiamond() failed. Player=" + player.getId() + " addValue=" + diamond + " changeType=" + changeType.name());
        //player.addDiamond(diamond, changeType);
        //dbManager.playerDao.updatePlayerDiamond(player);
        //sendUpdateDiamondMessage();
        if (diamond < 0) {
//			SimplePlayerEvent event = new SimplePlayerEvent(this, EGameEventType.DIAMOND_CONSUME, -diamond, 0);
//			getEventManager().notifyEvent(event);
        }
        return true;
    }

    public boolean addDiamond(int diamond, EGoodsChangeType changeType) {
        if (diamond < 0)
            return false;
        return changeDiamond(diamond, changeType);
    }

    public boolean subDiamond(int diamond, EGoodsChangeType changeType) {
        if (diamond < 0)
            return false;
        return changeDiamond(-diamond, changeType);
    }

    public boolean addExp(int exp, EnumSet<EPlayerSaveType> enumSet) {
        if (exp < 1) {
            return false;
        }
        short level = player.getLevel();
        if (level >= RoleDef.STORAGE_EXP_LEVEL) {
            player.setExp(player.getExp() + exp);
            return false;
        }
        short baseLevel = level;
        long result = player.getExp() + exp;
        long totalExp = RoleModel.getMaxExpByLevel(level);
        while (result >= totalExp) {
            ++level;
            if (level >= LevelDefine.MAX_ROLE_LEVEL) { // 满级则退出
                level = LevelDefine.MAX_ROLE_LEVEL;
                result = 0;
                break;
            }
            result -= totalExp;
            totalExp = RoleModel.getMaxExpByLevel(level);
            upSkillGrade(level);

        }
        boolean isLevelUp = level > baseLevel ? true : false;
        player.setExp(result);
        player.setLevel(level);

        //升级事件处理
        if (isLevelUp) {
            levelUp(enumSet);
        }
        return true;
    }

    /**
     * 升级技能等级
     */
    private void upSkillGrade(short level) {

        getNSkillManager().jihuoSkill(level);

    }

    public void levelUp(EnumSet<EPlayerSaveType> enumSet) {
        enumSet.add(EPlayerSaveType.LEVEL);
        //通知角色升级消息
        this.getEventManager().notifyEvent(new GameEvent(EGameEventType.PLAYER_REACH_LEVEL, player.getLevel(), enumSet));
        BossService.lvUpEvent(this);
    }

    public void sendMessageArray(MessageArray msgs) {
        addBufferedMessages(msgs);
        msgs.pack();
        GameWorld.getPtr().sendMessage(msgs.getChannel(), msgs.getBuf());
    }

    public void sendMessage(Message message) {
        MessageArray msgs = new MessageArray(message.getChannel());
        addBufferedMessages(msgs);
        msgs.addMessage(message);
        msgs.pack();
        GameWorld.getPtr().sendMessage(msgs.getChannel(), msgs.getBuf());
    }

    public void putMessageQueue(Message message) {
        messageQueue.put(message);
    }

    private void addBufferedMessages(MessageArray msgs) {
        messageQueue.addBufferdMessages(msgs);
    }

    /**
     * 发送错误提示
     *
     * @param request
     * @param index
     */
    public void sendErrorTipMessage(Message request, short index) {
        Message message = new Message(MessageCommand.ERROR_TIP_MESSAGE, request.getChannel());
        message.setShort(index);
        this.sendMessage(message);
    }

    public void putErrorMessage(short index) {
        if (ErrorDefine.ERROR_SERVER_FIGHT_FAIL == index) {
            return;
        }
        Message message = new Message(MessageCommand.ERROR_TIP_MESSAGE);
        message.setShort(index);
        this.putMessageQueue(message);
    }

    public void sendTick(Message request) {
        Message message = new Message(MessageCommand.GAME_TICK_MESSAGE, request.getChannel());
        this.sendMessage(message);
    }

    public void sendUpdateCurrencyMsg(EGoodsType type, EGoodsChangeType change) {
        Message msg = new Message(MessageCommand.UPDATE_CURRENCY_MESSAGE);
        msg.setByte(type.getId());
        long value = type.getCmd().getValue(this, new DropData(type, 0, 0));
        msg.setLong(value);
        msg.setShort(change.getId());
        this.putMessageQueue(msg);
    }

    /**
     * 保存player及character数据
     *
     * @param saves
     */
    public void saveData(int idx, EnumSet<EPlayerSaveType> saves) {
        this.getDbManager().playerDao.saveData(player, idx, saves);
    }

    /**
     * 仅保存player数据
     *
     * @param saves
     */
    public void savePlayer(EnumSet<EPlayerSaveType> saves) {
        this.getDbManager().playerDao.savePlayer(player, saves);
    }

    /**
     * 仅保存character数据
     *
     * @param saves
     */
    public void savePlayer(int idx, EnumSet<EPlayerSaveType> saves) {
        Character cha = player.getCharacter(idx);
        if (cha == null) {
            logger.error("保存了没有的角色，角色索引：" + idx);
            return;
        }
        //this.getDbManager().playerDao.saveCharacter(cha, saves);
    }

    public FunctionManager getFunctionManager() {
        if (null == _functionManager) {
            _functionManager = new FunctionManager(this);
        }
        return this._functionManager;
    }

    public DungeonManager getDungeonManager() {
        if (null == _dungeonManager) {
            _dungeonManager = new DungeonManager(this);
        }
        return this._dungeonManager;
    }

    public EventManager getEventManager() {
        if (null == _eventManager) {
            _eventManager = new EventManager(this);
        }
        return _eventManager;
    }

    public EquipManager getEquipManager() {
        if (null == _equipManager) {
            _equipManager = new EquipManager(this);
        }
        return _equipManager;
    }

    public SkillManager getSkillManager() {
        if (null == _skillManger) {
            _skillManger = new SkillManager(this);
        }
        return _skillManger;
    }

    public DBManager getDbManager() {
        if (null == _dbManager) {
            _dbManager = new DBManager();
        }
        return _dbManager;
    }

    public PackManager getPackManager() {
        if (null == _packManager) {
            _packManager = new PackManager(this);
        }
        return _packManager;
    }

    public Player getPlayer() {
        return player;
    }

    public ZhanWenManager getZhanWenManager() {
        if (null == _zhanWenManager) {
            _zhanWenManager = new ZhanWenManager(this);
        }
        return _zhanWenManager;
    }

    public FightManager getFightManager() {
        if (null == _fightManager) {
            _fightManager = new FightManager(this);
        }
        return _fightManager;
    }

    public LadderManager getLadderManager() {
        if (null == _ladderManager) {
            _ladderManager = new LadderManager(this);
        }
        return _ladderManager;
    }

    public ActivityManager getActivityManager() {
        if (null == _activityManager) {
            _activityManager = new ActivityManager(this);
        }
        return _activityManager;
    }

    public ShopManager getShopManager() {
        if (null == _shopManager) {
            _shopManager = new ShopManager(this);
        }
        return _shopManager;
    }

    public MailManager getMailManager() {
        if (null == _mailManager) {
            _mailManager = new MailManager(this);
        }
        return _mailManager;
    }

    public ChatManager getChatManager() {
        if (null == _chatManager) {
            _chatManager = new ChatManager(this);
        }
        return _chatManager;
    }

    public CheatManager getCheatManager() {
        if (null == _cheatManager) {
            _cheatManager = new CheatManager(this);
        }
        return _cheatManager;
    }

    public MissionManager getMissionManager() {
        return missionManager;
    }

//	public SpiritManager getSpiritManager() {
//		return spiritManager;
//	}

    public EscortManager getEscortManager() {
        if (null == _escortManager) {
            _escortManager = new EscortManager(this);
        }
        return _escortManager;
    }

//	public SmallDataManager getSmallDataManager() {
//		return smallDataManager;
//	}

    public GangManager getGangManager() {
        if (null == _gangManager) {
            _gangManager = new GangManager(this);
        }
        return _gangManager;
    }

    public BossManager getBossManager() {
        if (null == _bossManager) {
            _bossManager = new BossManager(this);
        }
        return _bossManager;
    }

    public SectionManager getSectionManager() {
//		if(null==_sectionManager){
//			_sectionManager=new SectionManager(this);
//		}
        return sectionManager;
    }

    public PvpManager getPvpManager() {
        if (null == _pvpManager) {
            _pvpManager = new PvpManager(this);
        }
        return _pvpManager;
    }

    public TitleManager getTitleManager() {
        if (null == _titleManager) {
            _titleManager = new TitleManager(this);
        }
        return _titleManager;
    }

    public BeatManager getBeatManager() {
        if (null == _beatManager) {
            _beatManager = new BeatManager(this);
        }
        return _beatManager;
    }

//	public TestManager getTestManager() {
//		return testManager;
//	}

//	public CrossGameManager getCrossManager() {
//		return crossManager;
//	}

//	public AuctionManager getAuctionManager() {
//		return auctionManager;
//	}

    public long getLastTickTime() {
        return lastTickTime;
    }

    public void setLastTickTime(long lastTickTime) {
        this.lastTickTime = lastTickTime;
    }

    public long getLastBeatTime() {
        return lastBeatTime;
    }

    public void setLastBeatTime(long lastBeatTime) {
        this.lastBeatTime = lastBeatTime;
    }

    public NightFightManager getNightFightManager() {
        if (null == _nightFightManager) {
            _nightFightManager = new NightFightManager(this);
        }
        return _nightFightManager;
    }

    /**
     * 进入游戏状态重置
     */
    private void resetEnter() {
        //登录数据放入在线数据 移除离线数据
        GameWorld.getPtr().removeOfflinePlayer(player.getId());
        //GameRelationshipManager.getInstance().removeCache(player.getId());
        NRelationManager nrelation = NGameRelationshipManager.getInstance().removeCache(player.getId());
        LinkedHashMap<Integer, NRelatedPlayer> fensi = null;
        LinkedHashMap<Integer, NRelatedPlayer> guanzhu = null;
        if (nrelation != null) {//两个玩家共同操作同一条数据为了产出并发倒是拿到的不是最新数据
            fensi = nrelation.getFenSi();
            guanzhu = nrelation.getGuanZhuList();
        }
        nRelationManager.initGuanZhu(fensi, guanzhu);

        NGameRelationshipManager.getInstance().removePlayerMap(player.getId());
        //设置登录码
        player.setLoginCode(GameWorld.getPtr().getLoginCode());
        //更新战斗力
        player.updateFighting();
        getNSkillManager().jihuoSkill(player.getLevel());
        //重置地图类型为野外小怪
        player.setMapType(EMapType.FIELD_NORMAL);
        //设置最后的tick时间
        long curr = System.currentTimeMillis();
        this.setLastBeatTime(curr);
        this.setLastTickTime(curr);
        this.getBeatManager().setCheat(0);
        //清空消息队列
        messageQueue.clear();
        //更新限时物品
        this.getPackManager().updateTimeGoods(curr);
        //设置玩家公会
        //GameGangManager.getInstance().setPlayerGang(player);
        NGameFactionManager.getInstance().setPlayerGang(player);
        //每日状态更新
        if (this.player.isDailyUpdate()) {
            this.resetDaily();
        }

        this.getCombatManager().onReset();
    }

    public boolean isOnline() {
        return true;
    }

    /**
     * 退出游戏
     */
    public void logout(long currentTime) {
        try {
            EnumSet<EPlayerSaveType> enumSet = EnumSet.allOf(EPlayerSaveType.class);
            this.getDbManager().playerDao.savePlayer(this.player, enumSet);
        } catch (Exception e) {
            logger.error("保存角色离线数据时发生异常！", e);
        }
    }

    /**
     * 得到玩家ID
     *
     * @return
     */
    public int getPlayerId() {
        return this.player.getId();
    }

    /**
     * 事件处理器
     */
    public void handleEvent(GameEvent event) {
        switch (event.getType()) {
            //case MOUNT_MELTING:			//坐骑装备熔炼
            //case GOD_ARTIFACT_ACTIVE:	//神器激活
            //case SWORD_POOL_UP:			//剑池升级
            case REIN_REACH_LEVEL:                //转生升级
                //case MAGIC_LEVEL_UP:		//法宝升级
                //case MAGIC_STAGE_UP:		//法宝升阶
                //case DUNGEON_DEKARON_PASS:	//诛仙台通关
                //case WEAR_EQUIPMENT:		//穿戴装备
                //case DOM_LEVEL_UP:			//主宰升级
                //case DOM_STAGE_UP:			//主宰升阶
            case MIRROR_UP:                //铜镜
                //case FLUTE_UP:				//玉笛
                //case EYE_LEFT_UP:			//左眼
                //case EYE_RIGHT_UP:			//右眼
            case EQUIP_STRENGTHEN:        //强化
                //case EQUIP_FILL_SOUL:		//注灵
            case GEM_REACH_LEVEL:                //宝石
                //case CASTING_SOUL_UP:		//铸魂
                //case MOUNT_UP:				//坐骑升级
                //case SPIRIT_ACTIVE:			//元神激活
                //case SPIRIT_UP:				//元神升级
            case MERIDIAN_UP:            //经脉升级
                //case GANG_SKILL_UP:			//公会技能
                //case GONG_FA_UP:			//功法升级
            case SKILL_LEVEL_UP:        //技能升级
                //case CUILIAN_UP:			//淬炼升级
                //case WEAPON_ACTIVE:			//武器激活
                //case PET_UP:				//宠物升级
                //case ARMOR_ACTIVE:			//装备激活
                //case MOUNT_ACTIVE:			//坐骑激活
            case DRAGON_BALL_LEVEL_UP:    //龙珠升级
            case MEDAL_REACH_LEVEL:        //勋章升级
            case ARTIFACT_BOSS_INVOKE:  //关卡神器激活
            case SHENBING_INVOKE:        //神兵激活
            case SHENBING_STAGE_UP:        //神兵升阶
                //case SHENBING_STAR_UP:		//神兵升星
            case GUANJIE_REACH_LEVEL:    //官阶升降
            case ZHANWEN_ACTIVE:        //战纹激活
            case ZHANWEN_UP:            //战纹升级
            case CARD_LEVEL_UP:
            case FIVE_ELEMENT_UP:       //五行升级
                //更新战斗力
                player.updateFighting();
                event.addPlayerSaveType(EPlayerSaveType.FIGHTING);
                break;
            default:
                break;
        }
        //玩家等级提升，邮件奖励发送
        if (EGameEventType.PLAYER_REACH_LEVEL == event.getType()) {

            int level = event.getData();
            sendMailReward(level);
        }
    }

    /**
     * 根据玩家等级，发送邮件奖励
     *
     * @param level
     */
    private void sendMailReward(int level) {
//		RoleData  roleData = RoleModel.getRoleData(level);
//		for (short mailId: roleData.getMailList()){
//			MailRewardModelData data = MailModel.getMailRewardModelData(mailId);
//			if (data != null) {
//				Mail  mail = MailService.createMail(data, EGoodsChangeType.LEVEL_UP_MAILREWARD);
//				MailService.sendSystemMail(getPlayerId(), mail);
//			}
//		}
    }

    public GuanJieManager getGuanJieManager() {
        if (null == _guanJieManager) {
            _guanJieManager = new GuanJieManager(this);
        }
        return _guanJieManager;
    }

    /**
     * 发送更新经验消息
     *
     * @param lvUp
     */
    public void sendUpdateExpMsg(boolean lvUp) {
        Message msg = new Message(MessageCommand.UPDATE_EXP_MESSAGE);
        msg.setLong(player.getExp());
        msg.setBool(lvUp);
        if (lvUp) {
            msg.setShort(player.getLevel());

            //记录玩家升级日志
            LogUtil.log(player, new LevelUp(player.getLevel()));

            //给玩家发送50级邮件
            if (player.getLevel() == 50) {
                Mail mail = MailService.createMail("快速攻略", "恭喜您到达了50级，以下攻略可以帮助您快速提升战力，成就霸业：\n" +
                        "1.挑战<font color='#04B701'>关卡</font>可以提升<font color='#04B701'>【经验】</font>，提升关卡效率\n" +
                        "2.升级到<font color='#04B701'>55</font>级，可以通过<font color='#04B701'>【闯天关】</font>快速获得<font color='#04B701'>海量经验</font>，迅猛达到80级\n" +
                        "3.完成<font color='#04B701'>限时任务</font>，获得合击技碎片，开启对怪物的<font color='#04B701'>成吨伤害</font>\n" +
                        "4.首充可获得<font color='#F20F14'>高级红装</font>，激活神器，<font color='#F20F14'>【战斗力】暴增</font>，轻松碾压各种BOSS\n" +
                        "5.<font color='#F20F14'>至尊2</font>可以提前解锁<font color='#F20F14'>第二角色</font>，<font color='#F20F14'>至尊4</font>可以提前解锁第<font color='#F20F14'>三个角色</font>，合击输出暴增\n" +
                        "6.充值至尊可以急速提升战力<font color='#04B701'><u>“前往充值”</u></font>", EGoodsChangeType.GM_ADD);
                this.getMailManager().addMailAndNotify(mail);
            }
        }
        this.putMessageQueue(msg);
    }

    /**
     * 每分钟更新
     */
    public void updateMinute(long currentTime) {
        //this.getPackManager().updateTimeGoods(currentTime);
    }

    @Override
    public GameRole getGameRole() {
        // TODO Auto-generated method stub
        return this;
    }

    public void savePlayer(EPlayerSaveType saveType) {
        EnumSet<EPlayerSaveType> enumSet = EnumSet.of(saveType);
        new PlayerDao().savePlayer(player, enumSet);
    }

    public void sendVipGiftMsg(VipModelData vipData) {
        Mail mail = MailService.createMail(vipData.getTitle(), vipData.getContent(),
                EGoodsChangeType.VIP_GIFT_ADD, vipData.getRewards());
        int id = this.getDbManager().mailDao.insertMail(this.getPlayerId(), mail);
        if (GameDefine.INVALID != id) {
            this.getMailManager().addMailAndNotify(mail);
        }
    }

    public void sendLimitGoods(TimeGoods timeGoods) {
        Message message = new Message(MessageCommand.GOODS_LIMIT_ADD_MESSAGE);
        timeGoods.getMessage(message, System.currentTimeMillis());
        this.putMessageQueue(message);
    }

//	public RelationshipManager getRelationshipManager() {
//		return relationshipManager;
//	}

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getSpid() {
        return spid;
    }

    public void setSpid(String spid) {
        this.spid = spid;
    }

    /**
     * 重置每日状态
     */
    public void resetDaily() {
        this.player.setDailyUpdateMark(DateUtil.formatDate(System.currentTimeMillis()));


        this.getNCopyManager().reset();
        this.getNRiChangManager().reset();
        //重置每日任务状态
        this.getMissionManager().reset();
        //重置公会BOSS次数
        this.player.setGangBossCount((byte) 0);
        //重置每日数据
        this.player.resetDayData();
        //回收在线玩家威望
        recoveryOnlineWeiWang();
        //重置秘境BOSS的挑战次数
        this.player.setMysteryBossLeft((short) 3);
        //重置副本状态
        this.getDungeonManager().reset();
        //初始化重置后的任务
        this.getMissionManager().init();

        //重置天梯数据
        this.getLadderManager().resetLadder();

        //重置渡劫数据
        this.getEscortManager().dayReset();

        //跨服周边数据
        //this.crossManager.resetCrossData();

        //重置月卡领奖时间
        this.getActivityManager().resetMonthlyCardReward();

        //重置遭遇积分
        this.getPvpManager().resetPvpInfo();

        TaskManager.getInstance().scheduleTask(ETaskType.LOGIC, new Task() {
            @Override
            public void run() {
                try {
                    new PlayerDao().resetDaily(player.getId(), DateUtil.formatDate(System.currentTimeMillis()));
                    //new DungeonDao().resetDungeon(player.getId());
                    getNCopyManager().dbSaveReset();
                    new LadderDao().dailyClear(player.getId());
                    new EscortDao().dailyClear(player.getId());
                    new CrossDao().dailyClear(player.getId());
                } catch (Exception e) {
                    logger.error("玩家：" + player.getId() + "每日重置失败." + e.getMessage());
                }
            }

            @Override
            public String name() {
                return "resetDailyTask";
            }
        });
    }

    public boolean addHeartSkill(byte id, int goodsNum) {
        Short num = player.getHeartSkillMap().get(id);
        if (num == null) {
            num = 0;
        }
        player.getHeartSkillMap().put((byte) id, (short) (num + goodsNum));
        return true;
    }

    public boolean subHeartSkill(byte id, short delNum) {
        Short num = player.getHeartSkillMap().get(id);
        if (num == null) {
            num = 0;
        }
        player.getHeartSkillMap().put((byte) id, (short) (num - delNum));
        return true;
    }

    public LianTiManager getLianTiManager() {
        if (null == _lianTiManager) {
            _lianTiManager = new LianTiManager(this);
        }
        return _lianTiManager;
    }

    /**
     * 0点内存回收在线玩家指定官阶的威望
     */
    private void recoveryOnlineWeiWang() {
        //用户当前威望
        int weiWang = player.getWeiWang();
        //用户当前官阶
        GuanJieData guanJieData = GuanJieModel.getData(weiWang);
        //需要回收的威望值
        int income = guanJieData.getIncome();

        if (income > 0) {
            EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
            DropData data = new DropData(EGoodsType.WEIWANG, 0, income);
            this.getPackManager().useGoods(data, EGoodsChangeType.WEIWANG_CONSUM, enumSet);
        }
    }

    public PayManager getPayManager() {
        if (null == _payManager) {
            _payManager = new PayManager(this);
        }
        return _payManager;
    }

    public MonsterSiegeManager getMonsterSiegeManager() {
        if (null == _monsterSiegeManager) {
            _monsterSiegeManager = new MonsterSiegeManager(this);
        }
        return _monsterSiegeManager;
    }

    public CardManager getCardManager() {
        if (null == _cardManager) {
            _cardManager = new CardManager(this);
        }
        return _cardManager;
    }

    public CombatManager getCombatManager() {
        if (null == _combatManager) {
            _combatManager = new CombatManager(this);
        }
        return _combatManager;
    }

    public GrowManager getGrowManager() {
        if (null == _growManager) {
            _growManager = new GrowManager(this);
        }
        return _growManager;
    }
}
