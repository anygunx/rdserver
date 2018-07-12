package com.rd.bean.fight.monstersiege.state;

import com.rd.bean.fight.monstersiege.GameMonsterData;

public interface IGameMonsterStateBuilder<S extends GameMonsterState> {
    S build(MonsterSiegeDefine.EMonsterSiegeState state, GameMonsterData owner);
}
