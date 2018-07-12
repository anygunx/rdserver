package com.rd.common;

import com.rd.bean.drop.DropData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ---
 * @version 1.0
 * @date 2018年4月17日下午3:09:22
 */
public class ParseCommon {

    private ParseCommon() {

    }

    public static List<Short> parseCommaList(String str) {
        List<Short> list = new ArrayList<>();
        String[] array = str.split(",");
        for (String s : array) {
            list.add(Short.parseShort(s));
        }
        return list;
    }

    public static short[] parseCommaShort(String str) {
        String[] array = str.split(",");
        short[] sa = new short[array.length];
        for (int i = 0; i < array.length; ++i) {
            sa[i] = Short.parseShort(array[i]);
        }
        return sa;
    }

    public static int[] parseCommaInt(String str) {
        String[] array = str.split(",");
        int[] sa = new int[array.length];
        for (int i = 0; i < array.length; ++i) {
            sa[i] = Integer.parseInt(array[i]);
        }
        return sa;
    }

    public static DropData[] parseSemicolonDropData(String str) {
        String[] array = str.split(";");
        String[] goods;
        DropData[] data = new DropData[array.length];
        for (int i = 0; i < array.length; ++i) {
            goods = array[i].split(",");
            data[i] = new DropData(Byte.parseByte(goods[0]), Short.parseShort(goods[1]), Integer.parseInt(goods[2]));
        }
        return data;
    }

    public static List<DropData> parseSemicolonDropDataList(String str) {
        String[] array = str.split(";");
        String[] goods;
        List<DropData> list = new ArrayList<>();
        for (int i = 0; i < array.length; ++i) {
            goods = array[i].split(",");
            list.add(new DropData(Byte.parseByte(goods[0]), Short.parseShort(goods[1]), Integer.parseInt(goods[2])));
        }
        return list;
    }
}
