package com.rd.bean.artifact;

import com.google.gson.annotations.SerializedName;
import com.rd.bean.comm.BanConstructor;
import com.rd.bean.comm.BanMethod;
import com.rd.net.message.Message;

import java.util.HashMap;
import java.util.Map;

/**
 * 关卡神器数据
 * Created by XingYun on 2017/11/28.
 */
public class ArtifactBoss {
    @SerializedName("d")
    private byte id;
    @SerializedName("p")
    private Map<Short, Byte> pieces;

    @BanConstructor
    public ArtifactBoss() {
        pieces = new HashMap<>();
    }

    public ArtifactBoss(byte id) {
        this();
        this.id = id;
    }

    public byte getId() {
        return id;
    }

    @BanMethod
    public void setId(byte id) {
        this.id = id;
    }

    public Map<Short, Byte> getPieces() {
        return pieces;
    }

    @BanMethod
    public void setPieces(Map<Short, Byte> pieces) {
        this.pieces = pieces;
    }

    public void getMessage(Message message) {
        message.setByte(getId());
        Map<Short, Byte> current = getPieces();
        message.setByte(current.size());
        for (Map.Entry<Short, Byte> entry : current.entrySet()) {
            message.setShort(entry.getKey());
            message.setByte(entry.getValue());
        }
    }
}
