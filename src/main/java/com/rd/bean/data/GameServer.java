package com.rd.bean.data;

import java.util.Date;

public class GameServer {

    private int id;

    private String name;

    private String innerHost;

    private int port;

    private byte state;

    private Date createTime;

    private int pvp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInnerHost() {
        return innerHost;
    }

    public void setInnerHost(String innerHost) {
        this.innerHost = innerHost;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public byte getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getPvp() {
        return pvp;
    }

    public void setPvp(int pvp) {
        this.pvp = pvp;
    }

}
