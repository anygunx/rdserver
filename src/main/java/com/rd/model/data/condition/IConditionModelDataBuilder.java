package com.rd.model.data.condition;

import com.rd.define.ECondition;
import org.w3c.dom.Element;

/**
 * 条件模板生成器
 */
public interface IConditionModelDataBuilder<T extends IConditionModelData> {
    /**
     * 从xml指定节点中，以prefix为前缀的属性中生成条件数据
     **/
    T build(ECondition condition, Element e, String prefix);
}
