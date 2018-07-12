package com.rd.bean.lianti;

import com.alibaba.fastjson.annotation.JSONField;
import com.rd.model.FunctionModel;
import com.rd.net.message.Message;

import java.util.HashMap;
import java.util.Map;

public class Ambit {

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
     * 激活的光环列表KEY：光环ID VALUE：结束时间，-1永久
     **/
    @JSONField(name = "h")
    private Map<Byte, Integer> halo = new HashMap<>();
    /**
     * 展示的光环
     */
    @JSONField(name = "w")
    private byte haloShow;
    /**
     * 制胜技能
     */
    @JSONField(name = "k")
    private byte skill;

    public void getMessage(Message message) {
        message.setByte(stage);
        message.setByte(star);
        message.setShort(exp);
        message.setByte(skill);
        message.setByte(haloShow);
        message.setByte(halo.size());
        for (Map.Entry<Byte, Integer> entry : halo.entrySet()) {
            message.setByte(entry.getKey());
            message.setInt(entry.getValue());
        }
    }

    public int addExp(int value, int maxExp) {
        exp += value;
        if (exp >= maxExp) {
            ++star;
            exp -= maxExp;
            if (star >= FunctionModel.getAmbitMaxStar()) {
                ++stage;
                star -= FunctionModel.getAmbitMaxStar();
                return 2;
            } else {
                return 1;
            }
        }
        return 0;
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

    public Map<Byte, Integer> getHalo() {
        return halo;
    }

    public void setHalo(Map<Byte, Integer> halo) {
        this.halo = halo;
    }

    public byte getHaloShow() {
        return haloShow;
    }

    public void setHaloShow(byte haloShow) {
        this.haloShow = haloShow;
    }

    public byte getSkill() {
        return skill;
    }

    public void setSkill(byte skill) {
        this.skill = skill;
    }

}
