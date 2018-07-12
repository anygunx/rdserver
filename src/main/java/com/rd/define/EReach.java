package com.rd.define;

import com.rd.bean.dungeon.Dungeon;
import com.rd.game.GameRole;
import com.rd.model.GuanJieModel;
import com.rd.model.data.GuanJieData;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * 达成
 *
 * @author ---
 * @version 1.0
 * @date 2018年3月24日下午4:09:08
 */
public enum EReach {

    /**
     * 1:特价贩卖
     */
    SUPER_SALES(1, null),
    /**
     * 2:羽翼等级
     */
    WING_LEVEL(2, EReach::reachWingLevel),
    /**
     * 3:玩家等级
     */
    PLAYER_LEVEL(3, EReach::reachPlayerLevel),
    /**
     * 4:红装等级
     */
    RED_ARMOR_LEVEL(4, EReach::reachRedArmorLevel),
    /**
     * 5:宝石等级
     */
    GEM_LEVEL(5, EReach::reachGemLevel),
    /**
     * 6:野外关卡数
     */
    FIELD_PASS(6, EReach::reachFieldPass),
    /**
     * 7:神铸件数
     */
    GOD_CAST_NUM(7, EReach::reachGodCastNum),
    /**
     * 8:龙纹等级
     */
    DRAGON_LINES_LEVEL(8, EReach::reachDragonLinesLevel),
    /**
     * 9:镇魔塔关卡数
     */
    TOWN_DEMON_TOWER_PASS(9, EReach::reachTownDemonTower),
    /**
     * 10:战纹总等级
     */
    BATTLE_LINES_ALL_LEVEL(10, EReach::reachBattleLinesAllLevel),
    /**
     * 11:龙鳞等级
     */
    DRAGON_SQUAMA_LEVEL(11, EReach::reachDragonSquamaLevel),
    /**
     * 12:激活图鉴霸者之刃
     */
    CARD_SUIT_1(12, EReach::reachCardSuit1),
    /**
     * 13:激活图鉴国色天香
     */
    CARD_SUIT_2(13, EReach::reachCardSuit2),
    /**
     * 14:激活图鉴凤天魔甲
     */
    CARD_SUIT_3(14, EReach::reachCardSuit3),
    /**
     * 15:激活图鉴法神披风
     */
    CARD_SUIT_4(15, EReach::reachCardSuit4),
    /**
     * 16:激活图鉴天尊道袍
     */
    CARD_SUIT_5(16, EReach::reachCardSuit5),
    /**
     * 17:激活图鉴青龙至尊
     */
    CARD_SUIT_6(17, EReach::reachCardSuit6),
    /**
     * 18:激活图鉴触龙神
     */
    CARD_SUIT_7(18, EReach::reachCardSuit7),
    /**
     * 19:神兵-神魄杖达到X阶
     */
    REFINE_PART_1(19, EReach::reachRefinePart1),
    /**
     * 20:神兵-清心戒达到X阶
     */
    REFINE_PART_2(20, EReach::reachRefinePart2),
    /**
     * 21:神兵-瀚海盾达到X阶
     */
    REFINE_PART_3(21, EReach::reachRefinePart3),
    /**
     * 22:神兵-陨星甲达到X阶
     */
    REFINE_PART_4(22, EReach::reachRefinePart4),
    /**
     * 23:所有神兵达到X阶
     */
    REFINE_ALL_STAGE(23, EReach::reachRefineAllStage),
    /**
     * 24:经脉等级
     */
    MERIDIAN_LEVEL(24, EReach::reachMeridianLevel),
    /**
     * 25:天梯段位
     */
    LADDER_STAGE(25, EReach::reachLadderStage),
    /**
     * 26:五行等级
     */
    FIVE_ELEMENT_LEVEL(26, EReach::reachFiveElementLevel),
    /**
     * 27:龙珠达到X阶
     */
    DRAGON_BEADS_STAGE(27, EReach::reachDragonBeadsStage),
    /**
     * 28:勋章阶段
     */
    MEDAL_STAGE(28, EReach::reachMedalStage),
    /**
     * 29:灵髓等级
     */
    SOUL_MARROW_LEVEL(29, EReach::reachSoulMarrowLevel),
    /**
     * 30:战斗力
     */
    FIGHTING(30, EReach::reachFighting),
    /**
     * 31:官阶阶段
     */
    OFFICIAL_STAGE(31, EReach::reachOfficialStage),
    /**
     * 32:镇魂激活头盔
     */
    TOWN_SOUL_ACTIVE_1(32, EReach::reachTownSoulActive1),
    /**
     * 33:镇魂激活项链
     */
    TOWN_SOUL_ACTIVE_2(33, EReach::reachTownSoulActive2),
    /**
     * 34:镇魂激活护腕
     */
    TOWN_SOUL_ACTIVE_3(34, EReach::reachTownSoulActive3),
    /**
     * 35:镇魂激活戒指
     */
    TOWN_SOUL_ACTIVE_4(35, EReach::reachTownSoulActive4),
    /**
     * 36:镇魂激活腰带
     */
    TOWN_SOUL_ACTIVE_5(36, EReach::reachTownSoulActive5),
    /**
     * 37:镇魂激活战靴
     */
    TOWN_SOUL_ACTIVE_6(37, EReach::reachTownSoulActive6),
    /**
     * 38:镇魂全部激活
     */
    TOWN_SOUL_ACTIVE_7(38, EReach::reachTownSoulActive7),
    /**
     * 39:每日福利
     */
    EVERY_DAY_WELFARE(39, null),
    /**
     * 40:灵阵阶段
     */
    SOUL_MATRIX_STAGE(40, EReach::reachSoulMatrixStage),
    /**
     * 41:装备1绿色战纹
     */
    BATTLE_LINES_GREEN(41, EReach::reachBattleLinesGreen),
    /**
     * 42:装备1紫色战纹
     */
    BATTLE_LINES_PURPLE(42, EReach::reachBattleLinesPurple),
    /**
     * 43:装备1橙色战纹
     */
    BATTLE_LINES_ORANGE(43, EReach::reachBattleLinesOrange),
    /**
     * 44:装备1红色战纹
     */
    BATTLE_LINES_RED(44, EReach::reachBattleLinesRed),;

