package com.rd.game.manager;

import com.rd.bean.drop.DropData;
import com.rd.bean.player.NBiaoCheLog;
import com.rd.bean.player.NBiaoche;
import com.rd.bean.player.NHuSongPlayer;
import com.rd.combat.CombatSystem;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.EPlayerSaveType;
import com.rd.dao.NBiaoCheDao;
import com.rd.define.*;
import com.rd.enumeration.EMessage;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.game.IGameRole;
import com.rd.game.NGameBiaocCheManager;
import com.lg.bean.game.Fun;
import com.rd.model.NHuSongModel;
import com.rd.model.data.husong.NHuSongData;
import com.rd.net.message.Message;
import com.rd.util.LogUtil;
import org.apache.commons.lang3.RandomUtils;

import java.util.*;

/*****
 *
 *护送镖车管理器
 * */
public class NHuSongManger {

    private GameRole role;

    //押镖数据
    private NBiaoche _escort;
    private NBiaoCheDao _dao;


    //车队时间
    private long refreshTime = 0;

    //车队
    private Map<Integer, NHuSongPlayer> robPlayers = new HashMap<>();

    private byte fightResult = FightDefine.FIGHT_RESULT_FAIL;

    public NHuSongManger(GameRole role) {
        this.role = role;
    }

    /**
     * 初始化押镖数据
     */
    private void init() {
        this._dao = role.getDbManager().nBiaoCheDao;
        NHuSongPlayer husong = NGameBiaocCheManager.getInstance().getNBiaocheById(role.getPlayerId());
        if (husong != null && husong.getnBiaoche() != null) {
            _escort = husong.getnBiaoche();
        } else {
            this._escort = _dao.getPlayerEscort(role.getPlayerId());
        }
    }

    private NBiaoche getEscort() {
        if (null == _escort) {
            init();
        }
        return _escort;
    }


    /**
     * 镖车列表面板
     */
    public void processBiaoCheListPanel(Message request) {
        short page = request.readShort();
        Message msg = new Message(EMessage.HUSONG_BIAOCHE_LIST_PANEL.CMD(), request.getChannel());
        List<NHuSongPlayer> list = NGameBiaocCheManager.getInstance().getbiaocheList();
        pageBean<NHuSongPlayer> bean = new pageBean<>(list.size(), NHuSongDefine.BIAOCHE_PAGE_COUNT, list);
        bean.setCurrPage(page);
        List<NHuSongPlayer> currList = bean.getCurrPageData();
        msg.setShort(page);
        msg.setShort(bean.getTotalPage());
        msg.setByte(currList.size());
        for (NHuSongPlayer nHuSongPlayer : currList) {
            nHuSongPlayer.getSimpleMessage(msg);
            msg.setByte(nHuSongPlayer.getQuality());
            msg.setLong(nHuSongPlayer.getShengYuTime());
            msg.setByte(nHuSongPlayer.getHurted());
        }
        role.sendMessage(msg);
    }

    /***
     *
     * 押镖或者劫镖 次数信息
     */
    public void processBiaoCheInfoPanel(Message request) {
        Message msg = new Message(EMessage.HUSONG_BIAOCHE_COUNT_PANEL.CMD(), request.getChannel());
        NBiaoche biaoche = getEscort();
        if (biaoche.getCargo() == 1 && biaoche.getShengYuTime() < 0) {
            biaoche.setArrive((byte) 1);
            biaoche.setStartTime(0);
        }
        msg.setByte(biaoche.isLingQu() ? 1 : 0);
        msg.setByte(biaoche.getCount());
        msg.setByte(biaoche.getJiebiaoCnt());
        msg.setByte(biaoche.getRefresh());
        msg.setLong(biaoche.getShengYuTime());
        msg.setByte(biaoche.getQuality());
        List<Integer> list = getEscort().getRobList();
        msg.setByte(list.size());
        for (Integer roleId : list) {
            msg.setInt(roleId);
        }
        role.sendMessage(msg);

    }


