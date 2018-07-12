package com.rd.bean.player;

import com.rd.bean.rank.PlayerRank;

public class ArenaChallenge extends PlayerRank {

    private AppearCharacter appearCha;

    public void init(PlayerRank pr) {
        super.init(pr);
        this.setRank(pr.getRank());
        this.setValue(pr.getValue());
        this.setValue2(pr.getValue2());
        this.setMatch(pr.getMatch());
    }

    public AppearCharacter getAppearCha() {
        return appearCha;
    }

    public void setAppearCha(AppearCharacter appearCha) {
        this.appearCha = appearCha;
    }

}
