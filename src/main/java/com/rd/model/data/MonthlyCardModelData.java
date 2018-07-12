package com.rd.model.data;

import com.rd.bean.drop.DropData;
import com.rd.define.EVipType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonthlyCardModelData {

    //月卡ID
    private byte id;

    //月卡奖励
    private List<DropData> reward;

    //持续天数
    private int keepDay;

    //是否可以叠加
    private byte addUp;

    //邮件标题
    private String title;

    //邮件奖励
    private String content;

    //特权
    private Map<EVipType, Integer> tequan = new HashMap<>();

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public List<DropData> getReward() {
        return reward;
    }

    public void setReward(List<DropData> reward) {
        this.reward = reward;
    }

    public int getKeepDay() {
        return keepDay;
    }

    public void setKeepDay(int keepDay) {
        this.keepDay = keepDay;
    }

    public byte getAddUp() {
        return addUp;
    }

    public void setAddUp(byte addUp) {
        this.addUp = addUp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<EVipType, Integer> getTequan() {
        return tequan;
    }

    public void setTequan(Map<EVipType, Integer> tequan) {
        this.tequan = tequan;
    }

}
