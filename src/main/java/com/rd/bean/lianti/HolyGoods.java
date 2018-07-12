package com.rd.bean.lianti;

import com.alibaba.fastjson.annotation.JSONField;
import com.rd.define.EAttrType;
import com.rd.model.FunctionModel;
import com.rd.net.message.Message;

public class HolyGoods {

    /**
     * 境界阶段
     */
    @JSONField(name = "t")
    private byte stage;
    /**
     * 境界星级
     */
    @JSONField(name = "s")
    private byte star;
    /**
     * 境界经验
     */
    @JSONField(name = "e")
    private short exp;
    /**
     * 圣纹装备
     */
    @JSONField(name = "q")
    private byte[] linesEquip = {0, 0, 0, 0};

    public void getMessage(Message message) {
        message.setByte(stage);
        message.setByte(star);
        message.setShort(exp);
        for (byte level : linesEquip) {
            message.setByte(level);
        }
    }

    public int addExp(int value, int maxExp) {
        exp += value;
        if (exp >= maxExp) {
            ++star;
            exp -= maxExp;
            if (star >= FunctionModel.getHolyGoodsMaxStar()) {
                ++stage;
                star -= FunctionModel.getHolyGoodsMaxStar();
                return 2;
            } else {
                return 1;
            }
        }
        return 0;
    }

    public int[] getAttr() {
        int[] attr = new int[EAttrType.ATTR_SIZE];
//        // 大师
//        if (master > 0){
//           ShengWenMasterData masterModelData = FunctionModel.getShengWenMaster(master);
//            if (masterModelData != null) {
//                for (int i = 0; i < EAttrType.ATTR_SIZE; ++i) {
//                    attr[i] *= 1+ masterModelData.getAddPercent();
//                }
//            }
//        }
//        //圣纹装备
//		for (Short id: equipments.values()) {
//            ShengWenData shengwenData = FunctionModel.getShengWenData(id);
//            for (int i = 0; i < EAttrType.ATTR_SIZE; ++i) {
//                attr[i] += shengwenData.getAttr()[i];
//            }
//		}
        return attr;
    }

    public byte getStage() {
        return stage;
    }

    public void setStage(byte stage) {
        this.stage = stage;
    }

    public byte getStar() {
        return star;
    }

    public void setStar(byte star) {
        this.star = star;
    }

    public short getExp() {
        return exp;
    }

    public void setExp(short exp) {
        this.exp = exp;
    }

    public byte[] getLinesEquip() {
        return linesEquip;
    }

    public void setLinesEquip(byte[] linesEquip) {
        this.linesEquip = linesEquip;
    }
}
