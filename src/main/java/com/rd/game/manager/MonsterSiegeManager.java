package com.rd.game.manager;

import com.rd.action.GameAction;
import com.rd.bean.fight.monstersiege.PlayerMonsterData;
import com.rd.bean.fight.monstersiege.PlayerMonsterRecord;
import com.rd.common.CombineRuneSkill;
import com.rd.dao.EPlayerSaveType;
import com.rd.dao.PlayerMonsterSiegeDao;
import com.rd.define.EGoodsChangeType;
import com.rd.define.ErrorDefine;
import com.rd.game.GameMonsterSiegeService;
import com.rd.game.GameRole;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.GameEvent;
import com.rd.model.MonsterSiegeModel;
import com.rd.model.data.MonsterSiegeModelData;
import com.rd.model.data.RewardBoxModelData;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;
import org.apache.log4j.Logger;

import java.util.EnumSet;
import java.util.Map;

/**
 * 怪物攻城管理
 */
public class MonsterSiegeManager {
    private static final Logger logger = Logger.getLogger(MonsterSiegeManager.class);
    private GameRole gameRole;

    /**
     * 玩家信息
     * lazy create
     * 注：使用getPlayerData()方法获取
     **/
    private PlayerMonsterData _playerData;

    //---------------------------------------- 以下是临时的战斗状态数据 不入库 --------------------------------------------/
    private short monsterId = 0;
    /**
     * 战斗开始时间
     **/
    private long startTime = 0;
    /**
     * 合击技释放时间点
     **/
    private long skillTime = 0;
    private CombineRuneSkill combineRuneSkill;

    private PlayerMonsterSiegeDao dao = new PlayerMonsterSiegeDao();

    public MonsterSiegeManager(GameRole gameRole) {
        this.gameRole = gameRole;
        this.combineRuneSkill = new CombineRuneSkill(gameRole.getPlayer());
    }


    private void init() {
        _playerData = dao.getData(gameRole.getPlayerId());//gameRole.getPlayer().getMonsterSiege();
        if (_playerData == null) {
            _playerData = new PlayerMonsterData(gameRole.getPlayer());
            dao.insert(_playerData);
        }
    }

    public PlayerMonsterData getPlayerData() {
        if (null == _playerData) {
            init();
        }
        return _playerData;
    }

    /**
     * 数据清理
     */
    public void clear() {
        _playerData = null;
    }

    public void onEnterGame() {
        if (monsterId == 0) {
            return;
        }
        // 断线重连
        Message message = getStartMessage();
        gameRole.putMessageQueue(message);
    }

    /**
     * 获取怪物攻城信息
     *
     * @param request
     */
    public void getInfoMessage(Message request) {
        long ts = System.currentTimeMillis();
        Message message = new Message(MessageCommand.MONSTER_SIEGE_GET_INFO_MESSAGE, request.getChannel());
        GameMonsterSiegeService.getInfoMessage(message);
        getPlayerData().getMessage(message, ts);
        gameRole.sendMessage(message);
    }

    /**
     * 获取怪物攻城排行
     *
     * @param request
     */
    public void getRankListMessage(Message request) {
        Message message = GameMonsterSiegeService.getRankListMessage();
        message.setChannel(request.getChannel());
        gameRole.sendMessage(message);
    }