    private final static Map<Byte, EReach> reachMap = new HashMap<Byte, EReach>() {
        private static final long serialVersionUID = 1L;

        {
            for (EReach reach : EReach.values()) {
                put(reach.type, reach);
            }
        }
    };

    public final static EReach getType(byte key) {
        return reachMap.get(key);
    }

    private final byte type;

    private final BiFunction<GameRole, Integer, Boolean> handler;

    EReach(int type, BiFunction<GameRole, Integer, Boolean> handler) {
        this.type = (byte) type;
        this.handler = handler;
    }

    public byte getType() {
        return type;
    }

    public BiFunction<GameRole, Integer, Boolean> getHandler() {
        return handler;
    }


    /**
     * 2:羽翼等级
     */
    private static boolean reachWingLevel(GameRole role, int target) {
//		for(Character ch:role.getPlayer().getCharacterList()){
//			if(ch.getMountStage()>=target){
//				return true;
//			}
//		}
        return false;
    }

    /**
     * 3：玩家等级
     */
    private static boolean reachPlayerLevel(GameRole role, int target) {
        if (role.getPlayer().getLevelWithRein() < target) {
            return false;
        }
        return true;
    }

    /**
     * 4:红装等级
     */
    private static boolean reachRedArmorLevel(GameRole role, int target) {
//		for(Character ch:role.getPlayer().getCharacterList()){
//			for(Equip equip: ch.getEquipList()){
//				if(equip.getQ()>EGoodsQuality.ORANGE.getValue()){
//					return true;
//				}
//				if(equip.getQ()==EGoodsQuality.ORANGE.getValue()){
//					EquipData model = GoodsModel.getEquipDataById(equip.getG());
//					if(model.getLevel()>=target){
//						return true;
//					}
//				}
//			}
//		}
        return false;
    }

    /**
     * 5：宝石等级
     */
    private static boolean reachGemLevel(GameRole role, int target) {
//		for(Character ch:role.getPlayer().getCharacterList()){
//			int count = 0;
//			for(EquipSlot slot:ch.getEquipSlotList()){
//				count += slot.getJ();
//			}
//			if(count>=target){
//				return true;
//			}
//		}
        return false;
    }

    /**
     * 6:野外关卡数
     */
    private static boolean reachFieldPass(GameRole role, int target) {
        if (role.getPlayer().getMapStageId() < target) {
            return false;
        }
        return true;
    }

