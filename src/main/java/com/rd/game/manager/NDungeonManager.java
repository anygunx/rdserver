package com.rd.game.manager;

import com.rd.bean.copy.cailiao.CLCopy;
import com.rd.bean.copy.cailiao.CLCopyDaily;
import com.rd.bean.drop.DropData;
import com.rd.bean.player.Player;
import com.rd.bean.rank.PlayerRank;
import com.rd.combat.CombatSystem;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.*;
import com.rd.enumeration.EMessage;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.game.IGameRole;
import com.rd.game.NGameRankManager;
import com.rd.game.event.EGameEventType;
import com.rd.game.event.GameEvent;
import com.rd.model.NCopyModel;
import com.rd.model.data.copy.CopyData;
import com.rd.model.data.copy.NSJGCopyData;
import com.rd.model.data.copy.cailiao.NCaiLiaoCopyData;
import com.rd.model.data.copy.geren.NGeRenBossData;
import com.rd.model.data.copy.mizang.NMiZangCopyData;
import com.rd.model.data.copy.mizang.NMiZangStarData;
import com.rd.model.data.copy.tianmen.NTianMenDBData;
import com.rd.model.data.copy.tianmen.NTianMenData;
import com.rd.model.data.copy.zhongkui.NZhongKuiData;
import com.rd.net.message.Message;
import org.apache.commons.lang3.RandomUtils;

import java.util.*;

/**
 * 副本管理器
 *
 * @author
 */
public class NDungeonManager {

    private GameRole gameRole;
    private Player player;

    private List<DropData> rewardList = new ArrayList<>();
    private int currCopyId;
    private int currCopyType;
    Map<String, CLCopy> roleCopy;


    public NDungeonManager(GameRole role) {
        this.gameRole = role;
        this.player = role.getPlayer();
    }

    public void init() {
        roleCopy = gameRole.getDbManager().copyDao.getCLCopy(player.getId());
    }


    /**
     * 打开材料或者个人boss面板
     */
    public void processCopyPanel(Message request) {
        byte type = request.readByte();
        if (type != (byte) NDungeonDefine.NDungeon.CAILIAO.getType() &&
                type != (byte) NDungeonDefine.NDungeon.GERENBOSS.getType()) {
            return;
        }
        Message msg = new Message(EMessage.COPY_PANEL.CMD(), request.getChannel());
        CLCopy clCopy = getCLCopyByTypeAndBubType(type, NDungeonDefine.SUBTYPE);
        Map<Integer, CLCopyDaily> map = clCopy.getDailyTimes();
        msg.setByte(type);
        Set<Integer> passed = clCopy.getPassed();
        msg.setByte(passed.size());
        for (Integer fubeiId : passed) {
            msg.setByte(fubeiId);
            CLCopyDaily da = map.get(fubeiId);
            if (type == (byte) NDungeonDefine.NDungeon.GERENBOSS.getType()) {
                if (da == null) {
                    msg.setByte(2);
                } else {
                    if (da.getButCnt() < 1) {
                        msg.setByte(1);
                    } else {
                        msg.setByte(2);
                    }
                }
            } else {
                msg.setByte(1);
                if (da == null) {
                    msg.setByte(0);
                    msg.setBool(true);
                } else {
                    msg.setByte(da.getButCnt());
                    msg.setBool(da.getMianfei() < 1);
                }
            }
        }

        gameRole.sendMessage(msg);
    }


    private boolean fightReult = false;

