package com.rd.bean.rank;

import com.rd.bean.player.Player;
import com.rd.bean.player.SimplePlayer;
import com.rd.define.ERankType;
import com.rd.net.message.Message;
import com.rd.util.StringUtil;

/**
 * 角色排行数据
 * Created by XingYun on 2016/5/19.
 */
public class PlayerRank extends SimplePlayer implements Comparable<PlayerRank> {

    protected int rank = 0;
    protected long value = 0;
    protected int value2 = 0;
    //排序值
    protected long match = 0;

    public PlayerRank() {
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public void addValue(int add) {
        this.value += add;
    }

    public int getValue2() {
        return value2;
    }

    public void setValue2(int value2) {
        this.value2 = value2;
    }

    public long getMatch() {
        return match;
    }

    public void setMatch(long match) {
        this.match = match;
    }

    @Override
    public int compareTo(PlayerRank o) {
        if (value > o.getValue())
            return -1;
        else if (value < o.getValue())
            return 1;
        if (value2 > o.getValue2())
            return -1;
        else if (value2 < o.getValue2())
            return 1;
        if (match > o.getMatch())
            return 1;
        else if (match < o.getMatch())
            return -1;
        if (rank > o.getRank())
            return 1;
        else if (rank < o.getRank())
            return -1;
        return 0;
    }

    public void getMessage(Message message) {
        super.getSimpleMessage(message);
        message.setInt(rank);
        message.setLong(value);
        message.setInt(value2);
    }

    @Override
    public String toString() {
        return super.toString() + StringUtil.COMMA + rank;
    }

    public static PlayerRank createWithString(ERankType rankType, String str) {
        String[] params = str.split(StringUtil.COMMA, -1);
        PlayerRank rank = new PlayerRank();
        rank.initWithString(params);
        return rank;
    }

    public void init(Player player) {
        super.init(player);
    }

    public void init(SimplePlayer player) {
        setId(player.getId());
        setName(player.getName());
        setHead(player.getHead());
        setRein(player.getRein());
        setLevel(player.getLevel());
        setVip(player.getVip());
        setFighting(player.getFighting());
    }

    public void initWithString(String[] params) {
        super.initWithString(params);
        setRank(Integer.valueOf(params[6]));
    }

    public void initReward() {
    }

}
