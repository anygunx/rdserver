package com.rd.define;

/**
 * 排行榜类型
 *
 * @author Created by U-Demon on 2016年12月14日 上午11:34:22
 * @version 1.0.0
 */
public enum ERankType {

    LEVEL(0, "等级榜"),
    FIGHTING(1, "战力榜"),
    WING(2, "羽翼榜"),
    WEIWANG(3, "官职榜"),;

    private int id;

    private String name;

    ERankType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static ERankType getType(int id) {
        for (ERankType type : ERankType.values()) {
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
