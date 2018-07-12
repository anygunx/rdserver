package com.rd.bean.gang.fight;

import java.util.ArrayList;
import java.util.List;

public class FightRound {

    private List<FightBattle> battleList;

    public List<FightBattle> getBattleList() {
        return battleList;
    }

    public void setBattleList(List<FightBattle> battleList) {
        this.battleList = battleList;
    }

    public FightRound() {
        this.battleList = new ArrayList<>();
    }

    public FightGang getSelfFightGang(int gangId) {
        for (FightBattle battle : battleList) {
            if (battle.getFightGangOne().getGang() != null && battle.getFightGangOne().getGang().getId() == gangId) {
                return battle.getFightGangOne();
            }
            if (battle.getFightGangTwo().getGang() != null && battle.getFightGangTwo().getGang().getId() == gangId) {
                return battle.getFightGangTwo();
            }
        }
        return null;
    }

    public FightGang getTargetFightGang(int gangId) {
        for (FightBattle battle : battleList) {
            if (battle.getFightGangOne().getGang() != null && battle.getFightGangOne().getGang().getId() == gangId) {
                return battle.getFightGangTwo();
            }
            if (battle.getFightGangTwo().getGang() != null && battle.getFightGangTwo().getGang().getId() == gangId) {
                return battle.getFightGangOne();
            }
        }
        return null;
    }
}
