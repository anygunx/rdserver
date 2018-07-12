package com.rd.bean.player;

/**
 * Created by XingYun on 2017/5/2.
 */
public interface ISimplePlayer {
    int getId();

    void setId(int id);

    String getName();

    void setName(String name);

    byte getHead();

    void setHead(byte head);

    short getLevel();

    void setLevel(short level);

    int getVip();

    void setVip(int vip);

    long getFighting();

    void setFighting(long fighting);

    short getRein();

    void setRein(int rein);
}
