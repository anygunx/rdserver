package com.rd.define;

import com.rd.bean.dungeon.IDungeonTypeData;
import com.rd.bean.dungeon.type.FengmoTypeData;
import com.rd.util.DateUtil;
import com.rd.util.StringUtil;

public class DungeonDefine {

    /**
     * 副本：个人boss
     */
    public final static byte DUNGEON_TYPE_BOSS = 1;
    /**
     * 副本：材料
     */
    public final static byte DUNGEON_TYPE_MATERIAL = 2;
    /**
     * 副本：诛仙台
     */
    public final static byte DUNGEON_TYPE_DEKARON = 3;
    /**
     * 副本：帮会
     */
    public final static byte DUNGEON_TYPE_GANG = 4;
    /**
     * 副本：锁妖塔
     */
    public final static byte DUNGEON_TYPE_FENGMOTA = 8;
    /**
     * 副本：主宰试炼
     **/
    public final static byte DUNGEON_TYPE_LINGSUI = 12;
    /**
     * 副本：圣物
     */
    public final static byte DUNGEON_TYPE_HOLY = 13;

    public enum EDungeon {
        BOSS(DUNGEON_TYPE_BOSS, null),
        MATERIAL(DUNGEON_SUB_TYPE_MERIDIAN, null),
        DEKARON(DUNGEON_TYPE_DEKARON, null),
        GANG(DUNGEON_TYPE_GANG, null),
        UNDEFINE1(0, null),
        UNDEFINE2(0, null),
        UNDEFINE3(0, null),
        FENGMOTA(DUNGEON_TYPE_FENGMOTA, FengmoTypeData.class);
//		ZHUZAISHILIAN(DUNGEON_TYPE_LINGSUI,);

        private final byte type;
        private final Class dataClazz;

        EDungeon(int type, Class clazz) {
            this.type = (byte) type;
            this.dataClazz = clazz;
        }

        public static <D extends IDungeonTypeData> D builder(byte type, String str) {
            EDungeon dungeon = getDungeon(type);
            if (dungeon == null) {
                return null;
            }
            Class clazz = dungeon.dataClazz;
            if (clazz == null) {
                return null;
            }
            if (StringUtil.isEmpty(str)) {
                try {
                    return (D) clazz.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return (D) StringUtil.gson2Obj(str, dungeon.dataClazz);
        }

        public static EDungeon getDungeon(byte type) {
            for (EDungeon dungeon : values()) {
                if (dungeon.type == type) {
                    return dungeon;
                }
            }
            return null;
        }
    }

    /**
     * 子副本：宝石
     */
    public static final byte DUNGEON_SUB_TYPE_GEM = 2;
    /**
     * 子副本：经脉
     */
    public static final byte DUNGEON_SUB_TYPE_MERIDIAN = 3;
    /**
     * 子副本： 龙纹
     */
    public static final byte DUNGEON_SUB_TYPE_MIRROR = 4;
    /**
     * 子副本：龙鳞
     */
    public static final byte DUNGEON_SUB_TYPE_FLUTE = 5;
    /**
     * 子副本：成就
     */
    public static final byte DUNGEON_SUB_TYPE_CUILIAN = 8;
    /**
     * 子副本：羽翼
     */
    public static final byte DUNGEON_SUB_TYPE_WING = 9;
    /**
     * 子副本：五行
     **/
    public static final byte DUNGEON_SUB_TYPE_FIVE = 11;

    public static final short HOLY_FIGHT_TIME = (short) (DateUtil.SECOND * 30);
}
