package com.rd.model.data.copy.cailiao;

import com.rd.bean.drop.DropData;
import com.rd.model.data.copy.CopyData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NCaiLiaoCopyData extends CopyData {
    private int type;
    private short neelLv;

    private byte vipLimite;
    private int price;
    private int boss;
    private int[] monstaerId;
    private short mapId = 10000;

    public short getMapId() {
        return mapId;
    }

    private Map<Byte, Byte> vipCount = new HashMap<>();
    private List<DropData> dropDataList = new ArrayList<>();

    public NCaiLiaoCopyData(int id, short needLv, int boosId, int[] arry,
                            List<DropData> reward, short price, Map<Byte, Byte> vipCount) {
        super(boosId, arry, reward);
        this.type = id;
        this.neelLv = needLv;
        this.boss = boosId;
        this.monstaerId = arry;
        this.dropDataList = reward;
        this.price = price;
        this.vipCount = vipCount;

    }

    public List<DropData> getDropDataList() {
        return dropDataList;
    }

    public int getPrice() {
        return price;
    }

    public byte getVipLimite() {
        return vipLimite;
    }

    public int getType() {
        return type;
    }

    public short getNeelLv() {
        return neelLv;
    }

    public byte getCountByteVip(byte vip) {
        if (vipCount.get(vip) == null) {
            return 0;
        }
        return vipCount.get(vip);
    }

    public int getBoss() {
        return boss;
    }

    public int[] getMonstaerId() {
        return monstaerId;
    }


}
