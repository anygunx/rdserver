package com.rd.define;

/**
 * @author ---
 * @version 1.0
 * @date 2018年6月11日上午10:48:59
 */
public class TeamDef {

    private TeamDef() {
    }

    public final static byte CROSS = 0;

    public final static byte LADD = 1;

    /**
     * 最大队伍人数
     **/
    public final static byte MAXIMUM = 3;

    public final static int SIGN_SID = 1000000;

    public final static int SIGN_KEY = 100000;

    public final static byte SIGN_TYPE = 10;

    public final static byte LADD_SIGN = 100;

    /**
     * 生死劫开启等级
     **/
    public final static byte LADD_NEED_LV = 105;
    /**
     * 生死劫协助次数
     **/
    public final static byte LADD_ASSIST_NUM = 10;

    public static int getLaddId(int gid, int sid) {
        return gid * TeamDef.LADD_SIGN + sid;
    }

    public static int getLaddGID(int ladd) {
        return ladd / LADD_SIGN;
    }

    public static int getLaddSID(int ladd) {
        return ladd % LADD_SIGN;
    }


    public static void main(String[] args) {

        System.out.println(System.currentTimeMillis());
        System.out.println(Integer.MAX_VALUE);
        System.out.println(Long.MAX_VALUE);

        System.out.println(Math.abs(-2147083647));
        int a = Math.abs(-2147083647) % Short.MAX_VALUE;
        int b = a;
        a += (100000 * 21);
        System.out.println(a / 100000);
        System.out.println(b);

    }


}
