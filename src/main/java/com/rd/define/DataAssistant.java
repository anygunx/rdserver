package com.rd.define;

/**
 * 数据小助手
 *
 * @author ---
 * @version 1.0
 * @date 2018年4月8日下午5:07:56
 */
public class DataAssistant {

    private DataAssistant() {

    }

    public static int isTrue(int index, int data) {
        return data >> index & 1;
    }

}