    /**
     * 7:神铸件数
     */
    private static boolean reachGodCastNum(GameRole role, int target) {
//		int count = 0;
//		for(Character ch:role.getPlayer().getCharacterList()){
//			for(EquipSlot slot:ch.getEquipSlotList()){
//				if(slot.getZh()>0){
//					++count;
//				}
//			}
//		}
//		if(count<target){
//			return false;
//		}
        return true;
    }

    /**
     * 8:龙纹等级
     */
    private static boolean reachDragonLinesLevel(GameRole role, int target) {
//		for(Character ch:role.getPlayer().getCharacterList()){
//			if(ch.getTongjing()>=target){
//				return true;
//			}
//		}
        return false;
    }

    /**
     * 9:镇魔塔关卡数
     */
    private static boolean reachTownDemonTower(GameRole role, int target) {
        Dungeon dungeon = role.getDungeonManager().getDungeon(DungeonDefine.DUNGEON_TYPE_FENGMOTA);
        if (dungeon.getPass() > target) {
            return true;
        }
        return false;
    }

    /**
     * 10:战纹总等级
     */
    private static boolean reachBattleLinesAllLevel(GameRole role, int target) {
//		int count = 0;
//		for(Character ch : role.getPlayer().getCharacterList()){
//			for (ZhanWen zw : ch.getZhanWen().values()) {
//				if (zw != null) {
//					ZhanWenModelData zwModelData = ZhanWenModel.getZhanWenModelData(zw.getG());
//					if (zwModelData != null) {
//						count+=zwModelData.getLv();
//					}
//				}
//			}
//		}
//		if(count<target){
//			return false;
//		}
        return true;
    }

    /**
     * 11:龙鳞等级
     */
    private static boolean reachDragonSquamaLevel(GameRole role, int target) {
//		for(Character ch:role.getPlayer().getCharacterList()){
//			if(ch.getYudi()>=target){
//				return true;
//			}
//		}
        return false;
    }

    /**
     * 12:激活图鉴霸者之刃
     */
    private static boolean reachCardSuit1(GameRole role, int target) {
        if (role.getPlayer().getCardBook().getCard((short) 5) != null) {
            return true;
        }
        return false;
    }

    /**
     * 13:激活图鉴国色天香
     */
    private static boolean reachCardSuit2(GameRole role, int target) {
        if (role.getPlayer().getCardBook().getCard((short) 10) != null) {
            return true;
        }
        return false;
    }

    /**
     * 14:激活图鉴凤天魔甲
     */
    private static boolean reachCardSuit3(GameRole role, int target) {
        if (role.getPlayer().getCardBook().getCard((short) 15) != null) {
            return true;
        }
        return false;
    }

    /**
     * 15:激活图鉴法神披风
     */
    private static boolean reachCardSuit4(GameRole role, int target) {
        if (role.getPlayer().getCardBook().getCard((short) 20) != null) {
            return true;
        }
        return false;
    }

    /**
     * 16:激活图鉴天尊道袍
     */
    private static boolean reachCardSuit5(GameRole role, int target) {
        if (role.getPlayer().getCardBook().getCard((short) 25) != null) {
            return true;
        }
        return false;
    }

    /**
     * 17:激活图鉴青龙至尊
     */
    private static boolean reachCardSuit6(GameRole role, int target) {
        if (role.getPlayer().getCardBook().getCard((short) 30) != null) {
            return true;
        }
        return false;
    }

    /**
     * 18:激活图鉴触龙神
     */
    private static boolean reachCardSuit7(GameRole role, int target) {
        if (role.getPlayer().getCardBook().getCard((short) 35) != null) {
            return true;
        }
        return false;
    }

    /**
     * 19:神兵-神魄杖达到X阶
     */
    private static boolean reachRefinePart1(GameRole role, int target) {
//		for(Character ch:role.getPlayer().getCharacterList()){
//			ShenBingData data=ch.getShenBing((byte)1);
//			if(data!=null){
//				ShenBingModelData modelData = LianTiModel.getShenBing(data.getId());
//				if(modelData.getStage()>=target){
//					return true;
//				}
//			}
//		}
        return false;
    }

    /**
     * 20:神兵-清心戒达到X阶
     */
    private static boolean reachRefinePart2(GameRole role, int target) {
//		for(Character ch:role.getPlayer().getCharacterList()){
//			ShenBingData data=ch.getShenBing((byte)2);
//			if(data!=null){
//				ShenBingModelData modelData = LianTiModel.getShenBing(data.getId());
//				if(modelData.getStage()>=target){
//					return true;
//				}
//			}
//		}
        return false;
    }

