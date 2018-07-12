package com.rd.bean.fight.monstersiege.state;

import com.rd.bean.fight.monstersiege.GameMonsterData;
import com.rd.define.ErrorDefine;
import com.rd.game.GameRole;
import com.rd.model.MonsterSiegeModel;
import com.rd.model.data.MonsterSiegeModelData;

/**
 * 怪物被攻击状态
 */
public class GameMonsterStateAttacked extends GameMonsterState {
    public GameMonsterStateAttacked(MonsterSiegeDefine.EMonsterSiegeState eType, GameMonsterData owner) {
        super(eType, owner);
    }

    @Override
    public void onEnter() {
        MonsterSiegeModelData modelData = MonsterSiegeModel.getMonsterSiege(owner.getId());
        owner.setDeadline(System.currentTimeMillis() + modelData.getEscapeTime());
    }

    @Override
    public void onExit() {
        // 清除状态
        owner.setDeadline(0);
    }

    @Override
    public short checkAttacked(GameRole attacker) {
        MonsterSiegeModelData modelData = MonsterSiegeModel.getMonsterSiege(owner.getId());
        if (owner.getAttackTimes() >= modelData.getAttackTimes()) {
            // 讲道理不在这。。
            return ErrorDefine.ERROR_TARGET_DEAD;
        }
        if (owner.containsAttacker(attacker.getPlayerId())) {
            return ErrorDefine.ERROR_ALREADY_ATTACK;
        }
        return ErrorDefine.ERROR_NONE;
    }

    @Override
    public boolean checkTransitions() {
        MonsterSiegeModelData modelData = MonsterSiegeModel.getMonsterSiege(owner.getId());
        // 次数够死亡
        if (owner.getAttackTimes() >= modelData.getAttackTimes()) {
            return true;
        }
        // 时间到逃跑
        if (owner.getDeadline() < System.currentTimeMillis()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkReward() {
        return false;
    }


}
