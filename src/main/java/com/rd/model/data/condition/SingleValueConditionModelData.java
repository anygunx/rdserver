package com.rd.model.data.condition;

import com.rd.define.ECondition;
import com.rd.util.XmlUtils;
import org.w3c.dom.Element;


/**
 * 单一参数的条件数据模板
 */
public class SingleValueConditionModelData extends BaseConditionModelData {
    private final int value;

    public SingleValueConditionModelData(ECondition condition, Element e, String prefix) {
        super(condition);
        this.value = Integer.parseInt(XmlUtils.getAttribute(e, prefix + "_p1"));
    }

    public int getValue() {
        return value;
    }
}
