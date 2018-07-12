package com.rd.bean.fight.monstersiege.state;

import com.rd.bean.fight.fsm.FSM;

import static com.rd.bean.fight.monstersiege.state.MonsterSiegeDefine.EMonsterSiegeState;

/**
 * 怪物状态机
 * 管理怪物状态数据及转换
 */
public class MonsterFSM extends FSM<GameMonsterState> {
    public MonsterFSM(GameMonsterState currentState) {
        super(currentState);
    }

    /**
     * 状态顺序转换
     * controller
     *
     * @return
     */
    public GameMonsterState switch2next() {
        // 状态集合是一个循环链表
        byte nextId = (byte) ((currentState.geteType().getId() + 1) % EMonsterSiegeState.getSize());
        EMonsterSiegeState nextState = EMonsterSiegeState.getType(nextId);
        return nextState.build(currentState.getOwner());
    }

    /**
     * 更新状态
     */
    public void updateState() {
        boolean transition = currentState.checkTransitions();
        if (transition) {
            currentState.exit();
            GameMonsterState nextState = switch2next();
            if (nextState != null) {
                currentState = nextState;
                currentState.enter();
            }
        }
    }
}
