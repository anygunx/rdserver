package com.rd.bean.dungeon;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.rd.define.DungeonDefine;
import com.rd.util.StringUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Dungeon {

    private int playerId;

    private byte type;

    private short pass = 1;

    private Map<Byte, DungeonDaily> dailyTimes;

    private byte sweep;

    private IDungeonTypeData typeData;

    private Set<Byte> passed = new HashSet<Byte>();

    public byte getSweep() {
        return sweep;
    }

    public void setSweep(byte sweep) {
        this.sweep = sweep;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public short getPass() {
        return pass;
    }

    public void setPass(short pass) {
        this.pass = pass;
    }

    public Map<Byte, DungeonDaily> getDailyTimes() {
        return dailyTimes;
    }

    public void setDailyTimes(Map<Byte, DungeonDaily> dailyTimes) {
        this.dailyTimes = dailyTimes;
    }

    public void setDailyTimesJson(String json) {
        this.dailyTimes = JSON.parseObject(json, new TypeReference<HashMap<Byte, DungeonDaily>>() {
        });
    }

    public String getDailyTimesJson() {
        return JSON.toJSONString(this.dailyTimes);
    }

    public Dungeon() {
        this.reset();
    }

    public DungeonDaily getDungeonDaily(byte id) {
        if (this.dailyTimes.containsKey(id)) {
            return this.dailyTimes.get(id);
        } else {
            DungeonDaily daily = new DungeonDaily();
            //？？
            synchronized (dailyTimes) {
                dailyTimes.put(id, daily);
            }
            return daily;
        }
    }

    public void addPass() {
        ++this.pass;
    }

    public void subSweep() {
        --this.sweep;
    }

    public void reset() {
        this.dailyTimes = new HashMap<>();
        this.sweep = 1;
    }

    public <T extends IDungeonTypeData> T getTypeData() {
        if (typeData == null) {
            typeData = DungeonDefine.EDungeon.builder(type, "");
        }
        return (T) typeData;
    }

    public void setTypeData(IDungeonTypeData typeData) {
        this.typeData = typeData;
    }

    public String getTypeDataJson() {
        return StringUtil.obj2Gson(typeData);
    }

    public Set<Byte> getPassed() {
        return passed;
    }

    public String getPassedJson() {
        return JSON.toJSONString(passed);
    }

    public void setPassedJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.passed = JSON.parseObject(json, new TypeReference<Set<Byte>>() {
            });
        }
    }
}
