package com.rd.bean.dragonball;

import com.google.gson.annotations.SerializedName;
import com.rd.model.DragonBallModel;

/**
 * 龙珠数据
 * Created by XingYun on 2017/10/30.
 */
public class DragonBall {
    /**
     * 等级
     **/
    @SerializedName("l")
    private short level = 0;
    /**
     * 碎片数
     **/
    @SerializedName("p")
    private int pieces = 0;
    /**
     * 月卡加成数
     **/
    @SerializedName("a")
    private int mothCardAddition = 0;

    public DragonBall() {
    }

    public short getLevel() {
        return level;
    }

    public void setLevel(short level) {
        this.level = level;
    }

    public int getPieces() {
        return pieces;
    }

    public void setPieces(int pieces) {
        this.pieces = pieces;
    }

    public int getMothCardAddition() {
        return mothCardAddition;
    }

    public void setMothCardAddition(int mothCardAddition) {
        this.mothCardAddition = mothCardAddition;
    }

    public void addMothCardAddition(int addition) {
        if (mothCardAddition >= DragonBallModel.MOTH_CARD_MAX) {
            return;
        }
        int finalValue = mothCardAddition + addition;
        if (finalValue > DragonBallModel.MOTH_CARD_MAX) {
            finalValue = DragonBallModel.MOTH_CARD_MAX;
        }
        setMothCardAddition(finalValue);
    }

    public void addPieces(int value) {
        this.pieces += value;
    }
}
