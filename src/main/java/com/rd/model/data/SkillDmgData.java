package com.rd.model.data;

public class SkillDmgData {

    public int lv;

    public int skill1;

    public int skill2;

    public int skill3;

    public int skill4;

    public int skill5;

    public int skill5ewai;

    public int getSkillAddDmg(int skillId) {
        switch (skillId - (skillId - 1) / 5 * 5) {
            case 1:
                return skill1;
            case 2:
                return skill2;
            case 3:
                return skill3;
            case 4:
                return skill4;
            case 5:
                return skill5;
            default:
                break;
        }
        return 0;
    }

}
