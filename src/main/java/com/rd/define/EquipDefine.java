package com.rd.define;

import com.rd.bean.drop.DropData;
import com.rd.common.GameCommon;
import com.rd.util.DateUtil;

import java.util.HashMap;
import java.util.Map;

public class EquipDefine {

    /**
     * 装备类型：武器
     */
    public static final byte EQUIP_TYPE_WEAPON = 0;
    /**
     * 装备类型：头盔
     */
    public static final byte EQUIP_TYPE_HELMET = 1;

    /**
     * 装备类型：项链
     */
    public static final byte EQUIP_TYPE_XIANGLIAN = 2;
    /**
     * 装备类型：衣服
     */
    public static final byte EQUIP_TYPE_CLOTHES = 3;
    /**
     * 装备类型：披肩
     */
    public static final byte EQUIP_TYPE_PIJIAN = 4;
    /**
     * 装备类型：腰带
     */
    public static final byte EQUIP_TYPE_YAODAI = 5;
    /**
     * 装备类型：护腕
     */
    public static final byte EQUIP_TYPE_HUWAN = 6;
    /**
     * 装备类型：戒指
     */
    public static final byte EQUIP_TYPE_RING = 7;
    /**
     * 装备类型：裤子
     */
    public static final byte EQUIP_TYPE_NECKLACES = 8;
    /**
     * 装备类型：鞋子
     */
    public static final byte EQUIP_TYPE_SHOE = 9;

    /**
     * 装备类型：手镯
     */
    public static final byte EQUIP_TYPE_BRACELET = 4;

    /**
     * 装备类型数量
     */
    public static final byte EQUIP_TYPE_NUM = 6;
    /**
     * 装备位数量
     */
    public static final byte EQUIP_POS_NUM = 10;
    /**
     * 装备位类型
     */
    private static final byte[] EQUIP_POS_TYPE = {
            EquipDefine.EQUIP_TYPE_WEAPON,        //武器
            EquipDefine.EQUIP_TYPE_HELMET,        //头盔
            EquipDefine.EQUIP_TYPE_XIANGLIAN,
            EquipDefine.EQUIP_TYPE_CLOTHES,        //衣服
            EquipDefine.EQUIP_TYPE_PIJIAN,        //披肩
            EquipDefine.EQUIP_TYPE_YAODAI,        //腰带
            EquipDefine.EQUIP_TYPE_HUWAN,        //护腕
            EquipDefine.EQUIP_TYPE_RING,        //戒指
            EquipDefine.EQUIP_TYPE_NECKLACES,    //裤子
            EquipDefine.EQUIP_TYPE_SHOE,        //鞋子
    };

    /**
     * 通过位置获取装备类型
     *
     * @param pos
     * @return
     */
    public static byte getEquipType(int pos) {
        return EQUIP_POS_TYPE[pos];
    }

    public static byte QUALITY_RED = 4;

    //装备位置的名称
    private static final String[] EQUIP_POS_NAMES = TextDefine.EQUIP_POS_NAMES;

    public static String getEquipPosName(int pos) {
        return EQUIP_POS_NAMES[pos];
    }

    /**
     * 装备品质系数
     */
    public static final double[] EQUIP_QUALITY_RATIO = {1.0d, 1.29d, 1.8d, 2.56d, 3.56d, 1.0d};

    public static final byte EQUIP_TONGJING = 0;
    public static final byte EQUIP_YUDI = 1;
    public static final byte EQUIP_ZUOYAN = 0;
    public static final byte EQUIP_YOUYAN = 1;

    //装备背包的上限
    public static final short EQUIP_BAG_MAX = 200;
    //元魂背包的上限
    //public static final short SPIRIT_BAG_MAX = 30;
    //主宰背包的上限
    public static final short DOM_BAG_MAX = 1000;

    public static final byte[] EQUIP_BASE_ATTR_IDX = {0, 0, 0, 1, 1, 2, 2, 2};

    public static final float[] EQUIP_BASE_ATTR_RATIO = {0.5f, 0.25f, 0.25f, 0.5f, 0.5f, 0.3f, 0.4f, 0.3f};

    public static final float[] EQUIP_BASE_QUALITY_RATIO = {1.0f, 1.1f, 1.3f, 1.5f, 1.8f, 3.0f};

    public static final float[] EQUIP_ADD_QUALITY_RATIO = {1.0f, 1.1f, 1.1f, 1.1f, 1.2f, 1.2f};

    public static final byte[] EQUIP_QUALITY_ADD_NUM = {1, 1, 2, 3, 4, 5};

    public static final byte[] EQUIP_ADD_ATTR_RANDOM = {3, 3, 4, 15, 15, 15, 15, 15, 15};

    /**
     * 装备掉落随机品质
     */
    public static final int[] EQUIP_DROP_QUALITY_RANDOM = {6000, 4000, 0, 0, 0};
    /**
     * 装备掉落概率
     */
    public static final float[] DROP_EQUIP_RANDOM = {0.30f, 0.30f, 0.30f, 0.30f, 0.30f, 0.28f, 0.28f, 0.28f, 0.28f, 0.28f, 0.25f, 0.25f, 0.25f, 0.25f, 0.25f, 0.23f, 0.23f, 0.23f, 0.23f, 0.23f, 0.20f, 0.20f, 0.20f, 0.20f, 0.20f, 0.18f, 0.18f, 0.18f, 0.18f, 0.18f, 0.15f, 0.15f, 0.15f, 0.15f, 0.15f, 0.13f, 0.13f, 0.13f, 0.13f, 0.13f, 0.10f, 0.10f, 0.10f, 0.10f, 0.10f, 0.09f, 0.09f, 0.09f, 0.09f, 0.09f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f, 0.08f};
    /**
     * 装备熔炼经验
     */
    public static final int[] MELT_EXP = {10, 15, 20, 40};
    /**
     * 装备熔炼灵气
     */
    public static final int[] MELT_ANIMA = {2, 3, 4, 5};
    /**
     * 灵器熔炼经验
     */
    public static final int[] MELT_ARTIFACT_EXP = {100, 150, 200, 400, 1000};
    /**
     * 装备熔炼品质系数
     */
    public static final byte[] MELT_QUALITY_FACTOR = {1, 2, 3, 4};
    /**
     * 装备熔炼强化石数量
     */
    public static final byte[] MELT_STRENGTHEN_STONE = {1, 1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9};

