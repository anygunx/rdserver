package com.rd.game.manager;


import com.rd.bean.player.Player;
import com.rd.bean.relationship.NRelatedPlayer;
import com.rd.dao.EPlayerSaveType;
import com.rd.dao.NRelationshipDao;
import com.rd.define.ErrorDefine;
import com.rd.define.NRelationshipDefine;
import com.rd.enumeration.EMessage;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.game.IGameRole;
import com.rd.game.NGameRelationshipManager;
import com.rd.net.message.Message;
import com.rd.util.DateUtil;
import com.rd.util.GameUtil;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.rd.define.NRelationshipDefine.GUANZHU_MY_ROLE_LIMITE_NUM;

/**
 * 关注 ,粉丝 ，黑名单  管理器 主要解决的是 被关注的人涉及到是个有限制人数
 * 因此就是 在多个玩家关注的时候是有 保证有序的去关注才能保证 固定的被关注数量
 *
 * @author MyPC
 */
public class NRelationManager {
    private static final Logger logger = Logger.getLogger(NRelationManager.class.getName());
    /**
     * 不在线玩家的gameRole为null
     **/
    private GameRole gameRole;
    private Player player;

    /**
     * 各种关系的玩家map
     **/
    private LinkedHashMap<Integer, NRelatedPlayer> guanzhus = new LinkedHashMap<>();
    private LinkedHashMap<Integer, NRelatedPlayer> beiguanzhus = new LinkedHashMap<>();
    private LinkedHashMap<Integer, NRelatedPlayer> blacks = new LinkedHashMap<>();

    public NRelationManager(Player player) {
        this.player = player;

    }

    public NRelationManager(GameRole gameRole) {
        this.gameRole = gameRole;
        this.player = gameRole.getPlayer();

    }

    public void setGameRole(GameRole gameRole) {
        this.gameRole = gameRole;
    }

    /**
     * 为不在线的gameRole新建dao
     **/
    private NRelationshipDao getRelationShipDao() {
        return NRelationshipDao.getInstance();
    }

    public void init() {
        blacks = getRelationShipDao().getBlacks(player.getId());
    }

    public void initGuanZhu(LinkedHashMap<Integer, NRelatedPlayer> beiguanzhus, LinkedHashMap<Integer, NRelatedPlayer> guanzhus) {
        if (beiguanzhus == null || guanzhus == null) {
            this.beiguanzhus = getRelationShipDao().getBeiGuanZhu(player.getId());
            this.guanzhus = getRelationShipDao().getRelations(player.getId());
        } else {
            this.beiguanzhus = beiguanzhus;
            this.guanzhus = guanzhus;
        }

    }

