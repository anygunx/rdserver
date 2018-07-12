package com.rd.model.data;

/**
 * 仙侣奇缘
 *
 * @author wh
 */
public class QiYuanData {

    private final int id;
    private final int need_lv;
    private final int att;
    private final int def;

    public QiYuanData(int id, int need_lv, int att, int def) {
        super();
        this.id = id;
        this.need_lv = need_lv;
        this.att = att;
        this.def = def;
    }

    public int getId() {
        return id;
    }

    public int getNeed_lv() {
        return need_lv;
    }

    public int getAtt() {
        return att;
    }

    public int getDef() {
        return def;
    }
}
