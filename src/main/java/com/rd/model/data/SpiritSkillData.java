package com.rd.model.data;

import com.rd.bean.drop.DropData;

import java.util.List;

/**
 * 通灵技能
 *
 * @author wh
 */
public class SpiritSkillData {

    private final int id;
    private final String name;
    private final byte type;
    private final int lv;
    private final int need_lv;
    private final List<DropData> cost_item;
    private final int att;
    private final int def;
    private final int hp;

    public SpiritSkillData(int id, String name, byte type, int lv, int need_lv, List<DropData> cost_item, int att,
                           int def, int hp) {
        super();
        this.id = id;
        this.name = name;
        this.type = type;
        this.lv = lv;
        this.need_lv = need_lv;
        this.cost_item = cost_item;
        this.att = att;
        this.def = def;
        this.hp = hp;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public byte getType() {
        return type;
    }

    public int getLv() {
        return lv;
    }

    public int getNeed_lv() {
        return need_lv;
    }

    public List<DropData> getCost_item() {
        return cost_item;
    }

    public int getAtt() {
        return att;
    }

    public int getDef() {
        return def;
    }

    public int getHp() {
        return hp;
    }
}
