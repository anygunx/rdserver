package com.rd.enumeration;

/**
 * @author ---
 * @version 1.0
 * @date 2018年7月2日下午3:41:15
 */
public enum ETrigger {

    /**
     * 0:攻击触发
     **/
    ATK,
    /**
     * 1:受单体攻击触发
     **/
    SINGLE_HIT,
    /**
     * 2：受全体攻击触发
     **/
    GROUP_HIT,
    /**
     * 3:受击触发
     **/
    HIT,;

    public static ETrigger getETrigger(int atkNum) {
        if (atkNum > 1) {
            return ETrigger.GROUP_HIT;
        }
        return ETrigger.SINGLE_HIT;
    }
}
