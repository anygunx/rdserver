package com.rd.define;

public enum EActivityRankType {

    TYPE_WING_FIGHTING(1, "翅膀"),
    TYPE_JEWEL_FIGHTING(2, "宝石"),
    TYPE_DRAGONPATTERN_FIGHTING(3, "龙纹"),
    TYPE_DRAGONSCALE_FIGHTING(4, "龙鳞"),
    TYPE_JINGMAI_FIGHTING(5, "经脉"),
    TYPE_DRAGONBALL_FIGHTING(6, "龙珠"),
    TYPE_FIGHT_FIGHTING(7, "战力"),;

    private int id;

    private String name;

    EActivityRankType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static EActivityRankType getType(int id) {
        for (EActivityRankType type : EActivityRankType.values()) {
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
