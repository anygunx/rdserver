package com.rd.bean.fight.fsm;

public interface IFSMState {
    /**
     * 获取状态类型
     **/
    byte getType();
//    /** 状态初始化 **/
//    void init();

    /**
     * 进入状态
     **/
    void enter();

    /**
     * 退出状态
     **/
    void exit();
}
