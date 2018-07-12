package com.rd.bean.player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ShareData {

    private List<Long> shareList = new ArrayList<>();

    private byte shareNum = 0;

    private Set<Byte> receiveSet = new HashSet<>();

    public List<Long> getShareList() {
        return shareList;
    }

    public void setShareList(List<Long> shareList) {
        this.shareList = shareList;
    }

    public byte getShareNum() {
        return shareNum;
    }

    public void setShareNum(byte shareNum) {
        this.shareNum = shareNum;
    }

    public Set<Byte> getReceiveSet() {
        return receiveSet;
    }

    public void setReceiveSet(Set<Byte> receiveSet) {
        this.receiveSet = receiveSet;
    }
}
