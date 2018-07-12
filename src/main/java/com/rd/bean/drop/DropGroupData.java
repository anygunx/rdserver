package com.rd.bean.drop;

import com.rd.define.FightDefine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DropGroupData {

    private short id;

    private byte dropType;

    private Map<Byte, DropParamsData> paramMap;

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public byte getDropType() {
        return dropType;
    }

    public void setDropType(byte dropType) {
        this.dropType = dropType;
    }

    public Map<Byte, DropParamsData> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<Byte, DropParamsData> paramMap) {
        this.paramMap = paramMap;
    }

    public List<DropData> getRandomDrop() {
        List<DropData> dropList = new ArrayList<>();
        for (DropParamsData temp : paramMap.values()) {
            temp.isHit = false;
        }
        //计算掉落 所有掉落共享概率
        for (int i = 0; i < dropType; ++i) {
            short randomValue = (short) (Math.random() * FightDefine.RANDOM_BASE);
            DropParamsData dropParamsData = null;
            for (DropParamsData temp : paramMap.values()) {
                if (temp.isHit == false && temp.getProbability() > randomValue) {
                    dropParamsData = temp;
                    dropParamsData.isHit = true;
                    break;
                } else if (temp.isHit == false) {
                    randomValue -= temp.getProbability();
                }
            }
            //判断是否随机到大类型
            if (dropParamsData != null) {
                for (int j = 0; j < dropParamsData.getDropNum(); ++j) {
                    randomValue = (short) (Math.random() * FightDefine.RANDOM_BASE);
                    DropRandom dropRandom = null;
                    for (DropRandom temp : dropParamsData.getDropRandomList()) {
                        if (temp.getRate() > randomValue) {
                            dropRandom = temp;
                            break;
                        } else {
                            randomValue -= temp.getRate();
                        }
                    }
                    //随机到掉落物品
                    if (dropRandom != null) {
                        dropList.addAll(dropRandom.getDropData());
                    }
                }
            }
        }
        return dropList;
    }
}