    /**
     * 这个方法是所有玩家按顺序执行 考虑到被关注的玩家是只有70个名额
     *
     * @param otherManager
     * @param otherId
     * @param mine
     * @param request
     */
    public void guanzhu(NRelationManager otherManager, NRelatedPlayer other, NRelatedPlayer mine, Message request) {
        if (otherManager == null) {
            return;
        }
        if (!checkAddGuanZhu(other.getId(), request)) {

            return;
        }
        synchronized (getFenSi()) {
            if (otherManager.isFenSiFull()) {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PLAYER_FRIEND_MAX);
                return;
            }
            // 已加好友
            if (otherManager.isHaveFensi(player.getId())) {
                return;
            }
            addGuanZhu(other);
            otherManager.addFenSi(mine);
        }
        Message msg = new Message(EMessage.FRIEND_GUANZHU.CMD(), request.getChannel());
        msg.setByte(NRelationshipDefine.ZHUANZHU_SUCESS);
        gameRole.sendMessage(msg);
    }

    /**
     * 添加关注的玩家
     *
     * @param request
     */
    public void prossGuanzhu(Message request) {
        int otherId = request.readInt();

        if (!checkAddGuanZhu(otherId, request)) {
            return;
        }
        if (GameWorld.getPtr().getGameRole(otherId) == null) {
            return;
        }
        final NRelatedPlayer mine = new NRelatedPlayer();
        mine.init(player);
        NGameRelationshipManager.getInstance().addGuanZhu(this, mine, otherId, request);
    }

    /**
     * 取消关注
     */
    public void prossQuXiaoGuanzhu(Message request) {
        int otherId = request.readInt();
        clearGuanZhu(otherId, request);
    }

    /**
     * 取消关注
     *
     * @param otherId
     */
    private void clearGuanZhu(int otherId, Message request) {
        LinkedHashMap<Integer, NRelatedPlayer> map = getGuanZhuList();
        if (map == null || map.isEmpty()) {
            return;
        }
        if (!map.containsKey(otherId)) {
            return;
        }
        map.remove(otherId);
        NRelationManager other = NGameRelationshipManager.getInstance().getNRelationshipManager(otherId);
        if (other == null) {
            return;
        }

        other.clearFenSiByPlayerId(player.getId());

        Message msg = new Message(EMessage.FRIEND_DELTE_GUANZHU.CMD(), request.getChannel());
        msg.setByte(1);
        gameRole.sendMessage(msg);
        getRelationShipDao().deleteGuanZhu(player.getId());
    }

    /**
     * 赠送友情币
     * FIX
     */
    public void prossSendFriendCoin(Message request) {
        int playerId = request.readInt();
        LinkedHashMap<Integer, NRelatedPlayer> guanZhuList = getGuanZhuList();
        if (guanZhuList == null || guanZhuList.isEmpty()) {
            return;
        }
        NRelatedPlayer guanzhu = guanZhuList.get(playerId);
        if (guanzhu == null) {
            return;
        }
        if (player.getSendFCC() >= NRelationshipDefine.GUANZHU_OTHER_ROLE_LIMITE_NUM) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_NOT_ENOUGHT);
            return;
        }
        if (DateUtil.isSameDay(guanzhu.getUpdateTime(), System.currentTimeMillis())) {
            return;
        }
        NGameRelationshipManager.getInstance().addYouqingBi(playerId, gameRole, guanzhu);

        Message msg = new Message(EMessage.FRIEND_SEND_FRIEND_COIN.CMD(), request.getChannel());
        msg.setInt(playerId);
        msg.setByte(player.getSendFCC());
        gameRole.sendMessage(msg);
    }

    /**
     * 一键赠送友情币
     */
    public void prossSendFriendCoinByOneKey(Message request) {
        LinkedHashMap<Integer, NRelatedPlayer> guanZhuList = getGuanZhuList();
        if (guanZhuList == null || guanZhuList.isEmpty()) {
            return;
        }

        LinkedHashMap<Integer, NRelatedPlayer> sendList = new LinkedHashMap<>();//未发送的
        for (NRelatedPlayer nRelatedPlayer : getGuanZhuList().values()) {
            if (DateUtil.isSameDay(nRelatedPlayer.getUpdateTime(), System.currentTimeMillis())) {
                continue;
            }

            sendList.put(nRelatedPlayer.getId(), nRelatedPlayer);

        }

        if (player.getSendFCC() >= NRelationshipDefine.GUANZHU_OTHER_ROLE_LIMITE_NUM) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_NOT_ENOUGHT);
            return;
        }


        List<NRelatedPlayer> list = sortList(sendList);
        int count = NRelationshipDefine.GUANZHU_OTHER_ROLE_LIMITE_NUM - player.getSendFCC();

        for (int i = 0; i < list.size(); i++) {
            if ((i + 1) > count) {
                break;
            }

            NRelatedPlayer pr = list.get(i);
            NGameRelationshipManager.getInstance().addYouqingBi(pr.getId(), gameRole, pr);

        }


        Message msg = new Message(EMessage.FRIEND_ONEKEY_SEND_FRIEND_COIN.CMD(), request.getChannel());
        msg.setByte(1);
        gameRole.sendMessage(msg);
    }

    private AtomicBoolean atomicBoolean = new AtomicBoolean();

    private void change() {
        if (atomicBoolean.get()) {
            return;
        }
        atomicBoolean.set(true);
    }

    /**
     * 赠送友情币
     * 被关注方调用 执行的时候 按照所有玩家的请求顺序来执行
     *
     * @param playerId
     */
    public boolean addFriendCoin(GameRole role) {
        long now = System.currentTimeMillis();
        LinkedHashMap<Integer, NRelatedPlayer> fenSis = getFenSi();
        NRelatedPlayer fs = fenSis.get(role.getPlayer().getId());
        if (fs == null) {
            return false;
        }
        if (DateUtil.isSameDay(fs.getUpdateTime(), now)) {
            return false;
        }

        if (player.getReFriendCoinCount() >= NRelationshipDefine.ADD_YOUQINGBI_PLAYER_MAX) {
            return false;
        }
        fs.addRelationCost(1);
        //player.addReFriendCoinCount(1);
        fs.setUpdateTime(now);
        role.getNRelationManager().changeFriendCoin(role, now, player.getId());

        getRelationShipDao().updateGuanzhu(role.getPlayerId(), player.getId(), 1, now);
        return true;
    }

    /**
     * 关注的人赠送成功赋值
     * <p>
     * 关注对象存储的值只是做判断验证用其他的也没啥用
     *
     * @param now
     * @param playerId
     */
    public void changeFriendCoin(GameRole role, long now, int playerId) {
        LinkedHashMap<Integer, NRelatedPlayer> guanzhuList = getGuanZhuList();
        NRelatedPlayer guanzhu = guanzhuList.get(playerId);
        if (guanzhu == null) {
            return;
        }

        role.getPlayer().addSendFriendCoinCount(1);
        guanzhu.setUpdateTime(now);
        guanzhu.addRelationCost(1);
        change();
        EnumSet<EPlayerSaveType> enumSet = EnumSet.of(EPlayerSaveType.SENDFCC);
        role.savePlayer(enumSet);
    }


    /**
     * 一键获取友情币
     * 一键获取可能存在关注他的玩家拉黑的同时出现并发问题
     */
    public void processOneKeyReceiveFriendCoin(Message request) {
        LinkedHashMap<Integer, NRelatedPlayer> fenSis = getFenSi();
        if (fenSis == null || fenSis.isEmpty()) {
            return;
        }
        synchronized (fenSis) {
            if (player.getReFriendCoinCount() >= NRelationshipDefine.RECEVIE_FRIEND_COIN_LIMITE) {
                return;
            }
            long now = System.currentTimeMillis();
            boolean state = false;
            for (NRelatedPlayer fs : fenSis.values()) {
                if (player.getReFriendCoinCount() >= NRelationshipDefine.RECEVIE_FRIEND_COIN_LIMITE) {
                    break;
                }
                if (!DateUtil.isSameDay(fs.getUpdateTime(), now)) {
                    continue;
                }
                if (fs.getRelationCost() < 1) {
                    continue;
                }
                state = true;
                player.addReFriendCoin(fs.getRelationCost());
                player.addReFriendCoinCount(1);
                fs.setRelationCost(0);
                getRelationShipDao().updateGuanzhu(fs.getId(), player.getId(), 0, fs.getUpdateTime());//循环操作数据库这个最好放到线程池里去操作
            }
            if (!state) {
                return;
            }
            EnumSet<EPlayerSaveType> enumSet = EnumSet.of(EPlayerSaveType.RECEIVEFC);
            enumSet.add(EPlayerSaveType.RECEIVEFCC);
            gameRole.savePlayer(enumSet);
        }
//        Message msg = new Message(EMessage.FRIEND_OONEKEY_RECEIVE_FRIENDCOIN.CMD(), request.getChannel());  
//        msg.setByte(1);
        byte currPage = request.readByte();
        sendFenSiMessage(currPage, request);

    }


    /**
     * 接收友情币
     */
    public void processReceiveFriendCoin(Message request) {

        LinkedHashMap<Integer, NRelatedPlayer> fenSis = getFenSi();
        if (fenSis == null || fenSis.isEmpty()) {
            return;
        }
        int id = request.readInt();
        if (!fenSis.containsKey(id)) {
            return;
        }

        synchronized (fenSis) {
            NRelatedPlayer fs = fenSis.get(id);
            if (!DateUtil.isSameDay(fs.getUpdateTime(), System.currentTimeMillis())) {
                return;
            }
            if (fs.getRelationCost() < 1) {
                return;
            }
            player.addReFriendCoin(fs.getRelationCost());
            player.addReFriendCoinCount(1);
            fs.setRelationCost(0);
            getRelationShipDao().updateGuanzhu(fs.getId(), player.getId(), 0, fs.getUpdateTime());
            EnumSet<EPlayerSaveType> enumSet = EnumSet.of(EPlayerSaveType.RECEIVEFC);
            enumSet.add(EPlayerSaveType.RECEIVEFCC);
            gameRole.savePlayer(enumSet);
        }
    }


    /**
     * 清理粉丝
     *
     * @param playerId
     */
    public void clearFenSiByPlayerId(int playerId) {

        if (!isHaveFensi(playerId)) {
            return;
        }
        LinkedHashMap<Integer, NRelatedPlayer> fensis = getFenSi();
        if (fensis == null || fensis.isEmpty()) {
            return;
        }
        fensis.remove(playerId);
    }

    /**
     * 粉丝是否已经满了
     *
     * @returnFIX
     */
    public boolean isFenSiFull() {
        LinkedHashMap<Integer, NRelatedPlayer> fensis = getFenSi();
        if (fensis == null) {
            return true;
        }
        if (fensis.size() >= GUANZHU_MY_ROLE_LIMITE_NUM + player.getLevel()) {
            return true;
        }
        return false;
    }

    /**
     * 是否存在粉丝
     *
     * @return
     */
    public boolean isHaveFensi(int otherId) {
        LinkedHashMap<Integer, NRelatedPlayer> fensis = getFenSi();
        if (fensis == null) {
            return false;
        }
        if (fensis.containsKey(otherId)) {
            return true;
        }

        return false;
    }


    /**
     * 添加关注
     *
     * @param other
     */
    private void addGuanZhu(NRelatedPlayer other) {
        LinkedHashMap<Integer, NRelatedPlayer> friends = getGuanZhuList();
        synchronized (friends) {
            friends.put(other.getId(), other);
            getRelationShipDao().insertGuanZhu(player.getId(), other.getId());
        }
    }

    /**
     * 添加粉丝
     *
     * @param other
     */
    private void addFenSi(NRelatedPlayer other) {
        LinkedHashMap<Integer, NRelatedPlayer> friends = getFenSi();
        int otherId = other.getId();

        friends.put(otherId, other);

    }


    /**
     * 检测是否可以加关注
     *
     * @param otherId
     * @return
     */
    public boolean checkAddGuanZhu(int playerId, Message request) {
        LinkedHashMap<Integer, NRelatedPlayer> guanzhus = getGuanZhuList();
        if (player.getId() == playerId) {
            return false;
        }
        if (guanzhus != null) {
            // 关注已经满
            if (guanzhus.size() >= NRelationshipDefine.GUANZHU_OTHER_ROLE_LIMITE_NUM) {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_PLAYER_FRIEND_MAX);
                return false;
            }
            // 已加好友
            if (guanzhus.containsKey(playerId)) {
                if (gameRole != null) {
                    gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_FRIEND_ALREADY_EXISTED);
                }
                return false;
            }
        }
        // 黑名单
        Map<Integer, NRelatedPlayer> blacks = getBlacks();
        if (blacks != null && blacks.containsKey(playerId)) {
            return false;
        }
        return true;
    }


    /**
     * 检测是否可以加粉丝
     *
     * @param otherId
     * @return
     */
    public boolean checkAddFenSi(int playerId) {
        LinkedHashMap<Integer, NRelatedPlayer> fensis = getFenSi();
        if (fensis != null) {
            // 被关注已满
            if (fensis.size() >= GUANZHU_MY_ROLE_LIMITE_NUM + player.getLevel()) {
                return false;
            }
            // 已经是粉丝
            if (fensis.containsKey(playerId)) {
                return false;
            }
        }
        return true;
    }


    public final LinkedHashMap<Integer, NRelatedPlayer> getGuanZhuList() {
        return guanzhus;
    }

    public final LinkedHashMap<Integer, NRelatedPlayer> getFenSi() {
        return beiguanzhus;
    }

    /***
     * 如果上线初始化没有拉黑列表 返回大小为0
     * @return
     */
    private final LinkedHashMap<Integer, NRelatedPlayer> getBlacks() {
        return blacks;
    }

    /**
     * 获取关注度的所有人数量
     *
     * @return
     */
    public int getGuanZhuListSize() {
        return getGuanZhuList().size();
    }


    /**
     * 获取粉丝数量
     *
     * @return
     */
    public int getFenSiListSize() {
        return getFenSi().size();
    }


    /**
     * 拉黑某个玩家的请求
     *
     * @param request
     */
    public void processBlackMessage(Message request) {
        int playerId = request.readInt();

        //判断这个角色是否存在 
        if (playerId == player.getId()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        IGameRole role = GameWorld.getPtr().getGameRole(playerId);
        if (role == null) {
            return;
        }
        LinkedHashMap<Integer, NRelatedPlayer> blacks = getBlacks();
        if (blacks.size() >= NRelationshipDefine.BLACK_MAX) {//黑名单数量达到上限了
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_FRIEND_BLACK_LIMITE);
            return;
        }

        if (blacks.containsKey(playerId)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_IN_BLACK_LIST);
            return;
        }
        if (GameWorld.getPtr().getGameRole(playerId) == null) {//判断是否有这个玩家 这个需要如果玩家不在线就要连数据的
            return;
        }

        if (guanzhus.get(playerId) != null) {
            clearGuanZhu(playerId, request);
        }
        List<Integer> list = new ArrayList<>();
        if (blacks.isEmpty()) {
            list.add(playerId);
            getRelationShipDao().insertBlack(player.getId(), list);
        } else {
            for (Integer id : blacks.keySet()) {
                list.add(id);
            }
            getRelationShipDao().updatBlack(player.getId(), list);
        }
        blacks.put(playerId, getRelatedPlayer(playerId));
        Message msg = new Message(EMessage.FRIEND_ADD_BLACK.CMD(), request.getChannel());
        msg.setByte(1);
        gameRole.sendMessage(msg);
    }

    /**
     * 移除某个黑名单
     *
     * @param request
     */
    public void processDeleteBlack(Message request) {
        int playerId = request.readInt();

        IGameRole role = GameWorld.getPtr().getGameRole(playerId);
        if (role == null) {
            return;
        }
        if (!blacks.containsKey(playerId)) {
            //gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_IN_BLACK_LIST);
            return;
        }
        blacks.remove(playerId);
        List<Integer> list = new ArrayList<>();
        for (Integer id : blacks.keySet()) {
            list.add(id);
        }
        getRelationShipDao().updatBlack(player.getId(), list);

        Message msg = new Message(EMessage.FRIEND_DELTE_BLACK.CMD(), request.getChannel());
        msg.setInt(playerId);
        gameRole.sendMessage(msg);

    }

    private NRelatedPlayer getRelatedPlayer(int playerId) {
        IGameRole gameRole = GameWorld.getPtr().getGameRole(playerId);
        if (gameRole.getPlayer() == null) {
            return null;
        }
        NRelatedPlayer relatedPlayer = new NRelatedPlayer();
        relatedPlayer.init(gameRole.getPlayer());
        return relatedPlayer;
    }


    /**
     * 所有在线玩家随机抽取20个 如果 在线不足二十个在非在线玩家获取补齐
     */
    public void processFriendTuiJianList(Message request) {
        Map<Integer, GameRole> onLineRole = GameWorld.getPtr().getOnlineRoles();
        List<GameRole> tempList = new ArrayList<>();
        List<GameRole> randomList = new ArrayList<>();
        for (GameRole role : onLineRole.values()) {
            int playerId = role.getPlayerId();
            if (getBlacks().containsKey(playerId)) {
                continue;
            }
            if (getGuanZhuList().containsKey(playerId)) {
                continue;
            }
            if (playerId == player.getId()) {
                continue;
            }
            tempList.add(role);
        }

        if (!tempList.isEmpty()) {
            if (tempList.size() <= NRelationshipDefine.RANDOM_ROLE_MAX) {
                randomList.addAll(tempList);
            } else {
                for (int i = 0; i < NRelationshipDefine.RANDOM_ROLE_MAX; i++) {
                    if (randomList.size() >= NRelationshipDefine.RANDOM_ROLE_MAX) {
                        break;
                    }
                    int random = GameUtil.getRangedRandom(0, tempList.size() - 1);
                    GameRole role = tempList.get(random);
                    randomList.add(role);
                    tempList.remove(role);
                }
            }
        }
        List<NRelatedPlayer> downLinePlayer = null;
        if (randomList.size() < NRelationshipDefine.RANDOM_ROLE_MAX) {
            int count = NRelationshipDefine.RANDOM_ROLE_MAX - randomList.size();
            downLinePlayer = getDownLineList(count);
        }
        Message msg = new Message(EMessage.FRIEND_TUIJIAN_LIST.CMD(), request.getChannel());
        int count = randomList.size();
        if (downLinePlayer != null) {
            count = downLinePlayer.size() + randomList.size();
        }

        msg.setByte(count);
        for (GameRole random : randomList) {
            msg.setInt(random.getPlayerId());
            msg.setString(random.getPlayer().getName());
            msg.setInt(0);
            msg.setByte(random.getPlayer().getHead());
            msg.setShort(random.getPlayer().getLevel());
            msg.setByte(0);
            msg.setByte(1);

        }
        if (downLinePlayer != null) {
            for (NRelatedPlayer pr : downLinePlayer) {
                msg.setInt(pr.getId());
                msg.setString(pr.getName());
                msg.setInt(0);

                msg.setByte(pr.getHead());
                msg.setShort(pr.getLevel());
                msg.setByte(0);
                msg.setByte(0);

            }
        }
        msg.setByte(getGuanZhuListSize());
        msg.setByte(NRelationshipDefine.GUANZHU_OTHER_ROLE_LIMITE_NUM);
        gameRole.sendMessage(msg);
    }


    private List<NRelatedPlayer> getDownLineList(int count) {
        LinkedHashMap<Integer, NRelatedPlayer> map = NGameRelationshipManager.getInstance().getPlayerMap();
        List<NRelatedPlayer> temp = new ArrayList<>();
        List<NRelatedPlayer> randomList = new ArrayList<>();
        if (map == null || map.isEmpty()) {
            return temp;
        }

        for (NRelatedPlayer pr : map.values()) {
            if (getBlacks() != null && getBlacks().containsKey(pr.getId())) {
                continue;
            }
            if (getGuanZhuList().containsKey(pr.getId())) {
                continue;
            }
            temp.add(pr);
        }

        if (!temp.isEmpty()) {
            if (temp.size() <= count) {
                randomList.addAll(temp);
            } else {
                for (int i = 0; i < count; i++) {
                    if (randomList.size() >= count) {
                        break;
                    }
                    int random = GameUtil.getRangedRandom(0, temp.size() - 1);
                    NRelatedPlayer role = temp.get(random);
                    randomList.add(role);
                    temp.remove(role);
                }
            }
        }


        return randomList;
    }


    /**
     * 关注列表
     */
    public void processGuanZhuList(Message request) {
        byte currPage = request.readByte();
        if (currPage <= 0) {
            return;
        }
        Message msg = new Message(EMessage.FRIEND_GUANZHU_LIST.CMD(), request.getChannel());
        List<NRelatedPlayer> list = sortList(guanzhus);
        if (list.size() < 1) {
            msg.setByte(0);
            msg.setByte(1);
            msg.setByte(1);
            msg.setByte(0);
            msg.setByte(NRelationshipDefine.GUANZHU_OTHER_ROLE_LIMITE_NUM);
            msg.setByte(0);
            msg.setByte(NRelationshipDefine.GUANZHU_OTHER_ROLE_LIMITE_NUM);
            gameRole.sendMessage(msg);
            return;
        }
        sendGuanZhuMessage(currPage, list, request);
    }

    private void sendGuanZhuMessage(int currPage, List<NRelatedPlayer> list, Message request) {
        Message msg = new Message(EMessage.FRIEND_GUANZHU_LIST.CMD(), request.getChannel());
        pageBean<NRelatedPlayer> bean = new pageBean<NRelatedPlayer>(list.size(), 4, list);
        int totalPage = bean.getTotalPage();
        if (currPage > totalPage) {
            return;
        }
        bean.setCurrPage(currPage);
        List<NRelatedPlayer> temp = bean.getCurrPageData();
        msg.setByte(temp.size());
        for (NRelatedPlayer pr : temp) {
            msg.setInt(pr.getId());
            msg.setString(pr.getName());
            msg.setInt(0);
            msg.setByte(pr.getHead());
            msg.setShort(pr.getLevel());
            msg.setByte(0);
            GameRole role = GameWorld.getPtr().getOnlineRole(pr.getId());
            msg.setByte(role == null ? 0 : 1);

            msg.setByte(isSendFriendCoin(pr) ? 1 : 0);//是否发送过友情币
            msg.setByte(getFenSi().containsKey(pr.getId()) ? 1 : 0);//是否关注过我

        }
        msg.setByte(currPage);
        msg.setByte(totalPage);

        msg.setByte(player.getSendFCC());
        msg.setByte(NRelationshipDefine.GUANZHU_OTHER_ROLE_LIMITE_NUM);
        msg.setByte(list.size());
        msg.setByte(NRelationshipDefine.GUANZHU_OTHER_ROLE_LIMITE_NUM);
        gameRole.sendMessage(msg);
    }

    private boolean isSendFriendCoin(NRelatedPlayer pr) {
        long now = System.currentTimeMillis();
        return DateUtil.isSameDay(pr.getUpdateTime(), now);
    }

    /**
     * 黑名单列表  人数少就二十多个因此就每打开就重新排序
     */
    public void processBlackList(Message request) {
        byte currPage = request.readByte();
        if (currPage < 0) {
            return;
        }
        Message msg = new Message(EMessage.FRIEND_BLACK_LIST.CMD(), request.getChannel());
        if (blacks.size() < 1) {
            msg.setByte(0);
            msg.setByte(1);
            msg.setByte(1);
            msg.setByte(blacks.size());
            msg.setByte(NRelationshipDefine.BLACK_MAX);
            gameRole.sendMessage(msg);
            return;
        }

        sendBlackList(request, currPage);
    }

    /**
     * 发送黑名单列表
     *
     * @param request
     * @param currPage
     */
    private void sendBlackList(Message request, byte currPage) {
        List<NRelatedPlayer> list = sortList(blacks);
        Message msg = new Message(EMessage.FRIEND_BLACK_LIST.CMD(), request.getChannel());
        pageBean<NRelatedPlayer> bean = new pageBean<NRelatedPlayer>(list.size(), 4, list);
        int totalPage = bean.getPageCount();
        if (currPage > totalPage) {
            return;
        }
        bean.setCurrPage(currPage);
        List<NRelatedPlayer> temp = bean.getCurrPageData();
        msg.setByte(temp.size());
        for (NRelatedPlayer pr : temp) {
            msg.setInt(pr.getId());
            msg.setString(pr.getName());
            msg.setInt(0);

            msg.setByte(pr.getHead());
            msg.setShort(pr.getLevel());
            GameRole role = GameWorld.getPtr().getOnlineRole(pr.getId());
            msg.setByte(role == null ? 0 : 1);
            msg.setByte(0);
        }
        msg.setByte(currPage);
        msg.setByte(totalPage);
        msg.setByte(list.size());
        msg.setByte(NRelationshipDefine.BLACK_MAX);
        gameRole.sendMessage(msg);
    }


    /**
     * 粉丝列表
     */
    public void processFenSiList(Message request) {
        byte currPage = request.readByte();
        if (currPage <= 0) {
            return;
        }

        Message msg = new Message(EMessage.FRIEND_FENSI_LIST.CMD(), request.getChannel());
        if (getFenSi().size() < 1) {
            msg.setByte(0);
            msg.setByte(1);
            msg.setByte(1);
            msg.setByte(0);
            msg.setByte(NRelationshipDefine.RECEVIE_FRIEND_COIN_LIMITE);
            msg.setByte(0);
            msg.setByte(NRelationshipDefine.GUANZHU_MY_ROLE_LIMITE_NUM + player.getLevel());
            gameRole.sendMessage(msg);
            return;
        }
        sendFenSiMessage(currPage, request);
    }


    private void sendFenSiMessage(int currPaget, Message request) {
        List<NRelatedPlayer> list = sortList(getFenSi());
        Message msg = new Message(EMessage.FRIEND_FENSI_LIST.CMD(), request.getChannel());
        pageBean<NRelatedPlayer> bean = new pageBean<NRelatedPlayer>(list.size(), 4, list);
        int totalPage = bean.getTotalPage();
        bean.setCurrPage(currPaget);
        List<NRelatedPlayer> temp = bean.getCurrPageData();
        msg.setByte(temp.size());
        for (NRelatedPlayer pr : temp) {
            msg.setInt(pr.getId());
            msg.setString(pr.getName());
            msg.setInt(0);
            msg.setByte(pr.getHead());
            msg.setShort(pr.getLevel());
            msg.setByte(0);
            GameRole role = GameWorld.getPtr().getOnlineRole(pr.getId());
            msg.setByte(role == null ? 0 : 1);
            msg.setByte(getGuanZhuList().containsKey(pr.getId()) ? 1 : 0);
            msg.setByte(isReceive(pr) ? 1 : 0);
            msg.setByte(isSendFriendCoin(pr) ? 1 : 0);
        }
        msg.setByte(currPaget);
        msg.setByte(totalPage);

        msg.setByte(player.getSendFCC());
        msg.setByte(NRelationshipDefine.RECEVIE_FRIEND_COIN_LIMITE);
        msg.setByte(list.size());
        msg.setByte(NRelationshipDefine.GUANZHU_MY_ROLE_LIMITE_NUM);
        gameRole.sendMessage(msg);
    }

    /**
     * 是否接收了友情值
     *
     * @param pr
     * @return
     */
    private boolean isReceive(NRelatedPlayer pr) {
        long now = System.currentTimeMillis();
        if (DateUtil.isSameDay(pr.getUpdateTime(), now)) {
            if (pr.getRelationCost() < 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 玩家详情
     *
     * @param request
     */
    public void processRoleInfo(Message request) {
        int roleId = request.readInt();
        IGameRole iRole = GameWorld.getPtr().getGameRole(roleId);
        if (iRole == null) {
            return;
        }
        Message msg = new Message(EMessage.FRIEND_ROLE_INFO.CMD(), request.getChannel());
        Player player = iRole.getPlayer();
        msg.setInt(player.getId());
        msg.setString(player.getName());
        msg.setByte(0);//性别
        msg.setShort(player.getLevel());
        msg.setByte(player.getHead());
        msg.setString("青龙帮");
        msg.setString("贤惠媳妇");
        msg.setByte(guanzhus.containsKey(roleId) ? 1 : 0);
        msg.setByte(blacks.containsKey(roleId) ? 1 : 0);
        msg.setByte(0);//是否收徒
        msg.setInt(0);
        msg.setByte(0);

        gameRole.sendMessage(msg);

    }


    /**
     * 排序
     */
    private List<NRelatedPlayer> sortList(LinkedHashMap<Integer, NRelatedPlayer> data) {
        List<NRelatedPlayer> temp = new ArrayList<>();
        for (NRelatedPlayer pr : data.values()) {
            temp.add(pr);
        }

        Collections.sort(temp, new compra());
        return temp;

    }
}

class compra implements Comparator<NRelatedPlayer> {

    @Override
    public int compare(NRelatedPlayer o1, NRelatedPlayer o2) {
        GameRole role1 = GameWorld.getPtr().getOnlineRole(o1.getId());
        GameRole role2 = GameWorld.getPtr().getOnlineRole(o2.getId());
        if (role1 != null) {
            if (role2 != null) {
                if (isSendFriendCoin(o1)) {
                    return 1;
                }
            }

            return -1;
        }
        return 1;
    }

    private boolean isSendFriendCoin(NRelatedPlayer o) {
        long now = System.currentTimeMillis();
        return DateUtil.isSameDay(o.getUpdateTime(), now);
    }

}

class pageBean<T> {

    //当前总页数
    private int totalPage;
    private int totalCount;
    private int currPage;
    //数据条数
    private int pageCount;
    //每页的数据条数
    private int start;
    //起始数据位置
    private int end;

    private List<T> list;

    public pageBean(int totalCount, int pageCount, List<T> list) {

        this.totalCount = totalCount;
        this.pageCount = pageCount;
        this.list = list;

        int pageSize_x = (int) totalCount / pageCount;
        if (totalCount >= pageCount) {
            this.totalPage = totalCount % pageCount == 0 ? pageSize_x : pageSize_x + 1;
        } else {
            this.totalPage = 1;
        }
    }

    public void setCurrPage(int currPage) {
        if (totalPage < currPage) {
            this.currPage = totalPage;
        } else {
            this.currPage = currPage;
        }

        this.start = (currPage - 1) * pageCount;
        this.end = currPage * pageCount;
    }

    public List<T> getCurrPageData() {
        if (end > list.size()) {
            return list.subList(start, list.size());
        } else {
            return list.subList(start, end);
        }
    }


    public int getTotalPage() {
        return totalPage;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

}