    /**
     * 副本请求
     *
     * @param request
     */
    public void processCopyRquest(Message request) {
        //副本id
        byte type = request.readByte();
        int id = request.readInt();
        if (type == NDungeonDefine.NDungeon.SHUIJINGGONG.getType() ||
                type == NDungeonDefine.NDungeon.TIANMEN.getType() ||
                type == NDungeonDefine.NDungeon.MIZANG.getType()
                ) {
            state = request.readBoolean();
        }
        boolean isEnter = true;

        if (NDungeonDefine.NDungeon.getNPiFuType(type) == null) {
            return;
        }
//		if(currCopyId!=0&&currCopyType!=0) {
//			return;
//		}
        if (gameRole.getFightManager().inInstance()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_COPY_ALREADY_IN);
            return;
        }
        CopyData data = null;
        List<DropData> zhongkuiRewards = new ArrayList<>();
        if (type == NDungeonDefine.NDungeon.CAILIAO.getType()) {
            NCaiLiaoCopyData cl = NCopyModel.getCLCopyDataMap(id);
            if (cl == null) {
                return;
            }
            CLCopy clCopy = getCLCopyByTypeAndBubType(type, NDungeonDefine.SUBTYPE);
            if (player.getLevel() < cl.getNeelLv()) {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_LEVEL_LESS);

                return;
            } else if (clCopy.getPassed().contains(id)) {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_TIMES_LESS);
                return;
            }
            data = cl;
        } else if (type == NDungeonDefine.NDungeon.SHUIJINGGONG.getType()) {
//			if(player.getLevel()<20) {
//				return;
//			}

            NSJGCopyData sjg = NCopyModel.getSJGCopyDataMap(id);
            if (sjg == null) {
                return;
            }
            CLCopy clCopy = getCLCopyByTypeAndBubType(type, NDungeonDefine.SUBTYPE);
            if (clCopy.getPass() + 1 != id) {
                return;
            }

            data = sjg;
        } else if (type == NDungeonDefine.NDungeon.TIANMEN.getType()) {
            NTianMenData tm = NCopyModel.getTianMenCopyDataMap(id);
            if (tm == null) {
                return;
            }
            CLCopy clCopy = getCLCopyByTypeAndBubType(type, NDungeonDefine.SUBTYPE);
            if (clCopy.getPass() + 1 != id) {
                return;
            }
            data = tm;
        } else if (type == NDungeonDefine.NDungeon.GERENBOSS.getType()) {
            CLCopy clCopy = getCLCopyByTypeAndBubType(type, NDungeonDefine.SUBTYPE);
            if (clCopy.getPassed().contains(id)) {
                return;
            }
            NGeRenBossData gr = NCopyModel.getNGeRenBossDataById(id);
            if (gr == null) {
                return;
            }
            data = gr;
        } else if (type == NDungeonDefine.NDungeon.MIZANG.getType()) {
            NMiZangCopyData mz = NCopyModel.getNMiZangCopyDataById(id);
            if (mz == null) {
                return;
            }
            NCopyModel.getNMiZangCopyDataByStarId(mz.getType());
            if (player.getLevel() < mz.getLevel()) {
                return;
            }

            CLCopy clCopy = getCLCopyByTypeAndBubType(type, mz.getType());

            if (clCopy.getDailyTimes().size() + 1 < id - getBefore(mz.getType())) {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_TIAOGUAN_PASS);
                return;
            }
            data = mz;
        } else if (type == NDungeonDefine.NDungeon.QUANMINBOSS.getType()) {
            fightReult = gameRole.getNBossManager().processCitizenStart(type, id, request);
            this.currCopyId = id;
            this.currCopyType = type;
            return;
        } else if (type == NDungeonDefine.NDungeon.ZHONGDAN.getType()) {
            NZhongKuiData zk = NCopyModel.getNZhongKuiDataById(id);
            if (zk == null) {
                return;
            }
            CLCopy clCopy = getCLCopyByTypeAndBubType(type, NDungeonDefine.SUBTYPE);
            if (clCopy.getTotalCount() < 0) {
                return;
            }
            data = zk;
            zhongkuiRewards = getZhongKuiRandomReward(data.getDropDataList(), clCopy.getSweep());
            //randomStar();
        }
        Message msg = new Message(EMessage.COPY_REQUITE_FIGHT.CMD(), request.getChannel());
        msg.setByte(type);
        msg.setInt(id);
        msg.setBool(isEnter);
        if (isEnter) {
            fightReult = CombatSystem.pveDungeon(msg, player, data.getBossid(), data.getMonsterids(), CombatDef.ROUND_FIVE);
            this.clearFightDrop();
            player.setMapType(EMapType.DUNGEON);
            this.currCopyId = id;
            this.currCopyType = type;
            if (!zhongkuiRewards.isEmpty()) {
                this.rewardList.addAll(zhongkuiRewards);
            } else {
                this.rewardList.addAll(data.getDropDataList());
            }


        }
        gameRole.sendMessage(msg);
    }

    /**
     * 钟馗设置星级
     */
    private void randomStar() {
        CLCopy clCopy = getCLCopyByTypeAndBubType((byte) NDungeonDefine.NDungeon.ZHONGDAN.getType(), NDungeonDefine.SUBTYPE);
        int random = RandomUtils.nextInt(NDungeonDefine.ZHONGKUI_MIN_STAR, NDungeonDefine.ZHONGKUI_MAX_STAR);
        clCopy.setSweep((byte) random);

    }

    private byte getBefore(int type) {
        byte count = 0;
        int before = type - 1;
        for (int i = before; i > 0; i--) {
            List<NMiZangCopyData> ds = NCopyModel.getNMiZangCopyDataByStarId(type);
            count += ds.size();
        }
        return count;
    }


    /**
     * 水晶棺面板
     */
    public void processSJGCopyPanel(Message request) {
        byte myrank = NGameRankManager.getInstance().getPlayerRankByPlayerId(player.getId(), NRankType.COPY_SJG);
        Message msg = new Message(EMessage.COPY_SJG_PANEL.CMD(), request.getChannel());
        msg.setByte(player.getSjgCopyId());
        msg.setByte(myrank);
        List<PlayerRank> rank = NGameRankManager.getInstance().getSJGRankList();
        if (rank == null) {
            msg.setByte(0);
            msg.setBool(state);
            gameRole.sendMessage(msg);
            return;
        }
        msg.setByte(rank.size());
        for (int i = 0; i < rank.size(); i++) {
            PlayerRank playerRank = rank.get(i);
            msg.setString(playerRank.getName());
            msg.setByte((byte) playerRank.getValue());
            msg.setByte(i + 1);
            int id = playerRank.getId();
            IGameRole role = GameWorld.getPtr().getGameRole(id);
            long fight = playerRank.getFighting();
            if (role != null) {
                fight = role.getPlayer().getFighting();
            }
            msg.setLong(fight);
        }
        msg.setBool(state);
        gameRole.sendMessage(msg);
    }


    private boolean state = false;

    /**
     * 更改水晶中自动挑战的状态
     */
    public void processSJGCopySet(Message request) {
        boolean state = request.readBoolean();
        this.state = state;
        Message msg = new Message(EMessage.COPY_SJG_AUTO_SET.CMD(), request.getChannel());
        msg.setBool(state);
        gameRole.sendMessage(msg);
    }


    public CLCopy getCLCopyByTypeAndBubType(byte type, int subType) {
        String key = getTypeKey(type, subType);
        if (this.roleCopy.containsKey(key)) {
            return this.roleCopy.get(key);
        } else {
            CLCopy copy = new CLCopy();
            copy.setPlayerId(player.getId());
            copy.setDatatype(type);
            copy.setSubType(subType);
            if (type == NDungeonDefine.NDungeon.ZHONGDAN.getType()) {
                copy.setTotalCount(5);
            }
            synchronized (roleCopy) {
                roleCopy.put(key, copy);
            }
            gameRole.getDbManager().copyDao.insertCLCopy(copy);
            return copy;
        }
    }


    private String getTypeKey(byte type, int subType) {
        return type + "_" + subType;
    }

    /**
     * 副本战斗结果
     *
     * @param request
     */
    public void processCopyFinish(Message request) {
//		if(currCopyId==0||currCopyType==0) {
//			return;
//		} 

        Message msg = new Message(EMessage.COPY_FIGHT_FINISH.CMD(), request.getChannel());
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        boolean isSave = false;
        if (fightReult) {
            isSave = passCopy(enumSet);
            msg.setByte(currCopyType);
            msg.setInt(currCopyId);
            msg.setByte(1);
            if (isSave) {
                msg.setByte(rewardList.size());
                for (DropData data : rewardList) {
                    msg.setByte(data.getT());
                    msg.setShort(data.getG());
                    msg.setInt(data.getN());
                }

            } else {
                msg.setByte(0);
            }
            clearFightDrop();
            //记录玩家副本日志
            //LogUtil.log(player, new Dungeon(data.getType(),daily.getAttackTimes(),dungeon.getPass()));
        } else {
            this.clearFightDrop();
            msg.setByte(currCopyType);
            msg.setInt(currCopyId);
            msg.setByte(0);
            msg.setByte(0);
            state = false;

        }
        player.setMapType(EMapType.FIELD_NORMAL);
        gameRole.sendMessage(msg);
        this.currCopyId = 0;
        this.currCopyType = 0;

        gameRole.getDbManager().playerDao.savePlayer(player, enumSet);

    }

    /**
     * 挑战成功
     */
    private boolean passCopy(EnumSet<EPlayerSaveType> enumSet) {
        CLCopy clCopy = null;
        boolean isAddRewad = true;
        if (currCopyType == NDungeonDefine.NDungeon.QUANMINBOSS.getType()) {
            gameRole.getNBossManager().resultFinish(fightReult);
            gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.COPY_QUANMIN, 1, enumSet));
            return true;
        }
        if (currCopyType == NDungeonDefine.NDungeon.MIZANG.getType()) {
            NMiZangCopyData data = NCopyModel.getNMiZangCopyDataById((byte) currCopyId);
            if (data == null) {
                return false;
            }
            clCopy = getCLCopyByTypeAndBubType((byte) currCopyType, data.getType());
        } else {
            clCopy = getCLCopyByTypeAndBubType((byte) currCopyType, NDungeonDefine.SUBTYPE);
        }
        if (currCopyType == NDungeonDefine.NDungeon.CAILIAO.getType()) {
            clCopy.getPassed().add(currCopyId);

            clCopy.getCLCopyDaily(currCopyId).setMianfei((byte) 1);
            gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.COPY_CAILIAO, 1, enumSet));
        } else if (currCopyType == NDungeonDefine.NDungeon.SHUIJINGGONG.getType()) {
            player.setSjgCopyId(currCopyId);
            enumSet.add(EPlayerSaveType.SJGCOPY);
            clCopy.addPass();
        } else if (currCopyType == NDungeonDefine.NDungeon.TIANMEN.getType()) {
            clCopy.addPass();
            if (player.getTmMaxCopyId() < clCopy.getPass()) {
                player.setTmMaxCopyId(clCopy.getPass());
                enumSet.add(EPlayerSaveType.TMMAXCOPYID);
            }
        } else if (currCopyType == NDungeonDefine.NDungeon.GERENBOSS.getType()) {
            clCopy.getPassed().add(currCopyId);
            gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.COPY_GEREN, clCopy.getPass(), enumSet));
        } else if (currCopyType == NDungeonDefine.NDungeon.MIZANG.getType()) {
            CLCopyDaily da = clCopy.getCLCopyDaily(currCopyId);
            if (da.getButCnt() > 0) {
                isAddRewad = false;
            } else {
                da.setButCnt((byte) 1);
            }

            setMiZangStar((byte) 4, da);
            getMiZiFistReward(da, enumSet);
            player.setMiSartTotal(getTotalStar());
            enumSet.add(EPlayerSaveType.MZSTAR);
        } else if (currCopyType == NDungeonDefine.NDungeon.ZHONGDAN.getType()) {
            player.getNrcData().addZhongdaoCopyCnt();
            player.getNrcData().addTotalZKCopyCnt((byte) 1);
            enumSet.add(EPlayerSaveType.RICHANG);
            gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.COPY_ZHOMGKUI, clCopy.getPass(), enumSet));
            randomStar();
        }
        clCopy.addTotalCount();
        if (isAddRewad) {
            gameRole.getPackManager().addGoods(rewardList, EGoodsChangeType.FIGHT_DUNGEON_ADD, enumSet);
        }

        if (currCopyType == NDungeonDefine.NDungeon.SHUIJINGGONG.getType()) {
            NGameRankManager.getInstance().updataRank(player, NRankType.COPY_SJG, player.getSjgCopyId());
        } else if (currCopyType == NDungeonDefine.NDungeon.TIANMEN.getType()) {
            NGameRankManager.getInstance().updataRank(player, NRankType.COPY_TM, player.getTmMaxCopyId());
        } else if (currCopyType == NDungeonDefine.NDungeon.MIZANG.getType()) {
            CLCopyDaily da = clCopy.getDailyTimes().get(currCopyId);
            NGameRankManager.getInstance().updataRank(player, NRankType.COPY_MZ, player.getMiSartTotal());
        }
        gameRole.getDbManager().copyDao.updateDungeon(clCopy);
        return isAddRewad;
    }

    /**
     * 根据回合数来判断 星级
     * 战斗≤4回合，评分3星
     * 战斗≤7回合，＞4回合，评分2星
     * 战斗≤10回合，＞7回合，评分1星
     */
    private void setMiZangStar(byte huiheCnt, CLCopyDaily da) {
        byte star = 0;
        if (huiheCnt <= 4) {
            star = 3;
        } else if (huiheCnt > 4 && huiheCnt <= 7) {
            star = 2;
        } else if (huiheCnt > 7 && huiheCnt <= 10) {
            star = 1;
        }
        if (da.getStar() > star) {
            return;
        }
        da.setStar(star);

    }


    /***
     *
     * 领取密藏首次通关奖励 永久只能领取一次
     */
    private void getMiZiFistReward(CLCopyDaily da, EnumSet<EPlayerSaveType> enumSet) {
        if (da == null) {
            return;
        }
        if (da.getTgsj() > 0) {
            return;
        }
        NMiZangCopyData data = NCopyModel.getNMiZangCopyDataById((byte) currCopyId);
        if (data == null) {
            return;
        }
        rewardList.addAll(data.getFirst());
        da.setTgsj((byte) 1);
    }


    /**
     * 添加掉落到人 身上
     */
    public void addDropToRole(EGoodsChangeType changeType, EnumSet<EPlayerSaveType> enumSet) {
        gameRole.getPackManager().addGoods(rewardList, changeType, enumSet);
        clearFightDrop();
    }


    /**
     * 副本扫荡
     **/
    public void processCopySweep(Message request) {
        byte type = request.readByte();
        byte id = request.readByte();
        CLCopy clCopy = getCLCopyByTypeAndBubType(type, NDungeonDefine.SUBTYPE);
        if (!clCopy.getPassed().contains(id)) {
            return;
        }
        CLCopyDaily daily = clCopy.getCLCopyDaily(id);
        if (type != NDungeonDefine.NDungeon.GERENBOSS.getType()) {
            daily = clCopy.getCLCopyDaily(id);
        } else {
            daily = clCopy.getDailyTimes().get(id);
        }
        if (daily == null) {
            return;
        }

        if (sweepCon(request, type, clCopy, daily, id)) {
            EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
            this.addDropToRole(EGoodsChangeType.FIGHT_DUNGEON_ADD, enumSet);
            Message msg = new Message(EMessage.COPY_SWEEP.CMD(), request.getChannel());
            msg.setByte(type);
            msg.setByte(id);
            if (type == NDungeonDefine.NDungeon.GERENBOSS.getType()) {
                msg.setByte(2);
                gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.COPY_GEREN, clCopy.getPass(), enumSet));
            } else {
                if (type == NDungeonDefine.NDungeon.CAILIAO.getType()) {
                    gameRole.getEventManager().notifyEvent(new GameEvent(EGameEventType.COPY_CAILIAO, clCopy.getPass(), enumSet));
                }
                msg.setByte(daily.getButCnt());
            }
            gameRole.sendMessage(msg);
            CopyData data = null;
            if (type == NDungeonDefine.NDungeon.CAILIAO.getType()) {
                data = NCopyModel.getCLCopyDataMap(id);

            } else if (type == NDungeonDefine.NDungeon.GERENBOSS.getType()) {
                data = NCopyModel.getNGeRenBossDataById(id);
            }
            if (data != null) {
                gameRole.getPackManager().addGoods(data.getDropDataList(), EGoodsChangeType.FIGHT_DUNGEON_ADD, enumSet);
            }

            gameRole.getDbManager().playerDao.savePlayer(player, enumSet);
            gameRole.getDbManager().copyDao.updateDungeon(clCopy);
        } else {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_NUM_MAX);
        }
    }


    /**
     * 不同类型的副本扫荡满足条件
     */
    private boolean sweepCon(Message request, byte type, CLCopy clCopy, CLCopyDaily daily, byte id) {

        if (type == NDungeonDefine.NDungeon.CAILIAO.getType()) {
            return isConClSweep(request, clCopy, daily, id);
        } else if (type == NDungeonDefine.NDungeon.GERENBOSS.getType()) {

            if (daily.getButCnt() > 1) {
                return false;
            }
            daily.addButCnt();

            return true;
        }

        return false;
    }

    /**
     * 是否满足材料副本扫荡条件
     */
    private boolean isConClSweep(Message request, CLCopy clCopy, CLCopyDaily daily, byte id) {
        NCaiLiaoCopyData data = NCopyModel.getCLCopyDataMap(id);
        if (daily.getMianfei() < 1 || daily.getButCnt() < data.getCountByteVip(player.getVipLevel())) {
            EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
            if (daily.getMianfei() > 0) {
                if (!gameRole.getPackManager().costCurrency(EGoodsType.DIAMOND, data.getPrice(), EGoodsChangeType.DUNGEON_SWEEP_CONSUME, enumSet)) {
                    gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_LESS);
                    return false;
                }
                daily.addButCnt();

            } else {
                daily.setMianfei((byte) 1);
            }
            return true;
        } else {
            return false;
        }
    }


    /**
     * 天门副本一键扫荡
     **/
    public void processTMCopyOneKeySweep(Message request) {
        CLCopy clCopy = getCLCopyByTypeAndBubType((byte) NDungeonDefine.NDungeon.TIANMEN.getType(), NDungeonDefine.SUBTYPE);
        if (clCopy.getSweep() < 1) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_NOHAVE_GUANQIA);
            return;
        }
        if (player.getTmMaxCopyId() < 1) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_NOHAVE_GUANQIA);
            return;
        }

        Message msg = new Message(EMessage.COPY_TM_ONEKEY_SWEEP.CMD(), request.getChannel());
        Map<Integer, NTianMenData> map = NCopyModel.getTianMenMap();
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        for (NTianMenData data : map.values()) {
            if (player.getTmMaxCopyId() < data.getId()) {
                break;
            }
            clCopy.addPass();
            gameRole.getPackManager().addGoods(data.getDropDataList(), EGoodsChangeType.FIGHT_DUNGEON_ADD, enumSet);
            //this.addDropToRole(EGoodsChangeType.FIGHT_DUNGEON_ADD,enumSet);
        }
        msg.setBool(true);
        gameRole.sendMessage(msg);
        clCopy.setSweep((byte) 0);
        gameRole.getDbManager().playerDao.savePlayer(player, enumSet);
        gameRole.getDbManager().copyDao.updateDungeon(clCopy);
    }

    /**
     * 天门挑战面板打开
     */
    public void processTMCopyPanel(Message request) {
        CLCopy clCopy = getCLCopyByTypeAndBubType((byte) NDungeonDefine.NDungeon.TIANMEN.getType(), NDungeonDefine.SUBTYPE);
        Message msg = new Message(EMessage.COPY_TM_PANEL.CMD(), request.getChannel());
        msg.setByte(clCopy.getPass());
        msg.setByte(player.getTmMaxCopyId());
        NTianMenDBDataMessage(msg);
        msg.setBool(state);
        gameRole.sendMessage(msg);
    }

    private int pageCount = 3;

    private void NTianMenDBDataMessage(Message msg, int currPage) {
        if (currPage < 1) {
            currPage = 1;
        }
        List<NTianMenDBData> data = NCopyModel.getNTianMenDBDataList();
        int end = currPage * pageCount;
        int start = (currPage - 1) * pageCount;
        if (end > data.size()) {
            end = data.size();
        }
        List<NTianMenDBData> temp = data.subList(start, end);
        CLCopy clCopy = getCLCopyByTypeAndBubType((byte) NDungeonDefine.NDungeon.TIANMEN.getType(), NDungeonDefine.SUBTYPE);
        msg.setByte(temp.size());
        int count = 0;
        for (NTianMenDBData nTianMenDBData : temp) {
            msg.setByte(nTianMenDBData.getId());
            if (clCopy.getPassed() == null) {
                msg.setBool(false);
            } else {
                boolean islingqu = clCopy.getPassed().contains(nTianMenDBData.getId());
                msg.setBool(islingqu);
            }
        }
        msg.setByte(currPage);

    }


    private void NTianMenDBDataMessage(Message msg) {
        CLCopy clCopy = getCLCopyByTypeAndBubType((byte) NDungeonDefine.NDungeon.TIANMEN.getType(), NDungeonDefine.SUBTYPE);
        List<NTianMenDBData> data = NCopyModel.getNTianMenDBDataList();
        int id = 0;
        for (NTianMenDBData nTianMenDBData : data) {
            if (!clCopy.getPassed().contains(nTianMenDBData.getId())) {
                id = nTianMenDBData.getId();
                break;
            }
        }
        int currpage = id % pageCount == 0 ? id / pageCount : id / pageCount + 1;
        int end = currpage * pageCount;
        int start = (currpage - 1) * pageCount;
        if (end > data.size()) {
            end = data.size();
        }
        List<NTianMenDBData> temp = data.subList(start, end);
        msg.setByte(temp.size());
        for (NTianMenDBData nTianMenDBData : temp) {
            msg.setInt(nTianMenDBData.getId());
            if (clCopy.getPassed() == null) {
                msg.setBool(false);
            } else {
                boolean islingqu = clCopy.getPassed().contains(nTianMenDBData.getId());
                msg.setBool(islingqu);
            }
        }
        msg.setByte(currpage);

    }


    /**
     * 天门达标奖励数据
     */
    public void processTMDBData(Message request) {
        byte page = request.readByte();
        Message msg = new Message(EMessage.COPY_TMDB_DATA.CMD(), request.getChannel());
        NTianMenDBDataMessage(msg, page);
        gameRole.sendMessage(msg);
    }


    /**
     * 领取天门挑战额外奖励
     */
    public void processTMCopyGetReward(Message request) {
        byte id = request.readByte();
        CLCopy clCopy = getCLCopyByTypeAndBubType((byte) NDungeonDefine.NDungeon.TIANMEN.getType(), NDungeonDefine.SUBTYPE);
        if (clCopy.getPassed().contains(id)) {
            return;
        }
        NTianMenDBData data = NCopyModel.getTianMenDBCopyDataMap(id);
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        if (data == null) {
            return;
        }
        Message msg = new Message(EMessage.COPY_TM_GET_REWARD.CMD(), request.getChannel());
        clCopy.getPassed().add(data.getId());
        gameRole.getPackManager().addGoods(data.getDropDataList(), EGoodsChangeType.FIGHT_DUNGEON_ADD, enumSet);
        msg.setByte(id);
        msg.setByte(true);
        gameRole.sendMessage(msg);
        boolean isAlllingqu = currPageIsLingQu(id, clCopy);
        if (isAlllingqu) {
            NTianMenDBDataMessage(msg);
        }
        gameRole.getDbManager().playerDao.savePlayer(player, enumSet);
        gameRole.getDbManager().copyDao.updateDungeon(clCopy);
    }


    private boolean currPageIsLingQu(byte id, CLCopy clCopy) {
        List<NTianMenDBData> data = NCopyModel.getNTianMenDBDataList();
        int currpage = id % pageCount == 0 ? id / pageCount : id / pageCount + 1;
        int end = currpage * pageCount;
        int start = (currpage - 1) * pageCount;
        if (end > data.size()) {
            end = data.size();
        }
        List<NTianMenDBData> temp = data.subList(start, end);
        int count = 0;
        for (NTianMenDBData nTianMenDBData : temp) {
            if (clCopy.getPassed().contains(nTianMenDBData.getId())) {
                count++;
            }
        }
        if (count >= pageCount) {
            return true;
        }
        return false;

    }


    /**
     * 天门挑战排行榜列表
     */
    public void processTMRankPanel(Message request) {
        byte myrank = NGameRankManager.getInstance().getPlayerRankByPlayerId(player.getId(), NRankType.COPY_TM);
        List<PlayerRank> ranks = NGameRankManager.getInstance().getRankByType(NRankType.COPY_TM);
        Message msg = new Message(EMessage.COPY_TM_RANK.CMD(), request.getChannel());
        if (ranks == null) {
            msg.setByte(0);
            msg.setByte(0);
            msg.setByte(0);
            gameRole.sendMessage(msg);
            return;
        }
        msg.setByte(myrank);
        msg.setByte(player.getTmMaxCopyId());
        msg.setByte(ranks.size());
        for (int i = 0; i < ranks.size(); i++) {
            PlayerRank playerRank = ranks.get(i);
            msg.setString(playerRank.getName());
            msg.setByte((byte) playerRank.getValue());
            msg.setByte(i + 1);
            int id = playerRank.getId();
            IGameRole role = GameWorld.getPtr().getGameRole(id);
            long fight = playerRank.getFighting();
            if (role != null) {
                fight = role.getPlayer().getFighting();
            }
            msg.setLong(fight);
        }
        gameRole.sendMessage(msg);
    }

    /**
     * 密藏面板
     */
    public void processMiZangPanel(Message request) {
        byte subType = request.readByte();
        NMiZangCopyData curr = getNCopy();
        if (subType == 0) {
            subType = (byte) curr.getType();
        }

        CLCopy clCopy = getCLCopyByTypeAndBubType((byte) NDungeonDefine.NDungeon.MIZANG.getType(), subType);
        Message msg = new Message(EMessage.COPY_MIZANG_PANEL.CMD(), request.getChannel());
        msg.setByte(subType);
        msg.setByte(curr.getId());
        if (clCopy.getDailyTimes() == null) {
            msg.setByte(0);
        } else {
            int size = clCopy.getDailyTimes().size();
            msg.setByte(size);
            for (Map.Entry<Integer, CLCopyDaily> da : clCopy.getDailyTimes().entrySet()) {
                msg.setByte(da.getKey());
                msg.setByte(da.getValue().getStar());
                byte isget = da.getValue().getTgsj();
                msg.setByte(isget);
                msg.setBool(da.getValue().getButCnt() > 0);


            }
        }
        NMiZangStarData star = NCopyModel.getNMiZangStarDataById(subType);
        Map<Byte, List<DropData>> reward = star.getMapReward();
        msg.setByte(reward.size());
        for (Map.Entry<Byte, List<DropData>> data : reward.entrySet()) {
            msg.setByte(data.getKey());
            if (clCopy.getPassed().contains(data.getKey())) {
                msg.setByte(2);
            } else {
                if (isGetReward(clCopy, data.getKey())) {
                    msg.setByte(1);
                } else {
                    msg.setByte(0);
                }
            }
        }
        msg.setBool(state);

        gameRole.sendMessage(msg);
    }


    private boolean isGetReward(CLCopy clCopy, byte starnum) {
        if (gettotalStar(clCopy) >= starnum) {
            return true;
        }
        return false;
    }

    /***
     * 获取某个大关卡的总星数
     */
    private byte gettotalStar(CLCopy clCopy) {
        Map<Integer, CLCopyDaily> map = clCopy.getDailyTimes();
        if (map == null || map.size() < 1) {
            return 0;
        }
        byte total = 0;
        for (CLCopyDaily da : map.values()) {
            total += da.getStar();

        }

        return total;
    }


    private byte getTotalStar() {
        Map<Integer, NMiZangStarData> dataMap = NCopyModel.getNMiZangStarDataMap();
        byte total = 0;
        for (NMiZangStarData data : dataMap.values()) {
            CLCopy clCopy = getCLCopyByTypeAndBubType((byte) NDungeonDefine.NDungeon.MIZANG.getType(), data.getId());
            int count = gettotalStar(clCopy);
            total += count;
        }
        return total;
    }

    /**
     * 密藏一键扫荡
     */
    public void processMiZangOneKeySeep(Message request) {
        if (player.getVipLevel() < 4) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_VIP_LEVEL_LESS);
            return;
        }

        CLCopy cl = getCLCopyByTypeAndBubType((byte) NDungeonDefine.NDungeon.MIZANG.getType(), (byte) 1);
