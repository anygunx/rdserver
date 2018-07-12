package com.rd.model.data;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public class CardSuitModelData {
    private final short id;
    private final Set<Short> formula;

    public CardSuitModelData(short id, Set<Short> formula) {
        this.id = id;
        this.formula = ImmutableSet.copyOf(formula);
    }

    public short getId() {
        return id;
    }

    public Set<Short> getFormula() {
        return formula;
    }
}
