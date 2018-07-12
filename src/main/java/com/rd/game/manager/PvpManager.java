package com.rd.game.manager;

import com.rd.bean.drop.DropData;
import com.rd.bean.player.Player;
import com.rd.bean.player.SimplePlayer;
import com.rd.bean.pvp.PvpInfo;
import com.rd.bean.pvp.PvpRecord;
import com.rd.common.GameCommon;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.*;
import com.rd.game.GamePvpManager;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.game.IGameRole;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.GameEvent;
import com.rd.game.event.IEventListener;
import com.rd.model.GoodsModel;
import com.rd.model.MapModel;
import com.rd.model.data.MapStageRewardData;
import com.rd.net.MessageCommand;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;
import org.jboss.netty.channel.Channel;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class PvpManager implements IEventListener {

    private GameRole gameRole;
    private Player player;

    private PvpInfo pvpInfo;
    private byte currentIndex = -1;

    public PvpManager(GameRole gameRole) {
        this.gameRole = gameRole;
        this.player = gameRole.getPlayer();
        this.checkPvpOpen();
    }

    private void checkPvpOpen() {
        if (gameRole.getFightManager().getClearanceStage() < PvpDefine.PVP_CHALLENGE_MAP_STAGE) {
            return;
        }
        if (pvpInfo != null) {
            return;
        }
        pvpInfo = gameRole.getDbManager().pvpDao.getPlayerPvpInfo(gameRole.getPlayerId());
        if (pvpInfo == null) {
            createPvpInfo();
        }
        updateChallengers(System.currentTimeMillis());
    }

    private void createPvpInfo() {
        pvpInfo = new PvpInfo(player);
        gameRole.getDbManager().pvpDao.createPlayerPvpInfo(pvpInfo);
    }

    /**
     * 刷新挑战者
     *
     * @param currentTime
     */
    private void updateChallengers(long currentTime) {
        if (pvpInfo.isFullChallengers()) {
            return;
        }
        int times;
        if (pvpInfo.getLastUpdateTime() == -1) {
            times = PvpDefine.CHALLENGER_COUNT;
        } else {
            long timeInterval = currentTime - pvpInfo.getLastUpdateTime();
            times = (int) (timeInterval / PvpDefine.CHALLENGER_UPDATE_INTERVAL_TIME);
            if (times == 0) {
                return;
            }
            if (times > PvpDefine.CHALLENGER_COUNT) {
                times = PvpDefine.CHALLENGER_COUNT;
            }
        }
        for (int i = 0; i < times; i++) {
            SimplePlayer challenger = getRandomChallenger();
            pvpInfo.getChallengerList().add(challenger);
            if (pvpInfo.isFullChallengers()) {
                break;
            }
        }
        pvpInfo.setLastUpdateTime(currentTime);
        gameRole.getDbManager().pvpDao.updateChallengers(pvpInfo);
    }

    private SimplePlayer getRandomChallenger() {
        int[] range = {50, 80, 100, 110};
        long fighting = player.getFighting();
        int rank = GameCommon.getRandomIndex(5000, 2500, 2500);
        // 三个档位
        int flow = (int) (fighting * range[rank] / 100.f);
        int fhigh = (int) (fighting * range[rank + 1] / 100.f);

        List<Integer> excludeList = new ArrayList<>();
        excludeList.add(gameRole.getPlayerId());
        for (SimplePlayer challenger : pvpInfo.getChallengerList()) {
            if (challenger.getId() == -1) {
                continue;
            }
            excludeList.add(challenger.getId());
        }
        String comp = "level";
        if (player.getRein() > 0) {
            comp = "rein";
            flow = player.getRein() - 1 > 0 ? player.getRein() - 1 : 1;
            fhigh = player.getRein() + 1;
        } else {
            flow = player.getLevel() - 10 > 0 ? player.getLevel() - 10 : 1;
            fhigh = player.getLevel() + 10;
        }
        SimplePlayer target = gameRole.getDbManager().playerDao.getRandomPlayerByFighting(excludeList, flow, fhigh, comp);
        if (target == null || GameWorld.getPtr().getGameRole(target.getId()).getPlayer().getState() != GameDefine.PLAYER_STATE_NORMAL) {
            // 没有只能打机器人
            target = new SimplePlayer();
            target.setId(-1);
            target.setName(GameCommon.getRandomName());
            target.setLevel(player.getLevel());
            target.setRein(player.getRein());
            target.setFighting((long) (fighting * 0.9f));
        }
        return target;
    }

    /**
     * 2801 野外pvp信息
     *
     * @param request
     */
    public void processInfo(Message request) {
        if (pvpInfo == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        long currentTime = System.currentTimeMillis();
        updateChallengers(currentTime);

        sendChallengersMessage(request.getChannel(), currentTime);
    }

    private void sendChallengersMessage(Channel channel, long currentTime) {
        Message message = getChallengersMessage(currentTime);
        message.setChannel(channel);
        gameRole.sendMessage(message);
    }

    public Message getChallengersMessage(long currentTime) {
        Message message = new Message(MessageCommand.FIELD_PVP_INFO_MESSAGE);
        message.setInt(pvpInfo.getPrestige());
        message.setShort(GamePvpManager.getInstance().getRankByPlayerId(player.getId()));
        long restTime = (pvpInfo.getLastUpdateTime() == -1) ?
                PvpDefine.CHALLENGER_UPDATE_INTERVAL_TIME :
                pvpInfo.getLastUpdateTime() + PvpDefine.CHALLENGER_UPDATE_INTERVAL_TIME - currentTime;
        message.setInt((int) (restTime < 0 ? 0 : restTime));
        message.setByte(pvpInfo.getChallengerList().size());
        for (int i = 0; i < pvpInfo.getChallengerList().size(); ++i) {
            SimplePlayer challenger = pvpInfo.getChallengerList().get(i);
            challenger.getBaseSimpleMessage(message);
            if (challenger.getId() != -1) {
                IGameRole role = GameWorld.getPtr().getGameRole(challenger.getId());
                if (role == null)
                    continue;
                role.getPlayer().getAppearMessage(message);
            }
        }
        message.setByte(player.getDayData().getFieldPvpRewardNum());
        return message;
    }

    /**
     * 2802 野外pvp寻找挑战者
     *
     * @param request
     */
    public void processSearch(Message request) {
        if (pvpInfo == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        long currentTime = System.currentTimeMillis();
        updateChallengers(currentTime);
        if (pvpInfo.isFullChallengers()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        if (!gameRole.getPackManager().useGoods(new DropData(EGoodsType.DIAMOND, 0, PvpDefine.SEARCH_CHALLENGE_DIAMOND), EGoodsChangeType.FIELD_PVP_CONSUME, enumSet)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }

        pvpInfo.setLastUpdateTime(currentTime);

        SimplePlayer challenger = getRandomChallenger();
        pvpInfo.getChallengerList().add(challenger);
        gameRole.getDbManager().pvpDao.updateChallengers(pvpInfo);

        sendChallengersMessage(request.getChannel(), currentTime);

        gameRole.savePlayer(enumSet);
    }

    /**
     * 2803 野外pvp排行榜
     *
     * @param request
     */
    public void processRank(Message request) {
        Message message = GamePvpManager.getInstance().getRankMessage(player.getId());
        message.setChannel(request.getChannel());
        gameRole.sendMessage(message);
    }

    /**
     * 2804 野外pvp战斗记录
     *
     * @param request
     */
    public void processRecord(Message request) {
        Message message = new Message(MessageCommand.FIELD_PVP_RECORD_MESSAGE, request.getChannel());
        message.setByte(pvpInfo.getRecordList().size());
        for (PvpRecord record : pvpInfo.getRecordList()) {
            record.getMessage(message);
        }
        gameRole.sendMessage(message);
    }

    /**
     * 2805 野外pvp战斗请求
     *
     * @param request
     */
    public void processRequest(Message request) {
        if (pvpInfo == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        if (gameRole.getFightManager().inInstance()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_COPY_ALREADY_IN);
            return;
        }
        byte index = request.readByte();
        if (pvpInfo.getChallengerList().size() <= index) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        SimplePlayer challenger = pvpInfo.getChallengerList().get(index);
        if (challenger == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        this.currentIndex = index;

        //通知遭遇战消息
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.FIELD_PVP, 1, enumSet));
        gameRole.savePlayer(enumSet);

        Message message = new Message(MessageCommand.FIELD_PVP_REQUEST_MESSAGE, request.getChannel());
        message.setBool(true);
        if (challenger.getId() == -1) {
            message.setBool(true);
            message.setInt(index);
            message.setString(challenger.getName());
            message.setLong(challenger.getFighting());
        } else {
            message.setBool(false);
            IGameRole targetPlayer = GameWorld.getPtr().getGameRole(challenger.getId());
            targetPlayer.getPlayer().updateFighting();//TODO:暂时先这么写
            targetPlayer.getPlayer().getBaseSimpleMessage(message);
            targetPlayer.getPlayer().getAppearMessage(message);
            targetPlayer.getPlayer().getAttrFighting(message);
            targetPlayer.getPlayer().getSkillMessage(message);
        }
        gameRole.sendMessage(message);
    }

    /**
     * 2806 野外pvp战斗结果
     *
     * @param request
     */
    public void processResult(Message request) {
        if (pvpInfo == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        byte result = request.readByte();
        SimplePlayer challenger = pvpInfo.getChallengerList().remove(this.currentIndex);
        if (challenger == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        pvpInfo.setLastUpdateTime(System.currentTimeMillis());

        MapStageRewardData mapStageRewardData = MapModel.getMapStageRewardDataById(player.getMapStageId());

        byte meltSoulStoneNum = 0;
        short exp = 0;
        short gold = 0;
        byte prestige = 0;
        byte honor = 0;
        short weiwang = 0;
        List<DropData> rewardList = new ArrayList<>();

        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        if (result == FightDefine.FIGHT_RESULT_SUCCESS) {
            honor = 13;
            weiwang = 133;
            meltSoulStoneNum = 4;

            pvpInfo.addPrestige(25 + pvpInfo.getStreakWin());
            pvpInfo.addStreakWin();
            if (player.getDayData().getFieldPvpRewardNum() < PvpDefine.DAY_REWARD_NUM) {
                gameRole.getPackManager().addGoods(new DropData(EGoodsType.HONOR, 0, honor), EGoodsChangeType.FIELD_PVP_ADD, enumSet);
                gameRole.getPackManager().addGoods(new DropData(EGoodsType.WEIWANG, 0, weiwang), EGoodsChangeType.FIELD_PVP_ADD, enumSet);
            } else {
                honor = 0;
                weiwang = 0;
                meltSoulStoneNum = 0;
            }
            gameRole.getPackManager().addGoods(new DropData(EGoodsType.ITEM, GoodsDefine.ITEM_ID_MELT_SOUL_STONE, 10), EGoodsChangeType.FIELD_PVP_ADD, enumSet);
            gameRole.getPackManager().addGoods(new DropData(EGoodsType.EXP, 0, mapStageRewardData.getMonsterExp() * 15), EGoodsChangeType.FIELD_PVP_ADD, enumSet);
            gameRole.getPackManager().addGoods(new DropData(EGoodsType.GOLD, 0, mapStageRewardData.getMonsterGold() * 150), EGoodsChangeType.FIELD_PVP_ADD, enumSet);

            for (int j = 0; j < (Math.random() * 6 + 4); ++j) {
                byte occupation = GameCommon.getRandomEquipOccupation();
                byte equipPos = GameCommon.getRandomEquipPosition();
                short equipLevel = GameCommon.getDropEquipLevel(player.getLevel());
                byte quality = (byte) GameCommon.getRandomIndex(3000, 4000, 3000);

                DropData equipData = new DropData();
                equipData.setT(EGoodsType.EQUIP.getId());
                equipData.setG(GoodsModel.getEquipId(equipLevel, occupation, equipPos));
                equipData.setQ(quality);
                equipData.setN(1);
                gameRole.getPackManager().addGoods(equipData, EGoodsChangeType.FIELD_PVP_ADD, enumSet);

                rewardList.add(equipData);
            }

            PvpRecord record = new PvpRecord();
            record.setName(challenger.getName());
            record.setResult(result);
            record.setTime(DateUtil.formatDateTime(System.currentTimeMillis()));
            record.setMeltSoulStone(meltSoulStoneNum);
            record.setExp((short) (mapStageRewardData.getMonsterExp() * 15));
            record.setGold((short) (mapStageRewardData.getMonsterGold() * 150));
            record.setPrestige((byte) (25 + pvpInfo.getStreakWin() - 1));
            pvpInfo.addPvpRecord(record);

            exp = (short) (mapStageRewardData.getMonsterExp() * 15);
            gold = (short) (mapStageRewardData.getMonsterGold() * 150);
            prestige = (byte) (25 + pvpInfo.getStreakWin() - 1);
        } else {
            honor = 0;
            weiwang = 100;
            meltSoulStoneNum = 1;
            //pvpInfo.addPrestige(10); 败者不给了
            pvpInfo.setStreakWin((byte) 0);
            if (player.getDayData().getFieldPvpRewardNum() < PvpDefine.DAY_REWARD_NUM) {
                gameRole.getPackManager().addGoods(new DropData(EGoodsType.WEIWANG, 0, weiwang), EGoodsChangeType.FIELD_PVP_ADD, enumSet);
            } else {
                honor = 0;
                weiwang = 0;
                meltSoulStoneNum = 0;
            }
            gameRole.getPackManager().addGoods(new DropData(EGoodsType.ITEM, GoodsDefine.ITEM_ID_MELT_SOUL_STONE, 5), EGoodsChangeType.FIELD_PVP_ADD, enumSet);
            gameRole.getPackManager().addGoods(new DropData(EGoodsType.EXP, 0, mapStageRewardData.getMonsterExp() * 8), EGoodsChangeType.FIELD_PVP_ADD, enumSet);
            gameRole.getPackManager().addGoods(new DropData(EGoodsType.GOLD, 0, mapStageRewardData.getMonsterGold() * 80), EGoodsChangeType.FIELD_PVP_ADD, enumSet);

            PvpRecord record = new PvpRecord();
            record.setName(challenger.getName());
            record.setResult(result);
            record.setTime(DateUtil.formatDateTime(System.currentTimeMillis()));
            record.setMeltSoulStone(meltSoulStoneNum);
            record.setExp((short) (mapStageRewardData.getMonsterExp() * 8));
            record.setGold((short) (mapStageRewardData.getMonsterGold() * 80));
            record.setPrestige((byte) 0);
            pvpInfo.addPvpRecord(record);

            exp = (short) (mapStageRewardData.getMonsterExp() * 8);
            gold = (short) (mapStageRewardData.getMonsterGold() * 80);
            prestige = 0;
        }
        if (player.getDayData().getFieldPvpRewardNum() < PvpDefine.DAY_REWARD_NUM) {
            player.getDayData().setFieldPvpRewardNum((byte) (player.getDayData().getFieldPvpRewardNum() + 1));
            enumSet.add(EPlayerSaveType.DAYDATA);
        }

        Message message = new Message(MessageCommand.FIELD_PVP_RESULT_MESSAGE, request.getChannel());
        message.setByte(result);
        message.setByte(meltSoulStoneNum);
        message.setShort(exp);
        message.setShort(gold);
        message.setByte(prestige);
        message.setByte(rewardList.size());
        for (DropData data : rewardList) {
            message.setShort(data.getG());
            message.setByte(data.getQ());
        }
        message.setByte(honor);
        message.setShort(weiwang);
        gameRole.sendMessage(message);

        GamePvpManager.getInstance().addRank(pvpInfo);
        gameRole.savePlayer(enumSet);
        gameRole.getDbManager().pvpDao.updateResult(pvpInfo);
    }

    public void handleEvent(GameEvent event) {
        if (event.getType() == EGameEventType.MAP_PASS) {
            this.checkPvpOpen();
        }
    }

    public void resetPvpInfo() {
        if (pvpInfo != null) {
            pvpInfo.setPrestige(0);
            pvpInfo.setStreakWin((byte) 0);
        }
    }
}
