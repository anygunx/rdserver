package com.rd.bean.fight.monstersiege.state;

import com.rd.bean.fight.fsm.IFSMState;
import com.rd.bean.fight.monstersiege.GameMonsterData;
import com.rd.game.GameRole;

import static com.rd.bean.fight.monstersiege.state.MonsterSiegeDefine.EMonsterSiegeState;

public abstract class GameMonsterState implements IFSMState {
    protected EMonsterSiegeState eType;
    protected GameMonsterData owner;

    public GameMonsterState(EMonsterSiegeState eType, GameMonsterData owner) {
        this.eType = eType;
        this.owner = owner;
    }

    @Override
    public byte getType() {
        return eType.getId();
    }

    public EMonsterSiegeState geteType() {
        return eType;
    }

    public GameMonsterData getOwner() {
        return owner;
    }

    /**
     * 进入状态
     */
    @Override
    public void enter() {
        owner.setState(getType());
        onEnter();
    }

    protected abstract void onEnter();

    @Override
    public void exit() {
        onExit();
    }

    protected abstract void onExit();

    /**
     * 检查该状态下是否可被指定玩家攻击
     *
     * @param attacker
     * @return
     */
    public abstract short checkAttacked(GameRole attacker);

    /**
     * 状态转换检测
     */
    public abstract boolean checkTransitions();

    /**
     * 检查是否获得奖励
     *
     * @return
     */
    public abstract boolean checkReward();
}
