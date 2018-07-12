package com.rd.common;

import com.rd.bean.drop.DropData;
import com.rd.bean.goods.Artifact;
import com.rd.bean.goods.Equip;
import com.rd.define.EGoodsQuality;
import com.rd.model.data.RedModelData;

public class EquipCommon {

    private EquipCommon() {

    }

    public static Equip generateEquip(DropData data) {
        //1、基础属性（读表）
        //2、被品质系数放大
        //3、浮动值=基础值*0%~15%浮动

        byte f = 15;
        if (data.getQ() < EGoodsQuality.ORANGE.getValue()) {
            f = (byte) (Math.random() * 16f);
        }

        Equip equip = new Equip();
        equip.setG(data.getG());
        equip.setQ(data.getQ());
        equip.setF(f);
        return equip;
    }

    /**
     * 创建红装
     *
     * @param data
     * @return
     */
    public static Equip createRedEquip(RedModelData data) {
        Equip equip = new Equip();
        equip.setG(data.getId());
        equip.setQ(EGoodsQuality.RED.getValue());
        equip.setF((byte) 0);
        return equip;
    }

    public static Artifact generateArtifact(DropData data) {
        Artifact artifact = new Artifact();
        artifact.setG(data.getG());
        //artifact.setQ(data.getQ());
        //artifact.setS(randomArtifactStar());
        return artifact;
    }
}
