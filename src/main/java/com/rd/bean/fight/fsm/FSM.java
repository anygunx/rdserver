package com.rd.bean.fight.fsm;

/**
 * 怪物状态机
 */
public abstract class FSM<S extends IFSMState> {
    protected S currentState;

    public FSM(S state) {
        this.currentState = state;
    }

    public S getCurrentState() {
        return currentState;
    }

}
