package com.rd.activity.data;

import com.rd.model.data.ExchangeModelData;
import org.w3c.dom.Element;

/**
 * 限时坐骑
 * Created by XingYun on 2017/1/19.
 */
public class TLHorseLogicData extends BaseActivityLogicData {
    private ExchangeModelData modelData;

    public ExchangeModelData getModelData() {
        return modelData;
    }

    @Override
    public String getKey() {
        return String.valueOf(modelData.getId());
    }

    @Override
    public void loadData(Element root) {
        modelData = ExchangeModelData.create(root);
    }
}
