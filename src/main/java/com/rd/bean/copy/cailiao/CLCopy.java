package com.rd.bean.copy.cailiao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.rd.util.StringUtil;

import java.util.*;

/**
 * 材料副本保存数据
 *
 * @author MyPC
 */
public class CLCopy {
    private byte datatype;
    private int subType;

    public int getSubType() {
        return subType;
    }

    public void setSubType(int subType) {
        this.subType = subType;
    }

    public void setPassed(Set<Integer> passed) {
        this.passed = passed;
    }

    public byte getDatatype() {
        return datatype;
    }

    public void setDatatype(byte datatype) {
        this.datatype = datatype;
    }


    private int playerId;

    public int getPlayerId() {
        return playerId;
    }

    public CLCopy() {
        dailyTimes = new HashMap<>();
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public short getPass() {
        return pass;
    }

    public void setPass(short pass) {
        this.pass = pass;
    }

    public void addPass() {
        ++this.pass;
    }

    /**
     * 总通关次数
     */
    private int totalCount;


    public void addTotalCount() {
        ++this.totalCount;
    }


    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public CLCopyDaily getCLCopyDaily(int id) {
        if (dailyTimes != null && this.dailyTimes.containsKey(id)) {
            return this.dailyTimes.get(id);
        } else {
            CLCopyDaily daily = new CLCopyDaily();
            //？？
            synchronized (dailyTimes) {
                daily.setId(id);
                dailyTimes.put(id, daily);
            }
            return daily;
        }
    }


    public Map<Integer, CLCopyDaily> getDailyTimes() {
        return dailyTimes;
    }

    public void setDailyTimes(Map<Integer, CLCopyDaily> dailyTimes) {
        this.dailyTimes = dailyTimes;
    }


    public List<CLCopyDaily> getCopyDailyList() {
        List<CLCopyDaily> temp = new ArrayList<>();
        for (CLCopyDaily da : dailyTimes.values()) {
            temp.add(da);
        }
        Collections.sort(temp);
        return temp;
    }


    public byte getSweep() {
        return sweep;
    }

    public void setSweep(byte sweep) {
        this.sweep = sweep;
    }

    private short pass;


    private Map<Integer, CLCopyDaily> dailyTimes;

    private byte sweep;//第二天开始免费扫荡一次

    public void setDailyTimesJson(String json) {
        if (json == null || json.trim().length() < 1) {
            return;
        }
        this.dailyTimes = JSON.parseObject(json, new TypeReference<HashMap<Integer, CLCopyDaily>>() {
        });
    }

    public String getDailyTimesJson() {
        return JSON.toJSONString(this.dailyTimes);
    }

    private Set<Integer> passed = new HashSet<Integer>();

    public Set<Integer> getPassed() {
        return passed;
    }

    public String getPassedJson() {
        return JSON.toJSONString(passed);
    }

    public void setPassedJson(String json) {
        if (!StringUtil.isEmpty(json)) {
            this.passed = JSON.parseObject(json, new TypeReference<Set<Integer>>() {
            });
        }
    }

    public void resetCaiLiao() {
        this.dailyTimes = new HashMap<>();
        //this.sweep=1;
    }


    public void resetGeRen() {
        for (Integer id : getPassed()) {
            CLCopyDaily day = getCLCopyDaily(id);
            if (day == null) {
                day = new CLCopyDaily();
                day.setId(id);
                getDailyTimes().put(id, day);
            } else {
                day.setButCnt((byte) 0);
            }
        }
    }


    public void resetTianMen() {
        setPass((byte) 0);
        setSweep((byte) 1);
    }

    public void resetMiZang() {
        //setPassed(new HashSet<Byte>());
        sweep = 1;
        for (Map.Entry<Integer, CLCopyDaily> da : getDailyTimes().entrySet()) {
            da.getValue().setButCnt((byte) 0);
        }
    }
}
