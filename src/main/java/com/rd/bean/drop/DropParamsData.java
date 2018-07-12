package com.rd.bean.drop;

import java.util.ArrayList;
import java.util.List;

public class DropParamsData {

    private final byte type;
    private final short probability;
    private final byte dropNum;
    private List<DropRandom> dropRandomList;
    public boolean isHit;

    public DropParamsData(byte type, short probability, byte dropNum, List<DropRandom> dropData) {
        this.type = type;
        this.probability = probability;
        this.dropNum = dropNum;
        this.dropRandomList = dropData;
    }

    public short getProbability() {
        return probability;
    }

    public byte getType() {
        return type;
    }

    public byte getDropNum() {
        return dropNum;
    }

    public void setDropRandomList(List<DropRandom> dropRandomList) {
        this.dropRandomList = dropRandomList;
    }

    public List<DropRandom> getDropRandomList() {
        return dropRandomList;
    }

    /**
     * @param type    掉落类型
     * @param dropNum 掉落事件类型
     * @param str     参数字符串 - 概率
     * @return
     */
    public static DropParamsData createWithStringParams(byte type, byte dropNum, String percent, String str) {
        String[] dataParams = str.split("#");
        short probability = Short.valueOf(percent);
        List<DropRandom> dropDataList = new ArrayList<>();

        for (int j = 0; j < dataParams.length; ++j) {
            String[] dataParam = dataParams[j].split("\\$");
            if (dataParam.length == 0) {
                continue;
            }

            String[] param = dataParam[0].split(",");
            Short dataProb = Short.valueOf(param[3].trim());
            if (dataProb == 0) {
                continue;
            }

            DropRandom random = new DropRandom();
            random.setRate(dataProb);
            List<DropData> dropList = new ArrayList<DropData>();
            for (int i = 0; i < dataParam.length; ++i) {
                param = dataParam[i].split(",");
                short id = Short.parseShort(param[0]);
                byte quality = Byte.valueOf(param[1].trim());
                int num = Integer.valueOf(param[2].trim());
                DropData data = new DropData();
                data.setT(type);
                data.setG(id);
                data.setN(num);
                data.setQ(quality);
                dropList.add(data);
            }
            random.setDropData(dropList);
            dropDataList.add(random);
        }

        return new DropParamsData(type, probability, dropNum, dropDataList);
    }
}
