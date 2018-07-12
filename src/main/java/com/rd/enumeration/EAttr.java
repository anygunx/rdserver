package com.rd.enumeration;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ---
 * @version 1.0
 * @date 2018年4月18日下午2:39:41
 */
public enum EAttr {
    /*
        编号	属性		英文名称		数值类型		示例			属性说明								属性价值
        1	速度		luck		整数			100			出手先后的判定参数							100
        2	攻击		att			整数			100			重要PK参数，输出能力的体现，数值越高，输出能力越高		4
        3	防御		def			整数			100			重要pk参数，玩家防御能力的体现，数值越高，免伤能力越强	4
        4	生命		hp			整数			100			重要pk,玩家生存能力的体现，数值小于零时，玩家死亡		0.5
        5	无视防御	redef		整数			100			重要pk参数，削弱目标御能力，同减免无视共同作用		10
        6	暴击		cri			整数			100			伤害效果判定参数，同抗暴运算					8
        7	抗暴		subcri		整数			100			伤害效果判定参数，同暴击运算					8
        8	命中		hit			整数			100			命中结果判定参数，命中能力的体现，同闪避运算			11
        9	闪避		dogge		整数			100			命中结果判定参数，闪避能力的体现，同命中运算			11
        10	暴伤增加	crihurt		万分比表示的百分比	10000=100%	暴击情境下，伤害数值决定参数，与爆伤减免共同作用		4
        11	暴伤减免	crireduce	万分比表示的百分比	10000=100%	暴击情境下，伤害数值决定参数，与爆伤增加共同作用		4
        12	减免无视	indef		整数			100			重要PK参数，玩家防御能力的体现，对抗目标的防御削减能力，同无视防御共同作用	10
        13	伤害增加	indamage	万分比表示的百分比	10000=100%	任何情境下，伤害数值决定参数，与伤害减少共同作用		8
        14	伤害减少	redamage	万分比表示的百分比	10000=100%	任何情景下，伤害数值决定参数，与伤害增加共同作用		8
        15	pvp增伤	pvpdamage	万分比表示的百分比	10000=100%	pvp情境下，伤害数值决定参数，与pvp减伤共同作用		6
        16	pvp减伤	repvpdamage	万分比表示的百分比	10000=100%	pvp情境下，伤害数值决定参数，与pvp增伤共同作用		6
        17	pve增伤	pvedamage	万分比表示的百分比	10000=100%	pve情景下，伤害数值决定参数，与pve增伤共同作用		6
        18	pve减伤	repvedamage	万分比表示的百分比	10000=100%	pve情境下，伤害数值决定参数，与pve减伤共同作用		6
    */
    LUCK("luck", 100),                //	1 速度
    ATK("att", 4),                    //	2 攻击
    DEF("def", 4),                    //	3 防御
    HP("hp", 0.5),                    //	4 生命
    REDEF("redef", 10),                //	5 无视防御
    CRIT("cri", 8),                    //	6 暴击
    SUBCRIT("subcri", 8),            //	7 抗暴
    HIT("hit", 11),                    //	8 命中
    DODGE("dogge", 11),                //	9 闪避
    CRITHURT("crihurt", 4),            //	10 暴伤增加
    CRITREDUCE("crireduce", 4),        //	11 暴伤减免
    INDEF("indef", 10),                //	12 减免无视
    INDAMAGE("indamage", 8),            //	13 伤害增加
    REDAMAGE("redamage", 8),            //	14 伤害减少
    PVPDAMAGE("pvpdamage", 6),        //	15 pvp增伤
    REPVPDAMAGE("repvpdamage", 6),    //	16 pvp减伤
    PVEDAMAGE("pvedamage", 6),        //	17 pve增伤
    REPVEDAMAGE("repvedamage", 6),    //	18 pve减伤
    ;

    private String field;

    private double factor;

    public static final int SIZE;

    public static final Map<Integer, EAttr> valueMap;

    static {
        valueMap = new HashMap<>();
        for (EAttr type : EAttr.values()) {
            valueMap.put(type.ordinal(), type);
        }
        SIZE = EAttr.values().length;
    }

    private EAttr(String field, double factor) {
        this.field = field;
        this.factor = factor;
    }

    public static int[] getIntAttr(JSONObject object) {
        int[] attr = new int[SIZE];
        for (EAttr type : EAttr.values()) {
            if (object.has(type.field))
                attr[type.ordinal()] = object.getInt(type.field);
        }
        return attr;
    }

    public double getFactor() {
        return factor;
    }

}