    /**
     * 刷新镖车品质
     */
    public void processRandomBiaocheQuality(Message request) {
        byte type = request.readByte();
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        if (type == 0) {
            //元宝
            if (getEscort().getRefresh() >= NHuSongDefine.BIAOCHE_REFRSH) {
                DropData cost = new DropData(EGoodsType.GOLD, 0, 50);
                if (!role.getPackManager().useGoods(cost, EGoodsChangeType.ESCORT_REFRESH_CONSUME, saves)) {
                    cost.setT(EGoodsType.DIAMOND.getId());
                    if (!role.getPackManager().useGoods(cost, EGoodsChangeType.ESCORT_REFRESH_CONSUME, saves)) {
                        role.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
                        return;
                    }
                }
            } else {
                getEscort().addRefresh();
            }
            int quality = randomQuality();
            getEscort().setQuality(quality);

        } else {

            DropData cost = new DropData(EGoodsType.ITEM, GoodsDefine.ITEM_ID_REFRESH_FLAG, 1);
            if (!role.getPackManager().useGoods(cost, EGoodsChangeType.ESCORT_REFRESH_CONSUME, saves)) {
                //元宝
                cost = new DropData(EGoodsType.DIAMOND, 0, NHuSongDefine.REFRESH_BIAOCHE_YUANBAO);
                if (!role.getPackManager().useGoods(cost, EGoodsChangeType.ESCORT_REFRESH_CONSUME, saves)) {
                    role.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
                    return;
                }
            }
            getEscort().setQuality(NHuSongDefine.BIAOCHE_MAX_QUALITY);

        }
        Message msg = new Message(EMessage.HUSONG_RANDOM_QUALITY.CMD(), request.getChannel());
        msg.setByte(getEscort().getQuality());
        msg.setByte(getEscort().getRefresh());
        role.sendMessage(msg);
        _dao.updatePlayerEscort(getEscort());
    }


    public int randomQuality() {
        List<NHuSongData> datas = NHuSongModel.getNHuSongDataList();
        int random = RandomUtils.nextInt(1, 100);
        int total = 0;
        int qui = getEscort().getQuality();
        for (NHuSongData nHuSongData : datas) {
            if (qui > nHuSongData.getId()) {
                total += nHuSongData.getGailv();
                continue;
            }
            total += nHuSongData.getGailv();
            if (random <= total) {
                return nHuSongData.getId();
            }
        }
        return 0;
    }

    private List<DropData> balanceItemReward() {
        List<DropData> rewards = new ArrayList<>();
        //物品奖励，还得加上被劫镖的负收益
        NHuSongData model = NHuSongModel.getNHuSongDataById(getEscort().getQuality());
        if (model.getId() >= NHuSongDefine.BIAOCHE_MAX_QUALITY) {
            return model.getRewards();
        }


        int huredNum = getEscort().getHurted();
        for (DropData reward : model.getRewards()) {
            DropData drop = getHurtReward(reward.getG(), model.getJiebiaoReards());
            int num = 0;
            if (drop != null) {
                num = drop.getN() * huredNum;
            }
            rewards.add(new DropData(reward.getT(), reward.getG(), reward.getN() - num));
        }
        return rewards;
    }


    private DropData getHurtReward(int goodId, List<DropData> rewards) {
        for (DropData dropData : rewards) {
            if (goodId == dropData.getG()) {
                return dropData;
            }
        }
        return null;
    }


    /**
     * 跨天重置数据
     *
     * @return
     */
    public void dayReset() {
        if (getEscort().getCount() > 0) {
            getEscort().setCount(0);
        }

    }

