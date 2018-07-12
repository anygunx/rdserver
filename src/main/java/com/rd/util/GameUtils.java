package com.rd.util;

/**
 * @author ---
 * @version 1.0
 * @date 2018年6月11日下午1:18:38
 */
public class GameUtils {

    private GameUtils() {

    }

    public static short[] parseShortArrayComma(String str) {
        String[] s = str.split(",");
        short[] array = new short[s.length];
        for (int i = 0; i < s.length; ++i) {
            array[i] = Short.parseShort(s[i]);
        }
        return array;
    }
}
