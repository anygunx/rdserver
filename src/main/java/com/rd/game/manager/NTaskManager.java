package com.rd.game.manager;

import com.rd.bean.drop.DropData;
import com.rd.bean.player.Player;
import com.rd.bean.task.handler.DailyTaskHandler;
import com.rd.bean.task.handler.ITaskHandler;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.define.ErrorDefine;
import com.rd.enumeration.EMessage;
import com.rd.game.GameRole;
import com.rd.game.event.GameEvent;
import com.rd.game.event.IEventListener;
import com.rd.model.NTaskModel;
import com.rd.model.data.task.NLiLianData;
import com.rd.model.data.task.NTaskLiLianData;
import com.rd.net.message.Message;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class NTaskManager implements IEventListener {


    private static final Logger logger = Logger.getLogger(NSkinMnager.class);

    private GameRole gameRole;
    private Player player;
    private List<ITaskHandler> handlers = new ArrayList<>();

    public NTaskManager(GameRole role) {
        this.gameRole = role;
        this.player = role.getPlayer();
        addFilters();
    }


    public void init() {
        updateDailyProcess(player.getDailyProgress());
    }


    private void addFilters() {
        handlers.add(DailyTaskHandler.getInstance());
    }


    public void handleEvent(GameEvent event) {
        for (ITaskHandler handler : handlers) {
            handler.handleEvent(gameRole, event);
        }
        this.saveEventData(event);
    }


    private void saveEventData(GameEvent event) {
        //处理需要接收者存储的数据集
        if (event.getSelfPlaySave() != null) {
            gameRole.savePlayer(event.getSelfPlaySave());
        }
    }


    /**
     * 每日重置
     */
    public void reset() {
        updateDailyProcess(null);
    }


    private void updateDailyProcess(short[] process) {
        if (process == null) {
            short[] dailyProgress = new short[NTaskModel.getTaskLiLianDailyDataMap().size() + 1];
            short[] yesterDateProgress = new short[NTaskModel.getTaskLiLianDailyDataMap().size() + 1];
            for (NTaskLiLianData data : NTaskModel.getTaskLiLianDailyDataMap().values()) {
                short count = dailyProgress[data.getId()];
                if (count > 0) {
                    yesterDateProgress[data.getId()] = count;
                } else {
                    yesterDateProgress[data.getId()] = 0;
                }
                dailyProgress[data.getId()] = 0;
            }
            player.setDailyProgress(dailyProgress);
            player.setYesterdateProgress(yesterDateProgress);
        } else {
            //兼容每日任务有XML新增任务
            if (process.length < NTaskModel.getTaskLiLianDailyDataMap().size() + 1) {
                short[] dailyProgress = new short[NTaskModel.getTaskLiLianDailyDataMap().size() + 1];
                for (NTaskLiLianData data : NTaskModel.getTaskLiLianDailyDataMap().values()) {
                    dailyProgress[data.getId()] = 0;
                }
                for (int i = 0; i < process.length; ++i) {
                    dailyProgress[i] = process[i];
                }
                player.setDailyProgress(dailyProgress);
            }
        }

    }

    /**
     * 找回
     */
    public void proccessTaskZhaoHui(Message request) {
        byte id = request.readByte();
        if (id < 0 || id > NTaskModel.getTaskLiLianDailyDataMap().size() + 1) {
            return;
        }
//		if(id==0) {
//			proccessZhaoHuiOneKey(request);
//			return;
//		}
        short[] yestedate = player.getYesterdateProgress();
        if (yestedate == null) {
            return;
        }

//		
//		if(data==null) {
//			return;
//		}
        DropData cost = new DropData(EGoodsType.DIAMOND, 0, 120);
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        if (!gameRole.getPackManager().useGoods(cost, EGoodsChangeType.SKILL_UP_CONSUME, enumSet, false)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_NOT_ENOUGHT);
            return;
        }
        int totalexp = 0;
        if (id == 0) {
            for (int i = 1; i < yestedate.length; i++) {
                short cnt = yestedate[i];
                NTaskLiLianData data1 = NTaskModel.getTaskLiLianDailyData(i);
                int shengyu = data1.getTarget() - cnt;
                if (shengyu <= 0) {
                    continue;
                }
                int addExp = data1.getLilianExp() * shengyu;
                totalexp += addExp;
                yestedate[i] = (short) data1.getTarget();
            }
        } else {
            short count = yestedate[id];
            NTaskLiLianData data = NTaskModel.getTaskLiLianDailyData(id);
            if (data == null) {
                return;
            }
            int shengyu = data.getTarget() - count;
            if (shengyu > 0) {
                totalexp = data.getLilianExp() * shengyu;
                yestedate[id] = (short) data.getTarget();
            }
        }

        player.addLiLianExp(totalexp);
        Message msg = new Message(EMessage.TASK_ZHAOHUI.CMD(), request.getChannel());
        msg.setByte(id);
        msg.setInt(player.getLiLianExp());
        gameRole.sendMessage(msg);
        enumSet.add(EPlayerSaveType.LILIAN_EXP);
        gameRole.savePlayer(enumSet);

    }

    /**
     * 一键找回
     */
    public void proccessZhaoHuiOneKey(Message request) {
        short[] yestedate = player.getYesterdateProgress();
        if (yestedate == null) {
            return;
        }
        DropData cost = new DropData(EGoodsType.DIAMOND, 0, 120);
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        if (!gameRole.getPackManager().useGoods(cost, EGoodsChangeType.SKILL_UP_CONSUME, enumSet, false)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_NOT_ENOUGHT);
            return;
        }
        int totalexp = 0;
        for (int i = 1; i < yestedate.length; i++) {
            short count = yestedate[i];
            NTaskLiLianData data = NTaskModel.getTaskLiLianDailyData(i);
            int shengyu = data.getTarget() - count;
            int addExp = data.getLilianExp() * shengyu;
            totalexp += addExp;
            yestedate[i] = (short) data.getTarget();
        }
        player.addLiLianExp(totalexp);
        Message msg = new Message(EMessage.TASK_ZHAOHUI_ONEKEY.CMD(), request.getChannel());
        msg.setByte(1);
        gameRole.sendMessage(msg);
        enumSet.add(EPlayerSaveType.LILIAN_EXP);
        gameRole.savePlayer(enumSet);

    }

    /***
     *
     * 找回面板
     *
     * */
    public void processZHaoHuiPanel(Message request) {
        Message msg = new Message(EMessage.TASK_ZHAOHUI_PANEL.CMD(), request.getChannel());
        player.setYesterdateProgress(new short[]{1, 2, 3, 4});
        if (player.getYesterdateProgress() == null) {
            msg.setByte(0);
        } else {
            msg.setByte(player.getYesterdateProgress().length - 1);
            for (int i = 1; i < player.getDailyProgress().length; ++i) {
                msg.setByte(i);
                msg.setByte(player.getDailyProgress()[i]);
            }
        }

        gameRole.sendMessage(msg);
    }

    /**
     * 任务面板信息
     */
    public void processTaskPanel(Message request) {
        Message msg = new Message(EMessage.TASK_PANEL.CMD(), request.getChannel());
        msg.setInt(player.getLiLianExp());
        msg.setShort(player.getLiLianLevel());
        msg.setByte(player.getDailyProgress().length - 1);
        for (int i = 1; i < player.getDailyProgress().length; ++i) {
            msg.setByte(i);
            msg.setByte(player.getDailyProgress()[i]);
        }
        gameRole.sendMessage(msg);
    }

    /**
     *
     *
     *
     * **/
    public void processUpgrade(Message request) {
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        addLiLianExp(0, enumSet);
        Message msg = new Message(EMessage.TASK_UPGRADE.CMD(), request.getChannel());
        msg.setInt(player.getLiLianExp());
        msg.setShort(player.getLiLianLevel());
        gameRole.sendMessage(msg);
        enumSet.add(EPlayerSaveType.LILIAN_EXP);
        enumSet.add(EPlayerSaveType.LILIAN_LEVEL);
        gameRole.savePlayer(enumSet);


    }


    /**
     * 升级
     */
    public void addLiLianExp(int exp, EnumSet<EPlayerSaveType> enumSet) {
        int maxLevel = NTaskModel.getNLiLianDataMap().size();
        if (player.getLiLianLevel() >= maxLevel) {
            return;
        }
        NLiLianData data = NTaskModel.getNLiLianData(player.getLiLianLevel());
        if (data == null) {
            return;
        }

        int reult = player.getLiLianExp() + 0;
        int total = data.getNeedExp();
        if (reult < total) {
            return;
        }
        short level = player.getLiLianLevel();
        List<DropData> rewards = new ArrayList<>();
        rewards.addAll(data.getRewards());
        while (reult >= total) {
            ++level;
            if (level > maxLevel) {
                level = (short) maxLevel;
                reult = 0;
                break;
            }
            reult -= total;
            data = NTaskModel.getNLiLianData(level);
            total = data.getNeedExp();
            rewards.addAll(data.getRewards());
        }
        player.setLiLianExp(reult);
        player.setLiLianLevel(level);
        gameRole.getPackManager().addGoods(rewards, EGoodsChangeType.FIGHT_DUNGEON_ADD, enumSet);

    }

}
