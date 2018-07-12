package com.rd.model.data.copy.tianmen;

import com.rd.bean.drop.DropData;

import java.util.ArrayList;
import java.util.List;

public class NTianMenDBData implements Comparable<NTianMenDBData> {
    private int id;
    private int copyId;
    private List<DropData> dropDataList = new ArrayList<>();

    public int getCopyId() {
        return copyId;
    }

    public List<DropData> getDropDataList() {
        return dropDataList;
    }


    public NTianMenDBData(int id, int copyId, List<DropData> dropDataList) {
        this.copyId = copyId;
        this.dropDataList = dropDataList;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public int compareTo(NTianMenDBData o) {
        if (id > o.getId()) {
            return 1;
        } else if (id < o.getId()) {
            return -1;
        }
        return 0;
    }

}
