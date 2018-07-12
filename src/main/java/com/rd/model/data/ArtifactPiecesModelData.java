package com.rd.model.data;

/**
 * 关卡神器碎片模板数据
 * Created by XingYun on 2017/11/28.
 */
public class ArtifactPiecesModelData {
    private final short id;
    private final short[] attr;

    public ArtifactPiecesModelData(short id, short[] attr) {
        this.id = id;
        this.attr = attr;
    }

    public short getId() {
        return id;
    }

    public short[] getAttr() {
        return attr;
    }
}