    /**
     * 押运镖车
     *
     * @param request
     */
    public void processDispatch(Message request) {
        //是否有未结算的镖车
        if (getEscort().getCargo() == 1) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_ESCORT_UNCOMPLETE);
            return;
        }
        //判断次数
        if (getEscort().getCount() >= NHuSongDefine.BIAOCHE_HUSONG_COUNT) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_ESCORT_DISPATCH_MAX);
            return;
        }
        NHuSongPlayer husong = NGameBiaocCheManager.getInstance().getNBiaocheById(role.getPlayerId());
        if (husong != null) {
            return;
        }
        long curr = System.currentTimeMillis();
        //发车
        getEscort().addCount();
        getEscort().setStartTime(curr);
        getEscort().setCargo((byte) 1);
        //getEscort().setHurted(0);
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        husong = new NHuSongPlayer(role.getPlayer());
        husong.setQuality(getEscort().getQuality());
        husong.setStartTime(curr);
        husong.setnBiaoche(getEscort());
        NGameBiaocCheManager.getInstance().addBiaoche(husong);
        Message msg = new Message(EMessage.HUSONG_BIAOCHE.CMD(), request.getChannel());
        msg.setLong(getEscort().getShengYuTime());
        msg.setByte(getEscort().getQuality());
        msg.setByte(getEscort().getCount());
        role.sendMessage(msg);
        role.savePlayer(enumSet);
        _dao.updatePlayerEscort(getEscort());
        //getEscort().addStartLog();
    }

    /**
     * 押镖完成
     *
     * @param request
     */
    public void processComplete(Message request) {
        byte type = request.readByte();
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        if (getEscort().getCargo() == 0) {
            return;
        }
        if (getEscort().getCargo() == 1 && getEscort().getArrive() == 1) {
            processPanel(request);
            return;
        }
        //立即完成
        if (type == 1) {
            DropData cost = new DropData(EGoodsType.GOLD, 0, 300);
            if (!role.getPackManager().useGoods(cost, EGoodsChangeType.ESCORT_COMPLETE_CONSUME, saves)) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOLD_LESS);
                return;
            }
            //记录完成状态


        } else {
            if (!getEscort().isComplete()) {
                return;
            }
        }
        getEscort().setArrive((byte) 1);
        getEscort().setStartTime(0);
        processPanel(request);
        _dao.updatePlayerEscort(this.getEscort());
    }

    private void processPanel(Message request) {
        //奖励信息
        List<DropData> rewards = balanceItemReward();
        Message msg = new Message(EMessage.HUSONG_BIAOCHE_END.CMD(), request.getChannel());
        msg.setByte(getEscort().getQuality());
        msg.setByte(rewards.size());
        for (DropData reward : rewards) {
            msg.setInt(reward.getG());
            msg.setShort(reward.getN());
            msg.setByte(reward.getT());

        }
        msg.setByte(getEscort().getHurted());
        List<NBiaoCheLog> list = new ArrayList<>();
        for (NBiaoCheLog log : getEscort().getLogs()) {
            //被劫
            if (log.getT() == 2) {
                list.add(log);
            }
        }
        msg.setByte(list.size());
        for (NBiaoCheLog nBiaoCheLog : list) {
            nBiaoCheLog.getLogMsg(msg);
        }
        role.sendMessage(msg);

    }


    /**
     * 领取渡劫奖励
     *
     * @param request
     */
    public void processReward(Message request) {
        if (getEscort().getArrive() == 0) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        if (getEscort().getCargo() == 0) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }
        //奖励
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        List<DropData> rewards = balanceItemReward();
        role.getPackManager().addGoods(rewards, EGoodsChangeType.ESCORT_COMPLETE_ADD, saves);
        //清空货物
        getEscort().setCargo((byte) 0);
        getEscort().setArrive((byte) 0);
        int qua = randomQuality();
        getEscort().setQuality(qua);
        //重置品质
        Message msg = new Message(EMessage.HUSONG_BIAOCHE_LINGQU.CMD(), request.getChannel());
        role.sendMessage(msg);
        //保存数据
        role.savePlayer(saves);
        _dao.updatePlayerEscort(this.getEscort());
        //记录玩家押镖日志
        LogUtil.log(role.getPlayer(), new Fun(LogFunType.ESCORT_COMP.getId(), 1));
    }

    /***
     *
     * 被劫镖或者劫镖的历史记录
     */
    public void processLogs(Message request) {
        Queue<NBiaoCheLog> logs = getEscort().getLogs();
        Message msg = new Message(EMessage.HUSONG_LOGS.CMD(), request.getChannel());
        msg.setByte(logs.size());
        for (NBiaoCheLog nBiaoCheLog : logs) {
            nBiaoCheLog.getLogMsg(msg);
        }
        role.sendMessage(msg);
    }


    private boolean fuchouFight = false;
    private NBiaoCheLog nBiaoCheLog = null;

    /***
     *
     * 复仇
     * */
    public void processFuChou(Message request) {
        if (nBiaoCheLog != null) {
            return;
        }
        int roleId = request.readInt();
        long time = request.readLong();
        Queue<NBiaoCheLog> logs = getEscort().getLogs();
        NBiaoCheLog currLog = null;
        for (NBiaoCheLog nBiaoCheLog : logs) {
            if (nBiaoCheLog.getT() == 2) {
                if (nBiaoCheLog.getId() == roleId && nBiaoCheLog.getS() == time) {
                    currLog = nBiaoCheLog;
                }
            }
        }
        if (currLog == null) {
            return;
        }
        if (currLog.getR() == 0) {
            return;
        }
        if (currLog.getRv() == 1) {
            return;
        }
        fuchouFight = true;
        nBiaoCheLog = currLog;
        Message msg = new Message(EMessage.HUSONG_FUCHONG.CMD(), request.getChannel());
        msg.setByte((byte) 1);
        role.sendMessage(msg);
        _dao.updatePlayerEscort(this.getEscort());
    }

    /*****
     *
     * 复仇结束
     */
    public void processFuChouEnd(Message request) {
        if (nBiaoCheLog == null) {
            return;
        }
        Message msg = new Message(EMessage.HUSONG_FUCHONG_END.CMD(), request.getChannel());

        nBiaoCheLog.setRv((byte) 1);
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        List<DropData> list = new ArrayList<>();
        if (fuchouFight) {
            NHuSongData model = NHuSongModel.getNHuSongDataById(getEscort().getQuality());
            List<DropData> das = model.getJiebiaoReards();
            rewarDouble(das);
            list = das;
            role.getPackManager().addGoods(das, EGoodsChangeType.ESCORT_COMPLETE_ADD, saves);
        }

        msg.setByte(list.size());
        for (DropData dropData : list) {
            msg.setInt(dropData.getG());
            msg.setShort(dropData.getN());
            msg.setByte(dropData.getT());
        }
        msg.setBool(fuchouFight);
        role.sendMessage(msg);
        _dao.updatePlayerEscort(this.getEscort());
        nBiaoCheLog = null;
        if (fuchouFight) {
            role.savePlayer(saves);
        }
    }


    private void rewarDouble(List<DropData> list) {
        for (DropData dropData : list) {
            dropData.setN(dropData.getN() * 2);
        }

    }

    private boolean fightRult = true;
    private int biaocheQuality = 1;

    /**
     * 劫杀镖车开始
     *
     * @param request
     */
    public void processJieBiaoStart(Message request) {
        int playerId = request.readInt();
        if (getEscort().getJiebiaoCnt() >= NHuSongDefine.JIEBIAO_COUNT) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_ESCORT_ROB_MAX);
            return;
        }
        if (playerId == role.getPlayerId()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_ESCORT_ROB_MAX);
            return;
        }
        NHuSongPlayer ep = NGameBiaocCheManager.getInstance().getNBiaocheById(playerId);
        if (ep == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        NBiaoche biaoche = ep.getnBiaoche();
        if (biaoche.getShengYuTime() < 1) {
            biaoche.setArrive((byte) 1);
            NGameBiaocCheManager.getInstance().remove(ep);
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        if (biaoche.getHurted() >= NHuSongDefine.BIAOCHE_JIE_COUNT) {
            return;
        }

        Message msg = new Message(EMessage.HUSONG_BIAOCHE_LANJIE.CMD(), request.getChannel());
        IGameRole roleB = GameWorld.getPtr().getGameRole(ep.getId());
        if (roleB != null) {
            fightRult = CombatSystem.pvp(msg, role.getPlayer(), roleB.getPlayer(), CombatDef.ROUND_FIVE);
            biaocheQuality = biaoche.getQuality();
        }

        biaoche.addHurted(role.getPlayer(), (byte) 1);
        _dao.updatePlayerEscort(biaoche);
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        getEscort().addRobLog(ep, (byte) 1);
        getEscort().addJiebiaoCnt();
        getEscort().getRobList().add(playerId);
        _dao.updatePlayerEscort(this.getEscort());
        role.savePlayer(enumSet);
        role.sendMessage(msg);
    }


    /*****
     *
     * 劫镖结果
     */
    public void processJieBiaoEnd(Message request) {
        Message msg = new Message(EMessage.HUSONG_BIAOCHE_LANJIE_RESULT.CMD(), request.getChannel());
        boolean isSave = false;
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        fightRult = true;
        if (fightRult) {
            NHuSongData data = NHuSongModel.getNHuSongDataById(biaocheQuality);
            if (data != null) {
                role.getPackManager().addGoods(data.getJiebiaoReards(), EGoodsChangeType.FIGHT_DUNGEON_ADD, enumSet);
                isSave = true;
            }
        }
        msg.setByte(biaocheQuality);
        msg.setBool(fightRult);
        role.sendMessage(msg);
        if (isSave) {
            role.savePlayer(enumSet);
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

}
