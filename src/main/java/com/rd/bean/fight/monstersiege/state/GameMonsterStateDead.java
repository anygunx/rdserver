package com.rd.bean.fight.monstersiege.state;

import com.rd.bean.fight.monstersiege.GameMonsterAttacker;
import com.rd.bean.fight.monstersiege.GameMonsterData;
import com.rd.define.ErrorDefine;
import com.rd.game.GameRole;
import com.rd.game.GameWorld;
import com.rd.model.MonsterSiegeModel;
import com.rd.model.data.MonsterSiegeModelData;

public class GameMonsterStateDead extends GameMonsterState {
    public GameMonsterStateDead(MonsterSiegeDefine.EMonsterSiegeState eType, GameMonsterData owner) {
        super(eType, owner);
    }

    @Override
    public void onEnter() {
        MonsterSiegeModelData modelData = MonsterSiegeModel.getMonsterSiege(owner.getId());
        owner.setDeadline(System.currentTimeMillis() + modelData.getDeadTime());
    }

    @Override
    public void onExit() {
        owner.setDeadline(0);
    }

    @Override
    public short checkAttacked(GameRole attacker) {
        return ErrorDefine.ERROR_TARGET_DEAD;
    }

    @Override
    public boolean checkTransitions() {
        // 时间到
        if (owner.getDeadline() < System.currentTimeMillis()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkReward() {
        for (GameMonsterAttacker attacker : owner.getAttackers().values()) {
            GameRole gameRole = GameWorld.getPtr().getOnlineRole(attacker.getId());
            if (gameRole != null && gameRole.getMonsterSiegeManager().isInBattle(owner.getId())) {
                // 没打完
                return false;
            }
        }
        return true;
    }
}
