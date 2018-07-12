package com.rd.model.data;

import com.rd.bean.drop.DropData;
import com.rd.define.EVipType;

import java.util.List;
import java.util.Map;

/**
 * VIP
 *
 * @author Created by U-Demon on 2016年12月21日 下午1:17:38
 * @version 1.0.0
 */
public class VipModelData {

    private int level;

    private int cost;

    private Map<EVipType, Integer> weals;

    private List<DropData> rewards;

    private int isNew;

    private String title;

    private String content;

    private int pvpDamage;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public Map<EVipType, Integer> getWeals() {
        return weals;
    }

    public void setWeals(Map<EVipType, Integer> weals) {
        this.weals = weals;
    }

    public List<DropData> getRewards() {
        return rewards;
    }

    public void setRewards(List<DropData> rewards) {
        this.rewards = rewards;
    }

    public int getIsNew() {
        return isNew;
    }

    public void setIsNew(int isNew) {
        this.isNew = isNew;
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

    public int getPvpDamage() {
        return pvpDamage;
    }

    public void setPvpDamage(int pvpDamage) {
        this.pvpDamage = pvpDamage;
    }
}
