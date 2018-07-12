package com.rd.activity.data;

import com.rd.util.XmlUtils;
import org.w3c.dom.Element;

/**
 * @author
 */
public class FiveElementLogicData extends BaseActivityLogicData {
    private int id;

    private int vip;

    private int day;

    private int attack;

    private int hp;

    private int phyDef;

    private int magicDef;

    @Override
    public String getKey() {
        return String.valueOf(id);
    }

    @Override
    public void loadData(Element root) {
        id = Integer.valueOf(XmlUtils.getAttribute(root, "id"));
        vip = Integer.valueOf(XmlUtils.getAttribute(root, "vip"));
        day = Integer.valueOf(XmlUtils.getAttribute(root, "day"));
        attack = Integer.valueOf(XmlUtils.getAttribute(root, "attack"));
        hp = Integer.valueOf(XmlUtils.getAttribute(root, "hp"));
        phyDef = Integer.valueOf(XmlUtils.getAttribute(root, "phyDef"));
        magicDef = Integer.valueOf(XmlUtils.getAttribute(root, "magicDef"));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getPhyDef() {
        return phyDef;
    }

    public void setPhyDef(int phyDef) {
        this.phyDef = phyDef;
    }

    public int getMagicDef() {
        return magicDef;
    }

    public void setMagicDef(int magicDef) {
        this.magicDef = magicDef;
    }

}
