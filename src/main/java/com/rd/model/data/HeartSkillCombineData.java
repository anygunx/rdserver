package com.rd.model.data;

import org.apache.commons.lang3.RandomUtils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class HeartSkillCombineData {

    private short count;

    private List<Entry<Byte, Byte>> list = new ArrayList<>();

    public HeartSkillCombineData() {

    }

    public void addData(byte weight, byte id) {
        this.list.add(new AbstractMap.SimpleEntry<>(weight, id));
        this.count += weight;
    }

    public byte combine() {
        int random = RandomUtils.nextInt(0, count);
        int weight = 0;
        for (Entry<Byte, Byte> entry : this.list) {
            weight += entry.getKey();
            if (weight > random) {
                return entry.getValue();
            }
        }
        return list.get(0).getValue();
    }
}
