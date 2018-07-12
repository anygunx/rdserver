package com.rd.bean.goods;

import com.rd.net.message.Message;

/**
 * 灵器
 *
 * @author Created by U-Demon on 2016年11月1日 下午2:04:28
 * @version 1.0.0
 */
public class Artifact {
    /**
     * 灵器id
     */
    private short d;
    /**
     * 物品原型id
     */
    private short g;
//	/** 品质 */
//	private byte q;
//	/** 星级 */
//	private byte s;

    public short getD() {
        return d;
    }

    public void setD(short d) {
        this.d = d;
    }

    public short getG() {
        return g;
    }

    public void setG(short g) {
        this.g = g;
    }

//	public byte getQ() {
//		return q;
//	}
//
//	public void setQ(byte q) {
//		this.q = q;
//	}
//
//	public byte getS() {
//		return s;
//	}
//
//	public void setS(byte s) {
//		this.s = s;
//	}

    public Artifact() {

    }

    public void getMessage(Message message) {
        message.setShort(this.d);
        message.setShort(this.g);
//		message.setByte(this.q);
//		message.setByte(this.s);
    }

}
