package com.rd.bean.comm.condition;

public interface IConditionHandlerBuilder<T extends IConditionHandler> {
    T build();
}
