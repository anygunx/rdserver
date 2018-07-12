package com.rd.bean.gang.fight;

import com.rd.define.GangDefine;

public class FightBattle {

    private FightGang fightGangOne = null;

    private FightGang fightGangTwo = null;

    public FightGang getFightGangOne() {
        return fightGangOne;
    }

    public void setFightGangOne(FightGang fightGangOne) {
        this.fightGangOne = fightGangOne;
    }

    public FightGang getFightGangTwo() {
        return fightGangTwo;
    }

    public void setFightGangTwo(FightGang fightGangTwo) {
        this.fightGangTwo = fightGangTwo;
    }

    public FightGang getWinFightGang() {
        if (fightGangOne.getState() == GangDefine.GANG_FIGHT_GANG_STATE_WIN) {
            return fightGangOne;
        } else {
            return fightGangTwo;
        }
    }

    public FightGang getLoseFightGang() {
        if (fightGangOne.getState() == GangDefine.GANG_FIGHT_GANG_STATE_WIN) {
            return fightGangTwo;
        } else {
            return fightGangOne;
        }
    }

    public void updateGangState() {
        if (fightGangOne.getState() == GangDefine.GANG_FIGHT_GANG_STATE_OUT && fightGangTwo.getState() == GangDefine.GANG_FIGHT_GANG_STATE_FIGHT) {
            fightGangTwo.setState(GangDefine.GANG_FIGHT_GANG_STATE_WIN);
            return;
        }
        if (fightGangTwo.getState() == GangDefine.GANG_FIGHT_GANG_STATE_OUT && fightGangOne.getState() == GangDefine.GANG_FIGHT_GANG_STATE_FIGHT) {
            fightGangOne.setState(GangDefine.GANG_FIGHT_GANG_STATE_WIN);
            return;
        }
        if (fightGangOne.getState() != GangDefine.GANG_FIGHT_GANG_STATE_OUT) {
            fightGangOne.reset();
        }
        if (fightGangTwo.getState() != GangDefine.GANG_FIGHT_GANG_STATE_OUT) {
            fightGangTwo.reset();
        }
    }
}