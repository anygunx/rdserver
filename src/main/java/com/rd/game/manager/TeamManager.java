package com.rd.game.manager;

import com.rd.bean.drop.DropData;
import com.rd.bean.mail.Mail;
import com.rd.bean.player.Player;
import com.rd.bean.team.Team;
import com.rd.bean.team.TeamRecord;
import com.rd.combat.CombatSystem;
import com.rd.common.ChatService;
import com.rd.common.MailService;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.*;
import com.rd.enumeration.EMessage;
import com.rd.game.*;
import com.rd.model.TeamModel;
import com.rd.model.data.CrossDunData;
import com.rd.model.data.LADDisasterData;
import com.rd.net.message.Message;
import com.rd.task.ETaskType;
import com.rd.task.Task;
import com.rd.task.TaskManager;
import com.rd.util.DateUtil;

import java.util.*;
import java.util.Map.Entry;

/**
 * @author ---
 * @version 1.0
 * @date 2018年6月9日下午2:31:36
 */
public class TeamManager {

    private GameRole role;

    private Player player;

    public TeamManager(GameRole role) {
        this.role = role;
        this.player = role.getPlayer();
    }

    /**
     * 260 跨服副本队伍列表
     *
     * @param request
     */
    public void processCrossList(Message request) {
        byte dungeonId = request.readByte();

        Integer teamID = GameTeamManager.getPtr().getTeamID(TeamDef.CROSS, player.getId(), dungeonId);
        if (teamID == null) {
            Map<Integer, Team> map = GameTeamManager.getPtr().getCrossTeam(dungeonId);

            Message message = new Message(request.getCmdId(), request.getChannel());
            message.setByte(dungeonId);
            message.setByte(map.size());
            for (Entry<Integer, Team> entry : map.entrySet()) {
                message.setInt(entry.getKey());
                entry.getValue().getMessage(message);
            }
            role.sendMessage(message);
        } else {
            this.sendCrossTeamInfo(request, dungeonId, teamID);
        }
    }

    /**
     * 261 跨服副本创建队伍
     *
     * @param request
     */
    public void processCrossCreate(Message request) {
        byte id = request.readByte();
        CrossDunData data = TeamModel.getCrossDunMap().get(id);
        if (player.getLevel() < data.getNeedLevel()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_LEVEL_LESS);
            return;
        }

        int tid = GameTeamManager.getPtr().createCrossTeam(id, role);
        if (tid == 0) {
            role.sendErrorTipMessage(request, ErrorDef.TEAM_EXIST);
            return;
        }

        //广播
        String content = id + "," + tid;
        ChatService.broadcastPlayerMsg(player, EBroadcast.TEAM_CROSS, content);

