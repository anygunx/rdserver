package com.rd.bean.fight.monstersiege.state;

import com.rd.bean.fight.monstersiege.GameMonsterData;
import com.rd.define.ErrorDefine;
import com.rd.game.GameRole;

/**
 * 怪物的出生状态
 */
public class GameMonsterStateBorn extends GameMonsterState {
    public GameMonsterStateBorn(MonsterSiegeDefine.EMonsterSiegeState eType, GameMonsterData owner) {
        super(eType, owner);
    }

    @Override
    public void onEnter() {
        // 重置所有状态信息
        owner.getAttackers().clear();
        owner.setDeadline(0);
        owner.setRewardFlag(false);
    }

    @Override
    public void onExit() {
    }

    @Override
    public short checkAttacked(GameRole attacker) {
        return ErrorDefine.ERROR_NONE;
    }

    @Override
    public boolean checkTransitions() {
        if (owner.getAttackTimes() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkReward() {
        return false;
    }
}
