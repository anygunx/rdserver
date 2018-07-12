package com.rd.bean.lianti;

import com.google.common.reflect.TypeToken;
import com.rd.define.EAttrType;
import com.rd.define.SectionDefine;
import com.rd.model.SectionModel;
import com.rd.model.data.MountData;
import com.rd.model.data.WingGodModelData;
import com.rd.model.data.WingMasterModelData;
import com.rd.model.data.WingSkillModelData;
import com.rd.net.message.Message;
import com.rd.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 仙羽
 */
public class Wing {
    /**
     * 坐骑(神羽)阶段
     */
    private short mountStage;
    /**
     * 坐骑(神羽)星级
     */
    private byte mountStar;
    /**
     * 坐骑(神羽)经验
     */
    private int mountExp;
    /**
     * 仙羽装备 pos-id
     **/
    private Map<Byte, Short> equipments = new HashMap<>();
    /**
     * 神羽大师
     **/
    private short master = 0;
    /**
     * 技能
     **/
    private short skill = 0;


    public Map<Byte, Short> getEquipments() {
        return equipments;
    }

    public void setEquipments(Map<Byte, Short> equipments) {
        this.equipments = equipments;
    }

    public void addEquipment(byte pos, short id) {
        equipments.put(pos, id);
    }

    public Short getEquipment(byte pos) {
        return equipments.get(pos);
    }

    public short getMountStage() {
        return mountStage;
    }

    public void setMountStage(short mountStage) {
        this.mountStage = mountStage;
    }

    public byte getMountStar() {
        return mountStar;
    }

    public void setMountStar(byte mountStar) {
        this.mountStar = mountStar;
    }

    public int getMountExp() {
        return mountExp;
    }

    public void setMountExp(int mountExp) {
        this.mountExp = mountExp;
    }

    public void getMessage(Message message) {
        message.setShort(this.mountStar);
        message.setShort(this.mountStage);
        message.setInt(this.mountExp);
        message.setByte(equipments.size());
        for (Map.Entry<Byte, Short> entry : equipments.entrySet()) {
            message.setByte(entry.getKey());
            message.setShort(entry.getValue());
        }
        message.setShort(master);
        message.setShort(skill);
    }


    public void addMountJieDuan() {
        this.mountStar = 0;
        ++this.mountStage;
    }

    public boolean addMountExp(short addExp) {
        boolean isUp = false;
        MountData data = SectionModel.getMountData(this.mountStage, this.mountStar);
        this.mountExp += addExp;
        if (this.mountExp >= data.getExp()) {
            this.mountExp -= data.getExp();
            ++this.mountStar;
        }
        return isUp;
    }

    public byte[] addMountExpPill(short addExp) {
        byte[] state = new byte[2];
        MountData data = SectionModel.getMountData(this.mountStage, this.mountStar);
        this.mountExp += addExp;
        while (this.mountExp >= data.getExp()) {
            this.mountExp -= data.getExp();
            ++this.mountStar;
            if (this.mountStar == SectionDefine.STAR_FULL) {
                this.addMountJieDuan();
                state[1] = 1;
            }
            data = SectionModel.getMountData(this.mountStage, this.mountStar);
            state[0] = 1;
        }
        return state;
    }


    public String getEquipmentsJson() {
        return StringUtil.obj2Gson(equipments);
    }

    public void setEquipmentsStr(String json) {
        if (!StringUtil.isEmpty(json)) {
            equipments = StringUtil.gson2Map(json, new TypeToken<Map<Byte, Short>>() {
            });
        }
    }

    public void setEquipment(byte pos, short id) {
        equipments.put(pos, id);
    }

    public short getMaster() {
        return master;
    }

    public void setMaster(short master) {
        this.master = master;
    }

    public short getSkill() {
        return skill;
    }

    public void setSkill(short skill) {
        this.skill = skill;
    }

    public int[] getAttr() {
        int[] attr = new int[EAttrType.ATTR_SIZE];
        //坐骑属性
        MountData mountData = SectionModel.getMountData(mountStage, mountStar);
        if (mountData == null) {
            return attr;
        }
        for (int i = 0; i < EAttrType.ATTR_SIZE; ++i) {
            attr[i] += mountData.getAttr()[i];
        }
        // 大师
        if (master > 0) {
            WingMasterModelData masterModelData = SectionModel.getWingMaster(master);
            if (masterModelData != null) {
                for (int i = 0; i < EAttrType.ATTR_SIZE; ++i) {
                    attr[i] *= 1 + masterModelData.getAddPercent();
                }
            }
        }
        // 神羽装备
        for (Short id : equipments.values()) {
            WingGodModelData wingGodModelData = SectionModel.getWingGod(id);
            for (int i = 0; i < EAttrType.ATTR_SIZE; ++i) {
                attr[i] += wingGodModelData.getAttr()[i];
            }
        }
        // 技能
        if (skill > 0) {
            WingSkillModelData skillModelData = SectionModel.getWingSkill(skill);
            if (skillModelData != null) {
                attr[EAttrType.AMP.getId()] += skillModelData.getAmp();
            }
        }
        return attr;
    }
}
