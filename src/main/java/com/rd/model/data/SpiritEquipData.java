package com.rd.model.data;

/**
 * 通灵技能
 *
 * @author wh
 */
public class SpiritEquipData {

    private final int id;
    private final String name;
    private final int pos;
    private final int lv;
    private final byte quality;
    private final int att;
    private final int def;
    private final int hp;

    public SpiritEquipData(int id, String name, int pos, int lv, byte quality, int att, int def, int hp) {
        super();
        this.id = id;
        this.name = name;
        this.pos = pos;
        this.lv = lv;
        this.quality = quality;
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

    public int getPos() {
        return pos;
    }

    public int getLv() {
        return lv;
    }

    public byte getQuality() {
        return quality;
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
