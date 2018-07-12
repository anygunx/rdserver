package com.rd.bean.rank;

public class ActivityRank implements Comparable<ActivityRank> {

    private int id;

    private String n;
    /**
     * 比较值1
     */
    private int v1 = 0;
    /**
     * 比较值2
     */
    private int v2 = 0;

    //不参与排序 value nosorted
    private int vn = 0;
    private int vn2 = 0;

    //排序值
    private long m = 0;

    @Override
    public int compareTo(ActivityRank ar) {
        if (v1 > ar.getV1())
            return -1;
        else if (v1 < ar.getV1())
            return 1;
        if (v2 > ar.getV2())
            return -1;
        else if (v2 < ar.getV2())
            return 1;
        if (m > ar.getM())
            return 1;
        else if (m < ar.getM())
            return -1;
        return 0;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public int getV1() {
        return v1;
    }

    public void setV1(int v1) {
        this.v1 = v1;
    }

    public int getV2() {
        return v2;
    }

    public void setV2(int v2) {
        this.v2 = v2;
    }

    public int getVn() {
        return vn;
    }

    public void setVn(int vn) {
        this.vn = vn;
    }

    public int getVn2() {
        return vn2;
    }

    public void setVn2(int vn2) {
        this.vn2 = vn2;
    }

    public long getM() {
        return m;
    }

    public void setM(long m) {
        this.m = m;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