//		if(cl.getSweep()<1) {
//			gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_NOHAVE_GUANQIA);
//			return;
//		}
        Map<Integer, NMiZangStarData> dataMap = NCopyModel.getNMiZangStarDataMap();
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        byte flg = 0;
        for (NMiZangStarData data : dataMap.values()) {
            CLCopy clCopy = getCLCopyByTypeAndBubType((byte) NDungeonDefine.NDungeon.MIZANG.getType(), data.getId());
            Map<Integer, CLCopyDaily> map = clCopy.getDailyTimes();
            if (map.isEmpty()) {
                continue;
            }
            if (clCopy.getSweep() < 1) {
                gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_NOHAVE_GUANQIA);
                return;
            }

            for (CLCopyDaily clCopyDaily : clCopy.getDailyTimes().values()) {
                if (clCopyDaily.getStar() < NDungeonDefine.MAX_STAR) {
                    break;
                }
                NMiZangCopyData mz = NCopyModel.getNMiZangCopyDataById(clCopyDaily.getId());
                gameRole.getPackManager().addGoods(mz.getDropDataList(), EGoodsChangeType.FIGHT_DUNGEON_ADD, enumSet);
                clCopyDaily.setButCnt((byte) 1);
                flg++;
                this.addDropToRole(EGoodsChangeType.FIGHT_DUNGEON_ADD, enumSet);
            }
        }

        if (flg <= 0) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_NOHAVE_GUANQIA);
            return;
        }
        Message msg = new Message(EMessage.COPY_MIZANG_SWEEP.CMD(), request.getChannel());
        msg.setBool(true);
        gameRole.sendMessage(msg);
        for (NMiZangStarData data : dataMap.values()) {
            CLCopy clCopy = getCLCopyByTypeAndBubType((byte) NDungeonDefine.NDungeon.MIZANG.getType(), data.getId());
            clCopy.setSweep((byte) 0);
        }
        gameRole.getDbManager().playerDao.savePlayer(player, enumSet);
        gameRole.getDbManager().copyDao.updateDungeon(cl);
    }


    private NMiZangCopyData getNCopy() {
        Map<Integer, NMiZangStarData> dataMap = NCopyModel.getNMiZangStarDataMap();
        NMiZangCopyData copyData = null;
        int total = 0;//记录大关卡中小关卡是否全满星

        for (NMiZangStarData data : dataMap.values()) {
            CLCopy clCopy = getCLCopyByTypeAndBubType((byte) NDungeonDefine.NDungeon.MIZANG.getType(), data.getId());
            List<NMiZangCopyData> copyList = NCopyModel.getNMiZangCopyDataByStarId(data.getId());
            Map<Integer, CLCopyDaily> copyDa = clCopy.getDailyTimes();
            if (copyDa == null || copyDa.isEmpty()) {

                return copyList.get(0);
            }
            if (copyList.size() > copyDa.size()) {
                copyData = copyList.get(copyDa.size());
                return copyData;
            }

            List<CLCopyDaily> list = new ArrayList<>();
            int FullCnt = 0;
            for (CLCopyDaily da : copyDa.values()) {
                if (da.getStar() >= NDungeonDefine.MAX_STAR) {
                    FullCnt++;
                }
                list.add(da);
            }
            if (FullCnt >= copyList.size()) {
                total++;
                continue;
            }
            Collections.sort(list);
            CLCopyDaily da = list.get(list.size() - 1);
            int id = da.getId();
            return copyList.get(id);

        }

        int size = NCopyModel.getNMiZangCopyDataMap().size();
        if (dataMap.size() >= total) {
            return NCopyModel.getNMiZangCopyDataById((byte) (size - 1));
        }

        return NCopyModel.getNMiZangCopyDataById((byte) 0);
    }


    /**
     * 获取最大关卡中小关卡累计星级的奖励
     */
    public void processMiZangGetReward(Message request) {
        byte id = request.readByte();
        byte star = request.readByte();
        NMiZangStarData data = NCopyModel.getNMiZangStarDataById(id);
        if (data == null) {
            return;
        }
        List<DropData> reward = data.getMapReward().get(star);

        if (reward == null) {
            return;
        }
        CLCopy clCopy = getCLCopyByTypeAndBubType((byte) NDungeonDefine.NDungeon.MIZANG.getType(), id);
        if (clCopy.getPassed().contains(star)) {
            return;
        }
        if (!isGetReward(clCopy, star)) {
            return;
        }
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        gameRole.getPackManager().addGoods(data.getMapReward().get(star), EGoodsChangeType.FIGHT_DUNGEON_ADD, enumSet);
        Message msg = new Message(EMessage.COPY_MIZANG_GET_REWARD.CMD(), request.getChannel());
        msg.setByte(id);
        msg.setByte(star);
        msg.setByte(2);
        gameRole.sendMessage(msg);
        clCopy.getPassed().add((int) star);
        gameRole.getDbManager().playerDao.savePlayer(player, enumSet);
        gameRole.getDbManager().copyDao.updateDungeon(clCopy);
    }


    /**
     * 密藏排行榜列表
     */
    public void processMZRankPanel(Message request) {
        byte myrank = NGameRankManager.getInstance().getPlayerRankByPlayerId(player.getId(), NRankType.COPY_MZ);
        List<PlayerRank> ranks = NGameRankManager.getInstance().getRankByType(NRankType.COPY_MZ);
        Message msg = new Message(EMessage.COPY_MIZANG_RANK.CMD(), request.getChannel());
        if (ranks == null) {
            msg.setByte(0);
            msg.setByte(0);
            msg.setByte(0);
            gameRole.sendMessage(msg);
            return;
        }
        msg.setByte(myrank);
        msg.setByte(player.getMiSartTotal());
        msg.setByte(ranks.size());
        for (int i = 0; i < ranks.size(); i++) {
            PlayerRank playerRank = ranks.get(i);
            msg.setString(playerRank.getName());
            msg.setByte((byte) playerRank.getValue());
            msg.setByte(i + 1);
            int id = playerRank.getId();
            IGameRole role = GameWorld.getPtr().getGameRole(id);
            long fight = playerRank.getFighting();
            if (role != null) {
                fight = role.getPlayer().getFighting();
            }
            msg.setLong(fight);
        }
        gameRole.sendMessage(msg);
    }


    /**
     * 清除战斗掉落
     */
    private void clearFightDrop() {
        rewardList = new ArrayList<>();
    }

    /**
     * 每日重置 副本
     */
    public void reset() {
        for (CLCopy data : roleCopy.values()) {
            if (data.getDatatype() == NDungeonDefine.NDungeon.GERENBOSS.getType()) {
                data.resetGeRen();
            } else if (data.getDatatype() == NDungeonDefine.NDungeon.CAILIAO.getType()) {
                data.resetCaiLiao();
            } else if (data.getDatatype() == NDungeonDefine.NDungeon.TIANMEN.getType()) {
                data.resetTianMen();
            } else if (data.getDatatype() == NDungeonDefine.NDungeon.MIZANG.getType()) {
                Map<Integer, NMiZangStarData> dataMap = NCopyModel.getNMiZangStarDataMap();
                for (NMiZangStarData star : dataMap.values()) {
                    CLCopy clCopy = getCLCopyByTypeAndBubType((byte) NDungeonDefine.NDungeon.MIZANG.getType(), star.getId());
                    clCopy.resetMiZang();
                }
            }
        }

    }

    public void restZk() {
        CLCopy clCopy = getCLCopyByTypeAndBubType((byte) NDungeonDefine.NDungeon.ZHONGDAN.getType(), NDungeonDefine.SUBTYPE);
        if (clCopy.getTotalCount() > NDungeonDefine.ZHONGKUI_COUNT) {
            return;
        }


    }

    public void dbSaveReset() {
        for (CLCopy data : roleCopy.values()) {
            gameRole.getDbManager().copyDao.updateDungeon(data);
        }

    }

    /**
     * 钟馗副本界面
     */
    public void processZhongKuiPanel(Message request) {
        CLCopy clCopy = getCLCopyByTypeAndBubType((byte) NDungeonDefine.NDungeon.ZHONGDAN.getType(), NDungeonDefine.SUBTYPE);
        if (clCopy.getTotalCount() > NDungeonDefine.ZHONGKUI_COUNT) {
            return;
        }
        if (clCopy.getSweep() <= 0) {
            clCopy.setSweep(NDungeonDefine.ZHONGKUI_MAX_STAR);
        }
        int id = (int) (clCopy.getPass() + 1);

        NZhongKuiData data = NCopyModel.getNZhongKuiDataById(id);
        List<DropData> rward = data.getDropDataList();
        List<DropData> rewardNew = getZhongKuiRandomReward(rward, clCopy.getSweep());
        Message msg = new Message(EMessage.COPY_ZHONGKUI_PANEL.CMD(), request.getChannel());
        msg.setByte(id);
        msg.setByte(clCopy.getSweep());
        msg.setByte(rewardNew.size());
        for (DropData dropData : rewardNew) {
            msg.setInt(dropData.getG());
            msg.setInt(dropData.getN());
            msg.setByte(dropData.getT());
        }
        int baifenbi = zhongkuiRewardBaiFenBi(clCopy.getSweep());
        msg.setInt(data.getExp() * baifenbi / 100);
        gameRole.sendMessage(msg);

        gameRole.getDbManager().copyDao.updateDungeon(clCopy);

    }


    /**
     *
     *
     * */
    private List<DropData> getZhongKuiRandomReward(List<DropData> rward, int star) {
        int baifenbi = zhongkuiRewardBaiFenBi(star);
        List<DropData> temp = new ArrayList<>();
        for (DropData dropData : rward) {
            DropData data = new DropData();
            int num = dropData.getN() * baifenbi / 100;

            data.setG(dropData.getG());
            data.setN(num);
            data.setQ(dropData.getQ());
            data.setT(dropData.getT());
            temp.add(data);
        }
        return temp;

    }


    /**
     * 1星	100%奖励
     * 2星	115%奖励
     * 3星	130%奖励
     * 4星	145%奖励
     * 5星	160%奖励
     * 6星	180%奖励
     * 7星	200奖%励
     */
    private int zhongkuiRewardBaiFenBi(int star) {
        int baifenbi = 0;
        switch (star) {
            case 1:
                baifenbi = 100;
                break;
            case 2:
                baifenbi = 115;
                break;
            case 3:
                baifenbi = 130;
                break;
            case 4:
                baifenbi = 145;
                break;
            case 5:
                baifenbi = 160;
                break;
            case 6:
                baifenbi = 180;
                break;
            case 7:
                baifenbi = 200;
                break;
        }
        return baifenbi;

    }

    /**
     * 钟馗购买星级
     */
    public void processZhongKuiBuySar(Message request) {
        CLCopy clCopy = getCLCopyByTypeAndBubType((byte) NDungeonDefine.NDungeon.ZHONGDAN.getType(), NDungeonDefine.SUBTYPE);

        if (clCopy.getSweep() >= NDungeonDefine.ZHONGKUI_MAX_STAR) {
            return;
        }
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        DropData cost = new DropData(EGoodsType.DIAMOND, 0, 50);
        if (!gameRole.getPackManager().useGoods(cost, EGoodsChangeType.SKILL_UP_CONSUME, enumSet, false)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_NOT_ENOUGHT);
            return;
        }
        clCopy.setSweep(NDungeonDefine.ZHONGKUI_MAX_STAR);
        Message msg = new Message(EMessage.COPY_ZHONGKUI_BUY_STAR.CMD(), request.getChannel());
        msg.setByte((byte) 1);
        gameRole.sendMessage(msg);
        gameRole.getDbManager().copyDao.updateDungeon(clCopy);
    }


}


