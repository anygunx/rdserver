package com.rd.model.data;

import com.rd.bean.drop.DropData;

import java.util.List;

/**
 * 天女技能
 *
 * @author wh
 */
public class TianNvSkillData {

    private final int id;
    private final String name;
    private final byte type;//位置
    private final byte tiannvType;//0：天女；1：仙器；2：花辇；3：灵气
    private final byte pos;//位置
    private final int lv;
    private final int need_lv;
    private final int atk;
    private final int[] buff;
    private final String effecta;
    private final String effectb;
    private final List<DropData> costItem;

    public TianNvSkillData(int id, String name, byte type, byte tiannvType, byte pos, int lv, int need_lv, int atk,
                           int[] buff, String effecta, String effectb, List<DropData> costItem) {
        super();
        this.id = id;
        this.name = name;
        this.type = type;
        this.tiannvType = tiannvType;
        this.pos = pos;
        this.lv = lv;
        this.need_lv = need_lv;
        this.atk = atk;
        this.buff = buff;
        this.effecta = effecta;
        this.effectb = effectb;
        this.costItem = costItem;
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

    public byte getTiannvType() {
        return tiannvType;
    }

    public byte getPos() {
        return pos;
    }

    public int getLv() {
        return lv;
    }

    public int getNeed_lv() {
        return need_lv;
    }

    public int getAtk() {
        return atk;
    }

    public int[] getBuff() {
        return buff;
    }

    public String getEffecta() {
        return effecta;
    }

    public String getEffectb() {
        return effectb;
    }

    public List<DropData> getCostItem() {
        return costItem;
    }
}
