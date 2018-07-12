package com.rd.model.data.condition;

import com.rd.define.ECondition;

public abstract class BaseConditionModelData implements IConditionModelData {
    protected final ECondition type;

    protected BaseConditionModelData(ECondition condition) {
        this.type = condition;
    }

    public ECondition getType() {
        return type;
    }
}
