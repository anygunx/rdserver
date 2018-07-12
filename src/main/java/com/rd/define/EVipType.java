package com.rd.define;

/**
 * VIP特权
 *
 * @author Created by U-Demon on 2016年12月21日 下午1:20:16
 * @version 1.0.0
 */
public enum EVipType {

    DUNGEON_STUFF(1, "可扫荡材料副本的次数"),
    BAG_CAPACITY(2, "免费增加背包上限"),
    CHARACTER_2(3, "提前开启第二角色"),
    CHARACTER_3(4, "提前开启第三角色"),
    GOLDTREE(5, "摇钱树次数"),
    GANGTURNTABLE(6, "帮会转盘次数"),
    OUTPUT_OFFLINE(7, "离线产出"),;

    private int id;

    private String desc;

    EVipType(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public static EVipType getType(int id) {
        for (EVipType type : EVipType.values()) {
            if (type.getId() == id)
                return type;
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }

}