    /**
     * BOSS装备掉落随机品质
     */
    public static final int[] BOSS_EQUIP_DROP_QUALITY_RONDOM = {0, 6000, 4000, 0, 0};
    /**
     * BOSS装备掉落随机品质
     */
    public static final int[] BOSS_EQUIP_DROP_QUALITY_RONDOM_10 = {0, 4500, 3000, 2500, 0};

    //装备·寻宝图
    public static final byte EQUIP_TREASURE_MAP = 71;

    //寻宝物品
    public static final Map<DropData, Integer> XUNBAO_ITEM = new HashMap<DropData, Integer>() {
        private static final long serialVersionUID = 1L;

        {
            put(new DropData((byte) 3, 3, 10), 1);
            put(new DropData((byte) 3, 4, 1), 1);
            put(new DropData((byte) 3, 48, 1), 10);
            put(new DropData((byte) 2, 9, 1), 5);
            put(new DropData((byte) 2, 12, 1), 5);
            put(new DropData((byte) 2, 13, 1), 5);
            put(new DropData((byte) 2, 14, 1), 5);
            put(new DropData((byte) 2, 15, 1), 5);
            put(new DropData((byte) 2, 16, 1), 5);
            put(new DropData((byte) 2, 17, 1), 5);
            put(new DropData((byte) 2, 18, 88), 8);
            put(new DropData((byte) 2, 18, 44), 2);
            put(new DropData((byte) 2, 18, 33), 8);
            put(new DropData((byte) 2, 18, 100), 16);
            put(new DropData((byte) 2, 20, 10), 2);
            put(new DropData((byte) 2, 20, 2), 16);
            put(new DropData((byte) 2, 20, 1), 1);
        }
    };
    //寻宝装备
    public static final Map<Integer, Integer> XUNBAO_EQUIP_TEN = new HashMap<Integer, Integer>() {
        private static final long serialVersionUID = 1L;

        {
            //同级或上下10级紫装
            put(3000, 9000);
            //橙装
            put(4030, 0);
            put(4040, 0);
            put(4050, 0);
            put(4060, 250);
            put(4070, 330);
            put(4080, 100);
            put(4090, 100);
            put(4100, 100);
            put(4110, 100);
            put(4120, 10);
            put(4130, 9);
            put(4140, 0);
            put(4150, 0);
            put(4160, 0);
            put(4170, 0);
            put(4180, 0);
            put(4190, 0);
            put(4200, 0);
            //红装
            put(5110, 1);
        }
    };
    public static final Map<Integer, Integer> XUNBAO_EQUIP_SINGLE = new HashMap<Integer, Integer>() {
        private static final long serialVersionUID = 1L;

        {
            put(3000, 9000);
            //橙装
            put(4030, 0);
            put(4040, 0);
            put(4050, 0);
            put(4060, 250);
            put(4070, 330);
            put(4080, 100);
            put(4090, 100);
            put(4100, 100);
            put(4110, 100);
            put(4120, 10);
            put(4130, 10);
            put(4140, 0);
            put(4150, 0);
            put(4160, 0);
            put(4170, 0);
            put(4180, 0);
            put(4190, 0);
            put(4200, 0);
            //红装
            put(5110, 0);
        }
    };
    //寻宝首抽送
    public static final short[] XUNBAO_FIRST = {51, 177, 303};

    public static final short TOWN_SOUL_TURNTABLE_TICKET = 103;

    public static final short TOWN_SOUL_TURNTABLE_ONE = 500;

    public static final short TOWN_SOUL_TURNTABLE_TEN = 4800;

    public static final int TOWN_SOUL_TURNTABLE_RESET_TIME = (int) (7 * DateUtil.DAY);

    public static final byte TOWN_SOUL_INITIAL_PROBAILITY = 1;

    /**
     * 是否随机掉落装备
     *
     * @param mapLevel
     * @return
     */
    public static boolean isRandomDropEquip(int mapLevel) {
        if (mapLevel >= DROP_EQUIP_RANDOM.length) {
            mapLevel = DROP_EQUIP_RANDOM.length - 1;
        }
        if ((Math.random() * 100) < DROP_EQUIP_RANDOM[mapLevel] * 100) {
            return true;
        }
        return false;
    }

    /**
     * 得到掉落装备等级
     *
     * @param mapLevel
     * @return
     */
    public static int getDropEquipLevel(int mapLevel) {
        mapLevel -= 1;
        if (mapLevel < 10) {
            return 1;
        } else {
            return mapLevel - (mapLevel % 10);
        }
    }

    /**
     * 随机装备品质
     *
     * @return
     */
    public static byte getRandomEquipQuality() {
        return (byte) GameCommon.getRandomIndex(EquipDefine.EQUIP_DROP_QUALITY_RANDOM);
    }

    /**
     * 得到随机装备位
     */
    public static byte getRandomEquipType() {
        return (byte) (Math.random() * 8);
    }


}
