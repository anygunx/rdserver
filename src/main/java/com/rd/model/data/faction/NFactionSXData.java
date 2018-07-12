package com.rd.model.data.faction;

import com.rd.bean.drop.DropData;

import java.util.List;

/****
 *
 *
 * 上香数据
 */
public class NFactionSXData {
    private int id;
    private DropData cost;
    private List<DropData> reward_capital;
    private DropData reward_gong;
    private DropData reward_fire;

    public NFactionSXData(int id, DropData cost, List<DropData> reward_capital, DropData reward_gong, DropData reward_fire) {
        this.id = id;
        this.cost = cost;
        this.reward_capital = reward_capital;
        this.reward_gong = reward_gong;
        this.reward_fire = reward_fire;
    }

    public int getId() {
        return id;
    }

    public DropData getCost() {
        return cost;
    }

    public List<DropData> getReward_capital() {
        return reward_capital;
    }

    public DropData getReward_gong() {
        return reward_gong;
    }

    public DropData getReward_fire() {
        return reward_fire;
    }

}
