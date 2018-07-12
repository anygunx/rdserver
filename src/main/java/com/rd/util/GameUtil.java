package com.rd.util;

import com.rd.bean.drop.DropData;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class GameUtil {

    /**
     * 返回[min,max]间随机数
     *
     * @param min
     * @param max
     * @return
     */
    public static int getRangedRandom(int min, int max) {
        if (min >= max) {
            return min;
        } else {
            return new Random().nextInt(max - min + 1) + min;
        }
    }

    public static int getRatesIndex(final int[] rates, int random) {
        int sum = 0;
        for (int i = 0; i < rates.length; i++) {
            sum += rates[i];
            if (random <= sum)
                return i;
        }
        return -1;
    }

    public static int getRatesValue(final Map<Integer, Integer> rates, int random) {
        int sum = 0;
        for (Entry<Integer, Integer> entry : rates.entrySet()) {
            sum += entry.getValue();
            if (random <= sum)
                return entry.getKey();
        }
        return -1;
    }

    public static DropData getRatesGoodsValue(final Map<DropData, Integer> rates, int random) {
        int sum = 0;
        for (Entry<DropData, Integer> entry : rates.entrySet()) {
            sum += entry.getValue();
            if (random <= sum)
                return entry.getKey();
        }
        return null;
    }

    public static String getLvConvertStr(int lv) {
        if (lv > 80)
            return (lv / 10 - 8) + "转";
        return lv / 10 * 10 + "级";
    }

}
