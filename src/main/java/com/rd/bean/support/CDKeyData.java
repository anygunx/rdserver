package com.rd.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CDKeyData {
    @Expose(serialize = false)
    private String key;
    @SerializedName("d")
    private int modelId;

    public CDKeyData() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String keys) {
        this.key = keys;
    }

    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }
}