    /**
     * 怪物攻城宝箱领取
     *
     * @param request
     */
    public void processReceiveBox(Message request) {
        byte boxId = request.readByte();
        RewardBoxModelData modelData = MonsterSiegeModel.getMonsterBox(boxId);
        if (modelData == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        PlayerMonsterData playerData = getPlayerData();
        if (modelData.getScore() > playerData.getScore()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        if (playerData.isReceived(boxId)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        playerData.receive(boxId);
        dao.update(playerData);

        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        gameRole.getPackManager().addGoods(modelData.getRewardList(), EGoodsChangeType.MONSTER_SIEGE_BOX_ADD, enumSet);
        gameRole.savePlayer(enumSet);

        Message message = new Message(MessageCommand.MONSTER_BOX_RECEIVE_MESSAGE, request.getChannel());
        playerData.getMessage(message, System.currentTimeMillis());
        gameRole.sendMessage(message);
    }

    /**
     * 怪物攻城记录获取
     *
     * @param request
     */
    public void getRecordList(Message request) {
        Message message = new Message(MessageCommand.MONSTER_SIEGE_RECORD_MESSAGE, request.getChannel());
        getPlayerData().getRecordMessage(message);
        gameRole.sendMessage(message);
    }

    /**
     * 请求战斗
     *
     * @param request
     */
    public void processStartMessage(Message request) {
        short monsterId = request.readShort();
        MonsterSiegeModelData modelData = MonsterSiegeModel.getMonsterSiege(monsterId);
        if (modelData == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        if (modelData.getDay() != GameMonsterSiegeService.getWeekDay()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        long ts = System.currentTimeMillis();
        PlayerMonsterData playerData = getPlayerData();
        Map.Entry<Byte, Long> timesInfo = playerData.calculateAddTimes(ts);
        if (timesInfo.getKey() <= 0) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_TIMES_LESS);
            return;
        }
        short error = GameMonsterSiegeService.startBattle(gameRole, monsterId);
        if (error != ErrorDefine.ERROR_NONE) {
            gameRole.sendErrorTipMessage(request, error);
            return;
        }
        // 真实消耗
        playerData.decTimes(ts);
        dao.update(playerData);
        // 自己的记录
        this.startBattle(monsterId, ts);

        Message message = getStartMessage();
        message.setChannel(request.getChannel());
        gameRole.sendMessage(message);
    }

    private Message getStartMessage() {
        Message message = new Message(MessageCommand.MONSTER_SIEGE_START_MESSAGE);
        message.setShort(monsterId);
        int restTime = isInBattle(monsterId) ?
                (int) ((MonsterSiegeModel.BATTLE_TIME - (System.currentTimeMillis() - startTime)) / DateUtil.SECOND) :
                0;
        message.setInt(restTime < 0 ? 0 : restTime);

        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.MONSTER_SIEGE, 1, saves));
        gameRole.savePlayer(saves);
        return message;
    }

    /**
     * 退出战斗
     *
     * @param request
     */
    public void processQuitMessage(Message request) {
        // 只靠战斗开启时控制
        if (monsterId == 0) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        long currentTime = System.currentTimeMillis();
        this.quitBattle(currentTime);

        gameRole.sendTick(request);
    }

    /**
     * 退出战斗处理
     *
     * @param ts
     */
    public void quitBattle(long ts) {
        if (!isInBattle()) {
            logger.info("MonsterSiegeManager player already quit. id=" + gameRole.getPlayerId() + ", monster=" + monsterId);
            return;
        }
        short monsterId = this.monsterId;
        quitBattle();
        PlayerMonsterData playerData = getPlayerData();
        PlayerMonsterRecord record = GameMonsterSiegeService.endBattle(gameRole.getPlayerId(), monsterId, ts);
        if (record == null) {
            return;
        }
        playerData.addRecord(record);
        dao.update(playerData);

        Message message = new Message(MessageCommand.MONSTER_SIEGE_QUIT_MESSAGE);
        message.setInt(record.getDmg());
        gameRole.putMessageQueue(message);
    }

    /**
     * 以此方法作为结算战斗的唯一出口
     *
     * @return
     */
    private void quitBattle() {
        logger.info("玩家id=" + getPlayerData().getId() + "退出攻城怪物战斗id=" + monsterId);
        this.monsterId = 0;
        this.startTime = 0;
        this.skillTime = 0;
    }

    private void startBattle(short monsterId, long ts) {
        this.monsterId = monsterId;
        this.startTime = ts;
        this.skillTime = 0;
        this.combineRuneSkill.init();
        logger.info("玩家id=" + getPlayerData().getId() + "开始攻击攻城怪物id=" + monsterId);
    }

    public boolean isInBattle() {
        return monsterId != 0;
    }

    public boolean isInBattle(short monsterId) {
        return this.monsterId == monsterId;
    }

    /**
     * 怪物攻城
     * 主动使用合击技
     *
     * @param request
     */
    public void processFightMessage(Message request) {
        if (this.skillTime != 0) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        setSkillTime(System.currentTimeMillis());
        gameRole.sendTick(request);
    }

    /**
     * 更新战斗状态
     *
     * @param ts
     * @return 是否战斗中
     */
    public boolean updateBattle(final long ts) {
        if (startTime + MonsterSiegeModel.BATTLE_TIME < ts) {
            logger.info(startTime + " + " + MonsterSiegeModel.BATTLE_TIME + " >= " + ts);
            // 战斗超时
            GameAction.submit(gameRole.getPlayerId(), () -> {
                // 玩家退出状态不影响monster状态 只影响奖励
                // 可能导致奖励延迟发放 影响不大
                quitBattle(ts);
            });
            return false;
        }
        return true;
    }

    public long getSkillTime() {
        return skillTime;
    }

    public void setSkillTime(long skillTime) {
        this.skillTime = skillTime;
    }

    public int getCombineSkillDamage() {
        if (combineRuneSkill.isHave()) {
            return combineRuneSkill.launch();
        }
        return 0;
    }
}
