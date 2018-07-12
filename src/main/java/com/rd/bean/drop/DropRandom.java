package com.rd.bean.drop;

import java.util.List;

public class DropRandom {

    private short rate;

    private List<DropData> dropData;

    public short getRate() {
        return rate;
    }

    public void setRate(short rate) {
        this.rate = rate;
    }

    public List<DropData> getDropData() {
        return dropData;
    }

    public void setDropData(List<DropData> dropData) {
        this.dropData = dropData;
    }
}
