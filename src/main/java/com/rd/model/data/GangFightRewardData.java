package com.rd.model.data;

import com.rd.bean.drop.DropData;

import java.util.List;

public class GangFightRewardData {

    private byte id;

    private String memberTitle;

    private String memberContent;

    private List<DropData> memberReward;

    private List<DropData> storeReward;

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public String getMemberTitle() {
        return memberTitle;
    }

    public void setMemberTitle(String memberTitle) {
        this.memberTitle = memberTitle;
    }

    public String getMemberContent() {
        return memberContent;
    }

    public void setMemberContent(String memberContent) {
        this.memberContent = memberContent;
    }

    public List<DropData> getMemberReward() {
        return memberReward;
    }

    public void setMemberReward(List<DropData> memberReward) {
        this.memberReward = memberReward;
    }

    public List<DropData> getStoreReward() {
        return storeReward;
    }

    public void setStoreReward(List<DropData> storeReward) {
        this.storeReward = storeReward;
    }
}
