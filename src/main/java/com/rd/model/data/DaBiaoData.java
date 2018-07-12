package com.rd.model.data;

import com.rd.bean.drop.DropData;

import java.util.List;

public class DaBiaoData {

    private byte id;

    private short dabiao;

    private List<DropData> firstReward;

    private String firstTitle;

    private String firstContent;

    private List<DropData> secondReward;

    private String secondTitle;

    private String secondContent;

    private List<DropData> thirdReward;

    private String thirdTitle;

    private String thirdContent;

    private List<DropData> forthReward;

    private String forthTitle;

    private String forthContent;

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public List<DropData> getFirstReward() {
        return firstReward;
    }

    public void setFirstReward(List<DropData> firstReward) {
        this.firstReward = firstReward;
    }

    public String getFirstTitle() {
        return firstTitle;
    }

    public void setFirstTitle(String firstTitle) {
        this.firstTitle = firstTitle;
    }

    public String getFirstContent() {
        return firstContent;
    }

    public void setFirstContent(String firstContent) {
        this.firstContent = firstContent;
    }

    public List<DropData> getSecondReward() {
        return secondReward;
    }

    public void setSecondReward(List<DropData> secondReward) {
        this.secondReward = secondReward;
    }

    public String getSecondTitle() {
        return secondTitle;
    }

    public void setSecondTitle(String secondTitle) {
        this.secondTitle = secondTitle;
    }

    public String getSecondContent() {
        return secondContent;
    }

    public void setSecondContent(String secondContent) {
        this.secondContent = secondContent;
    }

    public List<DropData> getThirdReward() {
        return thirdReward;
    }

    public void setThirdReward(List<DropData> thirdReward) {
        this.thirdReward = thirdReward;
    }

    public String getThirdTitle() {
        return thirdTitle;
    }

    public void setThirdTitle(String thirdTitle) {
        this.thirdTitle = thirdTitle;
    }

    public String getThirdContent() {
        return thirdContent;
    }

    public void setThirdContent(String thirdContent) {
        this.thirdContent = thirdContent;
    }

    public List<DropData> getForthReward() {
        return forthReward;
    }

    public void setForthReward(List<DropData> forthReward) {
        this.forthReward = forthReward;
    }

    public String getForthTitle() {
        return forthTitle;
    }

    public void setForthTitle(String forthTitle) {
        this.forthTitle = forthTitle;
    }

    public String getForthContent() {
        return forthContent;
    }

    public void setForthContent(String forthContent) {
        this.forthContent = forthContent;
    }

    public short getDabiao() {
        return dabiao;
    }

    public void setDabiao(short dabiao) {
        this.dabiao = dabiao;
    }
}
