package com.rd.define;

public enum NRankType {

    COPY_SJG(0, "水晶宫副本榜"),
    COPY_TM(1, "天门副本榜"),
    COPY_MZ(2, "密藏副本榜"),
    JINGJI(3, "竞技场排名榜");


    private int id;

    private String name;

    NRankType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static NRankType getType(int id) {
        for (NRankType type : NRankType.values()) {
            if (type.id == id)
                return type;
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
