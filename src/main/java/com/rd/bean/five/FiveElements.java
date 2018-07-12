package com.rd.bean.five;

/**
 * 五行
 *
 * @author ---
 * @version 1.0
 * @date 2018年1月26日下午7:51:00
 */
public class FiveElements {

    private byte[] elements = new byte[5];

    private byte fuse;

    private short fiveLevel;

    private short matrixLevel;

    public FiveElements() {

    }

    public byte[] getElements() {
        return elements;
    }

    public void setElements(byte[] elements) {
        this.elements = elements;
    }

    public byte getFuse() {
        return fuse;
    }

    public void setFuse(byte fuse) {
        this.fuse = fuse;
    }

    public short getFiveLevel() {
        return fiveLevel;
    }

    public void setFiveLevel(short fiveLevel) {
        this.fiveLevel = fiveLevel;
    }

    public short getMatrixLevel() {
        return matrixLevel;
    }

    public void setMatrixLevel(short matrixLevel) {
        this.matrixLevel = matrixLevel;
    }
}
