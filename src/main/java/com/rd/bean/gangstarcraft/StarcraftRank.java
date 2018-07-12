package com.rd.bean.gangstarcraft;

/**
 * @param <T>
 * @author ---
 * @version 1.0
 * @date 2017年12月29日下午5:00:27
 */
public class StarcraftRank extends GangStarcraftRank implements Comparable<StarcraftRank> {

    private long enterTime;

    public long getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(long enterTime) {
        this.enterTime = enterTime;
    }

    @Override
    public int compareTo(StarcraftRank rank) {
        int result = Integer.compare(rank.getScore(), this.getScore());
        if (result == 0) {
            result = Long.compare(this.enterTime, rank.enterTime);
        }
        return result;
    }
}
