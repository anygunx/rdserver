package com.rd.bean.fight.monstersiege.state;

import com.rd.bean.fight.monstersiege.GameMonsterData;

public class MonsterSiegeDefine {
    public enum EMonsterSiegeState {
        Born(GameMonsterStateBorn::new),
        Attacker(GameMonsterStateAttacked::new),
        Dead(GameMonsterStateDead::new);

        private final IGameMonsterStateBuilder builder;

        EMonsterSiegeState(IGameMonsterStateBuilder builder) {
            this.builder = builder;
        }

        public static int getSize() {
            return values().length;
        }

        public <S extends GameMonsterState> S build(GameMonsterData owner) {
            return (S) builder.build(this, owner);
        }

        public byte getId() {
            return (byte) ordinal();
        }

        public static EMonsterSiegeState getType(byte id) {
            return values()[id];
        }

    }

}
