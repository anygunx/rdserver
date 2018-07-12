package com.rd.bean.copy.cailiao;

public class CLCopyDaily implements Comparable<CLCopyDaily> {


    private byte flag;

    private byte mianfei = 0;

    public byte getMianfei() {
        return mianfei;
    }

    public void setMianfei(byte mianfei) {
        this.mianfei = mianfei;
    }

    /**
     * 购买次数
     */
    private byte butCnt;


    public byte getButCnt() {
        return butCnt;
    }

    public void setButCnt(byte butCnt) {
        this.butCnt = butCnt;
    }

    public void addButCnt() {
        ++butCnt;
    }

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * 通关首奖领取
     */
    private byte tgsj;
    /**
     * 星级
     */
    private byte star;


    public byte getTgsj() {
        return tgsj;
    }

    public void setTgsj(byte tgsj) {
        this.tgsj = tgsj;
    }

    public byte getStar() {
        return star;
    }

    public void setStar(byte star) {
        this.star = star;
    }

    @Override
    public int compareTo(CLCopyDaily o) {
        if (getStar() > o.getStar()) {
            return 1;
        } else if (getStar() < o.getStar()) {
            return -1;

        } else if (getStar() == o.getStar()) {
            if (getId() > o.getId()) {
                return 1;
            } else if (getId() < o.getId()) {
                return -1;
            }
        }

        return 0;
    }


}
