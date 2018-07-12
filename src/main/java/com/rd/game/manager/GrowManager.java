package com.rd.game.manager;

import com.rd.bean.drop.DropData;
import com.rd.bean.grow.Grow;
import com.rd.bean.grow.GrowSeed;
import com.rd.bean.grow.GrowSuit;
import com.rd.bean.player.Player;
import com.rd.common.GameCommon;
import com.rd.common.goods.EGoodsType;
import com.rd.dao.EPlayerSaveType;
import com.rd.define.EGoodsChangeType;
import com.rd.define.ErrorDefine;
import com.rd.define.GrowDef;
import com.rd.enumeration.EGrow;
import com.rd.enumeration.EMessage;
import com.rd.game.GameRole;
import com.rd.model.data.*;
import com.rd.net.message.Message;
import org.apache.commons.lang3.RandomUtils;

import java.util.*;
import java.util.Map.Entry;

/**
 * @author ---
 * @version 1.0
 * @date 2018年5月3日下午8:42:10
 */
public class GrowManager {

    private GameRole role;
    private Player player;

    public GrowManager(GameRole role) {
        this.role = role;
        this.player = role.getPlayer();
    }

    /**
     * 230成长项列表
     */
    public void processGrowList(Message request) {
        byte type = request.readByte();

        if (type < 0 || type > 4) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        Grow grow = player.getGrowList().get(EGrow.type(type).I());

        Message message = new Message(EMessage.GROW_LIST.CMD(), request.getChannel());
        message.setByte(type);
        grow.getMessage(message);
        role.sendMessage(message);
    }

    /**
     * 231成长项激活
     */
    public void processGrowActive(Message request) {
        byte type = request.readByte();
        short id = request.readShort();

        EGrow egrow = EGrow.type(type);

        GrowSeedData data = egrow.getGrowDataMap().get(id);
        if (data == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        Grow grow = player.getGrowList().get(egrow.I());
        if (grow.getMap().containsKey(id)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR__ALREADY_ACTIVATED);
            return;
        }

        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        if (!role.getPackManager().useGoods(data.getCost(), EGoodsChangeType.GROW_ACTIVE_CONSUME, saves)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }

        GrowSeed seed = new GrowSeed((short) id, data);
        grow.getMap().put(id, seed);

        Message message = new Message(EMessage.GROW_ACTIVE.CMD(), request.getChannel());
        message.setByte(type);
        message.setShort(id);
        if (type == EGrow.PET.ordinal()) {
            message.setByte(seed.getSkillPassive().length);
            for (int i = 0; i < seed.getSkillPassive().length; ++i) {
                message.setByte(seed.getSkillPassive()[i][1]);
            }
        }
        role.sendMessage(message);

        saves.add(EPlayerSaveType.GROW);
        role.savePlayer(saves);
    }

    /**
     * 232 成长项上阵
     */
    public void processGrowGo(Message request) {
        byte type = request.readByte();
        short id = request.readShort();
        byte pos = request.readByte();

        EGrow egrow = EGrow.type(type);

        if (egrow != EGrow.PET && egrow != EGrow.MATE) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        Grow grow = player.getGrowList().get(egrow.I());

        if (!grow.getMap().containsKey(id)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_NOT_ACTIVE);
            return;
        }

        for (int i = 0; i < grow.getGo().length; ++i) {
            if (grow.getGo()[i] == id) {
                grow.getGo()[i] = 0;
            }
        }
        if (pos < grow.getGo().length) {
            grow.getGo()[pos] = id;
        }

        Message message = new Message(EMessage.GROW_GO.CMD(), request.getChannel());
        message.setByte(type);
        for (short i : grow.getGo()) {
            message.setShort(i);
        }
        role.sendMessage(message);