        this.sendCrossTeamInfo(request, id, tid);
    }

    /**
     * 262 跨服副本加入队伍
     *
     * @param request
     */
    public void processCrossJoin(Message request) {
        byte id = request.readByte();
        int tid = request.readInt();

        short state = GameTeamManager.getPtr().joinCrossTeam(id, tid, role);
        if (state != ErrorDef.NONE) {
            role.sendErrorTipMessage(request, state);
            return;
        }

        this.sendCrossTeamInfo(request, id, tid);
    }

    /**
     * 263 跨服副本队伍信息
     *
     * @param request
     */
    public void processCrossInfo(Message request) {
        byte id = request.readByte();
        int tid = request.readInt();

        this.sendCrossTeamInfo(request, id, tid);
    }

    /**
     * 264 跨服副本队伍踢人
     *
     * @param request
     */
    public void processCrossKick(Message request) {
        byte id = request.readByte();
        int tid = request.readInt();
        int pid = request.readInt();

        short state = GameTeamManager.getPtr().teamCrossKick(id, tid, pid);
        if (state != ErrorDef.NONE) {
            role.sendErrorTipMessage(request, state);
            return;
        }

        Message message = new Message(request.getCmdId(), request.getChannel());
        message.setByte(id);
        message.setInt(tid);
        message.setInt(pid);
        role.sendMessage(message);
    }

    /**
     * 265 跨服副本队伍退出
     *
     * @param request
     */
    public void processCrossExit(Message request) {
        byte id = request.readByte();
        int tid = request.readInt();

        short state = GameTeamManager.getPtr().teamCrossExit(id, tid, player.getId());
        if (state != ErrorDef.NONE) {
            role.sendErrorTipMessage(request, state);
            return;
        }

        Message message = new Message(request.getCmdId(), request.getChannel());
        message.setByte(id);
        message.setInt(tid);
        role.sendMessage(message);
    }

    /**
     * 266 跨服副本开始
     *
     * @param request
     */
    public void processCrossStart(Message request) {
        byte id = request.readByte();
        int tid = request.readInt();

        Team team = GameTeamManager.getPtr().crossTeamStart(id, tid);
        if (team == null) {
            role.sendErrorTipMessage(request, ErrorDef.TEAM_NON_EXIST);
            return;
        }

        CrossDunData data = TeamModel.getCrossDunMap().get(id);

        Message comBatMessage = new Message(request.getCmdId());
        boolean state = CombatSystem.pveTeam(comBatMessage, team.getMember(), data.getBoss(), data.getMonster(), CombatDef.ROUND_FIVE);

        Message message = new Message(request.getCmdId(), request.getChannel());
        message.setByte(id);
        message.setInt(tid);
        message.setByte(state);
        if (state) {
            if (player.getSmallData().getCrossTeamStageState().contains(id)) {
                message.setByte(data.getReward2().size());
                Iterator<DropData> it = data.getReward2().iterator();
                while (it.hasNext()) {
                    it.next().getMessage(message);
                }
            } else {
                message.setByte(data.getReward1().size());
                Iterator<DropData> it = data.getReward1().iterator();
                while (it.hasNext()) {
                    it.next().getMessage(message);
                }
            }
        }
        message.writeBytes(comBatMessage.getChannelBuffer());

        for (Player p : team.getMember()) {
            IGameRole gr = GameWorld.getPtr().getGameRole(p.getId());
            if (gr.getGameRole() != null) {
                gr.getGameRole().putMessageQueue(message);
            }
        }

        role.sendTick(request);

        TaskManager.getInstance().scheduleDelayTask(ETaskType.COMMON, new Task() {

            @Override
            public void run() {

                for (Player p : team.getMember()) {
                    if (p.getDayData().getCrossDunNum() > 0) {
                        IGameRole gr = GameWorld.getPtr().getGameRole(p.getId());
                        if (gr.getGameRole() != null) {
                            Mail mail;
                            if (gr.getPlayer().getSmallData().getCrossTeamStageState().contains(id)) {
                                mail = MailService.createMail("跨服组队", "跨服组队", EGoodsChangeType.CROSS_TEAM_ADD, data.getReward2());
                            } else {
                                mail = MailService.createMail("跨服组队", "跨服组队", EGoodsChangeType.CROSS_TEAM_ADD, data.getReward2());
                            }
                            MailService.sendSystemMail(gr.getPlayer().getId(), mail);
                        }
                    }
                }
            }

            @Override
            public String name() {
                return "crossTeam";
            }
        }, DateUtil.SECOND * 5);
    }

    /**
     * 267 跨服副本结束
     *
     * @param request
     */
    public void processCrossEnd(Message request) {
        byte id = request.readByte();
        //int tid = request.readInt();

        //CrossDunData data = TeamModel.getCrossDunMap().get(id);

        Message message = new Message(request.getCmdId(), request.getChannel());
        message.setByte(id);
        message.setByte(player.getDayData().getCrossDunNum());
        role.sendMessage(message);
    }

    /**
     * 268 跨服副本关卡状态
     *
     * @param request
     */
    public void processCrossStageState(Message request) {
        Message message = new Message(request.getCmdId(), request.getChannel());
        int size = player.getSmallData().getCrossTeamStageState().size();
        message.setByte(size);
        for (int i = 0; i < size; ++i) {
            message.setByte(player.getSmallData().getCrossTeamStageState().get(i));
        }
        message.setByte(player.getDayData().getCrossDunNum());
        role.sendMessage(message);
    }

    private void sendCrossTeamInfo(Message request, byte dungeonId, int teamId) {
        Team team = GameTeamManager.getPtr().getCrossTeam(dungeonId, teamId);
        if (team == null) {
            role.sendErrorTipMessage(request, ErrorDef.TEAM_NON_EXIST);
            return;
        }

        Message message = new Message(EMessage.TEAM_CROSS_INFO.CMD(), request.getChannel());
        message.setByte(dungeonId);
        message.setInt(teamId);
        team.getMessage(message);
        role.sendMessage(message);
    }

    /**
     * 269 生死劫组队列表
     *
     * @param request
     */
    public void processLaddList(Message request) {
        byte gid = request.readByte();
        byte sid = request.readByte();

        int dungeonId = TeamDef.getLaddId(gid, sid);

        Integer teamID = GameTeamManager.getPtr().getTeamID(TeamDef.LADD, player.getId(), dungeonId);
        if (teamID == null) {
            Map<Integer, Team> map = GameTeamManager.getPtr().getLaddTeam(dungeonId);

            Message message = new Message(request.getCmdId(), request.getChannel());
            message.setByte(gid);
            message.setByte(sid);
            message.setByte(map.size());
            for (Entry<Integer, Team> entry : map.entrySet()) {
                message.setInt(entry.getKey());
                entry.getValue().getMessage(message);
            }
            role.sendMessage(message);
        } else {
            this.sendLaddTeamInfo(request, gid, sid, teamID);
        }
    }

    /**
     * 270  生死劫创建队伍
     *
     * @param request
     */
    public void processLaddCreate(Message request) {
        byte gid = request.readByte();
        byte sid = request.readByte();

        if (player.getLevel() < TeamDef.LADD_NEED_LV) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_LEVEL_LESS);
            return;
        }

        int tid = GameTeamManager.getPtr().createLaddTeam(gid, sid, role);
        if (tid == 0) {
            role.sendErrorTipMessage(request, ErrorDef.TEAM_EXIST);
            return;
        }

        //广播
        String content = gid + "," + sid + "," + tid;
        ChatService.broadcastPlayerMsg(player, EBroadcast.TEAM_LADD, content);

        this.sendLaddTeamInfo(request, gid, sid, tid);
    }

    /**
     * 271 生死劫加入队伍
     *
     * @param request
     */
    public void processLaddJoin(Message request) {
        byte gid = request.readByte();
        byte sid = request.readByte();
        int tid = request.readInt();

        short state = GameTeamManager.getPtr().joinLaddTeam(gid, sid, tid, role);
        if (state != ErrorDef.NONE) {
            role.sendErrorTipMessage(request, state);
            return;
        }

        this.sendLaddTeamInfo(request, gid, sid, tid);
    }

    /**
     * 272 生死劫队伍信息
     *
     * @param request
     */
    public void processLaddTeamInfo(Message request) {
        byte gid = request.readByte();
        byte sid = request.readByte();
        int tid = request.readInt();

        this.sendLaddTeamInfo(request, gid, sid, tid);
    }

    /**
     * 273 生死劫队长踢人
     *
     * @param request
     */
    public void processLaddTeamKick(Message request) {
        byte gid = request.readByte();
        byte sid = request.readByte();
        int tid = request.readInt();
        int pid = request.readInt();

        short state = GameTeamManager.getPtr().teamLaddKick(gid, sid, tid, pid);
        if (state != ErrorDef.NONE) {
            role.sendErrorTipMessage(request, state);
            return;
        }

        Message message = new Message(request.getCmdId(), request.getChannel());
        message.setByte(gid);
        message.setByte(sid);
        message.setInt(tid);
        message.setInt(pid);
        role.sendMessage(message);
    }

    /**
     * 274生死劫队伍退出
     *
     * @param request
     */
    public void processLaddTeamExit(Message request) {
        byte gid = request.readByte();
        byte sid = request.readByte();
        int tid = request.readInt();

        short state = GameTeamManager.getPtr().teamLaddExit(gid, sid, tid, player.getId());
        if (state != ErrorDef.NONE) {
            role.sendErrorTipMessage(request, state);
            return;
        }

        Message message = new Message(request.getCmdId(), request.getChannel());
        message.setByte(gid);
        message.setByte(sid);
        message.setInt(tid);
        role.sendMessage(message);
    }

    /**
     * 275生死劫开始战斗
     *
     * @param request
     */
    public void processLaddTeamStart(Message request) {
        byte gid = request.readByte();
        byte sid = request.readByte();
        int tid = request.readInt();

        int dungeonId = TeamDef.getLaddId(gid, sid);
        Team team = GameTeamManager.getPtr().getLaddTeam(dungeonId, tid);
        if (team == null) {
            role.sendErrorTipMessage(request, ErrorDef.TEAM_NON_EXIST);
            return;
        }
        GameTeamManager.getPtr().laddTeamStart(dungeonId, tid);

        LADDisasterData data = TeamModel.getLADDisasterData().get(dungeonId);

        Message combatMessage = new Message(request.getCmdId());
        boolean state = CombatSystem.pveTeamLadd(combatMessage, team.getMember(), data.getMonster(), CombatDef.ROUND_FIVE);
        state = true;
        handlerLaddCombat(gid, sid, tid, data, combatMessage, state);
        role.sendTick(request);

        GameGlobalManager.getPtr().addLaddRecord(dungeonId, team, (byte) 10);

        TaskManager.getInstance().scheduleTask(ETaskType.COMMON, new Task() {
            @Override
            public void run() {
                for (Player p : team.getMember()) {
                    if (p.getId() != player.getId()) {
                        IGameRole gr = GameWorld.getPtr().getGameRole(p.getId());
                        if (gr.getGameRole() != null) {
                            gr.getGameRole().getTeamManager().handlerLaddCombat(gid, sid, tid, data, combatMessage, true);
                            //gr.getGameRole().getTeamManager().handlerLaddCombat(gid,sid,tid,data,combatMessage,state);
                        }
                    }
                }
            }

            @Override
            public String name() {
                return "laddTeam";
            }
        });
    }

    public void handlerLaddCombat(byte gid, byte sid, int tid, LADDisasterData data, Message combatMessage, boolean state) {
        Message message = new Message(EMessage.TEAM_LADD_START.CMD());
        message.setByte(gid);
        message.setByte(sid);
        message.setInt(tid);
        message.setByte(state);
        if (state) {
            if (player.getLadDisaster() < data.getId()) {
                player.setLadDisaster(data.getId());
            }
            List<Byte> sweep = player.getDayData().getLaddSweep().get(gid);
            if (sweep == null) {
                sweep = new ArrayList<>();
                player.getDayData().getLaddSweep().put(gid, sweep);
            }
            sweep.add(sid);

            List<DropData> reward = new ArrayList<>();
            if (player.getLadDisaster() < data.getId()) {
                reward.addAll(data.getReward1());
            } else {
                reward.addAll(data.getReward2());

                if (player.getDayData().getLaddAssistNum() > 0) {
                    player.getDayData().subLaddAssistNum();
                    reward.add(data.getRewardAssist());
                }
            }
            message.setByte(reward.size());
            for (DropData d : reward) {
                d.getMessage(message);
            }

            TaskManager.getInstance().scheduleDelayTask(ETaskType.COMMON, new Task() {
                @Override
                public void run() {
                    Mail mail = MailService.createMail("生死劫组队", "生死劫组队", EGoodsChangeType.LADD_TEAM_ADD, reward);
                    role.getMailManager().addMailAndNotify(mail);
                }

                @Override
                public String name() {
                    return "laddTeamMail";
                }
            }, DateUtil.SECOND * 5);
        }
        message.writeBytes(combatMessage.getChannelBuffer());
        role.putMessageQueue(message);
    }

    /**
     * 276 生死劫战斗结束
     *
     * @param request
     */
    public void processLaddTeamEnd(Message request) {
        byte gid = request.readByte();
        byte sid = request.readByte();
        int tid = request.readInt();

        Message message = new Message(request.getCmdId(), request.getChannel());
        message.setByte(gid);
        message.setByte(sid);
        message.setByte(player.getDayData().getLaddAssistNum());
        role.sendMessage(message);
    }

    /**
     * 277 生死劫关卡状态
     *
     * @param request
     */
    public void processLaddStageState(Message request) {
        Message message = new Message(request.getCmdId(), request.getChannel());
        message.setByte(TeamDef.getLaddGID(player.getLadDisaster()));
        message.setByte(TeamDef.getLaddSID(player.getLadDisaster()));
        message.setByte(player.getDayData().getLaddAssistNum());
        message.setByte(player.getDayData().getLaddSweep().size());
        for (Entry<Byte, List<Byte>> entry : player.getDayData().getLaddSweep().entrySet()) {
            message.setByte(entry.getKey());
            message.setByte(entry.getValue().size());
            for (byte value : entry.getValue()) {
                message.setByte(value);
            }
        }
        message.setByte(player.getDayData().getLaddTreasureBox().size());
        for (Entry<Byte, List<Byte>> entry : player.getDayData().getLaddTreasureBox().entrySet()) {
            message.setByte(entry.getKey());
            message.setByte(entry.getValue().size());
            for (byte value : entry.getValue()) {
                message.setByte(value);
            }
        }
        role.sendMessage(message);
    }

    /**
     * 278 生死劫查看记录
     *
     * @param request
     */
    public void processLaddRecord(Message request) {
        byte gid = request.readByte();
        byte sid = request.readByte();

        int dungeonId = TeamDef.getLaddId(gid, sid);

        Message message = new Message(request.getCmdId(), request.getChannel());
        message.setByte(gid);
        message.setByte(sid);

        TeamRecord[] record = GameGlobalManager.getPtr().getLaddRecord(dungeonId);
        if (record == null) {
            message.setByte(0);
        } else {
            message.setByte(record[0].getName().size());
            for (String name : record[0].getName()) {
                message.setString(name);
            }
            message.setByte(record[0].getRound());
            message.setInt(record[0].getTime());
        }
        if (record == null) {
            message.setByte(0);
        } else {
            message.setByte(record[1].getName().size());
            for (String name : record[1].getName()) {
                message.setString(name);
            }
            message.setByte(record[1].getRound());
            message.setInt(record[1].getTime());
        }
        role.sendMessage(message);
    }

    /**
     * 279 生死劫一键扫荡
     *
     * @param request
     */
    public void processLaddSweep(Message request) {
        byte gid = request.readByte();
        byte sid = 0;
        if (gid > 0) {
            sid = request.readByte();
        }

        List<DropData> list = new ArrayList<>();

        if (gid == 0) {
            for (Entry<Integer, LADDisasterData> data : TeamModel.getLADDisasterData().entrySet()) {
                if (player.getLadDisaster() >= data.getKey()) {

                    byte g = (byte) TeamDef.getLaddGID(data.getKey());
                    byte s = (byte) TeamDef.getLaddSID(data.getKey());

                    List<Byte> value = player.getDayData().getLaddSweep().get(g);
                    if (value == null) {
                        value = new ArrayList<>();
                        player.getDayData().getLaddSweep().put(g, value);
                    }

                    if (!value.contains(s)) {
                        value.add(s);

                        list.addAll(data.getValue().getReward2());
                    }
                }
            }
        } else {
            List<Byte> value = player.getDayData().getLaddSweep().get(gid);
            if (value == null) {
                value = new ArrayList<>();
                player.getDayData().getLaddSweep().put(gid, value);
            }
            int dungeonId = TeamDef.getLaddId(gid, sid);

            if (player.getLadDisaster() >= dungeonId && !value.contains(sid)) {
                value.add(sid);

                LADDisasterData data = TeamModel.getLADDisasterData().get(dungeonId);
                list.addAll(data.getReward2());
            }
        }

        Message message = new Message(request.getCmdId(), request.getChannel());
        message.setByte(gid);
        message.setByte(sid);
        message.setByte(list.size());
        for (DropData reward : list) {
            reward.getMessage(message);
        }
        role.sendMessage(message);
    }

    /**
     * 280 生死劫开启宝箱
     *
     * @param request
     */
    public void processLaddTreasureBox(Message request) {
        byte gid = request.readByte();
        byte sid = request.readByte();

        int dungeonId = TeamDef.getLaddId(gid, sid);
        if (player.getLadDisaster() < dungeonId) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }

        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        if (!role.getPackManager().useGoods(new DropData(EGoodsType.DIAMOND, 0, 500), EGoodsChangeType.LADD_TEAM_ADD, enumSet)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }

        LADDisasterData data = TeamModel.getLADDisasterData().get(dungeonId);

        Message message = new Message(request.getCmdId(), request.getChannel());
        message.setByte(gid);
        message.setByte(sid);
        message.setByte(data.getRewardBox().length);
        for (DropData d : data.getRewardBox()) {
            d.getMessage(message);
        }
        role.sendMessage(message);

        List<Byte> box = player.getDayData().getLaddTreasureBox().get(gid);
        if (box == null) {
            box = new ArrayList<>();
            player.getDayData().getLaddTreasureBox().put(gid, box);
        }
        box.add(sid);
    }

    private void sendLaddTeamInfo(Message request, byte gid, byte sid, int tid) {
        int id = TeamDef.getLaddId(gid, sid);

        Team team = GameTeamManager.getPtr().getLaddTeam(id, tid);
        if (team == null) {
            role.sendErrorTipMessage(request, ErrorDef.TEAM_NON_EXIST);
            return;
        }
        Message message = new Message(EMessage.TEAM_LADD_INFO.CMD(), request.getChannel());
        message.setByte(gid);
        message.setByte(sid);
        message.setInt(tid);
        team.getMessage(message);
        role.sendMessage(message);
    }

}
