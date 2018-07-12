package com.rd.bean.player;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.rd.define.GameDefine;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.game.manager.EscortManager;
import com.rd.model.ConstantModel;
import com.rd.net.message.Message;
import com.rd.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 押镖数据
 *
 * @author Created by U-Demon on 2016年11月19日 下午5:18:54
 * @version 1.0.0
 */
public class Escort {

    //玩家ID
    private int playerId;

    //镖车品质
    private byte quality;

    //当日运镖次数--和startTime一起食用
    private short count;

    //镖车开始时间
    private long startTime;

    //是否有货物0--没有货物	1--有货物
    private byte cargo;

    //是否到达
    private byte arrive;

    //本趟镖车被劫次数
    private byte hurted;

    //日志
    private Queue<EscortLog> logs = new ConcurrentLinkedQueue<>();
    //是否已读
    private byte readed;

    //当日劫镖次数--和lastRob一起食用
    private short rob;

    //上次劫镖时间
//	private long lastRob;

    //劫镖玩家ID列表
    private List<Integer> robList = new ArrayList<>();

    //刷新次数
    private short refresh;

    //上次刷新时间
//	private long lastRefresh;

    //首次出橙
    private byte flag;

    //用于匹配
    private int match;

    public void getEscortMsg(Message msg) {
        msg.setByte(this.quality);
        msg.setShort(this.count);
        msg.setInt(getEscortLeftSec());
        msg.setByte(this.cargo);
        int hurtedNum = this.hurted;
        if (hurtedNum > ConstantModel.ESCORT_HURTED)
            hurtedNum = ConstantModel.ESCORT_HURTED;
        msg.setByte(hurtedNum);
        msg.setShort(this.rob);
        msg.setShort(this.refresh);
    }

    public int getEscortLeftSec() {
        int sec = 0;
        if (this.startTime > 0) {
            long curr = System.currentTimeMillis();
            double time = this.startTime + EscortManager.getEscortKeepTime(this.quality) - curr;
            sec = (int) Math.ceil(time / 1000);
        }
        return sec;
    }

    public boolean isComplete() {
        long curr = System.currentTimeMillis();
        if (this.arrive == 0 && curr - this.startTime < EscortManager.getEscortKeepTime(this.quality))
            return false;
        return true;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public byte getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = (byte) quality;
    }

    public void addQuality() {
        this.quality++;
    }

    public short getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = (short) count;
    }

    public void addCount() {
        this.count++;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public byte getCargo() {
        return cargo;
    }

    public void setCargo(int cargo) {
        this.cargo = (byte) cargo;
    }

    public byte getHurted() {
        return hurted;
    }

    public void setHurted(int hurted) {
        this.hurted = (byte) hurted;
    }

    public synchronized void addHurted(Player player, byte result) {
        if (hurted >= ConstantModel.ESCORT_HURTED)
            return;
        long curr = System.currentTimeMillis();
        if (this.startTime + EscortManager.getEscortKeepTime(quality) < curr)
            return;
        //成功被劫才计数
        if (result == 1)
            this.hurted++;
        //被劫日志
        EscortLog log = new EscortLog();
        log.setT(2);
        log.setR(result);
        log.setId(player.getId());
        log.setM(player.getName());
        log.setQ(this.quality);
        log.setS(curr);
        log.setH(player.getHead());
        log.setF(player.getFighting());
        this.addLog(log);
    }

    public void addRobLog(EscortPlayer ep, byte result) {
        //抢劫日志
        EscortLog log = new EscortLog();
        log.setT(3);
        log.setR(result);
        log.setId(ep.getId());
        log.setM(ep.getName());
        log.setQ(ep.getQuality());
        log.setS(System.currentTimeMillis());
        this.addLog(log);
    }

    public void addStartLog() {
        //运镖日志
        EscortLog log = new EscortLog();
        log.setT(1);
        log.setR(1);
        log.setId(playerId);
        log.setM("");
        log.setQ(this.quality);
        log.setS(startTime);
        this.addLog(log);
    }

    public void addCompleteLog(long endTime) {
        //运镖日志
        EscortLog log = new EscortLog();
        log.setT(0);
        log.setR(1);
        log.setId(playerId);
        log.setM("");
        log.setQ(this.quality);
        log.setS(endTime);
        this.addLog(log);
    }

    public short getRob() {
        return rob;
    }

    public void setRob(int rob) {
        this.rob = (short) rob;
    }

    public void addRob() {
        this.rob++;
    }

    public short getRefresh() {
        return refresh;
    }

    public void setRefresh(int refresh) {
        this.refresh = (short) refresh;
    }

    public void addRefresh() {
        this.refresh++;
    }

    public byte getFlag() {
        return flag;
    }

    public void setFlag(byte flag) {
        this.flag = flag;
    }

    public int getMatch() {
        return match;
    }

    public void setMatch(int match) {
        this.match = match;
    }

    public List<Integer> getRobList() {
        return robList;
    }

    public void setRobList(List<Integer> robList) {
        this.robList = robList;
    }

    public String getRobListJson() {
        return JSONArray.toJSONString(this.robList);
    }

    public void setRobListJson(String json) {
        if (StringUtil.isEmpty(json))
            return;
        this.robList = JSON.parseArray(json, Integer.class);
    }

    public Queue<EscortLog> getLogs() {
        return logs;
    }

    public void addLog(EscortLog log) {
        if (this.logs.size() < GameDefine.ESCORT_LOG_MAX) {
            this.logs.add(log);
        } else {
            this.logs.poll();
            this.logs.add(log);
        }
        this.readed = 0;
        GameRole role = GameWorld.getPtr().getOnlineRole(playerId);
        if (role != null) {
            role.putMessageQueue(role.getEscortManager().getEscortLogRead());
        }
    }

    public String getLogJson() {
        return JSONArray.toJSONString(this.logs);
    }

    public void setLogJson(String json) {
        if (json == null || json.length() <= 0)
            return;
        List<EscortLog> logs = JSONArray.parseArray(json, EscortLog.class);
        for (EscortLog log : logs) {
            this.logs.add(log);
        }
    }

    public byte getArrive() {
        return arrive;
    }

    public void setArrive(int arrive) {
        this.arrive = (byte) arrive;
    }

    public byte getReaded() {
        return readed;
    }

    public void setReaded(byte readed) {
        this.readed = readed;
    }

}
