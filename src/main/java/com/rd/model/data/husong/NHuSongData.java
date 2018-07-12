package com.rd.model.data.husong;

import com.rd.bean.drop.DropData;

import java.util.List;

public class NHuSongData {
    private int id;
    private int time;
    private int gailv;
    private List<DropData> rewards;

    private List<DropData> jiebiaoReards;


    public NHuSongData(int id, int time, int gailv, List<DropData> rewards, List<DropData> jiebiaoReards) {
        this.id = id;
        this.time = time;
        this.gailv = gailv;
        this.rewards = rewards;
        this.jiebiaoReards = jiebiaoReards;

    }


    public int getId() {
        return id;
    }


    public int getTime() {
        return time;
    }


    public int getGailv() {
        return gailv;
    }


    public List<DropData> getRewards() {
        return rewards;
    }


    public List<DropData> getJiebiaoReards() {
        return jiebiaoReards;
    }
}
