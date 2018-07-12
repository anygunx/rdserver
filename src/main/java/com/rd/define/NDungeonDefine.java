package com.rd.define;

import java.util.HashMap;
import java.util.Map;

/***
 *
 * 1 材料副本 2水晶宫 3天门挑战 4个人boss 5全名 6龙王宝藏
 */
public class NDungeonDefine {

    public static final byte SUBTYPE = 0;

    public static final byte SUBTYPE_1 = 1;

    public static final byte MAX_STAR = 3;
    public static final byte ZHONGKUI_MIN_STAR = 1;
    public static final byte ZHONGKUI_MAX_STAR = 7;
    public static final byte ZHONGKUI_COUNT = 10;

    /**
     * 密藏每个大关卡的
     */
    public static final byte MIN_NUM = 6;

    public enum NDungeon {
        CAILIAO(1),
        SHUIJINGGONG(2),
        TIANMEN(3),
        GERENBOSS(4),
        QUANMINBOSS(5),
        MIZANG(6),
        ZHONGDAN(7);

        private int type;

        NDungeon(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        private static Map<Byte, NDungeon> valueMap;

        static {
            valueMap = new HashMap<>();
            for (NDungeon etit : NDungeon.values()) {
                valueMap.put((byte) etit.type, etit);
            }
        }

        public static NDungeon getNPiFuType(byte type) {
            return valueMap.get(type);
        }

    }

}