    /**
     * 21:神兵-瀚海盾达到X阶
     */
    private static boolean reachRefinePart3(GameRole role, int target) {
//		for(Character ch:role.getPlayer().getCharacterList()){
//			ShenBingData data=ch.getShenBing((byte)3);
//			if(data!=null){
//				ShenBingModelData modelData = LianTiModel.getShenBing(data.getId());
//				if(modelData.getStage()>=target){
//					return true;
//				}
//			}
//		}
        return false;
    }

    /**
     * 22:神兵-陨星甲达到X阶
     */
    private static boolean reachRefinePart4(GameRole role, int target) {
//		for(Character ch:role.getPlayer().getCharacterList()){
//			ShenBingData data=ch.getShenBing((byte)4);
//			if(data!=null){
//				ShenBingModelData modelData = LianTiModel.getShenBing(data.getId());
//				if(modelData.getStage()>=target){
//					return true;
//				}
//			}
//		}
        return false;
    }

    /**
     * 23:所有神兵达到X阶
     */
    private static boolean reachRefineAllStage(GameRole role, int target) {
//		int count = 0;
//		for(Character ch:role.getPlayer().getCharacterList()){
//			for(ShenBingData entry: ch.getShenBing().values()){
//				ShenBingModelData modelData = LianTiModel.getShenBing(entry.getId());
//				if(modelData != null && modelData.getStage()<target){
//					return false;
//				}
//				++count;
//			}
//		}
//		if(count<12){
//			return false;
//		}
        return true;
    }

    /**
     * 24:经脉等级
     */
    private static boolean reachMeridianLevel(GameRole role, int target) {
//		for(Character ch:role.getPlayer().getCharacterList()){
//			if(ch.getMeridian()>=target){
//				return true;
//			}
//		}
        return false;
    }

    /**
     * 25:天梯阶段
     */
    private static boolean reachLadderStage(GameRole role, int target) {
        if (role.getLadderManager().getStar() < target) {
            return false;
        }
        return true;
    }

    /**
     * 26:五行等级
     */
    private static boolean reachFiveElementLevel(GameRole role, int target) {
        if (role.getPlayer().getFiveElements().getFiveLevel() < target) {
            return false;
        }
        return true;
    }

    /**
     * 27:龙珠达到X阶
     */
    private static boolean reachDragonBeadsStage(GameRole role, int target) {
        if (role.getPlayer().getDragonBall().getLevel() < target) {
            return false;
        }
        return true;
    }

    /**
     * 28:勋章阶段
     */
    private static boolean reachMedalStage(GameRole role, int target) {
        if (role.getPlayer().getMedal() < target) {
            return false;
        }
        return true;
    }

    /**
     * 29:灵髓等级
     */
    private static boolean reachSoulMarrowLevel(GameRole role, int target) {
//		for(Character ch:role.getPlayer().getCharacterList()){
//			int count = 0;
//			for(Soul soul:ch.getSoulList().values()) {
//				if(soul!=null) {
//					for(short i=0;i<soul.getId().length;i++) {
//						LingSuiModelData data=FunctionModel.getLingSuiModelData((byte)soul.getId()[i]);
//						if(data==null){
//							 continue;
//						}
//						count+=data.getLevel();
//					}
//				}
//			}
//			if(count>=target){
//				return true;
//			}
//		}
        return false;
    }

    /**
     * 30:战斗力
     */
    private static boolean reachFighting(GameRole role, int target) {
        role.getPlayer().updateFighting();
        if (role.getPlayer().getFighting() < target) {
            return false;
        }
        return true;
    }

    /**
     * 31:官阶阶段
     */
    private static boolean reachOfficialStage(GameRole role, int target) {
        GuanJieData guanJieData = GuanJieModel.getData(role.getPlayer().getWeiWang());
        if (guanJieData.getLevel() < target) {
            return false;
        }
        return true;
    }

    /**
     * 32:镇魂激活头盔
     */
    private static boolean reachTownSoulActive1(GameRole role, int target) {
//		for(Character ch:role.getPlayer().getCharacterList()){
//			if(ch.getTownSoulEquip()[4]>0){
//				return true;
//			}
//		}
        return false;
    }

    /**
     * 33:镇魂激活项链
     */
    private static boolean reachTownSoulActive2(GameRole role, int target) {
//		for(Character ch:role.getPlayer().getCharacterList()){
//			if(ch.getTownSoulEquip()[5]>0){
//				return true;
//			}
//		}
        return true;
    }

