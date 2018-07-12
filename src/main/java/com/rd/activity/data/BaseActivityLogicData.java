package com.rd.activity.data;

import org.w3c.dom.Element;

/**
 * 活动逻辑数据
 *
 * @author Created by U-Demon on 2016年11月3日 下午1:24:51
 * @version 1.0.0
 */
public abstract class BaseActivityLogicData {

    //轮次
    public int round;

    /**
     * Map中存取是的key
     *
     * @return
     */
    public abstract String getKey();

    /**
     * XML转换成对象
     *
     * @param root
     */
    public abstract void loadData(Element root);

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

}
