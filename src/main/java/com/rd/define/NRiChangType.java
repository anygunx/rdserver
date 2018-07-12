package com.rd.define;

public class NRiChangType {
    public static final short LEVELVIP_LIMITE = 6;

    public static final short LEVEL_LIMITE_MAX = 40;

    public static final short YEWAI_LIMITE_MAX = 300;
    public static final byte YEWAI_PRICE = 10;

    public static final short YUANBAO_PRICE = 10;

    public static final short TEAM_COPY_LIMITE_MAX = 10;
    public static final byte TEAM_COPY_PRICE = 80;

    public static final byte ZHONGKUI_COPY_COUNT = 5;
    public static final byte ZHONGKUI_TOTAL_COPY_COUNT = 10;
    public static final byte ZHONGKUI_COPY_PRICE = 100;

    public static final byte RICHANG_300_COUNT_REWARD_INDEX_MAX = 2;


    public enum NRiChangEnum {
        ZHONGKUI(1, "钟馗"),
        TEAMCOPY(2, "组队副本"),
        RI_300(3, "日300");

        private int id;
        private String name;

        NRiChangEnum(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public static NRiChangEnum getType(int id) {
            for (NRiChangEnum type : NRiChangEnum.values()) {
                if (type.id == id)
                    return type;
            }
            return null;
        }
    }
}
