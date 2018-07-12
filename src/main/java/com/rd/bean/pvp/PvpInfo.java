package com.rd.bean.pvp;

import com.alibaba.fastjson.JSON;
import com.rd.bean.player.Player;
import com.rd.bean.player.SimplePlayer;
import com.rd.define.PvpDefine;
import com.lg.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PvpInfo {

    private Player player;

    private int prestige;

    private long lastUpdateTime = -1;

    private List<SimplePlayer> challengerList = new ArrayList<>();

    private List<PvpRecord> recordList = new ArrayList<>();

    private byte streakWin;

    public PvpInfo(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getPrestige() {
        return prestige;
    }

    public void setPrestige(int prestige) {
        this.prestige = prestige;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public List<SimplePlayer> getChallengerList() {
        return challengerList;
    }

    public void setChallengerList(List<SimplePlayer> challengerList) {
        this.challengerList = challengerList;
    }

    public String toChallengerListJson() {
        return JSON.toJSONString(challengerList);
    }

    public void fromChallengerListJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.challengerList = JSON.parseArray(json, SimplePlayer.class);
        }
    }

    public List<PvpRecord> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<PvpRecord> recordList) {
        this.recordList = recordList;
    }

    public String toRecordListJson() {
        return JSON.toJSONString(recordList);
    }

    public void fromRecordListJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.recordList = JSON.parseArray(json, PvpRecord.class);
        }
    }

    public boolean isFullChallengers() {
        return challengerList.size() >= PvpDefine.CHALLENGER_COUNT;
    }

    public byte getStreakWin() {
        return streakWin;
    }

    public void setStreakWin(byte streakWin) {
        this.streakWin = streakWin;
    }

    public void addPrestige(int addPrestige) {
        this.prestige += addPrestige;
    }

    public void addStreakWin() {
        ++this.streakWin;
        if (this.streakWin > PvpDefine.STREAK_WIN_MAX) {
            this.streakWin = PvpDefine.STREAK_WIN_MAX;
        }
    }

    public void addPvpRecord(PvpRecord record) {
        this.recordList.add(record);
        while (this.recordList.size() > PvpDefine.RECORD_COUNT) {
            this.recordList.remove(0);
        }
    }
}
