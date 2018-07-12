package com.rd.bean.goods;

import com.alibaba.fastjson.annotation.JSONField;
import com.rd.net.message.Message;

/**
 * <p>Title: 限时物品</p>
 * <p>Description: 限时物品</p>
 * <p>Company: 北京万游畅想科技有限公司</p>
 *
 * @author ---
 * @version 1.0
 * @data 2016年12月27日 下午6:55:36
 */
public class TimeGoods {

    /**
     * 物品类型
     **/
    @JSONField(name = "t")
    private byte type;

    /**
     * 物品ID
     **/
    @JSONField(name = "i")
    private short id;

    /**
     * 限制时间
     **/
    @JSONField(name = "m")
    private long time;

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void getMessage(Message message, long currentTime) {
        message.setByte(type);
        message.setShort(id);
        message.setInt((int) (time - currentTime));
    }
}
