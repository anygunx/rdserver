package com.rd.define;

import com.rd.bean.comm.condition.*;
import com.rd.model.data.condition.IConditionModelData;
import com.rd.model.data.condition.IConditionModelDataBuilder;
import com.rd.model.data.condition.NoneConditionModelData;
import com.rd.model.data.condition.SingleValueConditionModelData;
import com.rd.util.XmlUtils;
import org.w3c.dom.Element;

import java.util.*;

/**
 * 条件枚举
 */
public enum ECondition {
    None(1, NoneConditionModelData::new, LevelConditionHandler::new),
    DailyPay(2, SingleValueConditionModelData::new, DailyPayConditionHandler::new),
    DailyCost(3, SingleValueConditionModelData::new, DailyCostConditionHandler::new);

    public final byte id;
    private final IConditionModelDataBuilder modelBuilder;
    private final IConditionHandlerBuilder handlerBuilder;

    ECondition(int id, IConditionModelDataBuilder modelBuilder, IConditionHandlerBuilder conditionBuilder) {
        this.id = (byte) id;
        this.modelBuilder = modelBuilder;
        this.handlerBuilder = conditionBuilder;
    }

    private static Map<Byte, ECondition> valueMap = new HashMap() {
        {
            for (ECondition condition : ECondition.values()) {
                put(condition.id, condition);
            }
        }
    };

    public static ECondition valueOf(byte type) {
        return valueMap.get(type);
    }

    public <T extends IConditionModelData> T buildModel(ECondition condition, Element e, String prefix) {
        return (T) modelBuilder.build(condition, e, prefix);
    }

    public <T extends IConditionHandler> T buildHandler() {
        return (T) handlerBuilder.build();
    }

    public static List<IConditionModelData> parseConditionModelList(Element e, String prefix) {
        String countStr = XmlUtils.getAttribute(e, prefix + "_count");
        if (countStr == null) {
            return Collections.emptyList();
        }
        byte count = Byte.parseByte(countStr);
        List<IConditionModelData> conditionList = new ArrayList<>(count);
        for (byte i = 1; i <= count; i++) {
            Byte conditionId = Byte.parseByte(XmlUtils.getAttribute(e, prefix + "_type"));
            ECondition condition = ECondition.valueOf(conditionId);
            IConditionModelData modelData = condition.buildModel(condition, e, prefix);
            conditionList.add(modelData);
        }
        return conditionList;
    }
}
