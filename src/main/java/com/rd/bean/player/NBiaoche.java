package com.rd.bean.player;

import com.alibaba.fastjson.JSONArray;
import com.rd.define.GameDefine;
import com.rd.define.NHuSongDefine;
import com.rd.model.NHuSongModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NBiaoche {
    public void setCargo(byte cargo) {
        this.cargo = cargo;
    }

    public void setArrive(byte arrive) {
        this.arrive = arrive;
    }

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

    private short jiebiaoCnt;
    //当日劫镖次数
    private short rob;
    //劫镖玩家ID列表
    private List<Integer> robList = new ArrayList<>();

    public List<Integer> getRobList() {
        return robList;
    }

    public void setRobList(List<Integer> robList) {
        this.robList = robList;
    }

    public short getRob() {
        return rob;
    }

    public void setRob(short rob) {
        this.rob = rob;
    }

    //日志
    private Queue<NBiaoCheLog> logs = new ConcurrentLinkedQueue<>();
    //刷新次数
    private short refresh;

    public short getJiebiaoCnt() {
        return jiebiaoCnt;
    }

    public void setJiebiaoCnt(short qiebiaoCnt) {
        this.jiebiaoCnt = qiebiaoCnt;
    }

    public void addJiebiaoCnt() {
        ++jiebiaoCnt;
    }

    public byte getCargo() {
        return cargo;
    }

    public byte getArrive() {
        return arrive;
    }

    //本趟镖车被劫次数
    private byte hurted;

    public byte getHurted() {
        return hurted;
    }

    public void setHurted(byte hurted) {
        this.hurted = hurted;
    }

    public void addHurted() {
        ++this.hurted;
    }


    public synchronized void addHurted(Player player, byte result) {
        if (hurted >= NHuSongDefine.BIAOCHE_JIE_COUNT)
            return;
        long curr = System.currentTimeMillis();
        if (getShengYuTime() < 0)
            return;
        //成功被劫才计数
        if (result == 1)
            this.hurted++;
        //被劫日志
        NBiaoCheLog log = new NBiaoCheLog();
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

    public long getShengYuTime() {
        long shengyu = 0;
        long curr = System.currentTimeMillis();
        if (this.startTime > 0) {
            long time = NHuSongModel.getNHuSongDataById(quality).getTime() * 1000;
            shengyu = this.startTime + time - curr;
        }
        return shengyu;
    }


    public boolean isComplete() {
        if (this.arrive == 0 && getShengYuTime() > 0)
            return false;
        return true;
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

    public void addRobLog(NHuSongPlayer ep, byte result) {
        //抢劫日志
        NBiaoCheLog log = new NBiaoCheLog();
        log.setT(3);
        log.setR(result);
        log.setId(ep.getId());
        log.setM(ep.getName());
        log.setQ(ep.getQuality());
        log.setS(System.currentTimeMillis());
        this.addLog(log);
    }

    public void addLog(NBiaoCheLog log) {
        if (this.logs.size() < GameDefine.ESCORT_LOG_MAX) {
            this.logs.add(log);
        } else {
            this.logs.poll();
            this.logs.add(log);
        }
        //this.readed = 0;
//			GameRole role = GameWorld.getPtr().getOnlineRole(playerId);
//			if (role != null)
//			{
//				role.putMessageQueue(role.getEscortManager().getEscortLogRead());
//			}
    }

    public String getLogJson() {
        return JSONArray.toJSONString(this.logs);
    }

    public void setLogJson(String json) {
        if (json == null || json.length() <= 0)
            return;
        List<NBiaoCheLog> logs = JSONArray.parseArray(json, NBiaoCheLog.class);
        for (NBiaoCheLog log : logs) {
            this.logs.add(log);
        }
    }


    public void addStartLog() {
        //运镖日志
        NBiaoCheLog log = new NBiaoCheLog();
        log.setT(1);
        log.setR(1);
        log.setId(playerId);
        log.setM("");
        log.setQ(this.quality);
        log.setS(startTime);
        this.addLog(log);
    }

    public Queue<NBiaoCheLog> getLogs() {
        return logs;
    }

    /***
     * 押镖完成是否领取奖励
     */
    public boolean isLingQu() {
        if (getCargo() == 1 && getArrive() == 1) {
            return true;
        }
        return false;
    }
}