        role.savePlayer(EPlayerSaveType.GROW);
    }

    /**
     * 233 成长项改名
     */
    public void processGrowRename(Message request) {
        byte type = request.readByte();
        short id = request.readShort();
        String name = request.readString();

        if (name.length() > 6) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_STRING_LENGTH_LIMIT);
            return;
        }

        EGrow egrow = EGrow.type(type);

        Grow grow = player.getGrowList().get(egrow.I());
        GrowSeed seed = grow.getMap().get(id);

        if (seed == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_NOT_ACTIVE);
            return;
        }

        seed.setName(name);

        Message message = new Message(EMessage.GROW_RENAME.CMD(), request.getChannel());
        message.setByte(type);
        message.setShort(id);
        message.setString(name);
        role.sendMessage(message);

        role.savePlayer(EPlayerSaveType.GROW);
    }

    /**
     * 234成长项升级
     */
    public void processGrowLevelUp(Message request) {
        byte type = request.readByte();
        short id = request.readShort();

        EGrow egrow = EGrow.type(type);

        GrowSeedData data = egrow.getGrowDataMap().get(id);
        if (data == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        Grow grow = player.getGrowList().get(egrow.I());
        if (!grow.getMap().containsKey(id)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        GrowSeed growSeed = grow.getMap().get(id);
        if (growSeed.getLevel() == GrowDef.MAX_LEVEL_UP) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_MAX_LEVEL);
            return;
        }
        GrowSeedLevelUpData upData = egrow.getLevelUpDataMap().get(data.getQuality() + "_" + data.getLevelUp()).get(growSeed.getLevel());
        if (upData.getCostGold().getN() > player.getGold()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOLD_LESS);
            return;
        }

        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        List<DropData> costList = new ArrayList<>();
        costList.add(upData.getCostGold());
        costList.add(upData.getCostLimit());
        if (!role.getPackManager().useGoods(costList, EGoodsChangeType.GROW_LEVELUP_CONSUME, saves)) {
            costList.clear();
            costList.add(upData.getCostGold());
            costList.add(upData.getCost());
            if (!role.getPackManager().useGoods(costList, EGoodsChangeType.GROW_LEVELUP_CONSUME, saves)) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
                return;
            } else {
                growSeed.addExp(upData.getExp());
            }
        } else {
            growSeed.addExpLimit(upData.getExp());
        }
        if (growSeed.getExpLimit() + growSeed.getExp() >= upData.getExpMax()) {
            growSeed.setExpLimit((short) 0);
            growSeed.setExp((short) 0);
            growSeed.addLevel();
        }

        Message message = new Message(EMessage.GROW_LEVELUP.CMD(), request.getChannel());
        message.setByte(type);
        message.setShort(id);
        message.setShort(growSeed.getLevel());
        message.setShort(growSeed.getExp());
        message.setShort(growSeed.getExpLimit());
        role.sendMessage(message);

        saves.add(EPlayerSaveType.GROW);
        role.savePlayer(saves);
    }

    /**
     * 235 成长项飞升
     */
    public void processFlyUp(Message request) {
        byte type = request.readByte();
        short id = request.readShort();

        EGrow egrow = EGrow.type(type);

        GrowSeedData data = egrow.getGrowDataMap().get(id);
        if (data == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        if (data.getQuality() < GrowDef.GOLDEN) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        Grow grow = player.getGrowList().get(egrow.I());
        if (!grow.getMap().containsKey(id)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        GrowSeed growSeed = grow.getMap().get(id);
        if (growSeed.getFlyUp() == GrowDef.MAX_FLYUP) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_MAX_LEVEL);
            return;
        }

        GrowSeedLevelUpData upData = egrow.getFlyUpDataMap().get(growSeed.getFlyUp());
        if (upData.getCostGold().getN() > player.getGold()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOLD_LESS);
            return;
        }

        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        List<DropData> costList = new ArrayList<>();
        //costList.add(cost);
        //costList.add(upData.getCostLimit());
        //if(!role.getPackManager().useGoods(costList, EGoodsChangeType.PET_LEVELUP_CONSUME,saves)) {
        //	costList.clear();
        costList.add(upData.getCostGold());
        costList.add(upData.getCost());
        if (!role.getPackManager().useGoods(costList, EGoodsChangeType.GROW_LEVELUP_CONSUME, saves)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        } else {
            growSeed.addFlyUpExp(upData.getExp());
        }
        //}else{
        //	growSeed.addExpLimitLevel(upData.getExp());
        //}
        if (growSeed.getFlyUpExpLevel() >= upData.getExpMax()) {
            growSeed.setFlyUpExpLevel((short) 0);
            growSeed.addFlyUp();
        }

        Message message = new Message(EMessage.GROW_FLYUP.CMD(), request.getChannel());
        message.setByte(type);
        message.setShort(id);
        message.setShort(growSeed.getFlyUp());
        message.setShort(growSeed.getFlyUpExpLevel());
        role.sendMessage(message);

        saves.add(EPlayerSaveType.GROW);
        role.savePlayer(saves);
    }

    /**
     * 236 成长项资质
     */
    public void processAptitude(Message request) {
        byte type = request.readByte();
        short id = request.readShort();

        EGrow egrow = EGrow.type(type);

        GrowSeedData data = egrow.getGrowDataMap().get(id);
        if (data == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        Grow grow = player.getGrowList().get(egrow.I());
        if (!grow.getMap().containsKey(id)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        GrowSeed growSeed = grow.getMap().get(id);
        if (growSeed.getAptitude() == GrowDef.MAX_APTITUDE) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_MAX_LEVEL);
            return;
        }

        GrowSeedLevelUpData upData = egrow.getAptitudeDataMap().get(data.getQuality() + "_" + data.getAptitude()).get(growSeed.getAptitude());
        //if(upData.getCostGold()>player.getGold()){
        //	role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOLD_LESS);
        //	return;
        //}

        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        //DropData cost = new DropData(EGoodsType.GOLD.getId(), 0, upData.getCostGold());
        //List<DropData> costList = new ArrayList<>();
        //costList.add(cost);
        //costList.add(upData.getCostLimit());
        //if(!role.getPackManager().useGoods(costList, EGoodsChangeType.PET_LEVELUP_CONSUME,saves)) {
        ////	costList.clear();
        //	costList.add(cost);
        //	costList.add(upData.getCost());
        if (!role.getPackManager().useGoods(upData.getCost(), EGoodsChangeType.GROW_LEVELUP_CONSUME, saves)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }//else{
        //	growSeed.addAptitudeExp(upData.getExp());
        //}
        //}else{
        //	growSeed.addExpLimitLevel(upData.getExp());
        //}
        //if(growSeed.getAptitudeExp()>=upData.getExpMax()){
        //	growSeed.setAptitudeExp((short)0);
        growSeed.addAptitude();
        //}

        Message message = new Message(EMessage.GROW_APTITUDE.CMD(), request.getChannel());
        message.setByte(type);
        message.setShort(id);
        message.setShort(growSeed.getAptitude());
        role.sendMessage(message);

        saves.add(EPlayerSaveType.GROW);
        role.savePlayer(saves);
    }

    /**
     * 237 成长项洗炼技能锁定
     */
    public void processWashLock(Message request) {
        byte type = request.readByte();
        short id = request.readShort();
        byte index = request.readByte();

        EGrow egrow = EGrow.type(type);

        Grow grow = player.getGrowList().get(egrow.I());
        if (!grow.getMap().containsKey(id)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        GrowSeed growSeed = grow.getMap().get(id);

        if (index >= growSeed.getSkillPassive().length) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }

        Message message = new Message(EMessage.GROW_WASHINGLOCK.CMD(), request.getChannel());
        message.setByte(type);
        message.setShort(id);
        message.setByte(index);
        role.sendMessage(message);

        if (growSeed.getSkillPassive()[index][0] != 1) {
            growSeed.getSkillPassive()[index][0] = 1;
            role.savePlayer(EPlayerSaveType.GROW);
        }
    }

    /**
     * 238 成长项洗炼技能解锁
     */
    public void processWashUnlock(Message request) {
        byte type = request.readByte();
        short id = request.readShort();
        byte index = request.readByte();

        EGrow egrow = EGrow.type(type);

        Grow grow = player.getGrowList().get(egrow.I());
        if (!grow.getMap().containsKey(id)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        GrowSeed growSeed = grow.getMap().get(id);

        if (index >= growSeed.getSkillPassive().length) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }

        Message message = new Message(EMessage.GROW_WASHINGUNLOCK.CMD(), request.getChannel());
        message.setByte(type);
        message.setShort(id);
        message.setByte(index);
        role.sendMessage(message);

        if (growSeed.getSkillPassive()[index][0] != 0) {
            growSeed.getSkillPassive()[index][0] = 0;
            role.savePlayer(EPlayerSaveType.GROW);
        }
    }

    /**
     * 239 成长项洗炼技能
     */
    public void processWashing(Message request) {
        byte type = request.readByte();
        short id = request.readShort();
        byte select = request.readByte();

        EGrow egrow = EGrow.type(type);

        Grow grow = player.getGrowList().get(egrow.I());
        if (!grow.getMap().containsKey(id)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        GrowSeed growSeed = grow.getMap().get(id);

        if (growSeed == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }

        int count = 0;
        for (int i = 0; i < growSeed.getSkillPassive().length; ++i) {
            if (growSeed.getSkillPassive()[i][0] != 0) {
                ++count;
            }
        }
        if (count == growSeed.getSkillPassive().length) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_UNLOCK);
            return;
        }
        int cost = GrowDef.WASH_LOCK[count];

        EnumSet<EPlayerSaveType> enumSet = EnumSet.noneOf(EPlayerSaveType.class);

        List<DropData> costList = new ArrayList<>();
        if (select == 1) {
            costList.add(new DropData(EGoodsType.ITEM.getId(), GrowDef.PET_WASH_SUPER_STONE, 1));
        } else {
            costList.add(new DropData(EGoodsType.ITEM.getId(), GrowDef.PET_WASH_STONE, 1));
        }
        if (cost > 0) {
            costList.add(new DropData(EGoodsType.DIAMOND.getId(), 0, cost));
        }

        if (!role.getPackManager().useGoods(costList, EGoodsChangeType.ARENA_ADD, enumSet)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }
        int star = growSeed.getWashStar() / 10;
        WashingData washData = egrow.getWashingMap().get((byte) star);
        int[] washNum = new int[washData.getRate().length];
        Set<Short> washSet = new HashSet<>();
        for (int i = 0; i < growSeed.getSkillPassive().length; ++i) {
            if (growSeed.getSkillPassive()[i][0] == 0) {
                while (true) {
                    int rate = GameCommon.getRandomIndex(washData.getRate());
                    int level = rate + 1;
                    List<SkillPassiveData> list = egrow.getSkillPassiveLevelMap().get((byte) level);
                    SkillPassiveData spd = list.get(RandomUtils.nextInt(0, list.size()));

                    if (washNum[rate] < washData.getNum()[rate] && !washSet.contains(spd.getId())) {
                        growSeed.getWashSkill()[i] = spd.getId();
                        washSet.add(spd.getId());
                        ++washNum[rate];
                        break;
                    }
                }
            } else {
                growSeed.getWashSkill()[i] = growSeed.getSkillPassive()[i][1];
            }
        }

        if (select == 1) {
            growSeed.setWashStar((byte) (growSeed.getWashStar() + 10));
        } else {
            growSeed.setWashStar((byte) (growSeed.getWashStar() + 1));
        }
        if (growSeed.getWashStar() >= 70) {
            growSeed.setWashStar((byte) 70);
        }

        Message message = new Message(EMessage.GROW_WASHING.CMD(), request.getChannel());
        message.setByte(type);
        message.setShort(id);
        for (short i : growSeed.getWashSkill()) {
            message.setShort(i);
        }
        message.setByte(growSeed.getWashStar());
        role.sendMessage(message);

        enumSet.add(EPlayerSaveType.GROW);
        role.savePlayer(enumSet);
    }

    /**
     * 240 成长项洗炼技能更换
     */
    public void processWashChange(Message request) {
        byte type = request.readByte();
        short id = request.readShort();

        EGrow egrow = EGrow.type(type);

        Grow grow = player.getGrowList().get(egrow.I());
        if (!grow.getMap().containsKey(id)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        GrowSeed growSeed = grow.getMap().get(id);

        if (growSeed == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }

        for (int i = 0; i < growSeed.getWashSkill().length; ++i) {
            short d = growSeed.getWashSkill()[i];
            if (d != 0 && growSeed.getSkillPassive()[i][0] == 0) {
                growSeed.getSkillPassive()[i][1] = d;
            }
            growSeed.getWashSkill()[i] = 0;
        }
        growSeed.setWashStar((byte) 10);

        Message message = new Message(EMessage.GROW_WASHCHANGE.CMD(), request.getChannel());
        message.setByte(type);
        message.setShort(id);
        for (short[] i : growSeed.getSkillPassive()) {
            message.setShort(i[1]);
        }
        role.sendMessage(message);

        role.savePlayer(EnumSet.of(EPlayerSaveType.GROW));
    }

    /**
     * 241 成长项通灵升阶
     */
    public void processGrowPsychicLevelUp(Message request) {
        byte type = request.readByte();
        byte up = request.readByte();//type=0代表普通升阶  type=1代表直升1阶

        EGrow egrow = EGrow.type(type);

        Grow grow = player.getGrowList().get(egrow.I());

        GrowSuit suit = grow.getSuit()[0];
        if (suit.getLevel() == GrowDef.MAX_SOUL_LEVEL) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_MAX_LEVEL);
            return;
        }

        short level = suit.getLevel();
        GrowSeedLevelUpData upData = egrow.getPsychicLevelUpDataMap().get(level);
        if (upData.getCostGold().getN() > player.getGold()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOLD_LESS);
            return;
        }

        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);

        if (up == 1) {
            if (!role.getPackManager().useGoods(new DropData(EGoodsType.ITEM, GrowDef.DAN_UP[egrow.getType1().ordinal()], 1), EGoodsChangeType.GROW_LEVELUP_CONSUME, saves)) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
                return;
            }
            if (suit.getLevel() >= GrowDef.DAN_UP_LEVEL) {
                suit.addExp(GrowDef.DAN_UP_EXP);
            }
        } else {
            List<DropData> costList = new ArrayList<>();
            costList.add(upData.getCostGold());
            costList.add(upData.getCostLimit());
            if (!role.getPackManager().useGoods(costList, EGoodsChangeType.GROW_LEVELUP_CONSUME, saves)) {
                costList.clear();
                costList.add(upData.getCostGold());
                costList.add(upData.getCost());
                if (!role.getPackManager().useGoods(costList, EGoodsChangeType.GROW_LEVELUP_CONSUME, saves)) {
                    role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
                    return;
                } else {
                    suit.addExp(upData.getExp());
                }
            } else {
                suit.addExpLimit(upData.getExp());
            }
        }

        if ((up == 1 && suit.getLevel() < GrowDef.DAN_UP_LEVEL) || suit.getExpLimit() + suit.getExp() >= upData.getExpMax()) {
            suit.setExpLimit((short) 0);
            suit.setExp((short) 0);
            suit.setLevel((byte) ++level);

            if (level == 3) {
                suit.getSkill()[1] = 1;
            } else if (level == 5) {
                suit.getSkill()[2] = 1;
            } else if (level == 7) {
                suit.getSkill()[3] = 1;
            }
        }

        Message message = new Message(EMessage.GROW_PSYCHICLEVELUP.CMD(), request.getChannel());
        message.setByte(type);
        message.setByte(suit.getLevel());
        message.setShort(suit.getExp());
        message.setShort(suit.getExpLimit());
        role.sendMessage(message);

        saves.add(EPlayerSaveType.GROW);
        role.savePlayer(saves);
    }

    /**
     * 242 成长项通灵技能
     */
    public void processGrowPsychicSkill(Message request) {
        byte type = request.readByte();
        byte pos = request.readByte();

        EGrow egrow = EGrow.type(type);

        Grow grow = player.getGrowList().get(egrow.I());

        GrowSuit suit = grow.getSuit()[0];
        byte level = suit.getSkill()[pos];
        if (level == GrowDef.MAX_SOUL_SKILL) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_MAX_LEVEL);
            return;
        }

        ++level;
        GrowSkillData upData = egrow.getPsychicSkillDataMap().get(level + "_" + (pos + 1));
        if (upData.getNeedLevel() > suit.getLevel()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        if (!role.getPackManager().useGoods(upData.getCost(), EGoodsChangeType.GROW_LEVELUP_CONSUME, saves)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }

        suit.getSkill()[pos] = level;

        Message message = new Message(EMessage.GROW_PSYCHICSKILL.CMD(), request.getChannel());
        message.setByte(type);
        message.setByte(pos);
        message.setByte(level);
        role.sendMessage(message);

        saves.add(EPlayerSaveType.GROW);
        role.savePlayer(saves);
    }

    /**
     * 243 成长项通灵丹
     */
    public void processGrowPsychicPill(Message request) {
        byte type = request.readByte();
        byte num = request.readByte();

        EGrow egrow = EGrow.type(type);

        Grow grow = player.getGrowList().get(egrow.I());

        GrowSuit suit = grow.getSuit()[0];

        byte level, oriLevel;
        level = oriLevel = suit.getPill();

        short error = ErrorDefine.ERROR_NONE;
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        while (num > 0) {
            ++level;
            if (level == GrowDef.MAX_SOUL_PILL) {
                error = ErrorDefine.ERROR_MAX_LEVEL;
                break;
            }
            GrowCostData data = egrow.getPsychicPillDataMap().get(level);
            if (data == null) {
                error = ErrorDefine.ERROR_PARAMETER;
                break;
            }
            if (!role.getPackManager().useGoods(data.getCost(), EGoodsChangeType.SPIRIT_PRO_UP_CONSUME, saves)) {
                error = ErrorDefine.ERROR_GOODS_LESS;
                break;
            }
            suit.setPill(level);
            --num;
        }
        if (oriLevel == suit.getPill()) {
            role.sendErrorTipMessage(request, error);
            return;
        }

        Message message = new Message(EMessage.GROW_PSYCHICPILL.CMD(), request.getChannel());
        message.setByte(type);
        message.setByte(level);
        role.sendMessage(message);
    }

    /**
     * 244 成长项通灵装备
     */
    public void processGrowPsychicEquip(Message request) {
        byte type = request.readByte();

        EGrow egrow = EGrow.type(type);

        Grow grow = player.getGrowList().get(egrow.I());

        GrowSuit suit = grow.getSuit()[0];

        Map<Short, Integer> equipMap = player.getGrowEquipList().get(egrow.getType1().ordinal());

        short[] equipBetter = new short[4];
        System.arraycopy(suit.getEquip(), 0, equipBetter, 0, 4);

        GrowEquipData data;
        for (Entry<Short, Integer> equip : equipMap.entrySet()) {
            data = egrow.getPsychicEquipDataMap().get(equip.getKey());
            if (data.getId() > equipBetter[data.getPos() - 1]) {
                equipBetter[data.getPos() - 1] = equip.getKey();
            }
        }

        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);

        for (int i = 0; i < 4; ++i) {
            if (equipBetter[i] > suit.getEquip()[i]) {
                role.getPackManager().useGoods(new DropData((byte) (40 + egrow.getType1().ordinal()), equipBetter[i], 1), EGoodsChangeType.GROW_LEVELUP_CONSUME, saves);
                if (suit.getEquip()[i] > 0)
                    role.getPackManager().addGoods(new DropData((byte) (40 + egrow.getType1().ordinal()), suit.getEquip()[i], 1), EGoodsChangeType.GROW_LEVELUP_CONSUME, saves);
                suit.getEquip()[i] = equipBetter[i];
            }
        }

        Message message = new Message(EMessage.GROW_PSYCHICEQUIP.CMD(), request.getChannel());
        message.setByte(type);
        for (short equip : suit.getEquip()) {
            message.setShort(equip);
        }
        role.sendMessage(message);

        saves.add(EPlayerSaveType.GROW);
        role.savePlayer(saves);
    }

    /**
     * 245 成长项兽魂升级
     */
    public void processGrowSoulLevelUp(Message request) {
        byte type = request.readByte();
        byte up = request.readByte();//type=0代表普通升阶  type=1代表直升1阶

        EGrow egrow = EGrow.type(type);

        Grow grow = player.getGrowList().get(egrow.I());

        GrowSuit suit = grow.getSuit()[1];

        if (suit.getLevel() == GrowDef.MAX_SOUL_LEVEL) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_MAX_LEVEL);
            return;
        }

        short level = suit.getLevel();
        GrowSeedLevelUpData upData = egrow.getSoulLevelUpDataMap().get(level);
        if (upData.getCostGold().getN() > player.getGold()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOLD_LESS);
            return;
        }

        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        if (up == 1) {
            if (!role.getPackManager().useGoods(new DropData(EGoodsType.ITEM, GrowDef.DAN_UP[egrow.getType2().ordinal()], 1), EGoodsChangeType.GROW_LEVELUP_CONSUME, saves)) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
                return;
            }
            if (suit.getLevel() >= GrowDef.DAN_UP_LEVEL) {
                suit.addExp(GrowDef.DAN_UP_EXP);
            }
        } else {
            List<DropData> costList = new ArrayList<>();
            costList.add(upData.getCostGold());
            costList.add(upData.getCostLimit());
            if (!role.getPackManager().useGoods(costList, EGoodsChangeType.GROW_LEVELUP_CONSUME, saves)) {
                costList.clear();
                costList.add(upData.getCostGold());
                costList.add(upData.getCost());
                if (!role.getPackManager().useGoods(costList, EGoodsChangeType.GROW_LEVELUP_CONSUME, saves)) {
                    role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
                    return;
                } else {
                    suit.addExp(upData.getExp());
                }
            } else {
                suit.addExpLimit(upData.getExp());
            }
        }

        if ((up == 1 && suit.getLevel() < GrowDef.DAN_UP_LEVEL) || suit.getExpLimit() + suit.getExp() >= upData.getExpMax()) {
            suit.setExpLimit((short) 0);
            suit.setExp((short) 0);
            suit.setLevel((byte) ++level);

            if (level == 3) {
                suit.getSkill()[1] = 1;
            } else if (level == 5) {
                suit.getSkill()[2] = 1;
            } else if (level == 7) {
                suit.getSkill()[3] = 1;
            }
        }

        Message message = new Message(EMessage.GROW_SOULLEVELUP.CMD(), request.getChannel());
        message.setByte(type);
        message.setByte(suit.getLevel());
        message.setShort(suit.getExp());
        message.setShort(suit.getExpLimit());
        role.sendMessage(message);

        saves.add(EPlayerSaveType.GROW);
        role.savePlayer(saves);
    }

    /**
     * 246 成长项兽魂技能
     */
    public void processGrowSoulSkill(Message request) {
        byte type = request.readByte();
        byte pos = request.readByte();

        EGrow egrow = EGrow.type(type);

        Grow grow = player.getGrowList().get(egrow.I());

        GrowSuit suit = grow.getSuit()[1];

        byte level = suit.getSkill()[pos];
        if (level == GrowDef.MAX_SOUL_SKILL) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_MAX_LEVEL);
            return;
        }

        ++level;
        GrowSkillData upData = egrow.getSoulSkillDataMap().get(level + "_" + (pos + 1));
        if (upData.getNeedLevel() > suit.getLevel()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        if (!role.getPackManager().useGoods(upData.getCost(), EGoodsChangeType.GROW_LEVELUP_CONSUME, saves)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }

        suit.getSkill()[pos] = level;

        Message message = new Message(EMessage.GROW_SOULSKILL.CMD(), request.getChannel());
        message.setByte(type);
        message.setByte(pos);
        message.setByte(level);
        role.sendMessage(message);

        saves.add(EPlayerSaveType.GROW);
        role.savePlayer(saves);
    }

    /**
     * 247 成长项兽魂丹
     */
    public void processGrowSoulPill(Message request) {
        byte type = request.readByte();
        byte num = request.readByte();

        EGrow egrow = EGrow.type(type);

        Grow grow = player.getGrowList().get(egrow.I());

        GrowSuit suit = grow.getSuit()[1];

        byte level, oriLevel;
        level = oriLevel = suit.getPill();

        short error = ErrorDefine.ERROR_NONE;
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        while (num > 0) {
            ++level;
            if (level == GrowDef.MAX_SOUL_PILL) {
                error = ErrorDefine.ERROR_MAX_LEVEL;
                break;
            }
            GrowCostData data = egrow.getSoulPillDataMap().get(level);
            if (data == null) {
                error = ErrorDefine.ERROR_PARAMETER;
                break;
            }
            if (!role.getPackManager().useGoods(data.getCost(), EGoodsChangeType.SPIRIT_PRO_UP_CONSUME, saves)) {
                error = ErrorDefine.ERROR_GOODS_LESS;
                break;
            }
            suit.setPill(level);
            --num;
        }
        if (oriLevel == suit.getPill()) {
            role.sendErrorTipMessage(request, error);
            return;
        }

        Message message = new Message(EMessage.GROW_SOULPILL.CMD(), request.getChannel());
        message.setByte(type);
        message.setByte(level);
        role.sendMessage(message);

        saves.add(EPlayerSaveType.GROW);
        role.savePlayer(saves);
    }

    /**
     * 248 成长项兽魂装备
     */
    public void processGrowSoulEquip(Message request) {
        byte type = request.readByte();

        EGrow egrow = EGrow.type(type);

        Grow grow = player.getGrowList().get(egrow.I());

        GrowSuit suit = grow.getSuit()[1];

        Map<Short, Integer> equipMap = player.getGrowEquipList().get(egrow.getType2().ordinal());

        short[] equipBetter = new short[4];
        System.arraycopy(suit.getEquip(), 0, equipBetter, 0, 4);

        GrowEquipData data;
        for (Entry<Short, Integer> equip : equipMap.entrySet()) {
            data = egrow.getSoulEquipDataMap().get(equip.getKey());
            if (data.getId() > equipBetter[data.getPos() - 1]) {
                equipBetter[data.getPos() - 1] = equip.getKey();
            }
        }

        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);

        for (int i = 0; i < 4; ++i) {
            if (equipBetter[i] > suit.getEquip()[i]) {
                role.getPackManager().useGoods(new DropData((byte) (40 + egrow.getType2().ordinal()), equipBetter[i], 1), EGoodsChangeType.GROW_LEVELUP_CONSUME, saves);
                if (suit.getEquip()[i] > 0)
                    role.getPackManager().addGoods(new DropData((byte) (40 + egrow.getType2().ordinal()), suit.getEquip()[i], 1), EGoodsChangeType.GROW_LEVELUP_CONSUME, saves);
                suit.getEquip()[i] = equipBetter[i];
            }
        }

        Message message = new Message(EMessage.GROW_SOULEQUIP.CMD(), request.getChannel());
        message.setByte(type);
        for (short equip : suit.getEquip()) {
            message.setShort(equip);
        }
        role.sendMessage(message);

        saves.add(EPlayerSaveType.GROW);
        role.savePlayer(saves);
    }

    /**
     * 249 成长项升级3
     */
    public void processGrowLevelUp3(Message request) {
        byte type = request.readByte();
        byte up = request.readByte();//type=0代表普通升阶  type=1代表直升1阶

        EGrow egrow = EGrow.type(type);

        Grow grow = player.getGrowList().get(egrow.I());

        GrowSuit suit = grow.getSuit()[2];

        if (suit.getLevel() == GrowDef.MAX_SOUL_LEVEL) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_MAX_LEVEL);
            return;
        }

        short level = suit.getLevel();
        GrowSeedLevelUpData upData = egrow.getSoulLevelUpDataMap().get(level);
        if (upData.getCostGold().getN() > player.getGold()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOLD_LESS);
            return;
        }

        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        if (up == 1) {
            if (!role.getPackManager().useGoods(new DropData(EGoodsType.ITEM, GrowDef.DAN_UP[egrow.getType3().ordinal()], 1), EGoodsChangeType.GROW_LEVELUP_CONSUME, saves)) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
                return;
            }
            if (suit.getLevel() >= GrowDef.DAN_UP_LEVEL) {
                suit.addExp(GrowDef.DAN_UP_EXP);
            }
        } else {
            List<DropData> costList = new ArrayList<>();
            costList.add(upData.getCostGold());
            costList.add(upData.getCostLimit());
            if (!role.getPackManager().useGoods(costList, EGoodsChangeType.GROW_LEVELUP_CONSUME, saves)) {
                costList.clear();
                costList.add(upData.getCostGold());
                costList.add(upData.getCost());
                if (!role.getPackManager().useGoods(costList, EGoodsChangeType.GROW_LEVELUP_CONSUME, saves)) {
                    role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
                    return;
                } else {
                    suit.addExp(upData.getExp());
                }
            } else {
                suit.addExpLimit(upData.getExp());
            }
        }

        if ((up == 1 && suit.getLevel() < GrowDef.DAN_UP_LEVEL) || suit.getExpLimit() + suit.getExp() >= upData.getExpMax()) {
            suit.setExpLimit((short) 0);
            suit.setExp((short) 0);
            suit.setLevel((byte) ++level);

            if (level == 3) {
                suit.getSkill()[1] = 1;
            } else if (level == 5) {
                suit.getSkill()[2] = 1;
            } else if (level == 7) {
                suit.getSkill()[3] = 1;
            }
        }

        Message message = new Message(request.getCmdId(), request.getChannel());
        message.setByte(type);
        message.setByte(suit.getLevel());
        message.setShort(suit.getExp());
        message.setShort(suit.getExpLimit());
        role.sendMessage(message);

        saves.add(EPlayerSaveType.GROW);
        role.savePlayer(saves);
    }

    /**
     * 250 成长项技能3
     */
    public void processGrowSkill3(Message request) {
        byte type = request.readByte();
        byte pos = request.readByte();

        EGrow egrow = EGrow.type(type);

        Grow grow = player.getGrowList().get(egrow.I());

        GrowSuit suit = grow.getSuit()[2];

        byte level = suit.getSkill()[pos];
        if (level == GrowDef.MAX_SOUL_SKILL) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_MAX_LEVEL);
            return;
        }

        ++level;
        GrowSkillData upData = egrow.getSoulSkillDataMap().get(level + "_" + (pos + 1));
        if (upData.getNeedLevel() > suit.getLevel()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        if (!role.getPackManager().useGoods(upData.getCost(), EGoodsChangeType.GROW_LEVELUP_CONSUME, saves)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }

        suit.getSkill()[pos] = level;

        Message message = new Message(request.getCmdId(), request.getChannel());
        message.setByte(type);
        message.setByte(pos);
        message.setByte(level);
        role.sendMessage(message);

        saves.add(EPlayerSaveType.GROW);
        role.savePlayer(saves);
    }

    /**
     * 251 成长项丹3
     */
    public void processGrowPill3(Message request) {
        byte type = request.readByte();
        byte num = request.readByte();

        EGrow egrow = EGrow.type(type);

        Grow grow = player.getGrowList().get(egrow.I());

        GrowSuit suit = grow.getSuit()[2];

        byte level, oriLevel;
        level = oriLevel = suit.getPill();

        short error = ErrorDefine.ERROR_NONE;
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        while (num > 0) {
            ++level;
            if (level == GrowDef.MAX_SOUL_PILL) {
                error = ErrorDefine.ERROR_MAX_LEVEL;
                break;
            }
            GrowCostData data = egrow.getSoulPillDataMap().get(level);
            if (data == null) {
                error = ErrorDefine.ERROR_PARAMETER;
                break;
            }
            if (!role.getPackManager().useGoods(data.getCost(), EGoodsChangeType.SPIRIT_PRO_UP_CONSUME, saves)) {
                error = ErrorDefine.ERROR_GOODS_LESS;
                break;
            }
            suit.setPill(level);
            --num;
        }
        if (oriLevel == suit.getPill()) {
            role.sendErrorTipMessage(request, error);
            return;
        }

        Message message = new Message(request.getCmdId(), request.getChannel());
        message.setByte(type);
        message.setByte(level);
        role.sendMessage(message);

        saves.add(EPlayerSaveType.GROW);
        role.savePlayer(saves);
    }

    /**
     * 252 成长项装备3
     */
    public void processGrowEquip3(Message request) {
        byte type = request.readByte();

        EGrow egrow = EGrow.type(type);

        Grow grow = player.getGrowList().get(egrow.I());

        GrowSuit suit = grow.getSuit()[2];

        Map<Short, Integer> equipMap = player.getGrowEquipList().get(egrow.getType3().ordinal());

        short[] equipBetter = new short[4];
        System.arraycopy(suit.getEquip(), 0, equipBetter, 0, 4);

        GrowEquipData data;
        for (Entry<Short, Integer> equip : equipMap.entrySet()) {
            data = egrow.getSoulEquipDataMap().get(equip.getKey());
            if (data.getId() > equipBetter[data.getPos() - 1]) {
                equipBetter[data.getPos() - 1] = equip.getKey();
            }
        }

        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);

        for (int i = 0; i < 4; ++i) {
            if (equipBetter[i] > suit.getEquip()[i]) {
                role.getPackManager().useGoods(new DropData((byte) (40 + egrow.getType3().ordinal()), equipBetter[i], 1), EGoodsChangeType.GROW_LEVELUP_CONSUME, saves);
                if (suit.getEquip()[i] > 0)
                    role.getPackManager().addGoods(new DropData((byte) (40 + egrow.getType3().ordinal()), suit.getEquip()[i], 1), EGoodsChangeType.GROW_LEVELUP_CONSUME, saves);
                suit.getEquip()[i] = equipBetter[i];
            }
        }

        Message message = new Message(request.getCmdId(), request.getChannel());
        message.setByte(type);
        for (short equip : suit.getEquip()) {
            message.setShort(equip);
        }
        role.sendMessage(message);

        saves.add(EPlayerSaveType.GROW);
        role.savePlayer(saves);
    }

    /**
     * 253 成长项升级4
     */
    public void processGrowLevelUp4(Message request) {
        byte type = request.readByte();
        byte up = request.readByte();//type=0代表普通升阶  type=1代表直升1阶

        EGrow egrow = EGrow.type(type);

        Grow grow = player.getGrowList().get(egrow.I());

        GrowSuit suit = grow.getSuit()[3];

        if (suit.getLevel() == GrowDef.MAX_SOUL_LEVEL) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_MAX_LEVEL);
            return;
        }

        short level = suit.getLevel();
        GrowSeedLevelUpData upData = egrow.getSoulLevelUpDataMap().get(level);
        if (upData.getCostGold().getN() > player.getGold()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOLD_LESS);
            return;
        }

        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        if (up == 1) {
            if (!role.getPackManager().useGoods(new DropData(EGoodsType.ITEM, GrowDef.DAN_UP[egrow.getType4().ordinal()], 1), EGoodsChangeType.GROW_LEVELUP_CONSUME, saves)) {
                role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
                return;
            }
            if (suit.getLevel() >= GrowDef.DAN_UP_LEVEL) {
                suit.addExp(GrowDef.DAN_UP_EXP);
            }
        } else {
            List<DropData> costList = new ArrayList<>();
            costList.add(upData.getCostGold());
            costList.add(upData.getCostLimit());
            if (!role.getPackManager().useGoods(costList, EGoodsChangeType.GROW_LEVELUP_CONSUME, saves)) {
                costList.clear();
                costList.add(upData.getCostGold());
                costList.add(upData.getCost());
                if (!role.getPackManager().useGoods(costList, EGoodsChangeType.GROW_LEVELUP_CONSUME, saves)) {
                    role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
                    return;
                } else {
                    suit.addExp(upData.getExp());
                }
            } else {
                suit.addExpLimit(upData.getExp());
            }
        }

        if ((up == 1 && suit.getLevel() < GrowDef.DAN_UP_LEVEL) || suit.getExpLimit() + suit.getExp() >= upData.getExpMax()) {
            suit.setExpLimit((short) 0);
            suit.setExp((short) 0);
            suit.setLevel((byte) ++level);

            if (level == 3) {
                suit.getSkill()[1] = 1;
            } else if (level == 5) {
                suit.getSkill()[2] = 1;
            } else if (level == 7) {
                suit.getSkill()[3] = 1;
            }
        }

        Message message = new Message(request.getCmdId(), request.getChannel());
        message.setByte(type);
        message.setByte(suit.getLevel());
        message.setShort(suit.getExp());
        message.setShort(suit.getExpLimit());
        role.sendMessage(message);

        saves.add(EPlayerSaveType.GROW);
        role.savePlayer(saves);
    }

    /**
     * 254 成长项技能4
     */
    public void processGrowSkill4(Message request) {
        byte type = request.readByte();
        byte pos = request.readByte();

        EGrow egrow = EGrow.type(type);

        Grow grow = player.getGrowList().get(egrow.I());

        GrowSuit suit = grow.getSuit()[3];

        byte level = suit.getSkill()[pos];
        if (level == GrowDef.MAX_SOUL_SKILL) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_MAX_LEVEL);
            return;
        }

        ++level;
        GrowSkillData upData = egrow.getSoulSkillDataMap().get(level + "_" + (pos + 1));
        if (upData.getNeedLevel() > suit.getLevel()) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_OPERATION_FAILED);
            return;
        }

        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        if (!role.getPackManager().useGoods(upData.getCost(), EGoodsChangeType.GROW_LEVELUP_CONSUME, saves)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }

        suit.getSkill()[pos] = level;

        Message message = new Message(request.getCmdId(), request.getChannel());
        message.setByte(type);
        message.setByte(pos);
        message.setByte(level);
        role.sendMessage(message);

        saves.add(EPlayerSaveType.GROW);
        role.savePlayer(saves);
    }

    /**
     * 255 成长项丹4
     */
    public void processGrowPill4(Message request) {
        byte type = request.readByte();
        byte num = request.readByte();

        EGrow egrow = EGrow.type(type);

        Grow grow = player.getGrowList().get(egrow.I());

        GrowSuit suit = grow.getSuit()[3];

        byte level, oriLevel;
        level = oriLevel = suit.getPill();

        short error = ErrorDefine.ERROR_NONE;
        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        while (num > 0) {
            ++level;
            if (level == GrowDef.MAX_SOUL_PILL) {
                error = ErrorDefine.ERROR_MAX_LEVEL;
                break;
            }
            GrowCostData data = egrow.getSoulPillDataMap().get(level);
            if (data == null) {
                error = ErrorDefine.ERROR_PARAMETER;
                break;
            }
            if (!role.getPackManager().useGoods(data.getCost(), EGoodsChangeType.SPIRIT_PRO_UP_CONSUME, saves)) {
                error = ErrorDefine.ERROR_GOODS_LESS;
                break;
            }
            suit.setPill(level);
            --num;
        }
        if (oriLevel == suit.getPill()) {
            role.sendErrorTipMessage(request, error);
            return;
        }

        Message message = new Message(request.getCmdId(), request.getChannel());
        message.setByte(type);
        message.setByte(level);
        role.sendMessage(message);

        saves.add(EPlayerSaveType.GROW);
        role.savePlayer(saves);
    }

    /**
     * 256 成长项装备4
     */
    public void processGrowEquip4(Message request) {
        byte type = request.readByte();

        EGrow egrow = EGrow.type(type);

        Grow grow = player.getGrowList().get(egrow.I());

        GrowSuit suit = grow.getSuit()[3];

        Map<Short, Integer> equipMap = player.getGrowEquipList().get(egrow.getType4().ordinal());

        short[] equipBetter = new short[4];
        System.arraycopy(suit.getEquip(), 0, equipBetter, 0, 4);

        GrowEquipData data;
        for (Entry<Short, Integer> equip : equipMap.entrySet()) {
            data = egrow.getSoulEquipDataMap().get(equip.getKey());
            if (data.getId() > equipBetter[data.getPos() - 1]) {
                equipBetter[data.getPos() - 1] = equip.getKey();
            }
        }

        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);

        for (int i = 0; i < 4; ++i) {
            if (equipBetter[i] > suit.getEquip()[i]) {
                role.getPackManager().useGoods(new DropData((byte) (40 + egrow.getType4().ordinal()), equipBetter[i], 1), EGoodsChangeType.GROW_LEVELUP_CONSUME, saves);
                if (suit.getEquip()[i] > 0)
                    role.getPackManager().addGoods(new DropData((byte) (40 + egrow.getType4().ordinal()), suit.getEquip()[i], 1), EGoodsChangeType.GROW_LEVELUP_CONSUME, saves);
                suit.getEquip()[i] = equipBetter[i];
            }
        }

        Message message = new Message(request.getCmdId(), request.getChannel());
        message.setByte(type);
        for (short equip : suit.getEquip()) {
            message.setShort(equip);
        }
        role.sendMessage(message);

        saves.add(EPlayerSaveType.GROW);
        role.savePlayer(saves);
    }

    /**
     * 257 成长项升星
     */
    public void processGrowStarUp(Message request) {
        byte type = request.readByte();
        short id = request.readShort();

        EGrow egrow = EGrow.type(type);

        GrowSeedData data = egrow.getGrowDataMap().get(id);
        if (data == null) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        Grow grow = player.getGrowList().get(egrow.I());
        if (!grow.getMap().containsKey(id)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_PARAMETER);
            return;
        }
        GrowSeed growSeed = grow.getMap().get(id);

        byte level = growSeed.getStarUp();
        int killId = (id - 1) * 7 + (++level);
        StarUpSkillData upData = egrow.getStarUpMap().get((byte) killId);

        EnumSet<EPlayerSaveType> saves = EnumSet.noneOf(EPlayerSaveType.class);
        if (!role.getPackManager().useGoods(upData.getCost(), EGoodsChangeType.GROW_LEVELUP_CONSUME, saves)) {
            role.sendErrorTipMessage(request, ErrorDefine.ERROR_GOODS_LESS);
            return;
        }
        growSeed.setStarUp(level);

        Message message = new Message(request.getCmdId(), request.getChannel());
        message.setByte(type);
        message.setShort(id);
        message.setByte(growSeed.getStarUp());
        role.sendMessage(message);

        saves.add(EPlayerSaveType.GROW);
        role.savePlayer(saves);
    }
}