    /**
     * 34:镇魂激活护腕
     */
    private static boolean reachTownSoulActive3(GameRole role, int target) {
//		for(Character ch:role.getPlayer().getCharacterList()){
//			if(ch.getTownSoulEquip()[2]>0){
//				return true;
//			}
//		}
        return true;
    }

    /**
     * 35:镇魂激活戒指
     */
    private static boolean reachTownSoulActive4(GameRole role, int target) {
//		for(Character ch:role.getPlayer().getCharacterList()){
//			if(ch.getTownSoulEquip()[3]>0){
//				return true;
//			}
//		}
        return true;
    }

    /**
     * 36:镇魂激活腰带
     */
    private static boolean reachTownSoulActive5(GameRole role, int target) {
//		for(Character ch:role.getPlayer().getCharacterList()){
//			if(ch.getTownSoulEquip()[6]>0){
//				return true;
//			}
//		}
        return true;
    }

    /**
     * 37:镇魂激活战靴
     */
    private static boolean reachTownSoulActive6(GameRole role, int target) {
//		for(Character ch:role.getPlayer().getCharacterList()){
//			if(ch.getTownSoulEquip()[7]>0){
//				return true;
//			}
//		}
        return true;
    }

    /**
     * 38:镇魂全部激活
     */
    private static boolean reachTownSoulActive7(GameRole role, int target) {
//		for(Character ch:role.getPlayer().getCharacterList()){
//			int count = 0;
//			for(byte d:ch.getTownSoulEquip()){
//				if(d>0){
//					++count;
//				}
//			}
//			if(count==8){
//				return true;
//			}
//		}
        return false;
    }

    /**
     * 40:灵阵阶段
     */
    private static boolean reachSoulMatrixStage(GameRole role, int target) {
        if (role.getPlayer().getFiveElements().getMatrixLevel() < target) {
            return false;
        }
        return true;
    }

    /**
     * 41:装备1绿色战纹
     */
    private static boolean reachBattleLinesGreen(GameRole role, int target) {
//		for(Character ch : role.getPlayer().getCharacterList()){
//			for (ZhanWen zw : ch.getZhanWen().values()) {
//				if (zw != null) {
//					ZhanWenModelData zwModelData = ZhanWenModel.getZhanWenModelData(zw.getG());
//					if (zwModelData != null && zwModelData.getPinzhi()>=EGoodsQuality.GREEN.getValue()) {
//						return true;
//					}
//				}
//			}
//		}
        return false;
    }

    /**
     * 42:装备1紫色战纹
     */
    private static boolean reachBattleLinesPurple(GameRole role, int target) {
//		for(Character ch : role.getPlayer().getCharacterList()){
//			for (ZhanWen zw : ch.getZhanWen().values()) {
//				if (zw != null) {
//					ZhanWenModelData zwModelData = ZhanWenModel.getZhanWenModelData(zw.getG());
//					if (zwModelData != null && zwModelData.getPinzhi()>=EGoodsQuality.BLUE.getValue()) {
//						return true;
//					}
//				}
//			}
//		}
        return false;
    }

    /**
     * 43:装备1橙色战纹
     */
    private static boolean reachBattleLinesOrange(GameRole role, int target) {
//		for(Character ch : role.getPlayer().getCharacterList()){
//			for (ZhanWen zw : ch.getZhanWen().values()) {
//				if (zw != null) {
//					ZhanWenModelData zwModelData = ZhanWenModel.getZhanWenModelData(zw.getG());
//					if (zwModelData != null && zwModelData.getPinzhi()>=EGoodsQuality.PURPLE.getValue()) {
//						return true;
//					}
//				}
//			}
//		}
        return false;
    }

    /**
     * 44:装备1红色战纹
     */
    private static boolean reachBattleLinesRed(GameRole role, int target) {
//		for(Character ch : role.getPlayer().getCharacterList()){
//			for (ZhanWen zw : ch.getZhanWen().values()) {
//				if (zw != null) {
//					ZhanWenModelData zwModelData = ZhanWenModel.getZhanWenModelData(zw.getG());
//					if (zwModelData != null && zwModelData.getPinzhi()>=EGoodsQuality.ORANGE.getValue()) {
//						return true;
//					}
//				}
//			}
//		}
        return false;
    }
}





