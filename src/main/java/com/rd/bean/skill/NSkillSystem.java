package com.rd.bean.skill;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class NSkillSystem {
    private static Logger logger = Logger.getLogger(NSkillSystem.class);
    /**
     * 技能列表
     */
    private List<Short> skillList = new ArrayList<>();


    public List<Short> getSkillList() {
        return skillList;
    }

    public void setSkillList(List<Short> skillList) {
        this.skillList = skillList;
    }


    /**
     * 添加技能
     *
     * @param skill
     */
    public boolean addSkill(short id) {
        if (getNskillBySkill(id) != 0) {
            return false;
        }
        skillList.add(id);
        return true;

    }


    /**
     * 改变顺序
     */
    public void changeSort(int postionfrom, int positionto) {
        if (skillList.size() < postionfrom || skillList.size() < positionto) {
            return;
        }
        short skillidfrom = skillList.get(postionfrom);
        short skillidto = skillList.get(positionto);
        skillList.set(postionfrom, skillidto);
        skillList.set(positionto, skillidfrom);
    }

    /**
     * 通过技能id  获取技能
     *
     * @return
     */
    public short getNskillBySkill(int skillId) {
        logger.info("getNskillBySkill===================================");
        for (short nSkill : skillList) {
            if (nSkill == skillId) {
                return nSkill;
            }
        }
        logger.info("getNskillBySkill result===================================" + 0);
        return 0;
    }

    public byte getSkillIdPos(int skillId) {
        for (byte i = 0; i < skillList.size(); i++) {
            if (skillId == skillList.get(i)) {
                return i;
            }
        }
        return 0;
    }


}