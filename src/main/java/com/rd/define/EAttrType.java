package com.rd.define;

import com.rd.util.StringUtil;
import com.rd.util.XmlUtils;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * 属性类型
 * id必须是连续的  id是属性数组的索引
 *
 * @author Created by U-Demon on 2016年11月2日 上午11:32:03
 * @version 1.0.0
 */
public enum EAttrType {
    HP(0, "生命", "hp", 0.4f),
    ATTACK(1, "攻击", "attack", 4),
    PHYDEF(2, "物防", "phyDef", 2),
    MAGICDEF(3, "魔防", "magicDef", 2),
    PHPUNCTURE(4, "物防穿刺", "phpuncture", 2),
    MAGPUNCTURE(5, "魔防穿刺", "magpuncture", 2),
    HIT(6, "命中", "hit", 200),
    DODGE(7, "闪避", "dodge", 200),
    CRIT(8, "暴击", "crit", 100),
    DUCT(9, "抗暴", "duct", 100),
    CRITDAM(10, "暴击伤害", "critdam", 0),
    CRITRES(11, "暴击抵抗", "critres", 100),
    AMP(12, "伤害加深", "amp", 100),
    DR(13, "伤害减免", "dr", 100),
    RESTORE(14, "生命恢复", "restore", 1),
    ATTACKRADIO(15, "攻击加成", "attackradio", 100),
    HPRADIO(16, "生命加成", "hpradio", 10),;

    public static final int ATTR_SIZE;

    public static final Map<Integer, EAttrType> valueMap;

    static {
        valueMap = new HashMap<>();
        for (EAttrType type : EAttrType.values()) {
            valueMap.put(type.id, type);
        }
        ATTR_SIZE = EAttrType.values().length;
    }

    private final int id;
    private final String desc;
    //xml中的名字
    private final String name;
    //战斗力系数
    private final float factor;

    EAttrType(int id, String desc, String name, float factor) {
        this.id = id;
        this.desc = desc;
        this.name = name;
        this.factor = factor;
    }

    public static EAttrType getType(int id) {
        return valueMap.get(id);
    }

    public static int[] getAttr(Element element) {
        int[] attr = new int[ATTR_SIZE];
        for (EAttrType type : EAttrType.values()) {
            String attrValue = XmlUtils.getAttribute(element, type.getName());
            if (StringUtil.isEmpty(attrValue))
                continue;
            attr[type.getId()] = Integer.parseInt(attrValue);
        }
        return attr;
    }

    public static int[] getAttr(Element element, String column) {
        int[] attr = new int[ATTR_SIZE];
        String value = XmlUtils.getAttribute(element, column);
        if (value == null || value.length() == 0)
            return attr;
        String[] value2 = null;
        if (value.contains("#"))
            value2 = StringUtil.getStringList(value, "#");
        else
            value2 = StringUtil.getStringList(value, ";");
        for (String value3 : value2) {
            String[] values = value3.split(",");
            if (values.length == 2) {
                int id = Integer.valueOf(values[0]);
                int v = Integer.valueOf(values[1]);
                attr[id] = attr[id] + v;
            } else if (values.length == 3) {
                int id = Integer.valueOf(values[0]);
                int v = Integer.valueOf(values[2]);
                attr[id] = attr[id] + v;
            }
        }
        return attr;
    }

    public static int[] getIntAttr(Element element) {
        int[] attr = new int[ATTR_SIZE];
        for (EAttrType type : EAttrType.values()) {
            String attrValue = XmlUtils.getAttribute(element, type.getName());
            if (StringUtil.isEmpty(attrValue))
                continue;
            attr[type.getId()] = Integer.parseInt(attrValue);
        }
        return attr;
    }

    public static short[] getShortAttr(Element element) {
        short[] attr = new short[ATTR_SIZE];
        for (EAttrType type : EAttrType.values()) {
            String attrValue = XmlUtils.getAttribute(element, type.getName());
            if (StringUtil.isEmpty(attrValue))
                continue;
            attr[type.getId()] = Short.parseShort(attrValue);
        }
        return attr;
    }

    public int getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }

    public String getName() {
        return name;
    }

    public float getFactor() {
        return factor;
    }

}
