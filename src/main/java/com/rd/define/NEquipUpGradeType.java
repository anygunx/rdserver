package com.rd.define;

import com.rd.model.data.equip.NEquipUpGradeItemData;

import java.util.HashMap;
import java.util.Map;

/**
 * 装备槽位各种升级类型  比如强化 精炼
 *
 * @author MyPC
 */
public enum NEquipUpGradeType {
    EQUIP_QH(0, "qianghua"),
    EQUIQ__JL(1, "jinglian"),
    EQUIQ__DL(2, "duanlian"),
    EQUIQ__BS(3, "baoshi"),

    /***以下暂且只是服务器用到的类型*/
    EQUIQ__QHDS(50, "qianghuadashi"),
    EQUIQ__JLDS(51, "jingliandashi"),
    EQUIQ__DLDS(52, "duanliandashi"),
    EQUIQ__BSDS(53, "baoshidashi");


    public Map<String, ? extends NEquipUpGradeItemData> getDatas() {
        return datas;
    }

    public void setDatas(Map<String, ? extends NEquipUpGradeItemData> datas) {
        this.datas = datas;
    }

    private int type;


    public Map<Short, ? extends NEquipUpGradeItemData> getdSData() {
        return dSData;
    }

    public void setdSData(Map<Short, ? extends NEquipUpGradeItemData> dSData) {
        this.dSData = dSData;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private String json;
    /**
     * 升级 数据（强化 精炼）
     */
    private Map<String, ? extends NEquipUpGradeItemData> datas = new HashMap<>();//data
    /**
     * 不同种大师数据
     */
    private Map<Short, ? extends NEquipUpGradeItemData> dSData = new HashMap<>();

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    private NEquipUpGradeType(int type, String json) {
        this.type = type;
        this.json = json;
    }

    private static Map<Byte, NEquipUpGradeType> valueMap;

    static {
        valueMap = new HashMap<>();
        for (NEquipUpGradeType etit : NEquipUpGradeType.values()) {
            valueMap.put((byte) etit.type, etit);
        }
    }

    public static NEquipUpGradeType geEquipUpGradeType(byte type) {
        return valueMap.get(type);
    }

    public NEquipUpGradeItemData getNEquipUpGradeItemData(int lv, int pos) {
        return datas.get(lv + "_" + pos);

    }

    public NEquipUpGradeItemData getNEquipUpGradeItemData(int lv) {
        return dSData.get((short) lv);

    }

}
