package com.rd.bean.support;

import com.google.common.reflect.TypeToken;
import com.rd.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 玩家辅助功能数据
 */
public class SupportData {
    private int playerId;
    /**
     * 玩家使用的兑换码
     **/
    private Map<String, CDKeyData> keys = new HashMap<>();

    public SupportData(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public Map<String, CDKeyData> getKeys() {
        return keys;
    }

    public void setKeys(Map<String, CDKeyData> keys) {
        this.keys = keys;
    }

    public String getKeyJson() {
        return StringUtil.obj2Gson(getKeys());
    }

    public void setKeys(String json) {
        Map<String, CDKeyData> keys = StringUtil.gson2Map(json, new TypeToken<Map<String, CDKeyData>>() {
        });
        for (Map.Entry<String, CDKeyData> entry : keys.entrySet()) {
            entry.getValue().setKey(entry.getKey());
        }
        this.keys = keys;
    }
}
