package com.rd.game;

import com.rd.bean.chat.ChatPlayer;
import com.rd.bean.player.Player;
import com.rd.bean.player.SimplePlayer;
import com.rd.common.BossService;
import com.rd.common.ChatService;
import com.rd.common.GameCommon;
import com.rd.dao.EPlayerSaveType;
import com.rd.dao.PlayerDao;
import com.rd.define.ErrorDefine;
import com.rd.define.GameDefine;
import com.rd.game.data.WorldDataManager;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.GameEvent;
import com.rd.game.event.ISimplePlayerListener;
import com.lg.bean.game.CreatePlayer;
import com.rd.model.WordSensitiveModel;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import com.rd.net.message.MessageArray;
import com.rd.util.DateUtil;
import com.rd.util.LogUtil;
import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names;
import org.jboss.netty.handler.codec.http.HttpHeaders.Values;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

import java.time.DayOfWeek;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GameWorld {
    private static Logger log = Logger.getLogger(GameWorld.class);

    /**
     * 登录玩家
     **/
    private Map<String, Integer> logonRole;

    //在线玩家
    private Map<Integer, GameRole> onlineRole;

    //离线玩家   优化
    private Map<Integer, Player> offlinePlayer;

    private AtomicInteger idGenerator;

    private AtomicInteger loginCode = new AtomicInteger();

    private List<ISimplePlayerListener> simplePlayerListener = new ArrayList<>();

    /**
     * 禁止的消息号
     */
    private Set<Short> banCmds = new HashSet<>();

    private WorldDataManager worldDataManager = new WorldDataManager();

    //服务器关闭
    private volatile boolean close = false;

    private static GameWorld instance = new GameWorld();

//	private Map<String,BeatData> beatLevelMap;
//	private Map<String,BeatData> beatFightingMap;
//	private Map<String,BeatData> beatLoginMap;

    public static GameWorld getPtr() {
        return instance;
    }

    private GameWorld() {
        this.logonRole = new ConcurrentHashMap<>();
        this.onlineRole = new ConcurrentHashMap<>();
        this.offlinePlayer = new ConcurrentHashMap<>();
    }

    public void init() {
        int serverId = GameDefine.getServerId();
        int idLow = GameDefine.getIdLow(serverId);
        int idHigh = GameDefine.getIdHigh(serverId);
        int maxId = new PlayerDao().getPlayerMaxId(idLow, idHigh);
        this.idGenerator = new AtomicInteger(maxId == 0 ? idLow : maxId);
        log.info("GameWorld.initIdGenerator() generator=" + idGenerator.get());

        this.worldDataManager.init();

        //加载封测发奖数据
//		PlayerDao playerDao=new PlayerDao();
//		beatLevelMap=playerDao.getBeatLevel();
//		beatFightingMap=playerDao.getBeatFight();
//		beatLoginMap=playerDao.getBeatLogin();
    }

    /**
     * 生成玩家id
     *
     * @return
     */
    private int generatePlayerId() {
        return idGenerator.incrementAndGet();
    }

    /**
     * 登录游戏
     * 用户在login服登录后通过account、channel、channelChild读取User数据，加入loginCode策略放在【登录服】的缓存中。
     * 向客户端回送userId和loginCode，客户端凭借这俩值登录游戏服。
     * 在向游戏服发起登录请求时，游戏服通过登录服校验userId和loginCode，
     * 校验通过后，在游戏服缓存userId+serverId和Player数据的对应关系。登录成功。
     * 活跃度高的玩家不用每次登录都去数据库load数据。
     * 这样把Player数据表中的account相关信息剥离出来，减少Player大数据表的索引列。
     *
     * @param request
     */
    public void loginGame(Message request) {
        short channelType = request.readShort();
        short subChannel = request.readShort();
        String account = request.readString();
        short serverId = request.readShort();
        byte platform = request.readByte();
        String ad = request.readString();
        String spid = request.readString();

        log.info("account=" + account + ",channelType=" + channelType + ",serverId=" + serverId + ",platform=" + platform);

        String loginKey = this.getLoginKey(channelType, subChannel, account, serverId, platform);
        Integer roleId = logonRole.get(loginKey);
        if (roleId != null) {
            GameRole role = this.getOnlineRole(roleId);
            if (role != null) {
                role.enterGame(request, false);
            } else {
                logonRole.remove(loginKey);
                this.offlineLogin(request, channelType, subChannel, account, serverId, platform, ad, spid);
            }
        } else {
            this.offlineLogin(request, channelType, subChannel, account, serverId, platform, ad, spid);
        }
    }

    private void offlineLogin(Message request, short channel, short subChannel, String account, short serverId, byte platform, String ad, String spid) {
        Player player = new PlayerDao().getPlayer(account, channel, subChannel, serverId, platform);
        if (player == null) {
            Message message = new Message(MessageCommand.CREATE_ROLE_MESSAGE, request.getChannel());
            sendMessage(message);
        } else {
            this.enterGame(player, ad, spid, request, false);
        }
    }

    public void kickOff(int playerId) {
        try {
            GameRole role = this.onlineRole.remove(playerId);
            Player player = null;
            if (role != null) {
                player = role.getPlayer();
            } else {
                player = this.offlinePlayer.remove(playerId);
            }
            if (player != null) {
                player.setLastLogoutTime(System.currentTimeMillis());
                EnumSet<EPlayerSaveType> enumSet = EnumSet.allOf(EPlayerSaveType.class);
                new PlayerDao().savePlayer(player, enumSet);
            }
        } catch (Exception e) {
            log.error("保存角色离线数据时发生异常！", e);
        }
    }

    public void createRole(Message request) {
        short channelType = request.readShort();
        short subChannel = request.readShort();
        String account = request.readString();
        short serverId = request.readShort();
        byte platform = request.readByte();
        String name = request.readString();
        byte occupation = request.readByte();
        String ad = request.readString();
        String spid = request.readString();

        if (platform < 0 || platform > 2) {
            // 定义提到common层 简单控制角色数量
            sendErrorTips(request.getChannel(), ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        log.info("account=" + account + " channelType=" + channelType + " serverId=" + serverId + " platform=" + platform + " name=" + name);
        if (!GameCommon.checkReservedWord(name)) {
            sendErrorTips(request.getChannel(), ErrorDefine.ERROR_INVALID_STRING);
            return;
        }
        if (!WordSensitiveModel.checkSensitive(name)) {
            sendErrorTips(request.getChannel(), ErrorDefine.ERROR_INVALID_STRING);
            return;
        }
        if (account == null || account.length() == 0) {
            sendErrorTips(request.getChannel(), ErrorDefine.ERROR_INVALID_STRING);
            return;
        }

        if (name.length() == 0 || name.length() > GameDefine.NAME_LENGTH_LIMIT) {
            sendErrorTips(request.getChannel(), ErrorDefine.ERROR_STRING_LENGTH_LIMIT);
            return;
        }
        // format name
        name = GameCommon.getFormatName(name, serverId);
        PlayerDao playerDao = new PlayerDao();
        String playerName = playerDao.getCreatedPlayer(account, channelType, serverId, name);
        if (playerName != null) {
            log.error(playerName + "========================");
            if (playerName.equals(name)) {
                sendErrorTips(request.getChannel(), ErrorDefine.ERROR_NAME_DUPLICATE);
                return;
            } else {
                sendErrorTips(request.getChannel(), ErrorDefine.ERROR_INVALID_STRING);
                return;
            }
        }

        long currentTime = System.currentTimeMillis();
        Player player = new Player();
        player.setId(generatePlayerId());
        player.setCreateTime(currentTime);
        player.setLastLoginTime(currentTime);
        player.setLastLogoutTime(currentTime);
        player.setFightRequestTime(currentTime - 1000);
        player.setDailyUpdateMark(DateUtil.formatDate(currentTime));
        player.initialPlayer(account, channelType, subChannel, serverId, platform, name, occupation);
        /**创建新用户，设置秘境BOSS的默认挑战次数**/
        player.setMysteryBossLeft(BossService.mysteryBossLeft);
//		player.initFive();
        boolean isSuccess = playerDao.createPlayer(player);
        if (!isSuccess) {
            sendErrorTips(request.getChannel(), ErrorDefine.ERROR_INVALID_STRING);
            return;
        }
        //发放封测奖励
        //FengCeModel.sendFengCeReward(account, player.getId());

        //新注册用户，发送邮件奖励
//		RoleData  roleData = RoleModel.getRoleData(player.getLevel());
//		for (short mailId: roleData.getMailList()) {
//			MailRewardModelData data = MailModel.getMailRewardModelData(mailId);
//			if (data != null) {
//				Mail mail = MailService.createMail(data, EGoodsChangeType.REGISTER_MAILREWARD);
//				MailService.sendSystemMail(player.getId(), mail);
//			}
//		}

        this.enterGame(player, ad, spid, request, true);
        //this.enterGameBeat(player,request);

        //记录玩家建角日志
        LogUtil.log(player, new CreatePlayer(DateUtil.formatDateTime(player.getCreateTime())));
    }

//	public void enterGameBeat(Player player, Message request)
//	{
//		GameRole gameRole=getOnlineRole(player.getId());
//		if(gameRole==null){
//			gameRole=new GameRole(player);
//			gameRole.init();
//			this.logonRole.put(this.getLoginKey(player.getChannel(), player.getSubChannel(), player.getAccount(),player.getServerId()), player.getId());
//			this.onlineRole.put(player.getId(), gameRole);
//			
//			//通知有玩家数据更新
//			notifySimplePlayerEvent(player);
//		}
//		
//		BeatData beatData;
//		String beatKey=player.getChannel()+"_"+player.getAccount();
//		if(beatLevelMap.containsKey(beatKey)){
//			beatData=beatLevelMap.get(beatKey);
//			Mail mail=MailService.createMail(beatData.getTitle(), beatData.getContent(), GameDefine.MAIL_TYPE_SYSTEM, new DropData(EGoodsType.GOLD,0,beatData.getNum()));
//			gameRole.getDbManager().mailDao.insertMail(gameRole.getPlayerId(), mail);
//			gameRole.getMailManager().addMail(mail);
//			gameRole.getDbManager().playerDao.updateBeatLevel(player.getChannel(), player.getAccount());
//			beatLevelMap.remove(beatKey);
//		}
//		if(beatFightingMap.containsKey(beatKey)){
//			beatData=beatFightingMap.get(beatKey);
//			Mail mail=MailService.createMail(beatData.getTitle(), beatData.getContent(), GameDefine.MAIL_TYPE_SYSTEM, new DropData(EGoodsType.GOLD,0,beatData.getNum()));
//			gameRole.getDbManager().mailDao.insertMail(gameRole.getPlayerId(), mail);
//			gameRole.getMailManager().addMail(mail);
//			gameRole.getDbManager().playerDao.updateBeatFight(player.getChannel(), player.getAccount());
//			beatLevelMap.remove(beatKey);
//		}
//		if(beatLoginMap.containsKey(beatKey)){
//			beatData=beatLoginMap.get(beatKey);
//			Mail mail=MailService.createMail("封测登录大回馈", "感谢您在封测期间积极参与，连续登陆达到了5天，公测如约为您奉上500绑元和VIP经验卡！", GameDefine.MAIL_TYPE_SYSTEM, new DropData(EGoodsType.GOLD,0,500),new DropData(EGoodsType.BOX,22,1));
//			gameRole.getDbManager().mailDao.insertMail(gameRole.getPlayerId(), mail);
//			gameRole.getMailManager().addMail(mail);
//			gameRole.getDbManager().playerDao.updateBeatLogin(player.getChannel(), player.getAccount());
//			beatLevelMap.remove(beatKey);
//		}
//		
//		gameRole.enterGame(request);
//	}


    public void enterGame(Player player, String ad, String spid, Message request, boolean isNew) {
        GameRole gameRole = getOnlineRole(player.getId());
        if (gameRole == null) {
            gameRole = new GameRole(player);
            gameRole.init();
            gameRole.setAd(ad);
            gameRole.setSpid(spid);
            this.logonRole.put(this.getLoginKey(player.getChannel(), player.getSubChannel(), player.getAccount(), player.getServerId(), player.getPlatform()), player.getId());
            this.onlineRole.put(player.getId(), gameRole);
            if (player.getState() == GameDefine.PLAYER_STATE_NORMAL) {
                //GameRankManager.getInstance().addRank(player);
            }
            //通知有玩家数据更新
            notifySimplePlayerEvent(player);
        }
        gameRole.enterGame(request, isNew);
    }

    public void resetLadderCache() {
        for (GameRole role : onlineRole.values()) {
            role.getLadderManager().resetLadder();
        }
    }

    public GameRole getOnlineRole(int playerId) {
        return onlineRole.get(playerId);
    }

    public Map<Integer, GameRole> getOnlineRoles() {
        return onlineRole;
    }

    public Player getOfflinePlayer(int playerId) {
        return offlinePlayer.get(playerId);
    }

    public Map<Integer, Player> getOfflinePlayer() {
        return offlinePlayer;
    }

    /**
     * 得到游戏角色
     *
     * @param playerId
     * @return
     */
    public IGameRole getGameRole(int playerId) {
        //优先从在线玩家中提取
        //其次从离线玩家中提取
        //再次查库并存入离线玩家数据
        GameRole role = getOnlineRole(playerId);
        if (role != null) {
            return role;
        }
        Player player = getOfflinePlayer(playerId);
        if (player != null) {
            return player;
        }
        player = new PlayerDao().getPlayer(playerId);
        if (player != null) {
            addOfflinePlayer(player);
            return player;
        }
        return null;
    }

    /**
     * 心跳任务
     */
    public void onTick() {
        //所有在线玩家
        for (GameRole role : onlineRole.values()) {
            if (role == null)
                continue;
            //广播
            ChatService.onTick(role);
        }
        //清除推送过的广播
        ChatService.clearDirty();
    }

    public void sendErrorTips(Channel channel, short errorCode) {
        Message message = new Message(MessageCommand.ERROR_TIP_MESSAGE, channel);
        message.setShort(errorCode);
        message.setShort((short) 0);
        sendMessage(message);
    }

    public void sendMessage(Message msg) {
        MessageArray msgs = new MessageArray(msg);
        msgs.pack();
        sendMessage(msgs.getChannel(), msgs.getBuf());
    }

    public void sendMessage(Channel channel, ChannelBuffer buffer) {
        //模拟数据不发送消息，直接退出
        if (channel == null) {
            log.error("GameWorld.sendMessage() failed with unexpected channel=" + channel);
            return;
        }
        if (!channel.isConnected()) {
            log.error("GameWorld.sendMessage() failed with closed channel=" + channel);
            return;
        }
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.setContent(buffer);
        response.headers().set(Names.CONTENT_TYPE, "text/plain");
        response.headers().set(Names.CONTENT_LENGTH, buffer.readableBytes());
        response.headers().set(Names.CONNECTION, Values.CLOSE);
        response.headers().set(Names.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        try {
            channel.write(response);
        } catch (Exception e) {
        }
    }

    public void broadcast(Message msg) {
        for (GameRole role : onlineRole.values()) {

            role.putMessageQueue(msg);
        }
    }

    public void broadcast(Message msg, ChatPlayer fromPlayer) {
        for (GameRole role : onlineRole.values()) {
            role.putMessageQueue(msg);
        }
    }

    private String getLoginKey(short channel, short subChannel, String account, short serverId, byte platform) {
        return "" + channel + subChannel + account + serverId + platform;
    }

    public short getLoginCode() {
        return (short) this.loginCode.incrementAndGet();
    }

    public void addOfflinePlayer(Player player) {
        player.setLastLogoutTime(System.currentTimeMillis());
        offlinePlayer.put(player.getId(), player);
    }

    public void addOfflinePlayer(Player player, long currentTime) {
        player.setLastLogoutTime(currentTime);
        offlinePlayer.put(player.getId(), player);
    }

    public void removeOfflinePlayer(int playerId) {
        offlinePlayer.remove(playerId);
    }

    /**
     * 重置在线玩家每日状态
     */
    public void resetDailyState() {
        int weekDay = DateUtil.getWeekDay();
        for (GameRole role : onlineRole.values()) {
            try {
                role.resetDaily();
                if (weekDay == DayOfWeek.MONDAY.getValue()) {
                    // 每周清理
                    role.getMonsterSiegeManager().clear();
                }
                // 重新发送登录事件
                GameEvent event = EGameEventType.ENTER_GAME.create(role, 1, null);
                role.getEventManager().notifyEvent(event);
                // FIXME ..
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            role.getPlayer().getCardMission().updateMission();
            role.putMessageQueue(role.getMissionManager().getCardMissionMessage());
        }
    }

    /**
     * 更新会话
     *
     * @param currentTime
     */
    public void updateSession(long currentTime) {
        //如果离线数据数量大于最小值，则进行超时判定
        if (this.offlinePlayer.size() > GameDefine.OFFLINE_MIN_NUM) {
            for (Player player : this.offlinePlayer.values()) {
                //如果离线玩家数据超时，则移除离线玩家数据
                if (currentTime - player.getLastLogoutTime() > GameDefine.OFFLINE_TIMEOUT) {
                    this.offlinePlayer.remove(player.getId());
                }
            }
        }
        //如果离线数据数量大于最大值，移除多余离线数据
        if (this.offlinePlayer.size() > GameDefine.OFFLINE_MAX_NUM) {
            int removeNum = this.offlinePlayer.size() - GameDefine.OFFLINE_MAX_NUM;
            Iterator<Player> it = this.offlinePlayer.values().iterator();
            while (it.hasNext()) {
                Player player = it.next();
                this.offlinePlayer.remove(player.getId());
                --removeNum;
                if (removeNum == 0) {
                    break;
                }
            }
        }
        for (GameRole role : this.onlineRole.values()) {
            //如果在线玩家超时，则移除在线玩家到离线玩家，否则更新
            if (currentTime - role.getLastTickTime() > GameDefine.ONLINE_TIMEOUT) {
                this.addOfflinePlayer(role.getPlayer());
                this.onlineRole.remove(role.getPlayerId());
                role.logout(currentTime);
            }
            //心跳超时
            //else if (currentTime - role.getLastBeatTime() > BeatManager.BEAT_TIME_OUT) {
            //	if (currentTime - role.getLastTickTime() < BeatManager.BEAT_TIME_OUT) {
            //		role.getBeatManager().addCheat();
            //	}
            //}
            else {
                role.updateMinute(currentTime);
            }
        }
    }

    public void notifySimplePlayerEvent(SimplePlayer simplePlayer) {
        for (ISimplePlayerListener listener : simplePlayerListener) {
            listener.updateSingleHandler(simplePlayer);
        }
    }

    public void addSimplePlayerListener(ISimplePlayerListener listener) {
        if (!simplePlayerListener.contains(listener)) {
            simplePlayerListener.add(listener);
        }
    }

    public void saveOnlinePlayerData() {
        long currentTime = System.currentTimeMillis();
        for (GameRole role : this.onlineRole.values()) {
            role.logout(currentTime);
        }
    }

    public boolean isClose() {
        return close;
    }

    public void setClose(boolean close) {
        this.close = close;
    }

    public void addBanCmd(short cmdId) {
        this.banCmds.add(cmdId);
    }

    public void removeBanCmd(short cmdId) {
        this.banCmds.remove(cmdId);
    }

    public boolean containBanCmd(short cmdId) {
        return this.banCmds.contains(cmdId);
    }

    public WorldDataManager getWorldDataManager() {
        return worldDataManager;
    }
}
