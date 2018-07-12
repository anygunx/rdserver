package com.rd.bean.player;
/***
 *
 * 成就系统 
 */

import java.util.HashSet;
import java.util.Set;

public class NTAData {

    private int type;

    private Set<Short> lingquId = new HashSet<>();

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Set<Short> getLingquId() {
        return lingquId;
    }

    public void setLingquId(Set<Short> lingquId) {
        this.lingquId = lingquId;
    }

    public NTAData() {

    }

}
