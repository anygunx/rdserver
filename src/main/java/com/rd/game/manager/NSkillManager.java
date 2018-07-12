package com.rd.game.manager;

import com.rd.bean.drop.DropData;
import com.rd.bean.player.Player;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.define.ErrorDefine;
import com.rd.enumeration.EMessage;
import com.rd.game.GameRole;
import com.rd.model.NskillModel;
import com.rd.model.data.skill.NSkillData;
import com.rd.net.message.Message;
import org.apache.log4j.Logger;

import java.util.EnumSet;
import java.util.List;

public class NSkillManager {
    private static Logger logger = Logger.getLogger(NSkillManager.class);
    private GameRole gameRole;
    private Player player;

    public NSkillManager(GameRole role) {
        this.gameRole = role;
        this.player = role.getPlayer();
    }

    /**
     * 通过玩家的等级激活技能
     */
    public void jihuoSkill(short level) {
        NSkillData data = NskillModel.getJiHuoSkillByGrade(level);

        if (data == null) {
            return;
        }
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        boolean issu = player.getNSkillSystem().addSkill(data.getId());
        if (issu) {
            enumSet.add(EPlayerSaveType.SKILL);
            gameRole.savePlayer(enumSet);
        }
    }

    /**
     * 单个技能手动升级
     *
     * @param request
     */
    public void processSkillUpGrade(Message request) {
        int pos = request.readInt();
        if (pos < 0) {
            processAutoUpSkill(request);
            return;
        }
        List<Short> list = player.getNSkillSystem().getSkillList();
        if (list == null || list.isEmpty() || pos >= list.size()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_NOT_ENOUGHT);
            return;
        }
        Short skill = player.getNSkillSystem().getSkillList().get(pos);
        if (skill == null || skill == 0) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_NOT_ENOUGHT);
            return;
        }

        NSkillData data = NskillModel.getUpGradeDataById(skill);
        if (data == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_NOT_ENOUGHT);
            return;
        }
        if (data.getLevel() >= player.getLevel()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_NOT_ENOUGHT);
            return;
        }
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        short nextlv = (short) (data.getLevel() + 1);
        NSkillData next = NskillModel.getUpGradeDataByGrade(pos, nextlv);
        if (next == null) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_NOT_ENOUGHT);
            return;
        }
        DropData cost = new DropData(data.getTypeCost(), data.getGoodId(), data.getNum());
        if (!gameRole.getPackManager().useGoods(cost, EGoodsChangeType.SKILL_UP_CONSUME, enumSet, false)) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_NOT_ENOUGHT);
            return;
        }

        //消耗游戏币
        player.getNSkillSystem().getSkillList().set(data.getPos(), next.getId());
        Message message = new Message(EMessage.SKILL_UPGRADE.CMD(), request.getChannel());
        message.setByte(next.getPos());
        message.setInt(next.getId());
        message.setShort(next.getLevel());
        gameRole.sendMessage(message);
        enumSet.add(EPlayerSaveType.SKILL);
        gameRole.savePlayer(enumSet);
    }

    /**
     * 一键自动升级
     *
     * @param request
     */
    public void processAutoUpSkill(Message request) {
        List<Short> skillList = player.getNSkillSystem().getSkillList();
        if (skillList == null || skillList.isEmpty()) {
            gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_NOT_ENOUGHT);
            return;
        }
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
//		for (Short nSkill : skillList) {
//			 int pos=player.getNSkillSystem().getSkillIdPos(nSkill);
//			 if(pos<0) {
//				 continue;
//			 }
//			upGradeSkll(nSkill, enumSet,pos);
//		}
        upSkillgrade();
        getSkillListMessage(request);
        enumSet.add(EPlayerSaveType.SKILL);
        gameRole.savePlayer(enumSet);
    }


    private void upSkillgrade() {
        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);
        Bean bean = minSkillData();
        NSkillData data = bean.skillData;
        NSkillData nextData = NskillModel.getUpGradeDataByGrade(data.getPos(), (short) (data.getLevel() + 1));
        while (nextData != null) {
            if (player.getLevel() < nextData.getLevel()) {
                break;
            }
            DropData cost = new DropData(nextData.getTypeCost(), nextData.getGoodId(), nextData.getNum());
            if (!gameRole.getPackManager().useGoods(cost, EGoodsChangeType.SKILL_UP_CONSUME, enumSet, false)) {
                break;
            }

            player.getNSkillSystem().getSkillList().set(bean.pos, nextData.getId());
            bean = minSkillData();
            data = bean.skillData;
            nextData = NskillModel.getUpGradeDataByGrade(data.getPos(), (short) (data.getLevel() + 1));
        }
    }


    private Bean minSkillData() {
        List<Short> skillList = player.getNSkillSystem().getSkillList();
        NSkillData minData = null;
        byte pos = -1;

        for (byte i = 0; i < skillList.size(); i++) {
            short skillid = skillList.get(i);
            NSkillData skillData = NskillModel.getUpGradeDataById(skillid);
            if (minData == null) {
                minData = skillData;
                pos = i;
            } else {
                if (minData.getLevel() > skillData.getLevel()) {
                    minData = skillData;
                    pos = i;
                } else if (minData.getLevel() == skillData.getLevel()) {
                    if (minData.getId() > skillData.getId()) {
                        minData = skillData;
                        pos = i;
                    }
                }

            }

        }
//		NSkillData skillid=null; 
//		for (Short short1 : skillList) {
//			NSkillData skillData= NskillModel.getUpGradeDataById(short1);
//			if(skillid==null) {
//				skillid=skillData;
//				++pos;
//			}else {
//				if(skillid.getLevel()>skillData.getLevel()) {
//					skillid=skillData;
//					++pos;
//				}else if(skillid.getLevel()==skillData.getLevel()) {
//					if(skillid.getId()>skillData.getId()) {
//						skillid=skillData;
//						++pos;
//					}
//				}
//			}
//		}


        return new Bean(minData, pos);

    }


    class Bean {
        public NSkillData skillData;
        public byte pos;

        Bean(NSkillData skillData, byte pos) {
            this.skillData = skillData;
            this.pos = pos;
        }


    }


    /**
     * 递归去自动升级 需要注意 停止递归的条件是 当前技能等级 不能大于角色等级 还有就是 升级到最高级了 以及没有了游戏币 不会再一直递归的
     *
     * @param grade
     * @param cost
     */
    private void upGradeSkll(Short nSkill, EnumSet<EPlayerSaveType> enumSet, int pos) {
        NSkillData data = NskillModel.getUpGradeDataById(nSkill);
        if (data == null) {
            return;
        }
        if (data.getLevel() >= player.getLevel()) {
            return;
        }
        NSkillData skillData = NskillModel.getUpGradeDataByGrade(pos, (short) (data.getLevel() + 1));
        if (skillData == null) {
            return;
        }

        DropData cost = new DropData(skillData.getTypeCost(), skillData.getGoodId(), skillData.getNum());
        if (!gameRole.getPackManager().useGoods(cost, EGoodsChangeType.SKILL_UP_CONSUME, enumSet, false)) {
            //gameRole.sendErrorTipMessage(request, ErrorDefine.ERROR_DIAMOND_NOT_ENOUGHT);
            return;
        }

        player.getNSkillSystem().getSkillList().set(pos, skillData.getId());
        upGradeSkll(skillData.getId(), enumSet, pos);

    }


    /**
     * 打开技能面板
     */
    public void processSkillPanel(Message request) {
        getSkillListMessage(request);
    }

    private void getSkillListMessage(Message request) {
        List<Short> skillList = player.getNSkillSystem().getSkillList();
        Message message = new Message(EMessage.SKILL_SKILLPANEL.CMD(), request.getChannel());
        if (skillList == null || skillList.isEmpty()) {
            message.setByte(0);
            gameRole.sendMessage(message);
            return;
        }

        message.setByte(skillList.size());
        for (Short skillId : skillList) {
            NSkillData data = NskillModel.getUpGradeDataById(skillId);
            if (data == null) {
                continue;
            }
            message.setByte(data.getPos());
            message.setInt(skillId);
            message.setShort(data.getLevel());

        }
        gameRole.sendMessage(message);

    }


    public void changeSkillPos(Message request) {
        byte posFrom = request.readByte();
        byte posTo = request.readByte();
        Message message = new Message(EMessage.SKILL_SKILLPANEL.CMD(), request.getChannel());
        player.getNSkillSystem().changeSort(posFrom, posTo);


    }


}



