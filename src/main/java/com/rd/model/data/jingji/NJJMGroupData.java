package com.rd.model.data.jingji;

/***
 * 竞技怪物 组合
 * @author MyPC
 *
 */
public class NJJMGroupData {

    private int id;


    private int fir_monster_id;
    private int sen_monster_id;

    public NJJMGroupData(int id, int fir_monster_id, int se_monster_id) {
        this.id = id;

        this.fir_monster_id = fir_monster_id;
        this.sen_monster_id = se_monster_id;
    }

    public int getId() {
        return id;
    }


    public int getFir_monster_id() {
        return fir_monster_id;
    }

    public int getSen_monster_id() {
        return sen_monster_id;
    }

}
