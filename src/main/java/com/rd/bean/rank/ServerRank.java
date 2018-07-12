package com.rd.bean.rank;

public class ServerRank implements Comparable<ServerRank> {

    private int serverId;

    private int value;

    @Override
    public int compareTo(ServerRank o) {
        if (value > o.getValue())
            return -1;
        else if (value < o.getValue())
            return 1;
        return 0;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void addValue(int add) {
        this.value += add;
    }

}
